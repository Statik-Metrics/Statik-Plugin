package io.statik.v1;

import com.google.common.base.Predicate;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Map;

public final class Statik {

    private final Plugin plugin;
    private final int projectId;

    private Predicate<AbstractMap.SimpleEntry<String, Map<String, Object>>> statHandler;

    /**
     * Creates a Statik instance for a plugin.
     *
     * @param plugin plugin to track
     * @param projectId BukkitDev project ID
     */
    public Statik(Plugin plugin, int projectId) {
        this.plugin = plugin;
        this.projectId = projectId;

        // Tracker registration
        boolean trackerNeeded = true;
        // Assume that other plugins could use a Predicate service, and actually check all registrations
        for (RegisteredServiceProvider<Predicate> provider : plugin.getServer().getServicesManager().getRegistrations(Predicate.class)) {
            Predicate predicate = provider.getProvider();
            if (!(predicate instanceof Thread)) { // Not a thread? Go away, you're not the stat handler!
                continue;
            }

            if (((Thread) predicate).isAlive()) {
                trackerNeeded = false;
                break; // We're too late! They've already started collection!
            }

            int version = this.getHandlerVersion(predicate);
            if (version >= StatHandler.STATIK_VERSION) { // Compare to OUR version
                trackerNeeded = false;
                break; // There already exists a newer or equal stat handler, don't bother registering.
            } else if (version != -1) { // If it's -1 it's not a stat handler, so don't remove it
                plugin.getServer().getServicesManager().unregister(predicate); // Remove older handler
            }

        }
        if (trackerNeeded) { // No newer or equal stat handler exists
            plugin.getServer().getServicesManager().register(Predicate.class, new StatHandler(), plugin, ServicePriority.Lowest);
        }
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() { // Wait until all plugins have run this
            @Override
            public void run() {
                for (RegisteredServiceProvider<Predicate> provider : Statik.this.plugin.getServer().getServicesManager().getRegistrations(Predicate.class)) {
                    Predicate predicate = provider.getProvider();
                    if (predicate instanceof Thread && Statik.this.getHandlerVersion(predicate) > 0) {
                        Statik.this.statHandler = predicate; // This is the one we want
                        Thread thread = (Thread) predicate;
                        if (!thread.isAlive()) {
                            thread.start(); // Start if nobody else has
                        }
                        return;
                    }
                }
                // TODO Output that everything is broken as somehow there aren't any stat handlers
            }
        });
    }

    private int getHandlerVersion(Predicate predicate) {
        try {
            Field field = predicate.getClass().getDeclaredField("STATIK_VERSION");
            return (Integer) field.get(null);
        } catch (Throwable thrown) {
            return -1;
        }
    }

}

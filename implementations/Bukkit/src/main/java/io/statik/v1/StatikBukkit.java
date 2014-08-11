package io.statik.v1;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Do not modify this class.
 */
public final class StatikBukkit {
    private static final String NOTICE = "DO NOT MODIFY THIS CLASS - MODIFICATIONS MAY RESULT STATIK.IO DELISTING";

    private class StatHandler extends StatikNetHandler {
        private static final int STATIK_VERSION = StatikNetHandler.STATIK_VERSION;

        private StatHandler(UUID uuid) {
            super(uuid);
        }

        // Dev note: Never change existing method, only add new methods
        public boolean add(Plugin plugin, Map<String, Object> data) {
            try { // In case something derpy happens
                // TODO track
            } catch (Throwable threwItOnTheGround) { // I'm not a part of your system!
                return false; // This is how we let you know that you screwed up.
            }
            return true;
        }
    }

    private class StatHandlerAccessor {
        private final Method add;
        private final Object object;
        private final int version;

        private StatHandlerAccessor(Object object) throws Throwable {
            this.add = object.getClass().getDeclaredMethod("add", Plugin.class, Map.class);
            this.version = (Integer) object.getClass().getDeclaredField("STATIK_VERSION").get(null);
            this.object = object;
        }

        private void add(Plugin plugin, Map<String, Object> data) throws InvocationTargetException, IllegalAccessException {
            this.add.invoke(this.object, plugin, data);
        }

        private int getVersion() {
            return this.version;
        }
    }

    private final Plugin plugin;
    private StatHandlerAccessor statHandler;

    public StatikBukkit(Plugin plugin) {
        this.plugin = plugin;

        // Tracker registration
        boolean trackerNeeded = true;

        for (RegisteredServiceProvider<Object> provider : plugin.getServer().getServicesManager().getRegistrations(Object.class)) {
            Object object = provider.getProvider();
            if (!(object instanceof Thread)) { // Not a thread? Go away, you're not the stat handler!
                continue;
            }

            StatHandlerAccessor accessor;
            try {
                accessor = new StatHandlerAccessor(object);
            } catch (Throwable thrown) {
                continue;
            }

            if (((Thread) object).isAlive()) {
                trackerNeeded = false;
                break; // We're too late! They've already started collection!
            }

            if (accessor.getVersion() >= StatHandler.STATIK_VERSION) { // Compare to OUR version
                trackerNeeded = false;
                break; // There already exists a newer or equal stat handler, don't bother registering.
            } else {
                plugin.getServer().getServicesManager().unregister(object); // Remove older handler
            }

        }
        if (trackerNeeded) { // No newer or equal stat handler exists
            plugin.getServer().getServicesManager().register(Object.class, new StatHandler(UUID.fromString(plugin.getDataFolder().getParentFile().getAbsolutePath())), plugin, ServicePriority.Lowest);
        }
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() { // Wait until all plugins have run this
            @Override
            public void run() {
                for (RegisteredServiceProvider<Object> provider : StatikBukkit.this.plugin.getServer().getServicesManager().getRegistrations(Object.class)) {
                    Object object = provider.getProvider();
                    if (!(object instanceof Thread)) { // Not a thread? Go away, you're not the stat handler!
                        continue;
                    }

                    StatHandlerAccessor accessor;
                    try {
                        accessor = new StatHandlerAccessor(object);
                    } catch (Throwable thrown) {
                        continue;
                    }
                    StatikBukkit.this.statHandler = accessor;
                    Thread thread = (Thread) object;
                    if (!thread.isAlive()) {
                        thread.start(); // Start if nobody else has
                    }
                    return;
                }
                // Inexplicably none exist, let's register our own
                StatikBukkit.this.plugin.getServer().getServicesManager().register(Object.class, new StatHandler(UUID.fromString(StatikBukkit.this.plugin.getDataFolder().getParentFile().getAbsolutePath())), StatikBukkit.this.plugin, ServicePriority.Lowest);
            }
        });
    }
}

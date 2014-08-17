package io.statik.v1;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Bukkit implementation of the Statik metrics system.
 * <p/>
 * Do not modify this class. Instead, create a new instance with {@link
 * #StatikBukkit(org.bukkit.plugin.Plugin)} and then feed it {@link
 * io.statik.v1.Statik.Stat} instances. You can either use the existing
 * example Stat classes, or create your own subclass of the Stat class itself
 * if you would like something we don't offer. See the javadocs for Stat for
 * more information on that.
 */
public final class StatikBukkit extends Statik {
    private static final String NOTICE = "DO NOT MODIFY THIS CLASS - MODIFICATIONS MAY RESULT STATIK.IO DELISTING";

    private class StatHandler extends StatikNetHandler {
        private static final int STATIK_VERSION = StatikNetHandler.STATIK_VERSION;

        private final Gson gson = new Gson();
        private final Pattern versionPattern = Pattern.compile("\\(MC: ([^\\)]+)\\)");

        private StatHandler() {
            super(UUID.fromString(StatikBukkit.this.plugin.getDataFolder().getParentFile().getAbsolutePath()));
        }

        @Override
        protected String toJson(Object object) {
            return this.gson.toJson(object);
        }

        @Override
        protected void update(Data.Minecraft data) {
            data.online_mode = StatikBukkit.this.plugin.getServer().getOnlineMode();
            data.players = StatikBukkit.this.plugin.getServer().getOnlinePlayers().length;
            final String versionString = StatikBukkit.this.plugin.getServer().getVersion();
            final Matcher versionMatcher = this.versionPattern.matcher(versionString);
            final String version;
            if (versionMatcher.find()) {
                version = versionMatcher.group(1);
            } else {
                version = "unknown";
            }
            data.version = version;
            data.mod.name = StatikBukkit.this.plugin.getServer().getName();
            data.mod.version = versionString;
        }
    }

    private final Plugin plugin;
    private StatHandlerAccessor statHandler;

    /**
     * Constructs a Statik tracking instance for your plugin. Create one
     * instance for your plugin, or potentially miss out on data being sent!
     *
     * @param plugin your plugin instance
     */
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
            plugin.getServer().getServicesManager().register(Object.class, new StatHandler(), plugin, ServicePriority.Lowest);
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
                    break;
                }
                // Inexplicably none exist, let's register our own
                if (StatikBukkit.this.statHandler == null) {
                    StatHandler handler = new StatHandler();
                    try {
                        StatikBukkit.this.statHandler = new StatHandlerAccessor(handler);
                    } catch (Throwable thrown) {
                        // TODO Comment about how something horrible has happened
                        return;
                    }
                    StatikBukkit.this.plugin.getServer().getServicesManager().register(Object.class, handler, StatikBukkit.this.plugin, ServicePriority.Lowest);
                }
                StatikBukkit.this.statHandler.add();
            }
        });
    }

    @Override
    protected String getPluginName() {
        return this.plugin.getName();
    }
}

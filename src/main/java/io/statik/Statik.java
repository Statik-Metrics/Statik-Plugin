package io.statik;

import org.bukkit.plugin.Plugin;

import java.util.Map;

/**
 * TODO WIP
 */
public abstract class Statik {

    /**
     * The current Statik instance
     */
    private static Statik instance;

    /**
     * Initializes Statik.
     */
    public static void initialize(Plugin pluginInstance) {
        // First, see if we nope
        if ("io.statik.Statik".equals(Statik.class.getName())) {
            throw new IllegalStateException("Nope.");
        }

        // Find latest version available
        int i = 1;
        Class<?> statikClass = null;
        while (true) {
            try {
                statikClass = Class.forName("io.statik.Statik_v" + i);
            } catch (ClassNotFoundException e) {
                if (statikClass == null) {
                    throw new RuntimeException("No Statik implementation found, make sure you shaded Statik correctly.");
                }
                break;
            }
            i++;
        }

        // Replace current instance with latest one available if needed
        if (Statik.instance == null || Statik.instance.getVersion() < i) {
            try {
                Statik.instance = (Statik) statikClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to instanciate Statik class '" + statikClass.getName() + "'");
            }
        }

        Statik.instance.registerPlugin(pluginInstance);
    }

    /**
     * <bendem> Ribesg, what If I don't want to clutter my main class and want to register another Statik.Custom?
     */
    public static void registerCustomTracker(Plugin plugin, Statik.Custom customTracker) {
        Statik.instance._registerCustomTracker(plugin, customTracker);
    }

    /**
     * Registers the provided Plugin for data collection.
     *
     * @param pluginInstance a plugin instance
     */
    protected abstract void registerPlugin(Plugin pluginInstance);

    /**
     * @see Statik#registerCustomTracker(Plugin, Statik.Custom)
     */
    protected abstract void _registerCustomTracker(Plugin plugin, Custom customTracker);

    /**
     * Gets the implementation's version.
     *
     * @return the implementation's version
     */
    protected abstract int getVersion();

    /**
     * Bukkit Plugins should implement this interface if they want to track
     * custom data with the Statik system.
     * <p/>
     * TODO Find a better name?
     */
    public interface Custom {

        /**
         * TODO Correct Javadoc
         * Should return a Map containing only the following values:
         * - String
         * - Double
         * - Float
         * - Long
         * - Integer
         * - Short
         *
         * @return a Map of custom data
         */
        public Map<String, Object> getCustomData();
    }
}

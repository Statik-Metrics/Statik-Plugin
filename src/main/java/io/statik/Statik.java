package io.statik;

import org.bukkit.plugin.Plugin;

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
     * Registers the provided Plugin for data collection.
     *
     * @param pluginInstance a plugin instance
     */
    protected abstract void registerPlugin(Plugin pluginInstance);

    /**
     * Gets the implementation's version.
     *
     * @return the implementation's version
     */
    protected abstract int getVersion();
}

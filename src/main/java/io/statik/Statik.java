package io.statik;

import org.bukkit.plugin.Plugin;

/**
 * TODO WIP
 */
public final class Statik {

    /**
     * The current Statik instance
     */
    private static StatikClient client;

    /**
     * Initialize Statik.
     * TODO: do we need a plugin instance?
     */
    public static void init(Plugin pluginInstance) {
        // Find latest version available
        int i = 0;
        Class<?> statikClass = null;
        while (true) {
            try {
                statikClass = Class.forName("Statik_v" + (i + 1));
                i++;
            } catch (ClassNotFoundException e) {
                if (statikClass == null) {
                    throw new RuntimeException("Wtf?");
                }
                break;
            }
        }

        // Replace current instance with latest one available if needed
        if (Statik.client == null || Statik.client.getVersion() < i) {
            try {
                Statik.client = (StatikClient) statikClass.getConstructor(Plugin.class).newInstance(pluginInstance);
            } catch (Exception e) {
                throw new RuntimeException("Wtf?"); // TODO
            }
            // TODO Map old instance to the new one
        }
    }

    /* TODO
     * Add graphs and data tracking methods and stuff here
     *   | 1) Static methods?
     *   | 2) Static get() and non-static methods?
     *
     * Those methods should build a JSON 'jsonData' item then use
     * Statik.client.queue(jsonData);
     */

    /**
     * Main interface. Every version of Statik will implement this interface.
     * <p>
     * <strong>This interface should never change!</strong>
     */
    static interface StatikClient {

        /**
         * Sends a JSON object to the Statik report server.
         *
         * @param jsonData the data to send to the Statik report server
         */
        public void queue(String jsonData);

        /**
         * Gets the implementation's version.
         *
         * @return the implementation's version
         */
        public int getVersion();
    }
}

package io.statik;

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
     * <p/>
     * This method should be updated with each Statik update.
     */
    public static void init() {
        if (Statik.client == null) {
            Statik.client = new Statik_v1();
        } else if (Statik.client.getVersion() < Statik_v1.STATIK_VERSION) {
            Statik.client = new Statik_v1();
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
     * <p/>
     * <strong>This interface should never change!</strong>
     */
    private static interface StatikClient {

        /**
         * Sends a JSON object to the implementation default enpoint.
         *
         * @param jsonData the data to send to the default enpoint
         */
        public void queue(String jsonData);

        /**
         * Sends a JSON object to the provided endpoint.
         *
         * @param destination the destination endpoint
         * @param jsonData    the data to send to the destination enpoint
         */
        public void queue(String destination, String jsonData);

        /**
         * Gets the implementation's version.
         *
         * @return the implementation's version
         */
        public int getVersion();
    }

    /**
     * First StatikClient implementation.
     */
    private static final class Statik_v1 implements StatikClient {

        /**
         * This Statik implementation version
         */
        public static final int STATIK_VERSION = 1;

        /**
         * This Statik implementation endpoint
         * <p/>
         * Changing this is supported, but it would be nice not to change it.
         * <strong>Changing this requires a version change!</strong>
         */
        private static final String STATIK_ENDPOINT = "http://report.statik.io/";

        /**
         * @see StatikClient#queue(String)
         */
        @Override
        public void queue(String jsonData) {
            this.queue(Statik_v1.STATIK_ENDPOINT, jsonData);
        }

        /**
         * @see StatikClient#queue(String, String)
         */
        @Override
        public void queue(String destination, String jsonData) {
            // TODO Implement method
        }

        /**
         * This method should be updated with each Statik update.
         *
         * @see StatikClient#getVersion()
         */
        @Override
        public int getVersion() {
            return 1;
        }
    }
}

package io.statik;

import org.bukkit.plugin.Plugin;

/**
 * First StatikClient implementation.
 */
final class Statik_v1 implements Statik.StatikClient {

    /**
     * This Statik implementation version
     */
    public static final int STATIK_VERSION = 1;

    /**
     * This Statik implementation endpoint
     * <p>
     * Changing this is supported, but it would be nice not to change it.
     * <strong>Changing this requires a version change!</strong>
     */
    private static final String STATIK_ENDPOINT = "http://report.statik.io/";

    /**
     * Package protected constructor.
     */
    Statik_v1(Plugin pluginInstance) {
        // TODO
    }

    /**
     * @see Statik.StatikClient#queue(String)
     */
    @Override
    public void queue(String jsonData) {
        // TODO
    }

    /**
     * This method should be updated with each Statik update.
     *
     * @see Statik.StatikClient#getVersion()
     */
    @Override
    public int getVersion() {
        return Statik_v1.STATIK_VERSION;
    }
}

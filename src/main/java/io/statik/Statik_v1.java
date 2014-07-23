package io.statik;

import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

/**
 * First StatikClient implementation.
 */
final class Statik_v1 extends Statik {

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
     * Set of all plugins registered for data collection.
     */
    private final Set<Plugin> plugins;

    /**
     * Package protected constructor.
     */
    Statik_v1() {
        this.plugins = new HashSet<Plugin>();
    }

    @Override
    protected void registerPlugin(Plugin pluginInstance) {
        // TODO Implement method
    }

    /**
     * This method can be updated safely with a Statik update.
     *
     * @see Statik#queueJson(String)
     */
    @Override
    protected void queueJson(String jsonData) {
        // TODO Implement method
    }

    /**
     * This method can be updated safely with a Statik update.
     *
     * @see Statik#getReportServerUrl()
     */
    @Override
    protected String getReportServerUrl() {
        return STATIK_ENDPOINT;
    }

    /**
     * This method has to be updated with each Statik update.
     *
     * @see Statik#getVersion()
     */
    @Override
    public int getVersion() {
        return STATIK_VERSION;
    }
}

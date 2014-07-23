package io.statik;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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
     * <p>
     * <strong>Changing this requires a version change!</strong>
     */
    private static final String STATIK_ENDPOINT = "http://report.statik.io/";

    /**
     * Set of all plugins registered for data collection.
     */
    private final Set<Plugin> plugins;

    /**
     * <bendem> Ribesg, what If I don't want to clutter my main class and want to register another Statik.Custom?
     */
    private final Map<Plugin, Set<StatikTracker>> customTrackers;

    /**
     * Package protected constructor.
     *
     * @param oldInstance an instance created by another plugin
     */
    @SuppressWarnings("unchecked")
    Statik_v1(Statik oldInstance) {
        if (oldInstance != null) {
            Object plugins = oldInstance.getReplacementMaterial().get("plugins");
            Object customTrackers = oldInstance.getReplacementMaterial().get("customTrackers");
            this.plugins = (HashSet<Plugin>) plugins;
            this.customTrackers = (HashMap<Plugin, Set<StatikTracker>>) customTrackers;
        } else {
            this.plugins = new HashSet<Plugin>();
            this.customTrackers = new HashMap<Plugin, Set<StatikTracker>>();
        }
    }

    /**
     * @see Statik#registerPlugin(Plugin)
     */
    @Override
    protected void registerPlugin(Plugin plugin) {
        if (this.plugins.contains(plugin)) {
            throw new IllegalArgumentException("Trying to register '" + plugin.getName() + "' twice");
        } else {
            this.plugins.add(plugin);
            if (plugin instanceof StatikTracker) {
                Set<StatikTracker> customTrackersSet = new HashSet<StatikTracker>();
                customTrackersSet.add((StatikTracker) plugin);
                this.customTrackers.put(plugin, customTrackersSet);
            }
        }
    }

    /**
     * @see Statik#registerCustomTracker(Plugin, StatikTracker)
     */
    @Override
    public void _registerCustomTracker(Plugin plugin, StatikTracker customTracker) {
        if (this.plugins.contains(plugin)) {
            Set<StatikTracker> pluginTrackersSet = this.customTrackers.get(plugin);
            if (pluginTrackersSet == null) {
                pluginTrackersSet = new HashSet<StatikTracker>();
            }
            pluginTrackersSet.add(customTracker);
            this.customTrackers.put(plugin, pluginTrackersSet);
        } else {
            throw new IllegalArgumentException("Plugin '" + plugin.getName() + "' should initialize Statik before registering a Custom Tracker");
        }
    }

    /**
     * @see Statik#getReplacementMaterial()
     */
    @Override
    protected Map<String, Object> getReplacementMaterial() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("plugins", this.plugins);
        result.put("customTrackers", this.customTrackers);
        return result;
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

    /**
     * Collects data from plugins and send it to the report server.
     */
    private void collectAndSend() {
        Map<String, Object> serverDataMap = new HashMap<String, Object>();
        // TODO Add server-related stuff: Java vserion...

        Map<String, Object> pluginsDataMap = new HashMap<String, Object>();
        for (Plugin plugin : this.plugins) {
            Map<String, Object> pluginDataMap = new HashMap<String, Object>();
            // TODO Add standard stuff: version...
            Set<StatikTracker> additionalPluginTrackers = this.customTrackers.get(plugin);
            if (additionalPluginTrackers != null) {
                Map<String, Object> pluginCustomDataMap = new HashMap<String, Object>();
                for (StatikTracker customTracker : additionalPluginTrackers) {
                    for (Entry<String, Object> e : customTracker.getStatikData().entrySet()) {
                        String key = e.getKey();
                        Object value = e.getValue();
                        if (isCustomValueValid(value)) {
                            if (pluginCustomDataMap.containsKey(key)) {
                                throw new IllegalArgumentException("Custom data key '" + key + "' used twice");
                            } else {
                                pluginCustomDataMap.put(key, value);
                            }
                        } else {
                            throw new IllegalArgumentException("Invalid value type: " + value.getClass().getName());
                        }
                    }
                }
                pluginDataMap.put("custom", pluginCustomDataMap);
            }
            pluginsDataMap.put(plugin.getName(), pluginDataMap);
        }
        serverDataMap.put("plugins", pluginsDataMap);

        this.queueJson(this.toJson(serverDataMap));
    }

    /**
     * Checks if a value has a Statik-accepted class.
     *
     * @param value the value to check
     * @return true if the value can be sent to the report server, false
     * otherwise
     */
    private boolean isCustomValueValid(Object value) {
        return Byte.class.isInstance(value)
                || Double.class.isInstance(value)
                || Float.class.isInstance(value)
                || Integer.class.isInstance(value)
                || Long.class.isInstance(value)
                || Short.class.isInstance(value)
                || String.class.isInstance(value);
    }

    /**
     * Converts a Map into a JSON object.
     *
     * @param dataMap a Map
     * @return a JSON object
     */
    private String toJson(Map<String, Object> dataMap) {
        return null; // TODO
    }

    /**
     * Sends a JSON object to the Statik report server.
     *
     * @param jsonData the data to send to the Statik report server
     */
    private void queueJson(String jsonData) {
        // TODO Implement method
    }
}

package io.statik.v1;

import io.statik.Statik;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.Map.Entry;

/**
 * First StatikClient implementation.
 */
final class StatikImpl extends Statik {

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
     * Tracking general server-wide stuff
     */
    private final ServerTracker serverTracker;

    /**
     * Set of all plugins registered for data collection.
     */
    private final Map<Plugin, PluginTracker> plugins;

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
    StatikImpl(Statik oldInstance, Plugin plugin) {
        if (oldInstance != null) {
            // FIXME Broken because of move to v1 package
            Object plugins = oldInstance.getReplacementMaterial().get("plugins");
            Object customTrackers = oldInstance.getReplacementMaterial().get("customTrackers");
            this.plugins = (HashMap<Plugin, PluginTracker>) plugins;
            this.customTrackers = (HashMap<Plugin, Set<StatikTracker>>) customTrackers;
        } else {
            this.plugins = new HashMap<Plugin, PluginTracker>();
            this.customTrackers = new HashMap<Plugin, Set<StatikTracker>>();
        }
        this.registerPlugin(plugin);
        this.serverTracker = new ServerTracker(this);
    }

    /**
     * @see Statik#registerPlugin(Plugin)
     */
    @Override
    protected void registerPlugin(Plugin plugin) {
        if (this.plugins.containsKey(plugin)) {
            throw new IllegalArgumentException("Trying to register '" + plugin.getName() + "' twice");
        } else {
            this.plugins.put(plugin, new PluginTracker(plugin));
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
        if (this.plugins.containsKey(plugin)) {
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
     * Gets all plugins registered with Statik.
     *
     * @return all plugins registered with Statik
     */
    public Set<Plugin> getPlugins() {
        return plugins.keySet();
    }

    /**
     * Collects data from plugins and server send it to the report server.
     */
    StatikDataMap collect() {
        StatikDataMap serverDataMap = this.serverTracker.getStatikData();

        List<StatikDataMap> pluginsDataList = new ArrayList<StatikDataMap>(this.plugins.size());
        for (Plugin plugin : this.plugins.keySet()) {
            // Add Statik collected data
            StatikDataMap pluginDataMap = this.plugins.get(plugin).getStatikData();

            // Add plugin's custom data
            StatikDataMap pluginData = this.collectCustomPluginData(plugin);
            if (pluginData != null && !pluginData.isEmpty()) {
                pluginDataMap.addStatikThing("_custom", pluginData);
            }

            pluginsDataList.add(pluginDataMap);
        }
        serverDataMap.addStatikThing("plugins", pluginsDataList);

        return serverDataMap;
    }

    /**
     * Collects custom data from a specified plugin
     */
    private StatikDataMap collectCustomPluginData(Plugin plugin) {
        Set<StatikTracker> pluginTrackers = this.customTrackers.get(plugin);
        if (pluginTrackers == null) {
            return null;
        }

        StatikDataMap pluginData = new StatikDataMap();
        for (StatikTracker tracker : pluginTrackers) {
            for (Entry<String, Object> e : tracker.getStatikData().getMap().entrySet()) {
                String key = e.getKey();
                Object value = e.getValue();

                if (pluginData.containsKey(key)) {
                    plugin.getLogger().severe("[Statik] Custom data key '" + key + "' used twice");
                } else {
                    pluginData.put(key, value);
                }
            }
        }

        // TODO Filter custom data

        return pluginData;
    }
}

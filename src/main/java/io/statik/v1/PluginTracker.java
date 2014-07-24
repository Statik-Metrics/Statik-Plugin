package io.statik.v1;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

final class PluginTracker implements StatikTracker {

    private final Plugin plugin;
    private final Map<String, Object> lastMap;

    public PluginTracker(Plugin plugin) {
        this.plugin = plugin;
        this.lastMap = new HashMap<String, Object>();
    }

    @Override
    public StatikDataMap getStatikData() {
        StatikDataMap result = new StatikDataMap();

        // TODO Add plugin values different than in lastMap to result

        // Filter the result to prevent sending already known information
        StatikDataMap finalResult = result.getFilteredMap(this.lastMap);
        this.lastMap.putAll(result.getMap());
        return finalResult;
    }
}

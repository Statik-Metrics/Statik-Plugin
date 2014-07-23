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
    public Map<String, Object> getStatikData() {
        Map<String, Object> result = new HashMap<String, Object>();

        // TODO Add plugin values different than in lastMap to result

        this.lastMap.putAll(result);
        return result;
    }
}

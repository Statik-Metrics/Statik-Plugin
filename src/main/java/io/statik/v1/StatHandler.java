package io.statik.v1;

import com.google.common.base.Predicate;

import java.util.AbstractMap;
import java.util.Map;

final class StatHandler extends Thread implements Predicate<AbstractMap.SimpleEntry<String, Map<String, Object>>> {

    static final int STATIK_VERSION = 1; // version of this stat handler. Don't touch, just let it do its job :)

    private Map<String, Object> serverData; // Replace with a map that tracks changed values
    private Map<String, Map<String, Object>> pluginData; // Replace with a map that tracks changed values

    @Override
    public boolean apply(AbstractMap.SimpleEntry<String, Map<String, Object>> entry) {
        try { // In case something derpy gets sent here, yay type erasure!
            String pluginName = entry.getKey(); // Just documenting that this is the plugin name
            Map<String, Object> pluginData = entry.getValue(); // Just documenting that this is the plugin data
            this.pluginData.put(pluginName, pluginData); // Replace with updating the change-tracking map
        } catch (Throwable threwItOnTheGround) { // I'm not a part of your system!
            return false; // This is how we let you know that you screwed up.
        }
        return true;
    }

    @Override
    public void run() {
        // TODO send data every half hour
    }

}

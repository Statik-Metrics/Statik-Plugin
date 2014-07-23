package io.statik.v1;

import java.util.HashMap;
import java.util.Map;

final class ServerTracker implements StatikTracker {

    private final Map<String, Object> lastMap;

    public ServerTracker() {
        this.lastMap = new HashMap<String, Object>();
    }

    @Override
    public Map<String, Object> getStatikData() {
        Map<String, Object> result = new HashMap<String, Object>();

        // TODO Add server-wide values different than in lastMap to result

        this.lastMap.putAll(result);
        return result;
    }
}

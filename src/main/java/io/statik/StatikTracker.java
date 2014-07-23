package io.statik;


import java.util.Map;

/**
 * Bukkit Plugins should implement this interface if they want to track
 * custom data with the Statik system.
 */
public interface StatikTracker {

    /**
     * TODO Correct Javadoc
     * Should return a Map containing only the following values:
     * - String
     * - Double
     * - Float
     * - Long
     * - Integer
     * - Short
     *
     * @return a Map of custom data
     */
    public Map<String, Object> getStatikData();
}
package io.statik.v1;

import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * Map wrapper that gives control on value types
 */
public class StatikDataMap {

    /**
     * Regular expression of allowed keys
     */
    private static final Pattern KEY_PATTERN = Pattern.compile("^[a-z0-9]+$");

    /**
     * The actual map we wrap
     */
    private final Map<String, Object> map;

    /**
     * This map is used to put "plugins" array into the Server's
     * StatikDataMap and to put "_custom" map in Plugin's StatikDataMap
     */
    private final Map<String, Object> statikThings;

    /**
     * Builds a StatikDataMap.
     */
    public StatikDataMap() {
        this.map = new HashMap<String, Object>();
        this.statikThings = new HashMap<String, Object>();
    }

    /**
     * Creates a map from another map.
     *
     * @param otherMap    the other map
     * @param copyEntries if we need to use the other map or copy it's entries
     */
    private StatikDataMap(StatikDataMap otherMap, boolean copyEntries) {
        this(otherMap.map, copyEntries);
    }

    /**
     * Creates a map from another map.
     *
     * @param otherMap    the other map
     * @param copyEntries if we need to use the other map or copy it's entries
     */
    private StatikDataMap(Map<String, Object> otherMap, boolean copyEntries) {
        if (copyEntries) {
            this.map = new HashMap<String, Object>(otherMap);
        } else {
            this.map = otherMap;
        }
        this.statikThings = new HashMap<String, Object>();
    }

    /**
     * Clears this map.
     */
    public void clear() {
        this.map.clear();
    }

    /**
     * Checks if this map is empty.
     *
     * @return true if this map is empty, false otherwise
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * Gets the size of this map.
     *
     * @return the size of this map
     */
    public int size() {
        return this.map.size();
    }

    /**
     * Checks if this map contains the provided key.
     *
     * @param key the key to check
     * @return true if the map contains the provided key, false otherwise
     */
    public boolean containsKey(String key) {
        return this.map.containsKey(key);
    }

    /**
     * Checks if this map contains the provided value.
     *
     * @param value the value to check
     * @return true if this map contaisn the provided value, false otherwise
     */
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    /**
     * Gets a value of the provided type with the provided key, if any.
     *
     * @param key       the key
     * @param valueType the value type
     * @param <T>       the value type
     * @return the value if found, null otherwise
     */
    private <T> T get(String key, Class<T> valueType) {
        Object result = this.map.get(key);
        return result != null && valueType.isInstance(result) ? valueType.cast(result) : null;
    }

    /**
     * Gets a value in the map.
     *
     * @param key the key
     * @return the value if any, null otherwise
     */
    Object get(String key) {
        return this.map.get(key);
    }

    /**
     * Gets a Boolean from this map with the provided key.
     *
     * @param key the key
     * @return the value if any, null otherwise
     */
    public Boolean getBoolean(String key) {
        return this.get(key, Boolean.class);
    }

    /**
     * Gets a Byte from this map with the provided key.
     *
     * @param key the key
     * @return the value if any, null otherwise
     */
    public Byte getByte(String key) {
        return this.get(key, Byte.class);
    }

    /**
     * Gets a Double from this map with the provided key.
     *
     * @param key the key
     * @return the value if any, null otherwise
     */
    public Double getDouble(String key) {
        return this.get(key, Double.class);
    }

    /**
     * Gets a Float from this map with the provided key.
     *
     * @param key the key
     * @return the value if any, null otherwise
     */
    public Float getFloat(String key) {
        return this.get(key, Float.class);
    }

    /**
     * Gets an Integer from this map with the provided key.
     *
     * @param key the key
     * @return the value if any, null otherwise
     */
    public Integer getInt(String key) {
        return this.get(key, Integer.class);
    }

    /**
     * Gets a Long from this map with the provided key.
     *
     * @param key the key
     * @return the value if any, null otherwise
     */
    public Long getLong(String key) {
        return this.get(key, Long.class);
    }

    /**
     * Gets a Short from this map with the provided key.
     *
     * @param key the key
     * @return the value if any, null otherwise
     */
    public Short getShort(String key) {
        return this.get(key, Short.class);
    }

    /**
     * Gets a String from this map with the provided key.
     *
     * @param key the key
     * @return the value if any, null otherwise
     */
    public String getString(String key) {
        return this.get(key, String.class);
    }

    /**
     * Puts the provided key:value pair in this map.
     * <p>
     * Note that you can't replace a value of a different type.
     *
     * @param key       the key
     * @param value     the value
     * @param valueType the value type
     * @param <T>       the value type
     * @return the old value if any, null otherwise
     * @throws IllegalArgumentException if trying to replace a value of a different type or key is invalid or key is invalid
     */
    private <T> T put(String key, T value, Class<T> valueType) {
        Validate.notNull(key, "key can't be null");
        Validate.notNull(valueType, "valueType can't be null");
        if (KEY_PATTERN.matcher(key).matches()) {
            Object previous = this.map.get(key);
            if (previous == null || valueType.isInstance(previous)) {
                this.map.put(key, value);
                return valueType.cast(previous);
            } else {
                throw new IllegalArgumentException("This StatikDataMap already contains a value of a different type");
            }
        } else {
            throw new IllegalArgumentException("key should match regex " + KEY_PATTERN.pattern());
        }
    }

    /**
     * Puts a key:value pair in the map.
     *
     * @param key   the key
     * @param value the value
     * @return the old value if any, null otherwise
     */
    Object put(String key, Object value) {
        return this.map.put(key, value);
    }

    /**
     * Puts the provided String:boolean pair in this map.
     * <p>
     * Note that you can't replace a value of a different type.
     *
     * @param key   the key
     * @param value the value
     * @return the old value if any, null otherwise
     * @throws IllegalArgumentException if trying to replace a value of a different type or key is invalid
     */
    public Boolean putBoolean(String key, boolean value) {
        return this.put(key, value, Boolean.class);
    }

    /**
     * Puts the provided String:byte pair in this map.
     * <p>
     * Note that you can't replace a value of a different type.
     *
     * @param key   the key
     * @param value the value
     * @return the old value if any, null otherwise
     * @throws IllegalArgumentException if trying to replace a value of a different type or key is invalid
     */
    public Byte putByte(String key, byte value) {
        return this.put(key, value, Byte.class);
    }

    /**
     * Puts the provided String:double pair in this map.
     * <p>
     * Note that you can't replace a value of a different type.
     *
     * @param key   the key
     * @param value the value
     * @return the old value if any, null otherwise
     * @throws IllegalArgumentException if trying to replace a value of a different type or key is invalid
     */
    public Double putDouble(String key, double value) {
        return this.put(key, value, Double.class);
    }

    /**
     * Puts the provided String:float pair in this map.
     * <p>
     * Note that you can't replace a value of a different type.
     *
     * @param key   the key
     * @param value the value
     * @return the old value if any, null otherwise
     * @throws IllegalArgumentException if trying to replace a value of a different type or key is invalid
     */
    public Float putFloat(String key, float value) {
        return this.put(key, value, Float.class);
    }

    /**
     * Puts the provided String:int pair in this map.
     * <p>
     * Note that you can't replace a value of a different type.
     *
     * @param key   the key
     * @param value the value
     * @return the old value if any, null otherwise
     * @throws IllegalArgumentException if trying to replace a value of a different type or key is invalid
     */
    public Integer putInt(String key, int value) {
        return this.put(key, value, Integer.class);
    }

    /**
     * Puts the provided String:long pair in this map.
     * <p>
     * Note that you can't replace a value of a different type.
     *
     * @param key   the key
     * @param value the value
     * @return the old value if any, null otherwise
     * @throws IllegalArgumentException if trying to replace a value of a different type or key is invalid
     */
    public Long putLong(String key, long value) {
        return this.put(key, value, Long.class);
    }

    /**
     * Puts the provided String:short pair in this map.
     * <p>
     * Note that you can't replace a value of a different type.
     *
     * @param key   the key
     * @param value the value
     * @return the old value if any, null otherwise
     * @throws IllegalArgumentException if trying to replace a value of a different type or key is invalid
     */
    public Short putShort(String key, short value) {
        return this.put(key, value, Short.class);
    }

    /**
     * Puts the provided String:String pair in this map.
     * <p>
     * Note that you can't replace a value of a different type.
     *
     * @param key   the key
     * @param value the value
     * @return the old value if any, null otherwise
     * @throws IllegalArgumentException if trying to replace a value of a different type or key is invalid
     */
    public String putString(String key, String value) {
        Validate.notNull(value, "Value can't be null");
        return this.put(key, value, String.class);
    }

    /**
     * Adds a statik thing to this map.
     *
     * @param key   the key
     * @param thing the thing
     */
    void addStatikThing(String key, Object thing) {
        this.statikThings.put(key, thing);
    }

    /**
     * Gets statik things of this map.
     *
     * @return statik things of this map
     */
    Map<String, Object> getStatikThings() {
        return this.statikThings;
    }

    /**
     * Gets the real map backing this map, ready to be JSONified.
     *
     * @return the real map backing this map
     */
    Map<String, Object> getMap() {
        return this.map;
    }

    /**
     * Gets the minimal map to send to the report server, ready to be
     * JSONified.
     *
     * @param lastMap a map containing all pairs "known" by the report server
     * @return a filtered version of this map
     */
    StatikDataMap getFilteredMap(Map<String, Object> lastMap) {
        if (lastMap == null || lastMap.isEmpty()) {
            return new StatikDataMap(this, true);
        } else {
            Map<String, Object> result = new HashMap<String, Object>();
            for (Entry<String, Object> entry : this.map.entrySet()) {
                Object lastValue = lastMap.get(entry.getKey());
                if (lastValue == null || !lastValue.equals(entry.getValue())) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
            return new StatikDataMap(result, false);
        }
    }
}

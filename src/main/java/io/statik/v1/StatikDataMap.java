package io.statik.v1;

import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.Map;

/**
 * Map wrapper that gives control on value types
 */
public class StatikDataMap {

    /**
     * The actual map we wrap
     */
    private final Map<String, Object> map;

    /**
     * Builds a StatikDataMap.
     */
    public StatikDataMap() {
        this.map = new HashMap<String, Object>();
    }

    /**
     * Clears this map.
     */
    public void clear() {
        this.map.clear();
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
     * @throws IllegalArgumentException if trying to replace a value of a different type
     */
    private <T> T put(String key, T value, Class<T> valueType) {
        Validate.notNull(key, "key can't be null");
        Validate.notNull(valueType, "valueType can't be null");
        Object previous = this.map.get(key);
        if (previous == null || valueType.isInstance(previous)) {
            this.map.put(key, value);
            return valueType.cast(previous);
        } else {
            throw new IllegalArgumentException("This StatikDataMap already contains a value of a different type");
        }
    }

    /**
     * Puts the provided String:boolean pair in this map.
     * <p>
     * Note that you can't replace a value of a different type.
     *
     * @param key   the key
     * @param value the value
     * @return the old value if any, null otherwise
     * @throws IllegalArgumentException if trying to replace a value of a different type
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
     * @throws IllegalArgumentException if trying to replace a value of a different type
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
     * @throws IllegalArgumentException if trying to replace a value of a different type
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
     * @throws IllegalArgumentException if trying to replace a value of a different type
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
     * @throws IllegalArgumentException if trying to replace a value of a different type
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
     * @throws IllegalArgumentException if trying to replace a value of a different type
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
     * @throws IllegalArgumentException if trying to replace a value of a different type
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
     * @throws IllegalArgumentException if trying to replace a value of a different type
     */
    public String putString(String key, String value) {
        Validate.notNull(value, "Value can't be null");
        return this.put(key, value, String.class);
    }
}

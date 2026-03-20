package io.github.team3engine.game.score;

import java.util.HashMap;
import java.util.Map;

public class ScoreContext {
    private final String eventType;
    private final Map<String, Object> payload;

    public ScoreContext(String eventType) {
        this.eventType = eventType;
        this.payload = new HashMap<>();
    }

    public String getEventType() {
        return eventType;
    }

    public void put(String key, Object value) {
        payload.put(key, value);
    }

    public Object get(String key) {
        return payload.get(key);
    }

    public boolean has(String key) {
        return payload.containsKey(key);
    }

    /**
     * Returns the value for {@code key} as an int, or {@code defaultValue}
     * if the key is missing or not a {@link Number}.
     */
    public int getInt(String key, int defaultValue) {
        Object v = payload.get(key);
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        return defaultValue;
    }

    /**
     * Returns the value for {@code key} as a float, or {@code defaultValue}
     * if the key is missing or not a {@link Number}.
     */
    public float getFloat(String key, float defaultValue) {
        Object v = payload.get(key);
        if (v instanceof Number) {
            return ((Number) v).floatValue();
        }
        return defaultValue;
    }

    /**
     * Returns the value for {@code key} as a boolean, or {@code defaultValue}
     * if the key is missing or not a {@link Boolean}.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Object v = payload.get(key);
        if (v instanceof Boolean) {
            return (boolean) v;
        }
        return defaultValue;
    }
}

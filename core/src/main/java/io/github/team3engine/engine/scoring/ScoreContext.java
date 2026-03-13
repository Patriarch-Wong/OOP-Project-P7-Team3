package io.github.team3engine.engine.scoring;

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
}

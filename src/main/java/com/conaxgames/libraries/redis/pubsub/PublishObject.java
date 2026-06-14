package com.conaxgames.libraries.redis.pubsub;

import com.conaxgames.libraries.redis.RedisManager;
import com.google.gson.JsonObject;

/**
 * Fluent builder for an outbound JSON message envelope ({@code origin} + {@code action} + fields).
 */
public class PublishObject {

    private final JsonObject object = new JsonObject();

    public PublishObject(String action) {
        RedisManager manager = RedisManager.getInstance();
        object.addProperty("origin", manager == null ? "unknown" : manager.getIdentifier());
        object.addProperty("action", action);
    }

    public JsonObject build() {
        return object;
    }

    public PublishObject addProperty(String key, String value) {
        if (key != null && value != null) object.addProperty(key, value);
        return this;
    }

    public PublishObject addProperty(String key, boolean value) {
        if (key != null) object.addProperty(key, value);
        return this;
    }

    public PublishObject addProperty(String key, Number value) {
        if (key != null && value != null) object.addProperty(key, value);
        return this;
    }
}

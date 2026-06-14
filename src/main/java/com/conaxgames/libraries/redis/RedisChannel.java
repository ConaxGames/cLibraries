package com.conaxgames.libraries.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.logging.Level;

/**
 * Base for a single Redis pub/sub channel. Subscribes itself on construction and routes
 * decoded JSON payloads to {@link #onMessage(JsonObject)}.
 */
public abstract class RedisChannel {

    protected final RedisManager manager;

    protected RedisChannel(RedisManager manager) {
        this.manager = manager;
        manager.register(this);
    }

    public abstract String getChannel();

    protected abstract void onMessage(JsonObject object);

    void receive(String raw) {
        try {
            onMessage(JsonParser.parseString(raw).getAsJsonObject());
        } catch (Exception e) {
            manager.getLogger().log(Level.SEVERE, "Failed to handle Redis message on channel " + getChannel(), e);
        }
    }

    public void write(JsonObject object) {
        if (object != null) {
            manager.publish(getChannel(), object);
        }
    }
}

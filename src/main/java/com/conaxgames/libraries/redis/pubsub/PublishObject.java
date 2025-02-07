package com.conaxgames.libraries.redis.pubsub;

import com.conaxgames.libraries.redis.JedisConnection;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class PublishObject {

    public JsonObject object;

    public PublishObject(String messageType) {
        this.object = getBase(messageType);
    }

    public JsonObject build() {
        return object;
    }

    public JsonObject getBase(String messageType) {
        String serverName = JedisConnection.getInstance().getInstanceId();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("origin", serverName);
        jsonObject.addProperty("action", messageType);
        return jsonObject;
    }


    public JsonObject addTarget(UUID target) {
        if (target != null) object.addProperty("target", target.toString());
        return object;
    }

    public JsonObject addMessage(String message) {
        if (message != null) object.addProperty("message", message);
        return object;
    }

    public JsonObject addPermission(String permission) {
        if (permission != null) object.addProperty("permission", permission);
        return object;
    }

    public JsonObject addDestination(String destination) {
        if (destination != null) object.addProperty("destination", destination);
        return object;
    }

    public JsonObject addProperty(String key, String field) {
        if (key != null && field != null) object.addProperty(key, field);
        return object;
    }

    public JsonObject addProperty(String key, int field) {
        if (key != null) object.addProperty(key, field);
        return object;
    }

    public JsonObject addProperty(String key, boolean field) {
        if (key != null) object.addProperty(key, field);
        return object;
    }

    public JsonObject addProperty(String key, long field) {
        if (key != null) object.addProperty(key, field);
        return object;
    }

    public JsonObject addProperty(String key, double field) {
        if (key != null) object.addProperty(key, field);
        return object;
    }

}

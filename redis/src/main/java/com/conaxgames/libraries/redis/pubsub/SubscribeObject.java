package com.conaxgames.libraries.redis.pubsub;

import com.conaxgames.api.redis.MessageType;
import com.conaxgames.libraries.redis.JedisConnection;
import com.google.gson.JsonObject;
import lombok.Getter;

@Getter
public class SubscribeObject {

    public MessageType messageType = MessageType.UNKNOWN;
    public String message = null;
    public String permission = null;
    public String destination = null;
    public String ipAddress = null;

    public SubscribeObject() {
    }

    public SubscribeObject from(JsonObject object) {
        if (object == null || !object.has("action")) {
            JedisConnection.getInstance().toConsole("Jedis Subscribe Object: Received JsonObject was null...");
            return null;
        }

        String action = object.get("action").getAsString();
        try {
            messageType = MessageType.valueOf(action);
        } catch (IllegalArgumentException e) {
            JedisConnection.getInstance().toConsole("Invalid action type: " + action);
            return null;
        }

        if (object.has("destination")) {
            destination = object.get("destination").getAsString();
            String localServer = JedisConnection.getInstance().getInstanceId();
            if (!destination.equalsIgnoreCase(localServer)) return null;
        }

        if (object.has("message")) {
            message = object.get("message").getAsString();
        }

        if (object.has("permission")) {
            permission = object.get("permission").getAsString();
        }

        if (object.has("ipAddress")) {
            ipAddress = object.get("ipAddress").getAsString();
        }

        return this;
    }

}

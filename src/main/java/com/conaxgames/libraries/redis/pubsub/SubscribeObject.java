package com.conaxgames.libraries.redis.pubsub;

import com.google.gson.JsonObject;
import lombok.Getter;

/**
 * Parsed view of an inbound JSON message envelope. Project-agnostic: {@code action} is exposed
 * as a raw string for the consumer to map onto its own message-type enum.
 */
@Getter
public class SubscribeObject {

    private String action;
    private String origin;
    private String target;
    private String message;
    private String permission;
    private String destination;
    private String ipAddress;

    public SubscribeObject from(JsonObject object) {
        if (object == null || !object.has("action")) {
            return null;
        }
        this.action = object.get("action").getAsString();
        if (object.has("origin")) this.origin = object.get("origin").getAsString();
        if (object.has("target")) this.target = object.get("target").getAsString();
        if (object.has("message")) this.message = object.get("message").getAsString();
        if (object.has("permission")) this.permission = object.get("permission").getAsString();
        if (object.has("destination")) this.destination = object.get("destination").getAsString();
        if (object.has("ipAddress")) this.ipAddress = object.get("ipAddress").getAsString();
        return this;
    }
}

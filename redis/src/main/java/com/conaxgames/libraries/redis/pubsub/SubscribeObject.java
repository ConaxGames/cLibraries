package com.conaxgames.libraries.redis.pubsub;

import com.conaxgames.libraries.redis.JedisConnection;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class SubscribeObject {

    public String messageType = "UNKNOWN";
    public Player target = null;
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

        messageType = object.get("action").getAsString();

        if (object.has("destination")) {
            destination = object.get("destination").getAsString();
            String localServer = JedisConnection.getInstance().getInstanceId();
            if (!destination.equalsIgnoreCase(localServer)) return null;
        }

        if (object.has("target")) {
            target = Bukkit.getPlayer(UUID.fromString(object.get("target").getAsString()));
        }

        if (object.has("targetName")) {
            target = Bukkit.getPlayerExact(object.get("targetName").getAsString());
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

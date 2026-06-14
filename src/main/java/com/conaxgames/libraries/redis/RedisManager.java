package com.conaxgames.libraries.redis;

import com.google.gson.JsonObject;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Lettuce-backed Redis transport: one shared command connection (publish + key commands)
 * and one pub/sub connection that dispatches every subscribed channel to its {@link RedisChannel}.
 */
@Getter
public class RedisManager {

    private static RedisManager instance;

    private final String identifier;
    private final Logger logger;
    private final RedisClient client;
    private final StatefulRedisConnection<String, String> connection;
    private final StatefulRedisPubSubConnection<String, String> pubSubConnection;
    private final Map<String, RedisChannel> channels = new ConcurrentHashMap<>();

    public RedisManager(RedisCredentials credentials, String identifier, Logger logger) {
        this.identifier = identifier;
        this.logger = logger;
        this.client = RedisClient.create(credentials.toRedisURI());
        this.connection = client.connect();
        this.pubSubConnection = client.connectPubSub();

        instance = this;

        this.pubSubConnection.addListener(new RedisPubSubAdapter<String, String>() {
            @Override
            public void message(String channel, String message) {
                RedisChannel handler = channels.get(channel);
                if (handler != null) {
                    handler.receive(message);
                }
            }
        });
    }

    public static RedisManager getInstance() {
        return instance;
    }

    public void register(RedisChannel channel) {
        channels.put(channel.getChannel(), channel);
        pubSubConnection.sync().subscribe(channel.getChannel());
    }

    public void publish(String channel, JsonObject object) {
        connection.async().publish(channel, object.toString());
    }

    public RedisCommands<String, String> commands() {
        return connection.sync();
    }

    public void shutdown() {
        pubSubConnection.close();
        connection.close();
        client.shutdown();
    }
}

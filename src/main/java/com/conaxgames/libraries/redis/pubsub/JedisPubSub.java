package com.conaxgames.libraries.redis.pubsub;

import com.conaxgames.libraries.redis.JedisConnection;
import com.conaxgames.libraries.redis.JedisCredentials;
import com.conaxgames.libraries.redis.pubsub.handler.JedisPublisher;
import com.conaxgames.libraries.redis.pubsub.handler.JedisSubscriber;
import com.conaxgames.libraries.redis.subscription.model.JedisSubscriptionHandler;
import com.google.gson.JsonObject;

import java.util.concurrent.CompletableFuture;

public abstract class JedisPubSub implements JedisSubscriptionHandler<JsonObject> {

    public JedisPublisher<JsonObject> publisher;
    public JedisSubscriber<JsonObject> subscriber;

    public JedisPubSub(JedisConnection connection, JedisCredentials credentials) {
        this.publisher = new JedisPublisher<>(credentials, getChannel());
        this.subscriber = new JedisSubscriber<>(connection, credentials, getChannel(), JsonObject.class, this);
    }

    public abstract String getChannel();

    public void write(JsonObject object) {
        CompletableFuture.runAsync(() -> publisher.write(object));
    }

}

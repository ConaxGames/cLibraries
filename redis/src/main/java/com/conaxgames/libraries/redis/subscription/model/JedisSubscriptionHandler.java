package com.conaxgames.libraries.redis.subscription.model;

import com.conaxgames.libraries.redis.pubsub.SubscribeObject;

public interface JedisSubscriptionHandler<K> {

    void subscribe(K object, SubscribeObject subscription);

}

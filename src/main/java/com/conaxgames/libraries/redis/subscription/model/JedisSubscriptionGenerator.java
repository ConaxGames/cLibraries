package com.conaxgames.libraries.redis.subscription.model;

public interface JedisSubscriptionGenerator<K> {

    K generateSubscription(String message);

}

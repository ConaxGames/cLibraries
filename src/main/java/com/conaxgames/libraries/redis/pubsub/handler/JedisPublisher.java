package com.conaxgames.libraries.redis.pubsub.handler;

import com.conaxgames.libraries.redis.JedisCredentials;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;

@RequiredArgsConstructor
public class JedisPublisher<K> {
    @Getter
    private final JedisCredentials jedisSettings;
    private final String channel;

    /**
     * Sends the required message to the channel that we are currently on.
     */
    public void write(K message) {
        Jedis jedis = null;
        try {
            jedis = this.jedisSettings.getJedisPool().getResource();
            jedis.publish(this.channel, message.toString());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}

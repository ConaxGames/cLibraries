package com.conaxgames.libraries.redis.pubsub.handler;

import com.conaxgames.libraries.redis.JedisCredentials;
import com.conaxgames.libraries.redis.JedisConnection;
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
            if (message != null) {
                jedis.publish(this.channel, message.toString());
            } else {
                JedisConnection.getInstance().toConsole("JedisPublisher: Attempted to publish null message to channel " + this.channel);
            }
        } catch (Exception e) {
            JedisConnection.getInstance().toConsole("JedisPublisher: Error publishing message to channel " + this.channel + ": " + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}

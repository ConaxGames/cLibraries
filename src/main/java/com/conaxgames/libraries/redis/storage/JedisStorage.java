package com.conaxgames.libraries.redis.storage;

import com.conaxgames.libraries.redis.JedisCredentials;
import redis.clients.jedis.Jedis;

public class JedisStorage extends JedisImplementation {

    public JedisStorage(JedisCredentials jedisSettings) {
        super(jedisSettings);
    }

    public String get(String channel, String key) {
        Jedis jedis = this.getJedis();

        try {
            return jedis.hgetAll(channel).get(key);
        } finally {
            this.cleanup(jedis);
        }
    }

    public void remove(String channel, String key) {
        Jedis jedis = this.getJedis();

        try {
            jedis.hdel(channel, key);
        } finally {
            this.cleanup(jedis);
        }
    }

    public void set(String channel, String key, String value) {
        Jedis jedis = this.getJedis();
        try {
            jedis.hset(channel, key, value);
        } finally {
            this.cleanup(jedis);
        }
    }

}

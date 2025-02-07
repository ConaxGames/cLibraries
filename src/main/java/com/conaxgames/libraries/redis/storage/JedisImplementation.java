package com.conaxgames.libraries.redis.storage;

import com.conaxgames.libraries.redis.JedisCredentials;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;

@RequiredArgsConstructor
public class JedisImplementation {

    protected final JedisCredentials jedisSettings;

    protected void cleanup(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    protected Jedis getJedis() {
        return this.jedisSettings.getJedisPool().getResource();
    }

}

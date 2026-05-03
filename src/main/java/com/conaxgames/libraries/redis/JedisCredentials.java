package com.conaxgames.libraries.redis;

import lombok.Getter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Getter
public class JedisCredentials {

    private final String address;
    private final int port;
    private final String password;
    private final JedisPool jedisPool;

    public JedisCredentials(String address, int port, String password) {
        this(address, port, password, new JedisPoolConfig());
    }

    public JedisCredentials(String address, int port, String password, JedisPoolConfig config) {
        this.address = address;
        this.port = port;
        this.password = password;

        this.jedisPool = new JedisPool(config, this.address, this.port, 0, this.password);
    }

    public JedisCredentials(String address, String password) {
        this(address, 6379, password);
    }

    public JedisCredentials(String address) {
        this(address, null);
    }

    public boolean hasPassword() {
        return this.password != null;
    }

}

package com.conaxgames.libraries.redis;

import io.lettuce.core.RedisURI;
import lombok.Getter;

@Getter
public class RedisCredentials {

    private final String host;
    private final int port;
    private final String password;

    public RedisCredentials(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
    }

    public RedisURI toRedisURI() {
        RedisURI.Builder builder = RedisURI.builder().withHost(host).withPort(port);
        if (password != null && !password.isEmpty()) {
            builder.withPassword(password.toCharArray());
        }
        return builder.build();
    }
}

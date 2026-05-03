package com.conaxgames.libraries.redis.message;

public interface MessageTypeResolver {
    MessageTypeInterface resolve(String action);
}

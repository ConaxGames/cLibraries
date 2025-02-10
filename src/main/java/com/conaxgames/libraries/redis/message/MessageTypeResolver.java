package com.conaxgames.libraries.redis.message;

/**
 * An interface to resolve message types from string actions.
 */
public interface MessageTypeResolver {
    MessageTypeInterface resolve(String action);
}

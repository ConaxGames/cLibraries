package com.conaxgames.libraries.redis.message;

/**
 * A generic interface for message types to decouple lib from specific API enums.
 */
public interface MessageTypeInterface {
    String name();
}

package com.conaxgames.libraries.redis.message;

/**
 * Implemented by a project's message-type enum so the generic transport can carry it as the
 * {@code action} on the wire via {@link #name()}.
 */
public interface MessageTypeInterface {

    String name();
}

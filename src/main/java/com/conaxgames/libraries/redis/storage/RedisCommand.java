package com.conaxgames.libraries.redis.storage;

public interface RedisCommand<T> {

    void execute(T t);
}

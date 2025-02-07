package com.conaxgames.libraries.ttl;

public interface TtlHandler<E> {

    void onExpire(E element);

    long getTimestamp(E element);

}
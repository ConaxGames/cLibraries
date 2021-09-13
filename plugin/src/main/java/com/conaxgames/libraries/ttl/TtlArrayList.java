package com.conaxgames.libraries.ttl;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TtlArrayList<E> implements List<E>, TtlHandler<E> {

    private final HashMap<E, Long> timestamps = new HashMap<>();
    private final List<E> store = new ArrayList<>();
    private final long ttl;

    public TtlArrayList(TimeUnit ttlUnit, long ttlValue) {
        this.ttl = ttlUnit.toNanos(ttlValue);
    }

    private boolean expired(E value) {
        return (System.nanoTime() - this.timestamps.get(value)) > this.ttl;
    }

    @Override
    public long getTimestamp(E value) {
        return this.timestamps.get(value);
    }

    // Let niggers handle this
    @Override
    public void onExpire(E element) {

    }

    @Override
    public E get(int index) {
        E e = this.store.get(index);

        if (e != null && this.timestamps.containsKey(e) && this.expired(e)) {
            this.store.remove(e);
            this.timestamps.remove(e);
            return null;
        }

        return e;
    }

    @Override
    public int indexOf(Object o) {
        return this.store.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.store.lastIndexOf(o);
    }

    @Override
    public E set(int index, E e) {
        this.timestamps.put(e, System.nanoTime());

        return this.store.set(index, e);
    }

    @Override
    public boolean add(E value) {
        this.timestamps.put(value, System.nanoTime());

        return this.store.add(value);
    }

    @Override
    public void add(int i, E value) {
        this.timestamps.put(value, System.nanoTime());

        this.store.add(i, value);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return this.store.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return this.store.addAll(index, c);
    }

    @Override
    public int size() {
        return this.store.size();
    }

    @Override
    public boolean isEmpty() {
        return this.store.isEmpty();
    }

    @Override
    public boolean contains(Object value) {
        if (value != null && this.store.contains(value) && this.timestamps.containsKey(value) && this.expired((E) value)) {
            this.store.remove(value);
            this.timestamps.remove(value);
            return false;
        }

        return this.store.contains(value);
    }

    @Override
    public boolean remove(Object value) {
        boolean cont = this.contains(value);
        this.timestamps.remove(value);
        this.store.remove(value);
        return cont;
    }

    @Override
    public E remove(int i) {
        return this.store.remove(i);
    }

    @Override
    public boolean removeAll(Collection<?> a) {
        for (Object object : a) {
            this.timestamps.remove(object);
        }

        return this.store.removeAll(a);
    }

    @Override
    public void clear() {
        this.timestamps.clear();
        this.store.clear();
    }

    @Override
    public Object[] toArray() {
        return this.store.toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        return this.store.toArray(a);
    }

    @Override
    public ListIterator<E> listIterator() {
        return this.store.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int a) {
        return this.store.listIterator(a);
    }

    public Iterator<E> iterator() {
        return this.store.iterator();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.store.retainAll(c);
    }

    @Override
    public List<E> subList(int fromIndex,
                           int toIndex) {
        return this.store.subList(fromIndex, toIndex);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.store.containsAll(c);
    }

}

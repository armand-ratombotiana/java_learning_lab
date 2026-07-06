package com.dsacademy.lab16.concurrent;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> {

    private final ConcurrentHashMap<E, Boolean> map = new ConcurrentHashMap<>();

    public boolean add(E e) { return map.putIfAbsent(e, Boolean.TRUE) == null; }

    public boolean remove(E e) { return map.remove(e) != null; }

    public boolean contains(E e) { return map.containsKey(e); }

    public int size() { return map.size(); }

    public void clear() { map.clear(); }

    public boolean isEmpty() { return map.isEmpty(); }
}

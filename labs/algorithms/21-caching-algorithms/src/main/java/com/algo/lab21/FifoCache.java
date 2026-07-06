package com.algo.lab21;

import java.util.*;

/**
 * FIFO (First-In-First-Out) cache eviction policy.
 * Evicts the oldest entry when the cache is full.
 * Time: O(1) for get/put, Space: O(capacity)
 */
public class FifoCache<K, V> {

    private final int capacity;
    private final LinkedHashMap<K, V> map;

    public FifoCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        this.map = new LinkedHashMap<>(capacity, 0.75f, false);
    }

    public V get(K key) {
        return map.get(key);
    }

    public void put(K key, V value) {
        if (map.containsKey(key)) {
            map.put(key, value);
        } else {
            if (map.size() >= capacity) {
                K oldest = map.keySet().iterator().next();
                map.remove(oldest);
            }
            map.put(key, value);
        }
    }

    public int size() {
        return map.size();
    }

    public void clear() {
        map.clear();
    }
}

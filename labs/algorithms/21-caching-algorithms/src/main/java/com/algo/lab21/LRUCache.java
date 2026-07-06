package com.algo.lab21;

import java.util.*;

/**
 * LRU (Least Recently Used) cache via LinkedHashMap.
 * Evicts the least recently accessed entry when full.
 * Time: O(1) for get/put, Space: O(capacity)
 */
public class LRUCache<K, V> {

    private final int capacity;
    private final LinkedHashMap<K, V> map;

    public LRUCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        this.map = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LRUCache.this.capacity;
            }
        };
    }

    public V get(K key) {
        return map.get(key);
    }

    public void put(K key, V value) {
        map.put(key, value);
    }

    public int size() {
        return map.size();
    }

    public void clear() {
        map.clear();
    }
}

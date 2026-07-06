package com.dsacademy.lab15.lrucache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCacheLinkedHashMap<K, V> {

    private final LinkedHashMap<K, V> map;

    public LRUCacheLinkedHashMap(int capacity) {
        this.map = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }

    public V get(K key) {
        return map.get(key);
    }

    public void put(K key, V value) {
        map.put(key, value);
    }

    public boolean contains(K key) {
        return map.containsKey(key);
    }

    public int size() { return map.size(); }

    public void clear() { map.clear(); }
}

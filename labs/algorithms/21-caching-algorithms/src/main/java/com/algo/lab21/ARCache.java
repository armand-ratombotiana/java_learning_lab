package com.algo.lab21;

import java.util.*;

/**
 * ARC (Adaptive Replacement Cache).
 * Dynamically balances between recency and frequency by maintaining
 * two lists: recent (LRU) and frequent (LFU).
 * Time: O(1) for get/put, Space: O(capacity)
 */
public class ARCache<K, V> {

    private final int capacity;
    private final LinkedHashMap<K, V> recent;
    private final LinkedHashMap<K, V> frequent;
    private int p;

    public ARCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        this.recent = new LinkedHashMap<>(capacity, 0.75f, true);
        this.frequent = new LinkedHashMap<>(capacity, 0.75f, true);
        this.p = 0;
    }

    public V get(K key) {
        if (frequent.containsKey(key)) {
            return frequent.get(key);
        }
        if (recent.containsKey(key)) {
            V value = recent.remove(key);
            frequent.put(key, value);
            return value;
        }
        return null;
    }

    public void put(K key, V value) {
        if (frequent.containsKey(key)) {
            frequent.put(key, value);
            return;
        }
        if (recent.containsKey(key)) {
            recent.put(key, value);
            frequent.put(key, recent.remove(key));
            return;
        }
        if (recent.size() + frequent.size() >= capacity) {
            if (recent.size() >= Math.max(1, p)) {
                K evict = recent.keySet().iterator().next();
                recent.remove(evict);
            } else {
                K evict = frequent.keySet().iterator().next();
                frequent.remove(evict);
            }
        }
        recent.put(key, value);
    }

    public int size() {
        return recent.size() + frequent.size();
    }

    public void clear() {
        recent.clear();
        frequent.clear();
        p = 0;
    }
}

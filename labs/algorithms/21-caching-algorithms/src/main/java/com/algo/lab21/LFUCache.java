package com.algo.lab21;

import java.util.*;

/**
 * LFU (Least Frequently Used) cache with frequency tracking.
 * Evicts the least frequently accessed entry; ties broken by LRU.
 * Time: O(log n) for get/put, Space: O(capacity)
 */
public class LFUCache<K, V> {

    private final int capacity;
    private final Map<K, V> values;
    private final Map<K, Integer> frequencies;
    private final TreeMap<Integer, LinkedHashSet<K>> freqMap;

    public LFUCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        this.values = new HashMap<>();
        this.frequencies = new HashMap<>();
        this.freqMap = new TreeMap<>();
    }

    public V get(K key) {
        if (!values.containsKey(key)) return null;
        incrementFrequency(key);
        return values.get(key);
    }

    public void put(K key, V value) {
        if (values.containsKey(key)) {
            values.put(key, value);
            incrementFrequency(key);
            return;
        }
        if (values.size() >= capacity) {
            evict();
        }
        values.put(key, value);
        frequencies.put(key, 1);
        freqMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
    }

    private void incrementFrequency(K key) {
        int freq = frequencies.get(key);
        frequencies.put(key, freq + 1);
        freqMap.get(freq).remove(key);
        if (freqMap.get(freq).isEmpty()) freqMap.remove(freq);
        freqMap.computeIfAbsent(freq + 1, k -> new LinkedHashSet<>()).add(key);
    }

    private void evict() {
        int lowestFreq = freqMap.firstKey();
        LinkedHashSet<K> keys = freqMap.get(lowestFreq);
        K evict = keys.iterator().next();
        keys.remove(evict);
        if (keys.isEmpty()) freqMap.remove(lowestFreq);
        values.remove(evict);
        frequencies.remove(evict);
    }

    public int size() {
        return values.size();
    }

    public void clear() {
        values.clear();
        frequencies.clear();
        freqMap.clear();
    }
}

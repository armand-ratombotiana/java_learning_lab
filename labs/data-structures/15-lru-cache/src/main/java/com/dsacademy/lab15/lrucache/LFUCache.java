package com.dsacademy.lab15.lrucache;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LFUCache<K, V> {

    private static class Node<K, V> {
        K key;
        V value;
        int freq;
        Node(K key, V value) { this.key = key; this.value = value; this.freq = 1; }
    }

    private final int capacity;
    private int minFreq;
    private final Map<K, Node<K, V>> cache;
    private final Map<Integer, LinkedHashSet<K>> freqMap;

    public LFUCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        this.minFreq = 0;
        this.cache = new HashMap<>();
        this.freqMap = new HashMap<>();
    }

    public V get(K key) {
        Node<K, V> node = cache.get(key);
        if (node == null) return null;
        updateFrequency(node);
        return node.value;
    }

    public void put(K key, V value) {
        Node<K, V> node = cache.get(key);
        if (node != null) {
            node.value = value;
            updateFrequency(node);
            return;
        }
        if (cache.size() >= capacity) {
            evict();
        }
        node = new Node<>(key, value);
        cache.put(key, node);
        freqMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
        minFreq = 1;
    }

    private void updateFrequency(Node<K, V> node) {
        int oldFreq = node.freq;
        freqMap.get(oldFreq).remove(node.key);
        if (freqMap.get(oldFreq).isEmpty()) {
            freqMap.remove(oldFreq);
            if (minFreq == oldFreq) minFreq++;
        }
        node.freq++;
        freqMap.computeIfAbsent(node.freq, k -> new LinkedHashSet<>()).add(node.key);
    }

    private void evict() {
        LinkedHashSet<K> keys = freqMap.get(minFreq);
        K keyToEvict = keys.iterator().next();
        keys.remove(keyToEvict);
        if (keys.isEmpty()) freqMap.remove(minFreq);
        cache.remove(keyToEvict);
    }

    public boolean contains(K key) { return cache.containsKey(key); }
    public int size() { return cache.size(); }
    public int capacity() { return capacity; }
}

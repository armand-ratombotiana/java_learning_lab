package com.sd.caching;

import java.util.*;

public class LruCache<K, V> {

    private final LinkedHashMap<K, V> map;
    private final int capacity;

    public LruCache(int capacity) {
        this.capacity = capacity;
        this.map = new LinkedHashMap<K, V>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }

    public synchronized V get(K key) {
        V value = map.get(key);
        if (value != null) {
            System.out.println("Cache HIT: " + key + " = " + value);
        } else {
            System.out.println("Cache MISS: " + key);
        }
        return value;
    }

    public synchronized void put(K key, V value) {
        map.put(key, value);
        System.out.println("Cache PUT: " + key + " = " + value + " (size: " + map.size() + "/" + capacity + ")");
    }

    public synchronized boolean contains(K key) {
        return map.containsKey(key);
    }

    public synchronized int size() { return map.size(); }

    public static class LruWithTTL<K, V> {
        private static class TimedValue<V> {
            final V value;
            final long expiry;

            TimedValue(V value, long ttlMs) {
                this.value = value;
                this.expiry = System.currentTimeMillis() + ttlMs;
            }

            boolean isExpired() { return System.currentTimeMillis() > expiry; }
        }

        private final LinkedHashMap<K, TimedValue<V>> map;

        public LruWithTTL(int capacity) {
            this.map = new LinkedHashMap<K, TimedValue<V>>(capacity, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<K, TimedValue<V>> eldest) {
                    return size() > capacity;
                }
            };
        }

        public synchronized V get(K key) {
            TimedValue<V> tv = map.get(key);
            if (tv == null) return null;
            if (tv.isExpired()) {
                map.remove(key);
                return null;
            }
            return tv.value;
        }

        public synchronized void put(K key, V value, long ttlMs) {
            map.put(key, new TimedValue<>(value, ttlMs));
        }
    }

    public static void main(String[] args) {
        LruCache<String, String> cache = new LruCache<>(3);

        cache.put("A", "1");
        cache.put("B", "2");
        cache.put("C", "3");
        cache.get("A");
        cache.put("D", "4");

        System.out.println("\nContains A: " + cache.contains("A"));
        System.out.println("Contains B: " + cache.contains("B"));
        System.out.println("Contains D: " + cache.contains("D"));
    }
}

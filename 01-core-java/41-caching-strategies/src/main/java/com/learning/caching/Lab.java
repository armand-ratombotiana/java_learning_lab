package com.learning.caching;

import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

public class Lab {

    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int maxSize;
        LRUCache(int maxSize) {
            super(16, 0.75f, true);
            this.maxSize = maxSize;
        }
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > maxSize;
        }
    }

    static class TTLCache<K, V> {
        private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
        private final long ttlMillis;

        record CacheEntry<V>(V value, long expiry) {}

        TTLCache(long ttlMillis) { this.ttlMillis = ttlMillis; }

        void put(K key, V value) {
            cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttlMillis));
        }

        V get(K key) {
            var entry = cache.get(key);
            if (entry == null) return null;
            if (System.currentTimeMillis() > entry.expiry) {
                cache.remove(key);
                return null;
            }
            return entry.value;
        }
    }

    static class ComputeIfAbsentCache<K, V> {
        private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();

        V getOrCompute(K key, Function<K, V> loader) {
            return cache.computeIfAbsent(key, loader);
        }

        void invalidate(K key) { cache.remove(key); }
        void clear() { cache.clear(); }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Caching Strategies Lab ===\n");

        lruCache();
        ttlCache();
        cacheAside();
        readThrough();
        writeStrategies();
        distributedCaching();
    }

    static void lruCache() {
        System.out.println("--- LRU (Least Recently Used) ---");
        var cache = new LRUCache<String, String>(3);
        cache.put("A", "value-a");
        cache.put("B", "value-b");
        cache.put("C", "value-c");
        System.out.println("  After A,B,C: " + cache.keySet());
        cache.get("A");
        cache.put("D", "value-d");
        System.out.println("  After get(A), put(D): " + cache.keySet() + " (B evicted)");
        System.out.println("  Performance: O(1) get/put (LinkedHashMap)");
    }

    static void ttlCache() throws Exception {
        System.out.println("\n--- TTL (Time-To-Live) ---");
        var cache = new TTLCache<String, String>(100);
        cache.put("session-1", "token-xyz");
        System.out.println("  Immediate get: " + cache.get("session-1"));
        Thread.sleep(150);
        System.out.println("  After 150ms:  " + cache.get("session-1") + " (expired)");
    }

    static void cacheAside() {
        System.out.println("\n--- Cache-Aside (Lazy Loading) ---");
        System.out.println("  1. Check cache -> miss");
        System.out.println("  2. Load from DB");
        System.out.println("  3. Populate cache");
        System.out.println("  4. Return data");
        System.out.println("  Write: invalidate cache, then update DB");
        System.out.println("  Risk: stale data if DB write succeeds but cache invalidation fails");
    }

    static void readThrough() {
        System.out.println("\n--- Read-Through ---");
        var cache = new ComputeIfAbsentCache<String, String>();
        var db = Map.of("k1", "db-value-1", "k2", "db-value-2");

        String val = cache.getOrCompute("k1", key -> {
            System.out.println("  [Cache miss] loading from DB: " + key);
            return db.get(key);
        });
        System.out.println("  Result: " + val);

        String val2 = cache.getOrCompute("k1", key -> {
            System.out.println("  [Cache miss] loading from DB: " + key);
            return db.get(key);
        });
        System.out.println("  Cached result: " + val2 + " (no DB load)");
    }

    static void writeStrategies() {
        System.out.println("\n--- Write Strategies ---");
        System.out.println("""
  Write-Through: write cache AND DB in same transaction
    + cache always consistent, no stale reads
    - higher write latency

  Write-Behind: write to cache, async write to DB
    + low write latency
    - risk of data loss on crash

  Write-Around: write directly to DB, invalidate cache
    + simple, no cache pollution
    - next read is a miss
    """);
    }

    static void distributedCaching() {
        System.out.println("\n--- Distributed Caching ---");
        System.out.println("  Consistent hashing: distribute keys across nodes");
        System.out.println("  Replication: copy data to N nodes for fault tolerance");
        System.out.println("  Redis/Memcached: popular distributed caches");
        System.out.println("  Cache stampede: Thundering herd on expiry -> solve with early re-compute");
        System.out.println("  Multi-level: L1 (local) + L2 (distributed)");
    }
}

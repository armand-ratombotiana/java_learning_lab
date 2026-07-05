package com.sd.caching;

import java.util.*;
import java.util.concurrent.*;

public class CacheAsidePattern {

    public static class Database {
        private final Map<String, String> store = new ConcurrentHashMap<>();

        public String get(String key) {
            simulateLatency(50);
            return store.get(key);
        }

        public void put(String key, String value) {
            simulateLatency(50);
            store.put(key, value);
        }

        private void simulateLatency(long ms) {
            try { Thread.sleep(ms); } catch (InterruptedException e) {}
        }

        public int size() { return store.size(); }
    }

    public static class CacheAside {
        private final Map<String, String> cache;
        private final Database db;
        private final int cacheTtlMs;

        public CacheAside(int cacheSize, Database db, int ttlMs) {
            this.cache = new LinkedHashMap<String, String>(cacheSize, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                    return size() > cacheSize;
                }
            };
            this.db = db;
            this.cacheTtlMs = ttlMs;
        }

        private final Map<String, Long> expiry = new HashMap<>();

        public synchronized String get(String key) {
            if (cache.containsKey(key) && !isExpired(key)) {
                System.out.println("Cache HIT: " + key);
                return cache.get(key);
            }

            System.out.println("Cache MISS: " + key + " (fetching from DB)");
            String value = db.get(key);
            if (value != null) {
                cache.put(key, value);
                expiry.put(key, System.currentTimeMillis() + cacheTtlMs);
            }
            return value;
        }

        public synchronized void put(String key, String value) {
            db.put(key, value);
            cache.put(key, value);
            expiry.put(key, System.currentTimeMillis() + cacheTtlMs);
            System.out.println("Wrote through cache-aside: " + key + " = " + value);
        }

        private boolean isExpired(String key) {
            Long exp = expiry.get(key);
            return exp != null && System.currentTimeMillis() > exp;
        }

        public int cacheSize() { return cache.size(); }
    }

    public static void main(String[] args) {
        Database db = new Database();
        db.put("name", "Alice");
        db.put("email", "alice@example.com");

        CacheAside cache = new CacheAside(100, db, 60000);

        System.out.println("=== Cache-Aside Pattern ===");
        System.out.println("First read (miss): " + cache.get("name"));
        System.out.println("Second read (hit): " + cache.get("name"));

        cache.put("role", "admin");
        System.out.println("Read after write: " + cache.get("role"));

        System.out.println("\nCache size: " + cache.cacheSize());
    }
}

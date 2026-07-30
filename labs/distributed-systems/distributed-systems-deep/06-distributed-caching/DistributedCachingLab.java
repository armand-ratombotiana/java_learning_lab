package com.distributedsystems.deep.lab06;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * DistributedCachingLab — implements cache-aside, write-through, write-behind
 * patterns and simulates cache stampede mitigation.
 */
public class DistributedCachingLab {

    static class Cache {
        final Map<String, String> store = new ConcurrentHashMap<>();
        final AtomicLong hits = new AtomicLong(0), misses = new AtomicLong(0);
        String get(String key) { String v = store.get(key); if (v != null) hits.incrementAndGet(); else misses.incrementAndGet(); return v; }
        void put(String key, String value) { store.put(key, value); }
        void invalidate(String key) { store.remove(key); }
    }
    static class Database { final Map<String, String> store = new ConcurrentHashMap<>();
        String get(String key) { return store.get(key); }
        void put(String key, String value) { store.put(key, value); }
    }

    static String cacheAsideRead(Cache cache, Database db, String key) {
        String val = cache.get(key);
        if (val != null) return val;
        val = db.get(key);
        if (val != null) cache.put(key, val);
        return val;
    }

    static void writeThrough(Cache cache, Database db, String key, String value) {
        cache.put(key, value); db.put(key, value);
    }

    static void writeBehind(Cache cache, Database db, String key, String value) {
        cache.put(key, value);
        CompletableFuture.runAsync(() -> { try { TimeUnit.MILLISECONDS.sleep(10); } catch (Exception e) {} db.put(key, value); });
    }

    static double simulateStampede(int concurrentRequests, boolean useLock) throws Exception {
        Cache cache = new Cache();
        Database db = new Database();
        db.put("hot_key", "expensive_value");
        AtomicInteger dbCalls = new AtomicInteger(0);
        AtomicBoolean lock = new AtomicBoolean(false);
        var exec = Executors.newFixedThreadPool(concurrentRequests);
        var futures = new ArrayList<CompletableFuture<String>>();
        for (int i = 0; i < concurrentRequests; i++) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                String val = cache.get("hot_key");
                if (val != null) return val;
                if (useLock) {
                    if (!lock.compareAndSet(false, true)) {
                        for (int j = 0; j < 50; j++) { val = cache.get("hot_key"); if (val != null) return val; try { TimeUnit.MILLISECONDS.sleep(5); } catch (Exception e) { break; } }
                    }
                }
                val = db.get("hot_key"); dbCalls.incrementAndGet(); cache.put("hot_key", val);
                if (useLock) lock.set(false);
                return val;
            }, exec));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        exec.shutdown();
        return dbCalls.get();
    }

    public static void main(String[] args) throws Exception {
        Cache cache = new Cache(); Database db = new Database(); db.put("user:1", "Alice");
        writeThrough(cache, db, "user:1", "Alice Updated");
        System.out.println("Write-through: cache=" + cache.get("user:1") + " db=" + db.get("user:1"));
        cache.invalidate("user:1");
        System.out.println("Cache-aside read: " + cacheAsideRead(cache, db, "user:1"));
        System.out.println("\n=== Cache Stampede ===");
        double noLock = simulateStampede(50, false);
        double withLock = simulateStampede(50, true);
        System.out.println("Without lock: " + noLock + " DB calls (expected ~50)");
        System.out.println("With lock:    " + withLock + " DB calls (expected ~1)");
    }
}
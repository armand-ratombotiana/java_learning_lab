package com.systemdesign.deep.lab02;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Lab 02: Distributed Caching — Cache-Aside, Read-Through, Write-Through,
 * Write-Behind, Eviction Policies, and Distributed Cache Topology.
 */
public class DistributedCachingLab {

    // Shared database simulator
    static class Database {
        final Map<String, String> data = new ConcurrentHashMap<>();

        void put(String k, String v) { data.put(k, v); }
        String get(String k) {
            sleep(10); // simulate DB latency
            return data.get(k);
        }
        void delete(String k) { data.remove(k); }
        long count() { return data.size(); }
    }

    static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    // ──────────────────────────────────────────────
    // 1. Cache-Aside Implementation
    // ──────────────────────────────────────────────
    static class CacheAside {
        final Map<String, String> cache = new ConcurrentHashMap<>();
        final Database db;
        final long ttlMs;
        final Map<String, Long> timestamps = new ConcurrentHashMap<>();

        CacheAside(Database db, long ttlMs) {
            this.db = db;
            this.ttlMs = ttlMs;
        }

        String get(String key) {
            Long ts = timestamps.get(key);
            if (ts != null && (System.currentTimeMillis() - ts) < ttlMs) {
                System.out.println("  [CacheAside] HIT for " + key);
                return cache.get(key);
            }
            System.out.println("  [CacheAside] MISS for " + key + " — loading from DB");
            String val = db.get(key);
            if (val != null) {
                cache.put(key, val);
                timestamps.put(key, System.currentTimeMillis());
            }
            return val;
        }

        void put(String key, String value) {
            db.put(key, value);
            cache.put(key, value);
            timestamps.put(key, System.currentTimeMillis());
            System.out.println("  [CacheAside] Updated " + key + " in DB + cache");
        }

        void invalidate(String key) {
            cache.remove(key);
            timestamps.remove(key);
            System.out.println("  [CacheAside] Invalidated " + key);
        }
    }

    // ──────────────────────────────────────────────
    // 2. Read-Through Implementation
    // ──────────────────────────────────────────────
    static class ReadThroughCache {
        final Map<String, String> cache = new ConcurrentHashMap<>();
        final Function<String, String> loader;

        ReadThroughCache(Function<String, String> loader) {
            this.loader = loader;
        }

        String get(String key) {
            String val = cache.get(key);
            if (val != null) {
                System.out.println("  [ReadThrough] HIT for " + key);
                return val;
            }
            System.out.println("  [ReadThrough] MISS — loading via CacheLoader for " + key);
            val = loader.apply(key);
            if (val != null) cache.put(key, val);
            return val;
        }
    }

    // ──────────────────────────────────────────────
    // 3. Write-Through Implementation
    // ──────────────────────────────────────────────
    static class WriteThroughCache {
        final Map<String, String> cache = new ConcurrentHashMap<>();
        final Database db;

        WriteThroughCache(Database db) {
            this.db = db;
        }

        String get(String key) {
            String val = cache.get(key);
            if (val == null) {
                val = db.get(key);
                if (val != null) cache.put(key, val);
            }
            return val;
        }

        void put(String key, String value) {
            System.out.println("  [WriteThrough] Writing " + key + " to DB (synchronous)");
            db.put(key, value);
            cache.put(key, value);
        }

        void delete(String key) {
            db.delete(key);
            cache.remove(key);
        }
    }

    // ──────────────────────────────────────────────
    // 4. Write-Behind Implementation
    // ──────────────────────────────────────────────
    static class WriteBehindCache {
        final Map<String, String> cache = new ConcurrentHashMap<>();
        final Database db;
        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        final Queue<Map.Entry<String, String>> writeQueue = new ConcurrentLinkedQueue<>();
        final AtomicInteger pendingWrites = new AtomicInteger();

        WriteBehindCache(Database db, long flushIntervalMs) {
            this.db = db;
            scheduler.scheduleAtFixedRate(this::flush, flushIntervalMs, flushIntervalMs, TimeUnit.MILLISECONDS);
        }

        String get(String key) {
            String val = cache.get(key);
            if (val == null) {
                val = db.get(key);
                if (val != null) cache.put(key, val);
            }
            return val;
        }

        void put(String key, String value) {
            cache.put(key, value);
            writeQueue.add(Map.entry(key, value));
            pendingWrites.incrementAndGet();
            System.out.println("  [WriteBehind] Queued write for " + key + " (pending: " + pendingWrites.get() + ")");
        }

        void flush() {
            List<Map.Entry<String, String>> batch = new ArrayList<>();
            Map.Entry<String, String> entry;
            while ((entry = writeQueue.poll()) != null) {
                batch.add(entry);
            }
            if (!batch.isEmpty()) {
                System.out.println("  [WriteBehind] Flushing " + batch.size() + " writes to DB");
                for (var e : batch) {
                    db.put(e.getKey(), e.getValue());
                    pendingWrites.decrementAndGet();
                }
            }
        }

        void shutdown() {
            scheduler.shutdown();
            try { scheduler.awaitTermination(5, TimeUnit.SECONDS); } catch (InterruptedException e) {}
            flush();
        }
    }

    // ──────────────────────────────────────────────
    // 5. Eviction Policy Comparison
    // ──────────────────────────────────────────────
    static class EvictionPolicies {

        interface Cache<K, V> {
            V get(K key);
            void put(K key, V value);
            int size();
        }

        static class LRUCache<K, V> extends LinkedHashMap<K, V> implements Cache<K, V> {
            final int maxSize;
            LRUCache(int maxSize) {
                super(maxSize, 0.75f, true);
                this.maxSize = maxSize;
            }
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) { return size() > maxSize; }
            public int size() { return super.size(); }
        }

        static class LFUCache<K, V> implements Cache<K, V> {
            final int maxSize;
            final Map<K, V> values = new HashMap<>();
            final Map<K, Integer> frequencies = new HashMap<>();
            int accessCount = 0;

            LFUCache(int maxSize) { this.maxSize = maxSize; }

            public V get(K key) {
                V val = values.get(key);
                if (val != null) {
                    frequencies.merge(key, 1, Integer::sum);
                    accessCount++;
                }
                return val;
            }

            public void put(K key, V value) {
                if (values.size() >= maxSize && !values.containsKey(key)) {
                    K lfuKey = frequencies.entrySet().stream()
                            .min(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey)
                            .orElseThrow();
                    values.remove(lfuKey);
                    frequencies.remove(lfuKey);
                }
                values.put(key, value);
                frequencies.put(key, 1);
            }

            public int size() { return values.size(); }
        }

        static void demo() {
            System.out.println("=== Eviction Policy Comparison ===");
            int ops = 100_000;
            int cacheSize = 1000;
            int keySpace = 10_000;

            System.out.println("  Testing LRU with " + ops + " ops, cache size " + cacheSize + "...");
            var lru = new LRUCache<Integer, String>(cacheSize);
            long start = System.nanoTime();
            for (int i = 0; i < ops; i++) {
                int key = ThreadLocalRandom.current().nextInt(keySpace);
                lru.put(key, "v" + key);
                lru.get(key);
            }
            long lruTime = (System.nanoTime() - start) / 1_000_000;

            System.out.println("  Testing LFU with " + ops + " ops, cache size " + cacheSize + "...");
            var lfu = new LFUCache<Integer, String>(cacheSize);
            start = System.nanoTime();
            for (int i = 0; i < ops; i++) {
                int key = ThreadLocalRandom.current().nextInt(keySpace);
                lfu.put(key, "v" + key);
                lfu.get(key);
            }
            long lfuTime = (System.nanoTime() - start) / 1_000_000;

            System.out.println("  LRU: " + lruTime + " ms, LFU: " + lfuTime + " ms");
            System.out.println("  LRU final size: " + lru.size() + ", LFU final size: " + lfu.size());
            System.out.println();
        }
    }

    // ──────────────────────────────────────────────
    // 6. Distributed Cache Topology Sim
    // ──────────────────────────────────────────────
    static class DistributedCacheTopology {

        static class CacheNode {
            final String id;
            final Map<String, String> data = new ConcurrentHashMap<>();
            CacheNode(String id) { this.id = id; }
            String get(String k) { return data.get(k); }
            void put(String k, String v) { data.put(k, v); }
        }

        // Simple consistent hash ring
        static class ConsistentHashRing {
            final TreeMap<Integer, CacheNode> ring = new TreeMap<>();
            final int virtualNodes;

            ConsistentHashRing(int virtualNodes, List<CacheNode> nodes) {
                this.virtualNodes = virtualNodes;
                for (var node : nodes) {
                    for (int i = 0; i < virtualNodes; i++) {
                        int hash = (node.id + "#" + i).hashCode() & 0x7fffffff;
                        ring.put(hash, node);
                    }
                }
            }

            CacheNode getNode(String key) {
                int hash = key.hashCode() & 0x7fffffff;
                var entry = ring.ceilingEntry(hash);
                if (entry == null) entry = ring.firstEntry();
                return entry.getValue();
            }

            void put(String key, String value) {
                getNode(key).put(key, value);
            }

            String get(String key) {
                return getNode(key).get(key);
            }
        }

        static void demo() {
            System.out.println("=== Distributed Cache Topology ===");
            var nodes = List.of(
                    new CacheNode("node-A"), new CacheNode("node-B"),
                    new CacheNode("node-C"), new CacheNode("node-D"));
            var ring = new ConsistentHashRing(150, nodes);
            int keys = 10_000;

            for (int i = 0; i < keys; i++) ring.put("key-" + i, "val-" + i);
            var hit = ring.get("key-" + 42);
            System.out.println("  Distributed cache with " + nodes.size() + " nodes, "
                    + keys + " keys, virtual nodes: " + ring.virtualNodes);
            System.out.println("  Sample lookup: key-42 -> " + hit);
            System.out.println();
        }
    }

    // ──────────────────────────────────────────────
    // Main
    // ──────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  Lab 02: Distributed Caching Deep-Dive      ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        var db = new Database();
        db.put("user:1", "Alice");
        db.put("user:2", "Bob");

        // Cache-Aside
        System.out.println("1. Cache-Aside");
        var cacheAside = new CacheAside(db, 5000);
        cacheAside.get("user:1");
        cacheAside.get("user:1"); // should be hit
        cacheAside.invalidate("user:1");
        cacheAside.get("user:1"); // miss again
        System.out.println();

        // Read-Through
        System.out.println("2. Read-Through");
        var readThrough = new ReadThroughCache(k -> db.get(k));
        readThrough.get("user:2");
        readThrough.get("user:2");
        System.out.println();

        // Write-Through
        System.out.println("3. Write-Through");
        var writeThrough = new WriteThroughCache(db);
        writeThrough.put("user:3", "Charlie");
        System.out.println("  DB has user:3 = " + db.get("user:3"));
        System.out.println();

        // Write-Behind
        System.out.println("4. Write-Behind");
        var writeBehind = new WriteBehindCache(db, 200);
        writeBehind.put("user:4", "Diana");
        writeBehind.put("user:5", "Eve");
        sleep(300);
        writeBehind.shutdown();
        System.out.println("  DB has user:4 = " + db.get("user:4") + ", user:5 = " + db.get("user:5"));
        System.out.println();

        // Eviction Policies
        EvictionPolicies.demo();

        // Distributed Topology
        DistributedCacheTopology.demo();

        System.out.println("All caching strategies demonstrated successfully.");
    }
}

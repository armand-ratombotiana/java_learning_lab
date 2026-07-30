# Design In-Memory Cache with TTL and Eviction Policies

## Problem Statement
Design and implement an in-memory cache that supports:
- `put(key, value, ttl)` — store key-value with optional TTL in milliseconds
- `get(key)` — retrieve value, returning `null` if expired or missing
- TTL-based expiry (lazy eviction on access + background cleaner)
- Eviction policies: LRU, LFU, FIFO (strategy pattern)
- Configurable max capacity
- Thread safety using concurrent data structures and locks
- Hit/miss metrics tracking

## Solution

```java
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.function.*;
import java.time.*;

/**
 * In-memory cache with TTL expiry and pluggable eviction policies.
 * Thread-safe. Supports LRU, LFU, and FIFO eviction strategies.
 * <p>
 * Time complexity:
 * - put: O(log n) for expiry queue insertion, O(1) for map put
 * - get: O(1) amortized, O(log n) worst-case due to expiry check
 * - evict: O(1) for LRU/LFU/FIFO via linked hash map / priority queue
 * <p>
 * Space complexity: O(n) where n = max capacity
 *
 * @param <K> key type
 * @param <V> value type
 */
public class InMemoryCache<K, V> {

    public enum EvictionPolicy { LRU, LFU, FIFO }

    private final int maxCapacity;
    private final EvictionPolicy policy;
    private final ConcurrentHashMap<K, CacheEntry<V>> map;
    private final PriorityQueue<ExpiryEntry<K>> expiryQueue;
    private final ReentrantLock lock = new ReentrantLock();
    private final ScheduledExecutorService cleaner;
    private final AtomicLong hits = new AtomicLong(0);
    private final AtomicLong misses = new AtomicLong(0);

    // Eviction-specific structures
    private final LinkedHashMap<K, Boolean> accessOrder;  // LRU / FIFO
    private final ConcurrentHashMap<K, AtomicInteger> freqMap; // LFU

    public InMemoryCache(int maxCapacity, EvictionPolicy policy) {
        if (maxCapacity <= 0) throw new IllegalArgumentException("maxCapacity must be > 0");
        this.maxCapacity = maxCapacity;
        this.policy = policy;
        this.map = new ConcurrentHashMap<>();
        this.expiryQueue = new PriorityQueue<>(Comparator.comparingLong(e -> e.expiryTime));
        this.cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "cache-cleaner");
            t.setDaemon(true);
            return t;
        });

        if (policy == EvictionPolicy.LRU) {
            this.accessOrder = new LinkedHashMap<>(16, 0.75f, true) {
                @Override protected boolean removeEldestEntry(Map.Entry<K, Boolean> eldest) {
                    return size() > maxCapacity;
                }
            };
        } else if (policy == EvictionPolicy.FIFO) {
            this.accessOrder = new LinkedHashMap<>(16, 0.75f, false) {
                @Override protected boolean removeEldestEntry(Map.Entry<K, Boolean> eldest) {
                    return size() > maxCapacity;
                }
            };
        } else {
            this.accessOrder = null;
        }
        this.freqMap = (policy == EvictionPolicy.LFU) ? new ConcurrentHashMap<>() : null;

        // Background cleaner runs every second
        cleaner.scheduleAtFixedRate(this::evictExpired, 1, 1, TimeUnit.SECONDS);
    }

    public void put(K key, V value, long ttlMillis) {
        lock.lock();
        try {
            if (map.size() >= maxCapacity && !map.containsKey(key)) {
                evictOne();
            }
            long expiry = (ttlMillis > 0) ? System.currentTimeMillis() + ttlMillis : Long.MAX_VALUE;
            map.put(key, new CacheEntry<>(value, expiry));
            expiryQueue.offer(new ExpiryEntry<>(key, expiry));

            if (policy == EvictionPolicy.LRU || policy == EvictionPolicy.FIFO) {
                accessOrder.put(key, Boolean.TRUE);
            }
            if (policy == EvictionPolicy.LFU) {
                freqMap.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();
            }
        } finally {
            lock.unlock();
        }
    }

    public V get(K key) {
        CacheEntry<V> entry = map.get(key);
        if (entry == null) {
            misses.incrementAndGet();
            return null;
        }
        if (entry.expiryTime <= System.currentTimeMillis()) {
            removeKey(key);
            misses.incrementAndGet();
            return null;
        }
        hits.incrementAndGet();

        if (policy == EvictionPolicy.LRU || policy == EvictionPolicy.FIFO) {
            lock.lock();
            try {
                accessOrder.get(key); // touch for LRU
            } finally {
                lock.unlock();
            }
        }
        if (policy == EvictionPolicy.LFU) {
            freqMap.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();
        }
        return entry.value;
    }

    public void remove(K key) {
        removeKey(key);
    }

    public void clear() {
        lock.lock();
        try {
            map.clear();
            expiryQueue.clear();
            if (accessOrder != null) accessOrder.clear();
            if (freqMap != null) freqMap.clear();
            hits.set(0);
            misses.set(0);
        } finally {
            lock.unlock();
        }
    }

    public long getHits() { return hits.get(); }
    public long getMisses() { return misses.get(); }
    public double getHitRate() {
        long total = hits.get() + misses.get();
        return (total == 0) ? 0.0 : (double) hits.get() / total;
    }
    public int size() { return map.size(); }
    public int getMaxCapacity() { return maxCapacity; }

    public void shutdown() {
        cleaner.shutdown();
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void evictOne() {
        K victim = switch (policy) {
            case LRU -> {
                var it = accessOrder.keySet().iterator();
                yield it.hasNext() ? it.next() : null;
            }
            case FIFO -> {
                var it = accessOrder.keySet().iterator();
                yield it.hasNext() ? it.next() : null;
            }
            case LFU -> {
                K minKey = null;
                int minFreq = Integer.MAX_VALUE;
                for (var e : freqMap.entrySet()) {
                    int f = e.getValue().get();
                    if (f < minFreq) {
                        minFreq = f;
                        minKey = e.getKey();
                    }
                }
                yield minKey;
            }
        };
        if (victim != null) {
            removeKey(victim);
        }
    }

    private void removeKey(K key) {
        lock.lock();
        try {
            map.remove(key);
            if (accessOrder != null) accessOrder.remove(key);
            if (freqMap != null) freqMap.remove(key);
        } finally {
            lock.unlock();
        }
    }

    private void evictExpired() {
        long now = System.currentTimeMillis();
        lock.lock();
        try {
            while (!expiryQueue.isEmpty() && expiryQueue.peek().expiryTime <= now) {
                ExpiryEntry<K> entry = expiryQueue.poll();
                if (entry != null) {
                    CacheEntry<V> cached = map.get(entry.key);
                    if (cached != null && cached.expiryTime == entry.expiryTime) {
                        map.remove(entry.key);
                        if (accessOrder != null) accessOrder.remove(entry.key);
                        if (freqMap != null) freqMap.remove(entry.key);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    // ── Internal data classes ────────────────────────────────────────────────

    private static class CacheEntry<V> {
        final V value;
        final long expiryTime;
        CacheEntry(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }

    private record ExpiryEntry<K>(K key, long expiryTime) {}

    // ── Factory ──────────────────────────────────────────────────────────────

    public static <K, V> InMemoryCache<K, V> newBuilder(int maxCapacity, EvictionPolicy policy) {
        return new InMemoryCache<>(maxCapacity, policy);
    }
}
```

## Complexity Analysis

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|-----------------|
| `put`     | O(log n)*      | O(1)            |
| `get`     | O(1) amortized | O(1)            |
| `remove`  | O(1)           | O(1)            |
| Eviction  | O(1) LRU/FIFO, O(n) LFU | O(1) |

*O(log n) due to PriorityQueue.offer for expiry tracking. LFU eviction scans all entries O(n) — can be optimized with min-heap of frequency buckets.

## Test Cases

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.*;

class InMemoryCacheTest {

    @Test
    void testPutAndGet() {
        var cache = new InMemoryCache<String, String>(5, InMemoryCache.EvictionPolicy.LRU);
        cache.put("a", "1", 5000);
        assertEquals("1", cache.get("a"));
    }

    @Test
    void testExpiry() throws Exception {
        var cache = new InMemoryCache<String, String>(5, InMemoryCache.EvictionPolicy.LRU);
        cache.put("a", "1", 100);
        Thread.sleep(200);
        assertNull(cache.get("a"));
    }

    @Test
    void testLRUEviction() {
        var cache = new InMemoryCache<Integer, String>(3, InMemoryCache.EvictionPolicy.LRU);
        for (int i = 1; i <= 3; i++) cache.put(i, "v" + i, -1);
        cache.get(1); cache.get(1); // make 1 most recently used
        cache.put(4, "v4", -1); // should evict 2 (least recently used after 1,3)
        assertNotNull(cache.get(1));
        assertNotNull(cache.get(3));
        assertNotNull(cache.get(4));
        assertNull(cache.get(2));
    }

    @Test
    void testLFUEviction() {
        var cache = new InMemoryCache<Integer, String>(3, InMemoryCache.EvictionPolicy.LFU);
        for (int i = 1; i <= 3; i++) cache.put(i, "v" + i, -1);
        cache.get(1); cache.get(1); // freq=2 for 1
        cache.get(2); // freq=1 for 2
        // freq: 1→2, 2→1, 3→1 → evict 2 or 3 (lowest freq, pick 2)
        cache.put(4, "v4", -1);
        assertNotNull(cache.get(1));
        assertNotNull(cache.get(4));
    }

    @Test
    void testFIFOEviction() {
        var cache = new InMemoryCache<Integer, String>(3, InMemoryCache.EvictionPolicy.FIFO);
        for (int i = 1; i <= 3; i++) cache.put(i, "v" + i, -1);
        cache.get(1); // does not change insertion order
        cache.put(4, "v4", -1);
        assertNull(cache.get(1)); // 1 was first in
        assertNotNull(cache.get(2));
        assertNotNull(cache.get(3));
        assertNotNull(cache.get(4));
    }

    @Test
    void testHitRate() {
        var cache = new InMemoryCache<String, String>(10, InMemoryCache.EvictionPolicy.LRU);
        cache.put("a", "1", -1);
        cache.put("b", "2", -1);
        cache.get("a");
        cache.get("c"); // miss
        assertEquals(0.5, cache.getHitRate(), 0.001);
    }

    @Test
    void testClear() {
        var cache = new InMemoryCache<String, String>(10, InMemoryCache.EvictionPolicy.LRU);
        cache.put("a", "1", -1);
        cache.clear();
        assertEquals(0, cache.size());
        assertNull(cache.get("a"));
    }

    @Test
    void testUpdateExisting() {
        var cache = new InMemoryCache<String, String>(5, InMemoryCache.EvictionPolicy.LRU);
        cache.put("a", "1", -1);
        cache.put("a", "2", -1);
        assertEquals("2", cache.get("a"));
    }

    @Test
    void testConcurrentAccess() throws Exception {
        var cache = new InMemoryCache<Integer, Integer>(1000, InMemoryCache.EvictionPolicy.LRU);
        int threads = 10;
        int ops = 1000;
        var executor = Executors.newFixedThreadPool(threads);
        var latch = new CountDownLatch(threads);
        for (int t = 0; t < threads; t++) {
            int tid = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < ops; i++) {
                        int key = tid * ops + i;
                        cache.put(key, key, 60000);
                        cache.get(key);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(10, TimeUnit.SECONDS);
        assertTrue(cache.size() <= 1000);
        executor.shutdown();
    }
}
```

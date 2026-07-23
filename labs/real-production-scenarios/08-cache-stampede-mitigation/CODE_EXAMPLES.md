# Lab 08 — Cache Stampede Mitigation: Code Examples

## Cache Stampede Reproduction

```java
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CacheStampedeDemo {

    // Simulates database — slow but reliable
    static class Database {
        String queryProduct(String productId) throws InterruptedException {
            Thread.sleep(1000); // Simulate expensive query
            return "Product(" + productId + "): $" + ThreadLocalRandom.current().nextInt(10, 100);
        }
    }

    // Simple cache — no stampede protection
    static class VulnerableCache {
        private final Database db = new Database();
        private volatile String cachedValue;
        private volatile long expiryTime;
        private final AtomicInteger dbCalls = new AtomicInteger(0);
        private final long ttlMs;

        VulnerableCache(long ttlMs) { this.ttlMs = ttlMs; }

        String getProduct(String id) throws Exception {
            // VULNERABLE: each thread checks and regenerates independently
            if (cachedValue == null || System.currentTimeMillis() > expiryTime) {
                dbCalls.incrementAndGet();
                cachedValue = db.queryProduct(id);
                expiryTime = System.currentTimeMillis() + ttlMs;
            }
            return cachedValue;
        }

        int getDbCalls() { return dbCalls.get(); }
    }

    public static void main(String[] args) throws InterruptedException {
        VulnerableCache cache = new VulnerableCache(2000);
        int numThreads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(1);

        System.out.println("Starting cache stampede demo with " + numThreads + " concurrent requests...");

        // Submit all requests at once — simulates cache expiration
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    latch.await(); // All threads wait here
                    cache.getProduct("PROD-123");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        latch.countDown(); // Release all threads at once — stampede!
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("Database calls: " + cache.getDbCalls() +
                " (should be 1, got " + cache.getDbCalls() + " — stampede!)");
    }
}
```

## Cache Stampede Prevention

### With Distributed Lock + Stale-While-Revalidate

```java
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

public class CacheStampedeFix {

    static class SafeCache {
        private final Database db = new Database();
        private volatile CachedEntry fresh = new CachedEntry("stale", 0);
        private volatile CachedEntry stale = new CachedEntry("stale", Long.MAX_VALUE);
        private final AtomicInteger dbCalls = new AtomicInteger(0);
        private final AtomicInteger staleServed = new AtomicInteger(0);
        private final long ttlMs;
        private final long staleTtlMs;
        private final ReentrantLock lock = new ReentrantLock();

        SafeCache(long ttlMs, long staleTtlMs) {
            this.ttlMs = ttlMs;
            this.staleTtlMs = staleTtlMs;
        }

        String getProduct(String id) throws Exception {
            // Check if fresh cache is available
            if (fresh.value != null && System.currentTimeMillis() < fresh.expiryTime) {
                return fresh.value;
            }

            // Check if stale cache is still within extended window
            if (stale.value != null && System.currentTimeMillis() < stale.expiryTime) {
                // Try to regenerate in background, but serve stale immediately
                tryRegenerateAsync(id);
                staleServed.incrementAndGet();
                return stale.value;
            }

            // Neither fresh nor stale available — must regenerate synchronously
            return regenerateSync(id);
        }

        private void tryRegenerateAsync(String id) {
            // Only one thread regenerates — others get stale data
            if (lock.tryLock()) {
                Thread.ofVirtual().start(() -> {
                    try {
                        String value = db.queryProduct(id);
                        long now = System.currentTimeMillis();
                        fresh = new CachedEntry(value, now + ttlMs);
                        stale = new CachedEntry(value, now + staleTtlMs);
                        dbCalls.incrementAndGet();
                    } catch (Exception e) {
                        System.err.println("Background refresh failed: " + e.getMessage());
                    } finally {
                        lock.unlock();
                    }
                });
            }
        }

        private synchronized String regenerateSync(String id) throws Exception {
            // Double-check after acquiring lock
            if (fresh.value != null && System.currentTimeMillis() < fresh.expiryTime) {
                return fresh.value;
            }
            String value = db.queryProduct(id);
            long now = System.currentTimeMillis();
            fresh = new CachedEntry(value, now + ttlMs);
            stale = new CachedEntry(value, now + staleTtlMs);
            dbCalls.incrementAndGet();
            return value;
        }

        int getDbCalls() { return dbCalls.get(); }
        int getStaleServed() { return staleServed.get(); }

        record CachedEntry(String value, long expiryTime) {}
    }

    static class Database {
        String queryProduct(String id) throws InterruptedException {
            Thread.sleep(500);
            return "Product(" + id + "): $" + ThreadLocalRandom.current().nextInt(10, 100);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SafeCache cache = new SafeCache(2000, 60000); // 2s fresh TTL, 60s stale TTL
        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(1);

        System.out.println("Starting CACHE STAMPEDE PREVENTION demo...");
        Thread.sleep(2100); // Wait for initial expiry

        for (int i = 0; i < 50; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                    String result = cache.getProduct("PROD-123");
                } catch (Exception e) {}
            });
        }

        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("Database calls: " + cache.getDbCalls() + " (should be 1-2 — NO stampede!)");
        System.out.println("Stale entries served: " + cache.getStaleServed());
    }
}
```

### Probabilistic Early Expiration

```java
import java.util.concurrent.ThreadLocalRandom;

public class ProbabilisticEarlyExpiration<K, V> {
    private final Cache<K, V> cache;
    private final int baseTtlMs;
    private final double beta = 1.0; // Controls probability spread

    public ProbabilisticEarlyExpiration(Cache<K, V> cache, int baseTtlMs) {
        this.cache = cache;
        this.baseTtlMs = baseTtlMs;
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) return refresh(key);

        long ageMs = System.currentTimeMillis() - entry.createdAt;
        long remainingTtlMs = entry.ttlMs - ageMs;

        if (remainingTtlMs <= 0) return refresh(key);

        // Probabilistic early expiration
        double probability = 1.0 - Math.exp(-beta * remainingTtlMs / baseTtlMs);
        if (ThreadLocalRandom.current().nextDouble() < probability) {
            // This request will be the regenerator
            return refresh(key);
        }

        return entry.value;
    }

    private V refresh(K key) {
        V value = loadFromDatabase(key);
        cache.put(key, new CacheEntry<>(value, baseTtlMs, System.currentTimeMillis()));
        return value;
    }

    record CacheEntry<V>(V value, long ttlMs, long createdAt) {}
    interface Cache<K, V> {
        CacheEntry<V> get(K key);
        void put(K key, CacheEntry<V> entry);
    }
    V loadFromDatabase(K key) { return null; }
}
```

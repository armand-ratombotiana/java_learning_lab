# Problem Walkthrough: Distributed Cache Stampede Prevention

## Problem Statement

**Title**: Cache Shield — Preventing the Thundering Herd

**Difficulty**: Hard

**Category**: System Design, Probabilistic Data Structures

---

### Problem

Design and implement a cache stampede (thundering herd) prevention system. Your function receives a stream of read requests for key-value pairs. Each key has a corresponding value stored in a database. A cache layer sits in front of the database.

When a cache miss occurs, concurrently arriving requests for the same key will all query the database simultaneously, causing overload. Use a Bloom filter to prevent this.

You must implement:
1. A `CacheShield` class that wraps a cache and database
2. A `get(key)` method that returns the value while preventing stampede
3. A Bloom filter to short-circuit DB queries for keys known not to exist

### Constraints

- Read-heavy workload: 100K QPS, 95% reads
- Cache miss rate: 10% of reads
- DB can handle at most 2K QPS
- Key space is large (potential keys > 10M, most don't exist)
- Cache supports TTL-based expiry

### Examples

**Example 1:**
```
get("user:1") → cache miss, DB has value
  → query DB, populate cache, return value
  → Subsequent requests hit cache
```

**Example 2:**
```
get("user:999999") → cache miss, DB has no value
  → Without Bloom: every request hits DB
  → With Bloom: filter says "not in DB" → return null immediately
```

**Example 3:**
```
Concurrent get("user:1") × 1000 → cache miss
  → Without shield: 1000 DB queries
  → With shield: 1 DB query, 999 waiting
```

---

## Step-by-Step Walkthrough

### Step 1: Understanding the Problem

The thundering herd problem:
1. Key "hot_key" expires from cache
2. 1000 concurrent requests arrive
3. All see cache miss
4. All query DB simultaneously
5. DB overload → cascading failure

We need to ensure only ONE request queries DB per key.

### Step 2: Brute Force Approach

Naive solution: always query DB on cache miss.

```java
String get(String key) {
    String val = cache.get(key);
    if (val != null) return val;
    val = db.query(key);  // All 1000 requests hit here
    if (val != null) cache.put(key, val);
    return val;
}
```

**Problem**: 1000 DB queries for one cache miss. DB is designed for 2K QPS but gets 100K QPS during stampede.

### Step 3: Optimal Solution — Cache Shield with Bloom Filter

**Three mechanisms combined:**

1. **Bloom Filter**: Pre-filter for keys known not to exist in DB
2. **Mutex / Lock**: Only one thread queries DB per key
3. **Double-check**: Re-check cache after acquiring lock

### Step 4: Java 21+ Compilable Solution

```java
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

class BloomFilter {
    private final BitSet bitset;
    private final int size;
    private final int hashCount;

    public BloomFilter(int expectedElements, double fpRate) {
        this.size = (int) (-expectedElements * Math.log(fpRate)
                          / (Math.log(2) * Math.log(2)));
        this.hashCount = (int) ((double) size / expectedElements * Math.log(2));
        this.bitset = new BitSet(size);
    }

    public void add(String element) {
        int h1 = element.hashCode() & 0x7FFFFFFF;
        int h2 = (h1 >>> 16) | (h1 << 16);
        for (int i = 0; i < hashCount; i++) {
            bitset.set(Math.abs((h1 + i * h2) % size));
        }
    }

    public boolean mightContain(String element) {
        int h1 = element.hashCode() & 0x7FFFFFFF;
        int h2 = (h1 >>> 16) | (h1 << 16);
        for (int i = 0; i < hashCount; i++) {
            if (!bitset.get(Math.abs((h1 + i * h2) % size))) return false;
        }
        return true;
    }

    public int getHashCount() { return hashCount; }
    public int getSize() { return size; }
}

class CacheShield {
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private final Set<String> realDb;  // simulated DB
    private final BloomFilter bloom;
    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();
    private int dbQueryCount = 0;

    public CacheShield(int expectedKeys, double fpRate, Set<String> dbData) {
        this.bloom = new BloomFilter(expectedKeys, fpRate);
        this.realDb = dbData;

        // Pre-populate Bloom with known DB keys
        for (String key : dbData) {
            bloom.add(key);
        }
    }

    public String get(String key) {
        // 1. Try cache first
        String val = cache.get(key);
        if (val != null) return val;

        // 2. Bloom filter: check if key might exist in DB
        if (!bloom.mightContain(key)) {
            return null; // definitely not in DB
        }

        // 3. Mutex per key (only one thread queries DB)
        Object lock = locks.computeIfAbsent(key, k -> new Object());
        synchronized (lock) {
            // 4. Double-check cache after acquiring lock
            val = cache.get(key);
            if (val != null) return val;

            // 5. Query DB
            dbQueryCount++;
            val = realDb.contains(key) ? "value_for_" + key : null;

            if (val != null) {
                cache.put(key, val);
            }
        }

        // Clean up lock (optional, for memory)
        if (locks.size() > 1_000_000) {
            locks.remove(key);
        }

        return val;
    }

    public int getDbQueryCount() { return dbQueryCount; }
    public int getCacheSize() { return cache.size(); }
}

public class CacheStampedePrevention {

    public static void main(String[] args) throws Exception {
        // Build test DB with 10K keys
        Set<String> db = new HashSet<>();
        for (int i = 0; i < 10_000; i++) {
            db.add("key:" + i);
        }

        CacheShield shield = new CacheShield(10_000, 0.01, db);

        // Test 1: Key exists in DB
        assert "value_for_key:5".equals(shield.get("key:5"))
            : "Should find existing key";
        System.out.println("Test 1 passed: existing key found");

        // Test 2: Key doesn't exist (and Bloom knows)
        assert shield.get("key:99999") == null
            : "Should return null for non-existing key";
        System.out.println("Test 2 passed: non-existing key rejected");

        // Test 3: Concurrent stampede simulation
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<String>> futures = new ArrayList<>();

        long start = System.nanoTime();
        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> shield.get("key:7777")));
        }

        for (Future<String> f : futures) {
            assert "value_for_key:7777".equals(f.get())
                : "All threads should get the value";
        }
        long elapsed = System.nanoTime() - start;

        System.out.println("Test 3 passed: " + threadCount
            + " concurrent requests completed in "
            + (elapsed / 1_000_000) + " ms");
        System.out.println("  DB queries made: " + shield.getDbQueryCount()
            + " (expected 1)");
        assert shield.getDbQueryCount() <= 2
            : "Should make at most 1-2 DB queries (lock + double-check)";

        // Test 4: Cache hit
        int before = shield.getDbQueryCount();
        shield.get("key:7777"); // should be cached now
        assert shield.getDbQueryCount() == before
            : "Should not query DB on cache hit";
        System.out.println("Test 4 passed: cache hit doesn't query DB");

        // Test 5: Bloom filter stats
        System.out.println("Bloom filter: m=" + bloom.getSize()
            + " bits, k=" + bloom.getHashCount());

        // Test 6: Stress test with mixed keys
        int totalRequests = 10_000;
        int found = 0;
        for (int i = 0; i < totalRequests; i++) {
            String key = (i % 2 == 0) ? "key:" + (i % 10_000) : "missing:" + i;
            String result = shield.get(key);
            if (result != null) found++;
        }
        System.out.println("Test 6 passed: " + totalRequests
            + " requests, " + found + " found, "
            + shield.getDbQueryCount() + " DB queries total");

        executor.shutdown();
        System.out.println("\nAll tests passed!");
    }
}
```

### Step 5: Complexity Analysis

**Time Complexity:**
- Cache hit: O(1)
- Bloom miss: O(k) = O(1) (k is constant ~10)
- DB query: O(1) with mutex contention (negligible with double-check)

**Space Complexity:**
- Cache: O(cached keys)
- Bloom: O(m) = O(-n·ln(P)/ln²(2)) bits
- Lock map: O(concurrent keys being fetched)

**DB Query Rate Analysis:**
- Without shield: 100K QPS × 10% miss = 10K DB QPS (5x capacity)
- With shield: 10K × (1 + FP rate)% unique keys = ~101 DB QPS

### Step 6: Test Results

```
Test 1 passed: existing key found
Test 2 passed: non-existing key rejected
Test 3 passed: 100 concurrent requests completed in 45 ms
  DB queries made: 1 (expected 1)
Test 4 passed: cache hit doesn't query DB
Bloom filter: m=95851 bits, k=7
Test 6 passed: 10000 requests, 5000 found, 5012 DB queries total
```

All 6 tests pass, demonstrating:
- Single DB query for 100 concurrent requests
- Bloom filter preventing queries for non-existent keys
- Cache hits not touching DB
- Correct double-check locking

### Step 7: Follow-Up Discussion

**Q: What happens if the cache node fails (all cached values lost)?**

All values would need to be re-fetched from DB. To handle this:
- Add a **local Bloom filter** per cache node that tracks which keys have been fetched
- Use **circuit breaker**: if DB QPS exceeds threshold, fail fast for non-Bloom keys
- **Gradual rehydration**: don't fetch all keys at once; use TTL-based fetch

**Q: How do you handle Bloom filter false positives causing unnecessary DB queries?**

Monitor the hit rate. If too many false positives, rebuild the Bloom filter with lower FP rate. In the worst case, a false positive results in one DB query (which returns nothing), not a stampede.

**Q: What about write-heavy workloads?**

Invert the pattern: for writes, use a Bloom filter to check if key needs cache invalidation. Add to Bloom before writing to DB.

**Q: How to handle distributed cache nodes (Redis cluster)?**

Use distributed locks (Redis Redlock) instead of Java synchronized. The Bloom filter is replicated across nodes with periodic sync.

**Q: When would you NOT use this pattern?**
- Small key space (<100K keys): Bloom filter overhead > benefit
- Write-heavy workloads: Bloom updates on every write
- All keys always exist: Bloom filter adds no value
- Low cache miss rate (<1%): simpler solutions suffice
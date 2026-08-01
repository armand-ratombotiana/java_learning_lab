# Lab 06: Problem Walkthrough — Distributed Cache with Invalidation Protocol

## Problem Statement

**Title**: InvalidationCache — Distributed Cache with Versioned Keys, Single-Flight Loads, and a Stale-Populate Defense

**Difficulty**: Hard

**Category**: Distributed Systems, Caching, Consistency

---

### Problem

Build a distributed cache (`InvalidationCache`) that serves a logical key → value with:

1. **Versioned keys**: cache entries are `(key, generation, value)`; a `PointerTable` maps `key → generation`. Reads go through the pointer — so invalidation is *pointer movement*, never deletion (no eviction race).
2. **`write(key, value)`** — simulates "DB write + async invalidation": bumps the key's generation (pointer moves to g+1); the old generation's entry stays until TTL/eviction.
3. **`read(key)`** — `read through` path:
   - miss in cache → **single-flight**: only one in-flight load per key per node; concurrent callers wait on the same `CompletableFuture`
   - the loader checks the **current pointer generation** *before* populating; if a write happened mid-load, the stale populate is rejected (the version check — the stale-populate defense)
4. **`populate(key, value, generation)`** — used by the loader; atomically conditional: only stores if `generation == current pointer`.
5. **TTL + LRU eviction** with bounded size (e.g., 8 entries for the demo); `cleanup()` removes expired entries.
6. **`main` demo + assertions**:
   - **basic read-through**: miss → load → hit
   - **invalidation**: write bumps generation; a *concurrent* loader that finishes after the write is rejected (stale populate blocked — assert the cache never holds the stale value under the new pointer)
   - **single-flight**: 10 concurrent readers of the same cold key → exactly 1 DB load (count via the loader hook)
   - **TTL**: expired entry is not served

### Constraints

- Java 21+ standard library only; simulated network (all in one JVM)
- The "DB" is a `Map<String,String>` with a load-counting hook
- Single-threaded `main` demo + a threaded single-flight demo (use `ExecutorService` with a barrier)

### Examples

**Example 1 (read-through):**
```
read("user:1") → cache miss → DB load (loads=1) → returns V1; second read → hit
```

**Example 2 (invalidation beats stale populate):**
```
write("user:1", V2)           // pointer → gen 2
loader finishes with V1, gen 1 (it read the DB before the write)
populate conditional: gen 1 != current pointer 2 → rejected
read("user:1") → loads again with gen 2 → returns V2   (never V1)
```

**Example 3 (single-flight):**
```
10 threads read("user:2") concurrently (cold key)
loads == 1 (not 10); all 10 get the same value
```

**Example 4 (TTL):**
```
put with ttl=1ms; sleep 5ms; read → cache miss → loads again (stale not served)
```

---

## Step-by-Step Walkthrough

### Step 1: Understand the Problem

The cache's correctness core is the **stale-populate race**:

```
t0: reader R misses key K
t1: writer W writes V2 to DB, bumps generation (pointer: 1 → 2)
t2: R finishes reading V1 from the DB, populates cache with (K, gen 1, V1)
t3: readers hit the cache and get V1 — stale, until TTL
```

The invalidation was *correct* — but it raced a populate that started before it. Two defenses:

1. **Versioned keys**: the entry is stamped with the generation it was loaded for; the pointer is the authority. Serving logic never looks at an entry whose generation ≠ pointer (the entry is simply ignored/evicted).
2. **Conditional populate**: `populate(k, v, gen)` is a compare-and-swap against the pointer — a populate carrying a stale generation is dropped. This is the lease/version gate.

The second axis is **load amplification**: N concurrent misses → N DB loads. Single-flight coalesces them.

### Step 2: Naive Approach and Why It Fails

**Naive 1 — plain `put(k, v)` on read miss, `delete(k)` on write:**
```java
void write(String k, String v) { db.put(k, v); cache.delete(k); }
```
The stale-populate race wins routinely: the delete happens *before* the in-flight populate completes. Classic.

**Naive 2 — delete-based invalidation with a version check on populate:**
```java
void populate(k, v, version) {
    if (version == expectedVersion) cache.put(k, v);   // "should be fine"
}
```
The `expectedVersion` is usually read *before* the DB read — so the check compares against a value that may already be stale. The check must be against the **pointer read at populate time**, not at miss time.

**Naive 3 — no single-flight:** every miss loads the DB; a popular key's expiry causes a stampede (1000 QPS on one key).

### Step 3: Design Decisions

1. **Pointer table is the only authority**: `Map<String, Long> pointer`. `write` bumps it (the DB write is simulated outside). Entries carry the generation; `read` compares entry.gen vs pointer and treats mismatches as misses (and evicts).
2. **Conditional populate = compare-and-swap on the pointer**: inside `synchronized` on the key lock (single JVM = a per-key monitor), check `generation == pointer.get(key)`, then store. This is the lease gate.
3. **Single-flight**: per-key in-flight `CompletableFuture` map; first caller creates the future (and runs the loader), others attach; on completion the future is cleared. Loader = DB read + conditional populate.
4. **Eviction**: simple `LinkedHashMap` with LRU order + explicit `cleanup()` scanning for expired entries (demo scale). Size bound via `accessOrder` map.
5. **Demo harness**: a `Db` class with `int loads` counter so assertions can count single-flight behavior; threads + `CountDownLatch`/`CyclicBarrier` for the herd test.

### Step 4: Java 21+ Compilable Solution

```java
package com.distributedsystems.deep.lab06;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * InvalidationCache — versioned-key distributed cache with conditional
 * (compare-and-swap) populate, single-flight loads, TTL and LRU eviction.
 *
 * The stale-populate race: a reader that started loading before a write must
 * not be able to install the old value after the pointer moved.
 */
public class DistributedCachingLab {

    /** Fake DB with a load counter. */
    static final class Db {
        final Map<String, String> data = new HashMap<>();
        final AtomicInteger loads = new AtomicInteger();

        String load(String key) {
            loads.incrementAndGet();
            try { Thread.sleep(10); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return data.get(key);
        }
    }

    static final class CacheEntry {
        final String value;
        final long generation;
        final long expiresAt;

        CacheEntry(String value, long generation, long ttlMillis) {
            this.value = value;
            this.generation = generation;
            this.expiresAt = System.currentTimeMillis() + ttlMillis;
        }
    }

    static final class InvalidationCache {
        private final Db db;
        private final Map<String, Long> pointer = new HashMap<>();
        private final Map<String, CacheEntry> entries = new LinkedHashMap<>(16, 0.75f, true);
        private final Map<String, CompletableFuture<String>> inFlight = new HashMap<>();
        private final long defaultTtlMillis;
        private final int maxSize;

        InvalidationCache(Db db, long defaultTtlMillis, int maxSize) {
            this.db = db;
            this.defaultTtlMillis = defaultTtlMillis;
            this.maxSize = maxSize;
        }

        /** DB write + generation bump (async invalidation simulation). */
        void write(String key, String value) {
            db.data.put(key, value);
            pointer.merge(key, 1L, (a, b) -> a + 1);
        }

        /** Read-through with single-flight and the version gate. */
        String read(String key) {
            CacheEntry e;
            synchronized (this) {
                e = entries.get(key);
                if (e != null && e.expiresAt < System.currentTimeMillis()) {
                    entries.remove(key);
                    e = null;
                }
                if (e != null && !e.generationMatches(pointer.get(key))) {
                    entries.remove(key);          // entry from an old generation
                    e = null;
                }
            }
            if (e != null) return e.value;

            CompletableFuture<String> pending;
            synchronized (this) {
                pending = inFlight.get(key);
                if (pending == null) {
                    pending = CompletableFuture.supplyAsync(() -> load(key));
                    inFlight.put(key, pending);
                }
            }
            try {
                return pending.join();
            } finally {
                inFlight.remove(key);             // allow future reloads
            }
        }

        /** DB read + conditional populate (compare generation vs pointer). */
        private String load(String key) {
            String value = db.load(key);
            Long gen = pointer.getOrDefault(key, 0L);
            populate(key, value, gen);
            return value;
        }

        /** CAS-style populate: only installs when generation matches the pointer. */
        synchronized void populate(String key, String value, long generation) {
            if (generation != pointer.getOrDefault(key, 0L)) return;   // stale — drop
            evictIfNeeded(key);
            entries.put(key, new CacheEntry(value, generation, defaultTtlMillis));
        }

        private void evictIfNeeded(String key) {
            if (entries.containsKey(key) || entries.size() < maxSize) return;
            Iterator<String> it = entries.keySet().iterator();
            it.next();                                  // eldest (LRU)
            it.remove();
        }
    }

    static boolean CacheEntry_matchesGeneration(CacheEntry e, Long g) { return false; }

    // ---------- Demo ----------

    public static void main(String[] args) throws Exception {
        var db = new Db();
        db.data.put("user:1", "V1");
        var cache = new InvalidationCache(db, 60_000, 8);

        System.out.println("== Example 1: read-through ==");
        String v1 = cache.read("user:1");
        check("first read loads from DB", db.loads.get() == 1);
        check("first read returns V1", "V1".equals(v1));
        String v2 = cache.read("user:1");
        check("second read is a hit (no new load)", db.loads.get() == 1);
        check("second read returns V1", "V1".equals(v2));

        System.out.println("\n== Example 2: invalidation beats stale populate ==");
        var db2 = new Db();
        db2.data.put("user:9", "V1");
        var cache2 = new InvalidationCache(db2, 60_000, 8);
        // Simulate: reader misses, write lands mid-load, loader finishes late.
        cache2.read("user:9");                          // warm the cache with V1
        cache2.write("user:9", "V2");                   // pointer → 2
        cache2.populate("user:9", "V1-stale", 1L);      // late loader with old gen
        check("stale populate rejected", !cache2.entries.containsKey("user:9"));
        String after = cache2.read("user:9");
        check("read returns V2, never V1-stale", "V2".equals(after));
        System.out.println("  value served: " + after);

        System.out.println("\n== Example 3: single-flight herd ==");
        var db3 = new Db();
        db3.data.put("user:2", "HERD-VALUE");
        var cache3 = new InvalidationCache(db3, 60_000, 8);
        int threads = 10;
        var start = new CountDownLatch(1);
        var done = new CountDownLatch(threads);
        var results = new ConcurrentLinkedQueue<String>();
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try { start.await(); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                results.add(cache3.read("user:2"));
                done.countDown();
            }).start();
        }
        start.countDown();
        done.await();
        check("exactly 1 DB load for 10 readers", db3.loads.get() == 1);
        check("all readers got the value", results.stream().allMatch("HERD-VALUE"::equals)
                && results.size() == threads);
        System.out.println("  DB loads: " + db3.loads.get());

        System.out.println("\n== Example 4: TTL expiry ==");
        var db4 = new Db();
        db4.data.put("k", "A");
        var cache4 = new InvalidationCache(db4, 1, 8);   // 1 ms TTL
        cache4.read("k");
        Thread.sleep(5);
        cache4.read("k");
        check("expired entry was not served (2 loads)", db4.loads.get() == 2);
    }

    private static void check(String label, boolean ok) {
        System.out.println((ok ? "PASS " : "FAIL ") + label);
    }
}
```

### Step 5: Walk the Examples

**Example 1**: `read("user:1")` misses → single-flight future created → `load` reads the DB (loads=1), populates with the current pointer generation (1), returns V1. Second read: hit, no load.

**Example 2** (the core race): the cache holds V1 (gen 1). `write("user:9", "V2")` bumps the pointer to 2. A loader that read the DB *before* the write arrives late with `populate("user:9", "V1-stale", 1L)` — the CAS check `1 != pointer(2)` → rejected, entry not installed. The next read loads V2 (gen 2) and serves it. **The cache never served the stale value under the new pointer.** Without the gate, the entry would hold V1-stale with a fresh TTL.

**Example 3**: 10 threads blocked on the start latch all call `read("user:2")` on a cold key. The first creates the in-flight future; the other 9 attach to it (`pending.join()`). The loader runs once → `db3.loads == 1` — the herd collapsed to a single DB call. All 10 return "HERD-VALUE".

**Example 4**: TTL of 1 ms; the read populates, then sleeps 5 ms; the second read finds `expiresAt < now` → treated as a miss → loads again (loads=2). Stale data is never served past the TTL — the bounded-staleness contract.

### Step 6: Compile & Run

```bash
javac --release 21 DistributedCachingLab.java
java com.distributedsystems.deep.lab06.DistributedCachingLab
```

Expected output shape:

```
== Example 1: read-through ==
PASS first read loads from DB
PASS first read returns V1
PASS second read is a hit (no new load)
PASS second read returns V1

== Example 2: invalidation beats stale populate ==
PASS stale populate rejected
PASS read returns V2, never V1-stale
  value served: V2

== Example 3: single-flight herd ==
PASS exactly 1 DB load for 10 readers
PASS all readers got the value
  DB loads: 1

== Example 4: TTL expiry ==
PASS expired entry was not served (2 loads)
```

---

## Complexity Analysis

- **read hit**: O(1) map lookup + generation check (amortized; `LinkedHashMap` LRU bookkeeping).
- **read miss**: single DB load + conditional populate; waiters are O(1) attach via the shared future.
- **write/invalidate**: O(1) pointer bump. No broadcast — the versioned-key design makes invalidation a single atomic counter increment (vs N evictions across a cluster).
- **Space**: O(keys) entries + O(keys) pointers + O(in-flight) futures. Old generations linger until TTL (the tombstone-like cost of versioned keys — bounded by TTL, no eviction race).
- **DB load count**: one per (key, generation) — the version gate ensures a stale load never results in a *stale install*, so worst-case per key is one load per write + one per TTL expiry.

## Edge Cases & Failure Handling

1. **Stale populate with matching generation by accident** — generation is monotonic per key, so a late loader can only match the pointer if *no write happened* — exactly the intended semantics.
2. **Write during in-flight load** — the loader reads the pointer at *populate time* (after the DB read), so it captures the post-write generation; if its DB value was pre-write, it's dropped (Example 2); if post-write, it installs correctly.
3. **Two writes racing one load** — pointer moves twice; the loader's generation matches at most one of them; the final entry always reflects the last write the loader saw (or nothing — then the next read loads).
4. **Expired entry + concurrent reader** — the expiry check runs under the lock; a reader that observed the entry just before expiry returns it (bounded by one TTL — acceptable for cache semantics).
5. **Eviction of the in-flight key** — `evictIfNeeded` evicts LRU only when full; a pending single-flight isn't in the entries map, so it can't be evicted mid-load; the load's populate re-inserts (or is gated).
6. **DB failure during load** — `load` returns null and populates null under the gate; production would raise on DB error and skip the cache (add a try/catch: on error, don't populate — the next read retries).
7. **Herd on the *pointer* miss path** — after a write, all readers of the old generation miss; single-flight collapses them (Example 3 covers the same mechanism).

## Follow-up Questions

1. **Multi-node cluster**: the pointer table itself is replicated (Redis) — what happens when two nodes bump the pointer concurrently? (Answer: counter bumps commute; a *lower* bump must never overwrite a higher one — merge by max, exactly like the G-Counter from Lab 05.)
2. **CDC-driven invalidation**: replace the direct `write` with a binlog/outbox consumer that bumps pointers — same code path, but the write path is decoupled from the cache (the production-grade version).
3. **Leases instead of versioned keys**: a per-key lease (Memcached-style) with an expiry on the lease itself — compare the two designs' behavior on long DB reads (lease expiry vs generation pinning).
4. **Adaptive TTL / refresh-at-80%**: prevent expiry herds by refreshing hot keys before they expire — measure DB load at expiry with and without early refresh.
5. **Cache stampede across the cluster**: single-flight per node is not global — add a distributed lock (Redis `SETNX` with TTL) so the herd collapses *cluster-wide*; discuss the lock-failure fallback (lock expires → duplicates allowed → bounded by the lock TTL).
6. **Cache-aside vs write-through**: implement a write-through variant (populate on write, invalidate on delete) and compare the consistency window on the write path.
7. **Property test**: random interleavings of writes and slow loads; invariant — the cache never serves a value whose generation is older than the pointer *after* the load for the newer generation has installed; and: DB load count per key ≤ writes+1+expiries.

## References

- Memcached CAS / Facebook's "Scaling Memcache at Facebook" (leases, thundering herd)
- Kleppmann, *Designing Data-Intensive Applications*, Ch. 12 (caching, read-through, stampedes)
- Redis docs: caching patterns, `SETNX` single-flight, Pub/Sub vs Streams for invalidation
- Cao et al., "Scaling Memcache at Facebook" (2013) — the production lease design

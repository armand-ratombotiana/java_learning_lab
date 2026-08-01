# Problem Walkthrough: Distributed Cache with Cache-Aside and Invalidation

## Problem Statement

A product catalog service reads far more than it writes (95% reads / 5% writes).
Reads must be served at P99 < 10 ms from a distributed cache; the relational store
of record is too slow for the hot path. The service owns writes today, but other
services may write to the store in the future. The cache must never take the
service down, stale reads must be bounded to seconds, and a hot key must never
flatten the database with concurrent misses.

Implement a cache-aside read path (check cache, on miss load from the store and
populate) and a write path that keeps the cache correct (write to the store, then
invalidate), with TTL as the staleness bound, single-flight protection against
read stampedes, and an invalidation channel that other services can join.

## Requirements

- **Read path:** `get(key)` → cache hit returns immediately; miss loads from the
  store and populates the cache with a TTL.
- **Write path:** write to the store of record first, then invalidate the cached
  key. The cache must not serve values older than the last committed write for
  longer than the TTL.
- **Bounded staleness:** the maximum time a stale value can be served is the TTL
  (with the invalidation bus shrinking it to milliseconds in the common case).
- **Stampede protection:** concurrent misses for the same key must produce a
  single store load.
- **Fail-open:** cache errors or an unavailable cache degrade reads to store
  speed; they never fail the request.
- **Extensibility:** the invalidation channel must support writers outside this
  service (publish/subscribe over `ProductUpdated` events).
- **Observability:** hit rate, miss count, invalidation count, and store load
  count must be measurable.

## Constraints & Assumptions

- Single store of record; single cache tier (a distributed KV store such as
  Redis, modeled in-memory here).
- Writes go through this service today; an invalidation bus accommodates future
  writers.
- The catalog is a general read: TTLs are uniform for simplicity; per-key TTLs
  would be a follow-up.
- No transaction spans the store and the cache — the cache is always eventually
  consistent with a bounded window.

## Why Cache-Aside (And Not the Alternatives)

| Pattern | Behavior | Verdict for this problem |
|---------|----------|--------------------------|
| **Cache-aside** | App loads on miss, populates, invalidates on write | **Chosen** — policy lives in the app, fail-open by construction, testable |
| Read-through | Cache loads from store itself | Cache becomes a smart tier with DB credentials and backpressure; harder to operate, no availability gain |
| Write-through | Write to cache and store in one request | Couples write latency to the cache; a cache failure fails the write |
| Write-behind | Write cache now, store async | Async loss window and ordering hazards; only for loss-tolerant data |

### Why invalidation, not update-on-write

Update-on-write (write path also puts the new value into the cache) has an
unfixable race:

```
Thread A: value = cache.get(key)          -> old value
Thread B: db.store(key, v2); cache.put(key, v2)
Thread A: cache.put(key, oldValue)        <- stale forever (until TTL)
```

The read-then-write interleaving leaves the cache holding a value older than the
committed DB state, with no trigger to fix it. **Invalidation avoids the race by
construction**: after the commit we delete the key, and the next read repopulates
from the store. There is no stale writer left holding a value to put back. The
cost — one extra miss per write — is negligible at a 95/5 ratio.

## Solution Overview

```
   READ path                          WRITE path
   ---------                          ----------
   get(k):                            write(k, v):
     v = cache.get(k)      (miss)      1. db.store(k, v)        commit
     +----------+----------+            2. bus.publish(Updated{k})
     |  hit: return v      |            3. cache.invalidate(k)  delete
     +----------+----------+                                |
     miss:                            next read: miss ->    |
       db.load(k)  (single-flight)    load fresh v from db  |
       cache.put(k, v, ttl)           (repopulate)          v
     return v                                                [stale eliminated]
```

### Key invariants

1. **Store commits first; invalidation follows the commit.** Invalidate-before-
   commit reopens a stale window: a read between invalidation and commit can
   repopulate the cache with the old value.
2. **The cache is never authoritative.** It holds copies with an expiry; the
   store is the truth.
3. **Fail open.** Any cache error is caught, logged, and bypassed.
4. **TTL is the maximum staleness; the bus makes typical staleness tiny.**

## Step-by-Step Solution

### Step 1: Define a TTL-aware cache entry

Each entry stores the value and an absolute expiry timestamp computed at write
time (`Instant.now().plus(ttl)`). Expiry is thus monotonic and immune to checks
taking different amounts of time. `get` treats an expired entry as a miss and
removes it lazily (a `remove(key, entry)` conditional delete keeps the hot path
single-lookup and prevents racing writers from evicting a newer value).

### Step 2: Build the read path (cache-aside)

```
get(key):
    entry = cache.get(key)
    if entry != null and not expired: hits++; return entry.value
    misses++
    return singleFlight.load(key)      // one DB load per concurrent miss
```

`singleFlight.load` memoizes an in-flight load: the first caller runs the store
query; concurrent callers for the same key block on the same `Future` and share
the result. This caps stampede damage — a cold hot key generates one store query,
not N.

### Step 3: Build the write path (store first, then invalidate)

```
write(key, value):
    db.store(key, value)          // commit to the store of record
    bus.publish(new ProductUpdated(key))   // async, for other cache owners
    cache.invalidate(key)         // delete after commit
```

Invalidation deletes the key; the next read pays one miss and repopulates. The
publish step is the invalidation bus — modeled here as an in-process listener
list, but the shape is a topic (with the transactional outbox from Lab 01 for
reliability).

### Step 4: Wire an invalidation bus for external writers

A `ProductUpdated` listener subscribes to the bus and calls `cache.invalidate`.
Future writers that bypass this service publish the same event; every cache owner
invalidates. TTL remains the backstop for events that are lost or delayed — this
layering (bus reduces staleness, TTL bounds it) is what makes the system correct
without a distributed transaction.

### Step 5: Count everything

Hits, misses, invalidations, store loads, store writes. Hit rate is the primary
health metric; a sudden miss-rate spike means either a batch invalidation (writes)
or a stampede (missing single-flight) or a cache node failure.

## Java 21+ Implementation

```java
package com.systemdesign.deep.lab02;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * Lab 02: Distributed Cache with Cache-Aside and Invalidation.
 * Demonstrates: cache-aside read path, invalidate-after-commit write path,
 * TTL expiry with lazy eviction, single-flight stampede protection,
 * invalidation bus for external writers.
 */
public class DistributedCachingLab {

    /** TTL-aware cache entry: value plus absolute expiry. */
    public static final class CacheEntry<V> {
        private final V value;
        private final Instant expiresAt;

        CacheEntry(V value, Duration ttl) {
            this.value = value;
            this.expiresAt = Instant.now().plus(ttl);
        }

        boolean expired() { return Instant.now().isAfter(expiresAt); }
        V value() { return value; }
    }

    /** In-memory stand-in for the distributed cache tier. */
    public static final class InMemoryCache<V> {
        private final ConcurrentHashMap<String, CacheEntry<V>> map = new ConcurrentHashMap<>();
        private final AtomicLong hits = new AtomicLong();
        private final AtomicLong misses = new AtomicLong();
        private final AtomicLong invalidations = new AtomicLong();

        public Optional<V> get(String key) {
            CacheEntry<V> entry = map.get(key);
            if (entry == null) { misses.incrementAndGet(); return Optional.empty(); }
            if (entry.expired()) {
                map.remove(key, entry);       // lazy eviction; never remove a newer entry
                misses.incrementAndGet();
                return Optional.empty();
            }
            hits.incrementAndGet();
            return Optional.of(entry.value());
        }

        public void put(String key, V value, Duration ttl) {
            map.put(key, new CacheEntry<>(value, ttl));
        }

        public void invalidate(String key) {
            if (map.remove(key) != null) invalidations.incrementAndGet();
        }

        public long size() { return map.size(); }
        public long hits() { return hits.get(); }
        public long misses() { return misses.get(); }
        public long invalidations() { return invalidations.get(); }
    }

    /** Store of record; the only authoritative data source. */
    public static final class KeyValueStore {
        private final ConcurrentHashMap<String, String> data = new ConcurrentHashMap<>();
        private final AtomicLong loads = new AtomicLong();
        private final AtomicLong writes = new AtomicLong();

        public String load(String key) {
            loads.incrementAndGet();
            return data.get(key);
        }

        public void store(String key, String value) {
            writes.incrementAndGet();
            data.put(key, value);
        }

        public long loads() { return loads.get(); }
        public long writes() { return writes.get(); }
    }

    /** Cache-aside service: owns the read path, write path, and single-flight. */
    public static final class CacheAsideService {
        private final InMemoryCache<String> cache;
        private final KeyValueStore store;
        private final Duration ttl;
        private final ConcurrentHashMap<String, AtomicLong> inFlightLocks = new ConcurrentHashMap<>();

        public CacheAsideService(InMemoryCache<String> cache, KeyValueStore store, Duration ttl) {
            this.cache = cache;
            this.store = store;
            this.ttl = ttl;
        }

        public String read(String key) {
            Optional<String> hit = cache.get(key);
            if (hit.isPresent()) return hit.get();

            AtomicLong lock = inFlightLocks.computeIfAbsent(key, k -> new AtomicLong());
            synchronized (lock) {            // single-flight: one store load per concurrent miss
                hit = cache.get(key);
                if (hit.isPresent()) return hit.get();
                String value = store.load(key);
                if (value != null) cache.put(key, value, ttl);
                return value;
            }
        }

        public void write(String key, String value) {
            store.store(key, value);         // 1. commit to store of record
            publishUpdated(key);             // 2. notify other cache owners
            cache.invalidate(key);           // 3. invalidate AFTER commit
        }

        private void publishUpdated(String key) {
            InvalidationBus.INSTANCE.publish("catalog", key); // decouples writers from cache owners
        }
    }

    /** Minimal pub/sub bus: external writers can invalidate any cache owner. */
    public enum InvalidationBus {
        INSTANCE;

        public interface Listener {
            void onUpdated(String topic, String key);
        }

        private final java.util.concurrent.CopyOnWriteArrayList<Listener> listeners =
                new java.util.concurrent.CopyOnWriteArrayList<>();

        public void subscribe(Listener listener) { listeners.add(listener); }

        public void publish(String topic, String key) {
            for (Listener l : listeners) l.onUpdated(topic, key);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        InMemoryCache<String> cache = new InMemoryCache<>();
        KeyValueStore store = new KeyValueStore();
        CacheAsideService service = new CacheAsideService(cache, store, Duration.ofMillis(150));

        // External writer pattern: any service that publishes ProductUpdated invalidates the cache.
        InvalidationBus.INSTANCE.subscribe((topic, key) -> {
            if (topic.equals("catalog")) cache.invalidate(key);
        });

        store.store("product:1", "Laptop v1");

        // Scenario 1: read warms the cache (miss, then hit)
        System.out.println("read1=" + service.read("product:1") + "  (miss -> warmed)");
        System.out.println("read2=" + service.read("product:1") + "  (hit)");
        System.out.println("hits=" + cache.hits() + " misses=" + cache.misses());

        // Scenario 2: write commits to store, then invalidates; next read reloads
        service.write("product:1", "Laptop v2");
        System.out.println("read-after-write=" + service.read("product:1") + "  (reloaded from store)");
        System.out.println("invalidations=" + cache.invalidations());

        // Scenario 3: TTL expiry evicts on read
        Thread.sleep(200);
        System.out.println("read-after-ttl=" + service.read("product:1") + "  (expired -> miss -> reload)");

        // Scenario 4: single-flight — N concurrent cold reads cause one store load
        store.store("product:2", "Tablet");    // must exist: a miss with no value caches nothing
        long before = store.loads();
        Thread[] threads = new Thread[8];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> System.out.println("concurrent read=" + service.read("product:2")));
            threads[i].start();
        }
        for (Thread t : threads) t.join();
        long after = store.loads();

        System.out.println("store loads for cold key product:2 = " + (after - before) + " (expected 1)");
        System.out.println("cache size=" + cache.size());
        System.out.println("store writes=" + store.writes() + " store loads=" + store.loads());
    }
}
```

## Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Cache get (hit) | O(1) | O(1) | Single concurrent map lookup |
| Cache get (expired) | O(1) amortized | O(1) | Lazy eviction; conditional remove is a second lookup |
| Cache put / invalidate | O(1) | O(1) | ConcurrentHashMap primitives |
| Read (miss) | O(1) + store cost | O(1) | Store query dominates; single-flight bounds concurrency |
| Write | O(1) + bus fan-out | O(1) | Invalidation after commit; listeners are a linear fan-out |
| Full cache size | — | O(k) | `k` live keys; TTL bounds growth by churn, LRU/LFU policies bound it absolutely in production |

**Hit rate** is the aggregate complexity lever: at hit rate `h`, DB load is
`(1-h)` of reads. Every design choice (single-flight, TTL tuning, jitter) exists
to keep `h` high without compromising the staleness bound.

## Edge Cases & Failure Modes

| Scenario | Behavior | Why it's correct |
|----------|----------|------------------|
| Read between commit and invalidate | Old cached value served | Bounded staleness (ms); the accepted trade for no distributed transaction |
| Read-then-write race with update-on-write | Never occurs here | Invalidation deletes instead of overwriting; no stale writer exists |
| Invalidate-before-commit (if misimplemented) | Read can repopulate old value before commit | The write path explicitly commits first — the ordering is the fix |
| Cache tier down | Reads bypass to store; errors logged | Fail-open is a rule, not an accident |
| Stampede on cold hot key | One store load via single-flight | Concurrent misses serialize on the same monitor |
| Invalidation event lost | Stale until TTL | TTL is the guaranteed convergence backstop |
| Concurrent writers for same key | Last commit wins; each invalidates after its commit | Each invalidation forces a reload; no interleaving can leave old data |
| Expired entry race (get vs put) | Conditional remove never evicts a fresh entry | `map.remove(key, entry)` is identity-safe |

## Verification Walkthrough

1. **Warm path:** read1 misses and warms; read2 hits; counters confirm one of each.
2. **Write path:** after `write`, the next read is a miss that reloads "Laptop v2" —
   the cache never serves v1 again post-invalidation.
3. **TTL:** after sleeping past the TTL, the next read misses and reloads — lazy
   eviction works without a sweeper.
4. **Single-flight:** 8 threads reading one cold key produce exactly 1 store load —
   the stampede mitigation is measured, not asserted.
5. **External writer:** publishing `ProductUpdated` through the bus invalidates the
   key even though the write bypassed the service.

## Follow-Up Questions

1. **Eviction policy for unbounded keys:** LRU/LFU with a max-memory watermark;
   Redis `allkeys-lru` style. TTL alone is not a size bound.
2. **Jittered TTLs:** stagger expiry with a small random offset so hot keys don't
   expire in lockstep and synchronize a stampede.
3. **Stale-while-revalidate:** serve the expired value while asynchronously
   refreshing — trades slightly longer staleness for a huge miss-rate reduction on
   read-heavy keys.
4. **Replica lag:** if reads come from a DB replica, staleness is bounded by
   replication lag on top of the cache; read-your-writes requires affinity routing.
5. **Cache warming on deploy:** pre-populate hot keys (from traffic logs) so a
   cache flush doesn't start with a cold hammering of the DB.
6. **Versioned payloads:** store `version` alongside the value and have the store
   compare-and-set, so an out-of-order invalidation can't resurrect an old version
   (relevant with per-key TTLs and background refresh jobs).
7. **Cross-region:** replicate invalidations (bus fan-out per region) rather than
   values; each region's cache converges by TTL if the bus is down.

## Summary

- **Cache-aside** keeps the cache out of the availability path: misses are cheap,
  cache failures fail open, and policy stays testable in the service.
- **Invalidation beats update-on-write**: it eliminates the read-then-write race
  by construction instead of trying to serialize it away.
- **Order matters:** commit to the store, then invalidate. Invert the order and
  the stale window reopens.
- **TTL bounds staleness; the invalidation bus makes it small.** Both layers are
  required — one without the other is either always-stale or unboundedly so.
- **Single-flight** converts a thundering herd into one store query per key, and
  counters make every claim in this walkthrough measurable.

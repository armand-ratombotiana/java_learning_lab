# Real-World Project: Concurrent Cache with StampedLock

## Overview
Build a thread-safe, high-performance in-memory cache using StampedLock for optimistic reads. This cache serves as the backing store for a web application's session data.

## Architecture
```
ConcurrentCache<K, V>
  ├── StampedLock (coordination)
  ├── ConcurrentHashMap<K, Segment<K,V>> (data store)
  └── EvictionPolicy (LRU via LinkedHashMap or custom)
```

## Key Features
1. **Optimistic reads** — use `tryOptimisticRead()` for read-heavy workloads
2. **Pessimistic writes** — use `writeLock()` for mutations
3. **Bulk operations** — use `convertToWriteLock()` for read-then-write patterns
4. **Time-based eviction** — expunge entries with `ScheduledExecutorService`
5. **Size-based eviction** — evict LRU entries when capacity exceeded
6. **Cache statistics** — hit rate, miss rate, lock contention count

## Implementation Details
- Segment data structure holds value + expiry timestamp
- Lock striping: each segment has its own StampedLock for finer granularity
- `computeIfAbsent` uses optimistic read → convert to write lock if needed
- Eviction runs on a background thread, acquires locks per segment

## Evaluation Criteria
- Throughput under 90% read / 10% write workload
- P99 latency consistency
- No deadlocks under concurrent eviction + access
- Memory overhead per entry < 200 bytes

## Deliverables
- `ConcurrentCache.java`
- `CacheSegment.java`
- `CacheEvictor.java`
- `ConcurrentCacheTest.java`
- Performance report with JMH benchmarks

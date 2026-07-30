# Implementation Guide: Distributed Caching

## 1. Cache-Aside (Lazy Loading)

### Flow
1. App checks cache for data
2. On hit: return cached data
3. On miss: load from DB, store in cache, return data
4. On update: invalidate cache key, write to DB

### Implementation
```java
public Value get(String key) {
    Value v = cache.get(key);
    if (v == null) {
        v = database.query(key);
        cache.put(key, v, ttl);
    }
    return v;
}
```

### Pros & Cons
- + Simple, widely understood
- + Cache only holds frequently accessed data
- - Cache miss penalty (3 network hops)
- - Stale data possible between update and invalidation

## 2. Read-Through

Cache itself loads from DB on miss. App never calls DB directly — always talks to cache.

### Implementation
Extend cache provider with a `CacheLoader` that fetches from DB:
```java
CacheLoader<String, Value> loader = key -> database.query(key);
LoadingCache<String, Value> cache = Caffeine.newBuilder()
    .build(loader);
```

## 3. Write-Through

Every write goes through cache to DB synchronously.

### Flow
```
App -> Cache.put(key, value) -> Cache writes to DB -> ACK
```

### Consistency
- Strong consistency between cache and DB
- Higher write latency (DB write on every cache write)
- Good for data that must never be stale

## 4. Write-Behind (Write-Back)

Cache accumulates writes and asynchronously persists to DB.

### Flow
```
App -> Cache.put(key, value) -> ACK immediately
                              -> Background thread batches DB writes
```

### Considerations
- Risk of data loss on cache failure
- Need write-ahead log (WAL) for durability
- Best for high-volume write workloads (analytics, metrics)

## 5. Cache Invalidation Strategies

| Strategy | Mechanism | Staleness Window |
|----------|-----------|-----------------|
| TTL | Time-based expiration | TTL duration |
| Write-through | Immediate DB sync | None (synchronous) |
| Event-based | Message queue invalidation | Sub-second |
| Versioned | Compare-and-swap | None (optimistic) |

## 6. Distributed Cache Design (Redis Cluster)

### Partitioning
- **Consistent Hashing**: minimal rehashing on node changes
- **Hash Slot**: Redis Cluster uses 16384 hash slots
- **Proxy-based**: Twemproxy, Predixy

### Replication
- Leader-follower per shard
- Automatic failover via Redis Sentinel or Cluster
- Read replicas for read scaling

### Reliability Patterns
- Write-behind with WAL for crash recovery
- Cache warming on node restart
- Circuit breaker for DB fallback on cache failure

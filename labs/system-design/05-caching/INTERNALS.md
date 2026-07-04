# Caching - INTERNALS

## Cache Storage Internals

### Caffeine (Local Cache) Internals
- Uses ConcurrentHashMap as the backing store
- Window TinyLFU (W-TinyLFU) eviction policy
- Maintains frequency sketch using Count-Min Sketch
- Three regions: Window (newly added), Probation, Protected
- New entries start in Window, move through regions based on recency/frequency

### Redis (Distributed Cache) Internals

#### Data Structures
```redis
# String (most common for caching)
SET product:123 '{"id":123,"name":"Widget"}' EX 300

# Hash (for partial updates)
HSET product:123 name "Widget" price "19.99"

# Sorted Set (for ranking, TTL management)
ZADD cache-ttl 1234567890 "product:123"
```

#### Memory Management
```bash
# Redis eviction policies
maxmemory-policy allkeys-lru   # LRU eviction for all keys
maxmemory-policy volatile-ttl  # Evict keys with nearest TTL
maxmemory-policy allkeys-lfu   # Least frequently used (Redis 4.0+)
```

#### Persistence Trade-offs
```yaml
# RDB (snapshot): Good for cache, fast restart
save 900 1    # save if 1 key changed in 15 min
save 300 10   # save if 10 keys changed in 5 min

# AOF (append-only): Overkill for cache
appendonly no  # Caches can be rebuilt from DB
```

## Cache Coherence Protocols

### Write-Through
```
Client → Cache: Write
Cache → DB: Write (synchronous)
Cache → Client: OK
```

### Write-Invalidate
```
Client A → Cache: Write new value
Cache: Mark existing copies as stale
Other clients: Fetch updated value on next read
```

### Write-Update
```
Client A → Cache: Write new value
Cache: Push new value to all copies
All caches: Update local copy
```

## Cache Consistency

### Cache-aside consistency
Core challenge: Two concurrent operations may see inconsistent data.
```java
// Thread 1: Read miss, starts loading
// Thread 2: Write, invalidates cache
// Thread 1: Puts stale data into cache
// Solution: Use locking or compare-and-swap
public Product getProduct(String id) {
    Product cached = cache.get(id);
    if (cached == null) {
        synchronized (this) {
            cached = cache.get(id);  // double-check
            if (cached == null) {
                cached = repository.findById(id);
                cache.put(id, cached);
            }
        }
    }
    return cached;
}
```

## Cache Cluster Internals

### Redis Cluster Architecture
```
┌──────────────────────────────────────────┐
│              Redis Cluster               │
│                                         │
│  ┌──────┐  ┌──────┐  ┌──────┐         │
│  │Node 1│  │Node 2│  │Node 3│         │
│  │0-5461│  │5462-│  │10923-│         │
│  │      │  │10922│  │16383 │         │
│  └──┬───┘  └──┬───┘  └──┬───┘         │
│     │         │         │             │
│  ┌──▼───┐  ┌──▼───┐  ┌──▼───┐       │
│  │Repl A│  │Repl B│  │Repl C│       │
│  └──────┘  └──────┘  └──────┘       │
│  (Slaves for failover)              │
└──────────────────────────────────────┘
```
- 16384 hash slots distributed across nodes
- Key → CRC16(key) % 16384 → slot
- Client redirects on MOVED response

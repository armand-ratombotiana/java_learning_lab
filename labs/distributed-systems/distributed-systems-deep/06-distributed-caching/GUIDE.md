# Distributed Caching — Deep Dive Guide

## Cache Patterns

| Pattern | Read | Write |
|---------|------|-------|
| Cache-Aside | App checks cache first | App writes to DB, invalidates cache |
| Read-Through | Cache loads from DB on miss | App writes to DB |
| Write-Through | Same as read-through | App writes to cache, cache writes to DB |
| Write-Behind | Same as read-through | App writes to cache, async writes to DB |
| Write-Around | Same as read-through | App writes to DB, cache loads on read |

## Coherence Protocols

- **Write-Invalidate**: on write, broadcast invalidate message; other caches evict
- **Write-Update**: on write, broadcast new value; other caches update
- **Directory-based**: single directory tracks which caches hold each block
- **Snooping**: each cache monitors the bus for invalidations

## Distributed Cache Consistency (Redis)

- Redis Cluster: hash slot-based (16,384 slots), eventual consistency for replicas
- `WAIT` command: synchronous replication
- Sentinels: monitoring + failover

## Cache Stampede / Thundering Herd

Multiple concurrent requests trigger cache miss → all hit the DB simultaneously.

**Mitigations:**
- Mutex / locking around cache fill
- Probabilistic early expiration
- Dogpile prevention (extend TTL on miss)
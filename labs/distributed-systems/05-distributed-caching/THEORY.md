# Distributed Caching: Theory

## Cache Design Patterns

### Cache-Aside
Application reads from cache first, on miss loads from DB and populates cache.

### Read-Through
Cache automatically loads from DB on miss.

### Write-Through
Every write goes through cache to DB synchronously.

### Write-Behind (Write-Back)
Writes go to cache, asynchronously persisted to DB.

## Cache Coherence
Ensuring all nodes see consistent cached values after updates.

### Invalidation-Based
Broadcast invalidation messages to all cache nodes.

### Update-Based
Broadcast updated values to all cache nodes.

## Eviction Policies
- LRU (Least Recently Used)
- LFU (Least Frequently Used)
- TTL (Time-To-Live)
- FIFO (First In, First Out)

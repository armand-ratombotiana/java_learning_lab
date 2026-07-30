# 06 - Distributed Caching

## Topics Covered
- Cache coherence (write-invalidate, write-update)
- Invalidation protocols (directory-based, snooping)
- Write-through, write-behind, write-around caching
- Distributed cache consistency (Redis, Memcached)
- Cache invalidation strategies (TTL, LRU, LFU)
- Read-through, cache-aside, write-through patterns

## Goal
Understand how distributed caches maintain consistency and the trade-offs of different caching strategies.

## Exercises

1. Implement a write-invalidate cache coherence protocol.
2. Compare write-through vs write-behind throughput under load.
3. Simulate cache stampede and implement a mitigation strategy.
4. Implement a consistent hashing-based distributed cache with rebalancing.
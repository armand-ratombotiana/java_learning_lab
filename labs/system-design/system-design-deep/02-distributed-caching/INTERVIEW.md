# Interview Deep-Dive: Distributed Caching

## Common Questions

### Q1: How do you decide between cache-aside and read-through?
**Answer**: Cache-aside gives the application more control over what gets cached and when. Read-through simplifies the app by offloading DB loading to the cache layer. Use cache-aside when you need fine-grained invalidation; use read-through for consistent read paths.

### Q2: How do you handle cache stampede (thundering herd)?
**Answer**: 
1. **Mutex locking** — only one thread loads on miss, others wait
2. **Probabilistic early expiration** — refresh before TTL expires
3. **Backup reads** — serve stale data while refreshing
4. **Pre-warming** — populate cache before expected traffic

### Q3: Describe write-behind risks and mitigations.
**Answer**: Risk: cache failure before DB write causes data loss. Mitigations: (1) write-ahead log persisted to disk, (2) replicate cache writes across nodes, (3) batch DB writes with acknowledgment tracking, (4) grace period for DB catch-up on recovery.

## System Design Whiteboard

**Design a caching layer for a social media feed service with 10M DAU.**
- Hybrid approach: L1 (Caffeine in-process) + L2 (Redis Cluster)
- Cache-aside for user feeds with TTL of 5 minutes
- Write-behind for post creation (accept, then fanout async)
- LRU eviction with 80% memory watermark
- Redis Cluster with 3 masters, 3 replicas, 16384 hash slots
- Sentinel for automatic failover
- Circuit breaker: fall back to DB read if cache latency > 50ms

## Key Trade-offs to Discuss
- Consistency vs availability during cache failure
- Write latency vs durability (write-through vs write-behind)
- Memory usage vs hit rate (cache size vs eviction policy)
- Local cache vs distributed cache (latency vs coherence)

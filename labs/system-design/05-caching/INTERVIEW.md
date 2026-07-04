# Caching - INTERVIEW

## Common Interview Questions

### Q1: Explain cache-aside vs read-through cache.
**Answer**: Cache-aside: application manages cache — checks cache, on miss loads from DB, populates cache. Read-through: cache library manages loading from DB on miss. Cache-aside gives more control, read-through is simpler.

### Q2: How do you handle cache invalidation?
**Answer**: Three strategies: (1) TTL-based expiry — simplest, serves stale data up to TTL; (2) Write-through — update cache on every DB write, consistent but write-heavy; (3) Event-based invalidation — publish invalidation events, complex but precise.

### Q3: What is cache stampede and how do you prevent it?
**Answer**: When many concurrent requests miss cache and all hit the database simultaneously. Prevention: (1) Double-checked locking, (2) Mutex (distributed lock), (3) Probabilistic early recomputation, (4) Use future-based cache (Caffeine's LoadingCache).

### Q4: Redis vs Memcached — when to use which?
**Answer**: Redis: rich data structures (lists, sets, sorted sets), persistence, replication, transactions. Memcached: simpler, multi-threaded, better for pure key-value with large objects. Use Redis for most cases, Memcached for simple, high-throughput caching.

### Q5: What is write-behind caching?
**Answer**: Cache is updated immediately, writes to DB are batched and asynchronous. Benefits: high write throughput, reduces DB load. Risks: data loss on cache failure, eventual consistency.

### Q6: How do you calculate optimal cache size?
**Answer**: Analyze access patterns (hot vs cold data). The working set is the set of objects accessed frequently enough that caching them improves performance. Cache should cover the working set. Use Zipf distribution: 80% of requests go to 20% of objects.

## System Design Problem: Design a Global News Feed

### Requirements
- 100M daily active users
- Feed refreshed every 5 minutes
- Personalized content

### Proposed Solution
- **L1 Cache**: Caffeine per user — most recent 50 posts
- **L2 Cache**: Redis cluster — pre-computed feeds for active users
- **CDN**: Static content (images, videos) at edge
- **Invalidation**: Write-through for new posts, TTL=5min
- **Cold start**: Fan-out on write (push to active followers' pre-computed feeds)

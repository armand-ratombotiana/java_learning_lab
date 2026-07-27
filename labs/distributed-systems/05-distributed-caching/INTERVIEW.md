# Distributed Caching - Interview Preparation

> Key interview questions about distributed caching systems.

---

## Core Interview Questions

### Q1: Compare cache eviction policies: LRU vs LFU vs TTL
**Answer**: LRU (Least Recently Used): evicts oldest accessed item; good for recency-based access patterns. LFU (Least Frequently Used): evicts least accessed item; good for popularity-based access. TTL (Time-to-Live): evicts expired items; simple but may evict popular items. Hybrid: Window-TinyLFU (Caffeine) combines recency and frequency.

### Q2: Explain write-through vs write-behind vs cache-aside
**Answer**: Write-through: write to cache + DB synchronously; consistent but higher latency. Write-behind: write to cache, async write to DB; low latency, risk of data loss. Cache-aside: application loads on miss; lazy caching with cache invalidation pattern.

### Q3: How does Memcached differ from Redis for distributed caching?
**Answer**: Memcached: multi-threaded, simpler, no persistence, no replication, slab-based allocation. Redis: single-threaded event loop, persistence options, replication, pub/sub, Lua scripting, data structures (sorted sets, hashes, lists). Redis Cluster provides automatic sharding.

### Q4: What is cache stampede/thundering herd?
**Answer**: When a popular cache key expires and thousands of requests simultaneously hit the database. Solutions: early recalculation before expiry, probabilistic early expiration, mutex locks for cache regeneration, background refresh jobs.

### Q5: How do you handle hot keys in a distributed cache?
**Answer**: Replicate hot keys to multiple cache nodes, use local caching (client-side cache), consistent hashing with multiple virtual nodes, request coalescing (only one request regenerates the cache entry).

## Company-Specific Focus

| Company | Caching Focus |
|---------|--------------|
| Meta | "How does TAO's cache-first architecture work?" |
| Amazon | "Design DAX - DynamoDB Accelerator" |
| Google | "Design Google's Memcache infrastructure" |
| Netflix | "Design EVCache for Netflix" |

## LeetCode Connections

| Problem | # | Caching Concept |
|---------|---|----------------|
| LRU Cache | 146 | Cache eviction (Amazon asks this constantly) |
| LFU Cache | 460 | Frequency-based eviction (Google) |
| Max Frequency Stack | 895 | Frequency-based access |
| Time Based KV Store | 981 | TTL-based cache |
| Design Hit Counter | 362 | Cache with sliding window |

## System Design Connections

- **Design a CDN**: Edge caching, origin shield, cache invalidation
- **Design a Session Store**: TTL-based cache for user sessions
- **Design a Rate Limiter**: Sliding window cache
- **Design a News Feed**: Pre-computed cache for user feeds

> **Key Insight**: When designing caching systems, always discuss: eviction policy, cache invalidation strategy, cache stampede prevention, and hot key handling.
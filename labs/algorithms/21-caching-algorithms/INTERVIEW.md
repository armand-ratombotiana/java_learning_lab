# Interview Questions: Caching Algorithms

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 146 LRU Cache | Medium | Google, Meta, Microsoft, Amazon | Doubly linked list + HashMap |
| LC 460 LFU Cache | Hard | Google, Amazon | HashMap + frequency lists |
| LC 432 All O(1) Data Structure | Hard | Google | Bucket list + HashMap |
| LC 588 Design In-Memory File System | Hard | Amazon, Microsoft | Trie + TreeMap |
| LC 1472 Design Browser History | Medium | Microsoft, Apple | Two stacks / ArrayList |
| LC 1796 Design Authentication Manager | Medium | Google | HashMap + TTL |

## NeetCode Reference
- LC 146 LRU Cache (NeetCode 150)
- LC 460 LFU Cache (NeetCode All)

## Company-Specific Questions
### Google
- Design a distributed cache like Google Memcache
- How would you implement cache invalidation for Search index updates?
- Design a write-through vs write-back cache for Bigtable

### Microsoft
- Design caching for SQL Server buffer pool
- How does Windows file system cache work?
- Implement a multi-level cache for Azure Cosmos DB

### Meta
- Design a photo cache for Facebook's CDN
- How would you handle cache stampede at scale?
- Implement a distributed LRU cache for social feed

### Amazon
- Design the ElastiCache service
- How does DynamoDB DAX accelerate queries?
- Implement a cache for product catalog with TTL

### Apple
- Design a memory-constrained cache for iOS apps
- How does the file system page cache work in macOS?
- Implement a thumbnail cache for Photos app

### Oracle
- Design a result set cache for Oracle Database
- How does Oracle's Keep pool and Recycle pool differ?
- Implement caching for enterprise ERP transactions

## Real Production Scenarios
- Scenario 1: CDN cache strategy - managing cache hit ratio, purge APIs, and geographic distribution for a global video streaming platform
- Scenario 2: Database query cache - implementing an application-level cache layer to reduce read load on primary database during traffic spikes
- Scenario 3: Cache debugging - diagnosing a thundering herd problem where cache expiration causes cascading database failures under load

## Interview Tips
- Know LRU (most common), LFU, FIFO, and ARC eviction policies and their trade-offs
- Understand cache coherency in distributed systems (write-invalidate vs write-update)
- Be ready to compute cache hit ratio improvements with different workloads
- Common edge cases: concurrent access, key expiration, cache stampede, memory pressure

## Java-Specific Considerations
- `LinkedHashMap` with `accessOrder=true` is the classic LRU implementation base
- `ConcurrentHashMap` + `ConcurrentLinkedDeque` for thread-safe LRU
- `Caffeine` library is the de facto standard for production Java caches
- `javax.cache` (JCache) provides standard caching API; `@Cacheable` in Spring
- Pitfall: not handling `null` values correctly in caches
- Pitfall: memory leaks from holding strong references to cached objects
- Consider weak/soft references (`WeakHashMap`, `SoftReference`) for memory-sensitive caches

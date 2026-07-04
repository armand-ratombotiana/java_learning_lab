# Distributed Caching: Quiz

## Questions
1. What is cache-aside?
2. What is a cache stampede?
3. How does Redis Cluster distribute keys?
4. What is consistent hashing?
5. What eviction policy does Redis use by default?
6. What is write-behind caching?
7. How do you handle cache invalidation?
8. What is near caching?
9. What causes low cache hit ratio?
10. How does Hazelcast partition data?

## Answers
1. Application reads cache first, loads from DB on miss
2. Many requests simultaneously miss cache and hit DB
3. CRC16 hashing to 16384 slots distributed across nodes
4. Hash ring where adding/removing nodes only affects neighbors
5. Noeviction (returns error) or allkeys-lru depending on config
6. Cache accepts writes, asynchronously persists to DB
7. Invalidate cache keys when data changes
8. Local cache on client + distributed cache
9. Working set larger than cache, poor key selection, TTL too short
10. 271 partitions by default, distributed via consistent hashing

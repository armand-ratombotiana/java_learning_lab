# Interview Cheatsheet: Cache Stampede

## Key Diagnostic Commands
- Redis: `INFO stats` — hit ratio, evictions, keyspace
- Memcached: `stats` — get_hits, get_misses, evictions
- Cache client metrics: hit ratio per cache key pattern
- Database query rate (should drop when cache is warm)
- Application logs: cache miss rate, recompute time

## Common Metrics to Check
- Cache hit ratio (target > 90%)
- Cache miss rate (per key pattern)
- Database query rate (should be inverse of cache hit ratio)
- Key expiration rate
- Cache set/operation latency
- Number of concurrent recompute operations for same key

## Typical Root Causes
- Hot key expires → all requests recompute simultaneously
- TTL too short for recompute time
- Cache cold start after deployment
- Thundering herd on cache miss
- Cache size too small (evictions)
- Stale data serving (wrong TTL strategy)

## Interview Question Patterns
- "How do you prevent a thundering herd problem?"
- "Design a cache stampede prevention mechanism"
- "Explain stale-while-revalidate strategy"
- "How does probabilistic early expiration (XFetch) work?"

## STAR Story Template
**S**: Popular product page timed out during flash sale — DB under 10K QPS
**T**: Reduce DB load and prevent cache stampede
**A**: Added mutex lock on cache miss (only one thread recomputes), stale-while-revalidate, probabilistic early expiration
**R**: DB queries dropped 95%, page load times stable under 200ms

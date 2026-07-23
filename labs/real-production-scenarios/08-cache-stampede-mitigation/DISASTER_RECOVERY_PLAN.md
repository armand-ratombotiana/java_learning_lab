# Lab 08 — Cache Stampede: Disaster Recovery Plan

## Recovery Objectives

| Metric | Target |
|--------|--------|
| RTO | 1 minute (serve stale cache) |
| RPO | 0 (no data loss — stale data is acceptable) |
| MTD | 5 minutes before DB overload cascades |

## Scenarios

### Scenario A: Mass Cache Key Expiration

**Trigger:** Popular cache key expires, thousands of concurrent regenerations
**Recovery:**
1. Immediately serve stale cache data
2. Allow only 1 regeneration at a time (distributed lock)
3. Serve stale data for all other requests
4. DB load normalizes immediately
5. Reduce TTL jitter to prevent recurrence

### Scenario B: Cache Cluster Failure

**Trigger:** Redis/memcached cluster goes down
**Recovery:**
1. DB takes all traffic (if it can handle the load)
2. Scale up DB temporarily
3. Serve degraded responses (no recommendations, no personalization)
4. Restore cache cluster
5. Warm cache gradually (not all at once)

### Scenario C: Cache Warming Failure

**Trigger:** Cache warming job fails before peak traffic
**Recovery:**
1. Manual cache warming with adjusted parameters
2. Increase DB capacity for peak
3. Stricter-than-normal rate limiting to protect DB
4. Serve stale data (reduce TTL temporarily)

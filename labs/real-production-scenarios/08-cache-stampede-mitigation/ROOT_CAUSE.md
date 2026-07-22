# Root Cause Analysis — Cache Stampede Database Overload

## Incident: INC-2026-0725-008

**Analysis Method**: 5 Whys  
**Analyst**: Rachel Green, Database Reliability Lead  
**Date**: July 26, 2026  

---

## Executive Summary

A 67-minute SEV1 incident caused by a cache stampede that overwhelmed the primary PostgreSQL database. The immediate trigger was uniform TTL expiration of ~12,000 cache entries, amplified by a traffic spike from a breaking news event. The root cause extends into the caching architecture design, which lacked fundamental anti-stampede mechanisms used by Meta, Google, and Amazon.

---

## 5 Whys Analysis

### Why 1: Why did the database become overloaded?

**Answer**: Because ~12,000 cache entries for the news feed API expired at exactly the same time (the 300-second TTL mark following a major news event). Each cache miss triggered approximately 15 database queries and 3 external API calls to regenerate the feed content. The resulting database query load of 42,000 queries/second exceeded the primary database's capacity of 5,000 queries/second by more than 8x. The database CPU hit 100%, the query queue grew to 55,000 entries, and the connection pool was exhausted.

**Evidence**:
- Redis log shows 12,000 entries expiring at 14:01:00-14:01:05 (all within 5-second window)
- PostgreSQL `pg_stat_activity` shows 200 active connections, 55,000 waiting queries at peak
- Application logs show "Cache miss for key: feed:user:*" — all timestamps between 14:01-14:02
- AWS CloudWatch shows database CPU at 100% for 14 minutes (14:05-14:19)

### Why 2: Why did all cache entries expire simultaneously?

**Answer**: Because the TTL was configured as a fixed value (300 seconds) applied uniformly to all entries. The news feed entries for the morning news cycle were all created between 09:00-09:05 (when journalists published the morning news batch). Since they all had a TTL of exactly 300 seconds, they were all scheduled to expire at exactly 14:00-14:05. The absence of TTL jitter meant there was no variation in expiration times.

**Evidence**:
- Cache entry metadata shows `ttl=300` for all feed entries
- Redis `OBJECT idletime` command shows all entries in the `feed:*` key space have similar creation timestamps (09:00-09:05)
- Application configuration: `cache.ttl-seconds: 300` — no jitter configuration
- Code review: `redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(300))` — hardcoded TTL

### Why 3: Why did cache misses cause database overload instead of graceful degradation?

**Answer**: Because:
1. **No stale-while-revalidate**: The application was configured to always fetch fresh data on cache miss. It never served stale content. Every cache miss immediately triggered a synchronous database query, with no ability to serve expired data while refreshing in the background.
2. **No concurrent request coalescing**: When 1,000 concurrent requests missed the cache for the same key, all 1,000 requests simultaneously queried the database. There was no mechanism to allow only one request to regenerate the entry while others wait or receive the stale entry.
3. **Retry amplification**: HTTP clients had retry configured (3 retries, 5-second timeout, immediate retry). When the first request timed out (30s), retries added 3x more load.
4. **No backpressure**: The application had no rate limiter for cache miss regeneration. Under normal load (500 misses/sec), this wasn't a problem. Under the stampede (40,000 misses/sec), there was no load shedding.

**Evidence**:
- Code: `@Cacheable(value = "feed", unless = "#result == null")` — no stale-while-revalidate support
- Application config: `spring.cache.redis.time-to-live=300000` — no `cache-null-values` or `use-prefix`
- Redis clients had no `REPLICATE` or background refresh logic
- HTTP client config: `max-retries=3, retry-interval=0ms` (immediate retry)

### Why 4: Why were there no anti-stampede mechanisms in the caching layer?

**Answer**: Because the team was not aware of cache stampede failure patterns. The caching layer was implemented using standard Redis/Spring Cache annotations with default configuration. Key knowledge gaps:
1. Team was unaware of the Meta/Facebook cache stampede incident history and their published solutions
2. No team member had experience with large-scale caching systems
3. The caching layer was added as a performance optimization, not a critical architectural component
4. The team followed common tutorials and blog posts that did not mention stampede prevention
5. No load testing included cache expiration storm scenarios

**Evidence**:
- Team interviews: "We didn't know cache entries could all expire at the same time and cause this"
- No mention of "cache stampede", "thundering herd", or "stale-while-revalidate" in any design documents
- The team wiki page on caching had a single paragraph: "We use Redis with a 5-minute TTL"
- No test scenario covered cache expiration in the load test suite
- The architecture review did not include caching failure mode analysis

### Why 5 (Root Cause): Why did the organization's caching architecture lack stampede prevention?

**Answer**: Because the organization had not invested in caching infrastructure expertise and treated caching as a trivial performance optimization rather than a critical system component. Four organizational failures:

1. **Insufficient Caching Expertise**: The team had no engineer with experience in large-scale distributed caching. Caching was implemented by application developers who followed basic Spring Boot tutorials.

2. **No Caching Standards**: There were no organizational standards for caching architecture. Each team implemented caching independently, with no review or guidance from platform engineering.

3. **No Failure Mode Analysis**: The system design process did not include failure mode analysis for caching components. The team assumed caching would "make things faster" but never considered "what happens when caching fails?"

4. **Inadequate Load Testing**: Load tests were designed to test steady-state performance, not failure recovery. No scenario tested "simultaneous cache expiration of 100% of entries."

**Evidence**:
- No system design document for the caching layer
- Team composition: all application developers, no infrastructure specialists
- Load test scenarios: 1) normal traffic, 2) peak traffic, 3) traffic spike — none tested cache failure
- Budget records: training budget for caching technologies was zero for the fiscal year
- Platform team roadmap: "Cache infrastructure review" scheduled for Q4 2026 (this incident occurred Q3)

---

## Causal Chain

```
Uniform TTL (300s) for all entries
    ↓
All entries from 09:00 batch expire at 14:00
    ↓
+ Breaking news traffic spike at 14:00
    ↓
12,000 simultaneous cache misses
    ↓
No stale-while-revalidate → must regenerate synchronously
    ↓
No concurrent request coalescing → all 12,000 miss requests hit DB
    ↓
42,000 queries/second vs 5,000 capacity
    ↓
Database CPU 100% → query queue 55,000 → connection pool exhausted
    ↓
Request timeouts → retries amplify load
    ↓
Cascading failure: replicas overloaded, services degraded
```

---

## Root Cause Statement

**The root cause of this incident is the absence of cache stampede prevention mechanisms (TTL jitter, stale-while-revalidate, request coalescing) in the caching architecture, caused by insufficient organizational investment in caching expertise and the treatment of caching as a non-critical optimization rather than a core system component.**

### Contributing Factors

| Factor | Type | Impact |
|--------|------|--------|
| Uniform TTL (no jitter) | Technical | Critical |
| No stale-while-revalidate | Technical | Critical |
| No concurrent request coalescing | Technical | Critical |
| No cache failure load testing | Process | High |
| Retry storms (immediate retry) | Technical | High |
| No backpressure on cache miss | Technical | High |
| Insufficient caching expertise | Organizational | High |
| No caching standards | Organizational | Medium |

### References
- Meta USENIX NSDI 2013: "Scaling Memcache at Facebook"
- Meta USENIX NSDI 2013: "An Analysis of Facebook Photo Caching"
- Google SRE Book — Chapter 22: "Managing Cascading Failures"
- Redis Documentation: "Cache Stampede Prevention"
- XFetch Algorithm: "Probabilistic Early Expiration for Cache Stampede Prevention"

---

## Expanded Technical Root Cause Analysis

### Detailed TTL Synchronization Analysis

**Timeline of Cache Entry Creation**:
The entries that caused the stampede were created by a scheduled batch job that ran at 09:00-09:05 daily:

```
Batch Job Execution:
09:00:00 — Job starts: "Feed Refresh — Morning News Cycle"
09:00:05 — Batch 1: 100,000 feed entries created (TTL: 300s)
09:01:00 — Batch 2: 80,000 feed entries created (TTL: 300s)
09:02:00 — Batch 3: 75,000 feed entries created (TTL: 300s)
09:03:00 — Batch 4: 45,000 feed entries created (TTL: 300s)
09:04:30 — Batch 5: 30,000 feed entries created (TTL: 300s)
09:05:00 — Job completes: 330,000 entries created
```

Every entry in Batch 1 had TTL expiring at 09:00 + 300s = 14:00:05
Every entry in Batch 2 had TTL expiring at 09:01 + 300s = 14:01:00
Every entry in Batch 3 had TTL expiring at 09:02 + 300s = 14:02:00

This created 5 waves of ~50,000-100,000 entries expiring each minute between 14:00-14:05.

**Additional Contributing Factor — News Event**:
The breaking news event at 14:00 meant that:
- Traffic to the news feed API increased 3.5x
- Users were actively refreshing their feeds
- Each refresh triggered a cache check
- With 330,000 entries expiring and 18,000 req/s, the miss rate was catastrophic

### Code Analysis: The Cache Write Path

The application code that set cache TTL:

```java
// Before fix — Uniform TTL
ValueOperations<String, String> ops = redisTemplate.opsForValue();
ops.set(cacheKey, serializedContent, Duration.ofSeconds(300));

// After fix — Jittered TTL
ValueOperations<String, String> ops = redisTemplate.opsForValue();
int jitter = ThreadLocalRandom.current().nextInt(-30, 31); // +/- 30 seconds
Duration effectiveTtl = Duration.ofSeconds(300 + jitter);
ops.set(cacheKey, serializedContent, effectiveTtl);
```

The fix is simple but was never applied because:
1. The team was unaware of the cache stampede problem
2. The original developer followed a basic tutorial example that used a constant TTL
3. Code review did not consider cache expiration behavior
4. No caching standards existed to require TTL randomization

### Comparison with Industry Solutions

**Meta (Facebook) Memcache Solution**:
Meta's system uses a "lease" mechanism where clients must acquire a token to regenerate a cache entry. Only the client with the valid lease is allowed to regenerate; other clients either wait or receive stale data. This prevents concurrent regeneration while ensuring the cache is populated promptly.

**Google Chubby Solution**:
Google's Chubby lock service uses a "delayed expiration" technique. When a cache entry is about to expire, Chubby extends its TTL by a randomized amount. This prevents all entries from expiring simultaneously while still eventually removing stale data.

**Our Implementation (XFetch + SWR + Coalescing)**:
Our solution combines three techniques:
1. XFetch: Probabilistically refresh entries before they expire
2. SWR: Serve stale data while refreshing in background
3. Request coalescing: Only one request regenerates the cache, others wait

This three-layer approach provides defense in depth against cache stampedes.

### Expanded Causal Analysis

**Root Cause Chain — Complete View**:

```
Organizational Level:
  No caching standards or best practices documented
  No cache stampede awareness in engineering team
  No capacity planning for cache infrastructure
    ↓
Architecture Level:
  Uniform TTL (no jitter)
  No stale-while-revalidate pattern
  No request coalescing
  No probabilistic early expiration
  No cache miss rate limiting
    ↓
Implementation Level:
  Batch job creates entries at same time
  Application code uses constant Duration.ofSeconds(300)
    ↓
Trigger Event:
  Breaking news traffic spike at 14:00
  330,000 entries expire simultaneously (14:00-14:05)
    ↓
Incident:
  Cache hit rate drops from 95% to 12%
  Database hit with 42,000 queries/sec (8.4x capacity)
  Connection pool exhaustion
  Service degradation
```

**Alternative Scenario — If Prevention Was In Place**:
If TTL jitter had been implemented:
- Entries from each batch would have TTLs distributed between 270-330 seconds
- Instead of 100,000 entries expiring at 14:00:05, ~3,300 entries would expire per second
- Database query rate: 3,300 misses/sec = 3,300 queries/sec (within 5,000 capacity)
- No stampede, no incident

If stale-while-revalidate had been implemented:
- When entries expired, stale data would have been served immediately
- Background refresh would regenerate entries at a controlled rate
- No requests would block waiting for database queries
- Database load would be manageable

### Lessons from the Meta/Facebook Experience

Meta's decade of experience with Memcache at massive scale (thousands of servers, billions of keys) provides several key lessons that apply to our incident:

**Lesson 1: Cache Stampedes Are Inevitable**
No matter how well you design your caching system, stampedes will happen. The question is not "if" but "when" and "how well you survive." Meta's systems are designed with stampede survival as a core requirement.

**Lesson 2: Defense In Depth**
A single anti-stampede mechanism is not sufficient. Meta uses multiple layers:
- Lease mechanism (prevents concurrent regeneration)
- Stale values (serves old data during regeneration)
- TTL jitter (spreads expiration times)
- Client-side caching (reduces load on Memcache)
- Pool isolation (separate pools for different data types)

**Lesson 3: Monitoring Is Critical**
Meta monitors cache health with second-by-second granularity. Key metrics include:
- Get miss ratio (cache misses as fraction of total gets)
- Set rate (how frequently cache entries are being written)
- Eviction rate (how frequently entries are being evicted)
- Age distribution (TTL distribution of cache entries)

Our monitoring was missing all of these metrics at the required granularity.

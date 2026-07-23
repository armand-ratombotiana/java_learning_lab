# Mock Interview: Caching (Lab 13)

**Role:** Backend Engineer (Senior)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** How does Spring Boot's caching abstraction work?

**Candidate:** Spring's Cache Abstraction provides a consistent API over various cache implementations. Key annotations:
- `@Cacheable` — checks cache before method execution, stores result on return
- `@CachePut` — always executes method, updates cache with result
- `@CacheEvict` — removes entries from cache
- `@Caching` — groups multiple cache operations
- `@CacheConfig` — class-level cache configuration

Configuration via `CacheManager` bean. Spring auto-configures `SimpleCacheManager` if no other provider is found, but can use Redis, Caffeine, Hazelcast, JCache, etc.

**Interviewer:** Compare Caffeine and Redis as cache providers.

**Candidate:**

| Aspect | Caffeine | Redis |
|--------|----------|-------|
| Location | In-process (local JVM) | Remote (network) |
| Speed | Nanoseconds | Milliseconds (network latency) |
| Capacity | RAM-limited | Configurable (can be large) |
| Distribution | Per-instance | Shared across instances |
| Eviction | LRU, LFU, weighted, time-based | TTL, LRU (via `allkeys-lru`) |
| Best for | Hot data, per-instance caching | Shared cache, distributed state |

Common pattern: **multi-level caching** — Caffeine as L1 (fast, local), Redis as L2 (shared, distributed). Spring Cache doesn't natively support L1/L2 cascading, but it can be implemented with custom `CacheManager`.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you ensure cache consistency when data changes?

**Candidate:** Cache invalidation strategies:
1. **Write-through:** Update cache when writing to database via `@CachePut`. Ensures consistency but adds write latency.
2. **Write-behind:** Update database, invalidate cache via `@CacheEvict`. Cache is repopulated on next read. High read throughput, eventual consistency.
3. **TTL-based:** Let cache expire naturally. Simplest approach, but stale data within TTL window.
4. **Event-driven invalidation:** On data change, publish event to invalidate caches across all instances (Redis pub/sub, Kafka).
5. **Version-based:** Cache keys include data version. When version changes, cache is effectively invalidated.

For most Spring Boot apps, `@CacheEvict` with TTL fallback is the pragmatic choice. For distributed systems, event-driven invalidation ensures all instances are notified.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a caching strategy for a social media news feed that serves 10M users.

**Candidate:** Multi-level caching architecture:

```
User Request → API Gateway → CDN (CloudFront) — static assets
                              ↓
                        Application Server
                              ↓
                   ┌──────────┴──────────┐
                   │   L1: Caffeine       │
                   │   Per-instance       │
                   │   100MB / instance   │
                   │   TTL: 30 seconds    │
                   │   Max: 10K entries   │
                   └──────────┬──────────┘
                              ↓
                   ┌──────────┴──────────┐
                   │   L2: Redis Cluster  │
                   │   Sharded by user ID │
                   │   100GB total        │
                   │   TTL: 5 minutes     │
                   └──────────┬──────────┘
                              ↓
                   ┌──────────┴──────────┐
                   │   L3: PostgreSQL     │
                   │   (read replicas)    │
                   └─────────────────────┘
```

**Cache key design:**
```java
@Cacheable(value = "userFeed", key = "#userId + '-' + T(java.time.LocalDate).now()")
public List<Post> getUserFeed(Long userId) { ... }
```

**Cache stampede prevention:**
```java
@Cacheable(value = "trendingTopics", sync = true) // synchronized cache loading
public List<Topic> getTrendingTopics() { ... }
```

**Invalidation on new post:**
```java
@CacheEvict(value = "userFeed", key = "#post.authorId")
public void createPost(Post post) { ... }
```

**Popular content vs personal feeds:**
- Celebrity posts: Cache aggressively with longer TTL
- Personal feeds: Cache per-user with shorter TTL
- Trending content: Global cache with periodic refresh

---

## Interviewer Feedback

**Strengths:** Good multi-level caching design, practical stampede prevention  
**Areas to Improve:** Could discuss Redis cluster topology and slot migration  
**Verdict:** Strong Hire

---

*Lab 13 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*

# Solution Guide — Cache Stampede Mitigation

## Problem: Cache Miss Storm Takes Down Database

This guide provides compilable Java code implementations of cache stampede prevention techniques: stale-while-revalidate, TTL jitter, probabilistic early expiration (XFetch), concurrent request coalescing, and rate limiting for cache miss regeneration.

---

## Layer 1: Stale-While-Revalidate (SWR) Pattern

### Redis SWR Service Implementation

```java
package com.example.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class StaleWhileRevalidateCacheService {

    private static final Logger log = LoggerFactory.getLogger(StaleWhileRevalidateCacheService.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ExecutorService revalidationExecutor;

    public StaleWhileRevalidateCacheService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.revalidationExecutor = Executors.newFixedThreadPool(10);
    }

    public <T> T getWithStaleWhileRevalidate(
            String key,
            Class<T> type,
            Supplier<T> dbQuery,
            Duration ttl,
            Duration swrWindow
    ) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String cached = ops.get(key);

        if (cached != null) {
            try {
                CacheEntry<T> entry = deserializeEntry(cached, type);
                long remainingTtl = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);

                if (remainingTtl > 0) {
                    log.debug("Cache HIT for key={}, TTL={}ms", key, remainingTtl);
                    return entry.getData();
                }

                // Entry expired but we have stale data — serve stale, revalidate in background
                long staleTtl = Math.abs(remainingTtl); // how long past TTL
                if (staleTtl < swrWindow.toMillis()) {
                    log.info("Serving stale data for key={} (stale for {}ms), revalidating in background", key, staleTtl);
                    revalidateAsync(key, type, dbQuery, ttl);
                    return entry.getData();
                }

                log.warn("Stale data for key={} exceeds SWR window ({}ms > {}ms), forcing synchronous refresh",
                    key, staleTtl, swrWindow.toMillis());
            } catch (Exception e) {
                log.error("Error deserializing cache entry for key={}", key, e);
            }
        }

        log.debug("Cache MISS for key={}, querying database synchronously", key);
        return revalidateSync(key, type, dbQuery, ttl);
    }

    private <T> void revalidateAsync(
            String key,
            Class<T> type,
            Supplier<T> dbQuery,
            Duration ttl
    ) {
        revalidationExecutor.submit(() -> {
            try {
                T freshData = dbQuery.get();
                CacheEntry<T> entry = new CacheEntry<>(freshData, System.currentTimeMillis());
                String serialized = serializeEntry(entry);
                redisTemplate.opsForValue().set(key, serialized, ttl);
                log.info("Background revalidation complete for key={}", key);
            } catch (Exception e) {
                log.error("Background revalidation failed for key={}", key, e);
            }
        });
    }

    private <T> T revalidateSync(
            String key,
            Class<T> type,
            Supplier<T> dbQuery,
            Duration ttl
    ) {
        try {
            T freshData = dbQuery.get();
            CacheEntry<T> entry = new CacheEntry<>(freshData, System.currentTimeMillis());
            String serialized = serializeEntry(entry);
            redisTemplate.opsForValue().set(key, serialized, ttl);
            log.debug("Synchronous revalidation complete for key={}", key);
            return freshData;
        } catch (Exception e) {
            log.error("Synchronous revalidation failed for key={}, returning null", key, e);
            return null;
        }
    }

    private <T> String serializeEntry(CacheEntry<T> entry) {
        try {
            return objectMapper.writeValueAsString(entry);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize cache entry", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> CacheEntry<T> deserializeEntry(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory()
                .constructParametricType(CacheEntry.class, type));
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize cache entry", e);
        }
    }

    static class CacheEntry<T> {
        private T data;
        private long createdAt;

        public CacheEntry() {}

        public CacheEntry(T data, long createdAt) {
            this.data = data;
            this.createdAt = createdAt;
        }

        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    }
}
```

---

## Layer 2: TTL Jitter Implementation

```java
package com.example.cache;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class TtlJitterGenerator {

    private static final double JITTER_PERCENTAGE = 0.10;
    private static final SecureRandom secureRandom = new SecureRandom();

    public Duration applyJitter(Duration baseTtl) {
        long baseMs = baseTtl.toMillis();
        long jitterMs = (long) (baseMs * JITTER_PERCENTAGE);
        long randomJitter = ThreadLocalRandom.current().nextLong(-jitterMs, jitterMs + 1);
        long resultMs = Math.max(1000, baseMs + randomJitter); // minimum 1 second
        return Duration.ofMillis(resultMs);
    }

    public Duration applyJitterWithRange(Duration baseTtl, double minJitter, double maxJitter) {
        long baseMs = baseTtl.toMillis();
        long minJitterMs = (long) (baseMs * minJitter);
        long maxJitterMs = (long) (baseMs * maxJitter);
        long randomJitter = ThreadLocalRandom.current().nextLong(minJitterMs, maxJitterMs + 1);
        return Duration.ofMillis(baseMs + randomJitter);
    }

    public int applyJitterToSeconds(int baseSeconds) {
        double jitterFactor = 0.10;
        int jitterRange = (int) (baseSeconds * jitterFactor);
        int jitter = secureRandom.nextInt(-jitterRange, jitterRange + 1);
        return Math.max(1, baseSeconds + jitter);
    }
}
```

### Using Jitter in Cache Operations

```java
package com.example.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class NewsFeedCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final TtlJitterGenerator ttlJitter;
    private final Duration baseTtl = Duration.ofSeconds(300);

    public NewsFeedCacheService(RedisTemplate<String, String> redisTemplate, TtlJitterGenerator ttlJitter) {
        this.redisTemplate = redisTemplate;
        this.ttlJitter = ttlJitter;
    }

    public void cacheFeedEntry(String key, String feedContent) {
        Duration ttlWithJitter = ttlJitter.applyJitter(baseTtl);
        redisTemplate.opsForValue().set(
            "feed:" + key,
            feedContent,
            ttlWithJitter
        );
        // Result: each entry gets TTL between 270s and 330s (300s ± 10%)
    }

    public void cacheFeedEntryWithBoundedJitter(String key, String feedContent) {
        Duration ttl = ttlJitter.applyJitterWithRange(baseTtl, -0.10, 0.05);
        // Negative jitter: 270-300s, positive jitter: 0-15s
        // Result: most entries expire slightly earlier, some later
        redisTemplate.opsForValue().set("feed:" + key, feedContent, ttl);
    }
}
```

---

## Layer 3: Probabilistic Early Expiration (XFetch Algorithm)

```java
package com.example.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class XFetchProbabilisticExpiration {

    private static final Logger log = LoggerFactory.getLogger(XFetchProbabilisticExpiration.class);

    private static final double BETA = 1.0; // XFetch beta parameter
    private static final double DELTA = 0.5; // XFetch delta parameter

    private final RedisTemplate<String, String> redisTemplate;

    public XFetchProbabilisticExpiration(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean shouldRefreshEarly(String key, long ttlMs) {
        long elapsedMs = getElapsedSinceCreation(key);
        if (elapsedMs < 0) {
            return false; // Can't determine age, don't refresh
        }

        double ageRatio = (double) elapsedMs / ttlMs;
        double probability = calculateXFetchProbability(ageRatio);

        boolean shouldRefresh = ThreadLocalRandom.current().nextDouble() < probability;

        if (shouldRefresh) {
            log.debug("XFetch: Early refresh triggered for key={}, ageRatio={}, probability={}",
                key, String.format("%.2f", ageRatio), String.format("%.4f", probability));
        }

        return shouldRefresh;
    }

    private double calculateXFetchProbability(double ageRatio) {
        // XFetch formula: P(refresh) = ageRatio^BETA - (1 - DELTA) * ageRatio^(BETA + 1)
        // where BETA controls how aggressively we refresh early
        // and DELTA controls the maximum probability
        double term1 = Math.pow(ageRatio, BETA);
        double term2 = (1 - DELTA) * Math.pow(ageRatio, BETA + 1);
        return Math.min(1.0, Math.max(0.0, term1 - term2));
    }

    private long getElapsedSinceCreation(String key) {
        try {
            Long idleTime = redisTemplate.getConnectionFactory()
                .getConnection()
                .objectIdletime(key.getBytes());
            if (idleTime != null) {
                return idleTime * 1000L; // Redis returns seconds, convert to ms
            }
        } catch (Exception e) {
            log.warn("Failed to get idle time for key={}", key, e);
        }
        return -1;
    }

    public boolean xFetchDecision(long elapsedMs, long ttlMs) {
        if (elapsedMs >= ttlMs) {
            return true; // Already expired, must refresh
        }

        double ageRatio = (double) elapsedMs / ttlMs;
        double probability = calculateXFetchProbability(ageRatio);

        return ThreadLocalRandom.current().nextDouble() < probability;
    }
}
```

### XFetch Threshold Visualization

| Age Ratio | XFetch Probability | Behavior |
|-----------|-------------------|----------|
| 0.0 (just created) | 0.0 | Never refresh |
| 0.5 (half TTL elapsed) | 0.125 | Rarely refresh |
| 0.75 | 0.34 | Sometimes refresh |
| 0.9 | 0.56 | Often refresh |
| 1.0 (expired) | 1.0 | Always refresh |
| 1.2 (past TTL) | 1.0 | Always refresh |

---

## Layer 4: Concurrent Request Coalescing (Mutex/Lock per Key)

```java
package com.example.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Service
public class RequestCoalescingCacheService {

    private static final Logger log = LoggerFactory.getLogger(RequestCoalescingCacheService.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ConcurrentHashMap<String, ReentrantLock> keyLocks = new ConcurrentHashMap<>();

    private static final String LOCK_KEY_PREFIX = "lock:";
    private static final Duration LOCK_TTL = Duration.ofSeconds(5);
    private static final Duration MAX_WAIT = Duration.ofMillis(500);

    public RequestCoalescingCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public <T> T getWithCoalescing(
            String cacheKey,
            Class<T> type,
            Supplier<T> dbQuery,
            Duration ttl
    ) {
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("Cache HIT for key={}", cacheKey);
            return deserialize(cached, type);
        }

        String lockKey = LOCK_KEY_PREFIX + cacheKey;
        Boolean lockAcquired = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, "LOCKED", LOCK_TTL);

        if (Boolean.TRUE.equals(lockAcquired)) {
            try {
                String recheck = redisTemplate.opsForValue().get(cacheKey);
                if (recheck != null) {
                    log.debug("Cache entry was populated by another request while acquiring lock, key={}", cacheKey);
                    return deserialize(recheck, type);
                }

                log.debug("Cache MISS for key={}, regenerating (lock acquired)", cacheKey);
                T freshData = dbQuery.get();
                redisTemplate.opsForValue().set(cacheKey, serialize(freshData), ttl);
                return freshData;
            } finally {
                redisTemplate.delete(lockKey);
            }
        }

        log.debug("Cache MISS for key={}, waiting for other request to populate (lock not acquired)", cacheKey);
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < MAX_WAIT.toMillis()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            String result = redisTemplate.opsForValue().get(cacheKey);
            if (result != null) {
                log.debug("Waited {}ms for concurrent request to populate key={}",
                    System.currentTimeMillis() - start, cacheKey);
                return deserialize(result, type);
            }
        }

        log.warn("Timed out waiting for concurrent cache population for key={}, querying DB directly", cacheKey);
        T freshData = dbQuery.get();
        redisTemplate.opsForValue().set(cacheKey, serialize(freshData), Duration.ofSeconds(30));
        return freshData;
    }

    private String serialize(Object data) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("Serialization error", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T deserialize(String data, Class<T> type) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(data, type);
        } catch (Exception e) {
            throw new RuntimeException("Deserialization error", e);
        }
    }
}
```

---

## Layer 5: Rate Limiter for Cache Miss Regeneration

```java
package com.example.cache;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.function.Supplier;

@Component
public class CacheMissRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(CacheMissRateLimiter.class);

    private final RateLimiter rateLimiter;

    public CacheMissRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitForPeriod(500)                     // Max 500 cache miss regenerations
            .limitRefreshPeriod(Duration.ofSeconds(1)) // Per second
            .timeoutDuration(Duration.ofMillis(100))   // Wait max 100ms for permit
            .build();

        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        this.rateLimiter = registry.rateLimiter("cacheMissRegeneration");
    }

    public <T> T executeWithRateLimit(Supplier<T> dbQuery, String key) {
        try {
            return RateLimiter.decorateSupplier(rateLimiter, dbQuery).get();
        } catch (Exception e) {
            log.warn("Rate limit exceeded for cache miss regeneration, key={}, returning null", key);
            return null;
        }
    }

    public int getAvailablePermits() {
        return rateLimiter.getMetrics().getAvailablePermissions();
    }
}
```

---

## Layer 6: Complete Cache Service with All Patterns

```java
package com.example.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.function.Supplier;

@Service
public class ResilientNewsFeedCacheService {

    private static final Logger log = LoggerFactory.getLogger(ResilientNewsFeedCacheService.class);

    private final StaleWhileRevalidateCacheService swrCache;
    private final RequestCoalescingCacheService coalescingCache;
    private final XFetchProbabilisticExpiration xFetch;
    private final CacheMissRateLimiter rateLimiter;
    private final TtlJitterGenerator ttlJitter;

    private static final Duration BASE_TTL = Duration.ofSeconds(300);
    private static final Duration SWR_WINDOW = Duration.ofSeconds(60);

    public ResilientNewsFeedCacheService(
            StaleWhileRevalidateCacheService swrCache,
            RequestCoalescingCacheService coalescingCache,
            XFetchProbabilisticExpiration xFetch,
            CacheMissRateLimiter rateLimiter,
            TtlJitterGenerator ttlJitter
    ) {
        this.swrCache = swrCache;
        this.coalescingCache = coalescingCache;
        this.xFetch = xFetch;
        this.rateLimiter = rateLimiter;
        this.ttlJitter = ttlJitter;
    }

    public String getFeedContent(String userId) {
        String key = "feed:" + userId;
        Duration ttl = ttlJitter.applyJitter(BASE_TTL);

        if (xFetch.shouldRefreshEarly(key, ttl.toMillis())) {
            log.debug("XFetch triggered early refresh for key={}", key);
            return swrCache.getWithStaleWhileRevalidate(
                key, String.class,
                () -> rateLimiter.executeWithRateLimit(
                    () -> queryDatabaseForFeed(userId), key),
                ttl, SWR_WINDOW
            );
        }

        return coalescingCache.getWithCoalescing(
            key, String.class,
            () -> rateLimiter.executeWithRateLimit(
                () -> queryDatabaseForFeed(userId), key),
            ttl
        );
    }

    private String queryDatabaseForFeed(String userId) {
        log.debug("Regenerating feed for userId={} from database", userId);
        return generateFeedContent(userId);
    }
}
```

---

## Layer 7: Cache Warming Service

```java
package com.example.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CacheWarmingService {

    private static final Logger log = LoggerFactory.getLogger(CacheWarmingService.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final TtlJitterGenerator ttlJitter;
    private final Duration BASE_TTL = Duration.ofSeconds(300);

    public CacheWarmingService(RedisTemplate<String, String> redisTemplate, TtlJitterGenerator ttlJitter) {
        this.redisTemplate = redisTemplate;
        this.ttlJitter = ttlJitter;
    }

    @Scheduled(fixedDelayString = "${cache.warming.interval:300000}")
    public void warmCache() {
        log.info("Starting cache warming cycle");
        Set<String> popularKeys = getPopularKeys();

        for (String key : popularKeys) {
            if (!redisTemplate.hasKey(key)) {
                Duration ttl = ttlJitter.applyJitter(BASE_TTL);
                String content = regenerateFeedContent(key);
                redisTemplate.opsForValue().set(key, content, ttl);
                log.info("Warmed cache key={} with TTL={}s", key, ttl.toSeconds());
            }
        }

        log.info("Cache warming cycle complete — warmed {} entries", popularKeys.size());
    }

    private Set<String> getPopularKeys() {
        return redisTemplate.keys("feed:*").stream()
            .limit(1000)
            .collect(Collectors.toSet());
    }

    public void warmSpecificKeys(List<String> keys) {
        keys.forEach(this::warmKey);
    }

    public void warmKey(String key) {
        if (!redisTemplate.hasKey(key)) {
            Duration ttl = ttlJitter.applyJitter(BASE_TTL);
            String content = regenerateFeedContent(key);
            redisTemplate.opsForValue().set(key, content, ttl);
            log.info("Warmed specific key={}", key);
        }
    }

    private String regenerateFeedContent(String key) {
        return "feed content for " + key + " generated at " + System.currentTimeMillis();
    }
}
```

---

## Complete Remediation Steps

### Step 1: Immediate Configuration Fix
1. Disable cache eviction: Redis `maxmemory-policy noeviction`
2. Increase default TTL to 3600s (temporary safety measure)
3. Apply TTL jitter (+/- 10%) to all cache write operations

### Step 2: Implement SWR Pattern
1. Add `StaleWhileRevalidateCacheService` to the codebase
2. Configure SWR window (60 seconds)
3. Update all cache reads to use SWR

### Step 3: Implement XFetch
1. Add `XFetchProbabilisticExpiration` service
2. Configure XFetch beta (1.0) and delta (0.5) parameters
3. Integrate XFetch decision with SWR trigger

### Step 4: Add Request Coalescing
1. Implement Redis-based distributed mutex per cache key
2. Configure lock TTL (5 seconds)
3. Handle lock acquisition failure with spin-wait

### Step 5: Rate Limit Cache Misses
1. Configure Resilience4j RateLimiter (500 regenerations/second)
2. Add graceful degradation when rate limited

### Step 6: Cache Warming
1. Implement scheduled cache warming for popular keys
2. Warm cache ahead of known traffic patterns

---

## Verification Commands

```powershell
# Check cache hit rate
redis-cli info stats | Select-String "keyspace_hits|keyspace_misses"
# Calculate: keyspace_hits / (keyspace_hits + keyspace_misses) * 100

# Check cache TTL distribution
redis-cli --eval ttl_distribution.lua , feed:* 10

# Monitor database query rate
az monitor metrics list --resource {db-id} --metric "queries_per_second"

# Verify TTL jitter
redis-cli TTL feed:user:001
redis-cli TTL feed:user:002
redis-cli TTL feed:user:003
# Should show different values (e.g., 271, 298, 322)

# Check cache miss rate limiter
curl http://feed-service:8080/actuator/metrics/resilience4j.ratelimiter.cacheMissRegeneration

# Validate SWR behavior
curl -v http://feed-service:8080/api/feed/user-001
# Check response headers: X-Cache: HIT, X-Cache-Stale: true (when serving stale)
```

---

## References

- Meta Engineering: "Scaling Memcache at Facebook" (USENIX NSDI 2013)
- Meta Engineering: "An Analysis of Facebook Photo Caching" (USENIX NSDI 2013)
- Google SRE Book — Chapter 22: "Managing Cascading Failures"
- Amazon ElastiCache: "Best Practices for Redis" — https://docs.aws.amazon.com/AmazonElastiCache/latest/red-ug/BestPractices.html
- Redis Documentation: "Using Redis as an LRU Cache" — https://redis.io/topics/lru-cache
- XFetch Algorithm: Vattani, Zaccarato — "Probabilistic Early Expiration for Cache Stampede Prevention"
- Twitter Engineering: "Timelines at Scale" — https://blog.twitter.com/engineering/en_us/topics/infrastructure/2016/timelines-at-scale

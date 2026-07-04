# Caching - DEBUGGING

## Monitoring Cache Metrics

### Spring Boot Actuator Cache Endpoints
```bash
# List all caches
GET /actuator/caches

# Cache metrics (with Micrometer)
# For Caffeine:
GET /actuator/metrics/cache.gets
GET /actuator/metrics/cache.evictions

# For Redis:
GET /actuator/metrics/redis.commands
```

### Caffeine Statistics
```java
Cache<String, Product> cache = Caffeine.newBuilder()
    .recordStats()
    .build();

// Access stats
CacheStats stats = cache.stats();
double hitRate = stats.hitRate();        // 0.0 - 1.0
long missCount = stats.missCount();
long evictionCount = stats.evictionCount();
double averageLoadPenalty = stats.averageLoadPenalty();  // nanoseconds
```

### Redis Monitoring
```bash
# Redis INFO command
redis-cli INFO stats
# Keyspace hits/misses ratio
redis-cli INFO stats | findstr keyspace

# Monitor latency
redis-cli --latency -h localhost -p 6379

# Slow queries
redis-cli SLOWLOG GET 10
```

## Common Cache Issues

| Symptom | Cause | Diagnostic |
|---------|-------|------------|
| Low hit rate < 50% | Cache too small, bad key design | Check cache stats, access patterns |
| High DB load despite cache | Cache stampede | Check concurrent miss rate |
| Stale data returned | Missing invalidation | Check invalidation coverage |
| Slow cache responses | Network latency, large values | Measure Redis latency |
| OOM crashes | Unbounded cache, memory leak | Heap dump, cache size monitoring |
| Inconsistent reads | Replication lag in Redis cluster | Check cluster node sync |

## Cache Debugging Tools

```java
// Log all cache operations for debugging
@Component
public class CacheLogger implements CacheErrorHandler {
    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
        log.error("Cache GET error: {} key={}", cache.getName(), key, e);
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
        log.error("Cache PUT error: {} key={}", cache.getName(), key, e);
    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
        log.error("Cache EVICT error: {} key={}", cache.getName(), key, e);
    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {
        log.error("Cache CLEAR error: {}", cache.getName(), e);
    }
}
```

## Redis Debugging Commands

```bash
# Check cache key TTL
TTL product:123
# -1 = no expiry, -2 = key doesn't exist

# Memory usage of specific key
MEMORY USAGE product:123

# Scan for keys matching pattern
SCAN 0 MATCH product:* COUNT 100

# Monitor live commands (production warning!)
MONITOR
```

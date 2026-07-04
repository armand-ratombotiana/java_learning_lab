# Scalability - INTERNALS

## Load Balancer Internals

### Health Checks
Load balancers perform periodic health checks:
- **TCP**: Port open check (basic)
- **HTTP**: 200 OK on `/health` endpoint
- **Custom**: Application-specific readiness/liveness probes

### Connection Draining
When an instance is removed, the load balancer:
1. Marks it as draining
2. Completes in-flight requests (up to timeout)
3. Stops sending new requests
4. Removes the instance

## Auto-Scaling Internals

### Metrics-Based Scaling
```yaml
# AWS Auto Scaling config
TargetTrackingScalingConfiguration:
  TargetValue: 50.0  # CPU %
  ScaleInCooldown: 300  # seconds
  ScaleOutCooldown: 60  # seconds
```

### State Management in Auto-Scaling
New instances must be provisioned with:
- Latest application artifact (AMI/container)
- Configuration (from config server/environment)
- Cache warm-up (prevent empty-cache stampede)
- Connection pool initialization

## Database Scaling Internals

### Read Replica Replication
```sql
-- Master logs changes
-- Replica reads binary log and applies
-- Types: synchronous (all), semi-sync (one), async (fastest)

-- Check replica lag
SHOW SLAVE STATUS\G
-- Seconds_Behind_Master: 0  <-- target
```

### Sharding Key Selection
```java
// Good shard keys: high cardinality, even distribution
// Customer ID    ✓  (many customers, uniform)
// Order ID       ✓  (unique, random)
// Timestamp      ✗  (hot shard for current time)
// Country        ✗  (skewed: US >> others)
```

## Caching Layer Internals

### Cache-Aside Pattern
```java
public Product getProduct(String id) {
    Product cached = cache.get(id);
    if (cached != null) return cached;

    Product db = repository.findById(id);  // cache miss
    cache.put(id, db);                     // populate cache
    return db;
}
```

### Cache Eviction Policies
- **LRU**: Evict least recently used
- **TTL**: Expire after time-to-live
- **LFU**: Evict least frequently used
- **FIFO**: Evict oldest first

## JVM Concurrency Internals

### Thread Pool Scaling
```java
// Fixed thread pool for bounded tasks
ExecutorService pool = Executors.newFixedThreadPool(
    Runtime.getRuntime().availableProcessors() * 2
);

// Virtual threads (Java 21+) - scale to millions
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> handleRequest());
}
```

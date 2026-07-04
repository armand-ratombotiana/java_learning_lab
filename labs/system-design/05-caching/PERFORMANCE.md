# Caching - PERFORMANCE

## Performance Benchmarks

### Cache Comparison
| Cache | Latency (read) | Throughput | Max Memory | Persistence |
|-------|---------------|-----------|------------|-------------|
| Caffeine (local) | 0.1ms | 1M+ ops/sec | JVM heap | No |
| Redis (single) | 1ms | 100K ops/sec | Configurable | Yes (RDB/AOF) |
| Redis Cluster | 2ms | 100K×N ops/sec | Distributed | Yes |
| Memcached | 1ms | 100K ops/sec | Configurable | No |
| Ehcache | 0.5ms | 500K ops/sec | JVM heap | Yes (disk) |

### Latency Breakdown
```
Local Cache (Caffeine)
├── Hash lookup:          0.01ms
├── Deserialization:      0.05ms
└── Return:               0.04ms
Total: ~0.1ms

Redis Cache
├── Network round trip:   0.5ms
├── Command execution:    0.1ms
├── Deserialization:      0.3ms
└── Return:               0.1ms
Total: ~1ms

Database (PostgreSQL)
├── Connection pool:      1ms
├── Query planning:       5ms
├── Disk IO:             40ms
├── Deserialization:      3ms
└── Return:               1ms
Total: ~50ms
```

## Optimization Techniques

### Key Design
```java
// GOOD: Structured keys with namespace and version
String key = "product:123:v2";

// BAD: Unstructured keys
String key = "123";

// Redis performance: KEY[100] > KEY[10000]
// Use short keys for better performance
```

### Serialization Performance
```java
// Fastest to slowest in Java:
// 1. Native binary (byte[])
// 2. Protocol Buffers
// 3. Kryo
// 4. Jackson (JSON)
// 5. Java Serialization (avoid)
```

### Connection Pooling
```yaml
# Redis pool configuration
spring.redis.lettuce.pool:
  max-active: 20
  max-idle: 10
  min-idle: 5
  max-wait: 2000ms
```

## Cache Warming

### Startup Warming
```java
@EventListener(ApplicationReadyEvent.class)
public void warmCache() {
    List<String> hotProductIds = analyticsService.getTopProducts(1000);
    hotProductIds.parallelStream().forEach(id -> {
        productService.getProduct(id);  // triggers cache load
    });
}
```

### Predictable Performance
```
Without warming:
  First 1000 requests: ~50ms each (cache miss)
  After: ~1ms each (cache hit)

With warming:
  All requests: ~1ms each
  No cold-start penalty
```

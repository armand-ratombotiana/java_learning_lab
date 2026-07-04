# Scalability - DEBUGGING

## Performance Testing

### Tools
- **Apache JMeter**: Thread-based load testing
- **Gatling**: Scala-based high-performance testing
- **k6**: JavaScript-based, modern
- **wrk**: HTTP benchmarking

### Key Metrics
```bash
# Request latency distribution
wrk -t12 -c400 -d30s http://localhost:8080/api/products

# Thread dumps for stuck threads
jstack <pid> > threaddump.txt

# Heap analysis for memory issues
jmap -dump:live,format=b,file=heap.hprof <pid>
```

## Common Scaling Issues

| Symptom | Likely Cause | Diagnostic |
|---------|-------------|------------|
| Latency spikes under load | GC pauses | GC logs, `-XX:+PrintGCDetails` |
| Connection timeouts | Connection pool exhausted | HikariCP metrics |
| 503 errors | Auto-scaling too slow | Check launch template, cache warmup |
| Uneven load | Bad hash distribution | Check load balancer algorithm |
| Slow reads | Replica lag | `SHOW SLAVE STATUS` |

## Database Bottleneck Detection

```sql
-- Check slow queries
SELECT * FROM pg_stat_activity WHERE state = 'active';

-- Check replication lag
SELECT * FROM pg_stat_replication;

-- Check connection count
SELECT count(*) FROM pg_stat_activity;
```

## Cache Debugging

```java
// Enable cache statistics
@Bean
public CacheManager cacheManager() {
    RedisCacheManager cm = RedisCacheManager.create(redisConnectionFactory());
    cm.setTransactionAware(true);
    return cm;
}

// Monitor hit ratio via Actuator
// GET /actuator/cache
```

## Network Debugging

```bash
# Check open connections
netstat -an | findstr :8080

# Trace route between services
tracert product-service

# DNS resolution issues
nslookup product-service
```

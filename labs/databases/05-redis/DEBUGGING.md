# Debugging: Redis

## Monitoring Commands

```bash
# Real-time statistics
redis-cli --stat

# All INFO sections
redis-cli INFO

# Memory details
redis-cli INFO MEMORY

# Slow queries (queries > 10ms)
redis-cli SLOWLOG GET 50

# Monitor all commands (⚠️ performance impact on production)
redis-cli MONITOR
```

## Common Debugging Template

```java
@RestController
public class RedisDebugController {

    @Autowired
    private RedisTemplate<String, String> redis;

    @GetMapping("/redis/info")
    public Properties info() {
        return redis.getRequiredConnectionFactory()
            .getConnection().info();
    }

    @GetMapping("/redis/memory")
    public Properties memory() {
        return redis.getRequiredConnectionFactory()
            .getConnection().info("memory");
    }

    @GetMapping("/redis/slowlog")
    public List<Object> slowlog() {
        return redis.getRequiredConnectionFactory()
            .getConnection().slowLog().getAll();
    }
}
```

## Connection Pool Debugging

```yaml
# Enable Lettuce logging
logging:
  level:
    io.lettuce.core: DEBUG
    io.lettuce.core.protocol: DEBUG
```

## Memory Analysis

```bash
# Find biggest keys
redis-cli --bigkeys

# Memory stats
redis-cli MEMORY STATS
redis-cli MEMORY DOCTOR

# Key memory usage
redis-cli MEMORY USAGE "mykey"

# Top memory consumers
redis-cli --stat
```

## Latency Debugging

```bash
# Check latency
redis-cli --latency -h host -p 6379

# Latency histogram
redis-cli --latency-dist

# Intrinsic latency (server itself)
redis-cli --intrinsic-latency 100
```

## Replication Issues

```bash
# Check replication status
redis-cli INFO REPLICATION

# Replica lag
redis-cli INFO REPLICATION | grep lag
```

## Slow Query Configuration

```conf
# redis.conf
slowlog-log-slower-than 10000    # log queries > 10ms
slowlog-max-len 128              # keep last 128 slow queries
```

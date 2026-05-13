# Debugging Redis Cache Issues

## Common Failure Scenarios

### Cache Misses / High Miss Rate

Applications hit the database repeatedly instead of using cached data. Cache hit rate falls below expected levels. Database load increases while cache remains underutilized. This indicates cache configuration or data pattern issues.

Keys never hit because of incorrect key names or TTL. Check if keys exist with `GET` or `KEYS` commands. Verify application uses consistent key naming conventions. Look for case sensitivity issues.

Serialization changes invalidate the cache. When classes change, stored serialized data cannot be deserialized. The application treats deserialization failures as cache misses and re-populates from the database.

### Connection Issues

Redis connections timeout or refuse. Applications cannot connect to Redis, falling back to database or failing. Connection pool exhaustion or network issues cause this.

Connection pool settings may be too small for the load. When all connections are in use, new requests wait or fail. Check pool statistics and increase size if needed.

Redis server memory pressure causes slow responses or eviction. When Redis approaches max memory, it evicts keys following the configured policy. Check memory usage with `INFO memory`.

### Stack Trace Examples

**Connection refused:**
```
io.lettuce.core.RedisConnectionException: Unable to connect to localhost/<unresolved>:6379
    at io.lettuce.core.RedisClient.connect(RedisClient.java:456)
```

**OOM command disallowed:**
```
redis.clients.jedis.exceptions.JedisDataException: OOM command not allowed when used memory > 'maxmemory'
```

**Timeout during BLPOP:**
```
redis.clients.jedis.exceptions.JedisConnectionException: Could not get a resource from the pool
    at redis.clients.jedis.util.Pool.getResource(Pool.java:123)
```

## Debugging Techniques

### Monitoring Cache Metrics

Track hit rate and miss rate over time. Calculate hit ratio: hits / (hits + misses). A healthy cache typically shows 80%+ hit rate. Low hit rates indicate configuration problems.

Monitor memory usage and eviction counts. Use `INFO memory` to see used_memory, maxmemory, and eviction statistics. High eviction rates indicate insufficient cache size.

Check keyspace with `INFO keyspace` to see number of keys and expiration statistics. Use `DEBUG SEGFAULT` is not for production—use `SCAN` instead to iterate keys safely.

### Connection Pool Debugging

Check active connections and waiting threads. Configure pool size to match expected concurrency. Use `pool.getNumActive()` to see active connections.

Monitor blocking operations that hold connections. BLPOP, BRPOP, and other blocking commands hold connections until data arrives. Ensure timeout values are set appropriately.

Verify Redis configuration allows remote connections. Check bind address in redis.conf and firewall rules. Use `redis-cli ping` to verify basic connectivity.

## Best Practices

Use key patterns with appropriate TTL for data volatility. Set TTL to match data freshness requirements. Use separate key prefixes for different data types to enable selective clearing.

Implement cache-aside pattern with proper invalidation. Update cache on writes, not reads. Use expiration as a safety net, not primary invalidation strategy.

Use Redis Cluster for horizontal scaling. Configure appropriate read/write splitting. Handle redirect errors properly in the client.
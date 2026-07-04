# Theory: Connection Pooling

## Connection Lifecycle
1. **Creation**: TCP connect → SSL handshake → authentication → session init
2. **Borrow**: Application requests connection from pool
3. **Use**: Application executes SQL statements
4. **Return**: Application closes connection (returns to pool, not to DB)
5. **Validation**: Pool tests connection before lending (if configured)
6. **Eviction**: Pool closes stale/leaked connections

## Pool Sizing Formula
The ideal pool size depends on database CPU cores and query latency:

```
PoolSize = (DatabaseCPUCores * 2) + effectiveSpindleCount
```

For modern SSD databases, 10-30 connections per pool instance is typical.

## Little's Law in Pooling
```
ConcurrentRequests = Throughput × Latency
```
If each request holds a connection for 50ms and throughput is 200 req/s:
```
Concurrent connections = 200 × 0.05 = 10
```

## Pool Patterns
- **Static Pool**: Fixed size, created at startup
- **Dynamic Pool**: Grows/shrinks based on demand (min/max idle)
- **Partitioned Pool**: Multiple pools for different workloads (reporting vs OLTP)

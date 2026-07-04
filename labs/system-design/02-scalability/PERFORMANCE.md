# Scalability - PERFORMANCE

## Performance Metrics

### Latency Percentiles
- **P50 (median)**: Typical user experience
- **P95**: Slow but acceptable
- **P99**: Edge cases, needs attention
- **P999**: Usually indicates problems

### Throughput
- **RPS**: Requests per second
- **TPS**: Transactions per second (db writes)
- **QPS**: Queries per second (db reads)

### Resource Utilization
- CPU: Target 50-70% under normal load
- Memory: 70-80% headroom for GC
- Network: < 50% bandwidth utilization
- Disk IO: < 30% utilization (NVMe clusters)

## Optimization Techniques

### Application Layer
- Connection pooling (HikariCP: 10-50 connections per instance)
- HTTP keep-alive for multiplexing
- Response compression (gzip/brotli)
- JSON serialization optimization (Jackson vs Gson)

### JVM Tuning
```bash
# G1GC for low latency
-XX:+UseG1GC
-XX:MaxGCPauseMillis=100
-XX:G1HeapRegionSize=16m

# ZGC for ultra-low pause
-XX:+UseZGC
-XX:ConcGCThreads=4
```

### Database Layer
```sql
-- Index missing queries
CREATE INDEX idx_orders_customer_date ON orders(customer_id, created_at);

-- Connection pooling
-- HikariCP: maximumPoolSize = ((core_count * 2) + effective_spindle_count)
```

### Caching Strategy
| Pattern | Use Case | Latency |
|---------|----------|---------|
| Cache-Aside | Read-heavy workloads | 1-5ms |
| Read-Through | Consistent reads | 1-10ms |
| Write-Through | Data integrity | 1-10ms |
| Write-Behind | High write throughput | <1ms |

## Scalability Benchmarks

| Architecture | Max Throughput | Latency P99 |
|-------------|---------------|-------------|
| Single server | 2K RPS | 200ms |
| 3 instances + LB | 6K RPS | 250ms |
| 10 instances + LB | 20K RPS | 300ms |
| + Cache layer | 50K RPS | 50ms |
| + Read replicas | 100K RPS | 60ms |
| + Async processing | 200K RPS | 100ms |

## Memory Budget Calculation
```java
// For 10K concurrent requests at 500ms each
// Concurrent requests in flight = 10K * 0.5 = 5K
// Each request uses ~10KB → 50MB heap for request processing
// Plus 100MB for application + 200MB cache
// Total: ~350MB per instance minimum
```

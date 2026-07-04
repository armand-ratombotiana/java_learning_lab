# Availability - PERFORMANCE

## Performance vs Availability Trade-off

### Key Tension
High availability often costs performance:
- **Redundancy**: More servers = more network hops
- **Replication**: Sync replication adds latency
- **Circuit breakers**: State tracking adds overhead
- **Health checks**: Polling consumes resources

## Circuit Breaker Performance

### Overhead
| Circuit Breaker Operation | Latency Added |
|--------------------------|---------------|
| State lookup | < 0.1ms |
| Success counting | < 0.01ms |
| Failure counting | < 0.01ms |
| Half-open probe | 1 call (negligible) |

Resilience4j circuit breakers add under 1ms to call latency.

## Synchronous vs Asynchronous Replication

### Trade-off
| Type | Data Loss | Write Latency |
|------|-----------|--------------|
| Async | Minutes (RPO) | 1-5ms |
| Semi-sync | 0 to KiB | 5-20ms |
| Full sync | Zero | 20-100ms |

For most apps, semi-sync provides the best balance.

## Health Check Impact

| Check Type | Frequency | Avg CPU Impact |
|-----------|-----------|---------------|
| TCP port | 5s | < 0.01% |
| HTTP /health | 10s | < 0.05% |
| Full dependency | 30s | 0.5% |
| Complex DB query | 60s | 2% |

Keep simple checks frequent, complex checks rare.

## Disaster Recovery Recovery Time

| Strategy | RTO | RPO | Performance During Normal Ops |
|----------|-----|-----|------------------------------|
| Active-Active | Seconds | < 1s | Full performance |
| Warm Standby | Minutes | Seconds | 95% performance |
| Pilot Light | 30 min | 1 hour | 99% performance |
| Backup/Restore | 4+ hours | 24 hours | No impact |

## Optimizing Resilience

### Connection Pool Sizing
```java
// Small pool for DB, larger for cache
@Bean
@ConfigurationProperties("spring.datasource.hikari")
public DataSource dbDataSource() {
    return DataSourceBuilder.create()
        .maximumPoolSize(10)  // DB pool
        .build();
}

@Bean
public JedisConnectionFactory redisFactory() {
    JedisPoolConfig config = new JedisPoolConfig();
    config.setMaxTotal(50);  // Redis pool
    return new JedisConnectionFactory(config);
}
```

### Circuit Breaker Tuning
- Small sliding window for fast recovery
- Short wait duration for transient-heavy workloads
- Higher threshold for critical services

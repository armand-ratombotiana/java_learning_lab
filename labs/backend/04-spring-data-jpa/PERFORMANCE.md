# Performance of 04 Spring Data Jpa

## Configuration Tuning
```properties
# Thread pool sizing
04springdatajpa.thread-pool.core-size=10
04springdatajpa.thread-pool.max-size=50
04springdatajpa.thread-pool.queue-capacity=100
```

## Connection Pooling
```properties
# Database connection pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

## Caching
```java
@Cacheable(value = "results", unless = "#result == null")
public Result getResult(String key) {
    // Expensive operation
}
```

## Monitoring
```properties
management.endpoints.web.exposure.include=health,metrics,prometheus
```

## Benchmark Tips
- Profile before optimizing
- Test with realistic workloads
- Monitor GC behavior
- Use connection pooling
- Enable compression


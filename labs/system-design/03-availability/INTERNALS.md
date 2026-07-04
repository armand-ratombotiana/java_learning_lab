# Availability - INTERNALS

## Circuit Breaker Internals

### Sliding Window
Resilience4j uses a sliding window to count successes/failures.
```java
// Count-based: last 100 calls
.slidingWindowType(COUNT_BASED)
.slidingWindowSize(100)

// Time-based: last 60 seconds
.slidingWindowType(TIME_BASED)
.slidingWindowSize(60)
```

### Half-Open Probing
When the circuit is half-open, a configurable number of requests are allowed through. If they succeed, the circuit closes. If any fails, it reopens.

```java
.permittedNumberOfCallsInHalfOpenState(5)
```

## Health Check Internals

### Spring Boot Health Indicators
```java
public class DatabaseHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        try {
            DataSource ds = getDataSource();
            try (Connection c = ds.getConnection()) {
                if (c.isValid(1000)) {
                    return Health.up().withDetail("database", "reachable").build();
                }
            }
        } catch (Exception e) {
            return Health.down(e).build();
        }
        return Health.down().build();
    }
}
```

### Kubernetes Probe Behavior
- **livenessProbe**: If fails → restart container
- **readinessProbe**: If fails → remove from service endpoints
- **startupProbe**: Delays liveness checks during slow startup

## Failover Internals

### Database Failover
```
1. Monitor detects primary is unreachable
2. Promote replica to new primary
3. Update DNS / connection string
4. Remaining replicas connect to new primary
5. Application reconnects (connection pool refresh)
```

### Application-Level Failover
```java
// Multiple data sources with failover
@Bean
@Primary
public DataSource primaryDataSource() { /* main DB */ }

@Bean
public DataSource fallbackDataSource() { /* replica */ }

// Routing data source with failover
public class FailoverDataSource extends AbstractRoutingDataSource {
    private boolean primaryFailed = false;

    @Override
    protected Object determineCurrentLookupKey() {
        return primaryFailed ? "fallback" : "primary";
    }

    public void failover() { this.primaryFailed = true; }
}
```

## Disaster Recovery Internals

### Recovery Time Objective (RTO)
Target time to restore service after disaster.

### Recovery Point Objective (RPO)
Maximum acceptable data loss in time.

| Strategy | RTO | RPO | Cost |
|----------|-----|-----|------|
| Backup & Restore | Hours | 24 hours | $ |
| Pilot Light | 10-30 min | Minutes | $$ |
| Warm Standby | Minutes | Seconds | $$$ |
| Active-Active | Seconds | <1 second | $$$$ |

# Availability - DEBUGGING

## Monitoring Availability

### Key Metrics
- **Error rate**: 5xx responses / total requests
- **Circuit breaker state**: OPEN, CLOSED, HALF_OPEN
- **Health check status**: UP, DOWN, UNKNOWN
- **Connection pool usage**: active / idle / pending
- **Thread pool utilization**: active threads / max threads

### Actuator Endpoints
```bash
# Health check
GET /actuator/health

# Circuit breakers
GET /actuator/circuitbreakers

# Metrics
GET /actuator/metrics/resilience4j.circuitbreaker.calls
GET /actuator/metrics/resilience4j.circuitbreaker.state
```

## Common Failure Scenarios

| Symptom | Likely Cause | Check |
|---------|-------------|-------|
| All requests to service fail | Circuit breaker is OPEN | Check CB metrics, downstream health |
| Intermittent timeouts | Connection pool exhausted | Thread dumps, connection pool metrics |
| Failover not working | Standby not configured correctly | Verify standby health endpoint |
| Slow after recovery | Cache cold after failover | Check cache hit ratio |
| Split-brain scenario | Network partition without quorum | Check cluster membership |

## Debugging Circuit Breakers

```java
// Monitor circuit breaker events
CircuitBreaker cb = CircuitBreaker.ofDefaults("productService");
cb.getEventPublisher()
    .onSuccess(e -> log.info("Success: {}", e))
    .onFailure(e -> log.warn("Failure: {}", e))
    .onStateTransition(e -> log.warn("State change: {}", e));
```

## Testing Failover

```bash
# Simulate failure
curl -X POST http://localhost:8081/actuator/health/fail

# Verify failover happened
curl http://localhost:8082/health
# Output: primary route taken over

# Recovery
curl -X POST http://localhost:8081/actuator/health/recover
```

## Chaos Engineering Tools

- **Chaos Monkey**: Randomly kills instances
- **Gremlin**: Network delays, packet loss, CPU stress
- **Litmus**: Kubernetes chaos engineering
- **ByteSim**: JVM-level fault injection

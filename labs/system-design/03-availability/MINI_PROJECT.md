# Availability - MINI PROJECT

## Project: Highly Available Product Service

**Time**: 4-6 hours

Build a product service with:
- Circuit breaker implementation
- Health check endpoint
- Failover mechanism
- Monitoring setup

### Step 1: Circuit Breaker

```java
public class ProductCircuitBreaker extends CircuitBreaker {
    // Implementation from CODE_DEEP_DIVE
}
```

### Step 2: Health Endpoint

```java
@RestController
public class HealthController {
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("circuitBreaker", circuitBreaker.getState());
        return status;
    }
}
```

### Step 3: Configure Monitoring

Add Spring Actuator for health checks.

### Deliverables

- Circuit breaker with configurable thresholds
- Health endpoint returning service status
- Fallback mechanism on circuit open
- Automated health monitoring
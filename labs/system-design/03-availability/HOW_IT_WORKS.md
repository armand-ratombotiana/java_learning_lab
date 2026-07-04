# Availability - HOW IT WORKS

## Redundancy Patterns

### Active-Active
All instances serve traffic simultaneously. If one fails, traffic routes to others.
```
Client → Load Balancer → [Server A, Server B, Server C]
                          Failures A → traffic distributed to B and C
```

### Active-Passive (Hot Standby)
Primary serves traffic. Standby takes over if primary fails.
```
Normal:    Client → LB → Primary
Failover:  Client → LB → Standby (hot, ready to serve)
```

### Active-Passive (Cold Standby)
Primary serves traffic. Standby must be started on failover.
```
Normal:    Client → LB → Primary
Failover:  Client → LB → Standby (must boot)
```

## Circuit Breaker Pattern

### Three States
```
CLOSED ──► (failures > threshold) ──► OPEN ──► (timeout elapsed) ──► HALF_OPEN
  ▲                                                                │
  └────────── (success) ◄──────────────────────────────────────────┘
                                                                   │
                                                (failure) ─────────┘
```

### Java Implementation (Resilience4j)
```java
// Configure circuit breaker
CircuitBreakerConfig config = CircuitBreakerConfig.custom()
    .failureRateThreshold(50)                    // 50% failures opens circuit
    .waitDurationInOpenState(Duration.ofSeconds(30))
    .slidingWindowSize(100)
    .build();

CircuitBreaker circuitBreaker = CircuitBreaker.of("productService", config);

// Decorate call with circuit breaker
Supplier<List<Product>> decorated = CircuitBreaker.decorateSupplier(
    circuitBreaker, () -> productClient.getAllProducts()
);

// With fallback
List<Product> result = Try.ofSupplier(decorated)
    .recover(throwable -> fallbackProducts())
    .get();
```

## Retry Pattern

```java
@Retryable(
    value = {TransientException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2)
)
public Product getProduct(String id) {
    return productClient.call(id);  // may throw transient exception
}

// Configure Spring Retry
@EnableRetry
@Configuration
public class RetryConfig { /* ... */ }
```

## Health Checks

```java
@GetMapping("/health")
public ResponseEntity<Map<String, String>> health() {
    Map<String, String> status = new HashMap<>();
    status.put("status", "UP");
    status.put("db", checkDb() ? "UP" : "DOWN");
    status.put("cache", checkCache() ? "UP" : "DOWN");
    return ResponseEntity.ok(status);
}

// Spring Boot Actuator
// /actuator/health  — default health endpoint
// /actuator/health/liveness — Kubernetes liveness
// /actuator/health/readiness — Kubernetes readiness
```

## Graceful Shutdown

```java
@PreDestroy
public void shutdown() {
    // 1. Stop accepting new requests
    // 2. Complete in-flight requests (within timeout)
    // 3. Close connections (DB, queue, cache)
    // 4. Deregister from service registry
    System.out.println("Shutting down gracefully...");
}
```

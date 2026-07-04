# Availability - STEP BY STEP

## Implementing Circuit Breaker

### Step 1: Add Resilience4j Dependency
```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot2</artifactId>
</dependency>
```

### Step 2: Configure Circuit Breaker
```yaml
resilience4j.circuitbreaker:
  configs:
    default:
      sliding-window-size: 100
      failure-rate-threshold: 50
      wait-duration-in-open-state: 30s
      permitted-number-of-calls-in-half-open-state: 5
  instances:
    productService:
      base-config: default
```

### Step 3: Annotate Service
```java
@CircuitBreaker(name = "productService", fallbackMethod = "fallback")
public List<Product> getAllProducts() {
    return productClient.call();
}

public List<Product> fallback(Throwable t) {
    return Collections.emptyList();  // degraded response
}
```

## Setting Up Health Checks

### Step 1: Enable Actuator
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Step 2: Configure Probes
```yaml
management.endpoint.health.probes.enabled: true
management.health.livenessstate.enabled: true
management.health.readinessstate.enabled: true
```

### Step 3: Add Custom Health Indicator
```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        if (externalServiceIsAvailable()) {
            return Health.up().withDetail("service", "available").build();
        }
        return Health.down().withDetail("service", "unavailable").build();
    }
}
```

## Creating a Graceful Shutdown

### Step 1: Implement Shutdown Logic
```java
@Component
public class GracefulShutdown {
    @EventListener
    public void onShutdown(ContextClosedEvent event) {
        // 1. Mark as unhealthy (readiness fails)
        // 2. Wait for LB to drain connections
        // 3. Complete in-flight requests
        // 4. Close resources
    }
}
```

### Step 2: Configure in Spring
```yaml
server.shutdown: graceful
spring.lifecycle.timeout-per-shutdown-phase: 30s
```

## Implementing Retry with Backoff

### Step 1: Enable Retry
```java
@EnableRetry
@SpringBootApplication
public class Application { /* ... */ }
```

### Step 2: Add Retry Annotation
```java
@Retryable(
    retryFor = {TimeoutException.class, ServiceUnavailableException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 500, multiplier = 2, maxDelay = 5000)
)
public Product fetchProduct(String id) {
    return externalApi.getProduct(id);
}
```

## Setting Up Active-Passive Failover

### Step 1: Configure Two Data Sources
```yaml
spring.datasource.primary.url: jdbc:postgresql://primary:5432/db
spring.datasource.fallback.url: jdbc:postgresql://standby:5432/db
```

### Step 2: Implement Failover Logic
```java
public class FailoverRoutingDataSource extends AbstractRoutingDataSource {
    private volatile boolean isPrimaryDown = false;

    @Override
    protected Object determineCurrentLookupKey() {
        return isPrimaryDown ? "fallback" : "primary";
    }
}
```

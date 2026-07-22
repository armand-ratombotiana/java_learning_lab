# Prevention Guide — Avoiding Circuit Breaker Cascading Failures

## How to Prevent Recurrence of SEV1 Cascading Failures

This document outlines the engineering practices, architectural standards, and organizational changes required to prevent cascading failure incidents in microservice architectures. Drawing from Netflix's production experience, Google SRE principles, and Amazon's operational excellence practices.

---

## 1. Standardized Circuit Breaker Configuration

### Production Configuration Standards

All microservices MUST use the following minimum circuit breaker configuration:

| Parameter | Required Value | Enforcement |
|-----------|---------------|-------------|
| `failureRateThreshold` | 50 (max) | Pipeline gate rejects configs > 50 |
| `slidingWindowSize` | 10 (min), 20 (max) | Code review requirement |
| `minimumNumberOfCalls` | 5 | Default, do not override |
| `waitDurationInOpenState` | 30s (min) | Pipeline gate validates |
| `permittedNumberOfCallsInHalfOpenState` | 3 | Required field |
| `automaticTransitionFromOpenToHalfOpenEnabled` | true | Required |
| Timeout | 5s (max) | Pipeline gate validates |
| Fallback method | Required | Compilation check via annotation processor |

### Configuration Validation Pipeline Gate

```java
package com.example.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.util.Map;

public class CircuitBreakerConfigValidator {

    private static final int MAX_FAILURE_RATE_THRESHOLD = 50;
    private static final int MAX_TIMEOUT_MS = 5000;
    private static final int MIN_WAIT_DURATION_MS = 30000;

    public void validate(String serviceName, Map<String, Object> config) {
        Integer failureRate = (Integer) config.get("failureRateThreshold");
        if (failureRate != null && failureRate > MAX_FAILURE_RATE_THRESHOLD) {
            throw new ConfigValidationException(
                String.format("%s: failureRateThreshold=%d exceeds maximum %d",
                    serviceName, failureRate, MAX_FAILURE_RATE_THRESHOLD)
            );
        }

        Integer timeout = (Integer) config.get("timeout");
        if (timeout != null && timeout > MAX_TIMEOUT_MS) {
            throw new ConfigValidationException(
                String.format("%s: timeout=%dms exceeds maximum %dms",
                    serviceName, timeout, MAX_TIMEOUT_MS)
            );
        }
    }
}
```

---

## 2. Mandatory Bulkhead Isolation

### Bulkhead Requirements
- Every downstream dependency must have its own thread pool or semaphore
- Maximum 10 threads per dependency (configurable, requires SRE approval for >10)
- Queue capacity must not exceed 20
- Bulkhead metrics must be exposed via Micrometer/Prometheus

### Bulkhead Default Configuration Template

```yaml
resilience4j:
  thread-pool-bulkhead:
    configs:
      default:
        maxThreadPoolSize: 5
        coreThreadPoolSize: 3
        queueCapacity: 10
        keepAliveDuration: 30ms
```

---

## 3. Mandatory Fallback Implementation

### Fallback Policy
Every circuit breaker MUST have an associated fallback method. Fallbacks must:
1. Return a degraded but valid response type
2. Not throw exceptions (fallback failures handled within fallback)
3. Log the fallback invocation for monitoring
4. Include a clear indication that the response is degraded

### Fallback Patterns

| Dependency | Fallback Strategy | Response |
|------------|-------------------|----------|
| Payment Service | Queue for retry + mark order as PENDING_PAYMENT | `PaymentResult(status=PENDING)` |
| Inventory Service | Cache known stock levels, delay reservation | `InventoryReservation(status=PENDING)` |
| Shipping Service | Generate deferred label | `ShippingLabel(status=DEFERRED)` |
| Fraud Detection | Accept order with manual review flag | `FraudCheck(status=MANUAL_REVIEW)` |

---

## 4. Retry Policy Standards

### Allowed Retry Configuration
| Parameter | Max Value | Notes |
|-----------|-----------|-------|
| `maxRetryAttempts` | 2 | More than 2 requires SRE approval |
| `waitDuration` | 1s | Maximum initial wait |
| `exponentialBackoffMultiplier` | 2 | Standard exponential backoff |
| Retry on | `HttpServerErrorException`, `TimeoutException` | DO NOT retry on 4xx errors |

### Prohibited Retry Patterns
- Immediate retry without backoff
- Retry on client errors (4xx)
- Retry count > 3
- Retry on circuit breaker OPEN state (must fail fast)

---

## 5. Monitoring and Alerting Standards

### Mandatory Metrics
| Metric | Source | Alert Threshold |
|--------|--------|----------------|
| Circuit breaker state changes | Resilience4j Actuator | Any OPEN transition |
| Thread pool utilization | Micrometer/JVM | > 80% for 2 minutes |
| Fallback invocation count | Custom metric | > 100/minute |
| Retry count per request | Distributed tracing | > 2 retries |
| Request timeout count | Application metric | > 1% of requests |

### Alert Configuration (Prometheus)

```yaml
# prometheus-rules.yml
groups:
  - name: circuit-breaker-alerts
    rules:
      - alert: CircuitBreakerOpen
        expr: resilience4j_circuitbreaker_state{state="open"} == 1
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Circuit breaker {{ $labels.name }} is OPEN"
          description: "Circuit breaker {{ $labels.name }} has been OPEN for more than 1 minute"

      - alert: ThreadPoolHighUtilization
        expr: jvm_threads_daemon_threads / resilience4j_bulkhead_core_thread_pool_size > 0.8
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "Thread pool utilization > 80%"
          description: "Thread pool {{ $labels.name }} is at {{ $value }}% utilization"

      - alert: FallbackRateHigh
        expr: rate(resilience4j_fallback_invocations_total[5m]) > 100
        for: 1m
        labels:
          severity: warning
        annotations:
          summary: "High fallback invocation rate"
          description: "Fallback invoked {{ $value }} times per minute"
```

---

## 6. Chaos Engineering Requirements

### Mandatory Resilience Tests
| Test | Frequency | Success Criteria |
|------|-----------|-----------------|
| Single dependency latency injection | Weekly | Circuit breaker opens, fallback returns valid response |
| Multiple dependency failure | Monthly | Bulkhead isolation prevents cross-dependency failure |
| Circuit breaker HALF_OPEN recovery | Weekly | Automatic recovery succeeds |
| Thread pool exhaustion | Monthly | Bulkhead prevents exhaustion spreading |

### Chaos Engineering Implementation (using Chaos Monkey for Spring Boot)

```java
package com.example.infra.chaos;

import de.codecentric.spring.boot.chaos.monkey.assaults.LatencyAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.ExceptionAssault;
import org.springframework.stereotype.Component;

@Component
public class ResilienceTestOrchestrator {

    public void runWeeklyResilienceTests() {
        // Test 1: Inject 3s latency into payment service
        testLatencyInjection("paymentService", 3000);
        assertCircuitBreakerOpensWithin("paymentService", 30);

        // Test 2: Kill all payment service threads
        testThreadPoolExhaustion("paymentService");
        assertBulkheadIsolatesOtherDependencies("inventoryService", "shippingService");

        // Test 3: Verify fallback responses
        testServiceFailure("paymentService");
        assertFallbackReturnsValidResponse();
    }
}
```

---

## 7. Organizational Standards

### Service Ownership Requirements
- Each service team must define circuit breaker configuration for their downstream dependencies
- Cross-team review required for any circuit breaker configuration changes
- Quarterly resilience review with SRE team

### Training Requirements
- All backend engineers must complete Resilience4j training
- Annual chaos engineering workshop
- Incident response drills include cascading failure scenarios

### Review Board Requirements
- Architecture Review Board must approve:
  - Circuit breaker configuration deviations from standard
  - Timeout values > 5 seconds
  - Retry counts > 2
  - Services without fallback implementation

---

## Summary of Prevention Measures

| # | Measure | Owner | Timeline | Impact |
|---|---------|-------|----------|--------|
| 1 | Standardized circuit breaker config | Platform Team | Q3 2026 | Critical |
| 2 | Mandatory bulkhead pattern | Platform Team | Q3 2026 | Critical |
| 3 | Mandatory fallback methods | Service Teams | Q3 2026 | Critical |
| 4 | Circuit breaker state alerts | SRE Team | Q3 2026 | High |
| 5 | Retry policy standards | Platform Team | Q3 2026 | High |
| 6 | Chaos engineering program | QA Team | Q4 2026 | High |
| 7 | Resilience training | Engineering | Q4 2026 | Medium |

### References
- Netflix Tech Blog: "Fault Tolerance in a High Volume, Distributed System"
- Google SRE Book — Chapter 22: "Managing Cascading Failures"
- Resilience4j Documentation: "Best Practices"
- Microsoft Azure Architecture Center: "Bulkhead Pattern"
- AWS re:Invent 2018 — DOP302: "Prime Day 2018: Failure Analysis and Lessons Learned"

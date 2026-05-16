# Observability - THEORY

## Overview

Observability enables understanding system behavior through metrics, logs, and traces.

## 1. Three Pillars

### Logs
- **Application Logs**: Debug, info, warn, error
- **Access Logs**: Request/response logging
- **Audit Logs**: Security events

```java
@Slf4j
@RestController
public class UserController {
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        log.info("Fetching user: {}", id);
        try {
            return userService.findById(id);
        } catch (Exception e) {
            log.error("Failed to fetch user: {}", id, e);
            throw e;
        }
    }
}
```

### Metrics
- **Counters**: Request count, error count
- **Gauges**: Current connections, queue size
- **Histograms**: Request latency, response size

```java
@Service
public class MetricsService {
    private final MeterRegistry registry;
    
    public void recordLatency(long duration) {
        Timer.builder("request.latency")
            .tag("service", "user-service")
            .register(registry)
            .record(duration, TimeUnit.MILLISECONDS);
    }
    
    public void incrementError() {
        Counter.builder("request.errors")
            .tag("service", "user-service")
            .register(registry)
            .increment();
    }
}
```

### Traces
```java
@Traced
public User getUser(Long id) {
    Span span = tracer.startSpan("getUser");
    try {
        return userService.findById(id);
    } finally {
        span.end();
    }
}
```

## 2. Distributed Tracing

### Trace Flow
```
Client → Gateway → User Service → Database
   |        |           |
   └────────┴───────────┴── Trace ID: abc123
```

### Headers
```
X-Request-ID: abc123
X-Trace-ID: abc123
X-Span-ID: 456
```

## 3. Health Checks

```java
@RestController
@RequestMapping("/actuator")
public class HealthController {
    @GetMapping("/health")
    public Health health() {
        return Health.builder()
            .status(Status.UP)
            .withDetail("database", checkDatabase())
            .withDetail("redis", checkRedis())
            .build();
    }
}
```

## 4. Alerting

```yaml
# Prometheus alerting rules
groups:
- name: service-alerts
  rules:
  - alert: HighErrorRate
    expr: rate(http_errors_total[5m]) > 0.05
    for: 5m
    labels:
      severity: critical
    annotations:
      summary: High error rate detected
```

## Summary

1. **Logs**: Structured logging for debugging
2. **Metrics**: Quantitive measurements (Prometheus, Grafana)
3. **Traces**: Request flow across services (Jaeger, Zipkin)
4. **Health**: Readiness and liveness probes
5. **Alert**: Proactive monitoring with thresholds
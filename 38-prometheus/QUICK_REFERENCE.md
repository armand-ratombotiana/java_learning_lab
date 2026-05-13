# Quick Reference: Prometheus

<div align="center">

![Module](https://img.shields.io/badge/Module-38-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Prometheus-green?style=for-the-badge)

**Quick lookup guide for Prometheus metrics**

</div>

---

## 📋 Metric Types

| Type | Description |
|------|-------------|
| **Counter** | Monotonically increasing |
| **Gauge** | Can go up/down |
| **Histogram** | Buckets for latency |
| **Summary** | Quantiles (percentiles) |

---

## 🔑 Key Commands

### Prometheus Config
```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'myapp'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

### Query Examples
```promql
# HTTP request rate
rate(http_requests_total[5m])

# Error rate
sum(rate(http_requests_total{status="5xx"}[5m])) / sum(rate(http_requests_total[5m]))

# 99th percentile latency
histogram_quantile(0.99, sum(rate(http_request_duration_seconds_bucket[5m])) by (le))

# Active connections
sum(gauge_connections_active)
```

---

## 📊 Java Implementation

### Counter
```java
Counter counter = Counter.builder("app_requests_total")
    .description("Total requests")
    .labelNames("endpoint", "method")
    .register(registry);

counter.labels("/api/users", "GET").inc();
counter.labels("/api/users", "POST").inc();
```

### Gauge
```java
Gauge gauge = Gauge.builder("app_active_users")
    .description("Currently active users")
    .register(registry);

gauge.set(42);
gauge.inc();
gauge.dec();
```

### Histogram
```java
Histogram histogram = Histogram.builder("http_request_duration_seconds")
    .labelNames("method", "endpoint")
    .buckets(0.1, 0.5, 1.0, 2.5, 5.0, 10.0)
    .register(registry);

histogram.labels("GET", "/api/users").observe(0.342);
```

### Timer
```java
@Timed(value = "db_query_duration")
public User findById(Long id) {
    return repository.findById(id).orElse(null);
}
```

---

## 📊 Spring Boot Integration

### Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### Configuration
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,prometheus,metrics
  metrics:
    tags:
      application: myapp
```

---

## 📊 Custom Metrics

### Aspect
```java
@Aspect
@Component
public class MetricsAspect {
    @Around("@annotation(Monitored)")
    public Object measure(ProceedingJoinPoint pjp) throws Throwable {
        Timer.Sample sample = Timer.start(registry);
        try {
            return pjp.proceed();
        } finally {
            sample.stop(Timer.builder("method.execution")
                .tag("method", pjp.getSignature().toShortString())
                .register(registry));
        }
    }
}
```

---

## ✅ Best Practices

- Use appropriate metric types for data
- Add labels for dimensional data
- Set meaningful buckets for histograms
- Monitor both system and business metrics

### ❌ DON'T
- Don't use high cardinality labels
- Don't add metrics in hot paths without testing

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>
# Observability - STEP BY STEP

## Setting Up Structured Logging

### Step 1: Add Dependencies
```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

### Step 2: Configure Logback
```xml
<appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
</appender>
```

### Step 3: Add MDC Context
```java
@Component
public class MdcFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(...) {
        MDC.put("traceId", UUID.randomUUID().toString());
        chain.doFilter(request, response);
    }
}
```

### Step 4: Log with Context
```java
MDC.put("orderId", order.getId());
log.info("Order created");
```

## Adding Metrics

### Step 1: Add Actuator + Micrometer
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

### Step 2: Configure Metrics
```yaml
management.endpoints.web.exposure.include: health,metrics,prometheus
management.metrics.export.prometheus.enabled: true
```

### Step 3: Create Custom Metrics
```java
@Bean
public Counter orderCounter(MeterRegistry registry) {
    return Counter.builder("orders.created").register(registry);
}
```

## Setting Up Distributed Tracing

### Step 1: Add OpenTelemetry
```xml
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-api</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-spring-boot-starter</artifactId>
</dependency>
```

### Step 2: Configure Exporter
```yaml
otel:
  exporter:
    otlp:
      endpoint: http://jaeger:4317
  resource:
    attributes:
      service.name: order-service
```

### Step 3: Add @WithSpan
```java
@WithSpan
public Order createOrder(Order order) {
    return repository.save(order);
}
```

## Creating Alerts

### Step 1: Define Alert Rules
```yaml
groups:
  - name: api-alerts
    rules:
      - alert: HighErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.05
        for: 5m
        annotations:
          summary: "Error rate {{ $value }}"
```

### Step 2: Configure Alertmanager
```yaml
route:
  receiver: slack
  routes:
    - match:
        severity: critical
      receiver: pagerduty
```

### Step 3: Test Alerts
```bash
# Trigger a test alert
curl -X POST http://localhost:9093/api/v1/alerts \
  -H "Content-Type: application/json" \
  -d '[{"labels":{"alertname":"TestAlert","severity":"critical"}}]'
```

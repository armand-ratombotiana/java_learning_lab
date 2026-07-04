# Observability - HOW IT WORKS

## Structured Logging

### Logback Configuration
```xml
<configuration>
    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeMdc>true</includeMdc>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="JSON" />
    </root>
</configuration>
```

### Structured Log Output
```json
{
    "@timestamp": "2025-01-15T10:30:00.123Z",
    "level": "ERROR",
    "logger": "com.example.service.OrderService",
    "message": "Failed to process order",
    "service.name": "order-service",
    "traceId": "abc123def456",
    "spanId": "span789",
    "orderId": "ord-456",
    "customerId": "c-789",
    "error": "PaymentDeclinedException"
}
```

## Metrics Collection

### Micrometer + Prometheus
```java
@Configuration
public class MetricsConfig {
    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
}

@Service
public class OrderService {
    private final Counter orderCreated;
    private final Timer orderProcessing;

    public OrderService(MeterRegistry registry) {
        this.orderCreated = registry.counter("orders.created");
        this.orderProcessing = registry.timer("orders.processing");
    }

    public Order createOrder(Order order) {
        return orderProcessing.record(() -> {
            Order saved = orderRepository.save(order);
            orderCreated.increment();
            return saved;
        });
    }
}
```

### Custom Metrics
```java
@Component
public class OrderMetrics {
    private final DistributionSummary orderValue;

    public OrderMetrics(MeterRegistry registry) {
        this.orderValue = DistributionSummary.builder("orders.value")
            .description("Value of processed orders")
            .baseUnit("USD")
            .sla(10, 50, 100, 500)  // predefined buckets
            .register(registry);
    }

    public void recordOrder(BigDecimal amount) {
        orderValue.record(amount.doubleValue());
    }
}
```

## Distributed Tracing

### OpenTelemetry Setup
```xml
<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-spring-boot-starter</artifactId>
</dependency>
```

### Tracing Code
```java
@Service
public class OrderService {
    private final Tracer tracer;

    @WithSpan
    public Order createOrder(CreateOrderRequest request) {
        Span span = tracer.spanBuilder("validateOrder").startSpan();
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("order.customerId", request.customerId());
            validate(request);
            return saveOrder(request);
        } finally {
            span.end();
        }
    }
}
```

## Actuator Endpoints
```bash
/actuator/health      # Health information
/actuator/metrics     # View available metrics
/actuator/metrics/jvm.memory.used  # Specific metric
/actuator/prometheus  # Prometheus scrape endpoint
/actuator/loggers     # View/change log levels at runtime
```

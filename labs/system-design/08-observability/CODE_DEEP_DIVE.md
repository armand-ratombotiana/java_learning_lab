# Observability - CODE DEEP DIVE

## Table of Contents
1. [Structured Logging with MDC](#structured-logging)
2. [Metrics with Micrometer](#metrics)
3. [Distributed Tracing with OpenTelemetry](#tracing)
4. [Custom Dashboards and Alerts](#dashboards)

---

## 1. Structured Logging with MDC <a name="structured-logging"></a>

### MDC Filter for Correlation IDs
```java
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);
        MDC.put("service", "order-service");
        MDC.put("instance", System.getenv("HOSTNAME"));
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
```

### Structured Logging in Services
```java
@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        MDC.put("customerId", request.customerId());
        log.info("Starting order creation");

        try {
            Order order = new Order(request.customerId());
            order = orderRepository.save(order);
            MDC.put("orderId", order.getId());

            paymentService.processPayment(order);
            MDC.put("paymentStatus", "COMPLETED");

            log.info("Order created successfully");

            return order;
        } catch (Exception e) {
            log.error("Order creation failed: {}", e.getMessage(), e);
            throw e;
        } finally {
            MDC.remove("customerId");
            MDC.remove("orderId");
            MDC.remove("paymentStatus");
        }
    }
}
```

### Logback Configuration with JSON
```xml
<configuration>
    <springProperty name="service" source="spring.application.name" defaultValue="unknown"/>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeMdc>true</includeMdc>
            <customFields>{"service":"${service:-}","environment":"${ENV:-dev}"}</customFields>
        </encoder>
    </appender>

    <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="STDOUT" />
        <queueSize>512</queueSize>
        <discardingThreshold>0</discardingThreshold>
    </appender>

    <root level="INFO">
        <appender-ref ref="ASYNC" />
    </root>
</configuration>
```

### Dynamic Log Level Changes
```java
@RestController
@RequestMapping("/admin/loggers")
public class LogLevelController {

    @GetMapping
    public Map<String, Object> getLoggers() {
        return actuatorLoggers();
    }

    @PostMapping("/{logger}")
    public void setLogLevel(@PathVariable String logger,
            @RequestParam LogLevel level) {
        setLoggerLevel(logger, level);
    }
}

// Usage: POST /admin/loggers/com.example.service.OrderService?level=DEBUG
```

---

## 2. Metrics with Micrometer <a name="metrics"></a>

### Custom Metric Configuration
```java
@Configuration
public class CustomMetricsConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> commonTags() {
        return registry -> registry.config()
            .commonTags("application", "order-service",
                        "environment", System.getenv("ENV"));
    }

    @Bean
    public Counter orderCounter(MeterRegistry registry) {
        return Counter.builder("orders.total")
            .description("Total number of orders created")
            .tag("type", "all")
            .register(registry);
    }

    @Bean
    public Timer orderTimer(MeterRegistry registry) {
        return Timer.builder("orders.processing.time")
            .description("Time taken to process orders")
            .publishPercentiles(0.5, 0.95, 0.99)
            .publishPercentileHistogram()
            .sla(Duration.ofMillis(100), Duration.ofMillis(500), Duration.ofSeconds(1))
            .register(registry);
    }
}
```

### Business Metrics
```java
@Component
public class BusinessMetrics {
    private final Counter revenueCounter;
    private final DistributionSummary orderValueSummary;
    private final Gauge pendingOrders;
    private final AtomicInteger pendingOrdersCount = new AtomicInteger(0);

    public BusinessMetrics(MeterRegistry registry) {
        this.revenueCounter = registry.counter("revenue.total", "currency", "USD");
        this.orderValueSummary = DistributionSummary.builder("orders.value")
            .baseUnit("USD")
            .sla(25, 50, 100, 200, 500)
            .register(registry);
        this.pendingOrders = Gauge.builder("orders.pending", pendingOrdersCount,
                AtomicInteger::get)
            .description("Number of pending orders")
            .register(registry);
    }

    public void recordOrder(Order order) {
        revenueCounter.increment(order.getTotal().doubleValue());
        orderValueSummary.record(order.getTotal().doubleValue());
    }

    public void incrementPending() {
        pendingOrdersCount.incrementAndGet();
    }

    public void decrementPending() {
        pendingOrdersCount.decrementAndGet();
    }
}
```

### Metrics in Controllers
```java
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final Counter requestCounter;
    private final Counter errorCounter;
    private final Timer requestTimer;

    public OrderController(MeterRegistry registry) {
        this.requestCounter = Counter.builder("api.orders.requests")
            .tag("method", "POST")
            .register(registry);
        this.errorCounter = Counter.builder("api.orders.errors")
            .tag("method", "POST")
            .register(registry);
        this.requestTimer = Timer.builder("api.orders.duration")
            .tag("method", "POST")
            .register(registry);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        requestCounter.increment();
        return requestTimer.record(() -> {
            try {
                Order created = orderService.createOrder(order);
                return ResponseEntity.status(201).body(created);
            } catch (Exception e) {
                errorCounter.increment();
                throw e;
            }
        });
    }
}
```

---

## 3. Distributed Tracing with OpenTelemetry <a name="tracing"></a>

### OpenTelemetry Configuration
```java
@Configuration
public class TracingConfig {

    @Bean
    public SdkTracerProvider tracerProvider() {
        return SdkTracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(
                OtlpGrpcSpanExporter.builder()
                    .setEndpoint("http://jaeger:4317")
                    .build())
                .build())
            .setResource(Resource.getDefault()
                .toBuilder()
                .put(ResourceAttributes.SERVICE_NAME, "order-service")
                .build())
            .build();
    }

    @Bean
    public OpenTelemetry openTelemetry(SdkTracerProvider tracerProvider) {
        return OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .setPropagators(ContextPropagators.create(
                W3CTraceContextPropagator.getInstance()))
            .build();
    }
}
```

### Manual Instrumentation
```java
@Service
public class PaymentService {
    private final Tracer tracer;
    private final PaymentClient paymentClient;

    public PaymentService(OpenTelemetry openTelemetry, PaymentClient paymentClient) {
        this.tracer = openTelemetry.getTracer(PaymentService.class.getName());
        this.paymentClient = paymentClient;
    }

    public PaymentResult processPayment(Order order) {
        Span span = tracer.spanBuilder("processPayment")
            .setSpanKind(SpanKind.CLIENT)
            .setAttribute("order.id", order.getId())
            .setAttribute("payment.amount", order.getTotal().doubleValue())
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            PaymentResult result = paymentClient.charge(order);

            if (result.isSuccess()) {
                span.setStatus(StatusCode.OK);
                span.setAttribute("payment.status", "SUCCESS");
            } else {
                span.setStatus(StatusCode.ERROR, result.getDeclineReason());
                span.setAttribute("payment.decline", result.getDeclineReason());
            }

            return result;
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
}
```

### Inter-Service Trace Propagation
```java
@Component
public class TracingRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private final TextMapPropagator.Setter<HttpHeaders> setter =
        (headers, key, value) -> headers.set(key, value);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        OpenTelemetryContext context = Context.current();
        propagator.inject(context, request.getHeaders(), setter);
        return execution.execute(request, body);
    }
}

// Usage in RestTemplate
@Bean
public RestTemplate restTemplate() {
    RestTemplate template = new RestTemplate();
    template.setInterceptors(List.of(new TracingRestTemplateInterceptor()));
    return template;
}
```

### Async Trace Propagation
```java
@Component
public class AsyncTracingConfig {

    @Bean
    public Executor asyncExecutor() {
        return new TaskDecoratorExecutor(Runnable::run, Decorators::decorate);
    }

    @Bean
    public AsyncUncaughtExceptionHandler asyncExceptionHandler() {
        return (ex, method, params) -> log.error("Async error in {}", method.getName(), ex);
    }
}
```

---

## 4. Custom Dashboards and Alerts <a name="dashboards"></a>

### Prometheus Rules
```yaml
# prometheus-rules.yml
groups:
  - name: order-service
    rules:
      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) > 0.01
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High error rate on {{ $labels.instance }}"

      - alert: HighLatency
        expr: histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m])) > 2
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "P95 latency exceeds 2 seconds"

      - alert: InstanceDown
        expr: up{job="order-service"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Instance {{ $labels.instance }} is down"
```

### Health Metrics Endpoint
```java
@RestController
@RequestMapping("/internal")
public class HealthMetricsController {

    @GetMapping("/health/ready")
    public ResponseEntity<HealthStatus> readiness() {
        return ResponseEntity.ok(new HealthStatus(
            "UP",
            Map.of("database", checkDb(), "cache", checkCache())
        ));
    }

    @GetMapping("/health/live")
    public ResponseEntity<Void> liveness() {
        return ResponseEntity.ok().build();
    }
}
```

---

## Summary

This deep dive covered:
1. **Structured Logging**: JSON logging with MDC correlation IDs, dynamic log levels
2. **Micrometer Metrics**: Custom counters, timers, summary metrics, percentiles
3. **OpenTelemetry Tracing**: Span creation, propagation, async instrumentation
4. **Dashboards and Alerts**: Prometheus rules for error rate, latency, and availability

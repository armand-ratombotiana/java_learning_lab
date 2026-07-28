# Observability — Deep Interview Guide

## Table of Contents
1. [Observability Fundamentals](#observability-fundamentals)
2. [Distributed Tracing with OpenTelemetry](#distributed-tracing-with-opentelemetry)
3. [Metrics with Micrometer & Prometheus](#metrics-with-micrometer--prometheus)
4. [Structured Logging & Log Aggregation](#structured-logging--log-aggregation)
5. [Correlation IDs & Baggage Propagation](#correlation-ids--baggage-propagation)
6. [SLOs, SLIs, Error Budgets](#slos-slis-error-budgets)
7. [Java Code Examples](#java-code-examples)
8. [15+ Interview Questions](#15-interview-questions)

---

## Observability Fundamentals

Observability is the ability to understand the internal state of a system based on its external outputs. The three pillars are:

| Pillar | What | Why | Tools |
|--------|------|-----|-------|
| **Logs** | Discrete events with timestamps | Debugging specific failures | ELK, Loki, CloudWatch |
| **Metrics** | Aggregated numerical measurements | Trend analysis, alerting | Prometheus, Grafana |
| **Traces** | Request lifecycle across services | Performance bottlenecks | Jaeger, Zipkin, Tempo |

### Observability vs. Monitoring

| Aspect | Monitoring | Observability |
|--------|------------|---------------|
| **Focus** | Known unknowns | Unknown unknowns |
| **Approach** | Predefined dashboards/alerts | Exploratory analysis |
| **Data** | Aggregated metrics | High-cardinality events + traces |
| **Goal** | Detect known failure modes | Understand any state |

---

## Distributed Tracing with OpenTelemetry

OpenTelemetry (OTel) is a collection of APIs, SDKs, and tools for generating, collecting, and exporting telemetry data.

### Core Concepts

| Concept | Description |
|---------|-------------|
| **Span** | A single operation within a trace (has name, start/end time, attributes) |
| **Trace** | A tree of spans representing a complete request flow |
| **Context** | Propagation mechanism (trace ID, span ID, baggage) |
| **Tracer** | Creates spans for a specific instrumentation |
| **Exporter** | Sends telemetry to a backend (Jaeger, Zipkin, OTLP) |
| **Sampler** | Controls which traces are sampled (rate-based, head-based, tail-based) |
| **Instrumentation** | Automatic or manual code to create spans |

### Span Lifecycle

```
Span (parent)
├── Span (child 1): DB query
│   └── Span (child 1.1): connection pool wait
├── Span (child 2): HTTP call to service B
│   └── Span (child 2.1): serialization
└── Span (child 3): cache lookup
```

### Trace Context Propagation

```
Service A                    Service B                    Service C
    │                            │                            │
    ├─ Span A (trace_id=abc)     │                            │
    │    └─ HTTP GET /orders     │                            │
    │       │───────────────────→├─ Span B (trace_id=abc)     │
    │       │   traceparent:     │    └─ gRPC /validate       │
    │       │   00-abc-def-01    │       │───────────────────→├─ Span C (trace_id=abc)
    │       │                    │       │                    │    └─ DB query orders
    │       │                    │       │←───────────────────┤
    │       │←───────────────────┤                            │
    │                            │                            │
```

### W3C Trace Context

The `traceparent` header format: `00-<trace-id>-<span-id>-<trace-flags>`

```
00-0af7651916cd43dd8448eb211c80319c-b7ad6b7169203331-01
│ │        trace_id (16 bytes hex)    span_id (8 bytes hex)  │
│ │                                                          │
version (00)                                             flags (01=sampled)
```

### OpenTelemetry Protocol (OTLP)

OTLP is the native protocol for OpenTelemetry. It supports gRPC and HTTP transports, both protobuf-encoded. Exporter types: `OTLP gRPC`, `OTLP HTTP`, `OTLP File`.

---

## Metrics with Micrometer & Prometheus

### Micrometer

Micrometer provides a vendor-neutral facade for metrics. It's the metrics library used by Spring Boot Actuator.

**Core Concepts**:

| Concept | Interface | Description |
|---------|-----------|-------------|
| **MeterRegistry** | `MeterRegistry` | Creates and manages meters |
| **Counter** | `Counter` | Monotonically increasing value (requests, errors) |
| **Gauge** | `Gauge` | Single value that can go up/down (memory, queue size) |
| **Timer** | `Timer` | Measures duration and rate (latency) |
| **DistributionSummary** | `DistributionSummary` | Distribution of values (payload size) |
| **Tag** | `Tag` | Key-value dimension for filtering/aggregation |

### Prometheus

Prometheus is a time-series database and monitoring system that scrapes metrics from HTTP endpoints.

```
Prometheus Server
    │
    ├─ scrape /actuator/prometheus ─→ Service A
    ├─ scrape /actuator/prometheus ─→ Service B
    └─ scrape /actuator/prometheus ─→ Service C
    │
    ├─ Alertmanager (alerts)
    ├─ Grafana (dashboards)
    └─ Recording Rules (aggregations)
```

### Common Metrics Naming Convention

```
<domain>_<component>_<metric>_<unit>
```

Examples:
- `http_server_requests_seconds_count`
- `jvm_memory_used_bytes`
- `db_connections_active_total`
- `order_service_created_total`

---

## Structured Logging & Log Aggregation

### Structured Logging

Instead of plain text, structured logging outputs JSON or key=value pairs.

```
Plain text:
2024-01-15 10:30:00 ERROR Order 123 failed: Connection timeout

Structured (JSON):
{"timestamp":"2024-01-15T10:30:00Z","level":"ERROR",
 "logger":"com.example.OrderService","message":"Order processing failed",
 "orderId":"123","duration_ms":5032,"error":"Connection timeout",
 "traceId":"abc123","userId":"user456"}
```

### Logback JSON Configuration

```xml
<appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="net.logstash.logback.encoder.LogstashEncoder">
        <includeMdcKeyName>correlationId</includeMdcKeyName>
        <includeMdcKeyName>userId</includeMdcKeyName>
        <includeMdcKeyName>requestId</includeMdcKeyName>
        <customFields>{"application":"order-service","environment":"production"}</customFields>
    </encoder>
</appender>
```

### Log Aggregation

**ELK Stack**: Elasticsearch (storage + search), Logstash (processing), Kibana (visualization).

**Loki**: Grafana's log aggregation system, designed to be cost-effective (indexes labels, not full text).

**Fluentd/Vector**: Log collectors and processors.

---

## Correlation IDs & Baggage Propagation

### Correlation ID Pattern

A correlation ID is a unique identifier attached to every request, propagated across service boundaries.

```
1. API Gateway generates correlation ID
2. Passed via HTTP header: X-Correlation-Id
3. Stored in MDC (Mapped Diagnostic Context)
4. Included in all log statements
5. Passed to downstream services via gRPC metadata or HTTP headers
6. Enables correlating logs across services for a single request
```

### OpenTelemetry Baggage

Baggage is key-value metadata that travels with the trace context.

```java
// Set baggage
Baggage.current().toBuilder()
    .put("userId", "user123")
    .put("sessionId", "sess456")
    .build()
    .makeCurrent();

// Read baggage
String userId = Baggage.current()
    .getEntryValue("userId");
```

### W3C Baggage Header

```
baggage: userId=user123,sessionId=sess456,featureFlag=dark-mode
```

---

## SLOs, SLIs, Error Budgets

### Definitions

| Term | Definition | Example |
|------|------------|---------|
| **SLI** (Service Level Indicator) | A quantifiable metric | Request latency p99 < 500ms |
| **SLO** (Service Level Objective) | Target value for an SLI | 99.9% of requests < 500ms |
| **SLA** (Service Level Agreement) | Contract with customers | 99.95% uptime guarantee |
| **Error Budget** | Allowed failure | 100% - SLO = 0.1% error budget |

### Error Budget Calculation

```
Error Budget = 1 - SLO
Monthly Error Budget = Total minutes × (1 - SLO)

Example:
99.9% SLO → 0.1% error budget → ~43 minutes/month of allowed downtime
99.99% SLO → 0.01% error budget → ~4.3 minutes/month of allowed downtime
```

### Burn Rate

Burn rate measures how fast the error budget is consumed:

| Burn Rate | SLO Window | Alert |
|-----------|------------|-------|
| 1x | 30 days | Warning — consuming budget at expected rate |
| 2x | 7 days | Page — consuming budget 2x faster than expected |
| 10x | 2 hours | Critical — potential imminent SLO breach |

---

## Java Code Examples

### 1. OpenTelemetry Instrumentation

```java
// OpenTelemetryConfig.java
package com.example.observability.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.semconv.ResourceAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenTelemetryConfig {

    @Bean
    public OpenTelemetry openTelemetry() {
        Resource resource = Resource.getDefault()
            .merge(Resource.create(Attributes.of(
                ResourceAttributes.SERVICE_NAME, "order-service",
                ResourceAttributes.SERVICE_VERSION, "1.0.0",
                ResourceAttributes.DEPLOYMENT_ENVIRONMENT, "production"
            )));

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .setResource(resource)
            .setSampler(Sampler.parentBased(
                Sampler.traceIdRatioBased(0.1)))  // 10% sampling
            .addSpanProcessor(BatchSpanProcessor.builder(
                    OtlpGrpcSpanExporter.builder()
                        .setEndpoint("http://otel-collector:4317")
                        .setTimeout(Duration.ofSeconds(30))
                        .build())
                .setScheduleDelay(Duration.ofSeconds(5))
                .setMaxExportBatchSize(512)
                .build())
            .build();

        return OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .setPropagators(ContextPropagators.create(
                io.opentelemetry.context.propagation
                    .W3CTraceContextPropagator.getInstance()))
            .build();
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("com.example.order-service", "1.0.0");
    }
}
```

```java
// Manual Instrumentation Service
// OrderProcessingService.java
package com.example.observability.service;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderProcessingService {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessingService.class);

    private final Tracer tracer;
    private final TextMapPropagator propagator;
    private final WebClient webClient;

    public OrderProcessingService(OpenTelemetry openTelemetry,
                                  WebClient.Builder webClientBuilder) {
        this.tracer = openTelemetry.getTracer(OrderProcessingService.class.getName());
        this.propagator = openTelemetry.getPropagators().getTextMapPropagator();
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public Mono<String> processOrder(String orderId) {
        Span span = tracer.spanBuilder("processOrder")
            .setSpanKind(SpanKind.SERVER)
            .setAttribute("orderId", orderId)
            .setAttribute("component", "order-service")
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Add correlation ID to MDC for logging
            MDC.put("traceId", span.getSpanContext().getTraceId());
            MDC.put("orderId", orderId);

            // Add to baggage
            Baggage.current().toBuilder()
                .put("orderId", orderId)
                .build()
                .makeCurrent();

            log.info("Processing order: {}", orderId);

            // Step 1: Validate order
            Span validateSpan = tracer.spanBuilder("validateOrder")
                .setSpanKind(SpanKind.INTERNAL)
                .setAttribute("orderId", orderId)
                .startSpan();

            try {
                validateOrder(orderId);
            } catch (Exception e) {
                validateSpan.recordException(e);
                validateSpan.setStatus(StatusCode.ERROR, e.getMessage());
                throw e;
            } finally {
                validateSpan.end();
            }

            // Step 2: Charge payment with propagation
            Span paymentSpan = tracer.spanBuilder("chargePayment")
                .setSpanKind(SpanKind.CLIENT)
                .setAttribute("orderId", orderId)
                .startSpan();

            try (Scope paymentScope = paymentSpan.makeCurrent()) {
                String result = chargePayment(orderId).block();
                return result;
            } catch (Exception e) {
                paymentSpan.recordException(e);
                paymentSpan.setStatus(StatusCode.ERROR, e.getMessage());
                throw e;
            } finally {
                paymentSpan.end();
            }

        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            log.error("Failed to process order: {}", orderId, e);
            throw new RuntimeException("Order processing failed", e);
        } finally {
            span.end();
            MDC.clear();
        }
    }

    private void validateOrder(String orderId) {
        // Simulate validation
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Mono<String> chargePayment(String orderId) {
        Span span = Span.current();

        return webClient.post()
            .uri("/api/payments/charge/{orderId}", orderId)
            .headers(headers -> {
                // Propagate trace context
                propagator.inject(Context.current(), headers,
                    (TextMapSetter<org.springframework.http.HttpHeaders>)
                        (carrier, key, value) -> carrier.set(key, value));
            })
            .retrieve()
            .bodyToMono(String.class)
            .doOnSuccess(result -> {
                span.setAttribute("payment.result", result);
                log.info("Payment successful for order: {}", orderId);
            })
            .doOnError(error -> {
                span.recordException(error);
                span.setStatus(StatusCode.ERROR, error.getMessage());
                log.error("Payment failed for order: {}", orderId, error);
            });
    }
}
```

### 2. Custom Micrometer Metrics

```java
// OrderMetricsService.java
package com.example.observability.metrics;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderMetricsService {

    private static final Logger log = LoggerFactory.getLogger(OrderMetricsService.class);

    private final MeterRegistry meterRegistry;

    // Counters
    private final Counter ordersCreated;
    private final Counter ordersFailed;
    private final Counter ordersCancelled;

    // Timer
    private final Timer orderProcessingTimer;

    // Gauge
    private final AtomicInteger activeOrders = new AtomicInteger(0);

    // Distribution summary
    private final DistributionSummary orderValueSummary;

    public OrderMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Counter for created orders
        this.ordersCreated = Counter.builder("orders.created.total")
            .description("Total number of orders created")
            .tag("service", "order-service")
            .register(meterRegistry);

        // Counter for failed orders with error type tag
        this.ordersFailed = Counter.builder("orders.failed.total")
            .description("Total number of failed orders")
            .tag("service", "order-service")
            .register(meterRegistry);

        // Counter for cancelled orders
        this.ordersCancelled = Counter.builder("orders.cancelled.total")
            .description("Total number of cancelled orders")
            .tag("service", "order-service")
            .register(meterRegistry);

        // Timer for order processing duration
        this.orderProcessingTimer = Timer.builder("orders.processing.duration")
            .description("Time taken to process an order")
            .tag("service", "order-service")
            .publishPercentiles(0.5, 0.95, 0.99)
            .publishPercentileHistogram()
            .register(meterRegistry);

        // Gauge for active orders
        Gauge.builder("orders.active", activeOrders, AtomicInteger::get)
            .description("Number of orders currently being processed")
            .tag("service", "order-service")
            .register(meterRegistry);

        // Distribution summary for order values
        this.orderValueSummary = DistributionSummary.builder("orders.value")
            .description("Distribution of order values")
            .tag("service", "order-service")
            .publishPercentiles(0.5, 0.75, 0.9, 0.99)
            .baseUnit("dollars")
            .register(meterRegistry);

        // Custom gauge using a lambda
        Gauge.builder("orders.pending.queue.size",
                this::getPendingQueueSize)
            .description("Number of orders in the pending queue")
            .tag("service", "order-service")
            .register(meterRegistry);
    }

    public void recordOrderCreated(String orderType, double value) {
        ordersCreated.increment();
        orderValueSummary.record(value);
        log.debug("Recorded order created, type={}, value={}", orderType, value);

        // Dynamic counter with extra tag
        Counter.builder("orders.created.by_type")
            .tag("type", orderType)
            .register(meterRegistry)
            .increment();
    }

    public void recordOrderFailed(String errorType) {
        ordersFailed.increment();

        // Counter with error type tag
        Counter.builder("orders.failed.by_error")
            .tag("error", errorType)
            .register(meterRegistry)
            .increment();
    }

    public void recordOrderCancelled(String reason) {
        ordersCancelled.increment();

        Counter.builder("orders.cancelled.by_reason")
            .tag("reason", reason)
            .register(meterRegistry)
            .increment();
    }

    public <T> T measureOrderProcessing(String orderType,
                                        java.util.function.Supplier<T> operation) {
        activeOrders.incrementAndGet();
        try {
            Timer.Sample sample = Timer.start(meterRegistry);
            T result = operation.get();
            sample.stop(orderProcessingTimer);
            return result;
        } finally {
            activeOrders.decrementAndGet();
        }
    }

    public void measureOrderProcessingRunnable(String orderType, Runnable operation) {
        activeOrders.incrementAndGet();
        try {
            Timer.Sample sample = Timer.start(meterRegistry);
            operation.run();
            sample.stop(orderProcessingTimer);
        } finally {
            activeOrders.decrementAndGet();
        }
    }

    private double getPendingQueueSize() {
        // In real app, query queue size from messaging system
        return Math.random() * 100;
    }

    // Custom meter with multiple dimensions
    public void recordOrderProcessingTime(String orderType, String region,
                                          long durationMs) {
        Timer.builder("orders.processing.duration")
            .tag("order_type", orderType)
            .tag("region", region)
            .register(meterRegistry)
            .record(durationMs, TimeUnit.MILLISECONDS);
    }
}
```

```java
// MetricsController.java
package com.example.observability.controller;

import com.example.observability.metrics.OrderMetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.search.Search;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final OrderMetricsService orderMetricsService;
    private final MeterRegistry meterRegistry;

    public MetricsController(OrderMetricsService orderMetricsService,
                             MeterRegistry meterRegistry) {
        this.orderMetricsService = orderMetricsService;
        this.meterRegistry = meterRegistry;
    }

    @PostMapping("/orders")
    public Map<String, Object> createOrder(@RequestParam String type,
                                           @RequestParam double value) {
        orderMetricsService.recordOrderCreated(type, value);
        return Map.of("status", "recorded", "type", type, "value", value);
    }

    @PostMapping("/orders/fail")
    public Map<String, Object> failOrder(@RequestParam String errorType) {
        orderMetricsService.recordOrderFailed(errorType);
        return Map.of("status", "recorded", "error", errorType);
    }

    @GetMapping("/orders/process/sync")
    public Map<String, Object> processOrderSync(@RequestParam String type) {
        long duration = orderMetricsService.measureOrderProcessing(type, () -> {
            try {
                Thread.sleep(100 + (long) (Math.random() * 400));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return 100L + (long) (Math.random() * 900);
        });
        return Map.of("status", "processed", "type", type, "duration_ms", duration);
    }

    @GetMapping("/snapshot")
    public Map<String, Object> getMetricsSnapshot() {
        Map<String, Object> snapshot = new HashMap<>();

        Search.in(meterRegistry).meters().forEach(meter -> {
            String name = meter.getId().getName();
            Map<String, String> tags = new HashMap<>();
            meter.getId().getTags().forEach(tag ->
                tags.put(tag.getKey(), tag.getValue()));

            snapshot.put(name, Map.of(
                "tags", tags,
                "type", meter.getId().getType().name(),
                "measurements", meter.measure().stream()
                    .map(m -> Map.of(
                        "statistic", m.getStatistic().name(),
                        "value", m.getValue()))
                    .toList()
            ));
        });

        return snapshot;
    }
}
```

### 3. MDC Logging with Correlation ID

```java
// CorrelationIdFilter.java
package com.example.observability.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class CorrelationIdFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Extract or generate correlation ID
        String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        // Set in MDC for logging
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
        MDC.put("requestUri", httpRequest.getRequestURI());
        MDC.put("method", httpRequest.getMethod());

        // Set response header
        httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            chain.doFilter(request, response);
        } finally {
            // Clean up MDC
            MDC.clear();
        }
    }
}
```

```java
// WebClient Correlation ID Interceptor
package com.example.observability.webclient;

import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class CorrelationIdClientInterceptor implements ClientHttpRequestInterceptor {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution)
            throws IOException {
        String correlationId = MDC.get(CORRELATION_ID_MDC_KEY);
        if (correlationId != null) {
            request.getHeaders().set(CORRELATION_ID_HEADER, correlationId);
        }
        return execution.execute(request, body);
    }
}
```

```java
// LoggingService.java — Structured Logging Helper
package com.example.observability.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Helper for structured logging with consistent fields.
 */
@Service
public class LoggingService {

    private static final Logger log = LoggerFactory.getLogger(LoggingService.class);

    private final ObjectMapper objectMapper;

    public LoggingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void logEvent(String eventType, String message, Map<String, Object> extraFields) {
        Map<String, Object> structuredLog = new LinkedHashMap<>();
        structuredLog.put("event", eventType);
        structuredLog.put("message", message);
        structuredLog.put("timestamp", java.time.Instant.now().toString());

        // Add MDC context
        if (MDC.get("correlationId") != null) {
            structuredLog.put("correlationId", MDC.get("correlationId"));
        }
        if (MDC.get("userId") != null) {
            structuredLog.put("userId", MDC.get("userId"));
        }
        if (MDC.get("requestUri") != null) {
            structuredLog.put("requestUri", MDC.get("requestUri"));
        }

        if (extraFields != null) {
            structuredLog.putAll(extraFields);
        }

        try {
            log.info(objectMapper.writeValueAsString(structuredLog));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize structured log", e);
        }
    }

    public void logError(String eventType, String message,
                         Throwable error, Map<String, Object> extraFields) {
        Map<String, Object> structuredLog = new LinkedHashMap<>();
        structuredLog.put("event", eventType);
        structuredLog.put("message", message);
        structuredLog.put("timestamp", java.time.Instant.now().toString());
        structuredLog.put("error", error.getMessage());
        structuredLog.put("errorClass", error.getClass().getName());

        if (MDC.get("correlationId") != null) {
            structuredLog.put("correlationId", MDC.get("correlationId"));
        }

        if (extraFields != null) {
            structuredLog.putAll(extraFields);
        }

        try {
            log.error(objectMapper.writeValueAsString(structuredLog), error);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize structured log", e);
        }
    }
}
```

### 4. Health Check with Custom Indicators

```java
// CustomHealthIndicators.java
package com.example.observability.health;

import org.springframework.boot.actuate.health.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("orderServiceHealth")
public class OrderServiceHealthIndicator implements HealthIndicator {

    private final Map<String, Boolean> dependencies = new ConcurrentHashMap<>();

    public OrderServiceHealthIndicator() {
        dependencies.put("database", true);
        dependencies.put("payment-service", true);
        dependencies.put("inventory-service", true);
    }

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();

        if (dependencies.values().stream().allMatch(v -> v)) {
            builder.up();
        } else {
            builder.down();
        }

        builder.withDetail("dependencies", dependencies);
        builder.withDetail("activeOrders", 42);
        builder.withDetail("version", "1.0.0");

        return builder.build();
    }

    public void setDependencyStatus(String dependency, boolean healthy) {
        dependencies.put(dependency, healthy);
    }
}
```

```java
// Composite Health Check
package com.example.observability.health;

import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.NamedContributor;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SystemHealthContributor implements CompositeHealthContributor {

    private final Map<String, HealthContributor> contributors = new LinkedHashMap<>();

    public SystemHealthContributor(
            OrderServiceHealthIndicator orderHealth,
            DatabaseHealthIndicator dbHealth) {
        contributors.put("orderService", orderHealth);
        contributors.put("database", dbHealth);
    }

    @Override
    public HealthContributor getContributor(String name) {
        return contributors.get(name);
    }

    @Override
    public Iterator<NamedContributor<HealthContributor>> iterator() {
        return contributors.entrySet().stream()
            .map(entry -> NamedContributor.of(entry.getKey(), entry.getValue()))
            .iterator();
    }
}
```

### 5. Prometheus Metrics Export and Custom Endpoint

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  metrics:
    tags:
      application: order-service
      environment: ${ENV:development}
    export:
      prometheus:
        enabled: true
        step: 30s
    distribution:
      percentiles-histogram:
        http.server.requests: true
        orders.processing.duration: true
      slo:
        http.server.requests: 10ms, 50ms, 100ms, 200ms, 500ms, 1s, 2s
  endpoint:
    prometheus:
      enabled: true
    metrics:
      enabled: true
```

```java
// PrometheusCustomMetrics.java
package com.example.observability.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.List;

@Component
public class PrometheusCustomMetrics {

    private static final Logger log = LoggerFactory.getLogger(PrometheusCustomMetrics.class);

    private final MeterRegistry meterRegistry;
    private final OperatingSystemMXBean osBean;

    public PrometheusCustomMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
    }

    @PostConstruct
    public void registerMetrics() {
        log.info("Registering custom Prometheus metrics");

        // JVM-specific custom metrics
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
            meterRegistry.gauge("os.process.cpu.load",
                List.of(Tag.of("type", "process")),
                sunOsBean,
                com.sun.management.OperatingSystemMXBean::getProcessCpuLoad);

            meterRegistry.gauge("os.system.cpu.load",
                List.of(Tag.of("type", "system")),
                sunOsBean,
                com.sun.management.OperatingSystemMXBean::getCpuLoad);
        }

        // Business metrics with constant tags
        meterRegistry.gauge("business.orders.pending",
            List.of(Tag.of("priority", "high")), this, s -> getPendingCount("high"));
        meterRegistry.gauge("business.orders.pending",
            List.of(Tag.of("priority", "normal")), this, s -> getPendingCount("normal"));
        meterRegistry.gauge("business.orders.pending",
            List.of(Tag.of("priority", "low")), this, s -> getPendingCount("low"));
    }

    private double getPendingCount(String priority) {
        // In real application, query from database or queue
        return Math.random() * 100;
    }
}
```

### 6. Distributed Tracing with Automatic Instrumentation

```java
// Application.java — With OpenTelemetry Java Agent
// Run with: -javaagent:opentelemetry-javaagent.jar
//           -Dotel.service.name=order-service
//           -Dotel.traces.exporter=otlp
//           -Dotel.exporter.otlp.endpoint=http://otel-collector:4317
//           -Dotel.metrics.exporter=prometheus

package com.example.observability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ObservabilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObservabilityApplication.class, args);
    }
}
```

### 7. SLO Monitoring Service

```java
// SloMonitorService.java
package com.example.observability.slo;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SloMonitorService {

    private static final Logger log = LoggerFactory.getLogger(SloMonitorService.class);

    private final MeterRegistry meterRegistry;

    // SLI accumulators
    private final AtomicLong totalRequests = new AtomicLong();
    private final AtomicLong successfulRequests = new AtomicLong();
    private final AtomicLong fastRequests = new AtomicLong();
    private final Map<String, SloDefinition> sloDefinitions = new ConcurrentHashMap<>();

    public SloMonitorService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Define SLOs
        sloDefinitions.put("availability", new SloDefinition(
            "Availability", 0.999, "proportion", "30d"));
        sloDefinitions.put("latency", new SloDefinition(
            "Latency p99 < 500ms", 0.99, "proportion", "30d"));
        sloDefinitions.put("durability", new SloDefinition(
            "Message durability", 0.9999, "proportion", "30d"));

        // Register SLO metrics
        sloDefinitions.forEach((name, slo) -> {
            meterRegistry.gauge("slo.target",
                List.of(Tag.of("slo", name)),
                slo, SloDefinition::target);

            meterRegistry.gauge("slo.error_budget_remaining",
                List.of(Tag.of("slo", name)),
                this, svc -> svc.calculateErrorBudgetRemaining(name));
        });
    }

    public void recordRequest(boolean success, long durationMs) {
        totalRequests.incrementAndGet();
        if (success) {
            successfulRequests.incrementAndGet();
        }
        if (durationMs < 500) {
            fastRequests.incrementAndGet();
        }
    }

    public double getAvailabilitySli() {
        long total = totalRequests.get();
        if (total == 0) return 1.0;
        return (double) successfulRequests.get() / total;
    }

    public double getLatencySli() {
        long total = totalRequests.get();
        if (total == 0) return 1.0;
        return (double) fastRequests.get() / total;
    }

    public double calculateErrorBudgetRemaining(String sloName) {
        SloDefinition slo = sloDefinitions.get(sloName);
        if (slo == null) return 1.0;

        double currentSli = switch (sloName) {
            case "availability" -> getAvailabilitySli();
            case "latency" -> getLatencySli();
            default -> 1.0;
        };

        // Error budget = 1 - SLO target
        // Burn rate = (1 - SLI) / (1 - SLO target)
        double errorBudget = 1.0 - slo.target();
        double consumed = 1.0 - currentSli;

        if (errorBudget <= 0) return 1.0;
        return Math.max(0, 1.0 - (consumed / errorBudget));
    }

    @Scheduled(cron = "0 */5 * * * *") // Every 5 minutes
    public void evaluateSlos() {
        log.info("=== SLO Evaluation ===");
        sloDefinitions.forEach((name, slo) -> {
            double remaining = calculateErrorBudgetRemaining(name);
            String status = remaining > 0.5 ? "HEALTHY"
                : remaining > 0.2 ? "WARNING"
                : remaining > 0 ? "CRITICAL"
                : "BREACHED";

            log.info("SLO: {} | Target: {} | Budget remaining: {}% | Status: {}",
                name, slo.target(), String.format("%.2f", remaining * 100), status);

            if (remaining <= 0) {
                log.warn("SLO BREACHED: {} — immediate action required!", name);
            }
        });
    }

    record SloDefinition(String description, double target,
                         String type, String window) {}
}
```

---

## 15+ Interview Questions

### Q1: What are the three pillars of observability and how do they differ?
**Answer**: The three pillars are **logs** (discrete events with timestamps — "I am here, this happened"), **metrics** (aggregated numerical measurements — "how many, how long, how full"), and **traces** (request lifecycle across services — "what happened, in what order, how long each step took"). They are complementary — logs give you details, metrics give you trends, and traces give you causality. Modern observability aims to correlate all three.

### Q2: How does distributed tracing work with OpenTelemetry?
**Answer**: OpenTelemetry uses **spans** (named, timed operations) connected into a **trace** tree. Each span has a trace ID (shared across requests) and a span ID. **Context propagation** carries the trace context across service boundaries via W3C `traceparent` headers. **Samplers** decide which traces to record (head-based: sample at start; tail-based: sample after completion). **Exporters** send spans to backends (Jaeger, Zipkin, OTLP collector).

### Q3: What is the difference between OpenTelemetry and OpenTracing/OpenCensus?
**Answer**: OpenTracing and OpenCensus were competing standards. **OpenTelemetry** merged them into a single standard. It provides: a unified API (traces, metrics, logs), standard context propagation, automatic instrumentation for popular libraries, a collector for processing/exporting, and broad vendor support. OpenTracing had only traces; OpenCensus had traces + metrics but limited support. OpenTelemetry is the CNCF standard.

### Q4: Explain Micrometer's role in Spring Boot metrics.
**Answer**: Micrometer is a metrics facade (like SLF4J for metrics). Spring Boot Actuator uses Micrometer to instrument the application. It supports various registries (Prometheus, Datadog, Graphite, InfluxDB). Key meters: `Counter` (increment-only count), `Gauge` (up/down value), `Timer` (duration distribution), `DistributionSummary` (value distribution). Spring Boot auto-configures `MeterRegistryCustomizer` beans and provides pre-built metrics (JVM, threads, HTTP requests, data sources).

### Q5: How does Prometheus scrape metrics and what is the pull model?
**Answer**: Prometheus uses a **pull model** — it scrapes (HTTP GETs) metrics endpoints at regular intervals. The application exposes `/actuator/prometheus` or `/metrics` endpoint. Advantages: easier to detect dead services, central control of scrape intervals, no need for push infrastructure. Disadvantages: services must be network-reachable, configuration needed for dynamic environments. Alternatives: Pushgateway (for batch jobs), Prometheus remote write (for push-based).

### Q6: What is the W3C trace context and why is it important?
**Answer**: W3C Trace Context is a standard for passing trace information across service boundaries. The `traceparent` header contains trace ID, span ID, and trace flags. The `tracestate` header carries vendor-specific data. Standardization ensures interoperability between different tracing systems (e.g., service A using OpenTelemetry can propagate to service B using Jaeger). Without it, every vendor had incompatible headers.

### Q7: What are the different sampling strategies in distributed tracing?
**Answer**: (1) **Head-based sampling** — decision at the start of the trace (e.g., rate-limited, probability-based). Simple but may miss rare errors. (2) **Tail-based sampling** — decision after the trace is complete (e.g., sample all errors, rate-limit successes). More accurate but requires buffering. (3) **Parent-based** — child spans inherit the sampling decision from the parent. (4) **Consistent probability sampling** — uses trace ID hash to get consistent results across services.

### Q8: Explain correlation IDs and how they enable debugging.
**Answer**: A **correlation ID** is a unique identifier assigned to each incoming request and propagated to all downstream calls and log statements. It enables: (1) Searching logs across services for a single user request; (2) Linking traces, logs, and metrics for the same request; (3) Tracking request flow from API gateway to backend services; (4) Measuring end-to-end latency. Implementation: filter generates/reads `X-Correlation-Id` header, stores in MDC, includes in all log statements, propagates via HTTP/gRPC headers.

### Q9: What is the difference between SLO, SLI, and SLA?
**Answer**: **SLI** (Service Level Indicator) is a quantifiable metric (e.g., request latency p99). **SLO** (Service Level Objective) is a target for the SLI (e.g., 99.9% of requests under 500ms). **SLA** (Service Level Agreement) is a contractual commitment with consequences (e.g., 99.95% uptime or refund). SLAs are external, SLOs are internal targets. Error budget = 100% - SLO → allowed failure rate.

### Q10: How do you implement logging best practices in a microservices architecture?
**Answer**: (1) **Structured logging** — output JSON with consistent field names. (2) **Correlation IDs** — propagate across service boundaries. (3) **Log levels** — use appropriately (ERROR for failures needing attention, WARN for potential issues, INFO for important events, DEBUG for troubleshooting). (4) **No sensitive data** — never log passwords, tokens, PII. (5) **Contextual data** — include service name, trace ID, user ID in every log. (6) **Centralized aggregation** — ship all logs to ELK/Loki.

### Q11: What is OpenTelemetry baggage and how is it different from trace context?
**Answer**: **Trace context** carries the trace ID, span ID, and sampling decision — it's for distributed tracing. **Baggage** carries arbitrary key-value metadata across service boundaries. Use baggage for: user ID, session ID, feature flags, tenant ID. Important: baggage is propagated everywhere and can impact performance and security. It should contain only low-cardinality, non-sensitive data. Accessed via `Baggage.current().getEntryValue("key")`.

### Q12: How does Spring Boot Actuator support observability?
**Answer**: Spring Boot Actuator provides: (1) **Health** — `/actuator/health` shows system health (liveness + readiness probes); (2) **Metrics** — `/actuator/metrics` exposes Micrometer metrics; (3) **Prometheus** — `/actuator/prometheus` exposes Prometheus-formatted metrics; (4) **Info** — `/actuator/info` shows application info; (5) **Loggers** — `/actuator/loggers` allows runtime log level changes; (6) **Mappings** — `/actuator/mappings` shows route mappings; (7) **Caches** — `/actuator/caches` shows cache metrics.

### Q13: What is the role of the OpenTelemetry Collector?
**Answer**: The OpenTelemetry Collector is a vendor-agnostic agent/processor for telemetry data. It receives telemetry from applications (via OTLP), processes it (filtering, sampling, transformation, batching), and exports it to one or more backends (Jaeger, Prometheus, Loki, Datadog, etc.). Modes: **Agent** (runs alongside applications), **Gateway** (standalone cluster). Benefits: decouples instrumentation from backends, reduces application overhead, enables tail-based sampling.

### Q14: How do you measure and reduce p99 latency?
**Answer**: **Measurement**: Use Micrometer `Timer` with percentile histograms; instrument database queries, HTTP calls, and business logic separately. **Reduction**: (1) Identify bottlenecks via distributed tracing; (2) Add caching (Redis, CDN); (3) Optimize database queries (indexes, connection pool size); (4) Use connection pooling; (5) Implement circuit breakers for slow dependencies; (6) Use async processing; (7) Right-size thread pools; (8) Profile CPU allocations.

### Q15: Explain the difference between RED metrics and USE metrics.
**Answer**: **RED** (Rate, Errors, Duration) — for service-level monitoring: Rate (requests/second), Errors (failed requests/second), Duration (latency distribution). **USE** (Utilization, Saturation, Errors) — for resource-level monitoring: Utilization (percentage busy), Saturation (queue length), Errors (error count). RED is for application services; USE is for infrastructure (CPU, memory, disk, network). They complement each other: RED tells you the service is slow; USE tells you the CPU is saturated.

### Q16: How do you handle high-cardinality metrics in Prometheus?
**Answer**: High cardinality (e.g., user ID as a label value) can cause Prometheus memory issues. Solutions: (1) Use **exemplars** to link metrics to traces with high-cardinality attributes; (2) Use **logging** for high-cardinality data instead of metrics; (3) Use **reduction** — aggregate before exporting (e.g., histogram buckets instead of per-user); (4) Use **metric relabeling** to drop high-cardinality labels; (5) Use **alternative storage** — Cortex, Thanos, VictoriaMetrics for better handling.

### Q17: What is tail-based sampling and when would you use it?
**Answer**: Tail-based sampling decides whether to keep a trace **after** all spans are completed. It requires buffering spans until the trace is complete. **Use cases**: (1) Always sample traces with errors; (2) Sample a percentage of successful traces; (3) Sample slow traces; (4) Ensure sample boundaries don't split related traces. Tail-based sampling is more accurate for error detection but requires more memory (buffering). The OpenTelemetry Collector supports tail-based sampling with the `tailsampling` processor.

### Q18: How do you implement health checks for Kubernetes?
**Answer**: Kubernetes uses two probes: **liveness** (is the app alive? restart if dead) and **readiness** (is the app ready to serve traffic? remove from service if not). Spring Boot Actuator supports both: `management.endpoint.health.probes.enabled=true` exposes `/actuator/health/liveness` and `/actuator/health/readiness`. Liveness checks should be lightweight (JVM alive). Readiness checks should verify dependencies (database, message broker). Use `KubernetesHealthIndicator` for automatic probes.

### Q19: Explain the concept of burn rate in SLO monitoring.
**Answer**: Burn rate measures how fast the error budget is being consumed relative to the SLO window. **1x burn rate** = consuming budget at the expected rate (your SLO will be exactly met at the end of the window). **2x burn rate** = consuming budget 2x faster (SLO will be breached before the end). Alerting rules: multi-window, multi-burn-rate approach — alert on 2x burn rate over 1 hour (critical) and 1x burn rate over 6 hours (warning). This prevents false positives while catching real issues quickly.

### Q20: What is the difference between logging frameworks (Logback, Log4j2, java.util.logging)?
**Answer**: Spring Boot uses **Logback** by default (via SLF4J). **Log4j2** offers better performance (asynchronous logging, garbage-free mode) but requires additional configuration. **java.util.logging** (JUL) is built-in but limited. **SLF4J** is a facade — it doesn't do logging itself but provides a common API. Spring Boot recommends Logback but supports Log4j2 and JUL through SLF4J bindings. For production: use async appenders with JSON formatting.

# Quick Reference: Jaeger

<div align="center">

![Module](https://img.shields.io/badge/Module-40-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Jaeger-green?style=for-the-badge)

**Quick lookup guide for distributed tracing**

</div>

---

## 📋 Core Concepts

| Concept | Description |
|---------|-------------|
| **Trace** | End-to-end request flow |
| **Span** | Single operation in trace |
| **SpanContext** | Trace/span ID for propagation |
| **Baggage** | Custom key-value in context |

---

## 🔑 Key Commands

### Jaeger Agent
```bash
# Start agent
docker run -p 6831:6831/udp -p 16686:16686 jaegertracing/all-in-one:latest

# Query UI
http://localhost:16686

# Send spans via HTTP
curl -X POST http://localhost:14268/api/traces \
  -H 'Content-Type: application/json' \
  -d '{"spans":[{"operationName":"test","startTime":0,"duration":1000,"traceId":"abc123"}]}'
```

---

## 📊 Java Implementation

### Manual Tracing
```java
@Trace(operationName = "process-order")
public Order processOrder(OrderRequest request) {
    Span span = tracer.buildSpan("process-order")
        .withTag("order.id", request.getId())
        .start();
    
    try {
        // business logic
        span.setTag("status", "success");
        return order;
    } catch (Exception e) {
        span.setTag("error", true);
        span.setTag("error.message", e.getMessage());
        throw e;
    } finally {
        span.finish();
    }
}
```

### Scoped Spans
```java
try (Scope scope = tracer.buildSpan("db-call").startActive(true)) {
    span.addEvent("Query started");
    repository.save(entity);
    span.addEvent("Query completed");
}
```

---

## 📊 Spring Boot Integration

### OpenTelemetry
```xml
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-sdk-extension-trace-auto</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-instrumentation-annotations</artifactId>
</dependency>
```

### Configuration
```yaml
spring:
  opentelemetry:
    tracing:
      enabled: true
      exporter:
        jaeger:
          endpoint: http://localhost:4317
          service-name: myapp
```

### Annotation
```java
@WithSpan("process-payment")
public PaymentResult processPayment(Payment payment) {
    // Auto captures: service name, span name, duration
    span.setAttribute("payment.id", payment.getId());
    
    // Nesting with @SpanChild
    paymentGateway.charge(payment);
    return result;
}
```

---

## 📊 Context Propagation

### Manual
```java
// Extract from incoming request
Context extracted = tracer.extract(
    Format.Builtin.HTTP_HEADERS,
    new TextMapAdapter(httpHeaders));

// Inject into outgoing request
Span span = tracer.buildSpan("outgoing-call").start();
tracer.inject(span.getContext(), Format.Builtin.HTTP_HEADERS, new TextMapAdapter(outgoingHeaders));

try (Scope scope = tracer.withSpanInScope(span)) {
    // call external service
} finally {
    span.finish();
}
```

### Baggage
```java
// Set
span.setBaggageItem("user.id", userId);

// Get
String userId = span.getBaggageItem("user.id");
```

---

## 📊 Sampling

```java
// Always sample
AlwaysSample.INSTANCE

// Never sample
NeverSample.INSTANCE

// Probabilistic
new ProbabilisticSampler(0.5)

// Rate limited
new RateLimitingSampler(10)

// Custom
new Sampler() {
    @Override
    public SamplingDecision shouldSample(SpanContext parent, String operation, long epochNanos) {
        return SamplingDecision.SAMPLE;
    }
}
```

---

## ✅ Best Practices

- Use meaningful span names
- Add relevant tags and events
- Implement proper context propagation
- Use sampling for high-volume traces

### ❌ DON'T
- Don't create spans in every method
- Don't forget to close/finish spans

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>
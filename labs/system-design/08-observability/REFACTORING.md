# Observability - REFACTORING

## From Log Strings to Structured Logging

### Before: String interpolation
```java
logger.info("User " + userId + " placed order " + orderId);
// ↓
// "User 123 placed order 456" (hard to search/parse)
```

### After: Structured JSON logging
```java
log.info("Order placed", KV("userId", userId), KV("orderId", orderId));
// ↓
// {"msg":"Order placed","userId":"123","orderId":"456"}
```

## From No Metrics to Business Metrics

### Before: No metrics at all
```java
public Order createOrder(Order order) {
    return orderRepository.save(order);
}
```

### After: Business metrics
```java
public Order createOrder(Order order) {
    Order saved = orderRepository.save(order);
    metrics.recordOrderCreated(saved);
    metrics.recordRevenue(saved.getTotal());
    return saved;
}
```

## Adding Tracing to Existing Code

### Before: No tracing
```java
public PaymentResult processPayment(Order order) {
    return paymentClient.charge(order);
}
```

### After: With tracing
```java
@WithSpan
public PaymentResult processPayment(Order order) {
    Span.current().setAttribute("order.id", order.getId());
    return paymentClient.charge(order);
}
```

## From Manual Instrumentation to Auto-Instrumentation

### Before: Manual spans everywhere
```java
// Every method decorated manually
Span span = tracer.spanBuilder("method").startSpan();
```
This is tedious and easy to miss.

### After: Auto-instrumentation with OpenTelemetry agent
```bash
java -javaagent:opentelemetry-javaagent.jar \
     -Dotel.service.name=order-service \
     -Dotel.exporter.otlp.endpoint=http://jaeger:4317 \
     -jar app.jar
```
Automatically instruments Spring, HTTP clients, JDBC, Kafka, etc.

## Performance Impact

| Observability Addition | Latency Overhead | Storage Impact |
|----------------------|-----------------|---------------|
| Structured logging | < 1ms | 10-100 MB/day per instance |
| Metrics (Prometheus) | < 0.1ms | 1-10 MB/day per instance |
| Full traces (1% sample) | < 0.5ms | 100 MB/day per instance |
| Full traces (100% sample) | < 0.5ms | 10 GB/day per instance |

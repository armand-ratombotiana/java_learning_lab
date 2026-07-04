# Observability - COMMON MISTAKES

## 1. Logging Without Structure
```java
// WRONG: String concatenation, no context
log.info("Order " + orderId + " created for customer " + customerId);

// RIGHT: Structured logging
log.info("Order created", KV("orderId", orderId), KV("customerId", customerId));
```

## 2. Too Many Metrics (Cardinality Explosion)
```java
// WRONG: User ID as label — millions of time series
Counter.builder("api.requests").tag("userId", userId).register(registry);

// RIGHT: Aggregate or use dimensional labels
Counter.builder("api.requests").tag("region", region).register(registry);
```

## 3. Not Propagating Trace Context
Tracing breaks if headers (traceparent, tracestate) aren't propagated between services.

## 4. No Correlation IDs
Without correlation IDs, connecting log entries across services in a distributed trace is impossible.

## 5. Sampling All Traces
Tracing 100% of requests generates too much data. Sample at 1-10% for high-volume services.

## 6. Ignoring JVM Metrics
```yaml
# WRONG: Only monitoring API metrics
# Missing: GC pauses, thread states, memory pools, file descriptors

# RIGHT: Enable all JVM metrics
management.metrics.enable.jvm: true
management.metrics.enable.process: true
```

## 7. Alert Fatigue
Too many alerts → ignore all alerts. Each alert should be actionable.

## 8. No SLIs/SLOs
```java
// WRONG: Goals are vague ("system should be fast")
// RIGHT: Define SLIs (Service Level Indicators)
// - 95% of requests complete in < 500ms
// - Error rate < 0.1%
// Then SLO: 99.9% of SLIs are met monthly
```

## 9. Not Testing Observability
The monitoring system itself fails — and you don't notice until you need it.

## 10. No Dashboards for Business Metrics
Dashboards showing only technical metrics miss the point. Business metrics (orders, revenue, users) should be visible alongside technical ones.

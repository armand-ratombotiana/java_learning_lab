# Monitoring and Alerting Guide — Circuit Breaker and Cascading Failure Detection

## Lab 07: Microservice Circuit Breaker Failure

This guide documents the monitoring and alerting configuration required to detect circuit breaker state changes, thread pool exhaustion, and cascading failure propagation in microservice architectures.

---

## 1. Circuit Breaker Metrics

### Resilience4j Actuator Endpoints

```properties
# application.properties — Actuator configuration
management.endpoints.web.exposure.include=health,metrics,prometheus,circuitbreakers,bulkheads,retries,timelimiters
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true
management.metrics.tags.application=${spring.application.name}
```

### Key Circuit Breaker Metrics

| Metric | Prometheus Name | Description |
|--------|-----------------|-------------|
| State | `resilience4j_circuitbreaker_state` | 0=CLOSED, 1=OPEN, 2=HALF_OPEN |
| Failure Rate | `resilience4j_circuitbreaker_failure_rate` | Current failure rate percentage |
| Buffered Calls | `resilience4j_circuitbreaker_buffered_calls` | Calls in sliding window |
| Successful Calls | `resilience4j_circuitbreaker_calls` | Tag: `kind="successful"` |
| Failed Calls | `resilience4j_circuitbreaker_calls` | Tag: `kind="failed"` |
| Not Permitted | `resilience4j_circuitbreaker_not_permitted_calls` | Calls rejected when OPEN |
| Fallback Calls | `resilience4j_fallback_calls` | Tag: `kind="successful"/"failed"` |

### Custom Circuit Breaker Metrics Exporter

```java
package com.example.order.monitoring;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class CircuitBreakerMetricsExporter {

    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerMetricsExporter.class);

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final MeterRegistry meterRegistry;

    public CircuitBreakerMetricsExporter(
            CircuitBreakerRegistry circuitBreakerRegistry,
            MeterRegistry meterRegistry
    ) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void registerMetrics() {
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(circuitBreaker -> {
            String name = circuitBreaker.getName();

            Gauge.builder("resilience4j.circuitbreaker.failure.rate", circuitBreaker,
                    cb -> cb.getMetrics().getFailureRate())
                .tag("name", name)
                .register(meterRegistry);

            Gauge.builder("resilience4j.circuitbreaker.buffered.calls", circuitBreaker,
                    cb -> cb.getMetrics().getNumberOfBufferedCalls())
                .tag("name", name)
                .register(meterRegistry);

            Gauge.builder("resilience4j.circuitbreaker.not.permitted.calls", circuitBreaker,
                    cb -> cb.getMetrics().getNumberOfNotPermittedCalls())
                .tag("name", name)
                .register(meterRegistry);

            circuitBreaker.getEventPublisher()
                .onStateTransition(event -> {
                    log.warn("Circuit breaker '{}' state transition: {} -> {}",
                        event.getCircuitBreakerName(),
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()
                    );

                    meterRegistry.counter("resilience4j.circuitbreaker.state.transition",
                        "name", name,
                        "from", event.getStateTransition().getFromState().toString(),
                        "to", event.getStateTransition().getToState().toString()
                    ).increment();
                });
        });
    }
}
```

---

## 2. Thread Pool and Bulkhead Monitoring

### Bulkhead Metrics

| Metric | Prometheus Name | Description |
|--------|-----------------|-------------|
| Core Pool Size | `resilience4j_bulkhead_core_thread_pool_size` | Configured core threads |
| Max Pool Size | `resilience4j_bulkhead_max_thread_pool_size` | Configured max threads |
| Queue Capacity | `resilience4j_bulkhead_queue_capacity` | Configured queue depth |
| Current Queue Size | `resilience4j_bulkhead_current_queue_size` | Current queue depth |
| Active Thread Count | `resilience4j_bulkhead_active_thread_count` | Currently active threads |
| Available Permits | `resilience4j_bulkhead_available_permissions` | Available semaphore permits |

### Thread Pool Health Check Endpoint

```java
package com.example.order.actuator;

import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class ResilienceHealthIndicator implements HealthIndicator {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final BulkheadRegistry bulkheadRegistry;

    public ResilienceHealthIndicator(
            CircuitBreakerRegistry circuitBreakerRegistry,
            BulkheadRegistry bulkheadRegistry
    ) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.bulkheadRegistry = bulkheadRegistry;
    }

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        boolean healthy = true;

        for (CircuitBreaker cb : circuitBreakerRegistry.getAllCircuitBreakers()) {
            CircuitBreaker.State state = cb.getState();
            details.put("circuitbreaker." + cb.getName(), state.toString());

            if (state == CircuitBreaker.State.OPEN) {
                healthy = false;
            }
        }

        for (io.github.resilience4j.bulkhead.Bulkhead bh : bulkheadRegistry.getAllBulkheads()) {
            int activeCount = bh.getMetrics().getNumberOfActiveThreads();
            int maxPoolSize = bh.getBulkheadConfig().getMaxThreadPoolSize();
            double utilization = (double) activeCount / maxPoolSize * 100;
            details.put("bulkhead." + bh.getName() + ".utilization", String.format("%.1f%%", utilization));

            if (utilization > 80) {
                healthy = false;
            }
        }

        if (healthy) {
            return Health.up().withDetails(details).build();
        }
        return Health.down().withDetails(details).build();
    }
}
```

---

## 3. Distributed Tracing (Zipkin/Jaeger)

### Brave Instrumentation for Circuit Breaker Events

```java
package com.example.order.tracing;

import brave.Span;
import brave.Tracer;
import brave.Tag;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class CircuitBreakerTracing {

    private static final Tag<String> CB_NAME = Tag.of("circuitbreaker.name");
    private static final Tag<String> CB_FROM = Tag.of("circuitbreaker.from");
    private static final Tag<String> CB_TO = Tag.of("circuitbreaker.to");

    private final Tracer tracer;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerTracing(Tracer tracer, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.tracer = tracer;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @PostConstruct
    public void init() {
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(cb -> {
            cb.getEventPublisher().onStateTransition(event -> {
                Span span = tracer.nextSpan()
                    .name("circuitbreaker-transition")
                    .start();
                try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
                    CB_NAME.tag(event.getCircuitBreakerName(), span);
                    CB_FROM.tag(event.getStateTransition().getFromState().toString(), span);
                    CB_TO.tag(event.getStateTransition().getToState().toString(), span);
                } finally {
                    span.finish();
                }
            });

            cb.getEventPublisher().onCallNotPermitted(event -> {
                Span span = tracer.nextSpan()
                    .name("circuitbreaker-call-blocked")
                    .start();
                try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
                    CB_NAME.tag(cb.getName(), span);
                    span.tag("blocked", "true");
                } finally {
                    span.finish();
                }
            });
        });
    }
}
```

---

## 4. Prometheus Alert Rules

```yaml
groups:
  - name: cascading-failure-detection
    rules:
      - alert: MultipleCircuitsOpen
        expr: count(resilience4j_circuitbreaker_state{state="open"} == 1) > 3
        for: 1m
        labels:
          severity: critical
          incident_type: cascading_failure
        annotations:
          summary: "{{ $value }} circuit breakers are OPEN"
          description: "More than 3 circuit breakers open — possible cascading failure"

      - alert: SingleCircuitOpenLong
        expr: resilience4j_circuitbreaker_state{state="open"} == 1
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Circuit breaker {{ $labels.name }} has been OPEN for 5 minutes"
          description: "Circuit breaker {{ $labels.name }} in {{ $labels.instance }} has been open for 5 minutes"

      - alert: ThreadPoolExhaustion
        expr: resilience4j_bulkhead_active_thread_count / resilience4j_bulkhead_max_thread_pool_size > 0.9
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Thread pool {{ $labels.name }} at {{ $value | humanizePercentage }} utilization"
          description: "Thread pool {{ $labels.name }} has exceeded 90% utilization"

      - alert: FallbackFailureRate
        expr: rate(resilience4j_fallback_calls_total{kind="failed"}[5m]) / rate(resilience4j_fallback_calls_total[5m]) > 0.1
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "Fallback failure rate > 10%"
          description: "{{ $labels.name }} fallback failing at {{ $value }}% rate"

      - alert: RetryStorm
        expr: rate(http_client_requests_seconds_count{status="5xx"}[1m]) > rate(http_client_requests_seconds_count{status="5xx"}[5m]) * 3
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "Possible retry storm detected"
          description: "5xx error rate has tripled in the last minute"
```

---

## 5. Dashboards

### Resilience Dashboard (Grafana JSON Model Extract)

**Panel 1: Circuit Breaker States**
- Type: Stat
- Query: `resilience4j_circuitbreaker_state`
- Show: Current state of all circuit breakers (color-coded: green=CLOSED, yellow=HALF_OPEN, red=OPEN)
- Thresholds: OPEN > 0 (red)

**Panel 2: Failure Rates**
- Type: Time series
- Query: `resilience4j_circuitbreaker_failure_rate{name=~".+"}`
- Threshold line: 50 (failure rate threshold)
- Legend: {{name}}

**Panel 3: Thread Pool Utilization**
- Type: Time series
- Query: `resilience4j_bulkhead_active_thread_count / resilience4j_bulkhead_max_thread_pool_size * 100`
- Threshold line: 80% (warning), 90% (critical)
- Legend: {{name}}

**Panel 4: Fallback Calls**
- Type: Time series
- Query: `rate(resilience4j_fallback_calls_total[5m])`
- Legend: {{kind}} — {{name}}

**Panel 5: Circuit Breaker Events**
- Type: Logs panel
- Source: Zipkin span logs for circuitbreaker-transition
- Show: Recent circuit breaker state transitions

**Panel 6: Request Success Rate**
- Type: Time series
- Query: `sum(rate(http_server_requests_seconds_count{status!~"5.."}[1m])) / sum(rate(http_server_requests_seconds_count[1m])) * 100`
- Threshold: 99.9%

---

## 6. Log Analysis Queries

### ELK/Splunk Queries

**Find circuit breaker state transitions:**
```
source="order-service" AND ("circuit breaker" OR "CircuitBreaker") AND ("OPEN" OR "CLOSED" OR "HALF_OPEN")
| stats count by circuit_breaker_name, state
| sort - count
```

**Identify thread pool exhaustion:**
```
source="order-service" AND "thread pool" AND ("exhausted" OR "rejected" OR "full")
| timechart count by source
```

**Trace cascading failure propagation:**
```
source IN ("order-service", "payment-service", "inventory-service") AND status=500
| transaction maxspan=5m by trace_id
| where eventcount > 3
| stats count by source
```

### Application Insights Query (Kusto)

```kusto
// Find all circuit breaker state transitions
traces
| where timestamp > ago(1h)
| where message contains "Circuit breaker"
| where message contains "state transition"
| extend cbName = extract("'([^']+)'", 1, message)
| extend transition = extract("([A-Z_]+ -> [A-Z_]+)", 1, message)
| project timestamp, cbName, transition
| order by timestamp desc
```

---

## 7. Alert Response Runbook

### Alert: Circuit Breaker OPEN
1. Check if this is a single circuit breaker or multiple
2. If single: identify the downstream service — check its health
3. If multiple: possible cascading failure — escalate immediately
4. Check if fallback is returning valid responses
5. Monitor thread pool utilization for the affected service
6. Contact downstream service owner

### Alert: Thread Pool Exhaustion (90%)
1. Identify which dependency is consuming threads
2. Check if circuit breaker for that dependency is OPEN or CLOSED
3. If CLOSED: manually force circuit breaker OPEN
4. Check retry configuration — disable retries if storm detected
5. Scale thread pool temporarily if needed (emergency)

### Alert: Multiple Circuit Breakers OPEN
1. DECLARE SEV1 incident immediately
2. Identify the root cause service (the one at the bottom of the dependency chain)
3. Isolate the failing service (remove from service discovery)
4. Force circuit breakers OPEN upstream
5. Monitor recovery as thread pools drain
6. Implement fix on failing service before restoring

---

## 8. Monitoring Configuration Checklist

- [ ] Circuit breaker metrics exported to Prometheus
- [ ] Circuit breaker state alert (OPEN transition)
- [ ] Thread pool utilization alert (80% warning, 90% critical)
- [ ] Fallback invocation rate alert
- [ ] Retry storm detection alert
- [ ] Multiple circuit breaker OPEN alert (cascading failure)
- [ ] Circuit breaker state transition tracing (Zipkin/Jaeger)
- [ ] Resilience health indicator in Actuator
- [ ] Grafana dashboard with all panels listed above
- [ ] ELK/Splunk log queries for incident investigation

---

## References
- Netflix Tech Blog: "Metrics at Netflix"
- Google SRE Book — Chapter 10: "Practical Alerting"
- Resilience4j Documentation: "Monitoring and Metrics"
- Prometheus Blog: "Alerting for Microservice Resilience"
- Zipkin Documentation: "Tracing Circuit Breaker Events"

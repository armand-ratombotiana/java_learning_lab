# Observability - INTERNALS

## Micrometer Internals

### Meter Registry Hierarchy
```
MeterRegistry (abstract)
├── SimpleMeterRegistry   (in-memory, testing)
├── CompositeMeterRegistry (multiple registries)
├── PrometheusMeterRegistry (Prometheus scrape)
└── GraphiteMeterRegistry (Graphite)
```

### Metric Types
- **Counter**: Monotonically increasing (requests, errors)
- **Gauge**: Point-in-time value (memory usage, queue size)
- **Timer**: Duration of operations (latency)
- **DistributionSummary**: Distribution of values (response sizes)
- **LongTaskTimer**: Duration of in-progress tasks

### Default JVM Metrics
Micrometer automatically collects:
- JVM memory (heap, non-heap, buffers)
- JVM GC (pause time, counts)
- JVM threads (states, daemon vs user)
- CPU usage (process, system)
- Class loading
- File descriptors

## OpenTelemetry Internals

### Trace Context Propagation
```
W3C Trace-Context format:
traceparent: 00-0af7651916cd43dd8448eb211c80319c-b7ad6b7169203331-01
  ── version ──┬─────── trace-id ───────┬────── span-id ───────┬─flags
```

### Span Lifecycle
```java
Span span = tracer.spanBuilder("operation")
    .setSpanKind(SpanKind.SERVER)
    .startSpan();

try (Scope scope = span.makeCurrent()) {
    // operation with span in context
    span.addEvent("processing started");
    // ...
    span.setAttribute("key", "value");
    span.setStatus(StatusCode.OK);
} catch (Exception e) {
    span.setStatus(StatusCode.ERROR, e.getMessage());
    span.recordException(e);
} finally {
    span.end();
}
```

## Log Aggregation Internals

### Architecture
```
Application → Logstash encoder → stdout → Container → Filebeat
                                                      ↓
                                              Elasticsearch
                                                      ↓
                                                 Kibana
```

### Logback Async Appender
```xml
<appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
    <!-- Queue 512 log events -->
    <queueSize>512</queueSize>
    <!-- Drop events if queue 80% full and level >= WARN -->
    <discardingThreshold>0</discardingThreshold>
    <!-- Never block the application thread -->
    <neverBlock>true</neverBlock>
    <appender-ref ref="JSON" />
</appender>
```

## Prometheus Internals

### Scrape Flow
```
1. Prometheus sends HTTP GET to /actuator/prometheus
2. Spring Boot returns metrics in text format
3. Prometheus stores in time-series DB
4. Rules evaluate for alerts
5. Grafana queries for visualization
```

### Metric Types
- **Counter**: _total suffix, rate() for per-second
- **Gauge**: No suffix, current value
- **Histogram**: _bucket, _count, _sum
- **Summary**: Configurable quantiles

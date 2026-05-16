# Observability - EXERCISES

## Exercise 1: Structured Logging
Implement structured logging with:
- JSON format
- Trace ID correlation
- Log levels

## Exercise 2: Custom Metrics
Create custom metrics for:
- Request count
- Latency histogram
- Error counter

## Exercise 3: Distributed Tracing
Implement tracing with:
- Trace ID propagation
- Span creation
- Trace context

---

## Solutions

### Exercise 1: Structured Logging

```java
@Slf4j
public class StructuredLogger {
    public static void log(String message, Map<String, Object> data) {
        Map<String, Object> logData = new HashMap<>(data);
        logData.put("timestamp", Instant.now());
        logData.put("traceId", MDC.get("traceId"));
        log.info(new ObjectMapper().writeValueAsString(logData));
    }
}
```

### Exercise 2: Custom Metrics

```java
@Service
public class MetricsService {
    private final MeterRegistry registry;
    
    private final Counter requests = Counter.builder("requests.total").register(registry);
    private final Timer latency = Timer.builder("requests.latency").register(registry);
    
    public void recordRequest(long durationMs) {
        requests.increment();
        latency.record(durationMs, TimeUnit.MILLISECONDS);
    }
}
```

### Exercise 3: Distributed Tracing

```java
@Service
public class TracingService {
    private final Tracer tracer;
    
    public <T> T execute(String name, Supplier<T> operation) {
        Span span = tracer.startSpan(name);
        try {
            return operation.get();
        } finally {
            span.end();
        }
    }
}
```
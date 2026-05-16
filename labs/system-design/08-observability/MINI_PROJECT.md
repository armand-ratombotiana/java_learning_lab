# Observability - MINI PROJECT

Build observability layer with:
- Structured logging
- Custom metrics
- Distributed tracing

```java
@Service
public class ObservabilityService {
    private final MeterRegistry metrics;
    private final Tracer tracer;
    
    public <T> T trace(String name, Supplier<T> op) {
        return tracer.withSpan(name, op);
    }
}
```
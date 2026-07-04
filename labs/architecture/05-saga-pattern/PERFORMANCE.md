# Saga Pattern Performance

## Performance Considerations

### Saga Timeouts
```java
@Saga
@SagaTimeout(duration = 5, unit = ChronoUnit.MINUTES)
public class OrderSaga {
    // Saga automatically fails after 5 minutes
    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderConfirmedEvent event) {
        // Normal completion
    }

    @SagaEventHandler(associationProperty = "orderId", 
            deadline = "saga-timeout")
    public void handle(DeadlineMessage deadline) {
        log.warn("Saga timed out for order: {}", orderId);
        compensate();
    }
}
```

### Performance Metrics
```java
@Component
public class SagaPerformanceMonitor {

    private final MeterRegistry meterRegistry;

    public void recordStepDuration(String sagaType, String step, long durationMs) {
        meterRegistry.timer("saga.step.duration", "type", sagaType, "step", step)
            .record(Duration.ofMillis(durationMs));
    }

    public void recordSagaDuration(String sagaType, long durationMs) {
        meterRegistry.timer("saga.total.duration", "type", sagaType)
            .record(Duration.ofMillis(durationMs));
    }

    public void recordCompensation(String sagaType) {
        meterRegistry.counter("saga.compensation", "type", sagaType).increment();
    }
}
```

## Optimization Tips
- Keep saga steps small and quick
- Use async commands for non-blocking execution
- Set appropriate timeouts per step
- Monitor compensation rate as key health metric
- Use saga store indexing for fast lookups

## Benchmarking
- Measure end-to-end saga latency
- Track compensation frequency
- Monitor saga store performance
- Profile command gateway throughput

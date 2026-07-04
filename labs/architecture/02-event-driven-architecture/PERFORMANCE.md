# Event-Driven Architecture Performance

## Kafka Performance Tuning
```yaml
# application.yml
spring:
  kafka:
    producer:
      properties:
        batch.size: 16384     # 16KB batches
        linger.ms: 5          # Wait up to 5ms for batching
        compression.type: snappy
        buffer.memory: 33554432  # 32MB
    consumer:
      properties:
        fetch.min.bytes: 1
        fetch.max.wait.ms: 500
        max.poll.records: 500
```

## Performance Metrics
```java
@Component
public class EventMetrics {

    private final MeterRegistry meterRegistry;

    public void recordEventProcessed(String eventType, long durationMs) {
        meterRegistry.timer("event.processing.time", "type", eventType)
            .record(Duration.ofMillis(durationMs));
        meterRegistry.counter("event.processed", "type", eventType)
            .increment();
    }
}
```

## Batch Processing
```java
@Bean
public Consumer<List<OrderEvent>> batchProcess() {
    return events -> {
        // Process 100 events at once
        List<String> orderIds = events.stream()
            .map(OrderEvent::getOrderId)
            .toList();
        inventoryService.batchReserve(orderIds);
    };
}
```

## Performance Considerations
- **Batching** - Larger batches improve throughput
- **Compression** - Snappy/Zstd reduce network bandwidth
- **Partition count** - More partitions = more parallelism
- **Acks setting** - acks=1 for throughput, acks=all for safety
- **Consumer prefetch** - Tune max.poll.records per batch size

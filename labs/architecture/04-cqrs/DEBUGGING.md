# Debugging CQRS

## Common Issues

### 1. Read Model Not Updated
```bash
# Check if events are being published
# Check consumer lag
# Verify projection is running
# Check event store for unprocessed events
```

### 2. Event Handler Failure
```java
@Component
@Slf4j
public class DebugProjection {
    @EventHandler
    public void on(OrderCreatedEvent event) {
        log.info("Processing event: {}", event);
        try {
            repository.save(new OrderView(event));
        } catch (Exception e) {
            log.error("Failed to update projection for event {}", event, e);
            throw e; // Let Axon handle retry
        }
    }
}
```

### 3. Monitoring Tools
```java
@Component
public class CQRSMonitor {
    private final MeterRegistry meterRegistry;

    public void recordCommand(String commandType, long duration) {
        meterRegistry.timer("cqrs.command", "type", commandType)
            .record(Duration.ofMillis(duration));
    }

    public void recordProjectionLag(String projectionName, long lagMs) {
        meterRegistry.gauge("cqrs.projection.lag", lagMs);
    }
}
```

### 4. Event Store Inspection
```sql
-- Query the event store directly
SELECT * FROM domain_events
WHERE aggregate_id = 'order-123'
ORDER BY sequence_number;
```

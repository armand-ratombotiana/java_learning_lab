# Event Sourcing Performance

## Performance Considerations

### Event Store Performance
```yaml
# Axon event store configuration
axon:
  eventhandling:
    processors:
      order-processor:
        mode: SUBSCRIBING
        batch-size: 100
  eventstore:
    storage:
      max-batch-size: 500
```

### Snapshot Optimization
```java
// Configure snapshot trigger
@Component
public class OrderSnapshotter {

    private static final int SNAPSHOT_THRESHOLD = 50;

    @EventHandler
    public void on(DomainEvent event, @SequenceNumber long seq) {
        if (seq % SNAPSHOT_THRESHOLD == 0) {
            AggregateLifecycle.createSnapshot(OrderAggregate.class);
        }
    }
}
```

### Batch Event Processing
```java
@Component
public class BatchEventProcessor {

    @Scheduled(fixedDelay = 5000)
    public void processBatchedEvents() {
        List<DomainEvent> batch = eventStore.readUnprocessedEvents(100);
        batch.forEach(this::process);
    }
}
```

## Performance Metrics
| Operation | Without Snapshot | With Snapshot |
|-----------|-----------------|---------------|
| Load aggregate (100 events) | 500ms | 10ms |
| Load aggregate (1000 events) | 5000ms | 15ms |
| Append event | 2ms | 2ms |
| Rebuild projection | 10s (10K events) | 1s (with snapshots) |

## Optimization Tips
- Use snapshots every 50-100 events
- Batch event writes
- Use async projections
- Consider event store partitioning
- Archive old events beyond retention period

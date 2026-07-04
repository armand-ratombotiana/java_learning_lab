# Debugging Event Sourcing

## Common Issues

### 1. Event Store Inspection
```sql
-- View all events for an aggregate
SELECT version, event_type, event_data, timestamp
FROM events
WHERE aggregate_id = 'order-123'
ORDER BY version;
```

### 2. Replay Debugging
```java
@Component
@Slf4j
public class EventReplayDebugger {

    public void debugReplay(String aggregateId) {
        List<DomainEvent> events = eventStore.readEvents(aggregateId);
        log.info("Replaying {} events for aggregate {}", events.size(), aggregateId);

        OrderAggregate aggregate = new OrderAggregate();
        for (DomainEvent event : events) {
            log.debug("Applying event {}: v{}", event.getClass().getSimpleName(), 
                ((VersionedEvent) event).getVersion());
            aggregate.on(event);
        }
        log.info("Final state: {}", aggregate);
    }
}
```

### 3. Concurrency Conflict
```java
// Optimistic concurrency control
@EventHandler
public void on(OrderSubmittedEvent event) {
    // Check version matches expected
    if (event.getVersion() != expectedVersion) {
        throw new ConcurrencyException("Concurrent modification detected");
    }
    // Apply event
}
```

### 4. Snapshot Verification
```sql
-- Compare snapshot version with event count
SELECT COUNT(*) as event_count FROM events WHERE aggregate_id = 'order-123';
SELECT version FROM snapshots WHERE aggregate_id = 'order-123';
-- Ensure snapshot version <= max event version
```

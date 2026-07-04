# Event Sourcing Internals

## Event Store Table Schema
```sql
CREATE TABLE events (
    global_sequence BIGSERIAL PRIMARY KEY,
    aggregate_id VARCHAR(36) NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    event_data JSONB NOT NULL,
    version INT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    UNIQUE(aggregate_id, version)
);

CREATE INDEX idx_events_aggregate ON events(aggregate_id, version);
CREATE INDEX idx_events_timestamp ON events(timestamp);
CREATE INDEX idx_events_type ON events(event_type);
```

## Snapshot Table
```sql
CREATE TABLE snapshots (
    aggregate_id VARCHAR(36) PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    snapshot_data JSONB NOT NULL,
    version INT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    UNIQUE(aggregate_id, version)
);
```

## Axon Event Sourcing Internals
```java
@Component
public class AxonEventSourcingRepository {

    private final EventStore eventStore;
    private final Snapshotter snapshotter;
    private final int snapshotThreshold = 100;

    public Aggregate load(String aggregateId) {
        // Try to load from snapshot first
        Snapshot snapshot = snapshotter.load(aggregateId);

        // Read events from snapshot version
        List<DomainEvent> events = eventStore.readEvents(aggregateId, snapshot.getVersion());

        // Recreate aggregate
        Aggregate aggregate = snapshot.getAggregate();
        events.forEach(aggregate::apply);

        // Check if snapshot needed
        if (aggregate.getVersion() - snapshot.getVersion() > snapshotThreshold) {
            snapshotter.create(aggregate);
        }

        return aggregate;
    }
}
```

## Event Serialization
```java
@Component
public class EventSerializer {

    private final ObjectMapper objectMapper;

    public String serialize(DomainEvent event) {
        EventEnvelope envelope = new EventEnvelope(
            event.getClass().getName(),
            event.getEventId(),
            event.getTimestamp(),
            objectMapper.valueToTree(event)
        );
        return objectMapper.writeValueAsString(envelope);
    }

    public DomainEvent deserialize(String eventType, String data) {
        Class<?> clazz = Class.forName(eventType);
        return (DomainEvent) objectMapper.readValue(data, clazz);
    }
}
```

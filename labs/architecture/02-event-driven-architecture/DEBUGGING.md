# Debugging Event-Driven Architecture

## Common Debugging Scenarios

### 1. Event Not Consumed
```bash
# Check consumer group status
kafka-consumer-groups.bat --bootstrap-server localhost:9092 \
  --group order-group --describe

# Check if consumer is lagging
GROUP           TOPIC           PARTITION  LAG
order-group     order-events    0          150
order-group     order-events    1          200
```

### 2. Event Processing Failure
```java
@EventListener
public void handleFailedEvent(ListenerExecutionFailedException e) {
    // Log failed event details
    log.error("Event processing failed at {}: {}",
        e.getTimestamp(), e.getMessage());
    // Check DLQ
    kafkaTemplate.send("dead-letter-queue", e.getFailedMessage());
}
```

### 3. Debug Event Flow
```java
@Component
@Slf4j
public class EventTracer {
    @EventListener
    public void traceEvent(Object event) {
        if (event instanceof Event e) {
            log.debug("Event traced: type={}, id={}, timestamp={}",
                e.getType(), e.getEventId(), e.getTimestamp());
        }
    }
}
```

### 4. Replay Events
```bash
# Reset consumer offset to replay events
kafka-consumer-groups.bat --bootstrap-server localhost:9092 \
  --group order-group --reset-offsets --to-earliest --execute \
  --topic order-events
```

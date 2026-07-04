# Event-Driven Architecture Internals

## Kafka Internals
### Topic Partitioning
```java
// Kafka producer decides partition based on key or round-robin
public class KafkaEventProducer {
    private final KafkaTemplate<String, Event> template;

    public void publish(String key, Event event) {
        // Same key always goes to same partition -> ordering preserved
        template.send("events", key, event);
    }
}
```

### Consumer Group Coordination
```java
@Component
public class EventConsumer {
    @KafkaListener(
        topics = "order-events",
        groupId = "order-group",
        concurrency = "3"  // 3 consumer threads
    )
    public void onMessage(ConsumerRecord<String, OrderEvent> record) {
        log.info("Partition: {}, Offset: {}, Key: {}",
            record.partition(), record.offset(), record.key());
        processEvent(record.value());
    }
}
```

## Dead Letter Queue (DLQ)
```java
@Bean
public DefaultErrorHandler errorHandler() {
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
        kafkaTemplate, (record, ex) -> new TopicPartition("order-events-dlq", 0)
    );
    return new DefaultErrorHandler(recoverer,
        new ExponentialBackOff(1000, 2.0));
}
```

## Event Processing Internals
```java
@Service
public class EventProcessor {
    private final Map<String, EventHandler> handlers = new HashMap<>();

    public void process(Event event) {
        EventHandler handler = handlers.get(event.getType());
        if (handler == null) {
            throw new UnsupportedEventException(event.getType());
        }
        handler.handle(event);
    }
}
```

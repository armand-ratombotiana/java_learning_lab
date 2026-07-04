# Messaging - INTERNALS

## Kafka Internals

### Partition Assignment
```java
// Partition assignment strategy
props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
    RoundRobinAssignor.class.getName());
// Alternatives: RangeAssignor, StickyAssignor, CooperativeStickyAssignor
```

### Log Compaction
```java
// Topic with log compaction — keeps latest value per key
// Good for event sourcing, CQRS
Properties props = new Properties();
props.put(TopicConfig.CLEANUP_POLICY_CONFIG, "compact");
props.put(TopicConfig.MIN_COMPACTION_LAG_MS_CONFIG, "60000");
props.put(TopicConfig.DELETE_RETENTION_MS_CONFIG, "86400000");
```

### Consumer Group Rebalancing
```
1. Consumer joins/leaves group
2. Group coordinator detects change
3. Assigns partitions to remaining consumers
4. Consumers pause processing during rebalance
5. Consumers seek to last committed offset
6. Processing resumes

Impact: Processing pause (seconds to minutes)
Mitigation: Cooperative rebalancing (incremental)
```

### Zero-Copy Data Transfer
Kafka uses `sendfile()` system call to transfer data from disk to socket without copying through application memory. Reduces CPU usage and latency.

## RabbitMQ Internals

### Message Flow
```
Producer → Exchange → Binding → Queue → Consumer
             │
             └── Routing Key match
```

### Exchange Types
- **Direct**: Exact routing key match
- **Topic**: Pattern matching (order.created, order.#)
- **Fanout**: Broadcast to all queues
- **Headers**: Match based on header attributes

### Publisher Confirms
```
Producer → Broker: "Here's a message"
Broker → Producer: "Message persisted to disk" (Confirm)
Broker → Consumer: Deliver message
Consumer → Broker: "Message processed" (Ack)
Broker: Remove from queue
Broker → Producer: (Nack if cannot persist)
```

## Message Persistence

### Kafka
- Messages written to disk immediately (configurable flush interval)
- Replicated across brokers for durability
- Configurable retention (time or size based)

### RabbitMQ
- Messages can be persistent (disk) or transient (memory)
- Durable queues survive broker restart
- Lazy queues store messages to disk immediately

## Backpressure

### Consumer Lag Monitoring
```java
@Component
public class LagMonitor {
    private final KafkaAdmin admin;

    @Scheduled(fixedDelay = 60000)
    public void checkLag() {
        try (AdminClient client = AdminClient.create(admin.getConfigurationProperties())) {
            Map<TopicPartition, Long> endOffsets = client.listOffsets(
                Map.of(tp, OffsetSpec.latest())
            ).all().get();

            Map<TopicPartition, OffsetSpec> committed = ...;
            // Calculate lag = endOffset - committedOffset
        }
    }
}
```

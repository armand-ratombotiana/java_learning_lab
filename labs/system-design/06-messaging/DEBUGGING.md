# Messaging - DEBUGGING

## Monitoring Tools

### Kafka CLI Commands
```bash
# List topics
kafka-topics --bootstrap-server localhost:9092 --list

# Describe topic (partitions, replicas, ISR)
kafka-topics --bootstrap-server localhost:9092 --describe --topic orders

# Check consumer group lag
kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group order-group --describe

# Consume from beginning for debugging
kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic orders --from-beginning --property print.key=true
```

### RabbitMQ Management UI
```bash
# Enable management plugin
rabbitmq-plugins enable rabbitmq_management

# Access UI: http://localhost:15672
# View: queues, exchanges, bindings, connections, channels
```

## Common Issues

| Symptom | Cause | Diagnostic |
|---------|-------|------------|
| Consumer not receiving messages | Wrong group id, no subscription | Check consumer group status |
| Increasing lag | Consumer too slow | Check processing time, add partitions |
| Messages lost | Auto-commit before processing | Switch to manual commit |
| Duplicate processing | No idempotency after rebalance | Implement idempotency key |
| Rebalancing too often | Session timeout too short | Increase session.timeout.ms |
| Broker out of disk | Retention too long | Reduce retention, add disks |

## Kafka Debugging Properties

```yaml
# Enable for troubleshooting
spring.kafka.producer.properties:
  debug: all  # VERY verbose, use only in dev

spring.kafka.consumer.properties:
  debug: all
```

## Message Tracking

```java
// Add headers for traceability
@KafkaListener(topics = "orders")
public void consume(ConsumerRecord<String, Order> record) {
    log.info("Received: topic={}, partition={}, offset={}, key={}",
        record.topic(), record.partition(),
        record.offset(), record.key());
    // Add tracing header for distributed tracing
    String traceId = record.headers().lastHeader("traceId") != null
        ? new String(record.headers().lastHeader("traceId").value())
        : "none";
}
```

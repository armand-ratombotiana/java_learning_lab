# Messaging - INTERVIEW

## Common Interview Questions

### Q1: Kafka vs RabbitMQ — when to use what?
**Answer**: Kafka: high throughput (100K+ msg/s), long retention, replay, stream processing, logs. RabbitMQ: low latency, complex routing, task queues, smaller scale. Kafka is for event streaming, RabbitMQ for message delivery.

### Q2: How does Kafka guarantee ordering?
**Answer**: Within a partition, messages are ordered. Use the same key for related messages → same partition → same order. Cross-partition ordering is not guaranteed. For total ordering, use one partition (sacrificing throughput).

### Q3: Explain at-least-once vs exactly-once semantics in Kafka.
**Answer**: At-least-once: producer retries until ack, consumer commits after processing. May duplicate. Exactly-once: idempotent producer + transactional API + consumer with transactional read. No duplicates, no data loss.

### Q4: What is consumer group rebalancing?
**Answer**: When a consumer joins/leaves a group, partitions are reassigned. During rebalance, no processing occurs. Cooperative rebalancing reduces impact by reassigning incrementally.

### Q5: How do you handle backpressure in messaging?
**Answer**: Monitor consumer lag. Scale consumers (more partitions, more instances). Use reactive streaming with backpressure. Implement circuit breakers on slow downstreams. Dead letter for persistently failing messages.

### Q6: What is the outbox pattern?
**Answer**: Avoid dual-write problems (DB + queue). Write event to an outbox table in the same DB transaction. A relay process reads the outbox and publishes to the message broker. Guarantees exactly-once delivery.

### Q7: How do you handle schema evolution in Kafka?
**Answer**: Use Schema Registry with Avro/Protobuf. Backward compatibility: new schema can read old data. Forward compatibility: old schema can read new data. Transitive compatibility: all versions are compatible.

## System Design Problem: Design a Real-Time Notification System

### Requirements
- 10M notifications/day
- Multiple channels (email, SMS, push)
- Reliable delivery with retries
- User preferences (opt-in/out)

### Proposed Solution
- **Kafka** for event ingestion (notifications topic)
- **Partition by user_id** for ordering per user
- **Fan-out** to channel-specific queues (email, sms, push)
- **DLQ** for failed notifications with retry
- **Consumer groups** per channel type for independent scaling
- **Schema registry** for notification event schema

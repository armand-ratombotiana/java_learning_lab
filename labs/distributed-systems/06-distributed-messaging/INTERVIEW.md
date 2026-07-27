# Distributed Messaging - Interview Preparation

> Key interview questions about distributed messaging systems.

---

## Core Interview Questions

### Q1: Compare Kafka vs RabbitMQ vs Google Pub/Sub
**Answer**: Kafka: log-based, partitioned, pull-based, high throughput, ordered within partition, replayable. RabbitMQ: queue-based, push/pull, lower throughput, flexible routing, AMQP. Pub/Sub: managed, pull/push, partitioned, at-least-once.

### Q2: How does Kafka achieve high throughput?
**Answer**: Sequential disk I/O, zero-copy (sendfile), batching (batch.size, linger.ms), partitioning, compression (gzip/snappy/lz4/zstd), page cache optimization, consumer pull model.

### Q3: Explain Kafka's consumer group rebalancing
**Answer**: When consumers join/leave, group rebalances (partition reassignment). Strategies: Eager (stop all, reassign all), Cooperative Sticky (incremental, reassign affected partitions). Newer: CooperativeStickyAssignor minimizes disruption.

### Q4: What is exactly-once semantics in distributed messaging?
**Answer**: Combination: idempotent producer (producer ID + sequence number), transactional producer (cross-partition atomic writes), consumer isolation (read_committed). Producer sends with unique ID, broker deduplicates.

### Q5: What are dead letter queues and when to use them?
**Answer**: DLQ stores messages that failed processing after max retries. Use for: poison messages (malformed data), transient failures exceeded retry limits, application-level validation failures. Monitor DLQ for operational issues.

## Company-Specific Focus

| Company | Messaging Focus |
|---------|----------------|
| Confluent | "Kafka internals: ISR, log compaction, controller" |
| LinkedIn | "Kafka at LinkedIn - how it evolved" |
| Amazon | "SQS vs Kinesis - when to use each" |
| Google | "Pub/Sub at Google scale" |

## LeetCode Connections

| Problem | # | Messaging Concept |
|---------|---|-----------------|
| Design Circular Queue | 622 | Bounded queue |
| Design Bounded Blocking Queue | 1188 | Producer-consumer |
| Task Scheduler | 621 | Job scheduling via queue |
| Sliding Window Maximum | 239 | Stream processing |

## System Design Connections

- **Design a Real-time Analytics Pipeline**: Kafka + Spark Streaming
- **Design a Notification System**: Pub/sub with delivery guarantees
- **Design an Event Sourcing System**: Log-based messaging
- **Design a Task Queue**: Redis or RabbitMQ with priority

> **Key Insight**: In messaging interviews, focus on guarantees (at-most-once, at-least-once, exactly-once) and how they affect system design.
# Debugging Apache Kafka Issues

## Common Failure Scenarios

### Consumer Lag and Performance

Consumer lag occurs when consumers fall behind producers, causing message processing delays. This manifests as increasing lag metrics, older messages being processed, and eventual timeout failures when messages remain unprocessed beyond retention periods. High lag indicates either insufficient consumer capacity or processing bottlenecks.

The primary cause is consuming messages faster than they can be processed. This can stem from slow message processing logic, insufficient partition count, or too few consumer instances. When you have more consumers than partitions, some consumers remain idle. When you have fewer partitions than needed throughput, some consumers become overloaded.

Another common issue is consumer group rebalancing. When consumers join or leave a group, Kafka triggers a rebalance that pauses processing. During rebalancing, all consumers stop processing, wait for partition assignments, and then resume. Excessive rebalancing, caused by frequent consumer crashes or network issues, degrades throughput significantly.

### Message Loss and Duplication

Message loss can occur at multiple points: producers fail to send, brokers lose messages, or consumers fail to commit offsets. Producers not waiting for acknowledgment may lose messages during broker failures. Configuring `acks=all` ensures messages are replicated before the producer considers them sent.

Duplicate messages arise from failed acknowledgment scenarios. If the producer doesn't receive acknowledgment but the broker processed the message, it retries and creates a duplicate. Without idempotent production, this produces duplicate records in the output system.

Consumer crashes between processing and committing create another duplication risk. The consumer processes the message, but crashes before committing the offset. On restart, it re-processes the same message. The at-least-once delivery guarantee requires idempotent processing or deduplication logic.

### Stack Trace Examples

**Consumer offset out of range:**
```
org.apache.kafka.clients.consumer.OffsetOutOfRangeException: OffsetOutOfRangeException
    at org.apache.kafka.clients.consumer.KafkaConsumer.seekToBeginning(KafkaConsumer.java:1234)
    at com.example.Consumer.resetOffset(Consumer.java:56)
```

**Authorization failure:**
```
org.apache.kafka.common.errors.TopicAuthorizationException: Topic authorization failed
    at org.apache.kafka.common.errors.TopicAuthorizationException.from(TopicAuthorizationException.java:42)
```

**Leader not available:**
```
org.apache.kafka.common.errors.LeaderNotAvailableException: Leader not available
    at org.apache.kafka.clients.producer.KafkaProducer.send(KafkaProducer.java:467)
```

## Debugging Techniques

### Monitoring Consumer Health

Use Kafka consumer metrics to track lag. The `consumer lag` metric shows the difference between the latest offset and the committed offset. Monitor `records-lag-max` to identify if any partition falls far behind. Prometheus with Kafka's JMX exporter provides continuous monitoring.

The `kafka-consumer-groups` command describes consumer groups and their lag:
```bash
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group my-group
```

This shows current offset, log-end offset, and lag for each partition. Regular monitoring catches lag growth before it causes problems.

### Producer Debugging

Enable producer logging at DEBUG level to trace send operations. Look for `RecordAccumulator` operations to see how messages are batched, and check for `batch.complete` messages showing successful sends.

Use the `acks` and `retries` configuration appropriately. For critical data, set `acks=all` and configure `retries` with `delivery.timeout.ms` to bound retry duration. For high-throughput, non-critical data, use `acks=1` for faster delivery at the cost of potential loss.

Verify broker health with `kafka-broker-api-versions` to check if brokers are responsive. Use `kafka-topics.sh` to verify topic configuration, partition count, and replication factor.

## Best Practices

Always use meaningful consumer group IDs to identify the purpose and application of consumers. Group IDs should follow a naming convention like `application-name-environment` for easier monitoring and debugging.

Configure appropriate `session.timeout.ms` and `heartbeat.interval.ms` values. Short timeouts detect failures faster but cause more frequent rebalancing. Balance based on your processing latency and acceptable rebalancing frequency.

Prefer exactly-once semantics when possible. Use idempotent producers with `enable.idempotence=true` and transactional consumers with `isolation.level=read_committed` to minimize duplicates. For systems that need exactly-once, add application-level deduplication based on message keys.

Monitor both producer and consumer metrics continuously. Key metrics include `records-per-second`, `request-latency-ms`, `outgoing-byte-rate`, `fetch-rate`, and `commit-rate`. Set alerts on deviations from normal patterns to catch issues before they impact users.
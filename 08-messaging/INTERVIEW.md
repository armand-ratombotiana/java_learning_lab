# Messaging Interview Questions

## 1. Kafka Basics

### Q1: Explain Kafka architecture

**Answer:**
Kafka has a distributed architecture with several key components:
- **Broker**: Server that stores messages, part of a cluster
- **Topic**: Logical channel for messages, divided into partitions
- **Partition**: Ordered, immutable sequence stored on disk
- **Producer**: Publishes messages to topics
- **Consumer**: Subscribes to topics, reads messages
- **Consumer Group**: Group of consumers sharing work
- **ZooKeeper**: Manages cluster metadata and leader election

Messages in partitions are replicated to multiple brokers for fault tolerance.

---

### Q2: How does Kafka ensure message ordering?

**Answer:**
Kafka guarantees ordering within a single partition. All messages with the same key go to the same partition, ensuring order for related messages. To ensure global ordering, use a single partition (but this limits throughput).

---

### Q3: What is the role of ZooKeeper in Kafka?

**Answer:**
ZooKeeper manages:
- Cluster membership
- Controller election
- Topic configuration
- Partition leadership
- Quotas

In newer versions (KRaft mode), ZooKeeper is being replaced by Kafka's internal Raft-based quorum controller.

---

## 2. RabbitMQ Basics

### Q4: Explain RabbitMQ exchange types

**Answer:**
1. **Direct**: Routes to queue with exact match on routing key
2. **Topic**: Routes using wildcard patterns:
   - `*` matches one word
   - `#` matches zero or more words
3. **Fanout**: Broadcasts to all bound queues
4. **Headers**: Routes based on message header attributes (x-match=all/any)

---

### Q5: How do you ensure message persistence in RabbitMQ?

**Answer:**
1. Set delivery mode to persistent on producer:
```java
producer.setDeliveryMode(DeliveryMode.PERSISTENT);
```
2. Declare queue as durable:
```java
new Queue("queue-name", true)
```
3. Declare exchange as durable:
```java
new DirectExchange("exchange-name", true)
```

---

### Q6: What is the difference between RabbitMQ and Kafka?

| Aspect | RabbitMQ | Kafka |
|--------|----------|-------|
| Architecture | Message broker | Distributed log |
| Ordering | Per-queue | Per-partition |
| Throughput | High | Very high |
| Latency | Very low | Low |
| Message retention | Per-queue TTL | Configurable by time/size |
| Replay | Limited | Yes, via offset reset |
| Protocol | AMQP, STOMP, MQTT | Binary TCP |
| Use case | Task queues, RPC | Event streaming |

---

## 3. Messaging Patterns

### Q7: How would you implement request-reply in RabbitMQ?

**Answer:**
```java
// Request
String correlationId = UUID.randomUUID().toString();
MessageProperties props = new MessageProperties();
props.setCorrelationId(correlationId);
props.setReplyTo("reply-queue");

rabbitTemplate.sendAndReceive("exchange", "request-key", message, props);

// Response handler
@RabbitListener(queues = "reply-queue")
public void handleReply(Message message) {
    String correlationId = new String(message.getMessageProperties().getCorrelationId());
}
```

---

### Q8: What is a dead letter queue and how do you configure it?

**Answer:**
A queue for messages that fail processing.

```java
@Bean
public Queue mainQueue() {
    return QueueBuilder.durable("main-queue")
        .withArgument("x-dead-letter-exchange", "dlx-exchange")
        .withArgument("x-dead-letter-routing-key", "dlq-key")
        .build();
}

@Bean
public Queue dlq() {
    return QueueBuilder.durable("dead-letter-queue").build();
}
```

---

## 4. Reliability

### Q9: What delivery guarantees does Kafka provide?

**Answer:**
Kafka provides "at-least-once" by default. With proper configuration:
- **At-most-once**: Set `acks=0` on producer
- **At-least-once**: Set `acks=all` (default)
- **Exactly-once**: Enable `enable.idempotence=true` + transactional API

---

### Q10: How do you handle message failures?

**Answer:**
1. **Retry with backoff**: Configure retry mechanism
2. **Dead letter queue**: Move failed messages to DLQ
3. **Idempotency**: Design consumers to handle duplicates
4. **Manual intervention**: Log and alert on DLQ messages

---

### Q11: What is consumer offset management?

**Answer:**
Offsets track consumer position in partition:
- **Auto-commit**: Configurable interval
- **Manual commit**: `consumer.commitSync()` or `commitAsync()`
- **Manual assignment**: Specify exact offsets

---

## 5. Spring Integration

### Q12: Explain Spring Cloud Stream

**Answer:**
Spring Cloud Stream provides:
- Unified programming model for messaging
- Auto-configuration of binder implementations
- Declarative message binding via `@EnableBinding`
- Support for Kafka, RabbitMQ, and others

```java
@EnableBinding(Sink.class)
public class Consumer {
    @StreamListener(Sink.INPUT)
    public void receive(Message<?> message) { }
}
```

---

### Q13: How do you configure Kafka in Spring Boot?

**Answer:**
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
    consumer:
      group-id: my-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
```

---

## 6. Advanced

### Q14: What is Kafka Streams?

**Answer:**
A lightweight stream processing library built into Kafka. Features:
- Functional programming API
- Stateful transformations
- Windowing operations
- Exactly-once semantics
- No separate cluster needed

```java
KStream<String, String> source = builder.stream("input");
KTable<String, Long> counts = source
    .flatMapValues(v -> Arrays.asList(v.split("\\W+")))
    .groupBy((k, v) -> v)
    .count();
```

---

### Q15: How do you handle high throughput in Kafka?

**Answer:**
1. **Increase partitions**: More partitions = more parallelism
2. **Batch production**: `batch.size` and `linger.ms`
3. **Compression**: `compression.type=snappy` or `lz4`
4. **Producer tuning**: `acks=1`, `buffer.memory`
5. **Consumer optimization**: Increase `fetch.min.bytes`

---

### Q16: What are idempotent consumers and why are they important?

**Answer:**
Idempotent consumers can process the same message multiple times without side effects. Important because:
- At-least-once delivery can cause duplicates
- Network issues may cause retries
- Achieved via: deduplication keys, idempotent database operations, or design that tolerates duplicates

---

### Q17: Explain partition assignment strategy

**Answer:**
- **Range**: Assign partitions per topic per consumer
- **RoundRobin**: Distribute partitions across consumers
- **StickyAssignor**: Maintain assignment stability

---

### Q18: How do you monitor Kafka?

**Answer:**
Metrics to monitor:
- Consumer lag (difference between latest offset and consumed)
- Under-replicated partitions
- Request latency
- Broker disk usage
- Controller status

Tools: Kafka Manager, Confluent Control Center, Prometheus + Grafana

---

### Q19: What is the difference between synchronous and asynchronous production?

**Answer:**
```java
// Synchronous - blocks until acknowledgment
producer.send(record).get();

// Asynchronous - callback on completion
producer.send(record, (metadata, exception) -> {
    if (exception != null) handleError();
});
```

---

### Q20: How do you secure Kafka?

**Answer:**
1. **SSL/TLS**: Encrypt communication
2. **SASL**: Authentication (PLAIN, SCRAM, GSSAPI)
3. **ACLs**: Authorization for topics, groups
4. **Encryption at rest**: Disk encryption

---

### Q21: What is consumer rebalancing?

**Answer:**
When a consumer joins or leaves a group, partitions are reassigned. Triggers:
- New consumer joins
- Consumer leaves/crashes
- Topic subscription changes
- Partition count changes

Caution: Rebalancing causes pause in consumption.

---

### Q22: How do you handle message ordering in distributed systems?

**Answer:**
1. Use partition key consistently
2. Design for eventual consistency
3. Implement saga pattern for distributed transactions
4. Use sequence numbers for ordering
5. Add timestamps for latency tracking

---

### Q23: What is the difference between topic and queue?

**Answer:**
- **Queue** (RabbitMQ): Point-to-point, message consumed once
- **Topic** (Kafka): Publish-subscribe, message retained, can replay

---

### Q24: Explain the Kafka producer guarantees

**Answer:**
- Messages with same key go to same partition
- Messages are delivered in order within partition
- With `acks=all`, guaranteed delivery to in-sync replicas
- With idempotence, no duplicates

---

### Q25: What is the purpose of retention policy in Kafka?

**Answer:**
Controls how long messages are kept:
- By time: `retention.ms`
- By size: `retention.bytes`
- After cleanup: `cleanup.policy=delete|compact`

Enables replay of historical messages and event sourcing.
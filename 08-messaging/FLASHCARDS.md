# Messaging Flashcards

## Card 1: Kafka Topic
**Question:** What is a Kafka topic?

**Answer:**
A topic is a category/feed name to which messages are published. Topics are multi-subscriber - a topic can have zero or many consumers.

---

## Card 2: Kafka Partition
**Question:** What is a Kafka partition?

**Answer:**
A partition is an ordered, immutable sequence of messages within a topic. Each partition is stored on disk and can be replicated across brokers.

---

## Card 3: Consumer Group
**Question:** What is the purpose of a consumer group in Kafka?

**Answer:**
A consumer group is a group of consumers that work together to consume messages. Messages are load-balanced across group members. Each partition is assigned to one consumer in the group.

---

## Card 4: RabbitMQ Exchange Types
**Question:** Name the four RabbitMQ exchange types and their routing behavior.

**Answer:**
1. **Direct**: Routes to queue with matching routing key exactly
2. **Topic**: Routes using wildcard patterns (# matches any, * matches one word)
3. **Fanout**: Routes to all bound queues (broadcast)
4. **Headers**: Routes based on message header attributes

---

## Card 5: Message Delivery Guarantees
**Question:** What are the three message delivery guarantees?

**Answer:**
- **At-most-once**: Message may be lost, never duplicated
- **At-least-once**: Message never lost, may be duplicated
- **Exactly-once**: Message processed exactly once (hard to achieve)

---

## Card 6: Kafka Producer Properties
**Question:** What are three important Kafka producer configurations?

**Answer:**
- **acks**: Number of replicas that must acknowledge (0, 1, all)
- **retries**: Number of retry attempts on failure
- **batch.size**: Size of message batches in bytes

---

## Card 7: Dead Letter Queue
**Question:** What is a dead letter queue (DLQ)?

**Answer:**
A queue that stores messages that failed processing. Used to handle poison messages, failed processing, and investigate issues without losing messages.

---

## Card 8: Offset Management
**Question:** What is offset in Kafka and how is it managed?

**Answer:**
An offset is the position of a consumer in a partition. Managed by consumer via commit() - can be auto-commit or manual. Enables replay of messages.

---

## Card 9: Spring Cloud Stream
**Question:** What is Spring Cloud Stream?

**Answer:**
A framework for building event-driven microservices using messaging systems. Provides unified programming model across different message brokers (Kafka, RabbitMQ).

---

## Card 10: Correlation ID
**Question:** What is a correlation ID in messaging?

**Answer:**
A unique identifier that travels with a message through the system. Used to match request/response pairs, trace message flow across services.

---

## Card 11: Kafka vs RabbitMQ Use Cases
**Question:** When should you use Kafka vs RabbitMQ?

**Answer:**
- **Kafka**: High-throughput event streaming, log aggregation, CDC, need message replay
- **RabbitMQ**: Task queues, RPC, complex routing, lower latency, message TTL

---

## Card 12: Partition Key
**Question:** What is a partition key and why is it important?

**Answer:**
A key used to determine which partition a message goes to. Ensures ordering for messages with same key. Should be chosen based on distribution needs.

---

## Card 13: Message Serialization
**Question:** What are common message serialization formats?

**Answer:**
- JSON (human-readable, flexible)
- Avro (compact, schema-based)
- Protobuf (compact, schema-based)
- Kryo (fast, Java-specific)

---

## Card 14: Kafka Streams
**Question:** What is Kafka Streams?

**Answer:**
A lightweight stream processing library built into Kafka. Allows building real-time applications using functional-style API without separate cluster.

---

## Card 15: Publisher/Subscriber Pattern
**Question:** What is the publisher/subscriber pattern?

**Answer:**
A pattern where producers publish messages to topics and consumers subscribe to topics. Each subscriber receives a copy of published messages.

---

## Card 16: Message Acknowledgment
**Question:** What is message acknowledgment and why is it important?

**Answer:**
Confirmation from consumer that message was processed. Ensures reliability - broker won't redeliver acknowledged messages. Can be auto or manual.

---

## Card 17: RabbitMQ Queue Arguments
**Question:** What are important RabbitMQ queue arguments?

**Answer:**
- x-dead-letter-exchange: Where failed messages go
- x-dead-letter-routing-key: Routing key for DLQ
- x-message-ttl: Message time-to-live
- x-max-length: Max messages in queue

---

## Card 18: Consumer Lag
**Question:** What is consumer lag?

**Answer:**
The difference between the latest offset and the current consumer position. High lag indicates consumer is behind producers - important metric for monitoring.

---

## Card 19: Idempotency
**Question:** Why is idempotency important in messaging?

**Answer:**
Processing a message multiple times should produce the same result. Prevents duplicate messages from causing issues. Achieved via deduplication keys or idempotent operations.

---

## Card 20: Exactly-Once
**Question:** What is exactly-once semantics and how is it achieved?

**Answer:**
Processing each message exactly once, not losing or duplicating. In Kafka, achieved with idempotent producer + transactional consumer. More expensive than at-least-once.
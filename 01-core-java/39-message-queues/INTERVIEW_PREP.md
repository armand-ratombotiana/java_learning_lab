# Module 39: Message Queues & Event Streaming - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the difference between a Message Queue (like RabbitMQ) and an Event Stream (like Apache Kafka)?
**Answer**:
- **Message Queue (RabbitMQ)**: Designed for transient message delivery (Point-to-Point or Pub/Sub). The broker keeps track of the state of a message. Once a consumer receives and acknowledges a message, the broker **deletes** it from the queue. It is heavily used for task routing and async command execution.
- **Event Stream (Kafka)**: Designed as an immutable, distributed, append-only log. The broker does NOT track which messages a consumer has read; instead, the consumer tracks its own "offset" (its place in the log). Messages are **retained** for a configured period (e.g., 7 days) even after being consumed. This allows multiple independent consumer groups to replay history at their own pace, making it ideal for Event Sourcing and big data pipelines.

### Q2: How does Kafka guarantee message ordering?
**Answer**:
Kafka guarantees strict message ordering **only within a single Partition**. A topic in Kafka is split into multiple partitions to allow concurrent processing. 
If message ordering is crucial (e.g., ensuring `UserCreated` is processed before `UserUpdated`), the publisher must assign the same **Routing Key** (e.g., `userId: 123`) to all related messages. Kafka uses a hashing algorithm on the routing key to ensure that all messages with the same key are written to the exact same partition, ensuring they are consumed sequentially by a single consumer thread.

### Q3: Why is consumer Idempotency critical in distributed messaging systems?
**Answer**:
Distributed messaging systems (like AWS SQS, RabbitMQ, Kafka) operate under the **"At-Least-Once"** delivery guarantee to ensure no messages are lost. This means that if a network glitch occurs, or a consumer crashes after processing a message but *before* acknowledging it, the broker will redeliver the exact same message to another consumer. 
If the consumer is not idempotent, processing the duplicate message might result in double-charging a customer or inserting duplicate rows in a database. Idempotency guarantees that processing a message once has the exact same effect as processing it 100 times.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: The Outbox Pattern
**Problem**: An interviewer asks: "In a microservice, you need to save an Order to your PostgreSQL database, and then publish an `OrderCreated` event to Kafka so the Shipping Service can react. If you wrap both actions in a `@Transactional` block and the Kafka broker is down, the database transaction rolls back, which is good. But what happens if the database commits successfully, and the JVM crashes *before* the Kafka publish executes?"

**Answer/Solution**:
The event is lost forever, resulting in a dual-write inconsistency (the DB has the order, but Shipping never ships it).
To solve this, use the **Transactional Outbox Pattern**:
1. Do not talk to Kafka directly during the business transaction.
2. In your single database transaction, insert the Order into the `orders` table, and simultaneously insert a JSON representation of the event into an `outbox_events` table. Since they are in the same relational DB, they are guaranteed to commit atomically.
3. A separate, asynchronous background process (like Debezium, or a Spring `@Scheduled` job) continuously polls the `outbox_events` table, publishes the events to Kafka, and then deletes them from the outbox table.
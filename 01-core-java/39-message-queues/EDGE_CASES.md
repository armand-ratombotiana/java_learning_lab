# Module 39: Message Queues & Event Streaming - Edge Cases & Pitfalls

---

## Pitfall 1: Lack of Idempotency

### ❌ Wrong
Assuming a message will only ever be delivered exactly once. Writing a consumer that blindly inserts a record or charges a credit card every time a message is received.
```java
@KafkaListener(topics = "orders")
public void processOrder(Order order) {
    // ❌ If Kafka redelivers this message, the user gets charged twice!
    creditCardService.charge(order.getAmount());
}
```

### ✅ Correct
Message brokers guarantee "At-Least-Once" delivery. Network partitions or consumer crashes can cause redeliveries. Always make consumers **idempotent** (processing the same message 10 times has the same effect as processing it once).
```java
@KafkaListener(topics = "orders")
public void processOrder(Order order) {
    if (database.hasProcessed(order.getId())) {
        return; // ✅ Idempotent check
    }
    creditCardService.charge(order.getAmount());
    database.markAsProcessed(order.getId());
}
```

---

## Pitfall 2: Message Ordering

### ❌ Wrong
Publishing events related to a specific entity (e.g., `UserCreated`, `UserUpdated`, `UserDeleted`) across multiple partitions in Kafka without a routing key. The consumer might process `UserDeleted` before `UserCreated`, crashing the system.

### ✅ Correct
If message ordering matters, always publish related messages using the same routing key (e.g., `UserId`). In Kafka, messages with the same key are guaranteed to go to the same partition, ensuring strict ordering.

---

## Pitfall 3: Poison Pill Messages

### ❌ Wrong
A consumer encounters a malformed message (a "Poison Pill") that cannot be deserialized or causes a NullPointerException. The consumer crashes, does not ACK the message, and restarts. The broker immediately redelivers the poison pill, causing an infinite crash loop that halts the entire partition.

### ✅ Correct
Catch parsing and processing exceptions. If a message is permanently unprocessable, ACK it to remove it from the main queue, and route it to a **Dead Letter Queue (DLQ)** for manual inspection later.
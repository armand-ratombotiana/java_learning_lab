# Module 57: Advanced Microservices Patterns - Edge Cases & Pitfalls

---

## Pitfall 1: Dual Writes (The Distributed Transaction Trap)

### ❌ Wrong
Attempting to update a PostgreSQL database and publish an event to Apache Kafka within a standard Spring `@Transactional` method.
```java
@Transactional
public void createOrder(Order order) {
    orderRepository.save(order);
    kafkaTemplate.send("orders", order);
    // ❌ If Kafka is down, the DB rolls back. BUT if the DB commit fails AFTER Kafka receives the message, the message is still sent!
}
```

### ✅ Correct
Never perform cross-system writes synchronously without a proper distributed transaction mechanism. Use the **Transactional Outbox Pattern** to write the event to a database table within the same transaction, and use a separate polling service to dispatch it.

---

## Pitfall 2: Overusing Saga Orchestration

### ❌ Wrong
Using a central Saga Orchestrator for every single minor interaction between microservices, effectively turning the orchestrator into a massive God-Service that contains all the business logic for the entire company, creating a distributed monolith.

### ✅ Correct
Use Orchestration only for complex, high-risk, multi-step workflows (like E-Commerce Checkout). For simple, side-effect events (e.g., updating a search index when a user changes their name), use decentralized Saga Choreography (simple Pub/Sub).

---

## Pitfall 3: Infinite Retry Loops

### ❌ Wrong
Configuring a microservice to infinitely retry calling a downstream service that is returning `500 Internal Server Error`. This creates a "Retry Storm" that acts as an internal DDoS attack, bringing down the downstream service completely just as it tries to recover.

### ✅ Correct
Always combine retries with **Exponential Backoff** (wait 1s, then 2s, then 4s, then 8s) and apply the **Circuit Breaker** pattern. If the circuit breaks, stop retrying entirely for a set cooldown period to allow the downstream service time to recover.
# Module 39: Message Queues & Event Streaming - Mini Project

**Project Name**: Reliable Event Processing Architecture  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Design a robust, distributed messaging pipeline using a message broker (e.g., RabbitMQ or Kafka concepts) demonstrating asynchronous communication, consumer idempotency, and Dead Letter Queue (DLQ) error handling.

## 📝 Requirements

### Core Features

1. **Producer Service (The Publisher)**:
   - Create a Spring Boot service called `OrderPublisher`.
   - Expose a REST endpoint `POST /api/orders` that accepts an `OrderRequest` (id, item, amount).
   - The service should not save the order to a database. Instead, it should serialize the order to JSON and publish it to an exchange/topic named `orders.processing`.
   - Return a `202 Accepted` to the client immediately.

2. **Consumer Service (The Subscriber)**:
   - Create a separate Spring Component `OrderConsumer` that listens to the `orders.processing` queue/topic.
   - Implement an **Idempotency Key** check. Use a local `ConcurrentHashMap` or a mock Redis store to keep track of processed Order IDs. If the ID has already been processed, log "Duplicate detected" and acknowledge the message without processing it again.
   - If it is a new order, simulate processing by sleeping for 1 second.
   - Mark the order as processed in your idempotency store.

3. **Dead Letter Queue (DLQ) Implementation**:
   - Introduce a deliberate flaw: if the `amount` of the order is negative, throw a `DataValidationException`.
   - Configure the listener so that if this exception is thrown, the message is NOT re-queued infinitely.
   - Instead, catch the exception, explicitly acknowledge the bad message to remove it from the main queue, and programmatically publish the raw JSON payload to a separate queue named `orders.dlq` along with an error header detailing why it failed.

---

## 💡 Solution Blueprint

**Consumer Logic with Idempotency and DLQ**:
```java
@Component
public class OrderConsumer {
    private final Set<String> processedOrders = ConcurrentHashMap.newKeySet();
    private final MessagePublisher publisher; // Custom wrapper around KafkaTemplate/RabbitTemplate

    @KafkaListener(topics = "orders.processing")
    public void consume(String messagePayload, Acknowledgment ack) {
        try {
            Order order = objectMapper.readValue(messagePayload, Order.class);
            
            // 1. Idempotency Check
            if (!processedOrders.add(order.getId())) {
                System.out.println("Duplicate ignored: " + order.getId());
                ack.acknowledge();
                return;
            }

            // 2. Processing & Validation
            if (order.getAmount() < 0) {
                throw new IllegalArgumentException("Amount cannot be negative");
            }
            
            System.out.println("Processed order: " + order.getId());
            ack.acknowledge(); // Success
            
        } catch (Exception e) {
            // 3. Dead Letter Queue Routing
            System.err.println("Poison pill detected. Routing to DLQ. Error: " + e.getMessage());
            publisher.publish("orders.dlq", messagePayload, e.getMessage());
            ack.acknowledge(); // Remove from main queue so it doesn't block
        }
    }
}
```
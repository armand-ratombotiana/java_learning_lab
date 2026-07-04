# Common Mistakes in Event-Driven Architecture

## 1. Treating Events as Commands
```java
// WRONG: Event expecting specific action
public record PayOrderEvent(Long orderId, BigDecimal amount) { }
// Consumer MUST pay the order - this is a command behavior

// CORRECT: Event is a fact that happened
public record OrderPaymentReceived(Long orderId, BigDecimal amount) { }
// Consumer decides what to do based on this fact
```

## 2. Ignoring Event Schema Evolution
```java
// WRONG: No versioning
public record CustomerEvent(String customerId, String name) { }

// CORRECT: Versioned events
public record CustomerEventV1(String customerId, String name) { }
public record CustomerEventV2(String customerId, String name, String email) { }
```

## 3. Not Designing for Idempotency
```java
// WRONG: Assuming events are delivered once
public void onOrderCreated(OrderCreatedEvent event) {
    inventoryService.deductStock(event.getProductId());
}

// CORRECT: Handle duplicates
public void onOrderCreated(OrderCreatedEvent event) {
    if (processedEvents.exists(event.getEventId())) {
        return; // Already processed
    }
    inventoryService.deductStock(event.getProductId());
    processedEvents.markProcessed(event.getEventId());
}
```

## 4. Synchronous Event Publishing
```java
// WRONG: Blocking on event publishing
public void placeOrder(Order order) {
    orderRepository.save(order);
    kafkaTemplate.send("orders", event).get(); // BLOCKING!
}

// CORRECT: Fire and forget
public void placeOrder(Order order) {
    orderRepository.save(order);
    kafkaTemplate.send("orders", event); // Async
}
```

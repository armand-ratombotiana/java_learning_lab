# Refactoring to Event-Driven Architecture

## Synchronous to Asynchronous Migration

### Step 1: Identify Coupling Points
```java
// BEFORE: Tightly coupled synchronous calls
public void placeOrder(Order order) {
    inventoryService.checkAvailability(order.getProductId());
    paymentService.charge(order.getCustomerId(), order.getTotal());
    emailService.sendConfirmation(order.getCustomerEmail());
}
```

### Step 2: Introduce Event Publishing
```java
// PHASE 1: Publish event alongside existing code
public void placeOrder(Order order) {
    orderRepository.save(order);
    // Old synchronous calls (temporary)
    inventoryService.checkAvailability(order.getProductId());
    // New event publishing
    eventPublisher.publish(new OrderPlacedEvent(order));
}
```

### Step 3: Move Consumers to Events
```java
// PHASE 2: Add event consumers
@EventListener
public void handleOrderPlaced(OrderPlacedEvent event) {
    // Eventually replace the synchronous call
    inventoryService.checkAvailability(event.getProductId());
}
```

### Step 4: Remove Synchronous Calls
```java
// PHASE 3: Remove old synchronous code
public void placeOrder(Order order) {
    orderRepository.save(order);
    eventPublisher.publish(new OrderPlacedEvent(order));
    // Old code removed - inventory now handles via event
}
```

## Event Schema Migration
```java
// Use upcaster pattern for schema evolution
@Component
public class OrderEventUpcaster {
    public DomainEvent upcast(DomainEvent oldEvent) {
        if (oldEvent instanceof OrderPlacedEventV1 v1) {
            return new OrderPlacedEventV2(
                v1.getOrderId(), v1.getCustomerId(),
                v1.getTotal(), "PENDING"  // new field with default
            );
        }
        return oldEvent;
    }
}
```

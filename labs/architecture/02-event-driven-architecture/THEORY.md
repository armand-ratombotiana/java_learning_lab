# Event-Driven Architecture Theory

## Core Concepts

### Event
An event is a record of something that happened in the system. Events are immutable facts.
```java
public record OrderCreatedEvent(
    String eventId,
    Long orderId,
    String customerId,
    BigDecimal amount,
    Instant timestamp
) {}
```

### Event Types
1. **Domain Events** - Business facts (OrderPlaced, PaymentReceived)
2. **Integration Events** - Cross-service events
3. **Notification Events** - Simple alerts/triggers

### Patterns

#### Event Notification
Simple notification that something happened:
```java
@Service
public class OrderService {
    private final EventPublisher publisher;

    public void placeOrder(Order order) {
        orderRepository.save(order);
        publisher.publish(new OrderPlacedEvent(order.getId()));
        // Publisher doesn't care about consumers
    }
}
```

#### Event-Carried State Transfer
```java
public record OrderShippedEvent(
    Long orderId,
    String customerEmail,  // Carried state for consumer convenience
    String shippingAddress,
    String trackingNumber
) {}
```

#### Event Sourcing
Store events as the source of truth instead of current state.

## Event Bus Models
- **Point-to-Point** - One consumer (Queue)
- **Pub-Sub** - Multiple consumers (Topic)
- **Event Stream** - Ordered, replayable event log (Kafka)

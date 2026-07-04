# Six-Port Architecture Theory

## Core Concepts

### Six Port Types
1. **Inbound Driving Port** - Operations called by external actors (REST controller)
2. **Inbound Driven Port** - Internal events/notifications
3. **Outbound Driving Port** - Calls to external systems (remote services)
4. **Outbound Driven Port** - Persistence operations (database repositories)
5. **Inbound Event Port** - Events consumed from external systems
6. **Outbound Event Port** - Events published to external systems

### Port Definitions
```java
// 1. Inbound Driving Port
public interface CreateOrderPort {
    OrderResponse createOrder(OrderRequest request);
}

// 2. Outbound Driven Port (Persistence)
public interface OrderPersistencePort {
    Order save(Order order);
    Optional<Order> findById(OrderId id);
}

// 3. Outbound Driving Port (External service call)
public interface PaymentServicePort {
    PaymentResult processPayment(PaymentRequest request);
}

// 4. Outbound Event Port
public interface OrderEventPort {
    void publishOrderCreated(OrderCreatedEvent event);
}

// 5. Inbound Event Port
public interface OrderEventListenerPort {
    void onPaymentReceived(PaymentReceivedEvent event);
}

// 6. Inbound Driven Port (Notification)
public interface NotificationPort {
    void sendConfirmation(OrderConfirmation confirmation);
}
```

## Six-Port vs Hexagonal
- Six-Port makes port types explicit
- Adds event-specific ports
- Separates driving/driven more clearly
- More prescriptive naming and organization

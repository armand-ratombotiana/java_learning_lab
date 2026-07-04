# How Six-Port Architecture Works

## Architecture Flow
```
External Actor -> [Inbound Driving Port] -> Domain -> [Outbound Driven Port] -> Database
                                              |
                                     [Outbound Driving Port] -> External Service
                                              |
                                     [Event Port] -> Event Bus
                                              |
                                     [Notification Port] -> Email
```

## Port Types in Action

### 1. Inbound Driving Port (REST)
```java
public interface OrderInputPort {
    OrderResponse createOrder(CreateOrderRequest request);
}

@RestController
public class OrderRestController implements OrderInputPort {
    private final OrderService orderService;

    @PostMapping("/api/orders")
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }
}
```

### 2. Outbound Driven Port (Persistence)
```java
public interface OrderPersistencePort {
    Order save(Order order);
    Optional<Order> findById(String id);
}

@Component
public class JpaOrderPersistenceAdapter implements OrderPersistencePort {
    private final OrderJpaRepository repo;
    private final OrderMapper mapper;
}
```

### 3. Outbound Driving Port (External API)
```java
public interface PaymentGatewayPort {
    PaymentResponse charge(PaymentRequest request);
}

@Component
public class StripePaymentGatewayAdapter implements PaymentGatewayPort {
    private final StripeClient stripeClient;

    @Override
    public PaymentResponse charge(PaymentRequest request) {
        return stripeClient.charge(request.toStripeRequest());
    }
}
```

### 4. Event Ports
```java
public interface OrderEventPublisherPort {
    void publish(OrderEvent event);
}

@Component
public class KafkaOrderEventAdapter implements OrderEventPublisherPort {
    private final KafkaTemplate<String, OrderEvent> kafka;
}
```

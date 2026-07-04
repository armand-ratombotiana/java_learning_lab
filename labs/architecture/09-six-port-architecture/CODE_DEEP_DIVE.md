# Code Deep Dive: Six-Port Architecture

## Complete Example

### Port Definitions
```java
// 1. Inbound Driving Port
public interface OrderInputPort {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrder(String orderId);
}

// 2. Outbound Driven Port (Persistence)
public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(String id);
}

// 3. Outbound Driving Port (External Service)
public interface PaymentServicePort {
    PaymentResult processPayment(Payment payment);
}

// 4. Outbound Event Publisher Port
public interface OrderEventPublisherPort {
    void publishOrderCreated(OrderCreatedEvent event);
    void publishOrderCancelled(OrderCancelledEvent event);
}

// 5. Inbound Event Subscriber Port
public interface OrderEventSubscriberPort {
    void onPaymentCompleted(PaymentCompletedEvent event);
    void onPaymentFailed(PaymentFailedEvent event);
}

// 6. Notification Port
public interface NotificationPort {
    void sendOrderConfirmation(OrderConfirmation confirmation);
    void sendOrderCancellationNotice(OrderCancellation cancellation);
}
```

### Domain Service
```java
@Service
@RequiredArgsConstructor
public class OrderDomainService {

    private final OrderInputPort inputPort;
    private final OrderRepositoryPort repositoryPort;
    private final PaymentServicePort paymentPort;
    private final OrderEventPublisherPort eventPublisherPort;
    private final OrderEventSubscriberPort eventSubscriberPort;
    private final NotificationPort notificationPort;

    @Transactional
    public Order createOrder(OrderRequest request) {
        Order order = inputPort.createOrder(request);
        Order savedOrder = repositoryPort.save(order);
        PaymentResult payment = paymentPort.processPayment(
            new Payment(savedOrder.getId(), savedOrder.getTotal()));
        if (payment.isSuccess()) {
            eventPublisherPort.publishOrderCreated(
                new OrderCreatedEvent(savedOrder.getId()));
            notificationPort.sendOrderConfirmation(
                new OrderConfirmation(savedOrder.getCustomerEmail()));
        }
        return savedOrder;
    }

    @Transactional
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        Order order = repositoryPort.findById(event.getOrderId())
            .orElseThrow();
        order.markPaid();
        repositoryPort.save(order);
        eventPublisherPort.publishOrderCreated(
            new OrderCreatedEvent(order.getId()));
    }
}
```

### Adapter Implementations
```java
// Inbound Driving Adapter
@RestController
public class OrderRestAdapter implements OrderInputPort {
    @Override
    @PostMapping("/api/orders")
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }
}

// Outbound Driven Adapter
@Component
public class OrderJpaRepositoryAdapter implements OrderRepositoryPort {
    @Override
    public Order save(Order order) {
        return jpaRepo.save(mapper.toEntity(order)).toDomain();
    }
}

// Outbound Driving Adapter
@Component
public class StripePaymentAdapter implements PaymentServicePort {
    @Override
    public PaymentResult processPayment(Payment payment) {
        return stripeClient.charge(payment.getAmount(), payment.getCurrency());
    }
}
```

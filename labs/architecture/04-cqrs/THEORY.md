# CQRS Theory

## Core Concepts

### Command
A command expresses intent to change state. Commands are imperative (placeOrder, processPayment).
```java
public record PlaceOrderCommand(
    String orderId,
    String customerId,
    List<OrderItem> items,
    ShippingAddress address
) implements Command {}
```

### Query
A query requests state without side effects.
```java
public record GetOrderQuery(String orderId) implements Query {}
```

### Command Handler
```java
@Component
@RequiredArgsConstructor
public class PlaceOrderHandler implements CommandHandler<PlaceOrderCommand> {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    @Override
    public void handle(PlaceOrderCommand command) {
        Order order = new Order(command.orderId(), command.customerId());
        command.items().forEach(order::addItem);
        orderRepository.save(order);
        eventPublisher.publish(order.releaseEvents());
    }
}
```

### Query Handler
```java
@Component
@RequiredArgsConstructor
public class GetOrderHandler implements QueryHandler<GetOrderQuery, OrderView> {

    private final OrderViewRepository viewRepository;

    @Override
    public OrderView handle(GetOrderQuery query) {
        return viewRepository.findById(query.orderId())
            .orElseThrow(() -> new OrderNotFoundException(query.orderId()));
    }
}
```

## Separate Models
```java
// Write model (normalized, for consistency)
@Entity
public class Order {
    @Id private String id;
    @OneToMany(cascade = ALL)
    private List<LineItem> items;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}

// Read model (denormalized, for queries)
@Document
public class OrderView {
    @Id private String id;
    private String customerName;
    private List<ItemView> items;
    private BigDecimal total;
    private String status;
    private Instant lastUpdated;
}
```

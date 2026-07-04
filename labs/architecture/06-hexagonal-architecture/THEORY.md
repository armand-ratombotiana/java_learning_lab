# Hexagonal Architecture Theory

## Core Concepts

### Port (Interface)
A port defines how the application interacts with external systems.
```java
// Inbound Port - driving adapter
public interface OrderService {
    Order createOrder(CreateOrderCommand command);
    Order getOrder(OrderId id);
    void cancelOrder(OrderId id);
}

// Outbound Port - driven adapter
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(OrderId id);
    List<Order> findByCustomerId(CustomerId customerId);
}
```

### Adapter (Implementation)
An adapter implements a port for a specific technology.
```java
// Driving Adapter (REST)
@RestController
@RequestMapping("/api/orders")
public class OrderRestController implements OrderService {

    private final OrderService orderService; // inbound port

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest req) {
        Order order = orderService.createOrder(req.toCommand());
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}

// Driven Adapter (JPA)
@Component
public class JpaOrderRepository implements OrderRepository {

    private final SpringDataJpaOrderRepository jpaRepository;

    @Override
    public Order save(Order order) {
        return jpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id);
    }
}
```

## Dependency Rule
Dependencies point inward toward the domain. The domain layer has no external dependencies.

```
[Adapters] -> [Ports] -> [Domain Core]
  External      Contracts   Business Logic
```

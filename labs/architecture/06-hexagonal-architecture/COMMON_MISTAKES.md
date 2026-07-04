# Common Mistakes in Hexagonal Architecture

## 1. Framework Annotations in Domain
```java
// WRONG: Domain depends on framework
@Entity
public class Order {
    @Id private Long id;
    @Autowired private OrderService service; // NO!
}

// CORRECT: Pure POJO domain
public class Order {
    private OrderId id;
    // No annotations, no framework references
}
```

## 2. Leaking Adapter Details
```java
// WRONG: Domain returns HTTP-related types
public Order createOrder(CreateOrderCommand cmd) {
    return ResponseEntity.ok(order); // Domain knows about HTTP!
}

// CORRECT: Domain returns domain types
public Order createOrder(CreateOrderCommand cmd) {
    return new Order(cmd.customerId());
}
```

## 3. Anemic Port Definitions
```java
// WRONG: Port that doesn't abstract
public interface OrderRepository {
    JpaOrder findById(Long id); // Exposing JPA type!
}

// CORRECT: Port uses domain types
public interface OrderRepository {
    Optional<Order> findById(OrderId id);
}
```

## 4. Fat Adapters
```java
// WRONG: Adapter contains business logic
@RestController
public class OrderController {
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest req) {
        if (req.amount() > 10000) { // Business logic in adapter!
            throw new OrderLimitExceededException();
        }
    }
}

// CORRECT: Adapter only translates
@RestController
public class OrderController {
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest req) {
        Order order = createOrderUseCase.execute(req.toCommand());
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}
```

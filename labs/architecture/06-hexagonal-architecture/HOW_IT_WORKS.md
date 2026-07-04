# How Hexagonal Architecture Works

## Architecture Flow
```
Outside -> Driving Adapter -> Port -> Domain Core -> Port -> Driven Adapter -> Outside
```

## Complete Flow Example

### 1. Driving Adapter (REST)
```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final CreateOrderUseCase createOrderUseCase;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request) {
        // Adapter translates request to domain command
        CreateOrderCommand command = new CreateOrderCommand(
            new CustomerId(request.customerId()),
            request.items().stream()
                .map(i -> new OrderItem(i.productId(), i.quantity()))
                .toList()
        );
        Order order = createOrderUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(OrderResponse.from(order));
    }
}
```

### 2. Port (Use Case Interface)
```java
public interface CreateOrderUseCase {
    Order execute(CreateOrderCommand command);
}
```

### 3. Domain Core
```java
@Service
public class CreateOrderService implements CreateOrderUseCase {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public Order execute(CreateOrderCommand command) {
        CustomerId customerId = command.customerId();
        List<OrderItem> items = command.items();
        // Validate products exist
        items.forEach(item -> productRepository.findById(item.productId())
            .orElseThrow(() -> new ProductNotFoundException(item.productId())));
        Order order = new Order(customerId, items);
        return orderRepository.save(order);
    }
}
```

### 4. Outbound Port
```java
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(OrderId id);
}
```

### 5. Driven Adapter (JPA)
```java
@Component
public class JpaOrderRepository implements OrderRepository {
    private final SpringDataJpaRepository jpa;

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderEntity.from(order);
        return jpa.save(entity).toDomain();
    }
}
```

# Code Deep Dive: Hexagonal Architecture

## Complete Implementation

### Domain Core
```java
// Inbound Port
public interface CreateOrderUseCase {
    Order execute(CreateOrderCommand command);
}

// Outbound Port
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(OrderId id);
}

// Domain Service
@Service
@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderEventPublisher eventPublisher;

    @Override
    @Transactional
    public Order execute(CreateOrderCommand command) {
        List<Product> products = validateProducts(command.items());
        Order order = Order.create(command.customerId(), products);
        order = orderRepository.save(order);
        eventPublisher.publish(new OrderCreatedEvent(order));
        return order;
    }

    private List<Product> validateProducts(List<OrderItem> items) {
        return items.stream()
            .map(item -> productRepository.findById(item.productId())
                .orElseThrow(() -> new ProductNotFoundException(item.productId())))
            .toList();
    }
}

// Domain Model
@Entity
@Table(name = "orders")
public class Order {
    @EmbeddedId
    private OrderId id;
    private CustomerId customerId;
    private Money total;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public static Order create(CustomerId customerId, List<Product> products) {
        Order order = new Order();
        order.id = OrderId.generate();
        order.customerId = customerId;
        order.total = calculateTotal(products);
        order.status = OrderStatus.PENDING;
        return order;
    }
}
```

### Driving Adapter (REST)
```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request) {
        CreateOrderCommand command = CreateOrderCommand.builder()
            .customerId(request.customerId())
            .items(request.items().stream()
                .map(i -> new OrderItem(i.productId(), i.quantity()))
                .toList())
            .build();
        Order order = createOrderUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(OrderResponse.from(order));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        Order order = getOrderUseCase.execute(new OrderId(orderId));
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}
```

### Driven Adapter (JPA)
```java
@Component
@RequiredArgsConstructor
public class JpaOrderRepository implements OrderRepository {

    private final SpringDataJpaRepository jpaRepository;
    private final OrderMapper mapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id.getValue())
            .map(mapper::toDomain);
    }
}

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderEntity toEntity(Order order);
    Order toDomain(OrderEntity entity);
}
```

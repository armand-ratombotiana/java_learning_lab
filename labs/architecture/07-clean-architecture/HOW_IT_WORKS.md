# How Clean Architecture Works

## Request Flow
```
Controller -> InputBoundary -> UseCaseInteractor -> OutputBoundary -> Presenter -> View
                                |                   |
                           Entity Gateway (interface)
                                |
                           Repository (implementation)
```

## Complete Flow Example

### 1. Controller (Interface Adapter)
```java
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final CreateOrderUseCase createOrderUseCase;
    private final OrderPresenter orderPresenter;

    @PostMapping
    public OrderResponseModel createOrder(@RequestBody OrderRequestModel request) {
        CreateOrderInputData inputData = CreateOrderInputData.builder()
            .customerId(request.getCustomerId())
            .items(request.getItems())
            .build();
        createOrderUseCase.execute(inputData, orderPresenter);
        return orderPresenter.getResponse();
    }
}
```

### 2. Use Case Interactor
```java
@Component
@RequiredArgsConstructor
public class CreateOrderInteractor implements CreateOrderUseCase {

    private final OrderRepository orderRepository;

    @Override
    public void execute(CreateOrderInputData input, CreateOrderOutputBoundary output) {
        Order order = Order.create(input.getCustomerId(), input.getItems());
        order.submit();
        orderRepository.save(order);
        output.present(CreateOrderResponseData.from(order));
    }
}
```

### 3. Entity
```java
public class Order {
    private OrderId id;
    private List<LineItem> items;

    public static Order create(CustomerId customerId, List<OrderItem> items) {
        Order order = new Order();
        order.id = OrderId.generate();
        order.customerId = customerId;
        order.items = items.stream().map(LineItem::new).toList();
        order.status = OrderStatus.DRAFT;
        return order;
    }

    public void submit() {
        if (items.isEmpty()) throw new IllegalStateException("Empty order");
        this.status = OrderStatus.SUBMITTED;
    }
}
```

### 4. Gateway (Interface)
```java
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(OrderId id);
}
```

### 5. Gateway Implementation
```java
@Component
public class JpaOrderRepository implements OrderRepository {
    private final SpringDataJpaOrderRepository repo;
    private final OrderMapper mapper;

    @Override
    public void save(Order order) {
        repo.save(mapper.toEntity(order));
    }
}
```

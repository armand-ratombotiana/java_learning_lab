# Code Deep Dive: Clean Architecture

## Complete Implementation

### Entity Layer
```java
// Enterprise business rules
public class Order {
    private OrderId id;
    private CustomerId customerId;
    private List<LineItem> items;
    private Money total;
    private OrderStatus status;

    public static Order create(CustomerId customerId, List<OrderItemInput> items) {
        Order order = new Order();
        order.id = OrderId.generate();
        order.customerId = customerId;
        order.items = items.stream().map(LineItem::new).toList();
        order.total = order.calculateTotal();
        order.status = OrderStatus.DRAFT;
        return order;
    }

    public void submit() {
        validateCanSubmit();
        this.status = OrderStatus.SUBMITTED;
    }

    public void cancel() {
        if (status == OrderStatus.SUBMITTED || status == OrderStatus.DRAFT) {
            this.status = OrderStatus.CANCELLED;
        } else {
            throw new IllegalStateException("Cannot cancel order in status: " + status);
        }
    }

    private Money calculateTotal() {
        return items.stream()
            .map(LineItem::getSubtotal)
            .reduce(Money.ZERO, Money::add);
    }

    private void validateCanSubmit() {
        if (items.isEmpty()) throw new IllegalStateException("Cannot submit empty order");
        if (status != OrderStatus.DRAFT) throw new IllegalStateException("Already submitted");
    }
}
```

### Use Case Layer
```java
// Input boundary
public interface CreateOrderUseCase {
    void execute(CreateOrderInputData input, CreateOrderOutputBoundary output);
}

// Output boundary
public interface CreateOrderOutputBoundary {
    void present(CreateOrderOutputData output);
    void presentError(String error);
}

// Data transfer objects
public class CreateOrderInputData {
    private final String customerId;
    private final List<OrderItemInput> items;
    // Constructor, getters
}

public class CreateOrderOutputData {
    private final String orderId;
    private final String status;
    private final BigDecimal total;
    // Constructor, getters
}

// Use case (interactor)
@Component
@RequiredArgsConstructor
public class CreateOrderInteractor implements CreateOrderUseCase {

    private final OrderRepository orderRepository;

    @Override
    public void execute(CreateOrderInputData input, CreateOrderOutputBoundary output) {
        try {
            Order order = Order.create(
                new CustomerId(input.getCustomerId()),
                input.getItems()
            );
            order.submit();
            orderRepository.save(order);
            output.present(CreateOrderOutputData.from(order));
        } catch (Exception e) {
            output.presentError(e.getMessage());
        }
    }
}

// Repository interface (outbound gateway)
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(OrderId id);
}
```

### Adapter Layer
```java
// Controller
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final OrderPresenter orderPresenter;

    @PostMapping
    public ResponseEntity<OrderResponseModel> createOrder(
            @RequestBody @Valid OrderRequestModel request) {
        CreateOrderInputData input = new CreateOrderInputData(
            request.getCustomerId(), request.getItems());
        createOrderUseCase.execute(input, orderPresenter);
        return ResponseEntity.ok(orderPresenter.getResponse());
    }
}

// Presenter
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OrderPresenter implements CreateOrderOutputBoundary {

    private OrderResponseModel response;

    @Override
    public void present(CreateOrderOutputData output) {
        this.response = new OrderResponseModel(
            output.getOrderId(), output.getStatus(), output.getTotal());
    }

    @Override
    public void presentError(String error) {
        throw new OrderCreationException(error);
    }

    public OrderResponseModel getResponse() {
        return response;
    }
}

// Gateway implementation
@Component
@RequiredArgsConstructor
public class JpaOrderRepository implements OrderRepository {

    private final SpringDataJpaOrderRepository repo;
    private final OrderMapper mapper;

    @Override
    public void save(Order order) {
        repo.save(mapper.toEntity(order));
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return repo.findById(id.getValue())
            .map(mapper::toDomain);
    }
}
```

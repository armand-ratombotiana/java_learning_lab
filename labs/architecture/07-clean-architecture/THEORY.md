# Clean Architecture Theory

## Layers
```
+------------------------------------------+
|           Frameworks & Drivers            |
|  (Spring, Database, Web, Messaging)       |
+------------------------------------------+
|         Interface Adapters                |
|  (Controllers, Presenters, Gateways)      |
+------------------------------------------+
|         Application Business Rules        |
|  (Use Cases / Interactors)                |
+------------------------------------------+
|         Enterprise Business Rules         |
|  (Entities)                               |
+------------------------------------------+
```

## Dependency Rule
Dependencies point inward. Nothing in an inner layer can know about something in an outer layer.

## Entity
```java
// Enterprise business rules
public class Order {
    private OrderId id;
    private Money total;
    private OrderStatus status;

    public void submit() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Only draft orders can be submitted");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot submit empty order");
        }
        this.status = OrderStatus.SUBMITTED;
    }
}
```

## Use Case (Interactor)
```java
// Application business rules
@Component
public class CreateOrderInteractor implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderPresenter presenter;

    @Override
    public OrderResponse execute(CreateOrderRequest request) {
        Order order = Order.builder()
            .customerId(request.getCustomerId())
            .items(request.getItems())
            .build();
        order.submit();
        orderRepository.save(order);
        return presenter.present(order);
    }
}
```

## Interface Adapter
```java
@RestController
public class OrderController {
    private final CreateOrderUseCase createOrderUseCase;

    @PostMapping("/orders")
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        return createOrderUseCase.execute(request);
    }
}
```

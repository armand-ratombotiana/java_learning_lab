# Step-by-Step Clean Architecture

## Step 1: Define Entity
```java
public class Order {
    private OrderId id;
    private OrderStatus status;

    public void submit() {
        if (this.status == OrderStatus.DRAFT) {
            this.status = OrderStatus.SUBMITTED;
        }
    }
}
```

## Step 2: Define Use Case Interface
```java
public interface CreateOrderUseCase {
    void execute(CreateOrderInputData input, CreateOrderOutputBoundary output);
}
```

## Step 3: Define Output Boundary
```java
public interface CreateOrderOutputBoundary {
    void present(CreateOrderOutputData output);
    void presentError(String error);
}
```

## Step 4: Implement Interactor
```java
@Component
public class CreateOrderInteractor implements CreateOrderUseCase {
    private final OrderRepository repository;

    public void execute(CreateOrderInputData input, CreateOrderOutputBoundary output) {
        Order order = Order.create(input.getCustomerId(), input.getItems());
        order.submit();
        repository.save(order);
        output.present(CreateOrderOutputData.from(order));
    }
}
```

## Step 5: Create Presenter
```java
@Component
public class OrderPresenter implements CreateOrderOutputBoundary {
    private OrderResponseModel response;

    public void present(CreateOrderOutputData output) {
        this.response = new OrderResponseModel(output);
    }
}
```

## Step 6: Create Controller
```java
@RestController
public class OrderController {
    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestModel request) {
        // Instantiate use case, call execute
    }
}
```

## Step 7: Wire Dependencies
```java
@Configuration
public class AppConfig {
    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderRepository repo) {
        return new CreateOrderInteractor(repo);
    }
}
```

## Step 8: Verify with ArchUnit
```java
@Test
void cleanArchitectureTest() {
    // Verify dependency rule
}
```

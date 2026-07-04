# Common Mistakes in Clean Architecture

## 1. Entity Contains Framework Code
```java
// WRONG: Entity uses JPA annotations
@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue
    private Long id; // Framework coupling!
}

// CORRECT: Pure POJO
public class Order {
    private OrderId id; // Domain type
}
```

## 2. Use Case Returns Entity
```java
// WRONG: Interactor returns entity
public Order execute(CreateOrderInputData input) {
    Order order = Order.create(input);
    order.submit();
    return order; // Exposing entity to outer layers!
}

// CORRECT: Use output boundary
public void execute(CreateOrderInputData input, CreateOrderOutputBoundary output) {
    Order order = Order.create(input);
    output.present(CreateOrderOutputData.from(order));
}
```

## 3. Using Spring Annotations in Use Case
```java
// WRONG: Use case knows about transactions
@Component
public class CreateOrderInteractor implements CreateOrderUseCase {
    @Transactional // Should be in adapter layer
    public void execute(...) { }
}

// CORRECT: Use case is plain
@Component
public class CreateOrderInteractor implements CreateOrderUseCase {
    public void execute(...) { }
}
```

## 4. Fat Controllers
```java
// WRONG: Controller has business logic
@PostMapping("/orders")
public Order createOrder(@RequestBody OrderRequest req) {
    if (req.getItems().size() > 10) { // Business rule!
        throw new ValidationException();
    }
}

// CORRECT: Controller delegates
@PostMapping("/orders")
public ResponseEntity<?> createOrder(@RequestBody OrderRequest req) {
    createOrderUseCase.execute(req.toInput(), presenter);
    return ResponseEntity.ok(presenter.getResponse());
}
```

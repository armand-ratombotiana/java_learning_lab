# Refactoring to Hexagonal Architecture

## Traditional Layered to Hexagonal Migration

### Step 1: Define Ports
```java
// Extract interfaces from existing services
// BEFORE: Concrete service
@Service
public class OrderService {
    @Autowired
    private OrderRepository repo;
}

// AFTER: Port interface
public interface OrderServicePort {
    Order createOrder(CreateOrderCommand cmd);
}

@Service
public class OrderService implements OrderServicePort {
    private final OrderRepository repo;
}
```

### Step 2: Extract Business Logic
```java
// Move business logic from controllers to domain services
// BEFORE: Controller with logic
@PostMapping
public Order createOrder(@RequestBody OrderRequest req) {
    // Validation logic here...
    // Business rules here...
}

// AFTER: Controller delegates
@PostMapping
public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest req) {
    Order order = createOrderUseCase.execute(req.toCommand());
    return ResponseEntity.ok(OrderResponse.from(order));
}
```

### Step 3: Create Adapters
```java
// Extract repository implementation to adapter
@Component
public class JpaOrderRepositoryAdapter implements OrderRepositoryPort {
    private final SpringDataJpaRepository repo;
    // Implementation
}
```

### Step 4: Add Architectural Tests
```java
@Test
void verifyHexagonalArchitecture() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..")
        .should().onlyDependOnClassesThat()
        .resideInAnyPackage("..domain..", "java..");
    rule.check(classes);
}
```

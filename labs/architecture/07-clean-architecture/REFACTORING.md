# Refactoring to Clean Architecture

## Step-by-Step Migration

### Phase 1: Identify Entities
```java
// 1. Extract pure business objects from existing code
// BEFORE: Service with God class
@Service
public class OrderService {
    public void placeOrder(OrderRequest req) {
        // Validation, business logic, persistence, notification all mixed
    }
}

// AFTER: Extract Entity
public class Order {
    // Pure business rules only
}
```

### Phase 2: Extract Use Cases
```java
// 2. Identify distinct use cases from services
// Each public method in service may represent a use case
// BEFORE:
@Service
public class OrderService {
    public void createOrder() { }
    public void cancelOrder() { }
    public void shipOrder() { }
}

// AFTER: Each becomes a use case interface
public interface CreateOrderUseCase { void execute(CreateOrderInputData input, CreateOrderOutputBoundary output); }
public interface CancelOrderUseCase { void execute(CancelOrderInputData input, CancelOrderOutputBoundary output); }
```

### Phase 3: Create Adapters
```java
// 3. Separate controllers and presenters
// Controller handles HTTP concerns
// Presenter formats output
```

### Phase 4: Add ArchUnit Tests
```java
// 4. Enforce boundaries with tests
@Test
void verifyDependencyRule() {
    // Entity layer only depends on Java
    // Use case depends on entity
    // Adapter depends on entity + use case
}
```

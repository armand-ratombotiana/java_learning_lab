# Debugging Clean Architecture

## Common Issues

### 1. Presenter Not Properly Scoped
```java
// WRONG: Singleton presenter shared between requests
@Component
public class OrderPresenter implements CreateOrderOutputBoundary {
    private OrderResponseModel response; // Shared mutable state!
}

// CORRECT: Request-scoped presenter
@Component
@RequestScope
public class OrderPresenter implements CreateOrderOutputBoundary {
    private OrderResponseModel response; // Per-request
}
```

### 2. Wrong Layer Import
```java
// ArchUnit catches this
@Test
void entityMustNotDependOnOuterLayers() {
    ArchRule rule = classes()
        .that().resideInAPackage("..entity..")
        .should().onlyDependOnClassesThat()
        .resideInAnyPackage("..entity..", "java..");
    rule.check(classes);
}
```

### 3. Use Case Error Handling
```java
@Component
@Slf4j
public class CreateOrderInteractor implements CreateOrderUseCase {
    public void execute(CreateOrderInputData input, CreateOrderOutputBoundary output) {
        try {
            Order order = Order.create(input);
            order.submit();
            repository.save(order);
            output.present(CreateOrderOutputData.from(order));
        } catch (Exception e) {
            log.error("Failed to create order", e);
            output.presentError(e.getMessage());
        }
    }
}
```

### 4. Circular Dependency Detection
```bash
# ArchUnit detects circular dependencies
slices().matching("com.company.(*)..")
    .should().beFreeOfCycles()
    .check(classes);
# Error: Circular dependency detected between packages
```

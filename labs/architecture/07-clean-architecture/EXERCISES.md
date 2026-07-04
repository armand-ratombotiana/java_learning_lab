# Clean Architecture Exercises

## Beginner Exercises

### Exercise 1: Identify Layers
Given a Spring Boot application, identify which classes belong to entity, use case, and adapter layers.

### Exercise 2: Extract Entity
Take an existing @Entity annotated class and extract a pure domain entity without annotations.

## Intermediate Exercises

### Exercise 3: Implement Use Case
Implement a complete use case for money transfer:
- Entity: Account
- Use case: TransferFundsUseCase
- Output boundary: TransferOutputBoundary
- Presenter: TransferPresenter

### Exercise 4: Add Presenter
Add a presenter that formats output for both JSON and HTML:
```java
@Component
@RequestScope
public class OrderPresenter implements CreateOrderOutputBoundary { }
```

### Exercise 5: ArchUnit Tests
Write ArchUnit tests that enforce:
- Entity layer doesn't depend on adapter
- Use case layer doesn't depend on controller
- No cyclic dependencies between packages

## Advanced Exercises

### Exercise 6: Multi-format Output
Create a clean architecture endpoint that supports both REST (JSON) and GraphQL outputs using separate presenters.

### Exercise 7: Full Clean Architecture Service
Build a complete Clean Architecture microservice:
- Entity: Order, LineItem, Money
- Use Cases: Create, Get, Cancel Order
- Adapters: REST controller, JPA repository, Kafka producer
- Tests: Unit + integration + ArchUnit

### Exercise 8: Framework Swap
Implement two versions of the same adapter (JPA and MyBatis) and verify the use case doesn't change.

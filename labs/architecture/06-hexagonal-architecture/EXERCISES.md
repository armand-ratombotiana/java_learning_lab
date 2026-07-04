# Hexagonal Architecture Exercises

## Beginner Exercises

### Exercise 1: Identify Ports
Given a simple order service, identify inbound and outbound ports.

### Exercise 2: Create an Adapter
Implement a MongoDB adapter for an existing OrderRepository port.

## Intermediate Exercises

### Exercise 3: Complete Hexagonal Service
Build a complete hexagonal customer management service:
- Domain: Customer entity, CustomerId value object
- Ports: CreateCustomerUseCase, CustomerRepository
- Adapters: REST controller, JPA repository

### Exercise 4: Multiple Adapters
Add a second driving adapter (messaging) to the customer service:
```java
@Component
public class CustomerMessageHandler {
    @Autowired
    private CreateCustomerUseCase useCase;

    @EventListener
    public void handle(CustomerRegistrationEvent event) {
        useCase.execute(event.toCommand());
    }
}
```

### Exercise 5: ArchUnit Tests
Write ArchUnit tests that enforce hexagonal rules:
- Domain doesn't depend on adapters
- Adapters depend on domain ports
- No framework annotations in domain

## Advanced Exercises

### Exercise 6: Adapter Decorator
Implement a decorator pattern around a port for cross-cutting concerns:
```java
@Component
public class LoggingOrderRepositoryDecorator implements OrderRepository {
    private final OrderRepository delegate;
    // Add logging to every method call
}
```

### Exercise 7: Hexagonal Microservice
Build a complete hexagonal microservice with:
- REST adapter (driving)
- JPA adapter (driven)
- Kafka adapter (driven, event publishing)
- All tested with Testcontainers
- ArchUnit verification

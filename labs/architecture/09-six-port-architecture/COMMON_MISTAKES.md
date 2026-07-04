# Common Mistakes in Six-Port Architecture

## 1. Missing Port Types
```java
// WRONG: Only using 2-3 port types
public class OrderService {
    private final OrderRepositoryPort repo; // Only persistence
    // Missing event port, notification port, etc.
}

// CORRECT: Use appropriate port types for each external interaction
```

## 2. Inconsistent Port Naming
```java
// WRONG: Inconsistent naming conventions
public interface OrderRepo {}
public interface IPaymentGateway {}
public interface OrderEventPublisher {}

// CORRECT: Consistent Port suffix
public interface OrderRepositoryPort {}
public interface PaymentGatewayPort {}
public interface OrderEventPublisherPort {}
```

## 3. Adapter with Business Logic
```java
// WRONG: Business logic in adapter
@Component
public class RestOrderAdapter implements OrderInputPort {
    public OrderResponse createOrder(OrderRequest request) {
        if (request.getAmount() > 10000) { // Business rule!
            throw new ValidationException();
        }
    }
}

// CORRECT: Adapter only translates
@Component
public class RestOrderAdapter implements OrderInputPort {
    public OrderResponse createOrder(OrderRequest request) {
        return orderService.createOrder(request); // Delegates to domain
    }
}
```

## 4. Exposing Adapter Implementation Details
```java
// WRONG: Port reveals adapter technology
public interface OrderRepositoryPort {
    JpaOrder save(Order order); // Exposes JPA type!
}

// CORRECT: Port uses domain types only
public interface OrderRepositoryPort {
    Order save(Order order);
}
```

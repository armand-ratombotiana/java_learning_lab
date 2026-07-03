# Why Abstraction & Interfaces Matter

## Loose Coupling

Abstraction decouples what something does from how it does it. A service layer that depends on `PaymentGateway` (interface) is loosely coupled — it doesn't care whether the implementation uses Stripe, PayPal, or Square. The implementation can change without affecting the caller.

```java
// Loosely coupled: depends on abstraction
public class OrderService {
    private final PaymentGateway gateway;

    public OrderService(PaymentGateway gateway) {
        this.gateway = gateway;  // Any implementation works
    }
}
```

## Testing

Interfaces enable mocking. Testing `OrderService` with `PaymentGateway` interface is trivial — pass a mock that returns success. Without the interface, testing requires a real payment provider or complex mocking of concrete classes.

## Evolving APIs

Default methods (Java 8+) allowed the Java team to add `stream()` to `Collection` without breaking every existing implementation. This was transformative for the Java ecosystem — APIs can now evolve safely.

## Plugin Architectures

Interfaces define plugin contracts. A logging framework defines a `LogAppender` interface. Third-party developers implement it for file, database, cloud, or custom logging. The framework discovers and invokes implementations polymorphically.

## Real-World Examples

- JDBC: `Connection`, `Statement`, `ResultSet` are interfaces. MySQL, PostgreSQL, Oracle provide implementations.
- JPA: `EntityManager` interface. Hibernate, EclipseLink, OpenJPA provide implementations.
- Spring: `ApplicationContext`, `BeanFactory`, `Environment` are interfaces with multiple implementations.
- Java Streams: `Stream<T>` interface with pipeline implementations (lazy, parallel support).

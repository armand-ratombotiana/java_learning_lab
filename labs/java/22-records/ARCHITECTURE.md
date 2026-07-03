# Architectural Patterns with Records

## Value Objects vs. Entities

Records are natural for **value objects** — objects defined by their state rather than identity:

```java
// Value Object → use record
record Money(String currency, BigDecimal amount) {}
record EmailAddress(String value) {}
record GeoLocation(double latitude, double longitude) {}

// Entity → use class
class Customer {
    private final CustomerId id;
    private String name;
    private EmailAddress email;
    // ... mutable state
}
```

This architectural distinction is fundamental to Domain-Driven Design (DDD):
- **Value objects** are immutable, have value equality, and can be shared
- **Entities** have identity, lifecycle, and mutable state

Records make this distinction syntactically visible, which helps enforce DDD principles.

## Layered Architecture with Records

### Data Transfer Objects (DTOs)
Records are ideal for DTOs across architectural boundaries:

```java
// API layer — request/response DTOs
public record CreateUserRequest(String name, String email) {}
public record UserResponse(long id, String name, String email, Instant createdAt) {}

// Controller
@RestController
class UserController {
    @PostMapping("/users")
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        // ...
    }
}
```

### Repository Layer
Records work well for query results:

```java
public record UserSummary(long id, String name, String email) {}

@Repository
class UserRepository {
    public List<UserSummary> findActiveUsers() {
        return jdbcTemplate.query(
            "SELECT id, name, email FROM users WHERE active = true",
            (rs, rowNum) -> new UserSummary(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email")
            )
        );
    }
}
```

### Domain Events
Records are natural for domain events (immutable by design):

```java
public record UserRegisteredEvent(
    String userId,
    String email,
    Instant occurredAt
) implements DomainEvent {}

public record OrderShippedEvent(
    String orderId,
    String trackingNumber,
    Instant shippedAt
) implements DomainEvent {}
```

## Functional Architecture with Records

### Pipeline Pattern
Records enable type-safe data transformations:

```java
// Pipeline stages as records
record RawData(String source) {}
record ParsedData(Map<String, Object> fields) {}
record ValidatedData(Map<String, Object> fields, List<String> errors) {}
record ProcessedResult(boolean success, String message) {}

public ProcessedResult process(RawData raw) {
    ParsedData parsed = parse(raw);
    ValidatedData validated = validate(parsed);
    if (!validated.errors().isEmpty()) {
        return new ProcessedResult(false, "Validation failed: " + validated.errors());
    }
    return save(validated);
}
```

### Algebraic Data Type Architecture
Records with sealed classes create algebraic data types:

```java
// Sum type — one of several alternatives
sealed interface PaymentMethod 
    permits CreditCard, BankTransfer, CryptoWallet, GiftCard {}

// Product types — each alternative has its own fields
record CreditCard(String cardNumber, String expiry, String cvv) implements PaymentMethod {}
record BankTransfer(String accountNumber, String sortCode) implements PaymentMethod {}
record CryptoWallet(String address, String currency) implements PaymentMethod {}
record GiftCard(String code, BigDecimal balance) implements PaymentMethod {}

// Pattern matching on algebraic types
class PaymentProcessor {
    public PaymentResult process(PaymentMethod method, BigDecimal amount) {
        return switch (method) {
            case CreditCard(var card, var exp, var cvv) -> processCardPayment(card, exp, cvv, amount);
            case BankTransfer(var acct, var sort) -> processBankTransfer(acct, sort, amount);
            case CryptoWallet(var addr, var curr) -> processCryptoPayment(addr, curr, amount);
            case GiftCard(var code, var bal) -> processGiftCard(code, bal, amount);
        };
    }
}
```

## Configuration Architecture

Records are excellent for typed configurations:

```java
public record ServerConfig(
    String host,
    int port,
    boolean useTls,
    Duration timeout,
    int maxConnections,
    List<String> allowedOrigins
) {
    public static ServerConfig fromProperties(Properties props) {
        return new ServerConfig(
            props.getProperty("server.host", "localhost"),
            Integer.parseInt(props.getProperty("server.port", "8080")),
            Boolean.parseBoolean(props.getProperty("server.tls", "true")),
            Duration.ofMillis(Long.parseLong(props.getProperty("server.timeout", "5000"))),
            Integer.parseInt(props.getProperty("server.max-connections", "100")),
            List.of(props.getProperty("server.allowed-origins", "*").split(","))
        );
    }
}
```

## Microservices Architecture

### Service Contracts
Records define clear service contracts:

```java
// Shared between services
record OrderCreatedEvent(String orderId, String customerId, List<OrderItem> items, 
                         Money total, Instant timestamp) {}
record OrderItem(String productId, int quantity, Money unitPrice) {}
```

### Event Messages
Records work with messaging systems:

```java
// Kafka event with key and payload
record PaymentEvent(String transactionId, String userId, Money amount, PaymentStatus status) {
    public String key() { return userId; }
}

// Serialization as JSON
// {"transactionId":"...","userId":"...","amount":{"currency":"USD","value":99.99},"status":"COMPLETED"}
```

## Anti-Patterns to Avoid

### Don't Use Records for JPA Entities
```java
// ANTI-PATTERN: Records don't work with JPA
@Entity
@Table(name = "users")
public record User(@Id Long id, String name, String email) {}  // Won't work
```

### Don't Use Records for Deeply Nested Mutable Structures
```java
// Questionable: Nested records with mutable components
record Order(List<OrderLine> lines) {}  // lines should be immutable
```

### Don't Use Records for State Machines
Stateful objects with lifecycle should use traditional classes with careful state management.

## Migration Strategy

1. **Start with DTOs**: Convert DTOs to records first (lowest risk)
2. **Convert value objects**: Domain-level value objects (Money, Email, Phone)
3. **Refactor method returns**: Multiple return values → records
4. **Adopt local records**: Use in stream pipelines
5. **Introduce sealed+records**: Algebraic data types for domain models

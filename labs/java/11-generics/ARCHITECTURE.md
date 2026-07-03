# Generics — Architectural Considerations

## Layered Type Safety

In a layered architecture, generics maintain type safety across boundaries:

```
Controller Tier:  OrderController (uses OrderService)
                           ↓
Service Tier:     OrderService<Order, OrderRepository>
                           ↓
Repository Tier:  OrderRepository implements Repository<Order, Long>
                           ↓
Data Tier:        JpaRepository<Order, Long> (Spring Data JPA)
```

Each layer declares types explicitly. Generics ensure that:
- Controllers don't accidentally mix Order and User types
- Services work with the correct entity type
- Repositories return the correct entity type without casting

## Generic Abstract Base Classes

```java
public abstract class BaseService<T, ID, R extends Repository<T, ID>> {
    protected final R repository;

    public BaseService(R repository) {
        this.repository = repository;
    }

    public T findById(ID id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Entity not found"));
    }

    public T save(T entity) {
        return repository.save(entity);
    }

    public void deleteById(ID id) {
        repository.deleteById(id);
    }
}
```

Concrete services inherit type safety:

```java
@Service
public class OrderService extends BaseService<Order, Long, OrderRepository> {
    public OrderService(OrderRepository repository) {
        super(repository);
    }
}
```

## Repository Pattern with Generics

```java
public interface Repository<T, ID> {
    T findById(ID id);
    List<T> findAll();
    T save(T entity);
    void delete(T entity);
    boolean existsById(ID id);
}
```

## Generic DTO Assemblers

```java
public abstract class Assembler<T, D> {
    public abstract D toDto(T entity);
    public abstract T toEntity(D dto);

    public List<D> toDtoList(List<T> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public List<T> toEntityList(List<D> dtos) {
        return dtos.stream().map(this::toEntity).toList();
    }
}
```

## Generic Result Types

```java
public sealed interface Result<T> permits Success<T>, Failure<T> {}
public record Success<T>(T value) implements Result<T> {}
public record Failure<T>(String error, Exception cause) implements Result<T> {}

// Usage:
public Result<Order> placeOrder(OrderRequest request) {
    try {
        Order order = orderService.create(request);
        return new Success<>(order);
    } catch (Exception e) {
        return new Failure<>("Order failed", e);
    }
}
```

## Strategy Pattern with Generics

```java
public interface PaymentStrategy<T extends PaymentRequest> {
    PaymentResult process(T request);
}

public class CreditCardStrategy implements PaymentStrategy<CreditCardRequest> { ... }
public class PayPalStrategy implements PaymentStrategy<PayPalRequest> { ... }

public class PaymentProcessor {
    private final Map<Class<?>, PaymentStrategy<?>> strategies = new HashMap<>();
    
    public <T extends PaymentRequest> void registerStrategy(
            Class<T> requestType, PaymentStrategy<T> strategy) {
        strategies.put(requestType, strategy);
    }
    
    @SuppressWarnings("unchecked")
    public <T extends PaymentRequest> PaymentResult process(T request) {
        PaymentStrategy<T> strategy = (PaymentStrategy<T>) strategies.get(request.getClass());
        if (strategy == null) throw new IllegalArgumentException("No strategy for " + request.getClass());
        return strategy.process(request);
    }
}
```

## When Generics Overcomplicate

Generics add complexity. Consider if they're worth it:
- **Simple wrappers**: A `Box<Order>` is overkill if you only need `Order`
- **Deeply nested types**: `Map<String, Map<String, List<Order>>>` suggests missing abstractions
- **Complex bounded wildcards**: If you need 3+ bounds, the design may be wrong

## Architectural Guidelines

1. **Use generics in library/framework code** but be judicious in application code
2. **Hide complex generic signatures** behind well-named types: `type OrderMap = Map<String, List<Order>>` via subclassing
3. **Prefer composition over generic inheritance** for deep hierarchies
4. **Type parameters are API** — they're part of your contract; don't over-constrain
5. **Document type parameter semantics** — what does `T` represent in this context?

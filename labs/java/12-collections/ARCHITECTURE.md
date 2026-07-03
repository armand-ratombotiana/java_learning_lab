# Collections — Architectural Considerations

## Interface vs Implementation Programming

Always program to the interface, not the implementation:

```java
// Good: Flexible
List<String> list = new ArrayList<>();
// Can change to:
// List<String> list = new LinkedList<>();

// Bad: Tight coupling
ArrayList<String> list = new ArrayList<>();
```

## Collection Choice by Access Pattern

### Primary Key Lookup
```java
// Customer by ID: HashMap
Map<Long, Customer> customerById = new HashMap<>();
```

### Range Queries
```java
// Orders by date: TreeMap for sorted iteration
TreeMap<LocalDate, List<Order>> ordersByDate = new TreeMap<>();
```

### Insertion Order
```java
// Recently viewed: LinkedHashMap preserves order
LinkedHashMap<Long, Product> recentlyViewed = new LinkedHashMap<>(16, 0.75f, true);
```

### Unique Constraint
```java
// Username uniqueness: HashSet
Set<String> usernames = new HashSet<>();
```

## Layered Architecture Collection Usage

```
Controller:
  List<OrderDTO> getOrders()  →  DTO list (immutable)

Service:
  Map<Long, Order> ordersById  →  Entity lookup
  List<Order> processOrders() →  Business logic

Repository:
  List<Order> findAllByStatus(Status s)
  Optional<Order> findById(Long id)
```

## Batch Operations and Streaming

For large datasets, prefer streaming to loading entire collections:

```java
// BAD: Loads all into memory
List<Order> allOrders = repository.findAll();
allOrders.stream().filter(...).collect(toList());

// GOOD: Database pagination or streaming
Stream<Order> orderStream = repository.streamAll();
orderStream.filter(...).limit(100).collect(toList());
```

## Immutability in Architecture

```java
// Service layer: Return immutable collections
public List<Order> getCompletedOrders() {
    return completedOrders.stream()
        .filter(Order::isCompleted)
        .toList();  // Java 16+ — immutable
}

// Internal state: Defensive copies
public void setDependencies(List<Dependency> deps) {
    this.dependencies = new ArrayList<>(deps);  // Defensive copy
}
```

## Collection Patterns in Frameworks

### Spring Data JPA
```java
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
    Page<Order> findAll(Pageable pageable);
    Slice<Order> findByStatus(Status status, Pageable pageable);
}
```

### Caching Strategy
```java
public class CacheManager {
    private final Map<String, Cache<?>> caches = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> Cache<T> getCache(String name, Class<T> type) {
        return (Cache<T>) caches.computeIfAbsent(name, k -> new Cache<>());
    }
}
```

## Collection Wrapping for API Boundaries

```java
public class OrderService {
    public List<Order> getRecentOrders(int limit) {
        return findRecentOrders(limit).stream()
            .map(this::enrich)
            .toList();  // Immutable at API boundary
    }

    public Map<Long, OrderSummary> getSummary() {
        return Collections.unmodifiableMap(
            orders.stream()
                .collect(Collectors.toMap(
                    Order::getId, OrderSummary::fromOrder
                ))
        );
    }
}
```

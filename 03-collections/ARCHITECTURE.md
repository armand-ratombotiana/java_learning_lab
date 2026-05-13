# Collection Architecture Patterns

## Design Patterns Using Collections

### 1. Strategy Pattern
- Different collections for different access patterns
- Swap ArrayList ↔ LinkedList without changing client code

### 2. Decorator Pattern
- Collections.unmodifiableList() wraps a list
- Collections.synchronizedMap() adds thread safety
- Checked collections add type safety

### 3. Builder Pattern
- Stream.collect(Collectors.toList())
- Build complex collections fluently

### 4. Flyweight Pattern
- Empty collections shared (Collections.emptyList())
- Immutable collection instances reused

## Architecture Guidelines

### Layered Collections
```java
// Presentation layer - interfaces
public interface OrderService {
    List<Order> findOrders();
}

// Business layer - specific implementations  
public List<Order> findOrders() {
    return orderRepository.findAll(); // Returns List
}

// Data layer - actual implementation
public List<Order> findAll() {
    return new ArrayList<>(orders.values());
}
```

### Decouple from Implementation
- Declare return types as interfaces
- Accept interface parameters
- Test with different implementations

### Thread-Safe Collections
- ConcurrentHashMap, ConcurrentLinkedQueue
- CopyOnWriteArrayList for read-heavy scenarios
- Collections.synchronizedXxx() for simple needs
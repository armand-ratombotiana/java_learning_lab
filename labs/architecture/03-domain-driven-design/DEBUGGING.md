# Debugging DDD

## Common Issues

### 1. Aggregate Invariant Violations
```java
// Debug invariant failures
public void submit() {
    if (items.isEmpty()) {
        log.error("Order {} submit failed: no items", id);
        throw new EmptyOrderException(id);
    }
    if (customerId == null) {
        log.error("Order {} submit failed: no customer", id);
        throw new MissingCustomerException(id);
    }
}
```

### 2. Transaction Boundary Issues
```java
@Service
public class OrderService {
    @Transactional
    public void placeOrder(PlaceOrderCommand cmd) {
        Order order = orderRepository.findById(cmd.orderId()).get();
        order.addItem(cmd.item()); // Works within transaction
        // After @Transactional returns, changes are committed
    }
}
```

### 3. Event Publishing Debugging
```java
@Component
public class DomainEventDebugger {
    @EventListener
    public void onDomainEvent(DomainEvent event) {
        log.info("Domain event: {} at {}",
            event.getClass().getSimpleName(),
            event.occurredOn());
    }
}
```

### 4. Repository Query Debugging
```java
@Component
public class OrderSpecificationDebugger {
    public List<Order> debugSpecification(Specification<Order> spec) {
        List<Order> results = orderRepository.findAll(spec);
        log.debug("Specification: {}", spec);
        log.debug("Results found: {}", results.size());
        return results;
    }
}
```

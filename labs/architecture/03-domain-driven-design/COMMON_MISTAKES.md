# Common Mistakes in DDD

## 1. Anemic Domain Model
```java
// WRONG: Model with only getters/setters
@Entity
public class Order {
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    // No business behavior!
}

// CORRECT: Rich domain model
@Entity
public class Order {
    private OrderStatus status;

    public void submit() {
        if (!status.canTransitionTo(OrderStatus.SUBMITTED)) {
            throw new InvalidStateTransition();
        }
        this.status = OrderStatus.SUBMITTED;
        registerEvent(new OrderSubmitted(id));
    }
}
```

## 2. Infrastructure Coupling in Domain
```java
// WRONG: Domain depends on infrastructure
@Entity
public class Order {
    @Autowired
    private OrderRepository repository; // NO!

    public void save() {
        repository.save(this); // Domain shouldn't know about persistence
    }
}

// CORRECT: Domain is infrastructure-agnostic
@Entity
public class Order {
    public void submit() {
        // Pure domain logic only
    }
}
```

## 3. Exposing Internal State
```java
// WRONG: Exposing internal collections
public List<LineItem> getItems() {
    return items; // Caller can modify!
}

// CORRECT: Return immutable copy
public List<LineItem> getItems() {
    return Collections.unmodifiableList(items);
}
```

## 4. Ignoring Bounded Contexts
- Different contexts have different meanings for the same term
- "Order" in Sales context vs "Order" in Shipping context are different models
- Use anticorruption layer between contexts

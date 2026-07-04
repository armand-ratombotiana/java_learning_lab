# Why DDD Matters

## Business Impact
- **Common Language** - Ubiquitous language aligns business and technical teams
- **Model Richness** - Business rules are explicit and testable
- **Change Management** - Domain changes localized to specific bounded contexts
- **Knowledge Capture** - Domain expertise encoded in the model

## Technical Benefits
```java
// Without DDD: Anemic domain model
@Entity
public class Order {
    @Id private Long id;
    private String status;  // String, not typed
    // Getters and setters only - no domain logic

    public void setStatus(String status) {
        this.status = status;  // Anyone can set any value
    }
}

// With DDD: Rich domain model
@Entity
public class Order {
    @Id private OrderId id;
    private OrderStatus status;  // Enum with behavior

    public void submit() {
        if (!status.canTransitionTo(OrderStatus.SUBMITTED)) {
            throw new InvalidOrderStateException(id, status, OrderStatus.SUBMITTED);
        }
        this.status = OrderStatus.SUBMITTED;
        registerEvent(new OrderSubmitted(id));
    }
}
```

## Industry Adoption
- **Amazon** - DDD for order management
- **Uber** - Bounded contexts for ride-hailing
- **Airbnb** - DDD for booking system
- **ING Bank** - DDD for core banking systems
- **eBay** - DDD for marketplace domain

## Agile Alignment
DDD complements agile methodologies by providing a framework for continuous domain discovery and model refinement alongside iterative development.

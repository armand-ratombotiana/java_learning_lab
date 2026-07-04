# DDD Theory

## Strategic Patterns

### Bounded Context
A bounded context defines explicit boundaries where a particular domain model applies.
```java
// Bounded Context: Order Management
@BoundedContext(name = "order-management")
public class Order {
    // Order-specific definition
}

// Bounded Context: Shipping (different Order concept)
@BoundedContext(name = "shipping")
public class ShipmentOrder {
    // Different properties and behaviors
}
```

### Context Mapping
- **Partnership** - Two contexts cooperate
- **Shared Kernel** - Shared subset of model
- **Customer-Supplier** - Upstream/downstream relationship
- **Conformist** - Downstream conforms to upstream
- **Anticorruption Layer** - Translation between contexts
- **Open Host Service** - Protocol access
- **Published Language** - Well-documented interchange

## Tactical Patterns

### Entity
```java
@Entity
public class Order {
    @Id
    private OrderId id;  // Value object as ID
    private OrderStatus status;
    private Money totalAmount;  // Value object
    private CustomerId customerId;

    // Behaviors that express business rules
    public void cancel() {
        if (this.status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot cancel shipped order");
        }
        this.status = OrderStatus.CANCELLED;
        registerEvent(new OrderCancelled(this.id));
    }
}
```

### Value Object
```java
@Value
@Embeddable
public class Money {
    BigDecimal amount;
    Currency currency;

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException();
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
```

### Aggregate
```java
@Entity
@AggregateRoot
public class Customer extends AggregateRoot<CustomerId> {
    private CustomerId id;
    private Email email;
    private Address shippingAddress;
    @OneToMany(cascade = ALL)
    private List<Order> orders = new ArrayList<>();

    public Order placeOrder(Money amount, Address shipping) {
        Order order = new Order(this.id, amount, shipping);
        this.orders.add(order);
        registerEvent(new OrderPlaced(this.id, order.getId()));
        return order;
    }
}
```

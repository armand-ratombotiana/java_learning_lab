# Why Clean Architecture Matters

## Key Benefits

### Framework Independence
```java
// Business rules don't import Spring/JPA
public class Order {
    private OrderId id;
    private List<LineItem> items;

    public Money calculateTotal() {
        return items.stream()
            .map(LineItem::getSubtotal)
            .reduce(Money.ZERO, Money::add);
    }
}
// This class never changes when frameworks change
```

### Testable Business Rules
```java
// Test entities without Spring
@Test
void orderShouldCalculateTotal() {
    Order order = new Order();
    order.addItem(new LineItem(Money.of(10), 2));
    order.addItem(new LineItem(Money.of(5), 3));
    assertThat(order.calculateTotal()).isEqualTo(Money.of(35));
}
```

### Technology Evolution
```java
// Switch from JPA to JDBC without touching business rules
public class JpaOrderRepository implements OrderRepository { }
// becomes
public class JdbcOrderRepository implements OrderRepository { }
// No changes to Order or CreateOrderInteractor
```

## Business Value
- Reduced risk of technology change
- Faster testing through isolated business logic
- Clear project structure for new team members
- Architecture communicates intent (screaming architecture)
- Lower maintenance costs over system lifetime

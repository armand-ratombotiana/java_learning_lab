# Refactoring to DDD

## Anemic to Rich Model Migration

### Step 1: Identify Anemic Models
```java
// BEFORE: Anemic
@Entity
public class Order {
    private String status;
    public void setStatus(String s) { this.status = s; }
    public String getStatus() { return status; }
}
```

### Step 2: Add Domain Behaviors
```java
// PHASE 1: Add typed status enum and behaviors
@Entity
public class Order {
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public void submit() {
        if (this.status == OrderStatus.SUBMITTED) return;
        validateState(OrderStatus.SUBMITTED);
        this.status = OrderStatus.SUBMITTED;
    }
}
```

### Step 3: Extract Value Objects
```java
// BEFORE: Primitive obsession
private String currency;
private BigDecimal amount;

// AFTER: Value object
@Embedded
private Money totalAmount;
```

### Step 4: Encapsulate Collections
```java
// BEFORE: Exposed collection
public List<LineItem> getItems() { return items; }

// AFTER: Behavior-driven
public void addItem(LineItem item) {
    items.add(item);
    recalculateTotal();
}
```

## Bounded Context Extraction
```java
// 1. Identify context boundary
// 2. Create separate package
// 3. Extract anticorruption layer
// 4. Set up independent persistence
// 5. Configure separate transactions
```

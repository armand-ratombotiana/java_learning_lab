# Step-by-Step DDD Implementation

## Step 1: Event Storming
```bash
# Workshop with domain experts
# Identify domain events (past tense)
OrderPlaced, PaymentReceived, OrderShipped
# Identify commands (imperative)
PlaceOrder, ProcessPayment, ShipOrder
# Identify aggregates and bounded contexts
```

## Step 2: Define Ubiquitous Language
Create glossary:
```
Order - A customer request to purchase products
Line Item - A single product line within an order
Invoice - A bill for an order
Shipment - A physical delivery of order items
```

## Step 3: Create Value Objects
```java
@Value
public class ProductId { String value; }
@Value
public class Quantity { int amount; }
@Value
public class Money { BigDecimal amount; Currency currency; }
```

## Step 4: Create Aggregate Root
```java
@Entity
public class Order {
    @EmbeddedId
    private OrderId id;
    // Business behaviors
    public void submit() { }
    public void addItem(ProductId product, Quantity qty, Money price) { }
}
```

## Step 5: Define Repository Interface
```java
public interface OrderRepository {
    Optional<Order> findById(OrderId id);
    void save(Order order);
}
```

## Step 6: Implement Application Service
```java
@Service
public class OrderApplicationService {
    @Transactional
    public OrderResponse placeOrder(PlaceOrderCommand cmd) {
        Order order = orderRepository.findById(new OrderId(cmd.orderId()))
            .orElseThrow(() -> new OrderNotFoundException(cmd.orderId()));
        order.submit();
        return OrderResponse.from(order);
    }
}
```

## Step 7: Enforce Aggregate Boundaries
```java
// Only modify Order through aggregate root
// LineItems can only be added via Order.addItem()
// Direct LineItem access prohibited
```

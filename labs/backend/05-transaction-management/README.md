# Transaction Management

Declarative and programmatic transaction management in Spring.

## Topics
- @Transactional annotation
- Transaction propagation levels (REQUIRED, REQUIRES_NEW, NESTED, etc.)
- Isolation levels (READ_COMMITTED, SERIALIZABLE, etc.)
- Rollback rules
- Transaction boundaries
- Distributed transactions (XA/JTA)
- Testing transactional behavior

## Example
```java
@Service
@Transactional
public class OrderService {
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Order placeOrder(OrderRequest request) {
        // All database operations in one transaction
        Order order = orderRepository.save(request.toOrder());
        inventoryService.deductStock(request.getItems());
        paymentService.charge(request.getPayment());
        return order;
    }
}
```

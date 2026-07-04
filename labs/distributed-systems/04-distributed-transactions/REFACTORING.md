# Refactoring for Distributed Transactions

## From 2PC to SAGA

### Before (2PC):
```java
public class MonolithicOrderService {
    @Transactional
    public void placeOrder(Order order) {
        inventoryService.decrement(order.productId);
        paymentService.charge(order.amount);
        shippingService.schedule(order);
    }
}
```

### After (SAGA):
```java
public class OrderSagaService {
    public void placeOrder(Order order) {
        saga.execute(new OrderContext())
            .addStep(ctx -> inventoryService.reserve(ctx.productId),
                     ctx -> inventoryService.release(ctx.productId))
            .addStep(ctx -> paymentService.debit(ctx.amount),
                     ctx -> paymentService.credit(ctx.amount))
            .addStep(ctx -> shippingService.schedule(ctx.orderId),
                     ctx -> shippingService.cancel(ctx.orderId));
    }
}
```

## Idempotency Keys
Add idempotency keys to handle retries safely.

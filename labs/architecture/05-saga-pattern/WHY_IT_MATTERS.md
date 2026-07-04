# Why Saga Pattern Matters

## Business Impact
- **Data Integrity** - Ensures data consistency across services
- **Resilience** - Handles failures gracefully with rollback
- **Traceability** - Complete audit trail of all steps
- **Recovery** - Ability to resume failed sagas

## Example: E-commerce Order Flow
```java
// Without Saga: Inconsistent state on failure
public void placeOrder(Order order) {
    orderService.createOrder(order);        // Success
    inventoryService.reserve(order);        // FAILS!
    paymentService.charge(order);           // Never called
    // Order created but inventory not reserved - inconsistent!
}

// With Saga: Compensating transaction on failure
public void placeOrderSaga(CreateOrderCommand cmd) {
    orderService.createOrder(cmd);          // Step 1
    try {
        inventoryService.reserve(cmd);      // Step 2
        paymentService.charge(cmd);         // Step 3
        orderService.confirm(cmd);          // Step 4
    } catch (PaymentException e) {
        inventoryService.release(cmd);      // Compensate step 2
        orderService.reject(cmd);           // Compensate step 1
    }
}
```

## Critical Systems Using Saga
- **E-commerce** - Order placement with payment, inventory, shipping
- **Banking** - Fund transfers between accounts
- **Travel** - Booking flights, hotels, and car rentals
- **Insurance** - Claims processing with multiple checks

## Benefits Over 2PC
| Aspect | 2PC | Saga |
|--------|-----|------|
| Performance | Blocking | Non-blocking |
| Scalability | Poor | Excellent |
| Failure Model | Single point | Graceful degradation |
| Locking | Long-lived | Local only |
| Timeout | Global | Per-step |

# Common Mistakes in Saga Pattern

## 1. Non-Idempotent Steps
```java
// WRONG: Processing same command twice causes issues
public void processPayment(ProcessPaymentCommand cmd) {
    paymentGateway.charge(cmd.getAmount()); // Charged twice if retried!
}

// CORRECT: Idempotent processing
public void processPayment(ProcessPaymentCommand cmd) {
    if (paymentRepository.existsByIdempotencyKey(cmd.getIdempotencyKey())) {
        return; // Already processed
    }
    paymentGateway.charge(cmd.getAmount());
}
```

## 2. Missing Compensating Actions
```java
// WRONG: Only forward path
@SagaEventHandler
public void on(PaymentProcessedEvent event) {
    gateway.send(new ShipOrderCommand(event));
}
// No handler for payment failure!

// CORRECT: Handle failure
@SagaEventHandler
public void on(PaymentFailedEvent event) {
    gateway.send(new ReleaseInventoryCommand(event));
    gateway.send(new CancelOrderCommand(event));
}
```

## 3. Incorrect Compensation Order
```java
// WRONG: Compensating in wrong order
// Steps: CreateOrder -> ReserveInv -> ProcessPayment
compensate(CancelOrder);  // Should be last!
compensate(ReleaseInv);   // Should be middle!

// CORRECT: Reverse order (LIFO)
compensate(RefundPayment);  // Undo step 3 first
compensate(ReleaseInventory); // Then step 2
compensate(CancelOrder);   // Then step 1
```

## 4. Saga Timeout Not Set
```java
// WRONG: No timeout
@Saga
public class OrderSaga { } // Can run forever!

// CORRECT: Set timeout
@Saga
@SagaTimeout(duration = 30, unit = ChronoUnit.SECONDS)
public class OrderSaga { }
```

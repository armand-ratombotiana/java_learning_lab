# Distributed Transactions: Code Deep Dive

## Complete SAGA Implementation

```java
import java.util.*;
import java.util.function.Consumer;

public class Saga<T> {
    private final List<Step<T>> steps = new ArrayList<>();
    
    public Saga<T> addStep(Consumer<T> action, Consumer<T> compensate) {
        steps.add(new Step<>(action, compensate));
        return this;
    }
    
    public void execute(T context) {
        Deque<Integer> executed = new ArrayDeque<>();
        
        for (int i = 0; i < steps.size(); i++) {
            Step<T> step = steps.get(i);
            try {
                step.action.accept(context);
                executed.push(i);
            } catch (Exception e) {
                // Compensate in reverse order
                while (!executed.isEmpty()) {
                    int idx = executed.pop();
                    try {
                        steps.get(idx).compensate.accept(context);
                    } catch (Exception ce) {
                        // Log compensation failure, continue
                        System.err.println("Compensation failed for step " + idx);
                    }
                }
                throw new SagaException("Saga failed at step " + i, e);
            }
        }
    }
    
    private record Step<T>(Consumer<T> action, Consumer<T> compensate) {}
    
    static class SagaException extends RuntimeException {
        SagaException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

// Usage
public class OrderSaga {
    public static void main(String[] args) {
        Saga<OrderContext> orderSaga = new Saga<>();
        
        orderSaga
            .addStep(
                ctx -> reserveInventory(ctx.productId, ctx.quantity),
                ctx -> releaseInventory(ctx.productId, ctx.quantity)
            )
            .addStep(
                ctx -> chargePayment(ctx.orderId, ctx.amount),
                ctx -> refundPayment(ctx.orderId, ctx.amount)
            )
            .addStep(
                ctx -> confirmShipment(ctx.orderId, ctx.address),
                ctx -> cancelShipment(ctx.orderId)
            );
        
        try {
            orderSaga.execute(new OrderContext());
            System.out.println("Order completed successfully");
        } catch (Saga.SagaException e) {
            System.err.println("Order failed, compensation applied: " + e.getMessage());
        }
    }
}
```

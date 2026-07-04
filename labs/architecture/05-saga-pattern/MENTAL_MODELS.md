# Mental Models for Saga Pattern

## The Domino Effect
Each step in a saga is like a domino. If one domino falls (fails), you need to set the previous ones back upright (compensating transactions).

## The Multi-Legged Journey
A saga is like booking a trip with multiple legs (flight, hotel, car). If one leg cancels, you need to cancel the others too, ideally in reverse order.

## The River and Dam Analogy
Each step builds a dam across a river. If later steps fail, you must remove the earlier dams (compensate) to let the river flow correctly again.

## The Transaction Receipt
Each step produces a receipt (event). If something goes wrong, you can use the receipts to "return" each completed step.

## The Cooking Recipe
A recipe with multiple steps - if you burn the sauce at step 4, you don't serve the half-cooked meal. You throw it out and start over (or serve something else).

```java
// Saga as a state machine
@Component
@Saga
public class OrderProcessingSaga {

    private OrderSagaState state = OrderSagaState.INITIALIZED;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderPlacedEvent event) {
        state = OrderSagaState.INVENTORY_RESERVING;
        commandGateway.send(new ReserveInventoryCommand(event));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(InventoryReservedEvent event) {
        state = OrderSagaState.PAYMENT_PROCESSING;
        commandGateway.send(new ProcessPaymentCommand(event));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderConfirmedEvent event) {
        state = OrderSagaState.COMPLETED;
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderFailedEvent event) {
        state = OrderSagaState.FAILED;
        // Compensating actions already triggered
    }
}
```

# Code Deep Dive: Saga Pattern

## Full Orchestration Saga with Axon

### Saga Class
```java
@Saga
@Slf4j
public class OrderFulfillmentSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private String orderId;
    private String productId;
    private int quantity;
    private BigDecimal amount;
    private boolean inventoryReserved = false;
    private boolean paymentProcessed = false;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderPlacedEvent event) {
        log.info("Saga started for order: {}", event.getOrderId());
        this.orderId = event.getOrderId();
        this.productId = event.getProductId();
        this.quantity = event.getQuantity();

        // Step 1: Reserve inventory
        commandGateway.send(new ReserveInventoryCommand(
            orderId, productId, quantity
        ));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(InventoryReservedEvent event) {
        log.info("Inventory reserved for order: {}", orderId);
        this.inventoryReserved = true;
        this.amount = event.getTotalAmount();

        // Step 2: Process payment
        commandGateway.send(new ProcessPaymentCommand(
            orderId, amount
        ));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentProcessedEvent event) {
        log.info("Payment processed for order: {}", orderId);
        this.paymentProcessed = true;

        // Step 3: Confirm order
        commandGateway.send(new ConfirmOrderCommand(orderId));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderConfirmedEvent event) {
        log.info("Order confirmed: {}", orderId);
        SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(InventoryReservationFailedEvent event) {
        log.error("Inventory reservation failed for order: {}", orderId);
        // No compensation needed - nothing happened yet
        commandGateway.send(new RejectOrderCommand(orderId, "Inventory unavailable"));
        SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentFailedEvent event) {
        log.error("Payment failed for order: {}", orderId);
        // Compensate: Release inventory
        if (inventoryReserved) {
            commandGateway.send(new ReleaseInventoryCommand(
                orderId, productId, quantity
            ));
        }
        commandGateway.send(new RejectOrderCommand(orderId, "Payment failed"));
        SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(InventoryReleasedEvent event) {
        log.info("Inventory released for order: {}", orderId);
        // Compensation complete
    }
}
```

### Compensating Commands
```java
@Value
public class ReleaseInventoryCommand {
    @TargetAggregateIdentifier
    String orderId;
    String productId;
    int quantity;
}

// Aggregate handling compensation
@Aggregate
public class InventoryAggregate {

    @CommandHandler
    public void handle(ReleaseInventoryCommand cmd) {
        AggregateLifecycle.apply(new InventoryReleasedEvent(
            cmd.getOrderId(), cmd.getProductId(), cmd.getQuantity()
        ));
    }
}
```

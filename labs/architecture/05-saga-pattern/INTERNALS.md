# Saga Pattern Internals

## Axon Saga Internals
```java
@Component
public class AxonSagaManager {

    private final SagaRepository sagaRepository;
    private final EventBus eventBus;

    public void processEvent(DomainEvent event) {
        // 1. Load associated saga instances
        List<SagaInstance> sagas = sagaRepository.find(event.getAggregateId());

        // 2. Dispatch event to matching sagas
        for (SagaInstance saga : sagas) {
            if (saga.getState().canHandle(event.getType())) {
                saga.handle(event);
                sagaRepository.save(saga);
            }
        }
    }
}

@Saga
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private String orderId;
    private String productId;
    private int quantity;
    private boolean inventoryReserved;
    private boolean paymentProcessed;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderPlacedEvent event) {
        this.orderId = event.getOrderId();
        this.productId = event.getProductId();
        this.quantity = event.getQuantity();
        commandGateway.send(new ReserveInventoryCommand(orderId, productId, quantity));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(InventoryReservedEvent event) {
        this.inventoryReserved = true;
        commandGateway.send(new ProcessPaymentCommand(orderId, event.getTotalAmount()));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentProcessedEvent event) {
        this.paymentProcessed = true;
        commandGateway.send(new ConfirmOrderCommand(orderId));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentFailedEvent event) {
        // Compensate inventory
        if (inventoryReserved) {
            commandGateway.send(new ReleaseInventoryCommand(orderId, productId, quantity));
        }
        SagaLifecycle.end();
    }
}
```

## Compensation Tracking
```java
@Component
public class CompensationTracker {

    private final Map<String, Stack<CompensationAction>> compensations = new ConcurrentHashMap<>();

    public void registerCompensation(String sagaId, CompensationAction action) {
        compensations.computeIfAbsent(sagaId, k -> new Stack<>())
            .push(action); // LIFO order
    }

    public void compensate(String sagaId) {
        Stack<CompensationAction> actions = compensations.get(sagaId);
        while (!actions.isEmpty()) {
            CompensationAction action = actions.pop();
            action.compensate(); // Execute in reverse order
        }
    }
}
```

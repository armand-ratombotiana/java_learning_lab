# Debugging Sagas

## Common Issues

### 1. Saga Not Advancing
```bash
# Check saga store
SELECT * FROM saga_entries WHERE saga_id = 'order-saga-123';
SELECT * FROM association_values WHERE saga_id = 'order-saga-123';
```

### 2. Missing Event Handlers
```java
@Component
@Slf4j
public class SagaDebugger {
    @EventListener
    public void onAnyEvent(Object event) {
        if (event.getClass().getName().contains("Event")) {
            log.info("Event published: {} = {}", 
                event.getClass().getSimpleName(), event);
        }
    }
}
```

### 3. Compensation Not Triggered
```java
// Add logging to verify compensation execution
@SagaEventHandler(associationProperty = "orderId")
public void on(PaymentFailedEvent event) {
    log.warn("Payment failed, starting compensation for order: {}", orderId);
    // Verify compensation commands are sent
    boolean sent = commandGateway.send(new ReleaseInventoryCommand(orderId));
    log.info("Compensation command sent: {}", sent);
}
```

### 4. Saga Store Inspection
```sql
-- Axon saga store tables
SELECT * FROM SA_ENTRY WHERE id = 'id';
SELECT * FROM SA_ASSOC_VALUE_ENTRY WHERE entry_id = 'id';
```

### 5. Monitoring
```java
@Component
public class SagaMonitor {
    public void monitorSagaHealth() {
        // Track saga duration
        // Monitor compensation rate
        // Alert on stuck sagas
    }
}
```

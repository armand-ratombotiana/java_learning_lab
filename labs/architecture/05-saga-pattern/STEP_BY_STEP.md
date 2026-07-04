# Step-by-Step Saga Implementation

## Step 1: Define Saga Steps
```
Order Saga:
1. Create Order (Order Service)
2. Reserve Inventory (Inventory Service)
3. Process Payment (Payment Service)
4. Confirm Order (Order Service)

Compensation:
2a. Release Inventory
1a. Cancel Order
```

## Step 2: Create Events
```java
public record OrderPlacedEvent(String orderId, String productId, int quantity) {}
public record InventoryReservedEvent(String orderId, BigDecimal total) {}
public record PaymentProcessedEvent(String orderId) {}
public record PaymentFailedEvent(String orderId, String reason) {}
```

## Step 3: Implement Services
Each service handles commands and publishes events.

## Step 4: Implement Saga (Orchestration)
```java
@Saga
public class OrderSaga {
    @Autowired private transient CommandGateway gateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderPlacedEvent event) {
        gateway.send(new ReserveInventoryCommand(event));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(InventoryReservedEvent event) {
        gateway.send(new ProcessPaymentCommand(event));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentProcessedEvent event) {
        gateway.send(new ConfirmOrderCommand(event));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentFailedEvent event) {
        gateway.send(new ReleaseInventoryCommand(event));
    }
}
```

## Step 5: Configure Saga Store
```yaml
axon:
  saga:
    store-type: jpa  # or mongodb, jdbc
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sagas
```

## Step 6: Test Saga Success Flow
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"productId": "P1", "quantity": 2, "customerId": "C1"}'
```

## Step 7: Test Saga Failure Flow
```bash
# Cause payment to fail
# Verify inventory is released and order is cancelled
```

# Step-by-Step Six-Port Architecture

## Step 1: Identify Port Types
For each external interaction, identify which of the 6 port types it represents.

## Step 2: Define Port Interfaces
```java
public interface CreateOrderInputPort { OrderResponse create(OrderRequest req); }
public interface OrderRepositoryPort { Order save(Order order); }
public interface PaymentGatewayPort { PaymentResult charge(Payment payment); }
public interface OrderEventPort { void publish(OrderEvent event); }
public interface OrderNotificationPort { void send(Notification notification); }
```

## Step 3: Implement Domain Service
```java
@Service
public class OrderService {
    private final CreateOrderInputPort inputPort;
    private final OrderRepositoryPort repoPort;
    // Constructor injection for all ports
}
```

## Step 4: Create Adapters
```java
@Component
public class RestOrderAdapter implements CreateOrderInputPort { }
@Component
public class JpaOrderAdapter implements OrderRepositoryPort { }
@Component
public class StripePaymentAdapter implements PaymentGatewayPort { }
@Component
public class KafkaEventAdapter implements OrderEventPort { }
@Component
public class EmailNotificationAdapter implements OrderNotificationPort { }
```

## Step 5: Wire with Spring
```java
@Configuration
public class SixPortConfig {
    @Bean
    public OrderService orderService(/* ...ports... */) {
        return new OrderService(/* ... */);
    }
}
```

## Step 6: Test with In-Memory Adapters
```java
@Test
void testWithInMemoryAdapters() {
    InMemoryOrderRepoPort repo = new InMemoryOrderRepoPort();
    OrderService service = new OrderService(/* in-memory ports */);
    // Test business logic without infrastructure
}
```

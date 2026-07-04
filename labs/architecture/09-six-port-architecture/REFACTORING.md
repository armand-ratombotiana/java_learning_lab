# Refactoring to Six-Port Architecture

## Migration from Layered Architecture

### Step 1: Identify External Dependencies
```java
// Current service with direct dependencies
@Service
public class OrderService {
    @Autowired
    private OrderJpaRepository jpaRepository; // Direct JPA dependency
    @Autowired
    private RestTemplate restTemplate; // Direct HTTP dependency
}
```

### Step 2: Extract Port Interfaces
```java
public interface OrderRepositoryPort { }
public interface PaymentGatewayPort { }
public interface OrderEventPublisherPort { }
```

### Step 3: Implement Adapters
```java
@Component
public class JpaOrderRepositoryAdapter implements OrderRepositoryPort {
    private final OrderJpaRepository jpaRepository;
}

@Component
public class StripePaymentGatewayAdapter implements PaymentGatewayPort {
    private final RestTemplate restTemplate;
}
```

### Step 4: Inject Ports to Service
```java
@Service
public class OrderService {
    private final OrderRepositoryPort orderRepo;
    private final PaymentGatewayPort paymentGateway;
    private final OrderEventPublisherPort eventPublisher;

    public OrderService(OrderRepositoryPort orderRepo,
                        PaymentGatewayPort paymentGateway,
                        OrderEventPublisherPort eventPublisher) {
        this.orderRepo = orderRepo;
        this.paymentGateway = paymentGateway;
        this.eventPublisher = eventPublisher;
    }
}
```

### Step 5: Add Test Adapters
```java
@Component
@Profile("test")
public class InMemoryOrderRepositoryAdapter implements OrderRepositoryPort {
    private final Map<String, Order> orders = new HashMap<>();
}
```

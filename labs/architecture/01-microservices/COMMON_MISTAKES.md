# Common Mistakes in Microservices

## 1. Shared Database Between Services
```java
// WRONG: Sharing database across services
@Service
public class PaymentService {
    // BAD - directly accessing order database
    @Query(value = "SELECT * FROM orders.orders", nativeQuery = true)
    public List<Order> getOrdersFromOtherService() { ... }
}

// CORRECT: Each service owns its data
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    // Only access payment database
}
```

## 2. Chatty Communication
```java
// WRONG: Multiple sequential calls
public OrderDetails getOrderDetails(Long orderId) {
    Order order = orderClient.getOrder(orderId);
    Customer customer = customerClient.getCustomer(order.getCustomerId());
    Product product = productClient.getProduct(order.getProductId());
    Payment payment = paymentClient.getPayment(order.getPaymentId());
    // 4 network calls for 1 request!
}

// CORRECT: Aggregation in gateway or use GraphQL
```

## 3. Ignoring Idempotency
```java
// WRONG: Not handling duplicate requests
public void processPayment(PaymentRequest request) {
    // If called twice, customer gets charged twice!
    paymentGateway.charge(request.getAmount());
}

// CORRECT: Idempotency key
public void processPayment(PaymentRequest request, String idempotencyKey) {
    if (paymentRepository.existsByIdempotencyKey(idempotencyKey)) {
        return; // Already processed
    }
    paymentGateway.charge(request.getAmount());
}
```

## 4. Tight Coupling Through Shared Libraries
- Avoid shared domain objects across services
- Share only DTOs/contracts via separate API modules
- Use versioned contracts (e.g., OpenAPI specs)

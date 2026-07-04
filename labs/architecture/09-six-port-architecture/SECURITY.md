# Six-Port Architecture Security

## Security by Port Type

### Inbound Driving Port (REST)
```java
public interface SecureOrderInputPort extends OrderInputPort {
    // Security handled at adapter level
}

@RestController
public class SecureOrderRestAdapter implements OrderInputPort {
    @PreAuthorize("hasRole('USER')")
    public OrderResponse createOrder(OrderRequest request, Authentication auth) {
        return delegate.createOrder(request.withUserId(auth.getName()));
    }
}
```

### Outbound Port Security
```java
@Component
public class SecurePaymentGatewayAdapter implements PaymentGatewayPort {
    private final EncryptionService encryption;

    @Override
    public PaymentResult processPayment(Payment payment) {
        // Encrypt sensitive data before sending
        Payment encrypted = encryption.encrypt(payment, "creditCard");
        return delegate.processPayment(encrypted);
    }
}
```

### Event Port Security
```java
@Component
public class SecureEventPublisherAdapter implements OrderEventPublisherPort {
    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        // Remove sensitive data before publishing
        OrderCreatedEvent sanitized = event.sanitize();
        delegate.publishOrderCreated(sanitized);
    }
}
```

## Security Layer Placement
- Authentication: Inbound adapter (filter/pre-handler)
- Authorization: Inbound port decorator
- Encryption/Decryption: Outbound adapter
- Audit Logging: Port decorator (cross-cutting)
- Input Validation: Inbound adapter
- Output Sanitization: Outbound adapter

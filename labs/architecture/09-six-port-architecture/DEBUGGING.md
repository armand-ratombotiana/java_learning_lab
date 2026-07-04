# Debugging Six-Port Architecture

## Common Issues

### 1. Wrong Adapter Wired
```java
// Check Spring profile
@Profile("prod")
@Component
public class StripePaymentAdapter implements PaymentGatewayPort { }

@Profile("test")
@Component
public class MockPaymentAdapter implements PaymentGatewayPort { }
```

### 2. Port Not Implemented
```bash
# Spring boot error:
No qualifying bean of type 'com.company.port.PaymentGatewayPort' available
```

### 3. Adapter Mapping Errors
```java
@Component
@Slf4j
public class JpaOrderRepositoryAdapter implements OrderRepositoryPort {
    @Override
    public Order save(Order order) {
        log.debug("Saving order: {}", order.getId());
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity saved = jpaRepo.save(entity);
        return mapper.toDomain(saved);
    }
}
```

### 4. Multi-Adapter Tracing
```java
@Component
@Slf4j
public class TraceablePaymentAdapter implements PaymentGatewayPort {
    private final PaymentGatewayPort delegate;

    public PaymentResult processPayment(Payment payment) {
        log.info("Processing payment {} via {}", payment.getId(), delegate.getClass());
        Timer.Sample sample = Timer.start();
        try {
            PaymentResult result = delegate.processPayment(payment);
            sample.stop(Timer.builder("payment.gateway")
                .tag("adapter", delegate.getClass().getSimpleName())
                .register(meterRegistry));
            return result;
        } catch (Exception e) {
            sample.stop(Timer.builder("payment.gateway.error")
                .tag("adapter", delegate.getClass().getSimpleName())
                .register(meterRegistry));
            throw e;
        }
    }
}
```

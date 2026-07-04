# Code Deep Dive: Transaction Management

## Complex Transactional Service
```java
@Service
public class PaymentService {

    @Transactional(rollbackFor = InsufficientFundsException.class)
    public Payment processPayment(Long orderId, BigDecimal amount) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

        Payment payment = new Payment(order, amount);
        payment = paymentRepository.save(payment);

        // This runs in the same transaction
        accountService.deduct(order.getCustomerId(), amount);

        return payment;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditLog(Long orderId, String action) {
        // Always commits independently
        auditRepository.save(new AuditLog(orderId, action));
    }
}

// Testing transactions
@SpringBootTest
@Transactional
class PaymentServiceTest {
    @Autowired private PaymentService paymentService;

    @Test
    void shouldRollbackOnFailure() {
        assertThrows(InsufficientFundsException.class,
            () -> paymentService.processPayment(1L, new BigDecimal("999999")));
        // Database is clean due to @Transactional rollback
    }
}
```

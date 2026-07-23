# Mock Interview: Transaction Management (Lab 05)

**Role:** Backend Engineer (Senior)  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is `@Transactional` and how does it work in Spring?

**Candidate:** `@Transactional` is a declarative transaction management annotation that handles transaction boundaries. When Spring encounters it on a method or class, it creates a proxy that:
1. Before method execution: Begin a transaction (or join an existing one based on propagation)
2. During method execution: All database operations use the same transaction
3. After successful execution: Commit the transaction
4. If an exception occurs: Roll back the transaction (by default on `RuntimeException` and `Error`, not on checked exceptions)

The underlying mechanism uses AOP proxies. Spring wraps the bean in a JDK dynamic proxy (or CGLIB proxy if class-based). The proxy intercepts calls and manages the transaction lifecycle through the `PlatformTransactionManager`.

**Interviewer:** Explain the different propagation levels.

**Candidate:** Propagation defines how transactional boundaries relate:
1. **`REQUIRED`** (default): Joins an existing transaction or creates a new one if none exists
2. **`REQUIRES_NEW`**: Always suspends the current transaction and creates a new one
3. **`SUPPORTS`**: Runs within a transaction if one exists, otherwise non-transactional
4. **`NOT_SUPPORTED`**: Suspends any existing transaction, runs non-transactionally
5. **`MANDATORY`**: Requires an existing transaction, throws exception if none exists
6. **`NEVER`**: Requires no existing transaction, throws exception if one exists
7. **`NESTED`**: Creates a savepoint within the current transaction; inner rollback rolls back to savepoint, outer can still commit

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** What are common pitfalls with `@Transactional` in Spring?

**Candidate:** The most common issues:

1. **Self-invocation problem:** When a method within the same class calls another `@Transactional` method, the proxy doesn't intercept it because it's an internal method call, not through the proxy. Solution: Self-inject the bean or use `AopContext.currentProxy()`.

2. **Wrong exception types:** By default, `@Transactional` only rolls back on `RuntimeException` and `Error`. Checked exceptions (like `SQLException`) do NOT trigger rollback. Solution: `@Transactional(rollbackFor = Exception.class)`.

3. **Large transactions:** Holding a transaction open for too long increases lock contention. Solution: Keep transactions minimal, separate read-only operations with `@Transactional(readOnly = true)`.

4. **Transaction + aspect order:** If multiple aspects interact (transaction, caching, security), the order matters. The transaction aspect should usually be outermost.

5. **`readOnly = true` misconception:** It doesn't prevent writes entirely — it sets `FlushMode.MANUAL` on Hibernate, which suppresses dirty checking. It also routes to read replicas if configured.

**Interviewer:** How do you handle transactions across multiple databases (distributed transactions)?

**Candidate:** For transactions spanning multiple databases:

1. **XA/JTA transactions:** Uses a transaction manager (Atomikos, Bitronix, Narayana) that coordinates across resources using two-phase commit (2PC). Configure with `@Transactional` as usual. Trade-off: performance overhead and blocking during commit phase.

2. **ChainedTransactionManager:** Spring's `ChainedTransactionManager` coordinates multiple `PlatformTransactionManager` instances sequentially. Each commits in order; if one fails, previous ones are rolled back. Not true XA but simpler.

3. **Best practice: Avoid distributed transactions.** Use patterns like:
   - **Saga pattern** (Lab 23 Axon): Event-driven compensation-based transactions
   - **Outbox pattern:** Write to a local `outbox` table in the same transaction, a separate process publishes events
   - **Transactional outbox + CDC:** Debezium captures changes from the database transaction log

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a payment processing system that must handle 10,000 concurrent transactions per second. It involves debiting one account and crediting another. How do you ensure consistency without distributed transactions?

**Candidate:** For high-throughput payments without distributed transactions:

**Architecture:**
```
Payment Request → API Gateway → Payment Service
                                    ├── Account Service (debit)
                                    └── Ledger Service (credit)
```

**Pattern: Orchestrated Saga (Lab 23)**

```java
// Saga orchestrator
@Component
public class PaymentSaga {
    
    @Saga
    @SagaEventHandler(associationProperty = "paymentId")
    public void handle(ProcessPaymentCommand cmd) {
        // Step 1: Debit sender
        SagaLifecycle.associateWith("paymentId", cmd.getPaymentId());
        send(new DebitAccountCommand(cmd.getSenderId(), cmd.getAmount()));
    }
    
    @SagaEventHandler(associationProperty = "paymentId")
    public void on(DebitedEvent event) {
        // Step 2: Credit receiver
        send(new CreditAccountCommand(event.getReceiverId(), event.getAmount()));
    }
    
    @SagaEventHandler(associationProperty = "paymentId")
    public void on(CreditedEvent event) {
        // Step 3: Complete
        send(new CompletePaymentCommand(event.getPaymentId()));
    }
    
    @SagaEventHandler(associationProperty = "paymentId")
    public void on(PaymentFailedEvent event) {
        // Compensation: reverse the debit
        send(new ReverseDebitCommand(event.getPaymentId()));
    }
}
```

For reconciliation, each account has a running balance and a version field for optimistic locking. If optimistic locking fails due to concurrent access, retry with exponential backoff.

**Interviewer:** But what about the scenario where the debit succeeds but the orchestrator crashes before sending the credit command?

**Candidate:** That's handled by the event store. Axon Framework persists all events durably. If the orchestrator crashes:
1. When it restarts, it replays incomplete sagas from the event store
2. It picks up from the last emitted event (DebitedEvent was stored)
3. It continues with the credit step
4. Sagas are stateful and checkpointed after each event handler

Additionally, we use a **transactional outbox** pattern for the initial payment request:
```java
@Transactional
public void processPayment(PaymentRequest request) {
    // Save to outbox table in the same transaction as our local state
    paymentRepository.save(request.toPayment());
    outboxRepository.save(new OutboxMessage("payment.requested", request));
}
```
A separate `OutboxPublisher` reads from the outbox table and publishes to the event bus, ensuring at-least-once delivery.

**Interviewer:** How do you handle the case where an optimistic locking failure causes an infinite retry loop?

**Candidate:** Implement retry with circuit breaker:

```java
@Retryable(
    value = OptimisticLockException.class,
    maxAttempts = 3,
    backoff = @Backoff(delay = 50, multiplier = 2)
)
public void debitAccount(Long accountId, BigDecimal amount) {
    Account account = accountRepository.findById(accountId);
    account.debit(amount);
    accountRepository.save(account);
}

@Recover
public void recover(OptimisticLockException e, Long accountId, BigDecimal amount) {
    // After 3 retries, send to dead letter queue
    deadLetterQueue.publish("payment.debit.failed", accountId, amount, e);
    // Manual reconciliation process will handle this
}
```

The dead letter queue feeds into a monitoring dashboard. Operations team reviews and manually reconciles. For high-value payments, we add a `payment_audit` table with idempotency key to detect duplicates.

**Interviewer:** How does Spring's `@Transactional` interact with `@EventListener`?

**Candidate:** The combination enables transactional event handling:

```java
@Transactional
public void createOrder(Order order) {
    orderRepository.save(order);
    eventPublisher.publishEvent(new OrderCreatedEvent(order.getId()));
}

@Component
public class OrderEventListener {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreated(OrderCreatedEvent event) {
        // This fires only AFTER the transaction commits
        // If the transaction rolls back, this never fires
        notificationService.sendConfirmation(event.getOrderId());
    }
}
```

The phases are:
- `AFTER_COMMIT` — fires after successful commit
- `AFTER_ROLLBACK` — fires after rollback
- `AFTER_COMPLETION` — fires after commit or rollback
- `BEFORE_COMMIT` — fires before commit (within transaction)

The `@TransactionalEventListener` also has a `fallbackExecution` attribute that, if true, executes immediately if no transaction is active.

---

## Interviewer Feedback

**Strengths:**
- Strong understanding of transaction management internals
- Practical saga pattern implementation
- Good error handling and retry strategies

**Areas to Improve:**
- Could mention `@Transactional(transactionManager = "...")` for multiple transaction managers
- Should discuss `TransactionSynchronizationManager` for manual synchronization

**Verdict:** Strong Hire

---

## Follow-Up Questions

1. How does `REQUIRES_NEW` work with the same DataSource? Does it use a new connection?
2. What is the difference between `PlatformTransactionManager` and `ReactiveTransactionManager`?
3. How would you test transactions without a real database?
4. Explain how savepoints work in `NESTED` propagation at the database level.
5. How does Spring integrate with JTA? What is `JtaTransactionManager`?

---

*Lab 05 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*

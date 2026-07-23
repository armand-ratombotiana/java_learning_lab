# Mock Interview: CQRS with Axon Framework (Lab 23)

**Role:** Backend Engineer (Staff/Architect)  
**Duration:** 50 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is CQRS and what problem does it solve?

**Candidate:** CQRS (Command Query Responsibility Segregation) separates read and write operations into different models:
- **Commands** — modify state (write model)
- **Queries** — return data (read model)

This solves the problem of having a single model that tries to be good at both reading and writing but ends up being mediocre at both. In traditional CRUD, the same entity handles validation, business logic, and data retrieval, leading to:
- Complex queries with multiple joins for write-optimized schemas
- Performance issues when read requirements differ from write requirements
- Security concerns (reading data you shouldn't)

CQRS allows independent optimization: write-optimized for commands (normalized, transactional), read-optimized for queries (denormalized, indexed for specific use cases).

**Interviewer:** How does Axon Framework implement CQRS and event sourcing?

**Candidate:** Axon provides:
- **Command Bus** — dispatches commands to command handlers
- **Event Bus** — publishes events from aggregates
- **Query Bus** — dispatches queries to query handlers
- **Event Store** — persists events (event sourcing)
- **Saga** — manages distributed transactions across aggregates

An Axon aggregate:
```java
@Aggregate
public class OrderAggregate {
    @AggregateIdentifier private String orderId;
    
    @CommandHandler
    public OrderAggregate(CreateOrderCommand cmd) {
        apply(new OrderCreatedEvent(cmd.getOrderId(), cmd.getProductId(), cmd.getQuantity()));
    }
    
    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
    }
}
```

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Explain the saga pattern. How does Axon implement orchestration-based sagas?

**Candidate:** A saga is a sequence of local transactions where each step publishes an event that triggers the next step. If any step fails, compensating transactions undo previous steps.

**Axon orchestration saga:**
```java
@Saga
public class OrderSaga {
    @Autowired private transient CommandGateway commandGateway;
    
    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderPlacedEvent event) {
        // Step 1: Reserve inventory
        commandGateway.send(new ReserveInventoryCommand(event.getOrderId(), event.getProductId()));
    }
    
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(InventoryReservedEvent event) {
        // Step 2: Process payment
        commandGateway.send(new ProcessPaymentCommand(event.getOrderId(), event.getAmount()));
    }
    
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent event) {
        // Step 3: Confirm order
        commandGateway.send(new ConfirmOrderCommand(event.getOrderId()));
        SagaLifecycle.end();
    }
    
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentFailedEvent event) {
        // Compensation: Release inventory
        commandGateway.send(new ReleaseInventoryCommand(event.getOrderId()));
        SagaLifecycle.end();
    }
}
```

---

## Round 3: Hard (15-25 minutes)

**Interviewer:** Design an event-sourced banking system using Axon that handles 10K transactions/second with strong consistency for account balances.

**Candidate:** 

**Domain design:**
```java
@Aggregate
public class AccountAggregate {
    @AggregateIdentifier private String accountId;
    private Money balance;
    private long version;
    
    @CommandHandler
    public void handle(DepositMoneyCommand cmd) {
        apply(new MoneyDepositedEvent(accountId, cmd.getAmount(), Instant.now()));
    }
    
    @CommandHandler
    public void handle(WithdrawMoneyCommand cmd) {
        // Optimistic concurrency: version check at event store level
        if (balance.compareTo(cmd.getAmount()) < 0) {
            throw new InsufficientFundsException(accountId, balance, cmd.getAmount());
        }
        apply(new MoneyWithdrawnEvent(accountId, cmd.getAmount(), Instant.now()));
    }
    
    @EventSourcingHandler
    public void on(MoneyDepositedEvent event) {
        balance = balance.add(event.getAmount());
        version++;
    }
    
    @EventSourcingHandler
    public void on(MoneyWithdrawnEvent event) {
        balance = balance.subtract(event.getAmount());
        version++;
    }
}
```

**Scaling strategy:**
1. **Command model optimized for writes** — event store is append-only, extremely fast
2. **Separate read model** — projection updates balance in PostgreSQL for fast queries
```java
@EventHandler
public void on(MoneyDepositedEvent event) {
    accountBalanceRepository.updateBalance(event.getAccountId(), 
        balanceRepository.findById(event.getAccountId()).getBalance().add(event.getAmount()));
}

@EventHandler
public void on(MoneyWithdrawnEvent event) {
    accountBalanceRepository.updateBalance(event.getAccountId(), 
        balanceRepository.findById(event.getAccountId()).getBalance().subtract(event.getAmount()));
}
```
3. **Read model eventual consistency** — accept milliseconds of delay for balance queries
4. **Snapshotting** — periodic snapshots of aggregate state to avoid replaying entire event stream on restart

**Handling concurrent withdrawals:**
- Axon's optimistic locking: concurrent commands to the same aggregate are serialized
- Event store uses aggregate version for concurrency control
- If two `WithdrawMoneyCommand` arrive simultaneously, one succeeds, one gets `ConcurrencyException`
- Retry the failed command (business logic decides if retry is valid)

**Performance at 10K TPS:**
- Event store partitioned by aggregate type
- Projections use batch processing (micro-batches of 100 events)
- CQRS allows separate scaling: 3 write nodes, 10 read nodes

**Interviewer:** How do you ensure consistency between the event store and the read model? What happens if a projection fails?

**Candidate:** The key pattern is **eventual consistency with tracking**:
1. Axon's `TrackingEventProcessor` reads events from the event store in order
2. Each event has a global sequence number (token)
3. The projection handler processes events and updates the read model
4. On failure, the token is NOT advanced — events will be replayed
5. Dead letter queue for poison events (events that always fail)
6. Monitoring: track lag between event store and read model (token position difference)

If a projection fails:
```java
@EventHandler
public void on(MoneyDepositedEvent event) throws Exception {
    // Use retry for transient failures
    retryTemplate.execute(ctx -> {
        accountBalanceRepository.updateBalance(event.getAccountId(), 
            balanceRepository.findById(event.getAccountId()).getBalance().add(event.getAmount()));
    });
}
```

For events that can never be processed (schema changes, corrupted data), configure a dead letter queue handler:
```yaml
axon:
  eventhandling:
    processors:
      balance-projection:
        mode: TRACKING
        dead-letter-queue:
          max-retries: 3
          enabled: true
```

---

## Interviewer Feedback

**Strengths:** Deep CQRS/event sourcing understanding, practical banking domain design, robust failure handling  
**Areas to Improve:** Could discuss Axon Server vs embedded event store trade-offs  
**Verdict:** Strong Hire (Architect level)

---

*Lab 23 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*

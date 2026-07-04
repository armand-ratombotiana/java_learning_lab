# Step-by-Step Event Sourcing

## Step 1: Define Events
```java
public class AccountCreatedEvent extends DomainEvent {
    String accountId, owner; BigDecimal initialBalance;
}
public class MoneyDepositedEvent extends DomainEvent {
    String accountId; BigDecimal amount;
}
public class MoneyWithdrawnEvent extends DomainEvent {
    String accountId; BigDecimal amount;
}
```

## Step 2: Create Aggregate
```java
@Aggregate
public class AccountAggregate {
    @AggregateIdentifier
    private String accountId;
    private BigDecimal balance;

    @CommandHandler
    public AccountAggregate(CreateAccountCommand cmd) {
        AggregateLifecycle.apply(new AccountCreatedEvent(cmd.getAccountId(), cmd.getOwner(), cmd.getInitialBalance()));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.getAccountId();
        this.balance = event.getInitialBalance();
    }
}
```

## Step 3: Set Up Event Store
```yaml
axon:
  axonserver:
    enabled: false
  eventhandling:
    processordirectory: /tmp/event-store
```

## Step 4: Create Projections
```java
@Component
public class AccountProjection {
    @EventHandler
    public void on(AccountCreatedEvent event) {
        viewRepository.save(new AccountView(event.getAccountId(), event.getOwner(), event.getInitialBalance()));
    }
}
```

## Step 5: Test Event Replay
```java
@Test
void shouldRebuildAccountFromEvents() {
    List<DomainEvent> events = List.of(
        new AccountCreatedEvent("A1", "Alice", BigDecimal.valueOf(1000)),
        new MoneyDepositedEvent("A1", BigDecimal.valueOf(500))
    );
    AccountAggregate account = new AccountAggregate();
    events.forEach(account::on);
    assertEquals(BigDecimal.valueOf(1500), account.getBalance());
}
```

## Step 6: Add Snapshots
```java
// Configure snapshot threshold
axon:
  snapshot:
    trigger:
      threshold: 50
```

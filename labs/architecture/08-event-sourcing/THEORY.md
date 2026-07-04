# Event Sourcing Theory

## Core Concepts

### Event
An immutable record of something that happened.
```java
public abstract class DomainEvent {
    private final String eventId;
    private final Instant timestamp;
    private final int version;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.version = 1;
    }
}

public class AccountCreatedEvent extends DomainEvent {
    private final String accountId;
    private final String owner;
    private final BigDecimal initialBalance;
}
```

### Aggregate
```java
public class Account {
    private String id;
    private String owner;
    private Money balance;
    private int version;

    public static Account create(String owner, Money initialDeposit) {
        Account account = new Account();
        account.apply(new AccountCreatedEvent(
            UUID.randomUUID().toString(), owner, initialDeposit));
        return account;
    }

    public void deposit(Money amount) {
        apply(new MoneyDepositedEvent(id, amount));
    }

    public void withdraw(Money amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        apply(new MoneyWithdrawnEvent(id, amount));
    }

    protected void apply(DomainEvent event) {
        when(event);
        // Publish to event store
        EventPublisher.publish(event);
    }

    protected void when(AccountCreatedEvent event) {
        this.id = event.getAccountId();
        this.owner = event.getOwner();
        this.balance = event.getInitialBalance();
    }

    protected void when(MoneyDepositedEvent event) {
        this.balance = this.balance.add(event.getAmount());
    }

    protected void when(MoneyWithdrawnEvent event) {
        this.balance = this.balance.subtract(event.getAmount());
    }
}
```

## Event Store
```java
public interface EventStore {
    void append(String aggregateId, List<DomainEvent> events, int expectedVersion);
    List<DomainEvent> readEvents(String aggregateId);
    List<DomainEvent> readEventsSince(String aggregateId, Instant since);
}
```

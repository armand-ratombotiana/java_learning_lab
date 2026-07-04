# How Event Sourcing Works

## Architecture Flow
```
Command -> Aggregate -> DomainEvent -> EventStore -> Projection -> ReadModel
                                              |
                                        [Event Replay for state rebuild]
```

## Basic Flow

### 1. Event Creation
```java
@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private OrderStatus status;

    @CommandHandler
    public OrderAggregate(CreateOrderCommand cmd) {
        // Apply event - this changes state AND stores the event
        AggregateLifecycle.apply(new OrderCreatedEvent(
            cmd.getOrderId(), cmd.getCustomerId(), cmd.getItems()));
    }

    @CommandHandler
    public void handle(SubmitOrderCommand cmd) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Can only submit draft orders");
        }
        AggregateLifecycle.apply(new OrderSubmittedEvent(orderId));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.status = OrderStatus.DRAFT;
    }

    @EventSourcingHandler
    public void on(OrderSubmittedEvent event) {
        this.status = OrderStatus.SUBMITTED;
    }
}
```

### 2. Event Storage
```java
@Component
public class JdbcEventStore implements EventStore {

    @Override
    public void append(String aggregateId, List<DomainEvent> events, int expectedVersion) {
        for (DomainEvent event : events) {
            jdbcTemplate.update(
                "INSERT INTO events (aggregate_id, event_type, event_data, version, timestamp) " +
                "VALUES (?, ?, ?, ?, ?)",
                aggregateId, event.getClass().getName(),
                serialize(event), expectedVersion++, Instant.now());
        }
    }

    @Override
    public List<DomainEvent> readEvents(String aggregateId) {
        return jdbcTemplate.query(
            "SELECT * FROM events WHERE aggregate_id = ? ORDER BY version",
            new Object[]{aggregateId},
            (rs, rowNum) -> deserialize(rs.getString("event_type"), rs.getString("event_data")));
    }
}
```

### 3. State Rebuild
```java
// Rebuild aggregate state from events
public class Account {
    public static Account recreateFrom(String accountId, List<DomainEvent> events) {
        Account account = new Account();
        account.id = accountId;
        events.forEach(account::apply);
        return account;
    }

    protected void apply(DomainEvent event) {
        when((dynamic) event); // Calls the correct when() method
        version++;
    }
}
```

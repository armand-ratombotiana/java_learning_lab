# Code Deep Dive: Event Sourcing

## Axon Framework Event Sourcing

### Aggregate
```java
@Aggregate
@NoArgsConstructor
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private OrderStatus status;
    private List<OrderLine> lines;
    private Money total;

    @CommandHandler
    public OrderAggregate(CreateOrderCommand cmd) {
        AggregateLifecycle.apply(new OrderCreatedEvent(
            cmd.getOrderId(),
            cmd.getCustomerId(),
            cmd.getLines()
        ));
    }

    @CommandHandler
    public void handle(AddLineCommand cmd) {
        AggregateLifecycle.apply(new LineAddedEvent(
            cmd.getOrderId(), cmd.getProductId(), cmd.getQuantity(), cmd.getPrice()
        ));
    }

    @CommandHandler
    public void handle(SubmitOrderCommand cmd) {
        if (status != OrderStatus.DRAFT) {
            throw new InvalidOrderStateException(orderId, status, "submit");
        }
        AggregateLifecycle.apply(new OrderSubmittedEvent(orderId));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.status = OrderStatus.DRAFT;
        this.lines = new ArrayList<>();
        this.total = Money.ZERO;
    }

    @EventSourcingHandler
    public void on(LineAddedEvent event) {
        this.lines.add(new OrderLine(
            event.getProductId(), event.getQuantity(), event.getPrice()
        ));
        this.total = this.total.add(
            event.getPrice().multiply(event.getQuantity()));
    }

    @EventSourcingHandler
    public void on(OrderSubmittedEvent event) {
        this.status = OrderStatus.SUBMITTED;
    }
}
```

### Event Store
```java
@Component
public class PostgresEventStore implements EventStore {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void append(String aggregateId, List<DomainEvent> events, int expectedVersion) {
        for (int i = 0; i < events.size(); i++) {
            DomainEvent event = events.get(i);
            jdbcTemplate.update(
                "INSERT INTO events " +
                "(aggregate_id, aggregate_type, event_type, event_data, version, timestamp) " +
                "VALUES (?, ?, ?, ?::jsonb, ?, ?)",
                aggregateId,
                event.getAggregateType(),
                event.getClass().getName(),
                objectMapper.writeValueAsString(event),
                expectedVersion + i + 1,
                event.getTimestamp()
            );
        }
    }

    @Override
    public List<DomainEvent> readEvents(String aggregateId) {
        return jdbcTemplate.query(
            "SELECT * FROM events WHERE aggregate_id = ? ORDER BY version ASC",
            new Object[]{aggregateId},
            (rs, rowNum) -> deserialize(
                rs.getString("event_type"),
                rs.getString("event_data"))
        );
    }
}
```

### Snapshot Repository
```java
@Component
public class SnapshotRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public Optional<Snapshot> load(String aggregateId) {
        List<Snapshot> results = jdbcTemplate.query(
            "SELECT * FROM snapshots WHERE aggregate_id = ? ORDER BY version DESC LIMIT 1",
            new Object[]{aggregateId},
            (rs, rowNum) -> new Snapshot(
                rs.getString("aggregate_id"),
                rs.getInt("version"),
                rs.getString("snapshot_data"),
                rs.getTimestamp("timestamp").toInstant()
            )
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public void save(String aggregateId, int version, String snapshotData) {
        jdbcTemplate.update(
            "INSERT INTO snapshots (aggregate_id, version, snapshot_data, timestamp) " +
            "VALUES (?, ?, ?::jsonb, ?) " +
            "ON CONFLICT (aggregate_id, version) DO UPDATE SET snapshot_data = ?::jsonb",
            aggregateId, version, snapshotData, Instant.now(), snapshotData
        );
    }
}
```

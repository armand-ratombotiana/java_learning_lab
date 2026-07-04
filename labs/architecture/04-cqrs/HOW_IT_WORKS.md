# How CQRS Works

## Architecture Flow

### Command Flow
```
Client -> Command -> Command Bus -> Command Handler -> Write Model -> Event Store
```

### Query Flow
```
Client -> Query -> Query Bus -> Query Handler -> Read Model -> Response
```

## Axon Framework Example

### Command Side
```java
// Command
@Value
@Builder
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    String orderId;
    String customerId;
    List<OrderLineCommand> items;
}

// Aggregate
@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private OrderStatus status;

    @CommandHandler
    public OrderAggregate(CreateOrderCommand cmd) {
        AggregateLifecycle.apply(new OrderCreatedEvent(
            cmd.getOrderId(), cmd.getCustomerId(), cmd.getItems()
        ));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.status = OrderStatus.CREATED;
    }
}
```

### Query Side
```java
// Projection
@Component
@RequiredArgsConstructor
public class OrderProjection {

    private final OrderViewRepository repository;

    @EventHandler
    public void on(OrderCreatedEvent event) {
        repository.save(new OrderView(
            event.getOrderId(),
            event.getCustomerId(),
            event.getItems().size(),
            BigDecimal.ZERO,
            "CREATED"
        ));
    }
}

// Query Handler
@Component
public class OrderQueryHandler {

    private final OrderViewRepository repository;

    @QueryHandler
    public OrderView handle(FindOrderQuery query) {
        return repository.findById(query.orderId())
            .orElseThrow(() -> new OrderNotFoundException(query.orderId()));
    }
}
```

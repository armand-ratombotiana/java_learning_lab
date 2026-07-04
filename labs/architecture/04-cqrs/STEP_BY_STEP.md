# Step-by-Step CQRS Implementation

## Step 1: Set Up Axon Framework
```xml
<dependency>
    <groupId>org.axonframework</groupId>
    <artifactId>axon-spring-boot-starter</artifactId>
</dependency>
```

## Step 2: Define Commands and Events
```java
public record CreateOrderCommand(@TargetAggregateIdentifier String orderId, String customerId) implements Command {}
public record OrderCreatedEvent(String orderId, String customerId) implements Event {}
```

## Step 3: Create Aggregate
```java
@Aggregate
public class OrderAggregate {
    @AggregateIdentifier
    private String orderId;
    private OrderStatus status;

    @CommandHandler
    public OrderAggregate(CreateOrderCommand cmd) {
        AggregateLifecycle.apply(new OrderCreatedEvent(cmd.orderId(), cmd.customerId()));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.orderId();
        this.status = OrderStatus.CREATED;
    }
}
```

## Step 4: Define Query Model
```java
@Query
public record FindOrderQuery(String orderId) implements Query {}
```

## Step 5: Create Projection
```java
@Component
public class OrderProjection {
    @EventHandler
    public void on(OrderCreatedEvent event) {
        repository.save(new OrderView(event.orderId(), event.customerId(), "CREATED"));
    }
}
```

## Step 6: Create Query Handler
```java
@Component
public class OrderQueryHandler {
    @QueryHandler
    public OrderView handle(FindOrderQuery query) {
        return repository.findById(query.orderId()).orElseThrow();
    }
}
```

## Step 7: Expose APIs
```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping
    public CompletableFuture<String> createOrder(@RequestBody CreateOrderRequest req) {
        return commandGateway.send(new CreateOrderCommand(UUID.randomUUID().toString(), req.customerId()));
    }

    @GetMapping("/{id}")
    public CompletableFuture<OrderView> getOrder(@PathVariable String id) {
        return queryGateway.query(new FindOrderQuery(id), OrderView.class);
    }
}
```

# Code Deep Dive: Actor Model

## Complete Akka Typed Example

### Dependencies
```xml
<dependency>
    <groupId>com.typesafe.akka</groupId>
    <artifactId>akka-actor-typed_2.13</artifactId>
    <version>2.7.0</version>
</dependency>
```

### Messages
```java
public sealed interface OrderCommand {}

public record PlaceOrder(
    String orderId,
    String customerId,
    List<OrderItem> items,
    ActorRef<OrderEvent> replyTo
) implements OrderCommand {}

public record GetOrderStatus(
    String orderId,
    ActorRef<OrderStatusResponse> replyTo
) implements OrderCommand {}

public sealed interface OrderEvent {}
public record OrderPlaced(String orderId) implements OrderEvent {}
public record OrderFailed(String orderId, String reason) implements OrderEvent {}
public record OrderStatusResponse(String orderId, String status) {}
```

### Order Actor
```java
public class OrderActor {

    private static final Logger log = LoggerFactory.getLogger(OrderActor.class);

    public static Behavior<OrderCommand> create() {
        return Behaviors.setup(ctx -> idle());
    }

    private static Behavior<OrderCommand> idle() {
        return Behaviors.receive(OrderCommand.class)
            .onMessage(PlaceOrder.class, (msg, ctx) -> {
                log.info("Placing order: {}", msg.orderId);
                // Validate, process, etc.
                msg.replyTo().tell(new OrderPlaced(msg.orderId));
                return active(msg.orderId, "PLACED");
            })
            .build();
    }

    private static Behavior<OrderCommand> active(String orderId, String status) {
        return Behaviors.receive(OrderCommand.class)
            .onMessage(GetOrderStatus.class, (msg, ctx) -> {
                msg.replyTo().tell(new OrderStatusResponse(orderId, status));
                return Behaviors.same();
            })
            .build();
    }
}
```

### Order Supervisor
```java
public class OrderSupervisor {

    public static Behavior<OrderCommand> create() {
        return Behaviors.supervise(OrderActor.create())
            .onFailure(SupervisorStrategy.restart()
                .withStopChildren(false));
    }
}
```

### Order System
```java
public class OrderSystem {

    private final ActorSystem<OrderCommand> system;

    public OrderSystem() {
        this.system = ActorSystem.create(OrderSupervisor.create(), "order-system");
    }

    public CompletionStage<OrderEvent> placeOrder(PlaceOrderRequest request) {
        return AskPattern.ask(
            system,
            (ActorRef<OrderEvent> ref) -> new PlaceOrder(
                UUID.randomUUID().toString(),
                request.customerId(),
                request.items(),
                ref
            ),
            Duration.ofSeconds(5),
            system.scheduler()
        );
    }
}
```

### Testing Actors
```java
public class OrderActorTest {

    @Test
    void shouldPlaceOrder() {
        TestKit<OrderEvent> probe = TestKit.create(testSystem);

        ActorRef<OrderCommand> orderActor = testSystem.spawn(OrderActor.create());

        orderActor.tell(new PlaceOrder("ORD-1", "CUST-1", List.of(), probe.getRef()));

        OrderPlaced event = probe.expectMessageClass(OrderPlaced.class);
        assertEquals("ORD-1", event.orderId());
    }

    @Test
    void shouldHandleFailure() {
        TestKit<OrderEvent> probe = TestKit.create(testSystem);

        ActorRef<OrderCommand> orderActor = testSystem.spawn(
            Behaviors.supervise(OrderActor.create())
                .onFailure(SupervisorStrategy.restart()));

        // Actor automatically restarts on failure
    }
}
```

# Refactoring to Actor Model

## Thread-Based to Actor Migration

### Step 1: Identify Concurrent Components
```java
// BEFORE: Thread-based concurrent processing
public class OrderProcessor {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final Map<String, OrderState> orders = new ConcurrentHashMap<>();

    public CompletableFuture<OrderResult> process(Order order) {
        return CompletableFuture.supplyAsync(() -> {
            // Concurrent order processing with shared state
            OrderState state = orders.computeIfAbsent(order.id(), 
                k -> new OrderState());
            return processOrder(order, state);
        }, executor);
    }
}
```

### Step 2: Define Actor Protocol
```java
// AFTER: Actor-based processing
public sealed interface OrderProcessingCommand {}
public record ProcessOrder(Order order, ActorRef<OrderResult> replyTo)
    implements OrderProcessingCommand {}
public record GetOrderStatus(String orderId, ActorRef<OrderStatus> replyTo)
    implements OrderProcessingCommand {}
```

### Step 3: Create Actor
```java
public class OrderProcessingActor {
    public static Behavior<OrderProcessingCommand> create() {
        return Behaviors.setup(ctx -> active(new HashMap<>()));
    }

    private static Behavior<OrderProcessingCommand> active(
            Map<String, OrderState> orders) {
        return Behaviors.receive(OrderProcessingCommand.class)
            .onMessage(ProcessOrder.class, (msg, ctx) -> {
                OrderState state = orders.computeIfAbsent(
                    msg.order().id(), k -> new OrderState());
                OrderResult result = processOrder(msg.order(), state);
                orders.put(msg.order().id(), state);
                msg.replyTo().tell(result);
                return active(orders);
            })
            .build();
    }
}
```

### Step 4: Use Actor System
```java
ActorSystem<OrderProcessingCommand> system = ActorSystem.create(
    OrderProcessingActor.create(), "order-system");
// No shared state, no locks, no thread management
```

# CQRS Internals

## Command Bus Internals
```java
@Component
public class SimpleCommandBus implements CommandBus {

    private final Map<Class<?>, CommandHandler<?>> handlers = new HashMap<>();
    private final MeterRegistry meterRegistry;

    public <C extends Command> void subscribe(Class<C> type, CommandHandler<C> handler) {
        handlers.put(type, handler);
    }

    @Override
    public <R> CompletableFuture<R> dispatch(Command<R> command) {
        CommandHandler handler = handlers.get(command.getClass());
        if (handler == null) {
            return CompletableFuture.failedFuture(
                new NoHandlerException(command.getClass()));
        }
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            R result = (R) handler.handle(command);
            sample.stop(Timer.builder("cqrs.command.duration")
                .tag("command", command.getClass().getSimpleName())
                .register(meterRegistry));
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            sample.stop(Timer.builder("cqrs.command.error")
                .tag("command", command.getClass().getSimpleName())
                .register(meterRegistry));
            return CompletableFuture.failedFuture(e);
        }
    }
}
```

## Event Bus and Projections
```java
@Component
public class EventBus {
    private final List<EventProjection> projections = new ArrayList<>();

    public void subscribe(EventProjection projection) {
        projections.add(projection);
    }

    public void publish(DomainEvent event) {
        projections.stream()
            .filter(p -> p.supports(event))
            .forEach(p -> p.handle(event));
    }
}
```

## Read Model Update Strategy
```java
@Component
public class OrderProjection implements EventProjection {

    @EventHandler
    public void on(OrderCreatedEvent event) {
        // Create read model
        repository.save(new OrderView(
            event.getOrderId(), event.getCustomerId(), "CREATED"
        ));
    }

    @EventHandler
    public void on(OrderShippedEvent event) {
        // Update existing read model
        repository.findById(event.getOrderId())
            .ifPresent(view -> {
                view.setStatus("SHIPPED");
                view.setTrackingNumber(event.getTrackingNumber());
                repository.save(view);
            });
    }
}
```

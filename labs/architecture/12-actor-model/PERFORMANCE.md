# Actor Model Performance

## Performance Considerations

### Dispatcher Configuration
```yaml
akka:
  actor:
    default-dispatcher:
      type: Dispatcher
      executor: "fork-join-executor"
      fork-join-executor:
        parallelism-min: 2
        parallelism-factor: 2.0
        parallelism-max: 8
      throughput: 20  # Max messages per actor per run
```

### Dispatcher Types
- **Dispatcher** - Default, non-blocking
- **PinnedDispatcher** - Dedicated thread per actor
- **CallingThreadDispatcher** - For testing
- **BlockingDispatcher** - For blocking operations

### Actor Count Tuning
```java
// Route to multiple actor instances
public class WorkerPool {

    public static Behavior<Command> create(int poolSize) {
        return Routers.pool(poolSize,
            Behaviors.supervise(WorkerActor.create())
                .onFailure(SupervisorStrategy.restart()));
    }
}
```

### Performance Metrics
```java
@Component
public class ActorMetrics {
    private final MeterRegistry registry;

    public <T> Behavior<T> monitored(String name, Behavior<T> behavior) {
        return Behaviors.intercept(() -> new BehaviorInterceptor<T>() {
            @Override
            public T aroundReceive(ReceiveTarget<T> target, T message) {
                return registry.timer("actor.process", "name", name)
                    .record(() -> target.apply(message));
            }
        }).apply(behavior);
    }
}
```

## Performance Characteristics
| Metric | Traditional Threads | Actors |
|--------|-------------------|--------|
| Context switch | Kernel-level (expensive) | User-level (cheap) |
| Memory per unit | ~1MB (stack) | ~few KB |
| Max units | Thousands | Millions |
| Shared state | Locks needed | No locks needed |
| Distribution | Complex | Built-in |

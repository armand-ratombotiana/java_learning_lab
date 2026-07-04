# Actor Model Internals

## Akka Actor Internals

### Actor Cell (Runtime)
```java
// Simplified view of Akka's actor cell
public class ActorCell {
    private final Mailbox mailbox;      // Message queue
    private final ActorRef self;        // Own address
    private final ActorRef parent;      // Supervisor
    private final Props props;          // Actor configuration
    private Object currentMessage;      // Currently processing

    public void invoke(Object message) {
        currentMessage = message;
        try {
            actor.onReceive(message);   // Process message
        } catch (Exception e) {
            parent.tell(new Failed(self, e)); // Notify supervisor
        } finally {
            currentMessage = null;
            processNextMessage();       // Process next from mailbox
        }
    }
}
```

### Mailbox Implementation
```java
public class DefaultMailbox implements Mailbox {

    private final Queue<Envelope> queue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean scheduled = new AtomicBoolean(false);

    public void enqueue(ActorRef sender, Object message) {
        queue.offer(new Envelope(sender, message));
        if (scheduled.compareAndSet(false, true)) {
            // Schedule processing on dispatcher
            dispatcher.schedule(this::process);
        }
    }

    public void process() {
        scheduled.set(false);
        Envelope envelope = queue.poll();
        while (envelope != null) {
            actorCell.invoke(envelope.message());
            envelope = queue.poll();
        }
    }
}
```

### Dispatcher
```java
public class ForkJoinDispatcher implements Dispatcher {

    private final ForkJoinPool pool;
    private final int throughput; // Max messages per actor per run

    public void schedule(Runnable runnable) {
        pool.execute(runnable);
    }
}
```

## Supervision Strategy
```java
public class SupervisorStrategy {

    public Directive handle(Throwable cause) {
        return switch (cause) {
            case ArithmeticException e -> Directive.RESUME;  // Continue
            case NullPointerException e -> Directive.RESTART; // Restart actor
            case Exception e -> Directive.STOP;              // Stop actor
        };
    }
}
```

## Dead Letters
```java
// Messages sent to stopped/non-existent actors go to Dead Letters
system.eventStream().subscribe(deadLetterActorRef, DeadLetter.class);
```

# Actor Model Theory

## Core Concepts

### Actor
The fundamental unit of computation. An actor can:
- Process messages sequentially from its mailbox
- Create other actors (children)
- Send messages to other actors
- Change its behavior for next message

```java
// Akka Typed Actor
public class CounterActor {

    public static Behavior<CounterMessage> create(int initialCount) {
        return Behaviors.setup(context -> counterBehavior(initialCount));
    }

    private static Behavior<CounterMessage> counterBehavior(int count) {
        return Behaviors.receive(CounterMessage.class)
            .onMessage(Increment.class, msg -> {
                int newCount = count + msg.amount();
                System.out.println("Count: " + newCount);
                return counterBehavior(newCount);
            })
            .onMessage(GetCount.class, (msg, ctx) -> {
                msg.replyTo().tell(new CurrentCount(count));
                return Behaviors.same();
            })
            .build();
    }
}

// Messages
public sealed interface CounterMessage {}
public record Increment(int amount) implements CounterMessage {}
public record GetCount(ActorRef<CurrentCount> replyTo) implements CounterMessage {}
public record CurrentCount(int count) {}
```

### Actor System
```java
ActorSystem<CounterMessage> system = ActorSystem.create(
    CounterActor.create(0), "counter-system");

ActorRef<CounterMessage> counter = system;
counter.tell(new Increment(5));
counter.tell(new Increment(3));
```

### Key Properties
1. **Encapsulation** - Actors protect their state
2. **Location Transparency** - Actors can be local or remote
3. **Fault Tolerance** - Supervision hierarchies
4. **Asynchronous** - Non-blocking message passing
5. **Concurrency** - No shared state, no locks needed

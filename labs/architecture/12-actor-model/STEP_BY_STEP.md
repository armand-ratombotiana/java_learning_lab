# Step-by-Step Actor Model

## Step 1: Add Akka Dependency
```xml
<dependency>
    <groupId>com.typesafe.akka</groupId>
    <artifactId>akka-actor-typed_2.13</artifactId>
</dependency>
```

## Step 2: Define Messages
```java
public sealed interface CounterCommand {}
public record Increment(int amount) implements CounterCommand {}
public record GetCount(ActorRef<Integer> replyTo) implements CounterCommand {}
```

## Step 3: Create Actor
```java
public class CounterActor {
    public static Behavior<CounterCommand> create(int initial) {
        return Behaviors.receive(CounterCommand.class)
            .onMessage(Increment.class, msg -> {
                int newCount = initial + msg.amount();
                return create(newCount); // New behavior with updated state
            })
            .onMessage(GetCount.class, (msg, ctx) -> {
                msg.replyTo().tell(initial);
                return Behaviors.same();
            })
            .build();
    }
}
```

## Step 4: Create Actor System
```java
ActorSystem<CounterCommand> system = ActorSystem.create(
    CounterActor.create(0), "counter-system");
```

## Step 5: Send Messages
```java
system.tell(new Increment(5));
system.tell(new Increment(3));

// Ask for response
CompletionStage<Integer> count = AskPattern.ask(system,
    ref -> new GetCount(ref), Duration.ofSeconds(3), system.scheduler());
```

## Step 6: Add Supervision
```java
Behavior<CounterCommand> supervised = Behaviors.supervise(CounterActor.create(0))
    .onFailure(SupervisorStrategy.restart());
```

## Step 7: Test
```java
@Test
void testCounter() {
    TestKit<Integer> probe = TestKit.create(system);
    ActorRef<CounterCommand> counter = system.spawn(CounterActor.create(0));
    
    counter.tell(new Increment(5));
    counter.tell(new GetCount(probe.getRef()));
    
    assertEquals(5, probe.expectMessageClass(Integer.class).intValue());
}
```

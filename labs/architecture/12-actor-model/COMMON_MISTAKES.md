# Common Mistakes in Actor Model

## 1. Blocking Operations
```java
// WRONG: Blocking inside actor
public Behavior<Command> create() {
    return Behaviors.receive(Command.class)
        .onMessage(Process.class, msg -> {
            Thread.sleep(1000); // BLOCKING! Blocks entire actor
            return Behaviors.same();
        })
        .build();
}

// CORRECT: Use async, or dedicated dispatcher
public Behavior<Command> create() {
    return Behaviors.receive(Command.class)
        .onMessage(Process.class, msg -> {
            return Behaviors.withDispatchers("blocking-dispatcher")
                .execute(() -> {
                    Thread.sleep(1000); // On blocking dispatcher pool
                    return Behaviors.same();
                });
        })
        .build();
}
```

## 2. Mutable Messages
```java
// WRONG: Mutable message
public class AddItemCommand {
    private List<String> items = new ArrayList<>(); // Mutable!
    public void addItem(String item) { items.add(item); }
}

// CORRECT: Immutable message
public record AddItemCommand(List<String> items) {}
```

## 3. Bypassing Actor Encapsulation
```java
// WRONG: Accessing actor internals
public class MyActor {
    public int counter; // Public mutable state!
}

// CORRECT: State is private, only changed via messages
public class MyActor {
    private int counter;
    // Only message handlers modify state
}
```

## 4. Not Handling Actor Lifecycle
```java
// WRONG: No cleanup on stop
public class DatabaseActor {
    private Connection connection;
    
    // Should close connection on PostStop
    public Behavior<Command> create() {
        return Behaviors.receive(Command.class)
            .onSignal(PostStop.class, signal -> {
                connection.close(); // Cleanup!
                return Behaviors.same();
            })
            .build();
    }
}
```

## 5. Forgetting Actor Isolation
```java
// WRONG: Sharing mutable object between actors
MutableObject shared = new MutableObject();
actorA.tell(new Update(shared));
actorB.tell(new Update(shared)); // Both actors share state!
```

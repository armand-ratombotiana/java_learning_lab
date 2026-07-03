# Debugging Java 21 Features

## Debugging Virtual Threads

### Stack Traces
Virtual thread stack traces show the execution history, including previous mount points:

```java
Exception in thread "virtual"
java.lang.RuntimeException: Task failed
    at com.example.MyTask.run(MyTask.java:25)
    at java.base/java.lang.VirtualThread.run(VirtualThread.java:311)
    at java.base/java.lang.VirtualThread$$Lambda/0x0000000800c02438@4cdf35a9.run(Unknown Source)
```

Unlike platform threads, the stack trace does not include carrier thread frames, making it cleaner.

### Thread Dumps
Use `jcmd` to capture virtual thread dumps:

```bash
jcmd <pid> Thread.vthread_dump
```

This shows all virtual threads, their states, and stack traces. Compare with `Thread.print()` which only shows carrier threads.

### Enabling Virtual Thread Logging
```bash
java -Djdk.tracePinnedThreads=short MyApp
```

This logs when virtual threads get pinned to carrier threads, helping identify `synchronized` blocks that prevent unmounting.

```bash
java -Djdk.tracePinnedThreads=full MyApp
```

Full mode shows complete stack traces for pinning events.

### Debugging with IntelliJ/VS Code
Modern IDEs support virtual thread debugging:
- Set breakpoints inside virtual thread tasks
- The debugger shows each virtual thread in the threads panel
- Step-through works as expected

## Debugging Pattern Matching

### Switch Exhaustiveness Errors
If the compiler reports "the switch expression does not cover all possible values," check:

1. Are all permitted subtypes of a sealed class covered?
2. Is null handled explicitly or via a default case?
3. Are there patterns that are accidentally identical?

```java
// Example: Missing a subtype causes compile error
sealed interface Shape permits Circle, Rectangle {}
record Circle(double r) implements Shape {}
record Rectangle(double w, double h) implements Shape {
    // Missing triangle...
}

// Compiler error: switch expression does not cover all possible values
double area = switch (shape) {
    case Circle c -> Math.PI * c.r() * c.r();
    // Missing Rectangle!
};
```

### Pattern Dominance Warnings
The compiler warns about unreachable patterns. If you see "this pattern is dominated by a previous pattern," reorder your patterns with more specific (guarded) patterns first.

## Debugging Sequenced Collections

### UnsupportedOperationException
Some sequenced operations throw `UnsupportedOperationException` on unmodifiable collections:

```java
List<String> immutable = List.of("A", "B", "C");
immutable.addFirst("Z"); // UnsupportedOperationException!

// Fix: Create a modifiable copy
var mutable = new ArrayList<>(immutable);
mutable.addFirst("Z");
```

## Debugging Structured Concurrency

### Common Exceptions

1. **IllegalStateException**: "Shutdown scope not joined" — You forgot to call `scope.join()`.
2. **StructureViolationException**: Forking after the scope has been joined or closed.
3. **ExecutionException**: Wrap exceptions from subtasks when using `ShutdownOnFailure`.
4. **CancellationException**: Thrown when cancellation policy cancels a subtask.

### Monitoring Subtask State
```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<String> f1 = scope.fork(() -> task1());
    Future<String> f2 = scope.fork(() -> task2());
    
    scope.join();
    scope.throwIfFailed();
    
    // Check individual task results
    System.out.println("Task1 state: " + f1.state());
    System.out.println("Task2 state: " + f2.state());
    
    if (f1.state() == Future.State.SUCCESS) {
        System.out.println(f1.resultNow());
    }
}
```

## General Debugging Tips

### Enable All Preview Features
When debugging preview features, ensure all modules have preview features enabled:

```bash
java --enable-preview --add-modules jdk.incubator.concurrent MyApp
```

### JVM Flags for Virtual Threads
```bash
# Set virtual thread parallelism (default: available processors)
-Djdk.virtualThreadScheduler.parallelism=4

# Set maximum pool size for carrier threads
-Djdk.virtualThreadScheduler.maxPoolSize=256
```

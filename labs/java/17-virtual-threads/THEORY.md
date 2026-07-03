# Theory — Virtual Threads

## What Are Virtual Threads?
Lightweight threads managed by the JVM, not the OS. Millions can exist without exhausting OS resources.

## Creating Virtual Threads
```java
// Java 21+
Thread vThread = Thread.ofVirtual().start(() -> doWork());

// Or via executor
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
executor.submit(() -> doWork());
```

## Platform vs Virtual
| Aspect | Platform Thread | Virtual Thread |
|--------|----------------|----------------|
| Managed by | OS | JVM |
| Stack size | ~1 MB | ~few KB |
| Max count | ~thousands | ~millions |
| Context switch | OS syscall | Java-level yield |
| Pinning | N/A | Can block carrier |

## Structured Concurrency
```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<String> user = scope.fork(() -> fetchUser());
    Future<Integer> orders = scope.fork(() -> fetchOrders());
    scope.join();
    scope.throwIfFailed();
    return new Response(user.resultNow(), orders.resultNow());
}
```

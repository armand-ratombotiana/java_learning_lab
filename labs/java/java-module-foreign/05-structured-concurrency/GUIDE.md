# Deep Dive: Structured Concurrency

## 1. Core Concept

A structured task scope ensures that:
- Subtasks complete before the scope exits
- Failure in one subtask cancels others
- Errors propagate predictably

```java
// Without structured concurrency — easy to leak tasks
Future<String> f1 = executor.submit(task1);
Future<String> f2 = executor.submit(task2);
// If f1 throws, f2 keeps running — wasted work

// With structured concurrency
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<String> f1 = scope.fork(task1);
    Future<String> f2 = scope.fork(task2);
    scope.join();
    // If f1 fails, f2 is automatically cancelled
}
```

## 2. ShutdownOnFailure

Completes when all tasks succeed, or cancels all on first failure:

```java
Response handleRequest() throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Future<User> user    = scope.fork(() -> fetchUser(id));
        Future<Orders> orders = scope.fork(() -> fetchOrders(id));

        scope.join();            // wait for all
        scope.throwIfFailed();   // throw if any failed

        return new Response(user.resultNow(), orders.resultNow());
    }
}
```

## 3. ShutdownOnSuccess

Completes when any task succeeds, cancels the rest:

```java
String findFastest(List<Callable<String>> providers) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
        for (var p : providers) scope.fork(p);
        scope.join();
        return scope.result();  // throws ExecutionException if all failed
    }
}
```

## 4. Custom Shutdown Policy

Extend `StructuredTaskScope` for custom behavior:

```java
class DeadlineScope<T> extends StructuredTaskScope<T> {
    private final Instant deadline;
    private volatile T result;

    DeadlineScope(Instant deadline) { this.deadline = deadline; }

    @Override
    protected void handleComplete(Future<T> future) {
        if (deadline.isBefore(Instant.now())) {
            shutdown(); // cancel all remaining tasks
            return;
        }
        // process completed future
    }
}
```

## 5. Error Handling

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<String> f1 = scope.fork(() -> { throw new IOException("fail"); });
    Future<String> f2 = scope.fork(() -> "ok");

    scope.join();          // completes (both tasks done or cancelled)
    scope.throwIfFailed(); // throws IOException wrapping the failure
    // f2 is cancelled by the scope shutdown
}
```

## 6. Timeouts

Combine with `scope.joinUntil(Instant)`:

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<String> f1 = scope.fork(() -> fetchRemote());

    scope.joinUntil(Instant.now().plusSeconds(5));

    if (!f1.isDone()) {
        scope.shutdown(); // cancel
        throw new TimeoutException("Timed out");
    }
    return f1.resultNow();
}
```

## 7. Best Practices

- Always use `try-with-resources` for task scopes
- Call `scope.join()` (or `joinUntil`) before accessing results
- Prefer `ShutdownOnFailure` for fan-out parallelism
- Prefer `ShutdownOnSuccess` for race/fastest-response patterns
- Customize `StructuredTaskScope` for complex orchestration
- Combine with virtual threads for maximum throughput

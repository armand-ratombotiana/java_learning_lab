# Deep Dive: Advanced Virtual Threads

## 1. The Paradigm Shift (Project Loom)
Virtual Threads (introduced in Java 21) fundamentally change how Java handles concurrency. Before Virtual Threads, Java threads were 1:1 mapped to OS threads (Platform Threads). OS threads are heavy (~1MB stack size, 1ms context switch time). You could only have a few thousand before crashing the OS.
Virtual Threads are lightweight. They are managed by the JVM, not the OS. You can create millions of them. They multiplex over a small pool of carrier OS threads.

### How they work:
When a Virtual Thread hits a blocking operation (e.g., waiting for a database response or `Thread.sleep()`), the JVM "unmounts" the Virtual Thread from its carrier OS thread, saving its stack state to the heap. The carrier OS thread is now free to execute a different Virtual Thread. When the blocking operation finishes, the Virtual Thread is "mounted" back onto an available carrier thread to resume execution.

## 2. Structured Concurrency (Preview in Java 21)
Traditionally, spawning threads using `ExecutorService` leads to "unstructured" concurrency. If a parent thread spawns 3 child threads and the parent dies or is cancelled, the child threads keep running in the background (orphan threads).

Structured Concurrency treats multiple concurrent tasks running in different threads as a single unit of work. It enforces a strict hierarchy: a parent thread cannot finish until all its child threads have finished (or been cancelled).

### `StructuredTaskScope`
This class manages the lifecycle of concurrent tasks.
*   **`ShutdownOnFailure`**: Runs tasks concurrently. If *any* task fails, it immediately cancels all other running tasks and throws the exception. (Fail-fast).
*   **`ShutdownOnSuccess`**: Runs tasks concurrently. As soon as *one* task succeeds, it cancels all other running tasks and returns the successful result. (First-to-finish).

```java
// Example: Fail-Fast Structured Concurrency
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Subtask<String> userTask = scope.fork(() -> fetchUser(id));
    Subtask<String> orderTask = scope.fork(() -> fetchOrder(id));

    scope.join();           // Wait for all to finish OR one to fail
    scope.throwIfFailed();  // Propagate exception if any failed

    // Both succeeded! Safe to get results.
    return new Response(userTask.get(), orderTask.get());
}
```

## 3. Scoped Values (Preview in Java 21)
`ThreadLocal` variables have major flaws: they are mutable, they can cause memory leaks if not cleared, and they are inherited by child threads (which requires expensive copying).

`ScopedValue` is the modern alternative designed specifically for Virtual Threads.
*   **Immutable**: Once bound, the value cannot be changed within that scope.
*   **Strictly Scoped**: The value is only available within the `run()` block of the `ScopedValue.where(...)` call. It is automatically cleaned up when the block exits.
*   **Efficient Inheritance**: Child threads spawned via `StructuredTaskScope` automatically inherit Scoped Values with zero copying overhead.

```java
public final static ScopedValue<String> USER_ID = ScopedValue.newInstance();

// Bind the value and execute a runnable
ScopedValue.where(USER_ID, "admin-123").run(() -> {
    // Inside this block (and any child threads spawned here), USER_ID is "admin-123"
    processRequest(); 
});
```

## 4. Migration Strategies
Migrating from Platform Threads to Virtual Threads is usually as simple as changing `Executors.newFixedThreadPool(100)` to `Executors.newVirtualThreadPerTaskExecutor()`. However, the architecture changes:
*   **Stop Pooling**: Never pool Virtual Threads. They are cheap to create and destroy. Create a new one for every single task.
*   **Keep Blocking I/O**: With Virtual Threads, synchronous, blocking I/O is fast and efficient. You do not need to rewrite your application to use complex reactive frameworks (like WebFlux or RxJava) to achieve high throughput.

## 5. Tuning and Limitations
While Virtual Threads are managed by the JVM, the underlying carrier pool is a `ForkJoinPool`.
*   **Carrier Pool Size**: By default, the number of carrier threads equals the number of CPU cores. You can tune this using `-Djdk.virtualThreadScheduler.parallelism=N`.
*   **Pinning**: This is the Achilles' heel of Virtual Threads. See the Edge Cases document for details.
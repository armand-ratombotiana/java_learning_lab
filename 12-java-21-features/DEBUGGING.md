# Debugging Java 21 Features

## Common Failure Scenarios

### Virtual Thread Issues

Virtual threads require JDK 21+ and behave differently from platform threads in subtle ways. The most common mistake is using thread-local variables extensively—each virtual thread has its own thread-local storage, so the memory model differs from platform threads. When you spawn many virtual threads, excessive use of thread-locals can cause memory issues because each virtual thread gets its own copy.

Blocking operations inside virtual threads are fine in isolation, but you must avoid pinning the carrier thread. Synchronized blocks and native methods cause pinning, meaning the carrier thread cannot run other virtual threads while your code holds the monitor. This defeats the purpose of virtual threads and can cause throughput degradation. Replace synchronized blocks with ReentrantLock or use virtual thread-friendly libraries.

Creating too many virtual threads without throttling can exhaust memory. Each virtual thread has a small stack that grows on demand, but if you spawn millions without control, you'll hit OutOfMemoryError. Use an ExecutorService with a bounded queue or semaphores to limit concurrent virtual threads.

### Stack Trace Examples

**Virtual thread memory exhaustion:**
```
java.lang.OutOfMemoryError: unable to create native thread: out of memory
    at java.lang.Thread.start0(Native Method)
    at java.lang.Thread.start(Thread.java:1634)
    at com.example.TaskProducer.produce(TaskProducer.java:25)
```

**Pinning warning with synchronized:**
```
WARNING: A thread marker (PID: 12345) (TID: 678, State: TIMED_WAITING) is pin-detected. 
Virtual thread status changed to parked: reason="monitor enter"
    at com.example.Service.blockingMethod(Service.java:45)
```

**Structured concurrency timeout:**
```
java.util.concurrent.TimeoutException: Scope 'task-scope' timed out after 5 seconds
    at java.util.concurrent.StructuredTaskScope$Scope.throwIfFailed(StructuredTaskScope.java:447)
    at com.example.ParallelProcessor.process(ParallelProcessor.java:30)
```

## Debugging Techniques

### Diagnosing Virtual Thread Problems

Enable virtual thread diagnostics with `-Djdk.virtualThreadScheduler.parallelism=N` to control the number of carrier threads. The default is based on available processors, but you may need to tune this based on your workload characteristics.

Use `Thread.getAllStackTraces()` to inspect all virtual threads in your application. When debugging, examine the state of virtual threads—they should be in TERMINATED, RUNNABLE, or PARKING states. If many are in BLOCKED state on synchronized blocks, you're hitting the pinning issue.

Monitor carrier thread usage with JFR (Java Flight Recorder). Enable the virtual thread events (`jdk.VirtualThread*`) to see detailed information about virtual thread scheduling and pinning. Look for high pinning rates that indicate the need to refactor synchronized code.

### Structured Concurrency Debugging

When using StructuredTaskScope, remember that it propagates exceptions by default unless you handle them. If any subtask throws, the scope throws after all subtasks complete. Use `StructuredTaskScope.Subtask.isFailure()` to check individual results instead of relying on exception propagation.

Timeouts with `StructuredTaskScope.orElseTimeout()` can be tricky—the timeout applies to the entire scope, not individual tasks. If you need per-task timeouts, use separate scopes or handle timeouts within each task.

## Best Practices

Use virtual threads for I/O-bound workloads where you have many concurrent operations waiting on external resources. For CPU-bound work, platform threads may still be more appropriate because virtual threads don't provide parallelism benefits.

Migrate from ThreadLocal to context variables (ContextSnapshot, StructuredTaskScope.copy()) for transferring context across virtual threads. Context propagation libraries like Loom-specific implementations or Micrometer's context propagation work well with structured concurrency.

Prefer structured concurrency patterns (StructuredTaskScope) over manual thread management. This ensures proper cleanup and provides better error handling and cancellation semantics.

Avoid synchronized unless necessary. If you must use it, profile with `-Djdk.virtualThread.pinning=exercise` to see how often pinning occurs. Consider using java.util.concurrent locks instead, which don't cause pinning.
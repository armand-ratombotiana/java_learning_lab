# How It Works — Concurrency

## Thread Lifecycle
```
NEW → RUNNABLE → WAITING/TIMED_WAITING → TERMINATED
         ↓
      BLOCKED (on lock)
```

## Synchronized Internals
`synchronized` maps to monitorenter/monitorexit bytecodes. Each object has a monitor. Only one thread can own the monitor at a time.

## ReentrantLock
Uses `AbstractQueuedSynchronizer` (AQS) — a CLH lock queue where threads spin or park.

## ExecutorService
```java
newCachedThreadPool()    → creates threads as needed, reuses idle ones
newFixedThreadPool(n)    → limits thread count
newSingleThreadExecutor() → single worker thread
newScheduledThreadPool(n) → delayed/periodic tasks
```

## CompletableFuture Execution
1. `supplyAsync` → task queued to ForkJoinPool.commonPool()
2. `thenApply` → registered as dependent stage
3. When complete, dependent stages are triggered via `complete()` or `completeExceptionally()`
4. Uses `ForkJoinPool` or custom `Executor`

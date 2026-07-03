# Internals — Concurrency

## Monitor (ObjectHeader)
Every Java object has a monitor stored in its object header:
- **Thin lock (biased):** Single thread, no CAS overhead
- **Inflated lock:** Contended — uses OS mutex + wait set

## AbstractQueuedSynchronizer (AQS)
Used by `ReentrantLock`, `CountDownLatch`, `Semaphore`:
- CLH queue of waiting threads
- Each node spins, then parks if contention high
- `acquire()` / `release()` are template methods

## ForkJoinPool Work-Stealing
Each worker thread has a double-ended deque of tasks. Idle workers steal tasks from the bottom of other workers' deques.

## CompletableFuture Internal State
```java
volatile Object result; // null, AltResult (exception), or T value
volatile WaitNode head; // Treiber stack of waiting dependent stages
```
Dependent stages (`thenApply`) are stored as linked nodes. When `complete()` is called, the result is CAS-set and the stack is traversed to fire dependents.

## Memory Ordering
- `synchronized` provides happens-before guarantees
- `volatile` provides visibility (no reordering)
- `Atomic*` classes use CAS instructions (e.g., `Unsafe.compareAndSwapInt`)

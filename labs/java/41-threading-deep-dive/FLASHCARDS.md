# Flashcards: Threading Deep Dive

## Thread Lifecycle
**Q: What are the six thread states in Java?**
A: NEW (not started), RUNNABLE (executing/ready), BLOCKED (waiting for monitor), WAITING (indefinite wait), TIMED_WAITING (timed wait), TERMINATED (completed).

**Q: How does a thread transition from RUNNABLE to BLOCKED?**
A: By attempting to enter a synchronized block or method for which another thread holds the monitor.

## ThreadPoolExecutor
**Q: What happens when core threads are busy and the queue is not full?**
A: The task is queued (offered to the work queue). No new threads are created.

**Q: What are the four built-in rejection policies?**
A: AbortPolicy (throws), CallerRunsPolicy (caller executes), DiscardPolicy (silent drop), DiscardOldestPolicy (drops oldest queued task).

## ForkJoinPool
**Q: What is work-stealing?**
A: Idle workers steal tasks from busy workers' deques. Task owners pop from top (LIFO), thieves steal from bottom (FIFO).

**Q: What does RecursiveTask.compute() return?**
A: The computed result. RecursiveAction.compute() returns void.

## CompletableFuture
**Q: What is the difference between thenApply and thenCompose?**
A: thenApply transforms a value (returns T), thenCompose chains a future-returning function (returns CompletableFuture<T>).

**Q: When does an exceptionally handler execute?**
A: When the upstream CompletableFuture completes exceptionally (with an exception).

## StructuredTaskScope
**Q: What does ShutdownOnSuccess do?**
A: Shuts down the scope when any subtask completes successfully, cancelling remaining subtasks.

**Q: Why use structured concurrency?**
A: Ensures all subtasks complete (or are cancelled) before the scope exits, preventing orphaned threads and resource leaks.

# Threading Deep Dive — Theoretical Foundation

## Thread Lifecycle
Java threads exist in one of six states defined by `Thread.State`:
- **NEW**: Allocated but not started. The thread object exists but has no OS-level thread.
- **RUNNABLE**: Ready to run or executing. The thread scheduler determines when it actually runs.
- **BLOCKED**: Waiting for a monitor lock to enter a synchronized block/method.
- **WAITING**: Waiting indefinitely for another thread to perform a specific action (notify, join, park).
- **TIMED_WAITING**: Like WAITING but with a timeout (sleep, wait(timeout), join(timeout), parkNanos).
- **TERMINATED**: The run() method has completed or an uncaught exception ended it.

## ThreadPoolExecutor Design
ThreadPoolExecutor separates task submission from execution using a work queue. Core threads handle the base load. When the queue fills, additional threads up to maxPoolSize are created. Idle threads beyond core size are terminated after keepAliveTime. The four rejection policies (AbortPolicy, CallerRunsPolicy, DiscardPolicy, DiscardOldestPolicy) define behavior when the pool is saturated.

## ForkJoinPool Work-Stealing
ForkJoinPool uses a deque per worker thread. Workers push tasks to their own deque (LIFO) and pop from the top. When empty, they steal from the bottom (FIFO) of a randomly selected victim's deque. This minimizes contention because each worker only accesses its own deque's top and steals from another's bottom.

## CompletableFuture Composition
CompletableFuture extends Future with functional composition. Completion stages form a DAG where each stage depends on previous stages. thenApply transforms values synchronously; thenCompose flattens nested futures; allOf/anyOf compose multiple independent futures; exceptionally provides error recovery.

## StructuredTaskScope
StructuredTaskScope (preview in Java 21, finalized in later versions) treats concurrent subtasks as a single unit of work. Subtasks are children of the scope and must complete before the scope returns. ShutdownOnSuccess cancels remaining tasks when one succeeds; ShutdownOnFailure propagates the first failure to the caller.

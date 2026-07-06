# Threading Internals

## Thread Pool Executor Internals
The `Worker` inner class extends `AbstractQueuedSynchronizer` to implement a non-reentrant lock for task execution. The `workers` set is a `HashSet<Worker>` guarded by the `mainLock` (ReentrantLock). The `ctl` AtomicInteger packs:
- Bits 29-32: worker count (runState)
- Bits 0-28: thread count

Run states: RUNNING (negative), SHUTDOWN (0), STOP (1), TIDYING (2), TERMINATED (3). The transition is monotonic.

## ForkJoinPool Internals
The pool's `ctl` field packs multiple counters:
- Bits 48-63: AC (active thread count complement)
- Bits 32-47: TC (total thread count complement)
- Bits 16-31: SS (stack snapshot of top-most waiting thread)
- Bits 0-15: ID (index of top-most waiting thread)

Work queues are stored in `WorkQueue[]` with even indices for submission queues and odd indices for worker-owned queues.

## CompletableFuture Internals
The `result` field uses special sentinel values:
- `NIL` (static Object) for null results
- `AltResult` wrapper for exceptions
- Completion is CAS-based: `UNSAFE.compareAndSwapObject(this, RESULT, null, value)`

Dependents use a Treiber stack (lock-free stack) via the `stack` field. `postComplete()` pops and runs each dependent.

## StructuredTaskScope Internals
Each subtask is wrapped in a `FutureTask`. The scope maintains a `CountedCompleter`-style count of incomplete subtasks. When a subtask completes, the count decrements. If ShutdownOnSuccess and the result is non-null, the scope is shut down (cancelling remaining subtasks). If ShutdownOnFailure and an exception occurs, the failure is recorded and the scope is shut down.

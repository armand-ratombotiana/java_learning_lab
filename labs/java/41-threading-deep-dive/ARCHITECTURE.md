# Architecture of Java Threading

## JVM Thread Architecture
Each Java thread maps 1:1 to an OS thread (on most platforms). The JVM provides a `java.lang.Thread` abstraction over OS threads. The JVM's thread management includes:
- Thread creation via native OS calls
- Thread scheduling (delegated to OS)
- Thread termination and cleanup
- Inter-thread coordination (wait/notify, LockSupport)

## Executor Framework Architecture
```
Executor (interface) → ExecutorService → AbstractExecutorService → ThreadPoolExecutor
                                    ↑                                  ↓
                                    └── ScheduledThreadPoolExecutor ←─┘
                                    ↓
                              ForkJoinPool
```
ThreadPoolExecutor uses a producer-consumer pattern: producers submit tasks, consumer threads execute them. The work queue decouples submission from execution.

## ForkJoinPool Architecture
ForkJoinPool has:
- `WorkQueue[]` array (power of 2 size)
- Submission queues (even indices) for external tasks
- Worker queues (odd indices) for forked tasks
- `ForkJoinWorkerThread` factory for custom threads
- `ForkJoinPool.ForkJoinWorkerThreadFactory` for thread customization

## CompletableFuture Architecture
```
CompletionStage (interface) → CompletableFuture
                                     ├── result (Object: value | AltResult | NIL)
                                     ├── stack (Treiber stack of dependent stages)
                                     └── executor (for async methods)
```
Stages form a DAG where each stage has zero or more dependents. Completion propagates from leaf stages upward.

## StructuredTaskScope Architecture
```
StructuredTaskScope<T>
  ├── subtasks (List<Future<T>>)
  ├── scopeId (unique per scope)
  ├── policy (ShutdownOnSuccess | ShutdownOnFailure)
  └── result (T or exception)
```
Each scope is single-use. Forked subtasks inherit the scope's context. The owner thread joins to wait for completion.

# Visual Guide to Threading

## Thread State Diagram
```
    ┌─────────────────────────────────────────────────────┐
    │                      NEW                            │
    │                  start() called                     │
    │                      ↓                              │
    │   ┌───────────> RUNNABLE <─────────────────┐        │
    │   │              ↓    ↑                    │        │
    │   │     scheduler selects   lock acquired  │        │
    │   │              ↓    ↑                    │        │
    │   │         wait/notify                    │        │
    │   │   ┌────────> WAITING ──────┐           │        │
    │   │   │         ↑              │           │        │
    │   │   │    Object.wait()   notify()        │        │
    │   │   │         │              │           │        │
    │   │   │    TIMED_WAITING ──────┘           │        │
    │   │   │         ↑                         │        │
    │   │   │   Thread.sleep()                  │        │
    │   │   │    wait(timeout)                  │        │
    │   │   │    join(timeout)                  │        │
    │   │   │                                   │        │
    │   │   └───────── BLOCKED <──── sync(obj)  │        │
    │   │                                       │        │
    │   └───────────────────────────────────────┘        │
    │                      ↓                              │
    │                  TERMINATED                         │
    └─────────────────────────────────────────────────────┘
```

## ThreadPoolExecutor Workflow
```
Task submitted → running < core? → Yes → new worker thread
                → No   → queue space? → Yes → add to queue
                         → No   → running < max? → Yes → new worker
                                  → No   → reject task
```

## Work-Stealing in ForkJoin
```
Worker 1 deque: [T1, T2, T3]  Worker 2 deque: [empty]
Worker 1 pops T3 (LIFO)        Worker 2 steals T1 (FIFO)
                                 Worker 2 processes T1
```

## CompletableFuture Pipeline
```
stage1 → thenApply(f) → stage2 → thenCompose(g) → stage3
                              ↘ exceptionally(h) → stage4
```

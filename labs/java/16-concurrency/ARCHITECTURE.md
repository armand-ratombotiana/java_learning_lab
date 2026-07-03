# Architecture — Concurrency

## Concurrency Models
| Model | Java Support |
|-------|-------------|
| Shared-state | `synchronized`, `Lock`, `ConcurrentHashMap` |
| Message-passing | `BlockingQueue`, `Exchanger` |
| Actor-like | `CompletableFuture` chains |
| Data parallelism | `parallelStream`, `ForkJoinPool` |

## Three-Tier Concurrent Architecture
```
[Controller / IO]  →  [Service / Logic]  →  [Data Access]
   (async)              (thread-safe)        (connection pool)
```

## Thread Safety Strategy
1. **Stateless** — no fields, always thread-safe
2. **Immutable** — record, final fields, thread-safe by design
3. **Synchronised** — lock or synchronized for mutable state
4. **Composable** — `ConcurrentHashMap`, atomic classes, safe recursion

## Concurrent Design Patterns
- **Producer-Consumer** — BlockingQueue
- **Thread Pool** — ExecutorService
- **Fork-Join** — ForkJoinPool, parallel streams
- **Future/Promise** — CompletableFuture
- **Reactor** — reactive streams (Flow API)

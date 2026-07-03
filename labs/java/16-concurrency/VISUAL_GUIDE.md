# Visual Guide — Concurrency

```
┌─────────────────┐     ┌─────────────────┐
│   Thread A      │     │   Thread B      │
│                 │     │                 │
│ synchronized(o) │────►│  blocks on o    │
│   ... modify    │     │   ... waits     │
│ } ← exit, notify│────►│  acquires o     │
│                 │     │   ... modify    │
└─────────────────┘     └─────────────────┘

┌─── Thread Pool (ExecutorService) ──────────────┐
│                                                 │
│  Task Queue ───► [Thread 1] [Thread 2] [Thread 3]
│                    │          │          │
│                    ▼          ▼          ▼
│                 Complete  Complete   Complete
└─────────────────────────────────────────────────┘

┌─── CompletableFuture Pipeline ─────────────────┐
│                                                 │
│  supplyAsync ─► thenApply ─► thenAccept ─► join
│  (fetchData)    (transform)  (consume)    (wait)
│                                                 │
│  ForkJoinPool.commonPool() executes each stage  │
└─────────────────────────────────────────────────┘
```

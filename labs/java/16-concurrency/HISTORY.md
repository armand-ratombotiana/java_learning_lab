# History — Concurrency in Java

- **1996:** Java 1.0 — `Thread`, `synchronized`, `wait`/`notify`
- **1999:** Java 2 — no major concurrency changes
- **2004:** Java 5 — `java.util.concurrent` (Executors, Locks, Atomicy, ConcurrentHashMap)
- **2006:** Java 6 — `ForkJoinPool` preview
- **2011:** Java 7 — `ForkJoinPool` official, `Phaser`
- **2014:** Java 8 — `CompletableFuture`, parallel streams
- **2017:** Java 9 — reactive streams API (`Flow`), `ProcessHandle`
- **2018:** Java 11 — `CompletableFuture` improvements
- **2022:** Java 19 — virtual threads (preview)
- **2023:** Java 21 — virtual threads (GA), structured concurrency (preview)

Key designers: Doug Lea (`java.util.concurrent`), Ron Pressler (Project Loom).

# Performance — Concurrency

## Thread Overhead
- Platform thread: ~1 MB stack (default)
- Context switch: 1-10 µs
- Thread creation: 10-100 µs

## Lock Contention
- Uncontended `synchronized`: ~10 ns
- Uncontended `ReentrantLock`: ~30 ns (fair), ~15 ns (unfair)
- Contended locks: 1-10 µs (parking, context switch)

## CAS vs Lock
```
AtomicInteger.incrementAndGet()  → ~20 ns (CAS)
synchronized integer increment   → ~50 ns (contention-free)
```

## Thread Pool Sizing
```
CPU-bound: Runtime.getRuntime().availableProcessors()
I/O-bound: threads = cores * (1 + wait_time / service_time)
```

## CompletableFuture Overhead
Chaining adds ~50-100 ns per stage. For very fine-grained tasks, consider merging stages.

## Lock Striping
`ConcurrentHashMap` uses 16 locks by default — reduces contention.

## Profiling Tools
- `-XX:+PrintGCDetails` — lock inflation due to GC pauses
- `-XX:+PrintConcurrentLocks` — which threads hold which locks
- `perf` / `async-profiler` — lock profiling

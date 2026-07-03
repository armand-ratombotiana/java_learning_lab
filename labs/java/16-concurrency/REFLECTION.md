# Reflection — Concurrency

## Why This Lab Matters
Concurrency is essential for modern applications — from web servers to data processing pipelines.

## What I Learned
- Thread lifecycle, synchronization, and locks
- ExecutorService for managing thread pools
- CompletableFuture for asynchronous composition
- The Java Memory Model and happens-before

## Questions I Still Have
- How will virtual threads change concurrent programming patterns?
- What are the performance characteristics of `StructuredTaskScope` vs `CompletableFuture`?

## Personal Application
- Replace manual thread management with ExecutorService
- Use CompletableFuture for async service calls
- Apply lock ordering to prevent deadlocks

## Key Takeaways
1. Prefer `java.util.concurrent` over raw `Thread` and `synchronized`
2. Always unlock in `finally` blocks
3. Use `ConcurrentHashMap` over `HashMap` + external sync
4. Measure before optimising — concurrency bugs are subtle

# Reflection — Virtual Threads

## Why This Lab Matters
Virtual threads represent the biggest change to Java concurrency since Java 5's `java.util.concurrent`.

## What I Learned
- Virtual vs platform thread semantics
- Pinning, carrier threads, continuation
- Structured concurrency with `StructuredTaskScope`
- When to choose virtual vs platform threads

## Questions I Still Have
- How will structured concurrency evolve (final API)?
- Will ScopedValue replace ThreadLocal entirely?
- How do frameworks like Spring and Tomcat integrate virtual threads?

## Personal Application
- Replace CompletableFuture chains with synchronous virtual thread code
- Use `Executors.newVirtualThreadPerTaskExecutor()` in all IO-heavy services
- Monitor pinning in CI with `-Djdk.tracePinnedThreads=full`

## Key Takeaways
1. Virtual threads make blocking IO scale — write simple synchronous code
2. Avoid `synchronized` — use `ReentrantLock`
3. Don't pool virtual threads; create them per task
4. Structured concurrency keeps error handling simple

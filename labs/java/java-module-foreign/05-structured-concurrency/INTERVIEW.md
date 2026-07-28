# Interview Questions: Structured Concurrency

## Basic
1. What problem does structured concurrency solve?
2. How does `StructuredTaskScope` relate to `ExecutorService`?
3. What is the `ShutdownOnFailure` policy?

## Intermediate
4. How does `ShutdownOnSuccess` differ from `ShutdownOnFailure`?
5. What happens if you don't call `scope.join()` before accessing results?
6. How do you implement a timeout with structured concurrency?

## Advanced
7. Explain how cancellation propagates through a task scope.
8. Can you nest structured task scopes? What are the semantics?
9. How does error handling differ between `StructuredTaskScope` and `CompletableFuture`?
10. What happens to subtasks if the enclosing scope's owner thread is interrupted?

## Expert
11. How would you implement a custom `StructuredTaskScope` with a deadline policy?
12. How does structured concurrency interact with `ScopedValue` propagation?
13. What are the thread-safety guarantees of `resultNow()` vs `get()`?
14. How does the JVM optimize forked tasks in a structured scope?

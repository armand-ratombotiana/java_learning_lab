# Interview Questions: CompletableFuture

## Company-Specific Focus

### Google
- CompletableFuture: async computation pipeline with thenApply, thenCompose, thenCombine
- CompletableFuture vs Future: callback-driven vs blocking get()
- Exception handling: exceptionally, handle, whenComplete

### Microsoft
- CompletableFuture vs C# Task<T>: similar async composition patterns
- thenApply vs thenApplyAsync: which thread runs the continuation

### Amazon
- Async service composition: chaining multiple microservice calls
- allOf: wait for multiple async operations
- anyOf: first result from multiple async operations

### Meta
- thenApply vs thenCompose: flattening vs mapping
- supplyAsync vs runAsync: async computation with/without return type
- CompletableFuture with virtual threads: new possibilities

### Apple
- Exception recovery: exceptionally provides fallback value
- CompletionStage interface: contract for async computation steps
- CompletableFuture.complete(): manual completion

### Oracle
- CompletableFuture specification: CompletionStage + CompletableFuture
- ForkJoinPool.commonPool(): default async executor
- Custom Executor: supplyAsync(task, executor)

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (CompletableFuture patterns apply to async design) |
| 1114 Print in Order | Easy | Amazon, Google | Async composition technique |

## Real Production Scenarios
- **Uber**: CompletableFuture.allOf for parallel downstream service calls — 50ms reduces to 15ms combined latency
- **Netflix**: CompletableFuture with timeout — orTimeout method to fail fast on slow downstream services

## Interview Patterns & Tips
- **thenApply**: synchronous mapping (runs in completing thread)
- **thenApplyAsync**: runs in a different thread (ForkJoinPool)
- **thenCompose**: async flatMap for dependent async operations
- **allOf**: returns CompletableFuture<Void>, combine results manually
- **exceptionally**: recover from exception with fallback value

## Deep Dive Questions
- **Chain execution**: How does thenApply chain its execution?
- **Threading**: When does thenApply vs thenApplyAsync run its function?
- **CompletableFuture.complete()**: How does manual completion work?
- **ForkJoinPool**: How does CompletableFuture use ForkJoinPool as its default async executor?
- **OOM**: Can CompletableFuture cause OOM with long chains?
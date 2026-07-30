# GUIDE — Parallel Streams

## Step 1: Enabling Parallelism
```java
list.parallelStream()
// or
stream.parallel()
```

## Step 2: ForkJoinPool
- Common pool size = `Runtime.availableProcessors() - 1`
- Custom pool via `ForkJoinPool` constructor + `submit()`

## Step 3: Spliterator Basics
```java
Spliterator<T> split = stream.spliterator();
Spliterator<T> half = split.trySplit();
```

## Step 4: Thread Safety
- Avoid shared mutable state in lambdas
- Use `ConcurrentHashMap`, `Atomic*` classes, or `synchronized`
- `collect()` is safe (per‑thread containers combined at the end)

## Step 5: Exercises
1. Measure sequential vs parallel for CPU‑heavy computations
2. Identify a race condition in a parallel `forEach` that mutates a shared list
3. Implement a custom `Spliterator` for a tree structure

# Stream API Internals

## Stream Pipeline Architecture

### Pipeline Construction

```
Collection.stream()
  .filter(predicate)
  .map(mapper)
  .collect(collector)
```

Each intermediate operation (filter, map, flatMap, etc.) returns a new `Stream` instance that wraps the previous stream and stores the operation's logic as a functional interface (Predicate, Function, etc.).

### Class Hierarchy

```
java.util.stream.ReferencePipeline
  ├── AbstractPipeline (head of pipeline)
  ├── StatelessOp (filter, map, flatMap, etc.)
  └── StatefulOp (sorted, distinct, limit, etc.)
```

The terminal operation triggers pipeline evaluation by traversing backward through the chain, building a single `Spliterator` that encapsulates all operations.

## Lazy Evaluation

### Terminal Operation Trigger

Streams are lazily evaluated. No computation occurs until a terminal operation is invoked:

```java
Stream<Integer> s = list.stream()
    .filter(i -> {            // No evaluation yet
        System.out.println("filter: " + i);
        return i > 0;
    })
    .map(i -> {               // No evaluation yet
        System.out.println("map: " + i);
        return i * 2;
    });
// No output - pipeline not executed
s.count();                    // Triggers evaluation
```

### Push vs Pull Model

The Stream API uses a **push model** at the pipeline level (operations push data to next stage) combined with a **pull model** at the source (Spliterator pulls from source). The `Spliterator.tryAdvance()` method is the key interface:

```java
public interface Spliterator<T> {
    boolean tryAdvance(Consumer<? super T> action);
    Spliterator<T> trySplit();
}
```

## JVM-Level Implementation

### Lambda Capture Mechanics

Lambdas are compiled to `invokedynamic` bytecodes that create anonymous classes at runtime. When capturing variables, the JVM creates a synthetic class:

```java
list.stream().filter(x -> x > 0)
// Becomes something like:
new Lambda$$1(x -> x > 0)
```

The captured variable is stored as a `final` field in the lambda class (effectively final via "effectively final" analysis).

### Method Handles

Modern JVM implementations use `MethodHandle` for lambda invocation rather than reflection, enabling JIT optimization of lambda call chains.

### Spliterator Implementation

Each pipeline stage contributes to the final `Spliterator`:

| Operation | Spliterator Behavior |
|-----------|---------------------|
| `filter` | Wraps source, conditionally accepts elements |
| `map` | Transforms elements during traversal |
| `flatMap` | Creates nested spliterators, flattens |
| `sorted` | Requires buffering all elements |

## Parallel Stream Internals

### Fork/Join Framework

Parallel streams use `ForkJoinPool.commonPool()` which defaults to `Runtime.availableProcessors() - 1` threads.

```java
list.parallelStream()
    .filter(...)
    .map(...)
    .collect(...);
```

### Work-Stealing Algorithm

The Fork/Join framework implements work-stealing:
1. Each thread has a deque of tasks
2. When a thread finishes its local tasks, it steals from other threads' deque ends
3. This minimizes contention and maximizes CPU utilization

### Spliterator Characteristics

```java
// Characteristics that affect parallel processing
Spliterator.ORDERED   // Elements have defined order
Spliterator.DISTINCT  // No duplicates (for distinct())
Spliterator.SORTED    // Elements sorted (for sorted())
Spliterator.SIZED     // Size known (for sized collections)
```

The `trySplit()` method is critical for parallelism - it attempts to split the data roughly in half.

## Short-Circuiting Optimization

### Lazy Evaluation + Short-Circuit

Certain operations can terminate early:

| Operation | Behavior |
|-----------|----------|
| `findFirst()` | Returns first element, stops pipeline |
| `anyMatch()` | Returns on first `true` |
| `noneMatch()` | Returns on first `true` |
| `limit(n)` | Stops after n elements |

### Implementation Pattern

```java
// AbstractPipeline.java
@Override
public final boolean anyMatch(Predicate<? super P_IN> predicate) {
    return evaluateMatchesTerminalOp((s, sink) -> {
        return s.anyMatch(predicate, sink);
    });
}
```

The terminal operation checks `tryAdvance()` return value to determine if pipeline should continue.

## Memory Allocation

### Stateless Operations

Stateless intermediate operations don't require buffering - elements flow directly from source to terminal:

```
Source → filter → map → collect
```

Each `tryAdvance` call propagates through the chain.

### Stateful Operations

Stateful operations require memory buffers:

| Operation | Buffer Type |
|----------|-------------|
| `sorted()` | `ArrayList` (Arrays.mergeSort) |
| `distinct()` | `HashSet` or `LinkedHashSet` |
| `limit(n)` | `ArrayList` with size hint |
| `skip(n)` | Counts/skips first n elements |

### Collector Accumulator Types

```java
// ReducingAccumulator uses arrays for primitive streams
// ListCollector uses ArrayList
// SetCollector uses HashSet/LinkedHashSet
```

## Performance Considerations

### Method Inlining

The JIT compiler can inline lambda bodies when they're simple, eliminating virtual dispatch overhead.

### Escape Analysis

JIT may allocate objects on stack instead of heap if they don't escape the method, eliminating GC pressure.

### Vectorization

For primitive streams, JIT can use SIMD instructions for batch operations when applicable.

## Debugging Stream Pipelines

### -XX:+PrintCompilation

```
$ java -XX:+UnlockDiagnosticVMOptions \
      -XX:+PrintCompilation \
      -XX:+PrintInlining \
      MyApp
```

Shows JIT compilation decisions, inlined methods.

### Flight Recorder

```java
$ java -XX:StartFlightRecording:filename=recording.jfr \
      -XX:FlightRecorderOptions:stackdepth=128 MyApp
```

Records detailed execution traces including stream pipeline events.
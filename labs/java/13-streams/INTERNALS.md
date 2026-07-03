# Streams — Internal Mechanics

## ReferencePipeline Stages

The stream pipeline is implemented as a linked list of pipeline stages:

```
Head (source) → StatelessOp (filter) → StatelessOp (map) → TerminalOp (collect)
```

Each stage extends `ReferencePipeline` and implements `opWrapSink()`:

```java
// Filter stage:
Sink<T> opWrapSink(int flags, Sink<T> sink) {
    return new Sink.ChainedReference<>(sink) {
        @Override
        public void accept(T element) {
            if (predicate.test(element)) {
                downstream.accept(element);
            }
        }
    };
}
```

## Sink Chain

During terminal operation, a chain of `Sink` objects is created:

```java
// Simplification of how sinks are chained:
Sink<String> terminalSink = new Sink.Impl.of(collector);
Sink<String> mapSink = mapStage.opWrapSink(0, terminalSink);
Sink<String> filterSink = filterStage.opWrapSink(0, mapSink);
// Now: source → filterSink → mapSink → terminalSink
```

## Spliterator

The `Spliterator` interface is the key to parallel stream performance:

```java
public interface Spliterator<T> {
    boolean tryAdvance(Consumer<? super T> action);
    Spliterator<T> trySplit();
    long estimateSize();
    int characteristics();
}
```

`trySplit()` partitions the remaining elements into two halves. The `ForkJoinPool` recursively splits until segments are small enough:

```java
// Typical split strategy for ArrayList:
Spliterator<String> trySplit() {
    int lo = index, mid = (lo + fence) >>> 1;  // Binary split
    if (lo >= mid) return null;                 // Too small to split
    index = mid;
    return new ArrayListSpliterator<>(list, lo, mid, ...);
}
```

## Characteristics Flags

```java
int ORDERED = 0x00000010;   // Defined encounter order
int DISTINCT = 0x00000001;  // No duplicates
int SORTED = 0x00000004;    // Sorted by natural/comparator
int SIZED = 0x00000040;     // Known exact size
int NONNULL = 0x00000100;   // No null elements
int IMMUTABLE = 0x00000400; // Source cannot be modified
int CONCURRENT = 0x00001000;// Source can be safely modified
int SUBSIZED = 0x00004000;  // Splits are also SIZED
```

Operations propagate and combine these flags to optimize execution.

## Parallel Merge for Collectors

The `collect()` operation uses a three-phase pattern:

```java
// Phase 1: Accumulate (each thread)
A result = collector.supplier().get();
for (T element : segment) {
    collector.accumulator().accept(result, element);
}

// Phase 2: Merge (combine results from threads)
A merged = collector.combiner().apply(result1, result2);

// Phase 3: Finish (optional transformation)
R finalResult = collector.finisher().apply(merged);
```

## Stream Fusion

The JIT compiler can fuse adjacent stream operations, avoiding intermediate object creation:

```
.filter(p).map(f)  →  FilterMap operation in one pass
```

This is done by inlining the lambda invocations and merging accept() methods. Not guaranteed but common with hotspot JIT.

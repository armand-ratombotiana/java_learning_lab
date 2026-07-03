# Streams — How It Works

## Step 1: Stream Source Creation

```java
Stream<String> stream = list.stream();
```

`Collection.stream()` calls `Spliterator.trySplit()` to enable parallel traversal. The `Spliterator` knows how to partition the collection and provides characteristics (SIZED, ORDERED, DISTINCT, etc.).

## Step 2: Pipeline Construction (Intermediate Operations)

Intermediate operations return a new `Stream` reference:

```java
Stream<String> filtered = stream.filter(s -> s.startsWith("A"));
Stream<String> mapped = filtered.map(String::toLowerCase);
```

No data processing happens yet. Each operation wraps the previous stage in a `ReferencePipeline` object:

```
list.stream()
  → Head pipeline (source stage)
  → StatelessOp (filter stage, stores Predicate)
  → StatelessOp (map stage, stores Function)
```

## Step 3: Terminal Operation Triggers Execution

```java
List<String> result = mapped.toList();
```

The terminal operation creates a terminal sink and calls `copyInto()` on the pipeline:
1. The terminal sink wraps around the last stage
2. Each stage wraps itself around the previous stage's sink
3. The source stage pushes elements through the chain:
   ```
   For each element:
     Source → filter(s -> s.startsWith("A")) → map(String::toLowerCase) → toList()
   ```

## Step 4: Short-Circuiting

For operations like `findFirst()`:
- The pipeline tracks whether short-circuiting is requested
- After the first matching element, no further elements are processed
- `limit()` stops after the specified count
- `anyMatch()` stops at the first match

## Step 5: Parallel Execution

```java
list.parallelStream().filter(p).map(f).collect(toList());
```

1. `trySplit()` divides the source into segments (ideally balanced)
2. Each segment is processed by a `ForkJoinTask` in the common pool
3. Operations on each segment run independently
4. Terminal operation merges:
   - `collect()` → combiner function merges result containers
   - `reduce()` → associative accumulator + combiner
   - `forEach()` → side-effect per element (no merge needed)

## Step 6: Stateful vs Stateless Operations

- **Stateless**: `filter`, `map`, `flatMap` — each element processed independently
- **Stateful**: `sorted`, `distinct`, `limit`, `skip` — must buffer elements

Stateful operations are barriers in parallel streams:
- `sorted()` must gather all elements from all segments, sort, and redistribute
- `distinct()` uses a shared `ConcurrentHashMap` for deduplication
- These operations are more expensive in parallel than sequential

# Streams — Theoretical Foundation

## Stream Pipeline Structure

A stream pipeline has three parts:
1. **Source**: Collection, array, generator function, I/O channel
2. **Intermediate operations**: Transform stream to stream (lazy)
3. **Terminal operation**: Produces result or side effect (eager)

```java
list.stream()            // Source
    .filter(x -> x > 0)  // Intermediate (lazy)
    .map(Object::toString) // Intermediate (lazy)
    .collect(toList());  // Terminal (eager)
```

## Stream Characteristics

- **Not a data structure**: Doesn't store elements; carries elements from source through pipeline
- **Lazy evaluation**: Intermediate operations don't execute until a terminal operation is invoked
- **Consumable**: Stream can be traversed only once; no reusable
- **Parallelizable**: `parallelStream()` or `parallel()` enables concurrent processing
- **Stateless operations preferred**: Avoid side effects in lambda expressions

## Stream Sources

```java
// Collections
list.stream();
list.parallelStream();

// Arrays
Arrays.stream(array);
Stream.of("a", "b", "c");

// Ranges
IntStream.range(0, 10);
LongStream.rangeClosed(1, 100);

// Generated
Stream.generate(Math::random);           // Infinite
Stream.iterate(0, n -> n + 1);           // Infinite
Stream.iterate(0, n -> n < 100, n -> n + 1); // Finite (Java 9+)

// Files
Files.lines(Path.of("file.txt"));
Files.list(Path.of("dir/"));
```

## Intermediate Operations

| Operation | Description |
|-----------|-------------|
| `filter(Predicate)` | Keep elements matching predicate |
| `map(Function)` | Transform each element |
| `flatMap(Function)` | Flatten nested streams |
| `distinct()` | Remove duplicates (uses equals()) |
| `sorted()` | Sort naturally |
| `sorted(Comparator)` | Sort with comparator |
| `peek(Consumer)` | Debug each element (side effect) |
| `limit(long)` | Truncate to first n elements |
| `skip(long)` | Skip first n elements |
| `takeWhile(Predicate)` | Take while predicate true (Java 9+) |
| `dropWhile(Predicate)` | Drop while predicate true (Java 9+) |

## Terminal Operations

| Operation | Description |
|-----------|-------------|
| `collect(Collector)` | Reduce into collection or value |
| `reduce(BinaryOperator)` | Combine elements to single value |
| `forEach(Consumer)` | Perform action on each element |
| `count()` | Count elements |
| `anyMatch(Predicate)` | Any element matches? |
| `allMatch(Predicate)` | All elements match? |
| `noneMatch(Predicate)` | No element matches? |
| `findFirst()` | First element (respects encounter order) |
| `findAny()` | Any element (for parallel streams) |
| `min(Comparator)` | Minimum element |
| `max(Comparator)` | Maximum element |
| `toList()` | Collect to immutable list (Java 16+) |

## Collectors

```java
// To collections
.collect(Collectors.toList());
.collect(Collectors.toSet());
.collect(Collectors.toCollection(ArrayList::new));

// To maps
.collect(Collectors.toMap(Function keyMapper, Function valueMapper));
.collect(Collectors.groupingBy(Function classifier));
.collect(Collectors.partitioningBy(Predicate predicate));

// Joining
.collect(Collectors.joining(", "));

// Aggregating
.collect(Collectors.summarizingInt(ToIntFunction));
.collect(Collectors.averagingDouble(ToDoubleFunction));

// Reducing
.collect(Collectors.reducing(identity, BinaryOperator));
```

## Parallel Streams

```java
list.parallelStream()
    .filter(x -> expensiveOperation(x))
    .map(this::transform)
    .collect(toList());
```

- Uses `ForkJoinPool.commonPool()` by default
- Splits source into segments, processes in parallel, combines results
- **Beware**: Shared mutable state in lambdas causes thread-safety issues
- **Order**: `forEachOrdered()` preserves order; `forEach()` does not

## Stream Characteristics

Streams have characteristics that affect optimization:
- `ORDERED`: Encounter order is defined
- `DISTINCT`: No duplicates
- `SORTED`: Elements are sorted
- `SIZED`: Known size
- `NONNULL`: No null elements

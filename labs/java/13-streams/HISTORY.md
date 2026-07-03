# Streams — Evolution Across Java Versions

## Java 8 (2014): Streams Introduced

The entire Stream API was introduced as part of Project Lambda:
- `java.util.stream` package
- `Stream<T>`, `IntStream`, `LongStream`, `DoubleStream`
- Intermediate operations: filter, map, flatMap, distinct, sorted, peek, limit, skip
- Terminal operations: collect, reduce, forEach, count, anyMatch, allMatch, findFirst, findAny
- `Collectors` utility class with toList, toSet, toMap, groupingBy, partitioningBy, joining, reducing
- Parallel stream support via `ForkJoinPool.commonPool()`
- `Optional` for representing absent results

## Java 9 (2017): Stream Enhancements

- `takeWhile(Predicate)` — take elements while predicate is true, then stop
- `dropWhile(Predicate)` — drop elements while predicate is true, then take rest
- `Stream.iterate(T seed, Predicate hasNext, UnaryOperator next)` — finite iteration
- `Stream.ofNullable(T)` — create stream of one element or empty if null

```java
Stream.iterate(0, n -> n < 100, n -> n + 1);  // Java 9+
Stream.ofNullable(someObject);                  // Java 9+
```

## Java 10 (2018): Collectors.toUnmodifiableList

```java
List<String> immutable = stream.collect(Collectors.toUnmodifiableList());
Set<String> immutableSet = stream.collect(Collectors.toUnmodifiableSet());
```

## Java 11 (2018): Minor

No major stream changes. `Files.readString()` and `Files.writeString()` added.

## Java 12 (2019): Collectors.teeing

```java
// Merge results of two collectors:
double avg = stream.collect(Collectors.teeing(
    summingDouble(Item::getPrice),
    counting(),
    (sum, count) -> sum / count
));
```

## Java 16 (2021): Stream.toList()

```java
// Direct to immutable list — no Collector needed:
List<String> result = stream.toList();
```

This is more concise than `collect(Collectors.toList())` and returns an immutable list.

## Java 17 (2021): Stream.toList() Stabilized

`stream.toList()` is now the preferred way to collect to a list.

## Java 21 (2023): No major stream changes

Stream API is mature. Performance improvements in parallel stream splitting and JIT optimization continue.

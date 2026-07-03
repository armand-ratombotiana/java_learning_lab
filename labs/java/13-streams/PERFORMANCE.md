# Performance — Java Streams

## Primitive Specialisations
Use `IntStream`, `LongStream`, `DoubleStream` to avoid boxing overhead:
```java
int sum = IntStream.range(0, 1_000_000).sum(); // No boxing
```

## Parallel Streams
```java
long count = list.parallelStream().filter(predicate).count();
```
- Best for CPU-intensive, stateless operations on large datasets.
- Splitting cost must be outweighed by parallelism gains.

## Benchmarking Rules
- Stream overhead is significant for small collections (< 1K elements) — loops may be faster.
- Use JMH for reliable measurements.

## Lazy Evaluation
Short-circuit operations (`findFirst`, `limit`, `anyMatch`) stop early — prefer them over `filter + collect + get(0)`.

## Collector Performance
- `Collectors.toList()` vs `toCollection(ArrayList::new)` — negligible difference.
- `collectingAndThen` adds a finishing transform — use sparingly in hot paths.

## Stateful Operations
`distinct()`, `sorted()` require buffering — O(n) memory cost.

## Common Pitfall
```java
stream.sorted().filter(...) // filter after sort is wasteful
stream.filter(...).sorted() // filter before sort reduces work
```

# Streams — Code Deep Dive

## Custom Collector Implementation

```java
public class CollectorDeepDive {
    public static <T> Collector<T, ?, List<T>> toImmutableList() {
        return Collector.of(
            ArrayList::new,                     // Supplier
            List::add,                          // Accumulator
            (left, right) -> { left.addAll(right); return left; }, // Combiner
            Collections::unmodifiableList,      // Finisher
            Collector.Characteristics.CONCURRENT // Characteristics
        );
    }

    // Usage:
    List<String> immutable = stream.collect(toImmutableList());
}
```

## flatMap in Depth

```java
// flatMap with nested collections:
List<List<Integer>> nested = List.of(
    List.of(1, 2, 3),
    List.of(4, 5),
    List.of(6, 7, 8, 9)
);

List<Integer> flattened = nested.stream()
    .flatMap(Collection::stream)
    .toList();  // [1, 2, 3, 4, 5, 6, 7, 8, 9]

// flatMap with Optional:
List<String> names = List.of("Alice", "Bob", null, "Charlie");
List<String> nonNull = names.stream()
    .flatMap(Optional::ofNullable)
    .map(String::toUpperCase)
    .toList();  // [ALICE, BOB, CHARLIE]
```

## Custom Grouping with Downstream Collectors

```java
record Order(String region, String status, double amount) {}

List<Order> orders = List.of(
    new Order("US", "SHIPPED", 100),
    new Order("US", "PENDING", 50),
    new Order("EU", "SHIPPED", 200),
    new Order("EU", "PENDING", 75)
);

// Multi-level grouping:
Map<String, Map<String, Long>> result = orders.stream()
    .collect(groupingBy(Order::region,
             groupingBy(Order::status, counting())));

// {US={SHIPPED=1, PENDING=1}, EU={SHIPPED=1, PENDING=1}}

// Aggregations:
Map<String, DoubleSummaryStatistics> stats = orders.stream()
    .collect(groupingBy(Order::region,
             summarizingDouble(Order::amount)));

stats.forEach((region, stat) ->
    System.out.println(region + ": avg=" + stat.getAverage()
        + ", total=" + stat.getSum()));
```

## Parallel Stream Pitfall

```java
// BAD — shared mutable state causes incorrect results:
List<Integer> results = new ArrayList<>();  // Not thread-safe!
IntStream.range(0, 1000)
    .parallel()
    .forEach(results::add);  // Results may miss elements!
// Expected: 1000 elements. Actual: maybe 987.

// GOOD — use reduction or concurrent collector:
List<Integer> results = IntStream.range(0, 1000)
    .parallel()
    .boxed()
    .collect(toList());  // Thread-safe collector
// Expected: 1000 elements. Actual: 1000.
```

## Reduce vs Collect

```java
// Custom reduce:
int product = IntStream.range(1, 6)
    .reduce(1, (a, b) -> a * b);  // 1*2*3*4*5 = 120

// Optional return when no identity:
OptionalInt product = IntStream.range(1, 6)
    .reduce((a, b) -> a * b);  // OptionalInt[120]

// Collect with StringBuilder:
String result = Stream.of("a", "b", "c")
    .collect(StringBuilder::new,
             StringBuilder::append,
             StringBuilder::append)
    .toString();
```

## Infinite Streams

```java
// Fibonacci with Stream.iterate:
Stream.iterate(new int[]{0, 1}, f -> new int[]{f[1], f[0] + f[1]})
    .limit(10)
    .map(f -> f[0])
    .forEach(System.out::println);  // 0, 1, 1, 2, 3, 5, 8, 13, 21, 34

// Random generation:
Stream.generate(Math::random)
    .limit(5)
    .forEach(System.out::println);
```

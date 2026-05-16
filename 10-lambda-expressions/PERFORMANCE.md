# Performance Considerations for Lambdas

## Lambda Creation Overhead

- Lambdas are created once and reused
- Creation cost is minimal for most use cases
- HotSpot optimizes lambda invocation heavily

## Memory Considerations

### Object Creation
- Each lambda can create an object
- Use method references when possible (may avoid object creation)
- Lazy stream operations minimize memory footprint

### Capturing Lambdas vs Non-Capturing

```java
// Non-capturing (stateless) - can be optimized
Function<String, String> f = String::toUpperCase;

// Capturing - creates object per lambda
int factor = 5;
Function<Integer, Integer> multiply = x -> x * factor;
```

## Stream Performance Tips

### Sequential vs Parallel
```java
// Sequential - better for small data or CPU-intensive
list.stream()...

// Parallel - better for large data and simple operations  
list.parallelStream()...
```

### Short-Circuit Operations
```java
// Eager - processes all
list.stream().filter(...).collect(...);

// Lazy with findFirst - stops early
list.stream()
    .filter(...)
    .findFirst();
```

## Common Performance Pitfalls

1. **Nested Streams**: Avoid stream-of-streams, use flatMap
2. **Unnecessary Boxing**: Use primitive streams (IntStream, LongStream)
3. **Multiple Collections**: Chain operations, don't create intermediate lists
4. **Parallel When Not Needed**: Parallel has overhead, not always faster

## Benchmark Example

```java
// IntStream is much faster than Stream<Integer>
long start = System.nanoTime();
IntStream.range(0, 10_000_000).sum();
System.out.println("IntStream: " + (System.nanoTime() - start));

start = System.nanoTime();
Stream<Integer> stream = IntStream.range(0, 10_000_000).boxed();
stream.reduce(0, Integer::sum);
System.out.println("Stream<Integer>: " + (System.nanoTime() - start));
```
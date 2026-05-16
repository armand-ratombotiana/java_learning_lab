# Performance Considerations in FP

## Stream Performance

### Lazy Evaluation
Stream operations are lazy—they don't execute until terminal operation:

```java
// Does NOT run - just builds pipeline
stream.filter(x -> expensiveOperation(x))
      .map(x -> anotherExpensive(x));

// Now it runs
list.stream()
    .filter(x -> expensiveOperation(x))
    .map(x -> anotherExpensive(x))
    .count();  // Terminal triggers execution
```

### Short-Circuit Operations
Some operations stop early:

```java
// Only processes until first match
Optional<String> first = list.stream()
    .filter(x -> x.length() > 5)
    .findFirst();  // Stops at first match
```

## Performance Tips

### 1. Use Primitive Streams
```java
// Slower - boxing overhead
list.stream()
    .mapToInt(Integer::intValue)
    .sum();

// Fast - no boxing
IntStream.of(1, 2, 3, 4, 5).sum();
```

### 2. Use Collector Appropriately
```java
// Slow - creates many strings
list.stream()
    .collect(Collectors.joining(","));

// Fast - pre-sized
list.stream()
    .collect(Collectors.joining(new StringBuilder(), ",", ""));
```

### 3. Don't Use Streams for Everything
```java
// Simple loop is faster for small data
for (int i = 0; i < list.size(); i++) {
    process(list.get(i));
}
```

### 4. Limit Intermediate Operations
Each operation creates new objects:
```java
// Creates many intermediate collections
list.stream()
    .filter(...)
    .map(...)
    .filter(...)
    .map(...)
    .collect(...);
```

## Parallel Streams

```java
// Fast for large datasets, simple operations
list.parallelStream()
    .map(this::processExpensive)
    .collect(Collectors.toList());

// Slower for small data or complex operations
// Overhead of splitting/merging exceeds benefit
```
# Module 04: Streams API - Solution

## Overview
This solution provides comprehensive reference implementations for Java Stream API, covering creation, operations, and advanced patterns.

## Package Structure
```
com.learning.lab.module04.solution
```

## Solution Components

### 1. Solution.java
Complete implementations covering:

#### Stream Creation
- From Collections (List, Set, Map)
- From Arrays
- From Values (Stream.of, Stream.empty, Stream.ofNullable)
- From Iteration (Stream.iterate with predicate)
- From Generation (Stream.generate)
- From Strings (String.chars())
- From Random (Random.ints, Random.doubles)
- Primitive Streams (IntStream, LongStream, DoubleStream)

#### Intermediate Operations
- **filter(Predicate)**: Filter elements by condition
- **map(Function)**: Transform each element
- **flatMap(Function)**: Transform to stream and flatten
- **distinct()**: Remove duplicates
- **sorted()**: Sort elements
- **sorted(Comparator)**: Sort with custom comparator
- **limit(n)**: Take first n elements
- **skip(n)**: Skip first n elements
- **peek(Consumer)**: Debug/examine elements

#### Terminal Operations
- **forEach(Consumer)**: Process each element
- **forEachOrdered(Consumer)**: Process in order (for parallel)
- **collect(Collector)**: Accumulate to collection
- **reduce(BinaryOperator)**: Reduce to single value
- **reduce(identity, BinaryOperator)**: Reduce with initial value
- **count()**: Count elements
- **anyMatch(Predicate)**: Any element matches
- **allMatch(Predicate)**: All elements match
- **noneMatch(Predicate)**: No elements match
- **findFirst()**: Get first element
- **findAny()**: Get any element (parallel-friendly)

#### Collectors
- **toList(), toSet(), toMap()**: Basic collection collectors
- **joining()**: Concatenate strings
- **groupingBy()**: Group by classifier
- **partitioningBy()**: Partition by predicate
- **counting(), summingInt(), averagingInt()**: Aggregation
- **summarizingInt()**: Statistics collection
- **mapping()**: Transform before collecting

#### Optional
- **Optional.of()**: Create with value
- **Optional.empty()**: Create empty
- **Optional.ofNullable()**: Create from nullable
- **map(), flatMap(), filter()**: Transform
- **orElse(), orElseGet(), orElseThrow()**: Retrieve with fallback

#### Advanced Patterns
- **Lazy Evaluation**: Streams are lazy until terminal
- **Short-Circuiting**: Some operations stop early
- **Stateful Operations**: distinct, sorted require more memory

#### Parallel Streams
- **parallel()**: Convert to parallel
- **parallelStream()**: Create parallel from collection
- **Thread Safety**: Use concurrent collections

### 2. Test.java
Comprehensive test suite with 60+ tests covering:
- All stream creation methods
- All intermediate operations
- All terminal operations
- All collectors
- Optional handling
- Primitive streams
- Parallel stream behavior

## Running the Solution

```bash
cd 04-streams-api/SOLUTION
javac -d . Solution.java Test.java
java com.learning.lab.module04.solution.Solution
java com.learning.lab.module04.solution.Test
```

## Key Concepts

### Stream Pipeline
```
Source → [Intermediate]* → Terminal
```

Each stream can have zero or more intermediate operations and exactly one terminal operation.

### Short-Circuiting Operations
Intermediate:
- limit(n), skip(n), takeWhile(), dropWhile()

Terminal:
- anyMatch(), allMatch(), noneMatch()
- findFirst(), findAny()

### Stateless vs Stateful
**Stateless** (can process elements independently):
- filter, map, flatMap

**Stateful** (need to see all elements):
- distinct, sorted, limit, skip

### Lazy Evaluation
Streams don't execute until a terminal operation is called. Intermediate operations build a pipeline that executes only when needed.

## Best Practices

1. **Prefer method references** over lambdas when possible
2. **Avoid side effects** in stream operations
3. **Use parallel streams** carefully - not always faster
4. **Close streams** when using I/O sources
5. **Use short-circuiting** operations to improve performance
6. **Avoid changing** external state in parallel streams
7. **Use appropriate** collectors for the use case

## Common Pitfalls

- Forgetting terminal operation (stream won't execute)
- Modifying source collection while streaming
- Using parallel streams with non-thread-safe collectors
- Using Stateful operations in parallel incorrectly
- Not handling null values properly
- Overusing streams (sometimes simple loops are better)

## Performance Tips

1. Use primitive streams (IntStream, etc.) for numeric operations
2. Use `findFirst()` over `findAny()` when order matters
3. Use `limit()` to avoid processing unnecessary elements
4. Consider using `Collector.of()` for custom collections
5. Use `collect()` with concurrent collectors for parallel

## When to Use Streams

| Use Case | Recommended Approach |
|----------|----------------------|
| Simple iteration | for-each loop |
| Transformation | map() |
| Filtering | filter() |
| Aggregation | reduce(), collect() |
| Grouping | groupingBy() |
| Finding | findFirst(), findAny() |
| Checking | anyMatch(), allMatch() |
| Numeric operations | IntStream, LongStream, DoubleStream |
| Parallel processing | parallelStream() |
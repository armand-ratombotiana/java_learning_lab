# 🔍 Streams API - Deep Dive

## Table of Contents
1. [Streams Fundamentals](#streams-fundamentals)
2. [Stream Creation](#stream-creation)
3. [Intermediate Operations](#intermediate-operations)
4. [Terminal Operations](#terminal-operations)
5. [Collectors](#collectors)
6. [Optional Type](#optional-type)
7. [Parallel Streams](#parallel-streams)
8. [Performance & Best Practices](#performance--best-practices)

---

## Streams Fundamentals

### What is a Stream?

A **Stream** is a sequence of elements supporting sequential and parallel aggregate operations.

```
Collection (Data)
       ↓
    Stream (Pipeline)
       ↓
   Operations (Transformations)
       ↓
    Result (Terminal Operation)
```

**Key Characteristics**:
- **Lazy**: Operations not executed until terminal operation
- **Functional**: Uses functional interfaces and lambdas
- **Immutable**: Doesn't modify source collection
- **Composable**: Chain multiple operations
- **Parallelizable**: Can process in parallel

### Stream vs Collection

```
Collection:
├─ Stores data in memory
├─ Eager evaluation
├─ Can iterate multiple times
├─ Mutable operations
└─ Example: List, Set, Map

Stream:
├─ Computes on-demand
├─ Lazy evaluation
├─ Single-use (one-time traversal)
├─ Immutable operations
└─ Example: stream(), parallelStream()
```

### Stream Pipeline

```
Source → Intermediate Operations → Terminal Operation → Result

Example:
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

numbers.stream()           // Source
    .filter(n -> n > 2)    // Intermediate
    .map(n -> n * 2)       // Intermediate
    .forEach(System.out::println);  // Terminal
```

### Lazy Evaluation

```java
// Operations are NOT executed until terminal operation
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

Stream<Integer> stream = numbers.stream()
    .filter(n -> {
        System.out.println("Filtering: " + n);
        return n > 2;
    })
    .map(n -> {
        System.out.println("Mapping: " + n);
        return n * 2;
    });

// Nothing printed yet! (Lazy)

stream.forEach(System.out::println);  // Terminal operation

// Output:
// Filtering: 1
// Filtering: 2
// Filtering: 3
// Mapping: 3
// 6
// Filtering: 4
// Mapping: 4
// 8
// Filtering: 5
// Mapping: 5
// 10
```

---

## Stream Creation

### From Collection

```java
List<String> list = Arrays.asList("A", "B", "C");
Stream<String> stream = list.stream();

Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 3));
Stream<Integer> stream = set.stream();

Map<String, Integer> map = new HashMap<>();
Stream<String> keyStream = map.keySet().stream();
Stream<Integer> valueStream = map.values().stream();
```

### From Array

```java
String[] array = {"A", "B", "C"};
Stream<String> stream = Arrays.stream(array);

int[] intArray = {1, 2, 3, 4, 5};
IntStream stream = Arrays.stream(intArray);
```

### From Stream.of()

```java
Stream<String> stream = Stream.of("A", "B", "C");
Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
```

### From Stream.generate()

```java
// Infinite stream of random numbers
Stream<Double> randomStream = Stream.generate(Math::random);

// Infinite stream of same value
Stream<String> infiniteStream = Stream.generate(() -> "A");

// Use limit() to get finite stream
List<Double> randomNumbers = Stream.generate(Math::random)
    .limit(10)
    .collect(Collectors.toList());
```

### From Stream.iterate()

```java
// Infinite stream: 0, 1, 2, 3, ...
Stream<Integer> infiniteStream = Stream.iterate(0, n -> n + 1);

// Finite stream: 0, 1, 2, 3, 4
List<Integer> numbers = Stream.iterate(0, n -> n + 1)
    .limit(5)
    .collect(Collectors.toList());

// With predicate (Java 9+): 0, 1, 2, 3, 4
List<Integer> numbers = Stream.iterate(0, n -> n < 5, n -> n + 1)
    .collect(Collectors.toList());
```

### From Files

```java
// Read lines from file
try (Stream<String> lines = Files.lines(Paths.get("file.txt"))) {
    lines.forEach(System.out::println);
}

// Count lines
long lineCount = Files.lines(Paths.get("file.txt"))
    .count();
```

### Primitive Streams

```java
// IntStream, LongStream, DoubleStream
IntStream intStream = IntStream.range(0, 10);      // 0-9
IntStream intStream = IntStream.rangeClosed(0, 10); // 0-10

// Convert to Stream<Integer>
Stream<Integer> stream = intStream.boxed();

// Convert back to IntStream
IntStream intStream = stream.mapToInt(Integer::intValue);
```

---

## Intermediate Operations

### filter()

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

List<Integer> evenNumbers = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());
// Result: [2, 4, 6]

// Multiple filters
List<Integer> result = numbers.stream()
    .filter(n -> n > 2)
    .filter(n -> n < 6)
    .collect(Collectors.toList());
// Result: [3, 4, 5]
```

### map()

```java
List<String> words = Arrays.asList("hello", "world");

List<Integer> lengths = words.stream()
    .map(String::length)
    .collect(Collectors.toList());
// Result: [5, 5]

// Transform to different type
List<String> numbers = Arrays.asList("1", "2", "3");
List<Integer> integers = numbers.stream()
    .map(Integer::parseInt)
    .collect(Collectors.toList());
// Result: [1, 2, 3]
```

### flatMap()

```java
// Flatten nested streams
List<List<Integer>> nestedList = Arrays.asList(
    Arrays.asList(1, 2),
    Arrays.asList(3, 4),
    Arrays.asList(5, 6)
);

List<Integer> flattened = nestedList.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList());
// Result: [1, 2, 3, 4, 5, 6]

// Split strings into words
List<String> sentences = Arrays.asList(
    "Hello World",
    "Java Streams"
);

List<String> words = sentences.stream()
    .flatMap(s -> Arrays.stream(s.split(" ")))
    .collect(Collectors.toList());
// Result: [Hello, World, Java, Streams]
```

### distinct()

```java
List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4);

List<Integer> unique = numbers.stream()
    .distinct()
    .collect(Collectors.toList());
// Result: [1, 2, 3, 4]
```

### sorted()

```java
List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 9);

// Natural order
List<Integer> sorted = numbers.stream()
    .sorted()
    .collect(Collectors.toList());
// Result: [1, 2, 5, 8, 9]

// Custom order
List<Integer> descending = numbers.stream()
    .sorted((a, b) -> b - a)
    .collect(Collectors.toList());
// Result: [9, 8, 5, 2, 1]

// Sort objects
List<String> words = Arrays.asList("banana", "apple", "cherry");
List<String> sorted = words.stream()
    .sorted()
    .collect(Collectors.toList());
// Result: [apple, banana, cherry]

// Sort by length
List<String> byLength = words.stream()
    .sorted(Comparator.comparingInt(String::length))
    .collect(Collectors.toList());
// Result: [apple, banana, cherry]
```

### peek()

```java
// Debug intermediate values
List<Integer> result = Arrays.asList(1, 2, 3, 4, 5).stream()
    .filter(n -> n > 2)
    .peek(n -> System.out.println("After filter: " + n))
    .map(n -> n * 2)
    .peek(n -> System.out.println("After map: " + n))
    .collect(Collectors.toList());

// Output:
// After filter: 3
// After map: 6
// After filter: 4
// After map: 8
// After filter: 5
// After map: 10
```

### limit() and skip()

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// First 5 elements
List<Integer> first5 = numbers.stream()
    .limit(5)
    .collect(Collectors.toList());
// Result: [1, 2, 3, 4, 5]

// Skip first 3, take next 4
List<Integer> middle = numbers.stream()
    .skip(3)
    .limit(4)
    .collect(Collectors.toList());
// Result: [4, 5, 6, 7]

// Pagination
int pageSize = 10;
int pageNumber = 2;
List<Integer> page = numbers.stream()
    .skip((long) pageNumber * pageSize)
    .limit(pageSize)
    .collect(Collectors.toList());
```

---

## Terminal Operations

### forEach()

```java
List<String> words = Arrays.asList("Hello", "World");

words.stream()
    .forEach(System.out::println);

// With index (not built-in, need helper)
words.stream()
    .forEach(word -> System.out.println(word));
```

### collect()

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// To List
List<Integer> list = numbers.stream()
    .collect(Collectors.toList());

// To Set
Set<Integer> set = numbers.stream()
    .collect(Collectors.toSet());

// To Map
Map<Integer, String> map = numbers.stream()
    .collect(Collectors.toMap(
        n -> n,
        n -> "Number: " + n
    ));
```

### reduce()

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Sum
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);
// Result: 15

// Product
int product = numbers.stream()
    .reduce(1, (a, b) -> a * b);
// Result: 120

// Without initial value (returns Optional)
Optional<Integer> sum = numbers.stream()
    .reduce((a, b) -> a + b);
// Result: Optional[15]

// Max
Optional<Integer> max = numbers.stream()
    .reduce((a, b) -> a > b ? a : b);
// Result: Optional[5]
```

### count()

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

long count = numbers.stream()
    .count();
// Result: 5

long evenCount = numbers.stream()
    .filter(n -> n % 2 == 0)
    .count();
// Result: 2
```

### findFirst() and findAny()

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// First element
Optional<Integer> first = numbers.stream()
    .findFirst();
// Result: Optional[1]

// First even number
Optional<Integer> firstEven = numbers.stream()
    .filter(n -> n % 2 == 0)
    .findFirst();
// Result: Optional[2]

// Any element (useful for parallel streams)
Optional<Integer> any = numbers.stream()
    .findAny();
// Result: Optional[1] (or any element)
```

### anyMatch(), allMatch(), noneMatch()

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// At least one even number?
boolean hasEven = numbers.stream()
    .anyMatch(n -> n % 2 == 0);
// Result: true

// All positive?
boolean allPositive = numbers.stream()
    .allMatch(n -> n > 0);
// Result: true

// No negative numbers?
boolean noNegative = numbers.stream()
    .noneMatch(n -> n < 0);
// Result: true
```

### min() and max()

```java
List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 9);

Optional<Integer> min = numbers.stream()
    .min(Integer::compare);
// Result: Optional[1]

Optional<Integer> max = numbers.stream()
    .max(Integer::compare);
// Result: Optional[9]

// With custom comparator
Optional<String> shortest = Arrays.asList("apple", "pie", "banana").stream()
    .min(Comparator.comparingInt(String::length));
// Result: Optional[pie]
```

---

## Collectors

### toList(), toSet(), toCollection()

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

List<Integer> list = numbers.stream()
    .collect(Collectors.toList());

Set<Integer> set = numbers.stream()
    .collect(Collectors.toSet());

TreeSet<Integer> treeSet = numbers.stream()
    .collect(Collectors.toCollection(TreeSet::new));
```

### toMap()

```java
List<String> words = Arrays.asList("apple", "banana", "cherry");

// Key: word, Value: length
Map<String, Integer> wordLengths = words.stream()
    .collect(Collectors.toMap(
        w -> w,
        String::length
    ));
// Result: {apple=5, banana=6, cherry=6}

// With merge function for duplicate keys
Map<String, Integer> map = words.stream()
    .collect(Collectors.toMap(
        w -> w.charAt(0) + "",  // First character as key
        String::length,
        Integer::sum  // Merge function
    ));
```

### groupingBy()

```java
List<String> words = Arrays.asList("apple", "apricot", "banana", "blueberry");

// Group by first character
Map<Character, List<String>> grouped = words.stream()
    .collect(Collectors.groupingBy(w -> w.charAt(0)));
// Result: {a=[apple, apricot], b=[banana, blueberry]}

// Group by length
Map<Integer, List<String>> byLength = words.stream()
    .collect(Collectors.groupingBy(String::length));
// Result: {5=[apple], 6=[banana], 7=[apricot, blueberry]}

// Count by group
Map<Integer, Long> countByLength = words.stream()
    .collect(Collectors.groupingBy(
        String::length,
        Collectors.counting()
    ));
// Result: {5=1, 6=1, 7=2}

// Map values to different type
Map<Character, Set<Integer>> groupedLengths = words.stream()
    .collect(Collectors.groupingBy(
        w -> w.charAt(0),
        Collectors.mapping(String::length, Collectors.toSet())
    ));
// Result: {a=[5, 7], b=[6]}
```

### partitioningBy()

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

// Partition into even and odd
Map<Boolean, List<Integer>> partitioned = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));
// Result: {false=[1, 3, 5], true=[2, 4, 6]}

// Count even and odd
Map<Boolean, Long> counts = numbers.stream()
    .collect(Collectors.partitioningBy(
        n -> n % 2 == 0,
        Collectors.counting()
    ));
// Result: {false=3, true=3}
```

### joining()

```java
List<String> words = Arrays.asList("Hello", "World", "Java");

// Simple join
String result = words.stream()
    .collect(Collectors.joining());
// Result: "HelloWorldJava"

// With delimiter
String result = words.stream()
    .collect(Collectors.joining(", "));
// Result: "Hello, World, Java"

// With prefix and suffix
String result = words.stream()
    .collect(Collectors.joining(", ", "[", "]"));
// Result: "[Hello, World, Java]"
```

### summarizingInt(), summarizingDouble()

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

IntSummaryStatistics stats = numbers.stream()
    .collect(Collectors.summarizingInt(Integer::intValue));

System.out.println(stats.getCount());   // 5
System.out.println(stats.getSum());     // 15
System.out.println(stats.getAverage()); // 3.0
System.out.println(stats.getMin());     // 1
System.out.println(stats.getMax());     // 5
```

---

## Optional Type

### Creating Optional

```java
// Empty Optional
Optional<String> empty = Optional.empty();

// Optional with value
Optional<String> value = Optional.of("Hello");

// Optional that might be empty
Optional<String> nullable = Optional.ofNullable(null);
// Result: Optional.empty

Optional<String> nullable = Optional.ofNullable("Hello");
// Result: Optional[Hello]
```

### Checking and Getting Values

```java
Optional<String> optional = Optional.of("Hello");

// Check if present
if (optional.isPresent()) {
    System.out.println(optional.get());
}

// Get or default
String value = optional.orElse("Default");
// Result: "Hello"

// Get or throw
String value = optional.orElseThrow(() -> 
    new IllegalArgumentException("Value not found")
);

// Get or execute
String value = optional.orElseGet(() -> "Default");
```

### Transforming Optional

```java
Optional<String> optional = Optional.of("Hello");

// map()
Optional<Integer> length = optional.map(String::length);
// Result: Optional[5]

// flatMap()
Optional<String> upper = optional.flatMap(s -> 
    Optional.of(s.toUpperCase())
);
// Result: Optional[HELLO]

// filter()
Optional<String> filtered = optional.filter(s -> s.length() > 3);
// Result: Optional[Hello]
```

### Chaining Operations

```java
Optional<String> optional = Optional.of("hello");

String result = optional
    .filter(s -> s.length() > 3)
    .map(String::toUpperCase)
    .orElse("DEFAULT");
// Result: "HELLO"

// With null handling
Optional<String> nullable = Optional.ofNullable(null);

String result = nullable
    .map(String::toUpperCase)
    .orElse("DEFAULT");
// Result: "DEFAULT"
```

---

## Parallel Streams

### Creating Parallel Streams

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// From collection
Stream<Integer> parallel = numbers.parallelStream();

// From stream
Stream<Integer> parallel = numbers.stream().parallel();

// Back to sequential
Stream<Integer> sequential = parallel.sequential();
```

### How Parallel Streams Work

```
ForkJoinPool (default: number of CPU cores)

Task: Process 1000 elements
├─ Thread 1: Process 250 elements
├─ Thread 2: Process 250 elements
├─ Thread 3: Process 250 elements
└─ Thread 4: Process 250 elements

Results combined at the end
```

### Performance Considerations

```java
// Good for parallel: Large dataset, expensive operation
List<Integer> largeList = IntStream.range(0, 1000000)
    .boxed()
    .collect(Collectors.toList());

long sum = largeList.parallelStream()
    .filter(n -> n % 2 == 0)
    .map(n -> expensiveOperation(n))
    .reduce(0, Integer::sum);

// Bad for parallel: Small dataset, cheap operation
List<Integer> smallList = Arrays.asList(1, 2, 3, 4, 5);

long sum = smallList.parallelStream()  // Overhead > benefit
    .reduce(0, Integer::sum);

// Better:
long sum = smallList.stream()
    .reduce(0, Integer::sum);
```

### Stateless vs Stateful Operations

```java
// ✅ GOOD: Stateless (safe for parallel)
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

List<Integer> result = numbers.parallelStream()
    .filter(n -> n > 2)
    .map(n -> n * 2)
    .collect(Collectors.toList());

// ❌ BAD: Stateful (unsafe for parallel)
List<Integer> result = new ArrayList<>();
numbers.parallelStream()
    .forEach(result::add);  // Race condition!

// ✅ CORRECT: Use collector
List<Integer> result = numbers.parallelStream()
    .collect(Collectors.toList());
```

---

## Performance & Best Practices

### When to Use Streams

```
✅ USE STREAMS:
- Functional transformations (map, filter, reduce)
- Complex data processing pipelines
- Readable, declarative code
- One-time processing

❌ AVOID STREAMS:
- Simple loops (for clarity)
- Modifying external state
- Debugging complex pipelines
- Performance-critical code (sometimes)
```

### Performance Tips

```java
// 1. Avoid unnecessary intermediate operations
// ❌ WRONG
List<Integer> result = numbers.stream()
    .filter(n -> n > 5)
    .filter(n -> n < 10)
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());

// ✅ BETTER
List<Integer> result = numbers.stream()
    .filter(n -> n > 5 && n < 10 && n % 2 == 0)
    .collect(Collectors.toList());

// 2. Use appropriate collectors
// ❌ WRONG
Set<Integer> set = numbers.stream()
    .collect(Collectors.toList())
    .stream()
    .collect(Collectors.toSet());

// ✅ CORRECT
Set<Integer> set = numbers.stream()
    .collect(Collectors.toSet());

// 3. Parallel only when beneficial
// ❌ WRONG
List<Integer> small = Arrays.asList(1, 2, 3);
small.parallelStream().forEach(System.out::println);

// ✅ CORRECT
List<Integer> large = IntStream.range(0, 1000000)
    .boxed()
    .collect(Collectors.toList());
large.parallelStream().forEach(System.out::println);

// 4. Use primitive streams when possible
// ❌ WRONG
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
int sum = numbers.stream()
    .mapToInt(Integer::intValue)
    .sum();

// ✅ BETTER
int[] numbers = {1, 2, 3, 4, 5};
int sum = Arrays.stream(numbers).sum();
```

### Common Patterns

```java
// Pattern 1: Filter and map
List<String> names = people.stream()
    .filter(p -> p.getAge() > 18)
    .map(Person::getName)
    .collect(Collectors.toList());

// Pattern 2: Group and count
Map<String, Long> countByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.counting()
    ));

// Pattern 3: Partition and process
Map<Boolean, List<Integer>> partitioned = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));

// Pattern 4: Reduce to single value
int sum = numbers.stream()
    .reduce(0, Integer::sum);

// Pattern 5: Find with fallback
String result = items.stream()
    .filter(item -> item.matches(criteria))
    .findFirst()
    .orElse("Not found");
```

---

## Key Takeaways

### Streams vs Loops

```
Streams:
- Declarative (what, not how)
- Composable
- Lazy evaluation
- Parallelizable
- Functional style

Loops:
- Imperative (how)
- Simple for basic operations
- Eager evaluation
- Sequential
- Procedural style
```

### Design Principles

1. **Immutability**: Streams don't modify source
2. **Laziness**: Operations deferred until terminal
3. **Composability**: Chain operations naturally
4. **Parallelism**: Potential for parallel processing
5. **Functional**: Use lambdas and method references

### Best Practices

- Use streams for complex transformations
- Keep pipelines readable
- Avoid stateful operations
- Use appropriate collectors
- Profile before parallelizing
- Document stream behavior

---

**Next**: Study QUIZZES.md to test your understanding!
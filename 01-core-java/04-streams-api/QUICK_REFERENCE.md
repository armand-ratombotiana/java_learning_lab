# Streams API - Quick Reference Guide

## Quick Start - Running the Module

```bash
cd 01-core-java/04-streams-api
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

---

## Core Concepts at a Glance

### Stream Creation
```java
// From Collection
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> s1 = list.stream();

// From Array
String[] arr = {"a", "b", "c"};
Stream<String> s2 = Arrays.stream(arr);

// From values
Stream<Integer> s3 = Stream.of(1, 2, 3);

// From range
IntStream s4 = IntStream.range(1, 5);  // [1, 2, 3, 4]

// From builder
Stream<String> s5 = Stream.builder()
    .add("a").add("b").build();
```

---

### Common Intermediate Operations

#### Filter
```java
numbers.stream()
    .filter(n -> n > 5)           // Keep only these
    .collect(Collectors.toList());
```

#### Map
```java
words.stream()
    .map(String::toUpperCase)     // Transform each
    .collect(Collectors.toList());
```

#### FlatMap
```java
lists.stream()
    .flatMap(List::stream)         // Flatten nested
    .collect(Collectors.toList());
```

#### Peek (Debugging)
```java
numbers.stream()
    .peek(n -> System.out.println("Processing: " + n))
    .filter(n -> n > 5)
    .forEach(System.out::println);
```

#### Distinct
```java
numbers.stream()
    .distinct()                    // Remove duplicates
    .collect(Collectors.toList());
```

#### Sorted
```java
numbers.stream()
    .sorted()                      // Natural order
    .collect(Collectors.toList());

numbers.stream()
    .sorted((a, b) -> b - a)       // Custom order
    .collect(Collectors.toList());
```

#### Limit & Skip
```java
numbers.stream()
    .skip(2)                       // Skip first 2
    .limit(3)                      // Take next 3
    .collect(Collectors.toList());
```

---

### Terminal Operations

#### forEach
```java
numbers.stream()
    .forEach(System.out::println);  // Execute action
```

#### collect
```java
// To List
List<String> list = stream.collect(Collectors.toList());

// To Set
Set<String> set = stream.collect(Collectors.toSet());

// To Map
Map<Integer, String> map = stream.collect(
    Collectors.toMap(Object::hashCode, Function.identity())
);

// Join strings
String joined = stream.collect(
    Collectors.joining(", ")
);
```

#### reduce
```java
// Sum
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);

// Product
int product = numbers.stream()
    .reduce(1, Math::multiplyExact);

// Optional result
Optional<Integer> maybe = numbers.stream()
    .reduce((a, b) -> a + b);
```

#### findFirst / findAny
```java
Optional<String> first = stream.findFirst();
Optional<String> any = stream.findAny();
```

#### Matching
```java
boolean allMatch = stream.allMatch(n -> n > 0);
boolean anyMatch = stream.anyMatch(n -> n > 100);
boolean noneMatch = stream.noneMatch(n -> n < 0);
```

#### count / min / max
```java
long count = stream.count();
Optional<Integer> min = stream.min(Integer::compare);
Optional<Integer> max = stream.max(Integer::compare);
```

---

### Collectors Patterns

#### Grouping
```java
Map<String, List<Person>> byDept = people.stream()
    .collect(Collectors.groupingBy(Person::getDepartment));
```

#### Partitioning
```java
Map<Boolean, List<Integer>> evenOdd = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));
```

#### Summarizing
```java
IntSummaryStatistics stats = numbers.stream()
    .collect(Collectors.summarizingInt(n -> n));
// Has: count, sum, min, max, average
```

---

### Optional Patterns

#### Safe Null Handling
```java
// Instead of:
if (person != null) { ... }

// Use:
Optional.ofNullable(person)
    .ifPresent(p -> ...);
```

#### Chaining Operations
```java
var email = customer.stream()
    .map(Customer::getEmail)
    .map(String::toLowerCase)
    .orElse("no-email@example.com");
```

#### Method Chaining
```java
result = getCustomerOpt()
    .flatMap(c -> getAddressOpt(c.getId()))
    .map(Address::getCity)
    .orElse("Unknown");
```

#### Default Values
```java
String val = opt.orElse("default");           // Always evaluated
String val = opt.orElseGet(this::compute);   // Lazy evaluated
String val = opt.orElseThrow();               // Throws if empty
```

---

### Parallel Streams

```java
// Sequential (default)
int sum = numbers.stream()
    .mapToInt(Integer::intValue)
    .sum();

// Parallel
int sum = numbers.parallelStream()
    .mapToInt(Integer::intValue)
    .sum();

// Convert to parallel
int sum = numbers.stream()
    .parallel()
    .mapToInt(Integer::intValue)
    .sum();
```

**When to use Parallel:**
- Large datasets (1M+ elements)
- CPU-intensive operations
- Stateless operations only
- Avoid with ordered operations (limit, skip)

---

## Primitive Streams

### IntStream
```java
IntStream.range(1, 5)        // [1,2,3,4]
IntStream.rangeClosed(1, 5)  // [1,2,3,4,5]
IntStream.of(1, 2, 3)
Arrays.stream(intArray)

IntSummaryStatistics stats = IntStream.range(1, 100)
    .summaryStatistics();
// count, sum, min, max, average
```

### LongStream & DoubleStream
```java
LongStream.range(0, 1_000_000)
DoubleStream.of(1.5, 2.5, 3.5)
```

---

## Performance Tips

### ✅ DO
- Use parallel for large datasets (1M+)
- Filter early to reduce data
- Use primitive streams when possible
- Chain operations instead of multiple passes

### ❌ DON'T
- Use parallel for small datasets
- Create unnecessary intermediate collections
- Use get() without isPresent() check
- Chain too many intermediate operations

---

## Common Patterns

### Filter and Collect
```java
List<String> result = data.stream()
    .filter(predicate)
    .map(transformer)
    .collect(Collectors.toList());
```

### Count Matching
```java
long count = stream.filter(predicate).count();
```

### Find Or Default
```java
String result = stream
    .filter(predicate)
    .findFirst()
    .orElse("default");
```

### Grouped Counting
```java
Map<String, Long> counts = stream
    .collect(Collectors.groupingBy(
        Object::toString,
        Collectors.counting()
    ));
```

### Nested Grouping
```java
Map<String, Map<String, List<Item>>> nested = items.stream()
    .collect(Collectors.groupingBy(
        Item::getCategory,
        Collectors.groupingBy(Item::getSubcategory)
    ));
```

---

## Stream Pipeline Example

```java
people.stream()                                    // Source
    .filter(p -> p.getAge() >= 18)                // Intermediate
    .map(Person::getName)                          // Intermediate  
    .map(String::toUpperCase)                      // Intermediate
    .sorted()                                       // Intermediate
    .distinct()                                     // Intermediate
    .limit(10)                                      // Intermediate
    .forEach(System.out::println);                  // Terminal
```

Each intermediate operation returns a new stream, allowing chaining.
The terminal operation triggers the entire pipeline execution.

---

## Demonstration Classes Quick Index

| Class | Purpose | Key Methods |
|-------|---------|-------------|
| **StreamInterfaceDemo** | Stream basics | Creation, laziness, characteristics |
| **ArrayListStreamDemo** | Collection ops | Filter, map, reduce, partition |
| **PeekOperationsDemo** | Debugging | Inspect, monitor, side effects |
| **FilterOperationsDemo** | Filtering | Complex predicates, negation |
| **MapOperationsDemo** | Transformation | Element conversion |
| **FlatMapOperationsDemo** | Flattening | Nested streams, combinations |
| **CollectOperationsDemo** | Result gathering | Collect patterns |
| **CollectorExamplesDemo** | Collectors | toList, toSet, joining |
| **GroupingByDemo** | Grouping | groupingBy, multi-level |
| **OptionalPatternsDemo** | Null safety | Optional, chaining, patterns |
| **ParallelStreamsDemo** | Concurrency | Parallel execution |
| **Main** | Orchestrator | 8 demonstration sections |

---

## Run Individual Demonstrations

```bash
# Compile
mvn clean compile

# Run specific class with main method
mvn exec:java -Dexec.mainClass="com.learning.Main"

# Run tests
mvn test
```

---

## Troubleshooting

### "Stream has already been operated upon"
```
// WRONG - trying to reuse stream
Stream<String> s = list.stream();
s.count();
s.forEach(System.out::println);  // ERROR!

// RIGHT - create new stream
list.stream().count();
list.stream().forEach(System.out::println);
```

### NullPointerException with Optional
```
// WRONG
Optional.empty().get();  // Throws NoSuchElementException!

// RIGHT
Optional.empty().orElse("default");
Optional.empty().ifPresent(x -> ...);
```

### Parallel Stream Not Faster
```
// Small dataset - no benefit
IntStream.range(0, 100).parallel().sum();

// Large dataset - visible benefit  
IntStream.range(0, 100_000_000).parallel().sum();
```

---

**Module**: 04 - Streams API  
**Version**: 2.0  
**Last Updated**: March 6, 2026  
**Status**: Production Ready ✅

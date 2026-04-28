# ⚠️ Streams API - Edge Cases & Pitfalls

## Table of Contents
1. [Stream Reusability](#stream-reusability)
2. [Lazy Evaluation Surprises](#lazy-evaluation-surprises)
3. [Stateful Operations](#stateful-operations)
4. [Parallel Stream Gotchas](#parallel-stream-gotchas)
5. [Collector Pitfalls](#collector-pitfalls)
6. [Optional Misuse](#optional-misuse)
7. [Performance Traps](#performance-traps)
8. [Memory Issues](#memory-issues)

---

## Stream Reusability

### Pitfall 1: Reusing Stream After Terminal Operation

**Problem**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

Stream<Integer> stream = numbers.stream()
    .filter(n -> n > 2);

// First terminal operation
List<Integer> list = stream.collect(Collectors.toList());
System.out.println(list);  // [3, 4, 5]

// Second terminal operation on same stream
long count = stream.count();  // ❌ IllegalStateException!
// Exception: stream has already been operated upon or closed
```

**Why It Happens**:
- Streams are designed for single-use pipelines
- After terminal operation, stream is closed
- Cannot reuse closed stream

**Solution**:
```java
// ✅ Create new stream for each operation
List<Integer> list = numbers.stream()
    .filter(n -> n > 2)
    .collect(Collectors.toList());

long count = numbers.stream()
    .filter(n -> n > 2)
    .count();

// ✅ Or use intermediate result
List<Integer> filtered = numbers.stream()
    .filter(n -> n > 2)
    .collect(Collectors.toList());

List<Integer> list = filtered;
long count = filtered.size();

// ✅ Or chain operations
long count = numbers.stream()
    .filter(n -> n > 2)
    .count();

List<Integer> list = numbers.stream()
    .filter(n -> n > 2)
    .collect(Collectors.toList());
```

---

## Lazy Evaluation Surprises

### Pitfall 2: Side Effects in Intermediate Operations

**Problem**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// ❌ WRONG: Side effect in filter
Stream<Integer> stream = numbers.stream()
    .filter(n -> {
        System.out.println("Filtering: " + n);  // Side effect!
        return n > 2;
    });

// Nothing printed yet! (Lazy evaluation)
System.out.println("Stream created");

// Now operations execute
stream.forEach(System.out::println);

// Output:
// Stream created
// Filtering: 1
// Filtering: 2
// Filtering: 3
// Filtering: 4
// Filtering: 5
// 3
// 4
// 5
```

**Why It's a Problem**:
- Side effects are unpredictable with lazy evaluation
- Difficult to debug
- Behavior changes with parallel streams
- Violates functional programming principles

**Solution**:
```java
// ✅ CORRECT: Use peek() for debugging
List<Integer> result = numbers.stream()
    .peek(n -> System.out.println("Processing: " + n))
    .filter(n -> n > 2)
    .collect(Collectors.toList());

// ✅ CORRECT: Separate concerns
List<Integer> filtered = numbers.stream()
    .filter(n -> n > 2)
    .collect(Collectors.toList());

filtered.forEach(n -> System.out.println("Result: " + n));

// ✅ CORRECT: Use forEach for side effects
numbers.stream()
    .filter(n -> n > 2)
    .forEach(n -> System.out.println("Result: " + n));
```

---

### Pitfall 3: Modifying Source Collection During Stream

**Problem**:
```java
List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

// ❌ WRONG: Modifying source during stream
List<Integer> result = numbers.stream()
    .filter(n -> {
        if (n == 3) {
            numbers.remove(Integer.valueOf(3));  // Modifying source!
        }
        return true;
    })
    .collect(Collectors.toList());

// Unpredictable behavior!
// ConcurrentModificationException or incorrect results
```

**Why It's a Problem**:
- Modifying source during iteration is undefined behavior
- Can cause ConcurrentModificationException
- Results are unpredictable
- Especially dangerous with parallel streams

**Solution**:
```java
// ✅ CORRECT: Collect first, then modify
List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

List<Integer> toRemove = numbers.stream()
    .filter(n -> n == 3)
    .collect(Collectors.toList());

numbers.removeAll(toRemove);

// ✅ CORRECT: Filter and collect
List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

List<Integer> result = numbers.stream()
    .filter(n -> n != 3)
    .collect(Collectors.toList());

numbers.clear();
numbers.addAll(result);

// ✅ CORRECT: Use immutable approach
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

List<Integer> result = numbers.stream()
    .filter(n -> n != 3)
    .collect(Collectors.toList());
```

---

## Stateful Operations

### Pitfall 4: Stateful Lambda in Parallel Stream

**Problem**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// ❌ WRONG: Stateful operation in parallel
Set<Integer> seen = new HashSet<>();
List<Integer> unique = numbers.parallelStream()
    .filter(n -> seen.add(n))  // Race condition!
    .collect(Collectors.toList());

// Result is unpredictable due to race conditions
// Multiple threads accessing seen simultaneously
```

**Why It's a Problem**:
- Multiple threads access shared state simultaneously
- No synchronization
- Results are unpredictable
- Data corruption possible

**Solution**:
```java
// ✅ CORRECT: Use distinct()
List<Integer> unique = numbers.parallelStream()
    .distinct()
    .collect(Collectors.toList());

// ✅ CORRECT: Use sequential stream
Set<Integer> seen = new HashSet<>();
List<Integer> unique = numbers.stream()
    .filter(n -> seen.add(n))
    .collect(Collectors.toList());

// ✅ CORRECT: Use collector
List<Integer> unique = numbers.parallelStream()
    .collect(Collectors.toCollection(LinkedHashSet::new))
    .stream()
    .collect(Collectors.toList());
```

---

### Pitfall 5: Mutable Accumulator in reduce()

**Problem**:
```java
List<String> words = Arrays.asList("hello", "world");

// ❌ WRONG: Mutable accumulator
StringBuilder result = words.stream()
    .reduce(
        new StringBuilder(),
        (sb, word) -> sb.append(word),  // Mutating accumulator
        (sb1, sb2) -> sb1.append(sb2.toString())
    );

// Works in sequential, but fails in parallel!
// Multiple threads share same StringBuilder

// Parallel version:
StringBuilder result = words.parallelStream()
    .reduce(
        new StringBuilder(),
        (sb, word) -> sb.append(word),  // Race condition!
        (sb1, sb2) -> sb1.append(sb2.toString())
    );
```

**Why It's a Problem**:
- Mutable accumulator not thread-safe
- Works in sequential by accident
- Fails in parallel with race conditions
- Violates functional programming principles

**Solution**:
```java
// ✅ CORRECT: Use immutable approach
String result = words.stream()
    .reduce(
        "",
        (s, word) -> s + word,
        (s1, s2) -> s1 + s2
    );

// ✅ CORRECT: Use collector
String result = words.stream()
    .collect(Collectors.joining());

// ✅ CORRECT: Use StringBuilder with sequential
StringBuilder result = new StringBuilder();
words.forEach(result::append);

// ✅ CORRECT: Use collector for parallel
String result = words.parallelStream()
    .collect(Collectors.joining());
```

---

## Parallel Stream Gotchas

### Pitfall 6: Parallel Stream Overhead

**Problem**:
```java
// ❌ WRONG: Parallel overhead > benefit
List<Integer> small = Arrays.asList(1, 2, 3, 4, 5);

long sum = small.parallelStream()
    .reduce(0, Integer::sum);

// Overhead:
// - Thread creation
// - Context switching
// - Synchronization
// - Result combining
// Total time > sequential!
```

**Why It's a Problem**:
- Parallel processing has overhead
- Only beneficial for large datasets
- Small datasets: overhead > benefit
- Wasted resources

**Solution**:
```java
// ✅ CORRECT: Use sequential for small datasets
List<Integer> small = Arrays.asList(1, 2, 3, 4, 5);
long sum = small.stream()
    .reduce(0, Integer::sum);

// ✅ CORRECT: Use parallel for large datasets
List<Integer> large = IntStream.range(0, 1000000)
    .boxed()
    .collect(Collectors.toList());

long sum = large.parallelStream()
    .filter(n -> n % 2 == 0)
    .map(n -> expensiveOperation(n))
    .reduce(0, Integer::sum);

// Rule of thumb:
// - Dataset < 1000: Use sequential
// - Dataset > 10000: Consider parallel
// - Expensive operation: Parallel beneficial
// - Cheap operation: Sequential better
```

---

### Pitfall 7: Unordered Parallel Streams

**Problem**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Parallel stream doesn't guarantee order
List<Integer> result = numbers.parallelStream()
    .map(n -> n * 2)
    .collect(Collectors.toList());

// Result might be: [2, 4, 6, 8, 10]
// Or: [6, 8, 10, 2, 4]
// Or any other order!

// Especially with unordered collections
Set<Integer> set = new HashSet<>(numbers);
List<Integer> result = set.parallelStream()
    .map(n -> n * 2)
    .collect(Collectors.toList());

// Order completely unpredictable
```

**Why It's a Problem**:
- Order not guaranteed in parallel
- Difficult to debug
- Tests might pass/fail randomly
- Unexpected behavior

**Solution**:
```java
// ✅ CORRECT: Use sequential if order matters
List<Integer> result = numbers.stream()
    .map(n -> n * 2)
    .collect(Collectors.toList());

// ✅ CORRECT: Use unordered() for performance
List<Integer> result = numbers.parallelStream()
    .unordered()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());

// ✅ CORRECT: Accept unordered result
Set<Integer> result = numbers.parallelStream()
    .map(n -> n * 2)
    .collect(Collectors.toSet());

// ✅ CORRECT: Use LinkedHashSet for order
List<Integer> result = numbers.parallelStream()
    .map(n -> n * 2)
    .collect(Collectors.toCollection(LinkedHashSet::new))
    .stream()
    .collect(Collectors.toList());
```

---

## Collector Pitfalls

### Pitfall 8: Incorrect toMap() with Duplicate Keys

**Problem**:
```java
List<String> words = Arrays.asList("apple", "apricot", "banana", "blueberry");

// ❌ WRONG: Duplicate keys
Map<Character, String> map = words.stream()
    .collect(Collectors.toMap(
        w -> w.charAt(0),  // Key: first character
        w -> w             // Value: word
    ));

// Exception: IllegalStateException: Duplicate key 'a'
// Both "apple" and "apricot" have key 'a'
```

**Why It's a Problem**:
- toMap() doesn't handle duplicate keys by default
- Throws exception on duplicate
- Loses data

**Solution**:
```java
// ✅ CORRECT: Provide merge function
Map<Character, String> map = words.stream()
    .collect(Collectors.toMap(
        w -> w.charAt(0),
        w -> w,
        (w1, w2) -> w1 + ", " + w2  // Merge function
    ));
// Result: {a=apple, apricot, b=banana, blueberry}

// ✅ CORRECT: Use groupingBy() instead
Map<Character, List<String>> map = words.stream()
    .collect(Collectors.groupingBy(w -> w.charAt(0)));
// Result: {a=[apple, apricot], b=[banana, blueberry]}

// ✅ CORRECT: Filter to ensure uniqueness
Map<Character, String> map = words.stream()
    .filter(w -> w.length() > 5)  // Filter first
    .collect(Collectors.toMap(
        w -> w.charAt(0),
        w -> w
    ));
```

---

### Pitfall 9: groupingBy() with Null Keys

**Problem**:
```java
List<String> words = Arrays.asList("apple", null, "banana", null);

// ❌ WRONG: Null values in stream
Map<Character, List<String>> map = words.stream()
    .collect(Collectors.groupingBy(w -> w.charAt(0)));

// NullPointerException!
// null.charAt(0) throws exception
```

**Why It's a Problem**:
- Null values cause NullPointerException
- Difficult to debug
- Unexpected behavior

**Solution**:
```java
// ✅ CORRECT: Filter nulls first
List<String> words = Arrays.asList("apple", null, "banana", null);

Map<Character, List<String>> map = words.stream()
    .filter(Objects::nonNull)
    .collect(Collectors.groupingBy(w -> w.charAt(0)));

// ✅ CORRECT: Handle nulls in grouping key
Map<String, List<String>> map = words.stream()
    .collect(Collectors.groupingBy(
        w -> w == null ? "null" : w.substring(0, 1)
    ));

// ✅ CORRECT: Use Optional
Map<Character, List<String>> map = words.stream()
    .flatMap(w -> w == null ? Stream.empty() : Stream.of(w))
    .collect(Collectors.groupingBy(w -> w.charAt(0)));
```

---

### Pitfall 10: Collector Mutability Issues

**Problem**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// ❌ WRONG: Mutable collector result
List<Integer> result = numbers.stream()
    .collect(Collectors.toList());

result.add(999);  // Modifying result

// Original list unchanged, but result is mutable
// Can cause issues if shared between threads
```

**Why It's a Problem**:
- Mutable results can be modified unexpectedly
- Thread-safety issues
- Difficult to debug

**Solution**:
```java
// ✅ CORRECT: Use immutable collection
List<Integer> result = numbers.stream()
    .collect(Collectors.toUnmodifiableList());

// result.add(999);  // UnsupportedOperationException

// ✅ CORRECT: Use Collections.unmodifiableList()
List<Integer> result = Collections.unmodifiableList(
    numbers.stream()
        .collect(Collectors.toList())
);

// ✅ CORRECT: Use ImmutableList (Guava)
List<Integer> result = ImmutableList.copyOf(
    numbers.stream()
        .collect(Collectors.toList())
);

// ✅ CORRECT: Accept mutability if needed
List<Integer> result = numbers.stream()
    .collect(Collectors.toList());
// Document that result is mutable
```

---

## Optional Misuse

### Pitfall 11: Using get() Without Checking

**Problem**:
```java
Optional<String> optional = Optional.empty();

// ❌ WRONG: get() without checking
String value = optional.get();  // NoSuchElementException!
```

**Why It's a Problem**:
- get() throws exception if empty
- Defeats purpose of Optional
- Unsafe

**Solution**:
```java
Optional<String> optional = Optional.empty();

// ✅ CORRECT: Check before get()
if (optional.isPresent()) {
    String value = optional.get();
}

// ✅ CORRECT: Use orElse()
String value = optional.orElse("default");

// ✅ CORRECT: Use orElseGet()
String value = optional.orElseGet(() -> "default");

// ✅ CORRECT: Use orElseThrow()
String value = optional.orElseThrow(() -> 
    new IllegalArgumentException("Value not found")
);

// ✅ CORRECT: Use ifPresent()
optional.ifPresent(value -> System.out.println(value));

// ✅ CORRECT: Use ifPresentOrElse()
optional.ifPresentOrElse(
    value -> System.out.println(value),
    () -> System.out.println("Not found")
);
```

---

### Pitfall 12: Chaining Operations on Empty Optional

**Problem**:
```java
Optional<String> optional = Optional.empty();

// ❌ WRONG: Assuming operations execute
Optional<Integer> length = optional
    .map(String::length)
    .map(l -> l * 2);

// Operations don't execute, but no error
// Confusing behavior

// Later:
int value = length.get();  // NoSuchElementException!
```

**Why It's a Problem**:
- Operations silently skipped on empty
- Difficult to debug
- Unexpected behavior

**Solution**:
```java
Optional<String> optional = Optional.empty();

// ✅ CORRECT: Check result
Optional<Integer> length = optional
    .map(String::length)
    .map(l -> l * 2);

if (length.isPresent()) {
    int value = length.get();
}

// ✅ CORRECT: Use orElse()
int value = optional
    .map(String::length)
    .map(l -> l * 2)
    .orElse(0);

// ✅ CORRECT: Use filter()
Optional<String> result = optional
    .filter(s -> s.length() > 5)
    .map(String::toUpperCase);

// ✅ CORRECT: Use flatMap()
Optional<Integer> result = optional
    .flatMap(s -> Optional.of(s.length()));
```

---

## Performance Traps

### Pitfall 13: Unnecessary Intermediate Collections

**Problem**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// ❌ WRONG: Multiple intermediate collections
List<Integer> result = numbers.stream()
    .filter(n -> n > 2)
    .collect(Collectors.toList())  // Intermediate collection
    .stream()
    .map(n -> n * 2)
    .collect(Collectors.toList());  // Final collection

// Inefficient: Creates 2 lists
```

**Why It's a Problem**:
- Extra memory allocation
- Extra garbage collection
- Slower performance
- Unnecessary complexity

**Solution**:
```java
// ✅ CORRECT: Chain operations
List<Integer> result = numbers.stream()
    .filter(n -> n > 2)
    .map(n -> n * 2)
    .collect(Collectors.toList());

// ✅ CORRECT: Use intermediate result only if needed
List<Integer> filtered = numbers.stream()
    .filter(n -> n > 2)
    .collect(Collectors.toList());

// Use filtered for multiple operations
long count = filtered.size();
int sum = filtered.stream().mapToInt(Integer::intValue).sum();
```

---

### Pitfall 14: Expensive Operations in filter()

**Problem**:
```java
List<String> words = Arrays.asList("apple", "banana", "cherry");

// ❌ WRONG: Expensive operation in filter
List<String> result = words.stream()
    .filter(w -> expensiveValidation(w))  // Called for every element
    .map(String::toUpperCase)
    .collect(Collectors.toList());

// If expensiveValidation() is slow, entire pipeline is slow
```

**Why It's a Problem**:
- Expensive operation called for every element
- Even if filtered out later
- Performance degradation

**Solution**:
```java
// ✅ CORRECT: Cheap filter first
List<String> result = words.stream()
    .filter(w -> w.length() > 3)  // Cheap filter first
    .filter(w -> expensiveValidation(w))  // Expensive filter second
    .map(String::toUpperCase)
    .collect(Collectors.toList());

// ✅ CORRECT: Cache expensive result
Map<String, Boolean> validationCache = new HashMap<>();
List<String> result = words.stream()
    .filter(w -> validationCache.computeIfAbsent(w, this::expensiveValidation))
    .map(String::toUpperCase)
    .collect(Collectors.toList());

// ✅ CORRECT: Use short-circuit operations
Optional<String> result = words.stream()
    .filter(w -> expensiveValidation(w))
    .findFirst();  // Stops after first match
```

---

## Memory Issues

### Pitfall 15: Large Intermediate Collections

**Problem**:
```java
// ❌ WRONG: Large intermediate collection
List<Integer> largeList = IntStream.range(0, 1000000)
    .boxed()
    .collect(Collectors.toList());  // 1 million objects in memory

List<Integer> result = largeList.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());

// Memory usage: 2 lists × 1 million integers = high memory
```

**Why It's a Problem**:
- Large intermediate collections consume memory
- Garbage collection overhead
- Potential OutOfMemoryError

**Solution**:
```java
// ✅ CORRECT: Stream directly without intermediate collection
List<Integer> result = IntStream.range(0, 1000000)
    .filter(n -> n % 2 == 0)
    .boxed()
    .collect(Collectors.toList());

// ✅ CORRECT: Use primitive streams
IntStream.range(0, 1000000)
    .filter(n -> n % 2 == 0)
    .forEach(System.out::println);

// ✅ CORRECT: Process in batches
int batchSize = 10000;
for (int i = 0; i < 1000000; i += batchSize) {
    List<Integer> batch = IntStream.range(i, Math.min(i + batchSize, 1000000))
        .boxed()
        .collect(Collectors.toList());
    processBatch(batch);
}
```

---

### Pitfall 16: Infinite Streams Without Limit

**Problem**:
```java
// ❌ WRONG: Infinite stream without limit
Stream<Integer> infinite = Stream.iterate(0, n -> n + 1);
infinite.forEach(System.out::println);  // Never stops!

// Program hangs, consuming memory
```

**Why It's a Problem**:
- Infinite loop
- Program hangs
- Memory exhaustion
- Difficult to debug

**Solution**:
```java
// ✅ CORRECT: Use limit()
Stream.iterate(0, n -> n + 1)
    .limit(10)
    .forEach(System.out::println);

// ✅ CORRECT: Use takeWhile() (Java 9+)
Stream.iterate(0, n -> n + 1)
    .takeWhile(n -> n < 10)
    .forEach(System.out::println);

// ✅ CORRECT: Use predicate in iterate() (Java 9+)
Stream.iterate(0, n -> n < 10, n -> n + 1)
    .forEach(System.out::println);

// ✅ CORRECT: Use generate() with limit
Stream.generate(Math::random)
    .limit(10)
    .forEach(System.out::println);
```

---

### Pitfall 17: Unclosed File Streams

**Problem**:
```java
// ❌ WRONG: File stream not closed
Stream<String> lines = Files.lines(Paths.get("file.txt"));
List<String> allLines = lines.collect(Collectors.toList());

// Stream not closed, file handle leaked
// Can cause "too many open files" error
```

**Why It's a Problem**:
- File handle not released
- Resource leak
- "Too many open files" error
- System resource exhaustion

**Solution**:
```java
// ✅ CORRECT: Use try-with-resources
try (Stream<String> lines = Files.lines(Paths.get("file.txt"))) {
    List<String> allLines = lines.collect(Collectors.toList());
}

// ✅ CORRECT: Close explicitly
Stream<String> lines = Files.lines(Paths.get("file.txt"));
try {
    List<String> allLines = lines.collect(Collectors.toList());
} finally {
    lines.close();
}

// ✅ CORRECT: Use Files.readAllLines()
List<String> allLines = Files.readAllLines(Paths.get("file.txt"));
```

---

## Summary of Common Pitfalls

| Pitfall | Problem | Solution |
|---------|---------|----------|
| Reusing stream | IllegalStateException | Create new stream |
| Side effects | Unpredictable behavior | Use peek() or forEach() |
| Modifying source | ConcurrentModificationException | Collect first, then modify |
| Stateful parallel | Race conditions | Use distinct() or collector |
| Mutable reduce() | Thread-safety issues | Use immutable approach |
| Parallel overhead | Slower than sequential | Use parallel for large datasets |
| Duplicate keys | IllegalStateException | Provide merge function |
| Null values | NullPointerException | Filter nulls first |
| get() without check | NoSuchElementException | Use orElse() or check first |
| Intermediate collections | Memory waste | Chain operations |
| Expensive filter | Performance degradation | Filter cheap operations first |
| Infinite streams | Program hangs | Use limit() or takeWhile() |
| Unclosed files | Resource leak | Use try-with-resources |

---

**Next**: Practice with executable code examples in the main source files!
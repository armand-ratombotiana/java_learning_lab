# 📝 Streams API - Quizzes

## Beginner Level (5 Questions)

### Q1: Stream Creation
**Question**: Which of the following creates a stream from a list?

**Options**:
A) `list.stream()`  
B) `Stream.of(list)`  
C) `new Stream(list)`  
D) `list.toStream()`  

**Answer**: **A) `list.stream()`**

**Explanation**:
```java
List<String> list = Arrays.asList("A", "B", "C");

// ✅ CORRECT: Use stream() method
Stream<String> stream = list.stream();

// ❌ WRONG: Stream.of() takes elements, not collection
Stream<String> stream = Stream.of("A", "B", "C");  // Elements, not list

// ❌ WRONG: No constructor
new Stream(list);  // Doesn't exist

// ❌ WRONG: No toStream() method
list.toStream();  // Doesn't exist
```

---

### Q2: Lazy Evaluation
**Question**: When are stream operations executed?

**Options**:
A) Immediately when called  
B) When the terminal operation is called  
C) When forEach() is called  
D) Never (streams are lazy)  

**Answer**: **B) When the terminal operation is called**

**Explanation**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Intermediate operations (lazy - not executed yet)
Stream<Integer> stream = numbers.stream()
    .filter(n -> {
        System.out.println("Filtering: " + n);
        return n > 2;
    })
    .map(n -> {
        System.out.println("Mapping: " + n);
        return n * 2;
    });

// Nothing printed yet!

// Terminal operation (executes the pipeline)
stream.forEach(System.out::println);

// Now output appears:
// Filtering: 1
// Filtering: 2
// Filtering: 3
// Mapping: 3
// 6
// ...
```

---

### Q3: filter() vs map()
**Question**: What's the difference between filter() and map()?

**Options**:
A) filter() removes elements, map() transforms them  
B) filter() transforms, map() removes  
C) They're the same  
D) filter() is for collections, map() is for streams  

**Answer**: **A) filter() removes elements, map() transforms them**

**Explanation**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// filter(): Keep or remove elements (same type)
List<Integer> filtered = numbers.stream()
    .filter(n -> n > 2)  // Keep only n > 2
    .collect(Collectors.toList());
// Result: [3, 4, 5] (removed 1, 2)

// map(): Transform elements (can change type)
List<Integer> mapped = numbers.stream()
    .map(n -> n * 2)  // Transform each element
    .collect(Collectors.toList());
// Result: [2, 4, 6, 8, 10] (all elements, transformed)

// map() with type change
List<String> strings = numbers.stream()
    .map(String::valueOf)  // Transform Integer to String
    .collect(Collectors.toList());
// Result: ["1", "2", "3", "4", "5"]
```

---

### Q4: Terminal Operations
**Question**: Which is a terminal operation?

**Options**:
A) filter()  
B) map()  
C) forEach()  
D) flatMap()  

**Answer**: **C) forEach()**

**Explanation**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Intermediate operations (return Stream)
Stream<Integer> stream1 = numbers.stream().filter(n -> n > 2);
Stream<Integer> stream2 = numbers.stream().map(n -> n * 2);
Stream<Integer> stream3 = numbers.stream().flatMap(n -> Stream.of(n));

// Terminal operations (return result, not Stream)
numbers.stream().forEach(System.out::println);  // void
long count = numbers.stream().count();          // long
List<Integer> list = numbers.stream()
    .collect(Collectors.toList());              // List

// Other terminal operations:
Optional<Integer> first = numbers.stream().findFirst();
boolean hasEven = numbers.stream().anyMatch(n -> n % 2 == 0);
int sum = numbers.stream().reduce(0, Integer::sum);
```

---

### Q5: Optional Type
**Question**: What does Optional represent?

**Options**:
A) A value that might be null  
B) A value that is always present  
C) A collection of values  
D) A stream operation  

**Answer**: **A) A value that might be null**

**Explanation**:
```java
// Optional: Container for value that might be null

// Empty Optional
Optional<String> empty = Optional.empty();
Optional<String> nullable = Optional.ofNullable(null);

// Optional with value
Optional<String> value = Optional.of("Hello");

// Checking and getting
if (optional.isPresent()) {
    String val = optional.get();
}

// Safe access
String val = optional.orElse("Default");
String val = optional.orElseGet(() -> "Default");

// Transforming
Optional<Integer> length = optional.map(String::length);

// Filtering
Optional<String> filtered = optional.filter(s -> s.length() > 3);
```

---

## Intermediate Level (5 Questions)

### Q6: flatMap() vs map()
**Question**: When should you use flatMap() instead of map()?

**Options**:
A) When you want to flatten nested streams  
B) When you want to transform elements  
C) When you want to filter elements  
D) They're the same  

**Answer**: **A) When you want to flatten nested streams**

**Explanation**:
```java
// map(): One-to-one transformation
List<String> words = Arrays.asList("hello", "world");
List<Integer> lengths = words.stream()
    .map(String::length)
    .collect(Collectors.toList());
// Result: [5, 5]

// flatMap(): One-to-many transformation (flattens)
List<String> sentences = Arrays.asList("Hello World", "Java Streams");
List<String> allWords = sentences.stream()
    .flatMap(s -> Arrays.stream(s.split(" ")))
    .collect(Collectors.toList());
// Result: [Hello, World, Java, Streams]

// Nested lists
List<List<Integer>> nested = Arrays.asList(
    Arrays.asList(1, 2),
    Arrays.asList(3, 4)
);

// map() keeps nesting
List<List<Integer>> mapped = nested.stream()
    .map(list -> list)
    .collect(Collectors.toList());
// Result: [[1, 2], [3, 4]]

// flatMap() flattens
List<Integer> flattened = nested.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList());
// Result: [1, 2, 3, 4]
```

---

### Q7: Collectors.groupingBy()
**Question**: What does groupingBy() return?

**Options**:
A) A list of grouped elements  
B) A map with groups as values  
C) A set of unique elements  
D) A stream of groups  

**Answer**: **B) A map with groups as values**

**Explanation**:
```java
List<String> words = Arrays.asList("apple", "apricot", "banana", "blueberry");

// groupingBy() returns Map<Key, List<Value>>
Map<Character, List<String>> grouped = words.stream()
    .collect(Collectors.groupingBy(w -> w.charAt(0)));

// Result:
// {
//   'a': [apple, apricot],
//   'b': [banana, blueberry]
// }

// Access groups
List<String> aWords = grouped.get('a');  // [apple, apricot]

// Count by group
Map<Character, Long> counts = words.stream()
    .collect(Collectors.groupingBy(
        w -> w.charAt(0),
        Collectors.counting()
    ));
// Result: {'a': 2, 'b': 2}

// Map values to different type
Map<Character, Set<Integer>> lengths = words.stream()
    .collect(Collectors.groupingBy(
        w -> w.charAt(0),
        Collectors.mapping(String::length, Collectors.toSet())
    ));
// Result: {'a': {5, 7}, 'b': {6}}
```

---

### Q8: reduce() Operation
**Question**: What does reduce() do?

**Options**:
A) Removes elements from stream  
B) Combines elements into single value  
C) Filters elements  
D) Transforms elements  

**Answer**: **B) Combines elements into single value**

**Explanation**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// reduce() with initial value
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);
// Result: 15 (0 + 1 + 2 + 3 + 4 + 5)

// reduce() without initial value (returns Optional)
Optional<Integer> sum = numbers.stream()
    .reduce((a, b) -> a + b);
// Result: Optional[15]

// Product
int product = numbers.stream()
    .reduce(1, (a, b) -> a * b);
// Result: 120 (1 * 1 * 2 * 3 * 4 * 5)

// Max
Optional<Integer> max = numbers.stream()
    .reduce((a, b) -> a > b ? a : b);
// Result: Optional[5]

// Concatenation
String result = Arrays.asList("a", "b", "c").stream()
    .reduce("", (a, b) -> a + b);
// Result: "abc"
```

---

### Q9: Parallel Streams
**Question**: When should you use parallelStream()?

**Options**:
A) Always for better performance  
B) For large datasets with expensive operations  
C) For small datasets  
D) Never (sequential is always better)  

**Answer**: **B) For large datasets with expensive operations**

**Explanation**:
```java
// ✅ GOOD: Large dataset, expensive operation
List<Integer> largeList = IntStream.range(0, 1000000)
    .boxed()
    .collect(Collectors.toList());

long sum = largeList.parallelStream()
    .filter(n -> n % 2 == 0)
    .map(n -> expensiveOperation(n))
    .reduce(0, Integer::sum);

// ❌ BAD: Small dataset, cheap operation
List<Integer> smallList = Arrays.asList(1, 2, 3, 4, 5);

long sum = smallList.parallelStream()  // Overhead > benefit
    .reduce(0, Integer::sum);

// Better:
long sum = smallList.stream()
    .reduce(0, Integer::sum);

// Overhead of parallelStream:
// - Thread creation
// - Context switching
// - Synchronization
// - Result combining

// Only worth it if:
// - Dataset is large (1000+ elements)
// - Operation is expensive (CPU-intensive)
// - Benefit > overhead
```

---

### Q10: Short-Circuit Operations
**Question**: What is a short-circuit operation?

**Options**:
A) An operation that fails  
B) An operation that stops early when condition met  
C) An operation that skips elements  
D) An operation that removes elements  

**Answer**: **B) An operation that stops early when condition met**

**Explanation**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Short-circuit: Stops when condition met
Optional<Integer> first = numbers.stream()
    .filter(n -> {
        System.out.println("Checking: " + n);
        return n > 5;
    })
    .findFirst();

// Output:
// Checking: 1
// Checking: 2
// Checking: 3
// Checking: 4
// Checking: 5
// Checking: 6
// (Stops here! Found first match)

// Short-circuit operations:
// - findFirst()
// - findAny()
// - anyMatch()
// - allMatch()
// - noneMatch()
// - limit()

// Non-short-circuit operations:
// - filter()
// - map()
// - forEach()
// - collect()
// - reduce()

// Benefit: Lazy evaluation + short-circuit = efficiency
List<String> words = Arrays.asList("a", "bb", "ccc", "dddd", "eeeee");

boolean hasLong = words.stream()
    .anyMatch(w -> w.length() > 3);  // Stops at "dddd"
// Result: true (doesn't check "eeeee")
```

---

## Advanced Level (5 Questions)

### Q11: Custom Collectors
**Question**: How do you create a custom collector?

**Options**:
A) Extend Collector interface  
B) Use Collector.of()  
C) Implement Collector class  
D) Use Collectors.custom()  

**Answer**: **B) Use Collector.of()**

**Explanation**:
```java
// Custom collector: Collect to comma-separated string
Collector<String, ?, String> csvCollector = Collector.of(
    StringBuilder::new,           // Supplier: Create container
    (sb, s) -> {                  // Accumulator: Add element
        if (sb.length() > 0) sb.append(", ");
        sb.append(s);
    },
    (sb1, sb2) -> {               // Combiner: Merge containers
        if (sb1.length() > 0) sb1.append(", ");
        sb1.append(sb2);
        return sb1;
    },
    StringBuilder::toString       // Finisher: Convert to result
);

List<String> words = Arrays.asList("apple", "banana", "cherry");
String result = words.stream()
    .collect(csvCollector);
// Result: "apple, banana, cherry"

// Simpler: Use Collectors.joining()
String result = words.stream()
    .collect(Collectors.joining(", "));
// Result: "apple, banana, cherry"

// Custom collector: Collect to custom object
Collector<Integer, ?, IntSummary> summaryCollector = Collector.of(
    IntSummary::new,
    IntSummary::add,
    IntSummary::merge
);

class IntSummary {
    int sum = 0;
    int count = 0;
    
    void add(int n) {
        sum += n;
        count++;
    }
    
    IntSummary merge(IntSummary other) {
        this.sum += other.sum;
        this.count += other.count;
        return this;
    }
}
```

---

### Q12: Stateful Operations
**Question**: Why are stateful operations dangerous in parallel streams?

**Options**:
A) They're slower  
B) They can cause race conditions  
C) They don't work with lambdas  
D) They require more memory  

**Answer**: **B) They can cause race conditions**

**Explanation**:
```java
// ❌ WRONG: Stateful operation (race condition)
List<Integer> result = new ArrayList<>();
numbers.parallelStream()
    .forEach(result::add);  // Multiple threads adding simultaneously!

// Race condition: Two threads might try to add at same time
// Result might be incomplete or corrupted

// ✅ CORRECT: Use collector (thread-safe)
List<Integer> result = numbers.parallelStream()
    .collect(Collectors.toList());

// ❌ WRONG: Stateful filter
Set<Integer> seen = new HashSet<>();
List<Integer> unique = numbers.parallelStream()
    .filter(n -> seen.add(n))  // Race condition!
    .collect(Collectors.toList());

// ✅ CORRECT: Use distinct()
List<Integer> unique = numbers.parallelStream()
    .distinct()
    .collect(Collectors.toList());

// Stateless operations (safe for parallel):
// - filter()
// - map()
// - flatMap()
// - distinct()
// - sorted()

// Stateful operations (unsafe for parallel):
// - Modifying external collections
// - Using shared mutable state
// - Relying on order
```

---

### Q13: Stream Performance
**Question**: Which is most efficient?

**Options**:
A) `stream().filter().map().collect()`  
B) `stream().map().filter().collect()`  
C) `stream().filter().filter().map().collect()`  
D) All the same  

**Answer**: **A) `stream().filter().map().collect()`**

**Explanation**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Option A: Filter first (reduces elements for map)
List<Integer> result = numbers.stream()
    .filter(n -> n > 5)      // 5 elements pass
    .map(n -> n * 2)         // Map only 5 elements
    .collect(Collectors.toList());

// Option B: Map first (maps all elements)
List<Integer> result = numbers.stream()
    .map(n -> n * 2)         // Map all 10 elements
    .filter(n -> n > 10)     // Filter 10 elements
    .collect(Collectors.toList());

// Option A is more efficient:
// - Filter reduces elements early
// - Map operates on fewer elements
// - Less work overall

// Option C: Multiple filters (redundant)
List<Integer> result = numbers.stream()
    .filter(n -> n > 5)      // 5 elements pass
    .filter(n -> n < 9)      // 3 elements pass
    .map(n -> n * 2)         // Map 3 elements
    .collect(Collectors.toList());

// Better: Combine filters
List<Integer> result = numbers.stream()
    .filter(n -> n > 5 && n < 9)  // Single filter
    .map(n -> n * 2)
    .collect(Collectors.toList());

// Performance principle:
// 1. Filter early to reduce elements
// 2. Combine filters when possible
// 3. Map after filtering
// 4. Use appropriate collectors
```

---

### Q14: Optional Chaining
**Question**: What's the result of this code?

```java
Optional<String> optional = Optional.of("hello");
String result = optional
    .filter(s -> s.length() > 10)
    .map(String::toUpperCase)
    .orElse("DEFAULT");
```

**Options**:
A) "HELLO"  
B) "hello"  
C) "DEFAULT"  
D) NullPointerException  

**Answer**: **C) "DEFAULT"**

**Explanation**:
```java
Optional<String> optional = Optional.of("hello");

String result = optional
    .filter(s -> s.length() > 10)  // "hello".length() = 5, not > 10
                                    // Filter fails, Optional becomes empty
    .map(String::toUpperCase)       // Not executed (Optional is empty)
    .orElse("DEFAULT");             // Returns "DEFAULT"

// Result: "DEFAULT"

// Another example:
Optional<String> optional = Optional.of("hello world");

String result = optional
    .filter(s -> s.length() > 10)   // "hello world".length() = 11, > 10
                                     // Filter passes, Optional still has value
    .map(String::toUpperCase)        // Executed: "HELLO WORLD"
    .orElse("DEFAULT");              // Not needed

// Result: "HELLO WORLD"

// Chaining pattern:
Optional<String> optional = Optional.ofNullable(getValue());

String result = optional
    .filter(s -> !s.isEmpty())
    .map(String::trim)
    .map(String::toUpperCase)
    .orElse("EMPTY");
```

---

### Q15: Stream Reusability
**Question**: Can you reuse a stream after terminal operation?

**Options**:
A) Yes, streams are reusable  
B) No, streams can only be used once  
C) Yes, if you call stream() again  
D) Depends on the operation  

**Answer**: **B) No, streams can only be used once**

**Explanation**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

Stream<Integer> stream = numbers.stream()
    .filter(n -> n > 2);

// First terminal operation
List<Integer> list = stream.collect(Collectors.toList());
// Result: [3, 4, 5]

// Second terminal operation on same stream
long count = stream.count();  // IllegalStateException!
// Stream has already been operated upon or closed

// ✅ CORRECT: Create new stream
Stream<Integer> stream1 = numbers.stream()
    .filter(n -> n > 2);
List<Integer> list = stream1.collect(Collectors.toList());

Stream<Integer> stream2 = numbers.stream()
    .filter(n -> n > 2);
long count = stream2.count();

// ✅ CORRECT: Use intermediate result
List<Integer> filtered = numbers.stream()
    .filter(n -> n > 2)
    .collect(Collectors.toList());

List<Integer> list = filtered;
long count = filtered.size();

// Why? Streams are designed for:
// - Single-use pipelines
// - Lazy evaluation
// - Potential parallel processing
// - Resource management
```

---

## Interview Tricky Questions (7 Questions)

### Q16: Infinite Streams
**Question**: How do you safely handle infinite streams?

**Answer**:
```java
// ❌ WRONG: Infinite stream without limit
Stream<Integer> infinite = Stream.iterate(0, n -> n + 1);
infinite.forEach(System.out::println);  // Never stops!

// ✅ CORRECT: Use limit()
Stream.iterate(0, n -> n + 1)
    .limit(10)
    .forEach(System.out::println);  // Prints 0-9

// ✅ CORRECT: Use takeWhile() (Java 9+)
Stream.iterate(0, n -> n + 1)
    .takeWhile(n -> n < 10)
    .forEach(System.out::println);  // Prints 0-9

// Infinite stream with condition
Stream.iterate(0, n -> n < 100, n -> n + 1)
    .forEach(System.out::println);  // Prints 0-99

// Generate infinite stream
Stream.generate(Math::random)
    .limit(5)
    .forEach(System.out::println);  // 5 random numbers
```

---

### Q17: Stream Memory Usage
**Question**: Do streams consume more memory than loops?

**Answer**:
```java
// Streams: Lazy evaluation, minimal memory
List<Integer> numbers = IntStream.range(0, 1000000)
    .boxed()
    .collect(Collectors.toList());

numbers.stream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * 2)
    .forEach(System.out::println);

// Loop: Eager evaluation, same memory
for (Integer n : numbers) {
    if (n % 2 == 0) {
        System.out.println(n * 2);
    }
}

// Memory usage: Similar for both

// But streams can be more efficient:
// - Lazy evaluation skips unnecessary processing
// - Short-circuit operations stop early
// - Parallel streams distribute work

// Streams can use more memory:
// - Intermediate collections (collect())
// - Buffering for parallel processing
// - Functional objects (lambdas)

// Best practice:
// - Use streams for clarity
// - Profile if memory is concern
// - Avoid unnecessary intermediate collections
```

---

### Q18: Collector vs reduce()
**Question**: When should you use collect() vs reduce()?

**Answer**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// reduce(): Combine elements into single value
int sum = numbers.stream()
    .reduce(0, Integer::sum);
// Result: 15

// collect(): Gather elements into container
List<Integer> list = numbers.stream()
    .collect(Collectors.toList());
// Result: [1, 2, 3, 4, 5]

// Key differences:
// reduce():
// - Combines elements into single value
// - Immutable accumulator (functional)
// - Good for: sum, product, max, min
// - Returns: Single value or Optional

// collect():
// - Gathers elements into container
// - Mutable accumulator (imperative)
// - Good for: lists, sets, maps, custom objects
// - Returns: Container

// Example: Sum with reduce()
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);

// Example: Sum with collect()
int sum = numbers.stream()
    .collect(Collectors.summingInt(Integer::intValue));

// Example: Group with collect()
Map<Boolean, List<Integer>> grouped = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));

// Example: Custom object with collect()
String csv = words.stream()
    .collect(Collectors.joining(", "));

// Rule of thumb:
// - Use reduce() for single value
// - Use collect() for container/grouping
```

---

### Q19: Parallel Stream Ordering
**Question**: Does parallelStream() maintain order?

**Answer**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Sequential: Maintains order
List<Integer> sequential = numbers.stream()
    .map(n -> n * 2)
    .collect(Collectors.toList());
// Result: [2, 4, 6, 8, 10]

// Parallel: Maintains order (for most operations)
List<Integer> parallel = numbers.parallelStream()
    .map(n -> n * 2)
    .collect(Collectors.toList());
// Result: [2, 4, 6, 8, 10] (same order)

// But some operations don't guarantee order:
Set<Integer> set = numbers.parallelStream()
    .collect(Collectors.toSet());
// Order not guaranteed

// Unordered stream (more efficient):
List<Integer> unordered = numbers.parallelStream()
    .unordered()
    .collect(Collectors.toList());
// Order not guaranteed, but faster

// Performance tip:
// - Use unordered() for parallel streams if order doesn't matter
// - Reduces synchronization overhead
// - Can improve performance

List<Integer> result = numbers.parallelStream()
    .unordered()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());
```

---

### Q20: Stream Debugging
**Question**: How do you debug a stream pipeline?

**Answer**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Method 1: Use peek()
List<Integer> result = numbers.stream()
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

// Method 2: Break into steps
Stream<Integer> filtered = numbers.stream()
    .filter(n -> n > 2);
System.out.println("Filtered: " + filtered.collect(Collectors.toList()));

Stream<Integer> mapped = numbers.stream()
    .filter(n -> n > 2)
    .map(n -> n * 2);
System.out.println("Mapped: " + mapped.collect(Collectors.toList()));

// Method 3: Use IDE debugger
// - Set breakpoint in lambda
// - Step through execution
// - Inspect variables

// Method 4: Print intermediate results
List<Integer> filtered = numbers.stream()
    .filter(n -> n > 2)
    .collect(Collectors.toList());
System.out.println("Filtered: " + filtered);

List<Integer> mapped = filtered.stream()
    .map(n -> n * 2)
    .collect(Collectors.toList());
System.out.println("Mapped: " + mapped);

// Best practice:
// - Use peek() for quick debugging
// - Break into steps for complex pipelines
// - Use IDE debugger for detailed analysis
```

---

### Q21: Stream vs Loop Performance
**Question**: Are streams always slower than loops?

**Answer**:
```java
// Benchmark: Sum 1 million numbers

// Loop:
long start = System.nanoTime();
int sum = 0;
for (int i = 0; i < 1000000; i++) {
    sum += i;
}
long loopTime = System.nanoTime() - start;

// Stream:
long start = System.nanoTime();
int sum = IntStream.range(0, 1000000)
    .sum();
long streamTime = System.nanoTime() - start;

// Results (approximate):
// Loop: 1-2ms
// Stream: 2-3ms
// Parallel Stream: 1-2ms (with 4 cores)

// Streams are slightly slower due to:
// - Functional overhead
// - Lambda creation
// - Stream object creation

// But streams can be faster:
// - Parallel processing
// - Short-circuit operations
// - Compiler optimizations

// Performance tips:
// 1. Use primitive streams (IntStream, LongStream)
List<Integer> numbers = ...;
int sum = numbers.stream()
    .mapToInt(Integer::intValue)
    .sum();  // More efficient

// 2. Avoid unnecessary intermediate operations
// ❌ WRONG
List<Integer> result = numbers.stream()
    .filter(n -> n > 5)
    .collect(Collectors.toList())
    .stream()
    .map(n -> n * 2)
    .collect(Collectors.toList());

// ✅ CORRECT
List<Integer> result = numbers.stream()
    .filter(n -> n > 5)
    .map(n -> n * 2)
    .collect(Collectors.toList());

// 3. Use parallel only when beneficial
// ❌ WRONG
List<Integer> small = Arrays.asList(1, 2, 3);
small.parallelStream().forEach(System.out::println);

// ✅ CORRECT
List<Integer> large = IntStream.range(0, 1000000)
    .boxed()
    .collect(Collectors.toList());
large.parallelStream().forEach(System.out::println);

// Conclusion:
// - Streams: Slightly slower for simple operations
// - Streams: Faster for complex pipelines
// - Streams: Much faster for parallel processing
// - Streams: Better for readability and maintainability
```

---

## Summary

### Key Concepts to Master
1. **Stream Creation**: From collections, arrays, generators
2. **Lazy Evaluation**: Operations deferred until terminal
3. **Intermediate Operations**: filter, map, flatMap, distinct, sorted
4. **Terminal Operations**: forEach, collect, reduce, count, findFirst
5. **Collectors**: toList, groupingBy, partitioningBy, joining
6. **Optional**: Safe null handling
7. **Parallel Streams**: Multi-threaded processing
8. **Performance**: When to use streams vs loops

### Common Mistakes
- ❌ Reusing streams after terminal operation
- ❌ Using parallelStream() for small datasets
- ❌ Stateful operations in parallel streams
- ❌ Not understanding lazy evaluation
- ❌ Inefficient collector usage

### Best Practices
- ✅ Use streams for complex transformations
- ✅ Filter before map
- ✅ Use appropriate collectors
- ✅ Understand lazy evaluation
- ✅ Profile before parallelizing
- ✅ Use peek() for debugging

---

**Next**: Study EDGE_CASES.md to learn about common pitfalls!
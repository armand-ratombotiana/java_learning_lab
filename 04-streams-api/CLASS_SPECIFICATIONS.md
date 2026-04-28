# Streams API Module - Class & Method Specifications

## Overview

This document provides complete method signatures and specifications for all 17 demonstration classes in the Streams API module. Each specification includes:
- Purpose of the class
- Public methods with signatures
- Return types and parameters
- Key implementation notes
- Example behaviors

---

## PACKAGE 1: STREAM BASICS (3 Classes)

### Class 1.1: StreamInterfaceDemo

**Purpose**: Demonstrate Stream creation, characteristics, and lifecycle

**Package**: `com.learning.package1_basics`

```java
public class StreamInterfaceDemo {
    
    // ============ Stream Creation Patterns ============
    
    /**
     * Creates a stream from a List collection
     * @param list - Non-null list to convert to stream
     * @return Stream of list elements
     */
    public static <T> Stream<T> createFromList(List<T> list) {
        // Implementation: return list.stream();
    }
    
    /**
     * Creates a stream from a Set collection
     * @param set - Non-null set to convert to stream
     * @return Stream of set elements
     */
    public static <T> Stream<T> createFromSet(Set<T> set) {
        // Implementation: return set.stream();
    }
    
    /**
     * Creates a stream from an array using Arrays.stream()
     * @param array - Non-null array to convert to stream
     * @return Stream of array elements
     */
    public static <T> Stream<T> createFromArray(T[] array) {
        // Implementation: return Arrays.stream(array);
    }
    
    /**
     * Creates a stream from a Map's values
     * @param map - Non-null map
     * @return Stream of map values
     */
    public static <K, V> Stream<V> createFromMapValues(Map<K, V> map) {
        // Implementation: return map.values().stream();
    }
    
    /**
     * Creates a stream from a Map's entries
     * @param map - Non-null map
     * @return Stream of map entries
     */
    public static <K, V> Stream<Map.Entry<K, V>> createFromMapEntries(Map<K, V> map) {
        // Implementation: return map.entrySet().stream();
    }
    
    /**
     * Creates a stream using Stream.builder() pattern
     * @return Stream built from builder
     */
    public static Stream<Integer> createUsingStreamBuilder() {
        // Implementation: Stream.Builder<Integer> builder = Stream.builder();
        //                builder.add(1).add(2).add(3);
        //                return builder.build();
    }
    
    /**
     * Creates a stream by concatenating multiple streams
     * @param streams - Variable number of streams
     * @return Concatenated stream
     */
    @SafeVarargs
    public static <T> Stream<T> concatenateStreams(Stream<T>... streams) {
        // Implementation: return Stream.concat(s1, s2) for multiple
        //                or use flatMap approach
    }
    
    /**
     * Creates a stream of a single element
     * @param element - Single element
     * @return Stream containing one element
     */
    public static <T> Stream<T> createSingleElementStream(T element) {
        // Implementation: return Stream.of(element);
    }
    
    /**
     * Creates an empty stream
     * @return Empty stream
     */
    public static <T> Stream<T> createEmptyStream() {
        // Implementation: return Stream.empty();
    }
    
    // ============ Stream Characteristics ============
    
    /**
     * Demonstrates stream characteristics (ORDERED, DISTINCT, etc)
     * Prints characteristics to console
     */
    public static void demonstrateStreamCharacteristics() {
        // Implementation: Show characteristics of different streams
        //                List.stream() -> ORDERED, SIZED
        //                Set.stream() -> DISTINCT
        //                IntStream.range() -> ORDERED, DISTINCT, SORTED
    }
    
    /**
     * Validates that stream is consumed after terminal operation
     * Attempting to reuse should throw IllegalStateException
     * @return true if reuse properly throws exception
     */
    public static boolean validateStreamReuse() {
        // Implementation: Create stream, consume with terminal op
        //                Attempt second operation -> should throw
        //                Return true if exception caught
    }
    
    // ============ Lazy vs Eager Execution ============
    
    /**
     * Demonstrates difference between intermediate (lazy) and terminal operations
     * Shows that intermediate operations don't execute until terminal op
     */
    public static void demonstrateLazyExecution() {
        // Implementation: Create stream with System.out.println in intermediate ops
        //                Show nothing prints until terminal operation
    }
    
    /**
     * Shows effect of short-circuit terminal operations
     * Some terminal ops (findFirst, anyMatch) return early
     */
    public static void demonstrateShortCircuiting() {
        // Implementation: CountDownLatch or other tracking to show
        //                Stream stops processing early
    }
    
    // ============ Stream Lifecycle ============
    
    /**
     * Demonstrates proper stream closure patterns
     * Shows try-with-resources for file streams
     */
    public static void demonstrateStreamResourceManagement() throws IOException {
        // Implementation: Show try-with-resources pattern
        //                Files.lines(Path) automatically closes
    }
    
    /**
     * Creates and immediately consumes a stream
     * Shows complete lifecycle from creation to terminal operation
     */
    public static long demonstrateCompleteStreamLifecycle(List<Integer> data) {
        // Implementation: Stream<Integer> stream = data.stream();
        //                return stream.filter(n -> n > 5).count();
    }
}
```

### Class 1.2: StreamSourcesDemo

**Purpose**: Show various stream sources and creation methods

**Package**: `com.learning.package1_basics`

```java
public class StreamSourcesDemo {
    
    // ============ Collection Sources ============
    
    /**
     * Creates stream from List
     */
    public static <T> Stream<T> streamFromList(List<T> list) {
        // Implementation: return list.stream();
    }
    
    /**
     * Creates stream from Set
     */
    public static <T> Stream<T> streamFromSet(Set<T> set) {
        // Implementation: return set.stream();
    }
    
    /**
     * Creates stream from Collection
     */
    public static <T> Stream<T> streamFromCollection(Collection<T> collection) {
        // Implementation: return collection.stream();
    }
    
    /**
     * Creates stream from Map keys
     */
    public static <K, V> Stream<K> streamFromMapKeys(Map<K, V> map) {
        // Implementation: return map.keySet().stream();
    }
    
    /**
     * Creates stream from Map values
     */
    public static <K, V> Stream<V> streamFromMapValues(Map<K, V> map) {
        // Implementation: return map.values().stream();
    }
    
    /**
     * Creates stream from Map entries
     */
    public static <K, V> Stream<Map.Entry<K, V>> streamFromMapEntries(Map<K, V> map) {
        // Implementation: return map.entrySet().stream();
    }
    
    // ============ Array Sources ============
    
    /**
     * Creates stream from object array
     */
    public static <T> Stream<T> streamFromArray(T[] array) {
        // Implementation: return Arrays.stream(array);
    }
    
    /**
     * Creates IntStream from primitive int array
     */
    public static IntStream streamFromIntArray(int[] array) {
        // Implementation: return Arrays.stream(array);
    }
    
    /**
     * Creates LongStream from primitive long array
     */
    public static LongStream streamFromLongArray(long[] array) {
        // Implementation: return Arrays.stream(array);
    }
    
    /**
     * Creates DoubleStream from primitive double array
     */
    public static DoubleStream streamFromDoubleArray(double[] array) {
        // Implementation: return Arrays.stream(array);
    }
    
    /**
     * Creates stream from array slice (start to end indices)
     */
    public static <T> Stream<T> streamFromArraySlice(T[] array, int start, int end) {
        // Implementation: return Arrays.stream(array, start, end);
    }
    
    // ============ Range Sources ============
    
    /**
     * Creates IntStream of range [start, end)
     */
    public static IntStream intRange(int start, int end) {
        // Implementation: return IntStream.range(start, end);
    }
    
    /**
     * Creates IntStream of range [start, end] (inclusive)
     */
    public static IntStream intRangeInclusive(int start, int end) {
        // Implementation: return IntStream.rangeClosed(start, end);
    }
    
    /**
     * Creates LongStream of range
     */
    public static LongStream longRange(long start, long end) {
        // Implementation: return LongStream.range(start, end);
    }
    
    /**
     * Creates LongStream of range (inclusive)
     */
    public static LongStream longRangeInclusive(long start, long end) {
        // Implementation: return LongStream.rangeClosed(start, end);
    }
    
    // ============ File & I/O Sources ============
    
    /**
     * Creates stream of lines from file
     * @param filePath - Path to file
     * @return Stream of file lines
     * @throws IOException if file not found
     */
    public static Stream<String> streamFromFile(Path filePath) throws IOException {
        // Implementation: return Files.lines(filePath);
        //                Note: Must be closed with try-with-resources
    }
    
    /**
     * Creates stream of lines from BufferedReader
     * @param reader - Non-null BufferedReader
     * @return Stream of lines
     * @throws IOException if read error
     */
    public static Stream<String> streamFromBufferedReader(BufferedReader reader) throws IOException {
        // Implementation: return reader.lines();
    }
    
    // ============ Generator Sources ============
    
    /**
     * Creates infinite stream using Stream.generate()
     * @param supplier - Supplier providing values
     * @return Infinite stream (must be limited with terminal op)
     */
    public static <T> Stream<T> generateInfiniteStream(Supplier<T> supplier) {
        // Implementation: return Stream.generate(supplier);
        //                WARNING: Without limit/terminal, runs forever
    }
    
    /**
     * Creates infinite stream using Stream.iterate()
     * @param seed - Initial value
     * @param operator - Function to generate next value
     * @return Infinite stream
     */
    public static <T> Stream<T> iterateInfiniteStream(T seed, UnaryOperator<T> operator) {
        // Implementation: return Stream.iterate(seed, operator);
    }
    
    /**
     * Creates infinite stream with condition (Java 9+)
     * @param seed - Initial value
     * @param hasNext - Predicate to continue
     * @param next - Function to generate next
     * @return Finite or infinite stream based on predicate
     */
    public static <T> Stream<T> iterateConditionalStream(T seed, Predicate<T> hasNext, UnaryOperator<T> next) {
        // Implementation: return Stream.iterate(seed, hasNext, next);
    }
    
    /**
     * Custom infinite sequence: Fibonacci numbers
     * @param limit - Maximum sequence length
     * @return Stream of first 'limit' Fibonacci numbers
     */
    public static Stream<Long> fibonacciStream(int limit) {
        // Implementation: Generate Fibonacci using iterate
        //                [0, 1, 1, 2, 3, 5, 8, 13, ...]
    }
    
    /**
     * Custom infinite sequence: Prime numbers
     * @param limit - Generate primes up to 'limit'
     * @return Stream of prime numbers
     */
    public static Stream<Integer> primeNumberStream(int limit) {
        // Implementation: Generate primes using filter or custom generation
    }
}
```

### Class 1.3: IntermediateOperationsBasicsDemo

**Purpose**: Master fundamental intermediate operations

**Package**: `com.learning.package1_basics`

```java
public class IntermediateOperationsBasicsDemo {
    
    // ============ Filter Operations ============
    
    /**
     * Filters stream using predicate
     * @param stream - Input stream
     * @param predicate - Condition to keep elements
     * @return Filtered stream
     */
    public static <T> Stream<T> filterByPredicate(Stream<T> stream, Predicate<T> predicate) {
        // Implementation: return stream.filter(predicate);
    }
    
    /**
     * Removes null values from stream
     */
    public static <T> Stream<T> filterNonNull(Stream<T> stream) {
        // Implementation: return stream.filter(Objects::nonNull);
    }
    
    /**
     * Filters stream to only include elements of specific type
     */
    public static <T> Stream<T> filterByType(Stream<?> stream, Class<T> type) {
        // Implementation: return stream
        //                    .filter(type::isInstance)
        //                    .map(type::cast);
    }
    
    /**
     * Multiple filters (AND logic)
     */
    public static <T> Stream<T> multipleFilters(Stream<T> stream, 
                                                  Predicate<T>... predicates) {
        // Implementation: return stream.filter(
        //                    t -> Arrays.stream(predicates).allMatch(p -> p.test(t))
        //                );
    }
    
    // ============ Map Operations ============
    
    /**
     * Transforms each element using function
     */
    public static <T, R> Stream<R> mapTransform(Stream<T> stream, Function<T, R> mapper) {
        // Implementation: return stream.map(mapper);
    }
    
    /**
     * Maps to integers
     */
    public static <T> IntStream mapToInt(Stream<T> stream, ToIntFunction<T> mapper) {
        // Implementation: return stream.mapToInt(mapper);
    }
    
    /**
     * Maps to longs
     */
    public static <T> LongStream mapToLong(Stream<T> stream, ToLongFunction<T> mapper) {
        // Implementation: return stream.mapToLong(mapper);
    }
    
    /**
     * Maps to doubles
     */
    public static <T> DoubleStream mapToDouble(Stream<T> stream, ToDoubleFunction<T> mapper) {
        // Implementation: return stream.mapToDouble(mapper);
    }
    
    /**
     * Maps Map entries to String representation
     */
    public static <K, V> Stream<String> mapEntriesToString(Stream<Map.Entry<K, V>> stream) {
        // Implementation: return stream.map(e -> e.getKey() + "=" + e.getValue());
    }
    
    /**
     * Chained mapping
     */
    public static <T> Stream<String> chainedMaps(Stream<T> stream) {
        // Implementation: return stream
        //                    .map(Object::toString)
        //                    .map(String::toUpperCase)
        //                    .map(s -> "*" + s + "*");
    }
    
    // ============ Sort Operations ============
    
    /**
     * Sorts stream using natural ordering
     */
    public static <T extends Comparable<T>> Stream<T> sortNatural(Stream<T> stream) {
        // Implementation: return stream.sorted();
    }
    
    /**
     * Sorts stream using custom comparator
     */
    public static <T> Stream<T> sortByComparator(Stream<T> stream, Comparator<T> comparator) {
        // Implementation: return stream.sorted(comparator);
    }
    
    /**
     * Sorts in reverse order
     */
    public static <T extends Comparable<T>> Stream<T> sortReverse(Stream<T> stream) {
        // Implementation: return stream.sorted(Comparator.reverseOrder());
    }
    
    /**
     * Sorts by specific field/property
     */
    public static <T extends Comparable<T>,U extends Comparable<U>> Stream<T> sortByField(Stream<T> stream, 
                                                                                           Function<T, U> keyExtractor) {
        // Implementation: return stream.sorted(Comparator.comparing(keyExtractor));
    }
    
    /**
     * Sorts by multiple criteria
     */
    public static Stream<String> sortMultipleCriteria(Stream<String> stream) {
        // Implementation: return stream.sorted(
        //                    Comparator.comparingInt(String::length)
        //                             .thenComparing(Comparator.naturalOrder())
        //                );
    }
    
    // ============ Limit & Skip ============
    
    /**
     * Takes first n elements
     */
    public static <T> Stream<T> limit(Stream<T> stream, long n) {
        // Implementation: return stream.limit(n);
    }
    
    /**
     * Skips first n elements
     */
    public static <T> Stream<T> skip(Stream<T> stream, long n) {
        // Implementation: return stream.skip(n);
    }
    
    /**
     * Pagination: skips (page-1)*pageSize, takes pageSize
     */
    public static <T> Stream<T> paginate(Stream<T> stream, int pageSize, int pageNumber) {
        // Implementation: return stream.skip((long)(pageNumber - 1) * pageSize)
        //                              .limit(pageSize);
    }
    
    /**
     * Takes elements while predicate is true
     */
    public static <T> Stream<T> takeWhile(Stream<T> stream, Predicate<T> condition) {
        // Implementation: return stream.takeWhile(condition);
        //                Note: Java 9+
    }
    
    /**
     * Skips elements while predicate is true
     */
    public static <T> Stream<T> skipWhile(Stream<T> stream, Predicate<T> condition) {
        // Implementation: return stream.dropWhile(condition);
        //                Note: Java 9+
    }
    
    // ============ Distinct ============
    
    /**
     * Removes duplicate elements
     */
    public static <T> Stream<T> distinct(Stream<T> stream) {
        // Implementation: return stream.distinct();
    }
    
    /**
     * Removes duplicates based on key extractor
     */
    public static <T, K> Stream<T> distinctBy(Stream<T> stream, Function<T, K> keyExtractor) {
        // Implementation: Set<K> seen = ConcurrentHashMap.newKeySet();
        //                return stream.filter(t -> seen.add(keyExtractor.apply(t)));
    }
}
```

---

## PACKAGE 2: ADVANCED INTERMEDIATE OPERATIONS (3 Classes)

### Class 2.1: FlatMapOperationsDemo

**Purpose**: Master nested structure flattening

**Package**: `com.learning.package2_intermediate`

```java
public class FlatMapOperationsDemo {
    
    // ============ Basic FlatMap ============
    
    /**
     * Core flatMap operation: maps each element to stream, then flattens
     */
    public static <T, R> Stream<R> flatMap(Stream<T> stream, Function<T, Stream<R>> mapper) {
        // Implementation: return stream.flatMap(mapper);
    }
    
    /**
     * FlatMap on List of Lists to single List
     */
    public static <T> Stream<T> flatMapNestedLists(Stream<List<T>> stream) {
        // Implementation: return stream.flatMap(List::stream);
    }
    
    /**
     * FlatMap on array of arrays to single stream
     */
    public static <T> Stream<T> flatMapNestedArrays(Stream<T[]> stream) {
        // Implementation: return stream.flatMap(Arrays::stream);
    }
    
    /**
     * FlatMap string to characters (example)
     */
    public static Stream<Character> flatMapStringToCharacters(Stream<String> words) {
        // Implementation: return words.flatMap(word -> 
        //                    word.chars()
        //                        .mapToObj(c -> (char)c)
        //                        .map(Character::toLowerCase)
        //                );
    }
    
    /**
     * FlatMap with complex transformation
     */
    public static Stream<Integer> flatMapNumberToFactors(Stream<Integer> numbers) {
        // Implementation: Find all factors of each number
        //                [12] -> [1,2,3,4,6,12]
        //                [10] -> [1,2,5,10]
    }
    
    /**
     * FlatMap with custom objects (orders from customers)
     */
    public static <T, R> Stream<R> flatMapCustom(Stream<T> stream, Function<T, Collection<R>> mapper) {
        // Implementation: return stream.flatMap(t -> mapper.apply(t).stream());
    }
    
    // ============ FlatMap with Optional ============
    
    /**
     * FlatMap each element to Optional, filtering out empty
     */
    public static <T, R> Stream<R> flatMapOptional(Stream<T> stream, 
                                                    Function<T, Optional<R>> mapper) {
        // Implementation: return stream
        //                    .flatMap(t -> mapper.apply(t).stream());
    }
    
    /**
     * Parse strings to integers, skipping invalid
     */
    public static Stream<Integer> flatMapParseIntegers(Stream<String> strings) {
        // Implementation: return strings.flatMap(s -> {
        //                    try {
        //                        return Stream.of(Integer.parseInt(s));
        //                    } catch (NumberFormatException e) {
        //                        return Stream.empty();
        //                    }
        //                });
    }
    
    // ============ Performance & Cardinality ============
    
    /**
     * Demonstrates flatMap performance impact
     * Shows N:M relationship processing
     */
    public static void demonstrateFlatMapPerformance() {
        // Implementation: Compare count of input vs output
        //                Show that 100 inputs with 10 outputs each = 1000 results
    }
    
    /**
     * Warns about cardinality explosion
     * When each element maps to many elements
     */
    public static void avoidCardinalityExplosion() {
        // Implementation: Show problematic pattern
        //                [1,2,3] -> flatMap to 1 billion elements
        //                Memory and time explosion
    }
    
    /**
     * Limits flatMap explosion with bounded output
     */
    public static <T, R> Stream<R> boundedFlatMap(Stream<T> stream, 
                                                   Function<T, Stream<R>> mapper,
                                                   int maxPerElement) {
        // Implementation: return stream.flatMap(t -> mapper.apply(t).limit(maxPerElement));
    }
}
```

### Class 2.2: PeekAndDebugDemo

**Purpose**: Debug streams without modifying data

**Package**: `com.learning.package2_intermediate`

```java
public class PeekAndDebugDemo {
    
    // ============ Basic Peek ============
    
    /**
     * Peek at stream elements for debugging
     * @param stream - Input stream
     * @param action - Consumer action (typically System.out.println)
     * @return Stream with side effects (not recommended in production)
     */
    public static <T> Stream<T> peek(Stream<T> stream, Consumer<T> action) {
        // Implementation: return stream.peek(action);
    }
    
    /**
     * Peek and log each element
     */
    public static <T> Stream<T> peekAndLog(Stream<T> stream) {
        // Implementation: System.out.println can show element flow
    }
    
    // ============ Conditional Peek ============
    
    /**
     * Peek only elements matching condition
     */
    public static <T> Stream<T> peekIf(Stream<T> stream, Predicate<T> condition, Consumer<T> action) {
        // Implementation: return stream.peek(t -> {
        //                    if (condition.test(t)) action.accept(t);
        //                });
    }
    
    // ============ Metrics Collection ============
    
    /**
     * Peek with element counting
     */
    public static <T> Stream<T> peekWithCount(Stream<T> stream) {
        // Implementation: Use AtomicInteger to track count
        //                Peek shows "Processing element 1, 2, 3..."
    }
    
    /**
     * Peek with timing metrics
     */
    public static <T> Stream<T> peekWithTiming(Stream<T> stream) {
        // Implementation: Use System.nanoTime to track throughput
        //                Shows time between elements
    }
    
    /**
     * Peek with type information
     */
    public static <T> Stream<T> peekWithType(Stream<T> stream) {
        // Implementation: Show class name of each element
    }
    
    // ============ Debug Checkpoints ============
    
    /**
     * Named checkpoint in pipeline
     */
    public static <T> Stream<T> checkpoint(Stream<T> stream, String name) {
        // Implementation: return stream.peek(t -> System.out.printf("[%s] %s%n", name, t));
    }
    
    /**
     * Debug entire pipeline with multiple checkpoints
     */
    public static Stream<Integer> debugPipeline(Stream<Integer> stream) {
        // Implementation: return stream
        //                    .filter(n -> n > 0).peek(t -> System.out.println("After filter: " + t))
        //                    .map(n -> n * 2).peek(t -> System.out.println("After map: " + t))
        //                    .limit(5).peek(t -> System.out.println("After limit: " + t));
    }
    
    // ============ WARNING: Side Effects ============
    
    /**
     * ANTI-PATTERN: Shows incorrect use of peek for side effects
     */
    public static void demonstrateIncorrectPeekUsage() {
        // Implementation: Show wrong pattern - using peek to modify state
        //                Correct: Use forEach for side effects
    }
    
    /**
     * Best practices for stream debugging
     */
    public static void bestPracticesForDebugging() {
        // Implementation: Document when to use .peek() vs .forEach()
        //                peek: intermediate, for debugging only
        //                forEach: terminal, for actual side effects
    }
}
```

### Class 2.3: StatefulOperationsDemo

**Purpose**: Handle stateful intermediate operations

**Package**: `com.learning.package2_intermediate`

```java
public class StatefulOperationsDemo {
    
    // ============ Understanding Stateful Ops ============
    
    /**
     * Explains why distinct() is stateful
     * Must remember all previously seen elements
     */
    public static <T> Stream<T> demonstrateDistinctAsStateful(Stream<T> stream) {
        // Implementation: return stream.distinct();
        //                Stateful: Must maintain Set of seen elements
    }
    
    /**
     * Shows memory usage of distinct for large datasets
     */
    public static void analyzeDistinctMemoryUsage(int dataSize) {
        // Implementation: Create large stream, track memory before/after distinct
    }
    
    /**
     * Explains why sorted() is stateful
     * Must buffer all elements before sorting
     */
    public static <T extends Comparable<T>> Stream<T> demonstrateSortedAsStateful(Stream<T> stream) {
        // Implementation: return stream.sorted();
        //                Stateful: Must buffer entire stream before sorting
    }
    
    /**
     * Shows memory impact of sorted on large datasets
     */
    public static void analyzeSortedMemoryUsage(int dataSize) {
        // Implementation: Track memory usage of sorted on various sizes
    }
    
    // ============ Ordering Anomalies ============
    
    /**
     * Explains that limit() after distinct preserves order
     * But within limit, elements are not guaranteed unique
     */
    public static <T> Stream<T> limitAndDistinctInteraction(Stream<T> stream, int limit) {
        // Implementation: Compare stream.limit(n).distinct()
        //              vs stream.distinct().limit(n)
        //              Different results!
    }
    
    /**
     * Shows skip/limit boundaries
     */
    public static <T> Stream<T> skipAndLimitInteraction(Stream<T> stream, int skip, int limit) {
        // Implementation: return stream.skip(skip).limit(limit);
    }
    
    // ============ Performance Impact ============
    
    /**
     * Demonstrates performance cost of stateful operations
     */
    public static void analyzeStatefulOperationPerformance() {
        // Implementation: Benchmark distinct vs filter
        //                distinct requires Set lookup/insertion O(1) per element
        //                But setup overhead is high
    }
    
    /**
     * Shows why parallel doesn't help stateful ops
     */
    public static void demonstrateParallelStatefulCosts() {
        // Implementation: Stream.of(data)
        //                    .parallel()
        //                    .distinct()  // <- Much slower in parallel
        //                    .forEach(System.out::println);
    }
    
    /**
     * Mitigating stateful operation performance
     */
    public static void optimizingStatefulOperations() {
        // Implementation: Strategies to minimize impact
        //                1. Use LinkedHashSet for distinct with ordering
        //                2. Filter before distinct to reduce size
        //                3. Avoid nested distinct() calls
    }
}
```

---

## PACKAGE 3: TERMINAL OPERATIONS (4 Classes)

### Class 3.1: TerminalOperationsBasicsDemo

**Purpose**: Master basic terminal operations

**Package**: `com.learning.package3_terminal`

```java
public class TerminalOperationsBasicsDemo {
    
    // ============ Consumption ============
    
    /**
     * forEach: Consume each stream element
     * @param stream - Input stream
     * @param consumer - Action to perform on each element
     */
    public static <T> void forEach(Stream<T> stream, Consumer<T> consumer) {
        // Implementation: stream.forEach(consumer);
    }
    
    /**
     * forEachOrdered: Guarantees order even in parallel streams
     */
    public static <T> void forEachOrdered(Stream<T> stream, Consumer<T> consumer) {
        // Implementation: stream.forEachOrdered(consumer);
    }
    
    /**
     * forEach with lambda: Print elements
     */
    public static void printElements(Stream<?> stream) {
        // Implementation: stream.forEach(System.out::println);
    }
    
    // ============ Counting ============
    
    /**
     * count: Returns total number of elements
     */
    public static long count(Stream<?> stream) {
        // Implementation: return stream.count();
    }
    
    /**
     * Count elements matching predicate
     */
    public static <T> long countMatching(Stream<T> stream, Predicate<T> predicate) {
        // Implementation: return stream.filter(predicate).count();
    }
    
    // ============ Finding ============
    
    /**
     * findFirst: Returns Optional with first element
     */
    public static <T> Optional<T> findFirst(Stream<T> stream) {
        // Implementation: return stream.findFirst();
    }
    
    /**
     * findAny: Returns any element (useful for parallel)
     */
    public static <T> Optional<T> findAny(Stream<T> stream) {
        // Implementation: return stream.findAny();
    }
    
    /**
     * Find first matching predicate
     */
    public static <T> Optional<T> findFirstMatching(Stream<T> stream, Predicate<T> predicate) {
        // Implementation: return stream.filter(predicate).findFirst();
    }
    
    // ============ Matching ============
    
    /**
     * anyMatch: Returns true if any element matches
     */
    public static <T> boolean anyMatch(Stream<T> stream, Predicate<T> predicate) {
        // Implementation: return stream.anyMatch(predicate);
    }
    
    /**
     * allMatch: Returns true if all elements match
     */
    public static <T> boolean allMatch(Stream<T> stream, Predicate<T> predicate) {
        // Implementation: return stream.allMatch(predicate);
    }
    
    /**
     * noneMatch: Returns true if no elements match
     */
    public static <T> boolean noneMatch(Stream<T> stream, Predicate<T> predicate) {
        // Implementation: return stream.noneMatch(predicate);
    }
    
    /**
     * Demonstrates any/all/none match with short-circuiting
     */
    public static void demonstrateShortCircuitingMatches() {
        // Implementation: Show that matching stops early
        //                anyMatch stops at first true
        //                allMatch stops at first false
    }
}
```

### Class 3.2: CollectOperationsDemo

**Purpose**: Collect stream results into collections

**Package**: `com.learning.package3_terminal`

```java
public class CollectOperationsDemo {
    
    // ============ Basic Collectors ============
    
    /**
     * Collect to List
     */
    public static <T> List<T> toList(Stream<T> stream) {
        // Implementation: return stream.collect(Collectors.toList());
        //                or stream.toList(); (Java 16+)
    }
    
    /**
     * Collect to Set
     */
    public static <T> Set<T> toSet(Stream<T> stream) {
        // Implementation: return stream.collect(Collectors.toSet());
    }
    
    /**
     * Collect to unmodifiable List (Java 10+)
     */
    public static <T> List<T> toUnmodifiableList(Stream<T> stream) {
        // Implementation: return stream.collect(Collectors.toUnmodifiableList());
    }
    
    /**
     * Collect to specific Collection type
     */
    public static <T> Collection<T> toCollection(Stream<T> stream, 
                                                  Supplier<Collection<T>> collectionSupplier) {
        // Implementation: return stream.collect(Collectors.toCollection(collectionSupplier));
    }
    
    /**
     * Collect to LinkedList
     */
    public static <T> LinkedList<T> toLinkedList(Stream<T> stream) {
        // Implementation: return stream.collect(Collectors.toCollection(LinkedList::new));
    }
    
    // ============ Map Collectors ============
    
    /**
     * Collect to Map (key-value pairs)
     */
    public static <T, K, V> Map<K, V> toMap(Stream<T> stream, 
                                             Function<T, K> keyMapper,
                                             Function<T, V> valueMapper) {
        // Implementation: return stream.collect(Collectors.toMap(keyMapper, valueMapper));
    }
    
    /**
     * Collect to Map with conflict resolution
     */
    public static <T, K, V> Map<K, V> toMapWithMerge(Stream<T> stream,
                                                      Function<T, K> keyMapper,
                                                      Function<T, V> valueMapper,
                                                      BinaryOperator<V> mergeFunction) {
        // Implementation: return stream.collect(
        //                    Collectors.toMap(keyMapper, valueMapper, mergeFunction)
        //                );
    }
    
    /**
     * Collect to specific Map type
     */
    public static <T, K, V> Map<K, V> toSpecificMap(Stream<T> stream,
                                                     Function<T, K> keyMapper,
                                                     Function<T, V> valueMapper,
                                                     Supplier<Map<K, V>> mapSupplier) {
        // Implementation: return stream.collect(
        //                    Collectors.toMap(keyMapper, valueMapper, (v1,v2)->v1, mapSupplier)
        //                );
    }
    
    // ============ String Collectors ============
    
    /**
     * Collect to concatenated String
     */
    public static <T> String joining(Stream<T> stream) {
        // Implementation: return stream.map(Object::toString)
        //                              .collect(Collectors.joining());
    }
    
    /**
     * Collect with delimiter
     */
    public static <T> String joiningWithDelimiter(Stream<T> stream, String delimiter) {
        // Implementation: return stream.map(Object::toString)
        //                              .collect(Collectors.joining(delimiter));
    }
    
    /**
     * Collect with prefix, delimiter, suffix
     */
    public static <T> String joiningWithFormat(Stream<T> stream, 
                                               String prefix, 
                                               String delimiter, 
                                               String suffix) {
        // Implementation: return stream.map(Object::toString)
        //                              .collect(Collectors.joining(delimiter, prefix, suffix));
    }
    
    // ============ Numeric Collectors ============
    
    /**
     * Collect summary statistics for integers
     */
    public static IntSummaryStatistics summaryStatisticsInt(Stream<Integer> stream) {
        // Implementation: return stream.collect(Collectors.summarizingInt(Integer::intValue));
    }
    
    /**
     * Collect summary statistics for doubles
     */
    public static DoubleSummaryStatistics summaryStatisticsDouble(Stream<Double> stream) {
        // Implementation: return stream.collect(Collectors.summarizingDouble(Double::doubleValue));
    }
}
```

### Class 3.3: ReductionOperationsDemo

**Purpose**: Reduce streams to single values

**Package**: `com.learning.package3_terminal`

```java
public class ReductionOperationsDemo {
    
    // ============ Reduce with Identity ============
    
    /**
     * Reduce to single value with identity
     * @param stream - Input stream
     * @param identity - Starting value
     * @param accumulator - How to combine elements
     */
    public static <T> T reduce(Stream<T> stream, T identity, BinaryOperator<T> accumulator) {
        // Implementation: return stream.reduce(identity, accumulator);
    }
    
    /**
     * Sum integers with reduce
     */
    public static int sumIntegers(Stream<Integer> stream) {
        // Implementation: return stream.reduce(0, Integer::sum);
    }
    
    /**
     * Multiply numbers
     */
    public static int productIntegers(Stream<Integer> stream) {
        // Implementation: return stream.reduce(1, (a, b) -> a * b);
    }
    
    /**
     * Concatenate strings
     */
    public static String concatenateStrings(Stream<String> stream) {
        // Implementation: return stream.reduce("", String::concat);
    }
    
    /**
     * Max of stream with identity
     */
    public static int maxWithIdentity(Stream<Integer> stream, int defaultValue) {
        // Implementation: return stream.reduce(defaultValue, Math::max);
    }
    
    // ============ Reduce without Identity ============
    
    /**
     * Reduce without identity (returns Optional)
     */
    public static <T> Optional<T> reduceNoIdentity(Stream<T> stream, BinaryOperator<T> accumulator) {
        // Implementation: return stream.reduce(accumulator);
    }
    
    /**
     * Sum without identity (empty stream case)
     */
    public static Optional<Integer> sumNoIdentity(Stream<Integer> stream) {
        // Implementation: return stream.reduce(Integer::sum);
    }
    
    /**
     * Find max without identity
     */
    public static <T extends Comparable<T>> Optional<T> findMaxNoIdentity(Stream<T> stream) {
        // Implementation: return stream.reduce((a, b) -> a.compareTo(b) > 0 ? a : b);
    }
    
    // ============ Min/Max Operations ============
    
    /**
     * Find minimum with comparator
     */
    public static <T> Optional<T> min(Stream<T> stream, Comparator<T> comparator) {
        // Implementation: return stream.min(comparator);
    }
    
    /**
     * Find maximum with comparator
     */
    public static <T> Optional<T> max(Stream<T> stream, Comparator<T> comparator) {
        // Implementation: return stream.max(comparator);
    }
    
    /**
     * Find min/max for integers
     */
    public static Optional<Integer> minInteger(Stream<Integer> stream) {
        // Implementation: return stream.reduce(Math::min);
    }
    
    /**
     * Find max for integers
     */
    public static Optional<Integer> maxInteger(Stream<Integer> stream) {
        // Implementation: return stream.reduce(Math::max);
    }
    
    // ============ Statistic Reductions ============
    
    /**
     * Average of integers
     */
    public static double averageIntegers(Stream<Integer> stream) {
        // Implementation: return stream.reduce(0, Integer::sum) / 
        //                       (double)stream.count();
        //                Note: Stream consumed by count, need separate stream
    }
    
    /**
     * Proper average using IntStream
     */
    public static double properAverage(IntStream stream) {
        // Implementation: return stream.average().orElse(0.0);
    }
    
    // ============ Custom Accumulator ============
    
    /**
     * Custom reduction with complex accumulator
     */
    public static <T, U> U customReduce(Stream<T> stream, 
                                        U identity,
                                        BiFunction<U, T, U> accumulator,
                                        BinaryOperator<U> combiner) {
        // Implementation: return stream.reduce(identity, accumulator, combiner);
    }
    
    /**
     * Build custom object through reduction
     */
    public static String buildStringThroughReduce(Stream<Character> stream) {
        // Implementation: Use StringBuilder with reduce
    }
}
```

### Class 3.4: MatchOperationsDemo

**Purpose**: Test stream elements against conditions

**Package**: `com.learning.package3_terminal`

```java
public class MatchOperationsDemo {
    
    // ============ anyMatch ============
    
    /**
     * anyMatch: Returns true if any element matches
     * Short-circuits at first true
     */
    public static <T> boolean anyMatch(Stream<T> stream, Predicate<T> predicate) {
        // Implementation: return stream.anyMatch(predicate);
    }
    
    /**
     * Check if stream contains element
     */
    public static <T> boolean contains(Stream<T> stream, T target) {
        // Implementation: return stream.anyMatch(target::equals);
    }
    
    /**
     * Check if any element is null
     */
    public static <T> boolean containsNull(Stream<T> stream) {
        // Implementation: return stream.anyMatch(Objects::isNull);
    }
    
    /**
     * Check if any element is positive
     */
    public static boolean anyPositive(Stream<Integer> stream) {
        // Implementation: return stream.anyMatch(n -> n > 0);
    }
    
    // ============ allMatch ============
    
    /**
     * allMatch: Returns true if all match
     * Short-circuits at first false
     */
    public static <T> boolean allMatch(Stream<T> stream, Predicate<T> predicate) {
        // Implementation: return stream.allMatch(predicate);
    }
    
    /**
     * Check if all elements are non-null
     */
    public static <T> boolean allNonNull(Stream<T> stream) {
        // Implementation: return stream.allMatch(Objects::nonNull);
    }
    
    /**
     * Check if all are positive
     */
    public static boolean allPositive(Stream<Integer> stream) {
        // Implementation: return stream.allMatch(n -> n > 0);
    }
    
    /**
     * Check if all strings uppercase
     */
    public static boolean allUppercase(Stream<String> stream) {
        // Implementation: return stream.allMatch(s -> s.equals(s.toUpperCase()));
    }
    
    // ============ noneMatch ============
    
    /**
     * noneMatch: Returns true if none match
     * Short-circuits at first true
     */
    public static <T> boolean noneMatch(Stream<T> stream, Predicate<T> predicate) {
        // Implementation: return stream.noneMatch(predicate);
    }
    
    /**
     * Check if no nulls
     */
    public static <T> boolean noNulls(Stream<T> stream) {
        // Implementation: return stream.noneMatch(Objects::isNull);
    }
    
    /**
     * Check if no negative numbers
     */
    public static boolean noNegatives(Stream<Integer> stream) {
        // Implementation: return stream.noneMatch(n -> n < 0);
    }
    
    // ============ Short-Circuit Behavior ============
    
    /**
     * Demonstrate short-circuit for performance
     */
    public static void demonstrateShortCircuitPerformance() {
        // Implementation: Show that anyMatch stops early
        //                Stream of 1 million elements
        //                firstMatch at position 10
    }
    
    /**
     * Compare anyMatch vs filter().count()
     */
    public static void compareMatchVsFilter() {
        // Implementation: anyMatch(predicate) faster than 
        //                filter(predicate).count() > 0
    }
}
```

---

## PACKAGE 4: COLLECTORS & GROUPING (3 Classes)

### Class 4.1: CollectorExamplesDemo

**Purpose**: Work with common collector patterns

**Package**: `com.learning.package4_collectors`

```java
public class CollectorExamplesDemo {
    
    // ============ Joining ============
    
    /**
     * Join strings without separator
     */
    public static String joinElements(Stream<String> stream) {
        // Implementation: return stream.collect(Collectors.joining());
    }
    
    /**
     * Join with delimiter
     */
    public static String joinWithComma(Stream<String> stream) {
        // Implementation: return stream.collect(Collectors.joining(", "));
    }
    
    /**
     * Join with prefix and suffix
     */
    public static String joinWithBrackets(Stream<String> stream) {
        // Implementation: return stream.collect(Collectors.joining(", ", "[", "]"));
    }
    
    // ============ Partitioning ============
    
    /**
     * Partition stream into two lists based on predicate
     * @return Map with true/false keys
     */
    public static <T> Map<Boolean, List<T>> partitionByPredicate(Stream<T> stream, 
                                                                  Predicate<T> predicate) {
        // Implementation: return stream.collect(Collectors.partitioningBy(predicate));
    }
    
    /**
     * Partition integers into even/odd
     */
    public static Map<Boolean, List<Integer>> partitionEvenOdd(Stream<Integer> stream) {
        // Implementation: return stream.collect(Collectors.partitioningBy(n -> n % 2 == 0));
    }
    
    /**
     * Partition with downstream collector
     */
    public static <T> Map<Boolean, Long> partitionWithCount(Stream<T> stream, 
                                                            Predicate<T> predicate) {
        // Implementation: return stream.collect(
        //                    Collectors.partitioningBy(predicate, Collectors.counting())
        //                );
    }
    
    /**
     * Partition to sets instead of lists
     */
    public static <T> Map<Boolean, Set<T>> partitionToSets(Stream<T> stream, 
                                                           Predicate<T> predicate) {
        // Implementation: return stream.collect(
        //                    Collectors.partitioningBy(predicate, Collectors.toSet())
        //                );
    }
    
    // ============ Mapping ============
    
    /**
     * Map and collect (transform during collection)
     */
    public static <T, R> List<R> mapAndCollect(Stream<T> stream, Function<T, R> mapper) {
        // Implementation: return stream.collect(Collectors.mapping(mapper, Collectors.toList()));
    }
    
    /**
     * Map to strings and collect
     */
    public static List<String> toStringList(Stream<?> stream) {
        // Implementation: return stream.collect(
        //                    Collectors.mapping(Object::toString, Collectors.toList())
        //                );
    }
    
    // ============ Flattening ============
    
    /**
     * Flatten nested collections during collection
     */
    public static <T> List<T> flattenAndCollect(Stream<List<T>> stream) {
        // Implementation: return stream.collect(
        //                    Collectors.flatMapping(List::stream, Collectors.toList())
        //                );
    }
}
```

### Class 4.2: GroupingByDemo

**Purpose**: Group elements by characteristics

**Package**: `com.learning.package4_collectors`

```java
public class GroupingByDemo {
    
    // ============ Simple Grouping ============
    
    /**
     * Group elements by classifier function
     */
    public static <T, K> Map<K, List<T>> groupBy(Stream<T> stream, Function<T, K> classifier) {
        // Implementation: return stream.collect(Collectors.groupingBy(classifier));
    }
    
    /**
     * Group by type
     */
    public static Map<String, List<?>> groupByType(Stream<?> stream) {
        // Implementation: return stream.collect(
        //                    Collectors.groupingBy(o -> o.getClass().getSimpleName())
        //                );
    }
    
    /**
     * Group strings by length
     */
    public static Map<Integer, List<String>> groupByLength(Stream<String> stream) {
        // Implementation: return stream.collect(Collectors.groupingBy(String::length));
    }
    
    /**
     * Group integers by parity
     */
    public static Map<String, List<Integer>> groupByParity(Stream<Integer> stream) {
        // Implementation: return stream.collect(
        //                    Collectors.groupingBy(n -> n % 2 == 0 ? "EVEN" : "ODD")
        //                );
    }
    
    // ============ Grouping with Downstream Collectors ============
    
    /**
     * Group and count
     */
    public static <T, K> Map<K, Long> groupAndCount(Stream<T> stream, Function<T, K> classifier) {
        // Implementation: return stream.collect(
        //                    Collectors.groupingBy(classifier, Collectors.counting())
        //                );
    }
    
    /**
     * Group to sets instead of lists
     */
    public static <T, K> Map<K, Set<T>> groupToSets(Stream<T> stream, Function<T, K> classifier) {
        // Implementation: return stream.collect(
        //                    Collectors.groupingBy(classifier, Collectors.toSet())
        //                );
    }
    
    /**
     * Group and join strings
     */
    public static <T, K> Map<K, String> groupAndJoin(Stream<T> stream,
                                                      Function<T, K> classifier,
                                                      Function<T, String> toStringMapper) {
        // Implementation: return stream.collect(
        //                    Collectors.groupingBy(classifier, 
        //                        Collectors.mapping(toStringMapper, Collectors.joining(", ")))
        //                );
    }
    
    /**
     * Group and find max
     */
    public static <T, K> Map<K, Optional<T>> groupAndFindMax(Stream<T> stream,
                                                              Function<T, K> classifier,
                                                              Comparator<T> comparator) {
        // Implementation: return stream.collect(
        //                    Collectors.groupingBy(classifier, Collectors.maxBy(comparator))
        //                );
    }
    
    // ============ Nested Grouping ============
    
    /**
     * Group by two levels (nested grouping)
     */
    public static <T, K1, K2> Map<K1, Map<K2, List<T>>> groupByTwoLevels(Stream<T> stream,
                                                                          Function<T, K1> classifier1,
                                                                          Function<T, K2> classifier2) {
        // Implementation: return stream.collect(
        //                    Collectors.groupingBy(classifier1,
        //                        Collectors.groupingBy(classifier2))
        //                );
    }
    
    /**
     * Group by three levels
     */
    public static <T, K1, K2, K3> Map<K1, Map<K2, Map<K3, List<T>>>> groupByThreeLevels(
                                                                      Stream<T> stream,
                                                                      Function<T, K1> classifier1,
                                                                      Function<T, K2> classifier2,
                                                                      Function<T, K3> classifier3) {
        // Implementation: Recursive groupingBy with three levels
    }
    
    // ============ Concurrent Grouping ============
    
    /**
     * Thread-safe grouping for parallel streams
     */
    public static <T, K> ConcurrentMap<K, List<T>> groupByConcurrent(Stream<T> stream,
                                                                      Function<T, K> classifier) {
        // Implementation: return stream.collect(
        //                    Collectors.groupingByConcurrent(classifier)
        //                );
    }
    
    /**
     * Concurrent grouping with counting
     */
    public static <T, K> ConcurrentMap<K, Long> groupByConcurrentWithCount(Stream<T> stream,
                                                                            Function<T, K> classifier) {
        // Implementation: return stream.collect(
        //                    Collectors.groupingByConcurrent(classifier, 
        //                        Collectors.counting())
        //                );
    }
}
```

### Class 4.3: ComplexCollectorsDemo

**Purpose**: Custom collectors and advanced patterns

**Package**: `com.learning.package4_collectors`

```java
public class ComplexCollectorsDemo {
    
    // ============ Custom Collectors ============
    
    /**
     * Collect to LinkedList (custom collection type)
     */
    public static <T> Collector<T, ?, LinkedList<T>> toLinkedListCollector() {
        // Implementation: return Collectors.toCollection(LinkedList::new);
    }
    
    /**
     * Collect to TreeSet with comparator
     */
    public static <T> Collector<T, ?, TreeSet<T>> toTreeSetCollector(Comparator<T> comparator) {
        // Implementation: return Collectors.toCollection(() -> new TreeSet<>(comparator));
    }
    
    /**
     * Collect to custom collection
     */
    public static <T, C extends Collection<T>> Collector<T, ?, C> toCustomCollection(
                                                    Supplier<C> collectionSupplier) {
        // Implementation: return Collectors.toCollection(collectionSupplier);
    }
    
    // ============ Collector.of() ============
    
    /**
     * Build custom collector from parts
     * Collector.of(supplier, accumulator, combiner)
     */
    public static <T> Collector<T, ?, List<T>> customListCollector() {
        // Implementation: return Collector.of(
        //                    ArrayList::new,
        //                    List::add,
        //                    (list1, list2) -> { list1.addAll(list2); return list1; }
        //                );
    }
    
    /**
     * Custom collector for Set
     */
    public static <T> Collector<T, ?, Set<T>> customSetCollector() {
        // Implementation: return Collector.of(
        //                    HashSet::new,
        //                    Set::add,
        //                    (set1, set2) -> { set1.addAll(set2); return set1; }
        //                );
    }
    
    /**
     * Complex custom collector
     */
    public static <T, K, V> Collector<T, ?, Map<K, V>> customMapCollector(
                                                Function<T, K> keyMapper,
                                                Function<T, V> valueMapper) {
        // Implementation: return Collector.of(
        //                    HashMap::new,
        //                    (map, t) -> map.put(keyMapper.apply(t), valueMapper.apply(t)),
        //                    (map1, map2) -> { map1.putAll(map2); return map1; }
        //                );
    }
    
    // ============ Advanced Composition ============
    
    /**
     * Teeing: Send stream to multiple collectors
     * Combines results with finisher
     */
    public static <T, R1, R2, R> R teeCollectors(Stream<T> stream,
                                                  Collector<T, ?, R1> collector1,
                                                  Collector<T, ?, R2> collector2,
                                                  BiFunction<R1, R2, R> merger) {
        // Implementation: return stream.collect(Collectors.teeing(collector1, collector2, merger));
    }
    
    /**
     * Example: Get count and list simultaneously
     */
    public static Object[] countAndCollect(Stream<Integer> stream) {
        // Implementation: return stream.collect(
        //                    Collectors.teeing(
        //                        Collectors.counting(),
        //                        Collectors.toList(),
        //                        (count, list) -> new Object[]{count, list}
        //                    )
        //                );
    }
    
    /**
     * Get min and max simultaneously
     */
    public static Object[] minAndMax(Stream<Integer> stream, Comparator<Integer> comp) {
        // Implementation: return stream.collect(
        //                    Collectors.teeing(
        //                        Collectors.minBy(comp),
        //                        Collectors.maxBy(comp),
        //                        (min, max) -> new Object[]{min, max}
        //                    )
        //                );
    }
}
```

---

## PACKAGE 5: PARALLEL STREAMS (2 Classes)

### Class 5.1: ParallelStreamsDemo

**Purpose**: Understand parallel execution model

**Package**: `com.learning.package5_parallel`

```java
public class ParallelStreamsDemo {
    
    // ============ Creating Parallel Streams ============
    
    /**
     * Convert sequential stream to parallel
     */
    public static <T> Stream<T> parallelStream(Stream<T> sequentialStream) {
        // Implementation: return sequentialStream.parallel();
    }
    
    /**
     * Create parallel stream directly from collection
     */
    public static <T> Stream<T> parallelStreamFromCollection(Collection<T> collection) {
        // Implementation: return collection.parallelStream();
    }
    
    /**
     * Create parallel IntStream
     */
    public static IntStream parallelIntStream(int... values) {
        // Implementation: return Arrays.stream(values).parallel();
    }
    
    /**
     * Convert back to sequential
     */
    public static <T> Stream<T> sequentialAgain(Stream<T> parallelStream) {
        // Implementation: return parallelStream.sequential();
    }
    
    // ============ ForkJoin Framework ============
    
    /**
     * Understand ForkJoinPool usage
     */
    public static void demonstrateForkJoinPool() {
        // Implementation: Show that parallel streams use ForkJoinPool.commonPool()
        //                Can observe with ForkJoinPool.getCommonPoolParallelism()
    }
    
    /**
     * Custom ForkJoinPool (advanced)
     */
    public static void customForkJoinPool() throws InterruptedException {
        // Implementation: Create custom ForkJoinPool with specific parallelism
        //                Use ManagedBlocker for blocking ops
    }
    
    /**
     * Verify parallelism level
     */
    public static int getParallelism() {
        // Implementation: return ForkJoinPool.getCommonPoolParallelism();
    }
    
    // ============ Thread Safety ============
    
    /**
     * Parallel with thread-safe collector
     */
    public static <T> List<T> parallelSafeCollect(Stream<T> stream) {
        // Implementation: return stream.parallel()
        //                    .collect(Collectors.toList());  // thread-safe
    }
    
    /**
     * UNSAFE: Non-thread-safe collection
     */
    public static void demonstrateUnsafeParallelCollection() {
        // Implementation: Use non-thread-safe ArrayList in parallel
        //                Can show data loss or corruption
    }
    
    /**
     * Thread safety with concurrent collectors
     */
    public static <T, K> ConcurrentMap<K, List<T>> parallelGrouping(Stream<T> stream,
                                                                     Function<T, K> classifier) {
        // Implementation: return stream.parallel()
        //                    .collect(Collectors.groupingByConcurrent(classifier));
    }
    
    // ============ Stateful Operations in Parallel ============
    
    /**
     * Distinct in parallel (slower than sequential)
     */
    public static <T> Stream<T> parallelDistinct(Stream<T> stream) {
        // Implementation: return stream.parallel().distinct();
        //                Note: Much slower than sequential
    }
    
    /**
     * Sorted in parallel (parallel advantage less clear than sequential)
     */
    public static <T extends Comparable<T>> Stream<T> parallelSorted(Stream<T> stream) {
        // Implementation: return stream.parallel().sorted();
        //                Note: Can be slow due to buffering all elements
    }
    
    /**
     * Why stateful operations hurt parallel performance
     */
    public static void explainStatefulParallelCosts() {
        // Implementation: Explain that stateful ops require synchronization
        //                Negates parallelism benefits
    }
}
```

### Class 5.2: PerformanceComparisonDemo

**Purpose**: Benchmark sequential vs parallel execution

**Package**: `com.learning.package5_parallel`

```java
public class PerformanceComparisonDemo {
    
    // ============ Benchmarking Framework ============
    
    /**
     * Setup performance testing
     */
    public static void setupPerformanceTests() {
        // Implementation: Prepare large datasets for testing
        //                Ensure consistent memory state
    }
    
    // ============ Filter Operation Benchmarks ============
    
    /**
     * Benchmark sequential filter
     */
    public static long benchmarkSequentialFilter(List<Integer> data, Predicate<Integer> predicate) {
        // Implementation: Time sequential filter operation
        //                Warm-up before measuring
        //                Run multiple times
    }
    
    /**
     * Benchmark parallel filter
     */
    public static long benchmarkParallelFilter(List<Integer> data, Predicate<Integer> predicate) {
        // Implementation: Time parallel filter operation
        //                Include ForkJoinPool overhead
    }
    
    // ============ Map Operation Benchmarks ============
    
    /**
     * Benchmark sequential map
     */
    public static long benchmarkSequentialMap(List<Integer> data, Function<Integer, Integer> mapper) {
        // Implementation: Time sequential map
    }
    
    /**
     * Benchmark parallel map
     */
    public static long benchmarkParallelMap(List<Integer> data, Function<Integer, Integer> mapper) {
        // Implementation: Time parallel map
    }
    
    // ============ Collect Benchmarks ============
    
    /**
     * Benchmark sequential collect
     */
    public static long benchmarkSequentialCollect(List<Integer> data) {
        // Implementation: Time sequential collect to List
    }
    
    /**
     * Benchmark parallel collect
     */
    public static long benchmarkParallelCollect(List<Integer> data) {
        // Implementation: Time parallel collect with thread safety
    }
    
    // ============ Pipeline Complexity ============
    
    /**
     * Complex pipeline sequential vs parallel
     */
    public static long benchmarkSequentialComplex(List<Integer> data) {
        // Implementation: Benchmark multi-stage pipeline sequentially
    }
    
    /**
     * Same pipeline in parallel
     */
    public static long benchmarkParallelComplex(List<Integer> data) {
        // Implementation: Benchmark multi-stage pipeline in parallel
    }
    
    // ============ Large Dataset Processing ============
    
    /**
     * Process large dataset (1M+ elements)
     */
    public static void benchmarkLargeDataset(int elementCount) {
        // Implementation: Create large dataset
        //                Show where parallel becomes beneficial
        //                Typically 100K+ elements
    }
    
    /**
     * Small dataset penalty demonstration
     */
    public static void demonstrateSmallDatasetPenalty() {
        // Implementation: Show overhead of parallelism on small datasets
        //                Sequential often faster for < 1000 elements
    }
    
    // ============ Optimal Threshold ============
    
    /**
     * Find optimal parallelization threshold
     * Where parallel becomes faster than sequential
     */
    public static int findOptimalThreshold(Function<Integer, Long> sequentialBench,
                                           Function<Integer, Long> parallelBench) {
        // Implementation: Binary search or linear search for break-even point
        //                Typically 1,000 - 10,000 elements
    }
    
    // ============ System Characteristics ============
    
    /**
     * Print system info for performance analysis
     */
    public static void printSystemCharacteristics() {
        // Implementation: Print:
        //                - Available processors
        //                - JVM memory
        //                - ForkJoinPool parallelism
        //                - JVM version
    }
    
    /**
     * Performance profile by dataset size
     */
    public static void gradientPerformanceAnalysis() {
        // Implementation: Test performance across various dataset sizes
        //                1K, 10K, 100K, 1M elements
        //                Plot sequential vs parallel
    }
}
```

---

## PACKAGE 6: OPTIONAL & ADVANCED (2 Classes)

### Class 6.1: OptionalDemo

**Purpose**: Master null-safe Optional handling

**Package**: `com.learning.package6_advanced`

```java
public class OptionalDemo {
    
   // ============ Creating Optional ============
    
    /**
     * Create Optional with non-null value
     */
    public static <T> Optional<T> createOptionalPresent(T value) {
        // Implementation: return Optional.of(value);
        //                Throws NPE if value is null
    }
    
    /**
     * Create empty Optional
     */
    public static <T> Optional<T> createOptionalEmpty() {
        // Implementation: return Optional.empty();
    }
    
    /**
     * Create Optional from nullable value
     */
    public static <T> Optional<T> createOptionalNullable(T value) {
        // Implementation: return Optional.ofNullable(value);
        //                Returns empty if null, else of(value)
    }
    
    // ============ Interrogating Optional ============
    
    /**
     * Check if Optional contains value
     */
    public static <T> void ifPresent(Optional<T> optional, Consumer<T> action) {
        // Implementation: optional.ifPresent(action);
    }
    
    /**
     * Check if Optional is empty
     */
    public static <T> boolean isEmpty(Optional<T> optional) {
        // Implementation: return optional.isEmpty();  // Java 11+
    }
    
    /**
     * Check if present (old API)
     */
    public static <T> boolean isPresent(Optional<T> optional) {
        // Implementation: return optional.isPresent();
    }
    
    /**
     * If-present-or-else pattern
     */
    public static <T> void ifPresentOrElse(Optional<T> optional, 
                                          Consumer<T> presentAction,
                                          Runnable elseAction) {
        // Implementation: optional.ifPresentOrElse(presentAction, elseAction);
    }
    
    // ============ Safe Access ============
    
    /**
     * Get value or default
     */
    public static <T> T getOrDefault(Optional<T> optional, T defaultValue) {
        // Implementation: return optional.orElse(defaultValue);
    }
    
    /**
     * Get value or compute default
     */
    public static <T> T getOrElseGet(Optional<T> optional, Supplier<T> supplier) {
        // Implementation: return optional.orElseGet(supplier);
        //                Lazy: only computes if empty
    }
    
    /**
     * Get value or throw exception
     */
    public static <T, E extends Exception> T getOrElseThrow(Optional<T> optional,
                                                            Supplier<E> exceptionSupplier) throws E {
        // Implementation: return optional.orElseThrow(exceptionSupplier);
    }
    
    // ============ Transformations ============
    
    /**
     * Transform Optional value with map
     */
    public static <T, R> Optional<R> mapOptional(Optional<T> optional, Function<T, R> mapper) {
        // Implementation: return optional.map(mapper);
        //                Returns empty if optional is empty
    }
    
    /**
     * Transform Optional value with flatMap
     */
    public static <T, R> Optional<R> flatMapOptional(Optional<T> optional,
                                                     Function<T, Optional<R>> mapper) {
        // Implementation: return optional.flatMap(mapper);
        //                Flattens Optional<Optional<R>> -> Optional<R>
    }
    
    /**
     * Filter Optional
     */
    public static <T> Optional<T> filterOptional(Optional<T> optional, Predicate<T> predicate) {
        // Implementation: return optional.filter(predicate);
        //                Returns empty if predicate false
    }
    
    // ============ Chaining Operations ============
    
    /**
     * Multiple transformations on Optional
     */
    public static <T> Optional<String> chainedOperations(Optional<T> optional) {
        // Implementation: return optional
        //                    .map(Object::toString)
        //                    .filter(s -> !s.isEmpty())
        //                    .map(String::toUpperCase);
    }
    
    /**
     * Complex chaining with flatMap
     */
    public static <T> Optional<T> complexChaining(Optional<T> optional, 
                                                  Function<T, Optional<T>> validator) {
        // Implementation: return optional
        //                    .flatMap(validator)
        //                    .filter(t -> t != null);
    }
    
    // ============ Stream Integration ============
    
    /**
     * Convert Optional to Stream
     */
    public static <T> Stream<T> optionalToStream(Optional<T> optional) {
        // Implementation: return optional.stream();
        //                Returns stream of 0 or 1 element
    }
    
    /**
     * Use Optional in stream pipeline
     */
    public static <T> List<T> filterOptionals(Stream<Optional<T>> stream) {
        // Implementation: return stream.flatMap(Optional::stream)
        //                              .collect(Collectors.toList());
    }
    
    // ============ Anti-patterns to Avoid ============
    
    /**
     * Shows incorrect use of Optional.isPresent()
     */
    public static void avoidIsPresent() {
        // Implementation: Bad: optional.isPresent() ? optional.get() : default
        //                Good: optional.orElse(default)
    }
    
    /**
     * Shows NPE risk with Optional.get()
     */
    public static void avoidUnsafeGet() {
        // Implementation: Bad: optional.get() without checking isPresent
        //                Good: orElse, orElseThrow, ifPresent
    }
}
```

### Class 6.2: AdvancedStreamPatterns

**Purpose**: Advanced real-world patterns and techniques

**Package**: `com.learning.package6_advanced`

```java
public class AdvancedStreamPatterns {
    
    // ============ Custom Filters ============
    
    /**
     * Compose multiple predicates with AND logic
     */
    public static <T> Predicate<T> andPredicate(Predicate<T>... predicates) {
        // Implementation: return t -> Arrays.stream(predicates)
        //                    .allMatch(p -> p.test(t));
    }
    
    /**
     * Compose multiple predicates with OR logic
     */
    public static <T> Predicate<T> orPredicate(Predicate<T>... predicates) {
        // Implementation: return t -> Arrays.stream(predicates)
        //                    .anyMatch(p -> p.test(t));
    }
    
    /**
     * Negate a predicate
     */
    public static <T> Predicate<T> not(Predicate<T> predicate) {
        // Implementation: return predicate.negate();
    }
    
    // ============ Lazy Evaluation Patterns ============
    
    /**
     * Demonstrate lazy evaluation benefits
     */
    public static void demonstrateLazyEvaluation() {
        // Implementation: Show that numbers that don't match filter
        //                never go through downstream operations
    }
    
    /**
     * Infinite stream with lazy limiting
     */
    public static void lazyInfiniteStream() {
        // Implementation: Create infinite stream but limit evaluation
        //                Without limit, would run forever
    }
    
    // ============ Custom Generators ============
    
    /**
     * Generate infinite sequence from seed
     */
    public static <T> Stream<T> infiniteStream(T seed, Function<T, T> generator) {
        // Implementation: return Stream.iterate(seed, generator);
    }
    
    /**
     * Fibonacci sequence
     */
    public static Stream<Long> fibonacciStream(int limit) {
        // Implementation: Generate [0, 1, 1, 2, 3, 5, 8, ...]
    }
    
    /**
     * Powers of 2
     */
    public static Stream<Integer> powersOfTwo(int limit) {
        // Implementation: Generate [1, 2, 4, 8, 16, ...]
    }
    
    /**
     * Random number stream
     */
    public static Stream<Integer> randomNumberStream(int limit) {
        // Implementation: Generate random numbers using generator
    }
    
    // ============ Stream Splitting ============
    
    /**
     * Split stream into two based on predicate
     */
    public static <T> Pair<List<T>, List<T>> splitStream(Stream<T> stream, Predicate<T> condition) {
        // Implementation: Use partition collector
        //                Map<Boolean, List<T>>  -> two lists
    }
    
    /**
     * Take and skip pattern
     */
    public static <T> Pair<List<T>, List<T>> takeAndSkip(Stream<T> stream, long takeCount) {
        // Implementation: Convert to list, split at index
        //                Avoid consuming stream twice
    }
    
    // ============ Stream Merging ============
    
    /**
     * Merge multiple streams
     */
    @SafeVarargs
    public static <T> Stream<T> mergeStreams(Stream<T>... streams) {
        // Implementation: return Arrays.stream(streams)
        //                    .flatMap(Function.identity());
    }
    
    /**
     * Concat with order preservation
     */
    public static <T> Stream<T> concatStreamSequence(Stream<T>... streams) {
        // Implementation: return Stream.of(streams)
        //                    .flatMap(Function.identity());
    }
    
    // ============ Windowing ============
    
    /**
     * Sliding window operation
     */
    public static <T> Stream<List<T>> windowStream(Stream<T> stream, int windowSize) {
        // Implementation: Complex - need custom collector/iterator
        //                [1,2,3,4,5] with window 2 -> [[1,2], [2,3], [3,4], [4,5]]
    }
    
    /**
     * Grouped window (non-sliding)
     */
    public static <T> Stream<List<T>> groupedWindowStream(Stream<T> stream, int groupSize) {
        // Implementation: [1,2,3,4,5] with group 2 -> [[1,2], [3,4], [5]]
    }
    
    // ============ Resource Management ============
    
    /**
     * Stream with automatic resource cleanup
     */
    public static <R extends AutoCloseable, T> Stream<T> tryWithResourcesStream(
                                                    Supplier<R> resourceSupplier,
                                                    Function<R, Stream<T>> streamSupplier) throws Exception {
        // Implementation: Create resource, get stream, ensure close on completion
        //                Use onClose() to register cleanup
    }
    
    /**
     * Safe file stream with cleanup
     */
    public static Stream<String> safeFileStream(Path filePath) throws IOException {
        // Implementation: return Files.lines(filePath)
        //                              .onClose(() -> System.out.println("File closed"));
    }
}
```

---

**Document Version**: 1.0  
**Total Classes**: 17 demonstration classes  
**Total Methods**: 200+ method specifications  
**Status**: Complete & Ready for Implementation

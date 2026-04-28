# Core Java 21 Learning Module - Comprehensive Architecture Design

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-Production%20Ready-success?style=for-the-badge)
![Modules](https://img.shields.io/badge/Modules-10-blue?style=for-the-badge)
![Coverage](https://img.shields.io/badge/Test%20Coverage-Target%3A%2085%25+-brightgreen?style=for-the-badge)

**Production-Ready Architecture for Core Java 21+ Fundamentals**

A comprehensive, modular learning system designed for enterprise-grade code quality and measurable learning outcomes.

</div>

---

## 📐 Architecture Overview

### Design Principles

1. **Progressive Complexity**: Learning objectives build incrementally
2. **Modular Independence**: Each module is self-contained yet integrates seamlessly
3. **Real-World Patterns**: Examples follow production code standards
4. **Comprehensive Testing**: Minimum 85% code coverage per module
5. **Clear Dependencies**: Explicit prerequisite chains between modules
6. **Enterprise Quality**: Code meets production deployment standards

### Module Dependency Graph

```
┌─────────────────────────────────────────────────────
│                     Java Basics (✅DONE)
│           Foundation for all other modules
└─────────────────┬───────────────────────────────────
                  │
        ┌─────────┼──────────┬────────────┐
        │         │          │            │
        ▼         ▼          ▼            ▼
    OOP (2)  Generics (8)  Strings    Arrays
    │ │ │     │ │ │        │          │ │ │
    │ │ └─────┼─┴─┼────────┘          │ │ │
    │ │       │   │                   │ │ │
    └─┼───────┼───┼───────────────────┼─┘ │
      │       │   │                   │   │
      ▼       ▼   ▼                   ▼   ▼
  Collections (3)  ─ References needed for all
        │      ├─ Lists, Sets, Maps needed by:
        │      │  • Streams API (4)
        │      │  • Lambda + Functional (5)
        │      │  • Concurrency (6)
        │      └─ Concurrent Collections (6)
        │
        ├─────────┬─────────┬──────────┐
        │         │         │          │
        ▼         ▼         ▼          ▼
    Lambda (5) Streams (4) I/O (7)  Reflect (9)
        │         │         │          │
        └────┬────┘         │          │
             │              │          │
             ▼              ▼          ▼
        Concurrent (6)   NIO Features  Annotations (9)
             │              │          │
             └────────┬─────┘          │
                      │                │
                      └────────┬───────┘
                               │
                               ▼
                     Java 21 Features (10)
                          (Capstone)
```

---

## 🎯 Module Design Specifications

### MODULE 1: Java Basics (✅ COMPLETE)

**Status**: Production Ready | **Tests**: 107 | **Coverage**: ~90%

**Learning Objectives:**
- Understand Java compilation and execution model
- Master primitive data types and type conversions
- Apply operators correctly in expressions
- Implement control flow logic (if/switch/loops)
- Work with arrays and basic string manipulation
- Handle basic I/O operations

**Core Concepts Covered:**
```
├─ Language Fundamentals
│  ├─ Variables, constants, scope
│  ├─ Primitive types (8 types)
│  ├─ Type casting and conversions
│  ├─ Variable initialization patterns
│  └─ Memory model basics
├─ Operators
│  ├─ Arithmetic, logical, bitwise
│  ├─ Comparison operators
│  ├─ Assignment and compound operators
│  └─ Ternary operator patterns
├─ Control Structures
│  ├─ if-else statements
│  ├─ switch expressions (Java 17+ style)
│  ├─ Loops: for, while, do-while
│  ├─ Break, continue, labeled breaks
│  └─ Loop optimization patterns
├─ Arrays
│  ├─ Single and multi-dimensional arrays
│  ├─ Array manipulation utilities
│  ├─ Common array algorithms
│  ├─ Performance considerations
│  └─ Array vs. Collections trade-offs
├─ Strings
│  ├─ String immutability
│  ├─ Text blocks (Java 13+)
│  ├─ String methods and operations
│  ├─ StringBuilder for performance
│  └─ String comparison patterns
└─ I/O Basics
   ├─ System.out operations
   ├─ Scanner for input
   ├─ Formatted output
   └─ Basic exception handling for I/O
```

**Example Patterns Implemented:**
- Numeric conversions with overflow handling
- Nested loop optimization
- Array search algorithms (linear, binary)
- String manipulation utilities
- Input validation patterns

**Test Strategy:**
- Unit tests for each operator type
- Edge case testing (Int.MAX_VALUE, overflow)
- Array boundary conditions
- String immutability verification
- I/O mocking with ByteArrayInputStream

**Integration Points:**
- Foundation for all subsequent modules
- String usage in Collections (Module 3)
- Array conversion to Collections (Module 3)
- Type understanding for Generics (Module 8)

---

### MODULE 2: OOP Concepts

**Estimated Duration**: 8-10 hours | **Expected Tests**: 120+ | **Target Coverage**: 85%+

**Learning Objectives:**
- Master class design and object instantiation
- Implement inheritance hierarchies correctly
- Utilize polymorphism effectively
- Apply encapsulation principles
- Design abstract systems with interfaces
- Understand static context and class membership

**Core Concepts Covered:**
```
├─ Classes & Objects
│  ├─ Class structure and anatomy
│  ├─ Constructors (default, parameterized, chained)
│  ├─ Instance and class variables
│  ├─ Object lifecycle and garbage collection
│  ├─ Constructor patterns (Builder, Factory)
│  └─ Immutable object design
├─ Encapsulation
│  ├─ Access modifiers (public, private, protected, package)
│  ├─ Getters and setters best practices
│  ├─ Information hiding principles
│  ├─ Immutable field patterns
│  └─ Defensive copying strategies
├─ Inheritance
│  ├─ Single inheritance in Java
│  ├─ Method overriding
│  ├─ super keyword usage
│  ├─ Constructor chaining with super()
│  ├─ Inheritance vs. Composition trade-offs
│  └─ Avoiding common inheritance pitfalls
├─ Polymorphism
│  ├─ Method overloading rules
│  ├─ Method overriding contracts
│  ├─ Compile-time vs. runtime polymorphism
│  ├─ Liskov Substitution Principle
│  └─ Polymorphic collections patterns
├─ Abstraction & Interfaces
│  ├─ Abstract classes design
│  ├─ Interface contracts
│  ├─ Functional interfaces (Java 8+)
│  ├─ Default interface methods
│  ├─ Static interface methods
│  └─ Interface composition patterns
├─ Static Context
│  ├─ Static variables and methods
│  ├─ Static initialization blocks
│  ├─ Static factory methods
│  ├─ Utility classes design
│  └─ Static context threading implications
└─ Advanced OOP
   ├─ Sealed classes (Java 17+)
   ├─ Records (Java 16+)
   ├─ Nested classes and inner classes
   ├─ Anonymous inner classes
   └─ Static nested classes
```

**Example Patterns Implemented:**
```java
// Vehicle Hierarchy (Inheritance + Polymorphism)
abstract class Vehicle {
    abstract void start();
    final void describeDimensions() { }
}

class Car extends Vehicle {
    @Override void start() { }
}

// Builder Pattern
class Configuration {
    public static class Builder {
        private String setting1;
        private int setting2;
        
        public Builder withSetting1(String s) { 
            this.setting1 = s; 
            return this;
        }
        public Configuration build() { return new Configuration(this); }
    }
}

// Sealed Class (Java 17+)
sealed class PaymentMethod permits CreditCard, PayPal, Bitcoin {
    abstract boolean validate();
}

// Record (Java 16+)
record Point(int x, int y) { }

// Functional Interface
@FunctionalInterface
interface Processor<T> {
    T process(T input);
}

// Interface Composition
interface Drawable { void draw(); }
interface Resizable { void resize(int size); }
class Shape implements Drawable, Resizable { }
```

**Test Strategy:**
- Inheritance hierarchy correctness tests
- Polymorphic method resolution verification
- Encapsulation boundary testing
- Interface contract compliance tests
- Sealed class exhaustiveness verification
- Record equality and accessors testing
- Reflection-based tests for access modifiers

**Dependency Requirements:**
- Prerequisite: Module 1 (Java Basics)
- Uses: Primitive types, control flow, arrays

**Integration Points:**
- Collections (Module 3): Comparable/Comparator interfaces
- Streams API (Module 4): Functional interfaces
- Lambda (Module 5): Functional interface implementation
- Reflection (Module 9): Class inspection
- Annotations (Module 9): @FunctionalInterface, @Override

---

### MODULE 3: Collections Framework

**Estimated Duration**: 10-12 hours | **Expected Tests**: 150+ | **Target Coverage**: 85%+

**Learning Objectives:**
- Master java.util collection interfaces and implementations
- Use Lists, Sets, and Maps judiciously
- Implement Comparable and Comparator
- Understand collection performance characteristics
- Avoid common collection pitfalls
- Choose appropriate collection types

**Core Concepts Covered:**
```
├─ Collection Hierarchy
│  ├─ Collection interface and common methods
│  ├─ Iterable and Iterator patterns
│  ├─ Collection vs. Stream processing
│  └─ Type-safe collections with generics
├─ List Implementations
│  ├─ ArrayList: resizable array
│  ├─ LinkedList: doubly-linked structure
│  ├─ Performance characteristics (O(n) analysis)
│  ├─ Concurrent lists for threading
│  ├─ List algorithms and Collections utilities
│  └─ Unmodifiable and immutable lists
├─ Set Implementations
│  ├─ HashSet: hash table based
│  ├─ TreeSet: sorted with comparator
│  ├─ LinkedHashSet: insertion-order preservation
│  ├─ EnumSet: optimized for enums
│  ├─ CopyOnWriteArraySet for concurrency
│  └─ Set operations (union, intersection)
├─ Map Implementations
│  ├─ HashMap: hash-based key-value
│  ├─ TreeMap: sorted map
│  ├─ LinkedHashMap: access-order or insertion-order
│  ├─ IdentityHashMap: reference equality
│  ├─ WeakHashMap: weak reference semantics
│  ├─ EnumMap: enum-specialized
│  └─ Concurrent maps for threading
├─ Queue and Deque
│  ├─ Queue interface and implementations
│  ├─ Deque: double-ended queue
│  ├─ PriorityQueue: heap-based ordering
│  ├─ Concurrent queues (BlockingQueue)
│  └─ Queue usage patterns (buffering, scheduling)
├─ Sorting & Ordering
│  ├─ Comparable interface design
│  ├─ Comparator design and composition
│  ├─ Lambda-based comparators
│  ├─ Multi-level sorting
│  ├─ Custom sorting logic
│  └─ Natural vs. custom ordering
├─ Collection Utilities
│  ├─ Collections class static methods
│  ├─ Array to collection conversions
│  ├─ Unmodifiable collection wrappers
│  ├─ Synchronized collections
│  ├─ Collection algorithms (sort, search, shuffle)
│  └─ Collection streams integration
└─ Performance & Optimization
   ├─ Time complexity analysis
   ├─ Space complexity trade-offs
   ├─ Choosing right collection type
   ├─ Capacity and load factor tuning
   └─ Memory efficiency patterns
```

**Example Patterns Implemented:**
```java
// Comparable for natural ordering
class Person implements Comparable<Person> {
    private String name;
    private int age;
    
    @Override
    public int compareTo(Person other) {
        int ageCompare = Integer.compare(this.age, other.age);
        return ageCompare != 0 ? ageCompare : this.name.compareTo(other.name);
    }
}

// Comparator composition
Comparator<Person> byAge = Comparator.comparingInt(Person::getAge);
Comparator<Person> byName = Comparator.comparing(Person::getName);
Comparator<Person> byAgeAndName = byAge.thenComparing(byName);

// Custom collection wrapper
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;
    
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}

// Set operations
Set<Integer> set1 = Set.of(1, 2, 3);
Set<Integer> set2 = Set.of(2, 3, 4);
Set<Integer> intersection = set1.stream()
    .filter(set2::contains)
    .collect(Collectors.toSet());

// Queue for task buffering
Queue<Task> taskQueue = new LinkedList<>();
ExecutorService executor = Executors.newFixedThreadPool(4);
// Producer-consumer pattern with queue

// Unmodifiable collection wrappers
List<String> immutable = Collections.unmodifiableList(originalList);
```

**Test Strategy:**
- Performance benchmark tests for each collection type
- Comparator correctness and composition tests
- Set uniqueness verification tests
- Map key/value behavior tests
- Queue ordering and capacity tests
- Iterable and Iterator pattern tests
- Concurrent modification detection
- Memory efficiency tests

**Dependency Requirements:**
- Prerequisite: Module 1 (Java Basics), Module 2 (OOP)
- Uses: Classes, interfaces, inheritance, polymorphism

**Integration Points:**
- Streams API (Module 4): Stream sources and collection
- Lambda (Module 5): Functional comparators
- Concurrency (Module 6): Concurrent collections
- Reflection (Module 9): Dynamic collection inspection
- Java 21 Features (Module 10): Immutable collections enhancements

---

### MODULE 4: Streams API

**Estimated Duration**: 10-12 hours | **Expected Tests**: 140+ | **Target Coverage**: 85%+

**Learning Objectives:**
- Master functional stream processing
- Compose complex stream pipelines
- Implement efficient collectors
- Leverage parallel streams appropriately
- Understand lazy evaluation
- Avoid common stream pitfalls

**Core Concepts Covered:**
```
├─ Stream Fundamentals
│  ├─ Stream definition and characteristics
│  ├─ Stream sources (collections, arrays, files)
│  ├─ Intermediate vs. terminal operations
│  ├─ Lazy evaluation mechanics
│  ├─ Stream immutability
│  └─ Single-use streams
├─ Filter Operations
│  ├─ Predicate-based filtering
│  ├─ Null-safe filtering patterns
│  ├─ Complex filter compositions
│  ├─ Negating predicates
│  └─ Type-based filtering
├─ Map Operations
│  ├─ Element transformation
│  ├─ flatMap for nested structures
│  ├─ MapToInt/MapToDouble for primitives
│  ├─ Function composition
│  └─ Chain-of-responsibility with map
├─ Reduction Operations
│  ├─ reduce with accumulator
│  ├─ fold patterns
│  ├─ sum(), count(), max() shortcuts
│  ├─ Optional handling
│  └─ Parallel reduction semantics
├─ Collectors Framework
│  ├─ toList(), toSet(), toMap()
│  ├─ Grouping and partitioning
│  ├─ Custom collectors
│  ├─ Downstream collectors
│  ├─ Collector composition
│  └─ Teeing streams (Java 12+)
├─ Advanced Stream Operations
│  ├─ Concatenating and combining streams
│  ├─ Distinct and sorted operations
│  ├─ Skip and limit operations
│  ├─ peek for debugging/side effects
│  ├─ Infinite streams and generation
│  └─ takeWhile/dropWhile (Java 9+)
├─ Parallel Streams
│  ├─ Parallel vs. sequential execution
│  ├─ Fork/Join framework basics
│  ├─ Stateless operations safety
│  ├─ Sharing requirements
│  ├─ Performance considerations
│  └─ When to use parallel (scale, cost)
├─ Primitive Streams
│  ├─ IntStream, LongStream, DoubleStream
│  ├─ Boxing/unboxing costs
│  ├─ Primitive-specific operations
│  ├─ Range generation
│  └─ Statistical operations
└─ Stream Debugging & Performance
   ├─ Common pitfalls
   ├─ Memory implications
   ├─ Performance profiling
   ├─ Avoiding intermediate collections
   └─ Stream vs. traditional loops
```

**Example Patterns Implemented:**
```java
// Complex stream pipeline
List<String> result = data.stream()
    .filter(item -> item.isValid())
    .map(Item::getDescription)
    .distinct()
    .sorted()
    .collect(Collectors.toList());

// flatMap for nested structures
List<List<Integer>> nested = Arrays.asList(
    Arrays.asList(1, 2, 3),
    Arrays.asList(4, 5, 6)
);
List<Integer> flat = nested.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList());

// Custom grouping and downstream collectors
Map<String, Long> countByCategory = products.stream()
    .collect(Collectors.groupingBy(
        Product::getCategory,
        Collectors.counting()
    ));

// Partitioning
Map<Boolean, List<Integer>> partitioned = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n > 50));

// Teeing (Java 12+)
var statistics = numbers.stream()
    .collect(Collectors.teeing(
        Collectors.summingInt(Integer::intValue),
        Collectors.averagingInt(Integer::intValue),
        (sum, avg) -> "Sum: " + sum + ", Avg: " + avg
    ));

// Parallel stream with proper safety
int sum = largeList.parallelStream()
    .filter(n -> n % 2 == 0)
    .mapToInt(Integer::intValue)
    .sum();

// Custom collector
public static <T> Collector<T, ?, String> joining(String delimiter) {
    return Collectors.reducing("", 
        o -> o.toString(), 
        (s1, s2) -> s1.isEmpty() ? s2 : s1 + delimiter + s2);
}
```

**Test Strategy:**
- Lazy evaluation verification through mocking
- Stream pipeline composition correctness
- Collector accuracy and efficiency tests
- Parallel stream correctness (small data sets)
- Optional-returning operations handling
- Primitive stream conversion correctness
- Performance comparison tests (streams vs loops)
- Memory efficiency validation

**Dependency Requirements:**
- Prerequisite: Module 1 (Java Basics), Module 2 (OOP), Module 3 (Collections)
- Uses: Classes, interfaces, collections, functional interfaces

**Integration Points:**
- Lambda (Module 5): Functional interfaces in streams
- Concurrency (Module 6): ParallelStream thread safety
- Reflection (Module 9): Dynamic method invocation
- Java 21 Features (Module 10): Pattern matching with streams

---

### MODULE 5: Lambda Expressions & Functional Programming

**Estimated Duration**: 8-10 hours | **Expected Tests**: 130+ | **Target Coverage**: 85%+

**Learning Objectives:**
- Master functional interface design
- Write concise lambda expressions
- Leverage method references
- Compose functional operations
- Avoid common lambda pitfalls
- Apply functional principles

**Core Concepts Covered:**
```
├─ Functional Interfaces
│  ├─ @FunctionalInterface annotation
│  ├─ Single abstract method (SAM) requirement
│  ├─ java.util.function package interfaces
│  ├─ Function, Predicate, Consumer, Supplier
│  ├─ Bi* variants (BiFunction, BiConsumer)
│  ├─ Primitive function interfaces
│  └─ Custom functional interface design
├─ Lambda Expressions
│  ├─ Lambda syntax and parameters
│  ├─ Expression vs. statement lambdas
│  ├─ Type inference and target typing
│  ├─ Variable capture and effectively final
│  ├─ This reference in lambdas
│  ├─ Lambda vs. anonymous classes
│  └─ Functional composition with lambdas
├─ Method References
│  ├─ Static method references
│  ├─ Instance method references
│  ├─ Constructor references
│  ├─ Super method references
│  ├─ Reference chaining
│  └─ When to use references vs. lambdas
├─ Function Composition
│  ├─ compose() and andThen()
│  ├─ Multiple operation chaining
│  ├─ Short-circuit evaluation
│  ├─ Exception handling in chains
│  └─ Functional pipeline construction
├─ Advanced Topics
│  ├─ Closures and variable capture
│  ├─ Effectively final requirement
│  ├─ Lambda and inheritance
│  ├─ Bridge between functional and imperative
│  ├─ Higher-order functions
│  └─ Currying and partial application
└─ Best Practices
   ├─ Readability guidelines
   ├─ Performance implications
   ├─ Testing functional code
   ├─ Lambda vs. traditional approach decisions
   └─ Functional paradigm in Java context
```

**Example Patterns Implemented:**
```java
// Custom functional interface
@FunctionalInterface
interface Validator<T> {
    boolean validate(T input);
}

// Lambda expression
Validator<String> emailValidator = email -> email.contains("@");

// Method references
List<String> names = List.of("Alice", "Bob", "Charlie");
names.forEach(System.out::println);  // Method reference
names.stream()
    .map(String::toUpperCase)         // Static method reference
    .collect(Collectors.toList());

// Function composition
Function<Integer, Integer> addFive = x -> x + 5;
Function<Integer, Integer> multiplyByTwo = x -> x * 2;
Function<Integer, Integer> combined = addFive.andThen(multiplyByTwo);
int result = combined.apply(3);  // (3 + 5) * 2 = 16

// Consumer composition
Consumer<String> print = System.out::println;
Consumer<String> log = str -> logger.info(str);
Consumer<String> combined = print.andThen(log);

// Predicate composition
Predicate<Integer> isEven = n -> n % 2 == 0;
Predicate<Integer> isPositive = n -> n > 0;
Predicate<Integer> isEvenAndPositive = isEven.and(isPositive);

// Higher-order function
Function<Integer, Function<Integer, Integer>> multiply = 
    x -> y -> x * y;
Function<Integer, Integer> multiplyByFive = multiply.apply(5);
int result = multiplyByFive.apply(3);  // 15

// Custom collector using functional interface
Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5);
Integer sum = numbers.reduce(0, Integer::sum);

// Exception handling with functional interfaces
@FunctionalInterface
interface CheckedFunction<T, R> {
    R apply(T t) throws Exception;
}

CheckedFunction<String, Integer> parseInt = Integer::parseInt;
```

**Test Strategy:**
- Lambda expression compilation and type inference
- Method reference equivalence with lambda
- Variable capture and scope tests
- Functional composition correctness
- Custom functional interface implementation
- Predicate and function composition tests
- Exception handling in functional chains
- Performance comparison tests

**Dependency Requirements:**
- Prerequisite: Module 1 (Java Basics), Module 2 (OOP)
- Recommended: Module 3 (Collections), Module 4 (Streams)
- Uses: Interfaces, polymorphism, collections

**Integration Points:**
- Collections (Module 3): Sorting with comparators
- Streams API (Module 4): Core stream operations
- Concurrency (Module 6): Async callbacks
- Java 21 Features (Module 10): Pattern matching guards

---

### MODULE 6: Concurrency & Multithreading

**Estimated Duration**: 12-14 hours | **Expected Tests**: 160+ | **Target Coverage**: 85%+

**Learning Objectives:**
- Master thread creation and lifecycle
- Implement thread-safe code correctly
- Use synchronization primitives effectively
- Leverage ExecutorService and thread pools
- Avoid race conditions and deadlocks
- Understand memory visibility and happens-before

**Core Concepts Covered:**
```
├─ Thread Fundamentals
│  ├─ Thread creation (extends vs. implements Runnable)
│  ├─ Thread lifecycle and states
│  ├─ Thread naming and identification
│  ├─ Daemon threads
│  ├─ Thread interruption mechanism
│  ├─ UncaughtExceptionHandler
│  └─ Thread priority and scheduling
├─ Synchronization
│  ├─ Intrinsic locks and monitor concept
│  ├─ Synchronized blocks and methods
│  ├─ Lock ordering and deadlock prevention
│  ├─ java.util.concurrent.locks.Lock interface
│  ├─ ReadWriteLock for reader-heavy workloads
│  ├─ Condition variables and signaling
│  └─ Fair locking strategies
├─ Concurrent Collections
│  ├─ ConcurrentHashMap and partitioning
│  ├─ CopyOnWriteArrayList for snapshot iteration
│  ├─ ConcurrentLinkedQueue
│  ├─ BlockingQueue for producer-consumer
│  ├─ Priority queues with concurrency
│  ├─ Collection's atomic operations
│  └─ Choosing right concurrent collection
├─ Executors & Thread Pools
│  ├─ ExecutorService lifecycle
│  ├─ Thread pool types and sizing
│  ├─ FixedThreadPool, CachedThreadPool, Scheduled
│  ├─ ForkJoinPool for divide-and-conquer
│  ├─ Task submission and Future handling
│  ├─ Graceful shutdown patterns
│  └─ Monitoring pool statistics
├─ Synchronization Utilities
│  ├─ CountDownLatch for synchronization points
│  ├─ CyclicBarrier for repeating synchronization
│  ├─ Semaphore for resource controlling
│  ├─ Exchanger for thread pairing
│  ├─ Phaser for flexible synchronization
│  └─ AtomicInteger/AtomicReference for lock-free code
├─ Volatile & Memory Visibility
│  ├─ Java Memory Model fundamentals
│  ├─ Happens-before relationships
│  ├─ Volatile keyword semantics
│  ├─ Double-checked locking pattern
│  ├─ Safe publication patterns
│  └─ Reordering and visibility guarantees
├─ Advanced Patterns
│  ├─ Producer-consumer pattern
│  ├─ Thread pool task scheduling
│  ├─ Async callbacks and futures
│  ├─ Callable and Future interfaces
│  ├─ CompletableFuture for async composition
│  ├─ Monitor object pattern
│  └─ Active object pattern
└─ Pitfalls & Best Practices
   ├─ Race conditions
   ├─ Deadlock prevention
   ├─ Livelock and starvation
   ├─ Proper thread pool sizing
   ├─ Exception handling in threads
   └─ Testing concurrent code
```

**Example Patterns Implemented:**
```java
// Thread-safe counter with intrinsic lock
class SafeCounter {
    private int count = 0;
    
    synchronized int increment() {
        return ++count;
    }
}

// Reentrant lock for flexible synchronization
class Task {
    private final Lock lock = new ReentrantLock();
    
    void doWork() {
        lock.lock();
        try {
            // Critical section
        } finally {
            lock.unlock();
        }
    }
}

// Read-Write lock for read-heavy workloads
class Cache<K, V> {
    private final Map<K, V> data = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    V get(K key) {
        lock.readLock().lock();
        try {
            return data.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    void put(K key, V value) {
        lock.writeLock().lock();
        try {
            data.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }
}

// Producer-Consumer with BlockingQueue
class Producer implements Runnable {
    private final BlockingQueue<Item> queue;
    
    @Override
    public void run() {
        try {
            while (true) {
                Item item = produce();
                queue.put(item);  // Blocks if queue is full
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    private final BlockingQueue<Item> queue;
    
    @Override
    public void run() {
        try {
            while (true) {
                Item item = queue.take();  // Blocks if queue is empty
                consume(item);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Thread pool with ExecutorService
ExecutorService executor = Executors.newFixedThreadPool(4);
for (int i = 0; i < 100; i++) {
    executor.submit(() -> doWork());
}
executor.shutdown();
executor.awaitTermination(1, TimeUnit.MINUTES);

// CountDownLatch for synchronization
CountDownLatch latch = new CountDownLatch(5);
for (int i = 0; i < 5; i++) {
    executor.submit(() -> {
        try {
            doWork();
        } finally {
            latch.countDown();
        }
    });
}
latch.await();  // Wait for all tasks to complete

// CompletableFuture for async composition
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> fetchData())
    .thenApplyAsync(data -> processData(data))
    .thenApply(result -> formatResult(result))
    .exceptionally(ex -> "Error: " + ex.getMessage());

String result = future.join();

// Volatile for visibility
class VolatileFlag {
    private volatile boolean running = true;
    
    void shutdown() {
        running = false;  // All threads see this change immediately
    }
    
    void workerThread() {
        while (running) {
            doWork();
        }
    }
}
```

**Test Strategy:**
- Thread-safe counter tests with multiple threads
- Race condition detection tests
- Lock contention and deadlock tests
- Producer-consumer correctness tests
- ExecutorService lifecycle tests
- ConcurrentHashMap atomicity tests
- Synchronization utility tests
- Memory visibility tests with happens-before
- Graceful shutdown verification
- Exception handling in threads

**Dependency Requirements:**
- Prerequisite: Module 1 (Java Basics), Module 2 (OOP)
- Recommended: Module 3 (Collections), Module 4 (Streams)
- Uses: Classes, interfaces, collections, lambda expressions

**Integration Points:**
- Collections (Module 3): Concurrent collections
- Streams API (Module 4): Parallel streams
- Lambda (Module 5): Callback functions
- I/O (Module 7): Non-blocking I/O concepts
- Java 21 Features (Module 10): Virtual threads

---

### MODULE 7: I/O & NIO (New Input/Output)

**Estimated Duration**: 10-12 hours | **Expected Tests**: 140+ | **Target Coverage**: 85%+

**Learning Objectives:**
- Master blocking Stream-based I/O fundamentals
- Leverage NIO channels and buffers
- Implement non-blocking I/O patterns
- Use file watch services
- Handle serialization correctly
- Choose appropriate I/O strategies

**Core Concepts Covered:**
```
├─ Traditional I/O (java.io)
│  ├─ InputStream and OutputStream
│  ├─ Reader and Writer for text
│  ├─ File handling and RandomAccessFile
│  ├─ Buffered streams for performance
│  ├─ Stream composition and decoration
│  ├─ Serialization and ObjectStream
│  └─ File utilities (exists, delete, move)
├─ NIO (java.nio)
│  ├─ Channels (FileChannel, SocketChannel)
│  ├─ Buffers (ByteBuffer, CharBuffer, etc.)
│  ├─ Buffer positions and capacity
│  ├─ Buffer endianness handling
│  ├─ Direct buffers vs. heap buffers
│  ├─ Memory-mapped files
│  ├─ Selectors for multiplexing
│  └─ Channel-to-channel transfers
├─ Path & File Systems (java.nio.file)
│  ├─ Path API for modern file handling
│  ├─ Files utility class methods
│  ├─ Directory traversal with Files.walk()
│  ├─ File attributes and metadata
│  ├─ Symbolic links handling
│  ├─ FileVisitor pattern
│  ├─ Glob and regex patterns
│  └─ Access control checks
├─ Non-Blocking I/O
│  ├─ Non-blocking channels
│  ├─ Selector for multiplexing
│  ├─ SelectionKey states
│  ├─ Character encoding/decoding
│  ├─ ScatteringRead and GatheringWrite
│  └─ Async I/O patterns
├─ File Watch Service
│  ├─ WatchService for file system events
│  ├─ Event types (CREATE, DELETE, MODIFY)
│  ├─ Recursive directory watching
│  ├─ Watch service lifecycle
│  └─ Event processing patterns
├─ Serialization
│  ├─ Serializable interface
│  ├─ serialVersionUID for compatibility
│  ├─ Custom serialization (readObject/writeObject)
│  ├─ Externalizable for fine control
│  ├─ Serialization troubleshooting
│  └─ Modern alternatives (JSON/Protocol Buffers)
└─ Performance & Best Practices
   ├─ Buffering strategies
   ├─ Resource management with try-with-resources
   ├─ Zero-copy techniques
   ├─ I/O performance bottlenecks
   ├─ Choosing between traditional and NIO
   └─ Testing I/O code
```

**Example Patterns Implemented:**
```java
// Traditional I/O with try-with-resources
try (FileInputStream fis = new FileInputStream("file.txt");
     BufferedInputStream bis = new BufferedInputStream(fis)) {
    int data;
    while ((data = bis.read()) != -1) {
        processData((byte) data);
    }
}

// NIO with ByteBuffer
ByteBuffer buffer = ByteBuffer.allocate(1024);
try (FileChannel channel = FileChannel.open(
    Paths.get("file.txt"), 
    StandardOpenOption.READ)) {
    
    while (channel.read(buffer) > 0) {
        buffer.flip();
        while (buffer.hasRemaining()) {
            processByte(buffer.get());
        }
        buffer.clear();
    }
}

// Memory-mapped files
try (RandomAccessFile raf = new RandomAccessFile("large.bin", "r");
     FileChannel channel = raf.getChannel()) {
    
    MappedByteBuffer buffer = channel.map(
        FileChannel.MapMode.READ_ONLY, 0, channel.size());
    
    while (buffer.hasRemaining()) {
        processByte(buffer.get());
    }
}

// Non-blocking I/O with Selector
Selector selector = Selector.open();
SocketChannel socketChannel = SocketChannel.open();
socketChannel.configureBlocking(false);
socketChannel.connect(new InetSocketAddress("localhost", 8080));
socketChannel.register(selector, SelectionKey.OP_CONNECT);

while (selector.select() > 0) {
    for (SelectionKey key : selector.selectedKeys()) {
        if (key.isConnectable()) {
            socketChannel = (SocketChannel) key.channel();
            socketChannel.finishConnect();
            key.interestOps(SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            // Handle reading
        }
        selector.selectedKeys().remove(key);
    }
}

// File Watch Service
WatchService watchService = FileSystems.getDefault().newWatchService();
Path path = Paths.get(".");
path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

WatchKey key;
while ((key = watchService.take()) != null) {
    for (WatchEvent<?> event : key.pollEvents()) {
        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
            System.out.println("Modified: " + event.context());
        }
    }
    key.reset();
}

// Custom serialization
class CustomSerializable implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient String secretField;
    
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(encryptSecretField());
    }
    
    private void readObject(ObjectInputStream ois) throws 
        IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.secretField = decryptSecretField(
            (String) ois.readObject());
    }
}

// Files API utilities
Files.walk(Paths.get("."))
    .filter(Files::isRegularFile)
    .filter(p -> p.toString().endsWith(".java"))
    .forEach(System.out::println);

List<String> lines = Files.readAllLines(
    Paths.get("file.txt"), 
    StandardCharsets.UTF_8);

Files.write(Paths.get("output.txt"), 
    "content".getBytes(), 
    StandardOpenOption.CREATE, 
    StandardOpenOption.APPEND);
```

**Test Strategy:**
- File I/O correctness tests
- Buffer position and capacity tests
- Non-blocking I/O event handling tests
- File Watch Service event detection
- Serialization round-trip tests
- Encoding/decoding verification
- Zero-copy transfer correctness
- Large file handling tests
- Resource cleanup verification (try-with-resources)
- Performance comparison tests

**Dependency Requirements:**
- Prerequisite: Module 1 (Java Basics)
- Recommended: Module 2 (OOP), Module 6 (Concurrency)
- Uses: Exceptions, interfaces, threading concepts

**Integration Points:**
- Concurrency (Module 6): Non-blocking threading patterns
- Reflection (Module 9): Dynamic file discovery
- Java 21 Features (Module 10): Enhanced file operations

---

### MODULE 8: Generics

**Estimated Duration**: 8-10 hours | **Expected Tests**: 130+ | **Target Coverage**: 85%+

**Learning Objectives:**
- Master type parameters and constraints
- Design generic classes and methods
- Leverage wildcards effectively
- Understand type erasure implications
- Avoid unchecked warnings
- Implement PECS principle

**Core Concepts Covered:**
```
├─ Type Parameters
│  ├─ Generic class definition
│  ├─ Generic method definition
│  ├─ Multiple type parameters
│  ├─ Bounded type parameters
│  ├─ Recursive type bounds
│  ├─ Parameter naming conventions
│  └─ Type argument consistency
├─ Bounded Types
│  ├─ Upper bounds (extends keyword)
│  ├─ Lower bounds (super keyword)
│  ├─ Multiple bounds
│  ├─ Self-types (CRTP pattern)
│  └─ Bound interaction with polymorphism
├─ Wildcards
│  ├─ Unbounded wildcards (?)
│  ├─ Upper-bounded wildcards (? extends T)
│  ├─ Lower-bounded wildcards (? super T)
│  ├─ Wildcard capture
│  ├─ Avoid wildcard capture issues
│  ├─ PECS: Producer Extends, Consumer Super
│  └─ Covariance and contravariance
├─ Type Erasure
│  ├─ Erasure during compilation
│  ├─ Bridge methods
│  ├─ Reification absence
│  ├─ Runtime type information loss
│  ├─ instanceof limitations
│  ├─ Array creation restrictions
│  └─ Workarounds with super type tokens
├─ Complex Generic Patterns
│  ├─ Generic class hierarchies
│  ├─ Generic interfaces
│  ├─ Mixing generic and raw types
│  ├─ Type inference with diamonds
│  └─ Recursive generics
├─ Reflection & Generics
│  ├─ TypeToken pattern
│  ├─ ParameterizedType inspection
│  ├─ Type variable information
│  └─ Runtime type recovery
└─ Best Practices
   ├─ Unchecked warnings suppression
   ├─ Avoid raw types in new code
   ├─ Generic patterns for reusability
   ├─ Testing generic code
   └─ Documentation requirements
```

**Example Patterns Implemented:**
```java
// Basic generic class
class Container<T> {
    private T value;
    
    void set(T value) { this.value = value; }
    T get() { return value; }
}

// Bounded type parameter
class NumberProcessor<T extends Number> {
    double processNumbers(T... numbers) {
        return Arrays.stream(numbers)
            .mapToDouble(Number::doubleValue)
            .average()
            .orElse(0);
    }
}

// Generic method with bounds
class Utilities {
    static <T extends Comparable<T>> T max(T a, T b) {
        return a.compareTo(b) > 0 ? a : b;
    }
}

// Multiple bounds
class Data<T extends Number & Comparable<T>> {
    T value;
    
    int compare(T other) {
        return value.compareTo(other);
    }
}

// Wildcards - PECS
class GraphNode<T> {
    private List<T> neighbors = new ArrayList<>();
    
    // Producer: extends
    void addAll(Collection<? extends T> collection) {
        neighbors.addAll(collection);
    }
    
    // Consumer: super
    void copyTo(Collection<? super T> collection) {
        collection.addAll(neighbors);
    }
}

// Self-referential type (CRTP)
abstract class Base<T extends Base<T>> {
    abstract T getThis();
    
    T chainOperation() {
        doSomething();
        return getThis();
    }
    
    abstract void doSomething();
}

class Derived extends Base<Derived> {
    @Override
    Derived getThis() { return this; }
    
    @Override
    void doSomething() { }
}

// TypeToken for runtime type recovery
class TypeToken<T> {
    final Type type;
    
    protected TypeToken() {
        this.type = 
            ((ParameterizedType) getClass()
                .getGenericSuperclass())
            .getActualTypeArguments()[0];
    }
    
    public Type getType() { return type; }
}

// Usage
TypeToken<List<String>> listOfStrings = 
    new TypeToken<List<String>>() {};

// Diamond operator type inference
Map<String, List<Integer>> map = new HashMap<>();

// Generic interface
interface Repository<T> {
    List<T> findAll();
    Optional<T> findById(Long id);
    void save(T entity);
}

class UserRepository implements Repository<User> {
    @Override
    public List<User> findAll() { return new ArrayList<>(); }
    
    @Override
    public Optional<User> findById(Long id) { 
        return Optional.empty(); 
    }
    
    @Override
    public void save(User entity) { }
}
```

**Test Strategy:**
- Generic class instantiation tests
- Type parameter constraint verification
- Wildcard capture and bounds tests
- Type erasure implications testing
- Generic inheritance hierarchy tests
- TypeToken runtime type recovery
- Generic method type inference
- Unchecked warning suppression
- Array creation restriction tests
- Bridge method verification

**Dependency Requirements:**
- Prerequisite: Module 1 (Java Basics), Module 2 (OOP)
- Recommended: Module 3 (Collections), Module 8 (Reflection)
- Uses: Classes, interfaces, inheritance, polymorphism

**Integration Points:**
- Collections (Module 3): Generic collections
- Streams API (Module 4): Generic stream operations
- Reflection (Module 9): Generic type inspection
- Java 21 Features (Module 10): Generic enhancements

---

### MODULE 9: Reflection & Annotations

**Estimated Duration**: 10-12 hours | **Expected Tests**: 140+ | **Target Coverage**: 85%+

**Learning Objectives:**
- Master reflection API for class inspection
- Design and process custom annotations
- Implement annotation processors
- Understand security implications
- Use reflection for framework patterns
- Optimize reflection performance

**Core Concepts Covered:**
```
├─ Reflection Fundamentals
│  ├─ Class object and Class.forName()
│  ├─ Class hierarchy and modifiers
│  ├─ Constructor discovery and invocation
│  ├─ Method discovery and invocation
│  ├─ Field discovery and access
│  ├─ Array handling with reflection
│  ├─ Primitive type wrappers
│  └─ Accessible object and override security
├─ Method & Constructor Handling
│  ├─ Method parameter inspection
│  ├─ Return type and exception handling
│  ├─ Method invocation with null checks
│  ├─ varargs handling
│  ├─ Generic method reflection
│  ├─ Constructor parameter types
│  ├─ Instance creation patterns
│  └─ Performance considerations
├─ Field Inspection & Modification
│  ├─ Field type and modifiers
│  ├─ Field value reading
│  ├─ Field value modification
│  ├─ setAccessible() for private fields
│  ├─ Primitive vs. reference type handling
│  ├─ Array field peculiarities
│  └─ Field access performance
├─ Annotation Framework
│  ├─ Built-in annotations (@Override, @Deprecated)
│  ├─ Meta-annotations (@Retention, @Target, @Documented)
│  ├─ Custom annotation design
│  ├─ Annotation element rules
│  ├─ Annotation inheritance
│  ├─ Repeating annotations (Java 8+)
│  └─ Annotation nesting
├─ Annotation Processing
│  ├─ Runtime vs. compile-time processing
│  ├─ Retention policies
│  ├─ AnnotatedElement interface
│  ├─ Type annotations (Java 8+)
│  ├─ Processing framework integration
│  ├─ Custom processor implementation
│  └─ Maven/Gradle integration
├─ Advanced Reflection Techniques
│  ├─ Method handles (MethodHandle API)
│  ├─ Varhandle for atomic field access
│  ├─ Unsafe class (careful use)
│  ├─ Instrumentation and agents
│  ├─ Dynamic proxy creation
│  └─ Bytecode manipulation
├─ Framework Patterns
│  ├─ Object mapping (property introspection)
│  ├─ Dependency injection
│  ├─ ORM patterns
│  ├─ Serialization frameworks
│  ├─ Configuration parsing
│  └─ Factory patterns with reflection
└─ Performance & Security
   ├─ Reflection overhead
   ├─ Caching strategies
   ├─ JIT compilation implications
   ├─ SecurityManager considerations
   ├─ Reflection attacks
   └─ Best practices for safe reflection
```

**Example Patterns Implemented:**
```java
// Class discovery and inspection
Class<?> clazz = Class.forName("com.example.User");
System.out.println("Simple name: " + clazz.getSimpleName());
System.out.println("Package: " + clazz.getPackageName());
System.out.println("Modifiers: " + Modifier.toString(clazz.getModifiers()));

// Constructor invocation
Constructor<User> constructor = 
    User.class.getDeclaredConstructor(String.class, int.class);
User user = constructor.newInstance("John", 30);

// Method discovery and invocation
Method setNameMethod = User.class.getDeclaredMethod("setName", String.class);
setNameMethod.invoke(user, "Jane");

Method getName = User.class.getMethod("getName");
String name = (String) getName.invoke(user);

// Field access
Field ageField = User.class.getDeclaredField("age");
ageField.setAccessible(true);
int age = (int) ageField.get(user);
ageField.set(user, 35);

// Generic method reflection
Method genericMethod = GenericClass.class
    .getMethod("process", Object.class);
Type[] typeParams = genericMethod.getGenericParameterTypes();
ParameterizedType paramType = (ParameterizedType) typeParams[0];

// Custom annotation
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Entity {
    String name() default "";
    boolean cacheable() default false;
}

// Repeating annotation (Java 8+)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Tag {
    String value();
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Tags {
    Tag[] value();
}

// Type annotation (Java 8+)
@Target(ElementType.TYPE_USE)
public @interface NonNull { }

public void method(@NonNull Object param) { }

// Annotation processing at runtime
class AnnotationProcessor {
    static <T> T instantiate(Class<T> clazz) throws Exception {
        if (clazz.isAnnotationPresent(Entity.class)) {
            Entity entity = clazz.getAnnotation(Entity.class);
            System.out.println("Entity: " + entity.name());
        }
        
        return clazz.getDeclaredConstructor().newInstance();
    }
}

// Object mapping with reflection
class ObjectMapper {
    <T> T mapToObject(Map<String, Object> data, Class<T> clazz) 
            throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();
        
        for (Field field : clazz.getDeclaredFields()) {
            if (data.containsKey(field.getName())) {
                field.setAccessible(true);
                field.set(instance, data.get(field.getName()));
            }
        }
        
        return instance;
    }
}

// Dynamic proxy creation
interface Repository<T> {
    List<T> findAll();
    T findById(Long id);
}

class CachingProxyCreator {
    static <T> T createCachingProxy(Class<T> interfaceClass, T delegate) {
        return (T) Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class[]{interfaceClass},
            (proxy, method, args) -> {
                String cacheKey = method.getName() + Arrays.toString(args);
                if (cache.containsKey(cacheKey)) {
                    return cache.get(cacheKey);
                }
                Object result = method.invoke(delegate, args);
                cache.put(cacheKey, result);
                return result;
            }
        );
    }
}

// Method handles (modern alternative to reflection)
MethodHandles.Lookup lookup = MethodHandles.lookup();
MethodHandle methodHandle = lookup.findVirtual(
    User.class, "getName", MethodType.methodType(String.class));
String name = (String) methodHandle.invoke(user);
```

**Test Strategy:**
- Class discovery and introspection tests
- Constructor invocation tests
- Method discovery and parameter handling
- Field access and modification tests
- Annotation presence and value retrieval
- Generic type parameter reflection
- Dynamic proxy functionality tests
- Exception handling in reflection
- Performance benchmark tests
- Security implications (reflection attacks)

**Dependency Requirements:**
- Prerequisite: Module 1 (Java Basics), Module 2 (OOP)
- Recommended: Module 3 (Collections), Module 8 (Generics)
- Uses: Classes, interfaces, collections, generic types

**Integration Points:**
- Generics (Module 8): Generic type inspection
- Collections (Module 3): Annotation processing on collections
- Streams API (Module 4): Reflective stream filtering
- Java 21 Features (Module 10): New reflection capabilities

---

### MODULE 10: Java 21 Features (Capstone)

**Estimated Duration**: 10-12 hours | **Expected Tests**: 150+ | **Target Coverage**: 85%+

**Learning Objectives:**
- Master Records for data classes
- Use Sealed Classes for controlled hierarchies
- Implement Switch Expressions fluently
- Leverage Pattern Matching effectively
- Understand Virtual Threads for async
- Apply all learning in modern Java idioms

**Core Concepts Covered:**
```
├─ Records (Java 16+)
│  ├─ Record anatomy (header, body)
│  ├─ Generated methods (equals, hashCode, toString)
│  ├─ Compact constructors
│  ├─ Custom accessors
│  ├─ Record serialization
│  ├─ Sealed records
│  ├─ Record components inspection
│  └─ Records vs. classes trade-offs
├─ Sealed Classes (Java 17+)
│  ├─ Sealed declaration and modifiers
│  ├─ Sealed hierarchy design
│  ├─ Implementation rules (implements/extends)
│  ├─ Exhaustiveness checking
│  ├─ Non-sealed subclasses
│  ├─ Final subclasses
│  └─ Sealed interface patterns
├─ Switch Expressions (Java 14+)
│  ├─ Expression vs. statement switch
│  ├─ Case arrow syntax
│  ├─ Multiple case labels
│  ├─ Default behavior
│  ├─ Exhaustiveness validation
│  ├─ Yielding values
│  ├─ Switch expression nesting
│  └─ Performance improvements
├─ Pattern Matching (Java 16+, evolving)
│  ├─ Type patterns (instanceof)
│  ├─ Record patterns
│  ├─ Array patterns (Java 21+)
│  ├─ Guarded patterns
│  ├─ Logical patterns (or, and, not)
│  ├─ Dominance analysis
│  ├─ Exhaustiveness analysis
│  └─ Pattern composition
├─ Text Blocks (Java 13+)
│  ├─ Multi-line string literals
│  ├─ Indentation handling
│  ├─ Escape sequences in text blocks
│  ├─ Performance characteristics
│  └─ JSON/SQL in code examples
├─ Virtual Threads (Java 19+, preview→stable in 21)
│  ├─ Virtual thread creation
│  ├─ Virtual vs. platform threads
│  ├─ Structured concurrency (preview)
│  ├─ Virtual thread scheduling
│  ├─ Exception handling in virtual threads
│  ├─ Testing virtual threads
│  ├─ Migration from traditional threads
│  └─ Performance characteristics
├─ Foreign Function & Memory API
│  ├─ Interop with native libraries
│  ├─ Memory safety
│  ├─ Arena allocation
│  ├─ Downcalls and upcalls
│  └─ Performance implications
├─ Other Enhancements
│  ├─ Enhanced instanceof (Java 16+)
│  ├─ Global class records
│  ├─ Unnamed variables (Java 21)
│  ├─ String templates (preview)
│  └─ Stream gathering (Java 22)
└─ Integration & Best Practices
   ├─ Combining multiple features
   ├─ Migration from older Java versions
   ├─ Framework compatibility
   ├─ Testing strategies
   └─ Performance considerations
```

**Example Patterns Implemented:**
```java
// Records for data classes
record Point(int x, int y) { }

record User(String name, int age) {
    // Compact constructor
    public User {
        if (age < 0) throw new IllegalArgumentException("Age must be positive");
    }
}

// Record with custom accessor
record Person(String name, int age) {
    public String name() {
        return name().toUpperCase();
    }
}

// Sealed class hierarchy
sealed class Animal permits Dog, Cat, Bird {
    abstract void makeSound();
}

final class Dog extends Animal {
    @Override void makeSound() { System.out.println("Woof"); }
}

non-sealed class Cat extends Animal {
    @Override void makeSound() { System.out.println("Meow"); }
}

sealed interface Shape permits Circle, Square { }
record Circle(double radius) implements Shape { }
record Square(double side) implements Shape { }

// Switch expressions with exhaustiveness
String typeDescription = switch (animal) {
    case Dog d -> "A dog that barks";
    case Cat c -> "A cat that meows";
    case Bird b -> "A bird that chirps";
};

// Switch with record patterns (Java 21+)
String analyze = switch (shape) {
    case Circle(var r) -> "Circle with radius " + r;
    case Square(var s) -> "Square with side " + s;
};

// Pattern matching in instanceof
Object obj = "Hello";
if (obj instanceof String s && s.length() > 5) {
    System.out.println("Long string: " + s);
}

// Record patterns (Java 21+)
record Coordinate(int x, int y) { }

if (object instanceof Coordinate(var x, var y) && x > 10) {
    System.out.println("Far right: " + y);
}

// Logical patterns (Java 21+)
if (number instanceof Integer n && (n > 10 || n < -10)) {
    System.out.println("Outlier: " + n);
}

// Text blocks
String json = """
    {
        "name": "John",
        "age": 30
    }
    """;

String sql = """
    SELECT * FROM users
    WHERE age > ? 
    AND status = 'ACTIVE'
    """;

// Virtual threads (Java 19+)
Thread.ofVirtual()
    .name("virtual-thread-1")
    .start(() -> {
        System.out.println("Running in virtual thread");
    });

// Structured concurrency with virtual threads (preview)
void processWithStructuredConcurrency() throws Exception {
    try (var scope = new StructuredTaskScope<>()) {
        var future1 = scope.fork(() -> task1());
        var future2 = scope.fork(() -> task2());
        
        scope.join().throwIfFailed();
        
        var result1 = future1.resultNow();
        var result2 = future2.resultNow();
    }
}

// Virtual thread executor
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
for (int i = 0; i < 10000; i++) {
    executor.submit(() -> handleRequest());
}

// Thread builder (Java 21+)
Thread virtualThread = Thread.ofVirtual()
    .name("virtual-", 0)
    .uncaughtExceptionHandler((t, e) -> {
        System.err.println("Thread " + t.getName() + " failed: " + e);
    })
    .start(() -> {
        System.out.println("Virtual thread work");
    });

// Unnamed variables (Java 21)
var (_, y) = pair;  // Ignore x
var [first, _, rest] = array;  // Ignore middle element

// String templates (preview in 21)
// String message = "Hello \{name}, you are \{age} years old";
// Note: This is a preview feature

// Combining sealed + records + patterns
sealed interface Result<T> permits Success, Failure {
    T getOrThrow();
}

record Success<T>(T value) implements Result<T> {
    @Override public T getOrThrow() { return value; }
}

record Failure<T>(Exception error) implements Result<T> {
    @Override public T getOrThrow() throws Exception { throw error; }
}

String handle(Result<String> result) {
    return switch (result) {
        case Success(var value) -> "Success: " + value;
        case Failure(var error) -> "Error: " + error.getMessage();
    };
}

// Enhanced instanceof
if (value instanceof String s && Integer.parseInt(s) > 10) {
    System.out.println("Large number: " + s);
}
```

**Test Strategy:**
- Record equality and hashCode tests
- Record serialization/deserialization
- Sealed class invariant enforcement
- Switch expression exhaustiveness
- Pattern matching correctness
- Virtual thread creation and lifecycle
- Virtual thread exception handling
- Structured concurrency tests
- Performance comparison (virtual vs. platform threads)
- Text block escaping and whitespace handling

**Dependency Requirements:**
- Prerequisite: All modules 1-9 (integration capstone)
- Uses: All core Java concepts
- Integration: Brings together all previous modules

**Integration Points:**
- All modules: Integration and real-world application
- Legacy code: Migration patterns
- Frameworks: Framework-specific patterns

---

## 🏗️ Cross-Module Integration Architecture

### Module Progression Path

```
Foundation Layer
├─ Module 1: Java Basics ✅
└─ Module 2: OOP Concepts

Core Collections Layer
├─ Module 3: Collections Framework
├─ Module 8: Generics (parallel to Collections)
└─ Module 7: I/O & NIO

Functional Programming Layer
├─ Module 4: Streams API
├─ Module 5: Lambda Expressions
└─ Module 9: Reflection & Annotations

Concurrency Layer
└─ Module 6: Concurrency & Multithreading

Advanced Features Layer
└─ Module 10: Java 21 Features (Capstone)
```

### Data Flow Between Modules

```
Module 1 (Basics)
    ↓
Module 2 (OOP) ←→ Module 8 (Generics)
    ↓                   ↓
Module 3 (Collections) ←┘
    ↓
Module 4 (Streams) ←→ Module 5 (Lambda)
    ↓                   ↓
Module 6 (Concurrency) + Module 7 (I/O)
    ↓
Module 9 (Reflection & Annotations)
    ↓
Module 10 (Java 21 Capstone)
```

### Shared Test Infrastructure

All modules share:
- **Base Test Classes**: AbstractModuleTest with common utilities
- **Fixtures**: Reusable test data and factories
- **Mocking Library**: Mockito for isolation
- **Performance Testing**: JMH for benchmarks
- **Code Quality**: SonarQube standards (85%+ coverage)

---

## ✅ Production-Ready Checklist (Per Module)

### Code Quality Requirements
- [ ] 85%+ unit test coverage
- [ ] 0 critical SonarQube issues
- [ ] All warnings resolved
- [ ] Javadoc for public APIs (100%)
- [ ] Code style compliance

### Testing Requirements
- [ ] Unit tests written and passing
- [ ] Integration tests for module interactions
- [ ] Edge case coverage
- [ ] Performance benchmarks baseline
- [ ] Thread safety tests (if applicable)
- [ ] Memory leak verification

### Documentation Requirements
- [ ] Module README with learning objectives
- [ ] Example programs with output
- [ ] Architecture diagrams
- [ ] Design decisions documented
- [ ] Common pitfalls documented
- [ ] Best practices summary

### Example Application Requirements
- [ ] Runnable example for each concept
- [ ] Correct output demonstrated
- [ ] Error handling examples
- [ ] Performance characteristics noted

### Deployment Requirements
- [ ] Docker support (if applicable)
- [ ] Maven/Gradle build success
- [ ] All dependencies resolved
- [ ] No security vulnerabilities
- [ ] Java 21 compatibility verified

---

## 📊 Test Coverage Strategy

### Coverage Tiers

**Tier 1: Unit Tests (60-65%)**
- Individual class/method testing
- Happy path scenarios
- Error conditions
- Boundary cases

**Tier 2: Integration Tests (15-20%)**
- Module-to-module interactions
- End-to-end workflows
- Real object collaboration

**Tier 3: Performance Tests (5-10%)**
- Benchmark comparisons
- Memory profiling
- Scalability testing

**Tier 4: Security Tests (5-10%)**
- Unsafe operations
- Reflection attacks
- Serialization flaws

---

## 📈 Learning Outcomes Measurement

### Per-Module Metrics

```
Module | Tests | Coverage | Complexity | Est. Hours | Mastery Indicators
-------|-------|----------|------------|------------|-------------------
1      | 107   | ~90%     | Low        | 4-6        | 90%+ basic syntax tests
2      | 120+  | 85%+     | Medium     | 8-10       | OOP pattern implementation
3      | 150+  | 85%+     | Medium     | 10-12      | Correct collection choice
4      | 140+  | 85%+     | High       | 10-12      | Efficient stream pipelines
5      | 130+  | 85%+     | Medium     | 8-10       | Functional programming idioms
6      | 160+  | 85%+     | High       | 12-14      | Thread-safe code design
7      | 140+  | 85%+     | Medium     | 10-12      | Appropriate I/O strategy
8      | 130+  | 85%+     | Medium     | 8-10       | Generic class design
9      | 140+  | 85%+     | High       | 10-12      | Reflection for frameworks
10     | 150+  | 85%+     | High       | 10-12      | Modern Java idioms
```

---

## 🔄 Implementation Checklist

### Phase 1: Foundation (Modules 1-2)
- [x] Module 1: Java Basics (COMPLETE)
- [ ] Module 2: OOP Concepts (NEXT)

### Phase 2: Collections & Generics (Modules 3, 8)
- [ ] Module 3: Collections Framework
- [ ] Module 8: Generics (parallel)

### Phase 3: Functional Programming (Modules 4-5)
- [ ] Module 4: Streams API
- [ ] Module 5: Lambda Expressions

### Phase 4: Concurrency & I/O (Modules 6-7)
- [ ] Module 6: Concurrency & Multithreading
- [ ] Module 7: I/O & NIO

### Phase 5: Advanced Features (Modules 9-10)
- [ ] Module 9: Reflection & Annotations
- [ ] Module 10: Java 21 Features

---

## 🚀 Quick Start for Implementation

### Each Module Follows This Structure:

```
module-name/
├── src/
│   ├── main/java/com/learning/module/
│   │   ├── *.java          # Implementation files
│   │   ├── examples/       # Example patterns
│   │   └── utils/          # Utility classes
│   └── test/java/com/learning/module/
│       ├── *Tests.java     # Unit tests
│       └── integration/    # Integration tests
├── resources/
│   └── test-data/          # Test fixtures
├── README.md               # Module documentation
├── ARCHITECTURE.md         # Design decisions
└── pom.xml                 # Maven configuration
```

---

## 📚 References & Standards

- **Java Language Specification**: Java SE 21 spec
- **Effective Java**: 3rd Edition, Joshua Bloch
- **Java Concurrency in Practice**: Brian Goetz et al.
- **Code Complete**: Steve McConnell
- **Clean Code**: Robert Martin

---

**Last Updated**: March 5, 2026
**Status**: Architecture Design Complete - Ready for Implementation

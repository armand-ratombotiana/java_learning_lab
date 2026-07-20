# Profiling with async-profiler — Exercises

## Beginner Exercises

### Exercise 1: Basic Operations
Implement a program that demonstrates the core operations of Profiling with async-profiler.
```java
// Create an instance of the data structure
// Add 5-10 elements
// Retrieve each element and verify correctness
// Test contains() for existing and non-existing keys
// Remove an element and verify it is gone
```

### Exercise 2: Iteration
Write code to iterate through all elements using multiple approaches.
```java
// Use for-each loop
// Use iterator explicitly
// Use Java 8 forEach() method
// Use stream API
// Compare iteration order guarantees
```

## Intermediate Exercises

### Exercise 3: Custom Object Storage
Create a custom class (Person with name and age). Override equals() and hashCode() correctly.
Store instances and verify lookup behavior.

### Exercise 4: Comparator-Based Ordering
Implement a Comparator and use it to control ordering behavior in sorted variants.
```java
Comparator<Person> byAge = Comparator.comparingInt(Person::age);
// Use with sorted collection
```

### Exercise 5: Concurrent Access
Use synchronized wrappers or concurrent variants to safely access from multiple threads.
```java
Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());
// Test concurrent put/get from 4 threads
```

## Advanced Exercises

### Exercise 6: Custom Implementation
Implement a simplified version of this data structure from scratch.
Implement all core operations without using java.util collections.

### Exercise 7: Performance Benchmark
Write a JMH benchmark comparing this structure with alternatives.
Measure throughput, latency, and allocation rates.

### Exercise 8: Memory Footprint Analysis
Use JOL (Java Object Layout) to measure the memory footprint with varying element counts.
```java
// Use GraphLayout.parseInstance() to measure memory
```

## Challenge Exercises

### Exercise 9: Thread-Safe Variant
Implement a thread-safe version using ReentrantReadWriteLock or synchronized blocks.
Benchmark against java.util.concurrent variants.

### Exercise 10: Optimization
Analyze the implementation and identify optimization opportunities.
Profile with async-profiler and verify improvements.

## Bonus Exercises

### Exercise 11: Serialization
Make the implementation serializable and test round-trip serialization/deserialization.

### Exercise 12: Custom Iterators
Implement custom iterators that support fail-fast behavior and the remove() operation.


## Further Exploration

### Additional Reading
- Review the companion files in this micro-lab for deeper understanding
- Complete the exercises in EXERCISES.md to apply your knowledge
- Build the MINI_PROJECT to cement the concepts
- Test yourself with QUIZ.md and FLASHCARDS.md
- Practice with INTERVIEW.md questions for job preparation

### Related Concepts
- equals() and hashCode() contracts in Java
- Comparable and Comparator interfaces for ordering
- Iterator and Iterable patterns for traversal
- Stream API for functional-style operations
- Serialization for object persistence
- Cloning and defensive copying

### Best Practices
1. Always choose the right data structure for your use case
2. Consider initial capacity for large datasets
3. Use immutable objects as keys in hash-based collections
4. Synchronize externally or use concurrent variants for thread safety
5. Profile before optimizing - don't guess about performance
6. Document ordering guarantees your code depends on
7. Use interfaces (Map, List, Set) for variable declarations
8. Prefer composition over inheritance for custom collections
9. Override toString() for meaningful debug output
10. Consider memory implications of your collection choices

### Common Pitfalls to Avoid
- Using mutable objects as keys in HashMap/HashSet
- Iterating and modifying without using iterator methods
- Assuming iteration order without checking documentation
- Using LinkedList when random access is needed
- Ignoring initial capacity for large collections
- Forgetting to override both equals() and hashCode()
- Using == instead of equals() for key comparison
- Not handling ConcurrentModificationException properly

### Next Steps
1. Implement a custom version of this data structure from scratch
2. Benchmark against the standard Java implementation
3. Analyze memory usage with JOL (Java Object Layout)
4. Profile performance with async-profiler
5. Write comprehensive unit tests covering all edge cases
6. Design a thread-safe variant for concurrent use cases
7. Research alternative implementations in other languages
8. Apply the concept to a real-world project

### Key Takeaways Summary
- Understand the internal mechanics and algorithmic complexity
- Know the performance characteristics and memory footprint
- Recognize appropriate use cases and selection criteria
- Master common patterns and anti-patterns
- Develop debugging intuition for related issues
- Build mental models that transfer to other concepts

### Discussion Questions
1. How would you design this differently if starting from scratch?
2. What are the limits of this approach in terms of scale?
3. How does this concept interact with modern hardware (CPU caches, NUMA)?
4. What alternatives exist in other programming languages?
5. How would you implement this for a distributed system?

### Code Review Checklist
- [ ] Correct equals() and hashCode() implementations for keys
- [ ] Appropriate initial capacity and load factor selection
- [ ] Proper synchronization or concurrent variant for shared state
- [ ] No concurrent modification during iteration
- [ ] Immutable or effectively immutable key objects
- [ ] Consistent use of interface types for declarations
- [ ] Proper null handling (or documentation of non-null requirement)
- [ ] toString() implementation for debugging
- [ ] Serializable implementation if needed
- [ ] Performance considerations documented

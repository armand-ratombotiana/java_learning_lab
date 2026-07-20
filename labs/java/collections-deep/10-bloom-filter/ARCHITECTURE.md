# Bloom Filter — Architecture

## Design Goals
1. **Performance**: Optimized for average-case O(1) operations
2. **Memory Efficiency**: Minimal per-element overhead
3. **API Consistency**: Follows Java Collections Framework interfaces
4. **Fail-Fast Safety**: Detects concurrent modification during iteration
5. **Serialization**: Supports standard Java serialization mechanism

## Component Diagram
```
+--------------------------------------------------+
|                   Interface                        |
|         Map<K,V> / Collection<E>                   |
+------------------------+-------------------------+
                         | implements
+------------------------v-------------------------+
|              Abstract Class                        |
|        AbstractMap<K,V> / AbstractCollection<E>    |
+------------------------+-------------------------+
                         | extends
+------------------------v-------------------------+
|              Concrete Class                        |
|           MainImplementation<K,V>                  |
+--------------------------------------------------+
| - table: Node<K,V>[]                              |
| - size: int                                       |
| - modCount: int                                   |
| - threshold: int                                  |
| - loadFactor: float                               |
+--------------------------------------------------+
| + put(K,V): V                                     |
| + get(K): V                                       |
| + remove(K): V                                    |
| + containsKey(K): boolean                         |
| + keySet(): Set<K>                                |
| + values(): Collection<V>                         |
| + entrySet(): Set<Map.Entry<K,V>>                 |
+--------------------------------------------------+
```

## Package Structure
```
com.javalab.10
+-- MainImplementation.java
+-- MainImplementationTest.java
```

## Extension Points
- Subclassing for specialized behavior
- Custom equals/hashCode on key objects
- Comparator injection for sorted variants
- Serialization proxy pattern for custom serialization

## Architectural Decisions

### Decision 1: Array-Based Backing Store
Using arrays provides contiguous memory allocation for cache-friendly access,
O(1) index-based bucket lookup, and simple resize via array copy.

### Decision 2: Chain-Based Collision Resolution
Chaining was chosen over open addressing because it provides better cache
performance under high load, simpler deletion, and more predictable performance.

### Decision 3: Power-of-Two Sizing
Capacities are powers of two enabling fast bitwise index calculation (hash & (capacity-1))
and simplifying resize rehashing (entries split on hash & oldCapacity).


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

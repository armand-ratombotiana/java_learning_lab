# Memory Barriers — Common Mistakes

## Mistake 1: Incorrect equals/hashCode
**Problem**: Custom objects used without properly overriding equals() and hashCode().
**Effect**: Keys not found, duplicates occur, behavior unpredictable.
**Fix**: Always override both methods. Use Objects.hash().

## Mistake 2: Assuming Insertion Order
**Problem**: Depending on specific iteration order without checking contract.
**Effect**: Works in testing, fails in production with different implementation.
**Fix**: Use LinkedHashMap/LinkedHashSet when order matters.

## Mistake 3: Concurrent Modification
**Problem**: Modifying non-thread-safe collection while iterating.
**Effect**: ConcurrentModificationException at runtime.
**Fix**: Use concurrent collections or synchronize externally.

## Mistake 4: Using Mutable Keys
**Problem**: Modifying key object after insertion into hash-based collection.
**Effect**: Object in wrong bucket, cannot be found, memory leaks.
**Fix**: Use immutable keys (String, Integer, records).

## Mistake 5: Ignoring Initial Capacity
**Problem**: Default capacity for large expected dataset.
**Effect**: Multiple resizing operations degrade performance.
**Fix**: Use constructor with initial capacity: new HashMap<>(expectedSize).

## Mistake 6: LinkedList Random Access
**Problem**: Using LinkedList.get(index) in a loop.
**Effect**: O(n^2) time complexity instead of O(n).
**Fix**: Use iterator or ArrayList for random access.

## Mistake 7: Stream Overuse
**Problem**: Complex stream pipelines on large collections.
**Effect**: Poor performance, high memory usage.
**Fix**: Use simple loops for critical paths.

## Mistake 8: Not Trimming After Bulk Load
**Problem**: Keeping oversized internal array after bulk loading.
**Effect**: Wasted memory.
**Fix**: Call trimToSize() after bulk loading.

## Mistake 9: Assuming Thread Safety
**Problem**: Using HashMap in multi-threaded context.
**Effect**: Data corruption, infinite loops (pre-Java 8), lost updates.
**Fix**: Use ConcurrentHashMap for concurrent access.


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

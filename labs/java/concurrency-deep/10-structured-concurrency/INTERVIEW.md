# Structured Concurrency — Interview Questions

## Beginner Questions

### Q1: What is the main purpose of this data structure?
**A**: To provide efficient storage and retrieval of elements with specific performance guarantees.

### Q2: What is the difference between Collection and Collections?
**A**: Collection is an interface (root of the collection hierarchy). Collections is a utility class with static methods.

### Q3: What is the difference between List and Set?
**A**: List allows duplicates and maintains insertion order. Set does not allow duplicates.

## Intermediate Questions

### Q4: How does this structure work internally?
**A**: Explain the backing data structure, the algorithm for storing and retrieving, and how collisions are handled.

### Q5: When would you choose this over alternatives?
**A**: Compare with similar structures. List scenarios where this is optimal and where it is not.

### Q6: How do you make it thread-safe?
**A**: External synchronization, synchronized wrappers, or use concurrent variants.

### Q7: Explain fail-fast and fail-safe iterators.
**A**: Fail-fast (HashMap, ArrayList) throw ConcurrentModificationException on concurrent modification. Fail-safe (ConcurrentHashMap, CopyOnWriteArrayList) iterate over a snapshot.

## Advanced Questions

### Q8: How does the resize mechanism work?
**A**: Explain the growth factor, threshold calculation, rehashing process, and complexity analysis.

### Q9: What is the load factor and why is it important?
**A**: Controls the density before resize. Default 0.75 balances time and space. Lower = less collisions, more memory.

### Q10: How does the structure handle concurrent access?
**A**: Explain CAS operations, lock striping, copy-on-write, snapshot iteration for concurrent variants.

### Q11: What is the performance impact of poor hash distribution?
**A**: All keys map to same bucket -> O(n) operations. Mitigated by treeification at threshold 8.

### Q12: Memory profiling — how do you identify issues?
**A**: JOL for memory layout, heap dump analysis with MAT, GC log analysis with GCeasy.

## Coding Questions

### Q13: Implement a simple version from scratch.
### Q14: Implement a thread-safe cache using this structure.
### Q15: Implement an LRU cache using LinkedHashMap.
### Q16: Write code to count word frequency in a large file.
### Q17: Implement a custom iterator for this structure.


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

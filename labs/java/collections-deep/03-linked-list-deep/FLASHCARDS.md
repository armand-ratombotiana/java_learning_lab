# LinkedList Deep Dive — Flashcards

## Card 1: Time Complexity
**Q**: What is the average and worst-case time complexity of the primary operation?
**A**: Average O(1), worst-case O(n) (when all elements hash to same bucket)

## Card 2: Load Factor
**Q**: What is the default load factor and what does it mean?
**A**: Default 0.75. Structure resizes when 75% full, balancing time and space.

## Card 3: Resize Factor
**Q**: By what factor does the backing array grow during resize?
**A**: 2x for HashMap/HashSet, 1.5x for ArrayList

## Card 4: Treeification
**Q**: At what threshold does a linked list become a red-black tree?
**A**: TREEIFY_THRESHOLD = 8 entries in a single bucket

## Card 5: Fail-Fast
**Q**: What is fail-fast behavior and how is it implemented?
**A**: Iterators throw ConcurrentModificationException if collection is structurally modified during iteration. Implemented via modCount field.

## Card 6: equals() vs hashCode()
**Q**: What is the contract between equals() and hashCode()?
**A**: If a.equals(b) then a.hashCode() == b.hashCode(). The reverse is not required.

## Card 7: Initial Capacity
**Q**: What is the default initial capacity?
**A**: 16 (for HashMap), 10 (for ArrayList)

## Card 8: Null Keys
**Q**: Which collections allow null keys?
**A**: HashMap, LinkedHashMap, WeakHashMap allow one null key. Hashtable, ConcurrentHashMap, TreeMap do not.

## Card 9: Synchronized Wrapper
**Q**: How do you make a non-thread-safe collection thread-safe?
**A**: Collections.synchronizedMap(map), Collections.synchronizedList(list), etc.

## Card 10: ConcurrentHashMap Segments
**Q**: How did ConcurrentHashMap handle concurrency before Java 8?
**A**: Used segment-based locking — 16 independent locks, each protecting a portion of the map.


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

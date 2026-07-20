# Reordering — Debugging

## Debugging Strategies

### Print-Based Debugging
```java
System.out.println("State: " + collection);
System.out.println("Size: " + collection.size());
System.out.println("Contains key X: " + collection.containsKey("X"));
```

### IDE Debugger Inspection
Modern IDEs provide collection visualizers:
- **IntelliJ IDEA**: Shows internal structure, hash codes, conflicts
- **Eclipse**: Detail formatters for common collections
- **VS Code**: Java Debug Extension with variable inspection

### JFR Events
Java Flight Recorder captures collection-related events:
- **Allocation events**: When collections allocate new backing arrays
- **GC events**: Collection overhead during GC pauses
- **Lock events**: Contention on synchronized collections

### Heap Dump Analysis
```bash
jmap -dump:live,format=b,file=heap.hprof <pid>
```
Use Eclipse MAT or JProfiler to analyze:
- **Dominator tree**: Find largest collections
- **GC root paths**: Why collections aren't collected
- **OQL queries**: Filter specific collection types

### Thread Dump Analysis
```bash
jstack <pid> > threaddump.txt
```
Look for threads stuck in collection code, infinite resize loops, deadlocks.

## Debugging Tools

### JOL (Java Object Layout)
```java
System.out.println(GraphLayout.parseInstance(map).toFootprint());
```

### jhsdb
```bash
jhsdb jmap --heap --pid <pid>
jhsdb jmap --histo --pid <pid>
```

## Debugging Checklist
1. Check equals() and hashCode() implementations
2. Verify collection is the correct type
3. Confirm thread safety guarantees
4. Check for ConcurrentModificationException patterns
5. Verify initial capacity for expected data size


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

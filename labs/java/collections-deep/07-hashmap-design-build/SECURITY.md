# Build Your Own HashMap — Security

## Denial of Service via Hash Collisions
The most significant security concern is hash collision attacks:
- **Hash DoS**: Attacker crafts keys with identical hash codes
- **Effect**: All keys map to same bucket -> O(n^2) operations
- **Mitigation**: Treeification (Java 8+) converts long chains to red-black trees
- **Defense**: Random hash seed (introduced in Java 8)

## Hash Collision Attack Prevention
```java
// Java 8+ HashMap uses TreeNode after TREEIFY_THRESHOLD (8)
// String.hashCode() randomization is available via:
-Djdk.map.althashing.threshold=512
```

## Serialization Security
- Collections containing sensitive data require careful serialization
- Override writeObject/readObject for custom serialization
- Use transient modifier for sensitive internal data
- Validate serialized data before deserialization
- Be aware of deserialization gadget chains

## Thread Safety Guarantees
- **Non-thread-safe variants**: External synchronization required
- **Synchronized wrappers**: Per-method sync but compound ops not atomic
- **Concurrent variants**: Thread-safe for individual operations
- **Copy-on-write**: Safe iteration but memory-heavy

## Memory Exposure via Unmodifiable Views
```java
List<Integer> internal = new ArrayList<>();
List<Integer> view = Collections.unmodifiableList(internal);
// Internal list still accessible via original reference!
```
Use defensive copying for true immutability:
```java
public List<Integer> getData() {
    return List.copyOf(internal); // Creates immutable copy
}
```

## Security Checklist
1. Validate all keys before insertion
2. Do not expose internal arrays via reflection
3. Use Collections.unmodifiableXxx for API returns
4. Avoid using mutable objects as keys
5. Consider ConcurrentHashMap for shared state


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

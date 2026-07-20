# ZGC — Internal Implementation

## Internal Architecture

### Storage Layout
The internal storage follows a specific memory layout designed for efficient access patterns
and cache utilization. The primary structure uses an array-based backing store with specific
growth and shrinkage policies.

#### Array/Buffer Details
- **Initial Capacity**: Default of 16 (HashMap) or 10 (ArrayList), configurable via constructor
- **Growth Policy**: When capacity is exceeded, the internal array grows by a factor of 2x (HashMap) or 1.5x (ArrayList)
- **Shrink Policy**: trimToSize() reduces capacity to match current size exactly

### Sentinel/Nil Nodes
Many linked structures use sentinel nodes to simplify boundary conditions:
- **Head Sentinel**: Precedes first real element, simplifies insertion at beginning
- **Tail Sentinel**: Follows last real element, simplifies appending
- **Nil Leaf**: Red-black trees use sentinel nil leaf to fix insertion/deletion fixup

### State Variables

#### Modification Counter (modCount)
Each mutating operation increments this counter. Iterators check modCount at the start of
each iteration operation and throw ConcurrentModificationException if it changed since last check.

#### Structural Fields
- **size**: Current number of elements stored
- **capacity**: Current backing array length
- **threshold**: Capacity * load factor, triggers resize when exceeded
- **loadFactor**: Configurable density control (default 0.75)

### Internal Algorithms

#### Hash Function
The supplemental hash function spreads key hashes to reduce collisions:
```java
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```
This XORs the high 16 bits into the low 16 bits to improve bit distribution.

#### Index Calculation
```
index = (capacity - 1) & hash
```
Bitwise AND replaces modulo when capacity is a power of two, which is much faster.

#### Treeification
When a bucket chain exceeds TREEIFY_THRESHOLD (8), the linked list is converted to a
red-black tree. This prevents O(n) worst-case lookup for poorly-distributed hash codes.
The UNTREEIFY_THRESHOLD (6) is lower to prevent oscillation between list and tree.

### Memory Barriers (Concurrent Variants)
Concurrent variants use specific memory ordering guarantees:
- **Volatile reads**: volatile fields establish happens-before relationships
- **CAS operations**: Atomic compare-and-swap for lock-free updates
- **VarHandle**: Opaque, acquire, release access modes

### Debugging Internals
- **-XX:+PrintFieldLayout**: Shows object field layout in memory
- **JHSDB**: HotSpot debugger for inspecting runtime structures
- **JOL (Java Object Layout)**: Tool for measuring object memory footprint

### Source Code Navigation
Key internal methods to study in the JDK source:
- HashMap: putVal(), getNode(), resize(), treeifyBin()
- ConcurrentHashMap: putVal(), transfer(), addCount()
- ArrayList: grow(), add(), remove(), subList()
- LinkedList: linkFirst(), linkLast(), unlink(), node()
- TreeMap: put(), getEntry(), fixAfterInsertion(), rotateLeft/Right()


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

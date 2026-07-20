# Sealed Classes — How It Works

## Mechanical Explanation

### The Core Mechanism
Sealed Classes works by organizing data in a specific structure that enables efficient operations.
The fundamental mechanism relies on a clever mapping between keys and storage locations.

### Step-by-Step Execution

#### Insertion
When a new key-value pair is inserted:
1. The key's hash code is computed using key.hashCode()
2. A supplemental hash is applied to improve distribution across buckets
3. The hash is mapped to an index in the backing array using bitwise AND
4. If the bucket is empty, a new node is created and placed there
5. If occupied, the chain is traversed:
   - Key exists -> update value, return old value
   - Key not found -> append new node at end of chain
6. After insertion, if size exceeds threshold, the array is resized

#### Lookup
When a key is looked up:
1. Hash code computed and mapped to index using same algorithm
2. Bucket at that index is examined
3. If empty -> key not found, return null
4. If occupied, chain is traversed comparing keys using equals()
5. Key found -> return associated value
6. End of chain -> return null

#### Resizing
When resizing is triggered:
1. New array of twice the capacity is allocated
2. Each entry is rehashed (or reassigned) to the new array
3. For hash-based structures, entries split into low/high buckets based on hash & oldCapacity
4. Threshold is recalculated: newCapacity * loadFactor
5. Old array becomes eligible for garbage collection

### Concurrency Model
For concurrent variants:
- **Lock-Free**: CAS operations on head/tail pointers avoid blocking
- **Lock-Based**: Segmented locks reduce contention compared to single lock
- **Snapshot**: Copy-on-write semantics for safe iteration without locking

### Memory Model
The Java Memory Model guarantees:
- **Visibility**: volatile fields ensure cross-thread visibility of latest writes
- **Atomicity**: Reference assignment is atomic for all types except long/double
- **Ordering**: final fields guarantee initialization safety
- **Happens-Before**: Monitor enter/exit, volatile read/write establish ordering

### Interaction with Garbage Collection
- **Short-lived objects**: Iterators, temporary nodes collected in young generation
- **Tenured objects**: Long-lived collections promoted to old generation
- **GC roots**: Static collections prevent GC of all referenced objects
- **Reference types**: WeakHashMap uses weak references for automatic cleanup

### Performance Characteristics

#### When It's Fast
- Uniform hash distribution: most buckets have 0-1 elements
- Sequential access patterns: good cache locality
- Appropriate initial capacity: no resize overhead

#### When It's Slow
- Poor hash distribution: long chains, O(n) lookup
- Frequent resizing: repeated array allocation and rehashing
- Concurrent contention: threads competing for locks
- Cache misses: pointer chasing through linked list chains


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

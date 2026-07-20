# Memory Leaks — Performance

## Time Complexity

### Operation Costs
| Operation | Average Case | Worst Case | Notes |
|-----------|-------------|-----------|-------|
| Insert    | O(1)        | O(n)      | Resizing or collisions |
| Lookup    | O(1)        | O(n)      | Hash collisions |
| Delete    | O(1)        | O(n)      | After locating element |
| Contains  | O(1)        | O(n)      | Same as lookup |
| Iteration | O(n)        | O(n)      | Traverses all elements |
| Size      | O(1)        | O(1)      | Maintained as field |

### Amortized Analysis
Individual operations may be expensive (resize), but amortized cost over a sequence
of operations is O(1) for most data structures.

## Memory Footprint

### Object Overhead
- **Base object header**: 12-16 bytes (depending on JVM flags)
- **Reference**: 4 bytes (Compressed OOPs) or 8 bytes
- **int fields**: 4 bytes each
- **Padding**: Aligned to 8-byte boundaries

### Per-Element Storage
| Structure | Per-Element Overhead | Cache Locality |
|-----------|---------------------|----------------|
| HashMap   | ~32 bytes + K + V   | Poor (chains) |
| ArrayList | 4-8 bytes (ref)     | Excellent |
| LinkedList| ~24 bytes (3 ptrs)  | Poor |
| TreeMap   | ~40 bytes (4 ptrs)  | Poor |

## Optimization Techniques

### CPU Cache Behavior
- **Sequential access**: Array-based structures have excellent cache locality
- **Pointer chasing**: Linked structures have poor cache locality
- **False sharing**: Concurrent writes to adjacent memory cause cache line bouncing

### JIT Compilation
- **Inlining**: Small methods (get(), put()) are prime candidates for inlining
- **Intrinsification**: System.arraycopy() is a hotspot intrinsic
- **Branch Prediction**: Common-case fast paths benefit from predictable branching
- **Loop Unrolling**: Iteration over arrays can be unrolled by JIT

### GC Impact
- **Young Gen**: Short-lived iterators, temporary collections collected in young gen
- **Old Gen**: Long-lived maps and caches promoted to old gen
- **Allocation Rate**: Frequent resize causes allocation spikes
- **Card Table**: Large collections increase GC root scanning overhead

## Profiling Tools
- **JMH**: Java Microbenchmark Harness for accurate microbenchmarks
- **async-profiler**: CPU/wall-clock/allocation profiling
- **JFR**: Java Flight Recorder event collection
- **JOL**: Java Object Layout for memory analysis


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

# JMM Foundations — Mental Models

## Analogy 1: Library Index System
Think of JMM Foundations like a library's book indexing system:
- **HashMap**: The card catalog — find books quickly by title (key), but physical arrangement is arbitrary
- **TreeMap**: The Dewey Decimal System — books organized in sorted order for range queries
- **ArrayList**: A bookshelf where books are in order, easy to browse sequentially
- **LinkedList**: A treasure hunt where each book contains a clue pointing to the next book

## Analogy 2: Restaurant Kitchen
- **HashMap**: The chef's ingredient bin — grab any ingredient instantly if you know its name
- **TreeMap**: The walk-in refrigerator — ingredients sorted alphabetically for scanning
- **ArrayList**: The counter prep line — ingredients in the order they'll be used
- **LinkedList**: The dishwashing queue — each dirty dish points to the next in line

## Analogy 3: City Infrastructure
- **HashMap**: A postal system — deliver mail using zip codes (hash codes) to neighborhoods
- **TreeMap**: Street addresses — navigate by sorted street numbers
- **ArrayList**: Apartment building — floors numbered sequentially, elevator access is O(1)
- **LinkedList**: A circular bus route — each stop connects to the next, reaching specific stop requires riding through all previous

## Conceptual Model: The Bucket Array
Visualize the internal array as a row of buckets:
```
Bucket[0] -> Node(key1, val1) -> Node(key2, val2)  [2 elements]
Bucket[1] -> null                                   [empty]
Bucket[2] -> Node(key3, val3)                       [1 element]
...
Bucket[n] -> Node(key4, val4) -> ...                [chain]
```
Each bucket can hold multiple items chained together. The goal is to distribute items evenly.

## Conceptual Model: Tree Self-Balancing
For tree-based structures, think of a mobile hanging from the ceiling:
```
    +---+
    | 8 |  <- New weight added, tilts
    +---+
   /     \
+---+   +---+
| 3 |   | 12|
+---+   +---+
```
The tree rotates to rebalance:
```
    +---+
    | 5 |  <- After rotation, 5 becomes root
    +---+
   /     \
+---+   +---+
| 3 |   | 8 |
+---+   +---+
         \
        +---+
        | 12|
        +---+
```

## Mental Framework: When to Use What
```
Need fast key-value access, no ordering?   -> HashMap
Need sorted keys with range queries?       -> TreeMap
Need insertion order preserved?            -> LinkedHashMap
Need thread-safe concurrent access?        -> ConcurrentHashMap
Need fast index-based access?              -> ArrayList
Need fast insert/delete at ends?           -> LinkedList
Need unique elements?                      -> HashSet/TreeSet
Need min/max always available?             -> PriorityQueue
```

## Memory Model Visualization
```
Thread A: write to volatile field
    |
    v  (StoreStore + StoreLoad barriers)
    |
    All threads see the write
    |
Thread B: read from volatile field
    |
    v  (LoadLoad + LoadStore barriers)
    |
    Thread B sees Thread A's write
```


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

# Memory Leaks — Refactoring

## Refactoring Patterns

### Pattern 1: Replace Array with Collection
**Before**:
```java
String[] names = new String[100];
names[0] = "Alice";
int count = 1;
```
**After**:
```java
List<String> names = new ArrayList<>();
names.add("Alice");
```

### Pattern 2: Replace Raw Type with Generics
**Before**:
```java
List list = new ArrayList();
list.add("hello");
String s = (String) list.get(0);
```
**After**:
```java
List<String> list = new ArrayList<>();
list.add("hello");
String s = list.get(0);
```

### Pattern 3: Replace Legacy with Modern Variant
**Before**: Hashtable, Vector, Stack (synchronized overhead)
**After**: HashMap, ArrayList, ArrayDeque

### Pattern 4: Fix LinkedList Random Access
**Before**: list.get(i) in loop on LinkedList -> O(n^2)
**After**: Enhanced for-loop or ArrayList

### Pattern 5: Introduce EnumMap for Enum Keys
**Before**: Map<Color, String> map = new HashMap<>();
**After**: Map<Color, String> map = new EnumMap<>(Color.class);

### Pattern 6: Use Empty Collection Constants
**Before**: return new ArrayList<>(); // Allocates every call
**After**: return Collections.emptyList(); // Reuses singleton

## Step-by-Step Refactoring

### Step 1: Identify the Problem
- Profiling shows hotspot in collection code
- Excessive allocations in GC logs
- ConcurrentModificationException in logs
- High memory usage from collections

### Step 2: Choose the Right Alternative
- ArrayList vs LinkedList: Random access? Use ArrayList
- HashMap vs TreeMap: Need sorted order? Use TreeMap
- HashMap vs ConcurrentHashMap: Concurrent access? Use ConcurrentHashMap

### Step 3: Migrate Carefully
1. Change declaration to interface (if not already)
2. Update constructor to new implementation
3. Run existing tests
4. Profile to verify improvement

### Step 4: Measure Impact
- Throughput: Operations/second
- Latency: Average and P99
- Memory: Heap usage
- GC: Pause time and frequency


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

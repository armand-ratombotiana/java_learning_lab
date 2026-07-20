# DCL Pattern — Step-by-Step Implementation

## Step 1: Create Project Structure
```
my-project/
+-- src/main/java/com.javalab.05/
|   +-- MainImplementation.java
+-- src/test/java/com.javalab.05/
|   +-- MainImplementationTest.java
+-- pom.xml (or build.gradle)
```

## Step 2: Add Dependencies
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

## Step 3: Create Main Implementation
```java
package com.javalab.05;

import java.util.Objects;

public class MainImplementation<K, V> {
    private Node<K, V>[] table;
    private int size;
    private final float loadFactor;
    private int threshold;

    static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    @SuppressWarnings("unchecked")
    public MainImplementation(int initialCapacity, float loadFactor) {
        this.loadFactor = loadFactor;
        this.table = (Node<K, V>[]) new Node[initialCapacity];
        this.threshold = (int) (initialCapacity * loadFactor);
    }

    public MainImplementation() {
        this(16, 0.75f);
    }
}
```

## Step 4: Implement Core Operations

### Put Operation
Hash the key, find the bucket, traverse the chain, insert or update.

### Get Operation
Hash the key, find the bucket, traverse the chain, return value or null.

### Remove Operation
Hash the key, find the bucket, traverse the chain with prev pointer, unlink node.

### Resize
Double capacity, allocate new array, rehash all entries, update threshold.

## Step 5: Write Tests
Create JUnit 5 test class with @BeforeEach setup and test methods for:
- Basic put/get operations
- Update existing key
- Remove entries
- Size tracking
- Edge cases (nulls, empty, many entries)

## Step 6: Run and Verify
```bash
mvn test
```
All tests should pass. Check code coverage.


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

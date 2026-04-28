# Module 03: Collections Framework

## Overview
This module provides comprehensive coverage of the Java Collections Framework, including Lists, Sets, Maps, Queues, and custom collection implementations. It demonstrates best practices for choosing and using the right collection type for different scenarios.

## Learning Objectives
- Understand the Collections Framework hierarchy
- Master List implementations (ArrayList, LinkedList, CopyOnWriteArrayList)
- Understand Set implementations (HashSet, LinkedHashSet, TreeSet)
- Master Map implementations (HashMap, LinkedHashMap, TreeMap, ConcurrentHashMap)
- Understand Queue implementations (Queue, Deque, PriorityQueue, BlockingQueue)
- Create custom collections by implementing Collection interface
- Use Collections utility methods effectively
- Understand performance characteristics and thread-safety considerations

## Module Structure

### Source Code (src/main/java/com/learning/)

#### Lists Package
- **ListInterfaceDemo.java** - List interface contract, CRUD operations, iteration
- **ArrayListDemo.java** - Dynamic array-based implementation, indexed access
- **LinkedListDemo.java** - Node-based implementation, Deque interface operations
- **CopyOnWriteArrayListDemo.java** - Thread-safe list for concurrent reads
- **ListComparatorDemo.java** - Sorting operations, custom comparators

#### Sets Package
- **SetInterfaceDemo.java** - Set contract, uniqueness guarantees
- **HashSetDemo.java** - Unordered set with O(1) operations
- **LinkedHashSetDemo.java** - Insertion-order preserving set
- **TreeSetDemo.java** - Sorted set with navigable operations

#### Maps Package
- **MapInterfaceDemo.java** - Key-value mapping, interface fundamentals
- **HashMapDemo.java** - Unordered map with putIfAbsent, getOrDefault, replace
- **LinkedHashMapDemo.java** - Access-order map, LRU cache patterns
- **TreeMapDemo.java** - Sorted map, range operations, navigation methods
- **ConcurrentHashMapDemo.java** - Thread-safe with atomic operations

#### Queues Package
- **QueueInterfaceDemo.java** - FIFO contract, offer/poll/peek operations
- **PriorityQueueDemo.java** - Min/max heap with custom comparators
- **DequeDemo.java** - Double-ended queue, stack/queue duality
- **BlockingQueueBasicsDemo.java** - Producer-consumer pattern, blocking operations

#### Custom & Utilities
- **CustomCollectionExample.java** - SimpleStack<E> custom collection implementation
- **CollectionsUtilityDemo.java** - Collections static methods, immutable collections, binary search

### Test Code (src/test/java/com/learning/)

**138 comprehensive test methods across 10 test classes:**

| Test Class | Test Methods | Coverage |
|-----------|-------------|----------|
| ArrayListTests | 16 | CRUD, indexing, iteration, sorting, streams |
| LinkedListTests | 15 | Head/tail ops, Deque interface, stack behavior |
| HashSetTests | 15 | Uniqueness, set operations, null handling |
| TreeSetTests | 15 | Natural ordering, navigation, range queries |
| HashMapTests | 17 | Key-value operations, lambda iteration |
| TreeMapTests | 14 | Sorted ordering, range operations, navigation |
| ConcurrentHashMapTests | 14 | Thread-safe operations, atomic compute |
| DequeTests | 13 | Double-ended operations, bidirectional iteration |
| PriorityQueueTests | 9 | Heap behavior, comparators, ordering |
| CollectionsIntegrationTests | 10 | Cross-collection operations, grouping |

## Quick Start

### Running the Main Demonstration
```bash
cd 03-collections-framework
mvn clean compile exec:java -Dexec.mainClass="com.learning.Main"
```

### Running the Tests
```bash
mvn clean test -Djacoco.skip=true
```

### Building with Code Coverage
```bash
mvn clean test
# Report generated at target/site/jacoco/index.html
```

## Key Concepts Covered

### Lists
- **ArrayList**: Resizable array, O(1) access, O(n) insert/delete (except append)
- **LinkedList**: Node-based, O(n) access, O(1) insert/delete at head/tail
- **CopyOnWriteArrayList**: Thread-safe, iteration-safe, expensive writes

### Sets
- **HashSet**: Unordered, O(1) operations, provides uniqueness
- **LinkedHashSet**: Insertion-order, O(1) operations
- **TreeSet**: Sorted, O(log n) operations, range queries

### Maps
- **HashMap**: Unordered, O(1) operations, allows one null key
- **LinkedHashMap**: Insertion-order or access-order, LRU cache patterns
- **TreeMap**: Sorted keys, O(log n) operations, range/navigation methods
- **ConcurrentHashMap**: Segment-based locking, thread-safe without full synchronization

### Queues
- **Queue**: FIFO interface with offer/poll/peek operations
- **Deque**: Double-ended queue, works as stack or queue
- **PriorityQueue**: Heap-based ordering with custom comparators
- **BlockingQueue**: Blocking operations for producer-consumer patterns

## Performance Considerations

| Collection | Add | Remove | Get | Space |
|-----------|-----|--------|-----|-------|
| ArrayList | O(1)* | O(n) | O(1) | O(n) |
| LinkedList | O(1) | O(1)* | O(n) | O(n) |
| HashSet | O(1) | O(1) | O(1) | O(n) |
| TreeSet | O(log n) | O(log n) | O(log n) | O(n) |
| HashMap | O(1) | O(1) | O(1) | O(n) |
| TreeMap | O(log n) | O(log n) | O(log n) | O(n) |

*Average case for appending to ArrayList; O(n) if insertion triggers resize
*Head/tail operations on LinkedList

## Common Patterns

### LRU Cache with LinkedHashMap
```java
LinkedHashMap<String, Integer> lru = new LinkedHashMap<String, Integer>(16, 0.75f, true) {
    protected boolean removeEldestEntry(Map.eldest eldest) {
        return size() > 100;
    }
};
```

### Stream Processing with Collections
```java
list.stream()
    .filter(x -> x > 0)
    .map(x -> x * 2)
    .collect(Collectors.toList());
```

### Set Operations
```java
Set<String> union = new HashSet<>(set1);
union.addAll(set2);

Set<String> intersection = new HashSet<>(set1);
intersection.retainAll(set2);

Set<String> difference = new HashSet<>(set1);
difference.removeAll(set2);
```

## Thread Safety

| Collection | Thread-Safe | Notes |
|-----------|-------------|-------|
| ArrayList | No | Use CopyOnWriteArrayList or synchronizedList |
| LinkedList | No | Use Collections.synchronizedList |
| HashSet | No | Use Collections.synchronizedSet |
| HashMap | No | Use ConcurrentHashMap for concurrent access |
| TreeMap | No | Use Collections.synchronizedSortedMap |

## Prerequisites
- Module 01: Java Basics (Variables, operators, control flow)
- Module 02: OOP Concepts (Classes, inheritance, polymorphism)

## Dependencies
- Java 21+
- JUnit 5.10.1
- AssertJ 3.24.2
- Mockito 5.8.0 (for advanced tests)

## Certification Objectives
After completing this module, you will be able to:
✅ Choose appropriate collection types for different scenarios
✅ Use ArrayList, LinkedList, and other List implementations effectively
✅ Implement Set operations correctly
✅ Use HashMap, TreeMap, and other Map implementations
✅ Apply Queue and Deque patterns
✅ Write custom collections implementing the Collection interface
✅ Understand thread-safety and synchronization strategies
✅ Handle performance implications of different collection choices

## Status: PRODUCTION READY ✅
- Tests: 138 passing (100% pass rate)
- Coverage: 80%+ code coverage
- Build: mvn clean test succeeds
- Documentation: Complete

## Next Steps
- Study Module 04: Streams API for functional operations on collections
- Explore Module 05: Lambda Expressions for working with collections
- Review concurrency patterns in Module 06: Multithreading

## References
- [Java Collections Framework](https://docs.oracle.com/javase/tutorial/collections/)
- [Collections API Documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collection.html)
- [Effective Java - Chapter 2-3](https://www.oreilly.com/library/view/effective-java-3rd/9780134685991/)

---
**Created**: March 5, 2026
**Modified**: March 5, 2026
**Java Version**: 21 (LTS)
**Status**: ✅ PRODUCTION READY

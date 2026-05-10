# Module 03: Collections - Solution

## Overview
This solution provides comprehensive reference implementations for Java Collections Framework, including standard implementations, utilities, and custom implementations.

## Package Structure
```
com.learning.lab.module03.solution
```

## Solution Components

### 1. Solution.java
Complete implementations covering all major collection interfaces:

#### List Implementations
- **ArrayList**: Dynamic array, O(1) access, O(n) insert/remove
- **LinkedList**: Doubly-linked list, O(1) insert/remove, O(n) access
- **Vector**: Synchronized ArrayList with legacy compatibility
- **Stack**: LIFO data structure extending Vector

#### Set Implementations
- **HashSet**: Unordered set using hash table, O(1) operations
- **LinkedHashSet**: Ordered set maintaining insertion order
- **TreeSet**: Sorted set using Red-Black tree, O(log n) operations
- **EnumSet**: Specialized set for enum types

#### Map Implementations
- **HashMap**: Key-value pairs with O(1) average operations
- **LinkedHashMap**: Ordered map maintaining insertion/access order
- **TreeMap**: Sorted map by keys, O(log n) operations
- **Hashtable**: Synchronized legacy map implementation
- **ConcurrentHashMap**: Thread-safe map with fine-grained locking

#### Queue Implementations
- **PriorityQueue**: Heap-based queue with priority ordering
- **ArrayDeque**: Double-ended queue, O(1) operations
- **BlockingQueue**: Thread-safe queue with blocking operations

#### Custom Implementations
- **ArrayListCustom**: Custom ArrayList with growable array
- **HashMapCustom**: Custom hash map with chaining collision handling

### 2. Test.java
Comprehensive test suite with 40+ tests covering:
- Basic operations (add, remove, get)
- Edge cases and boundaries
- Performance characteristics
- Thread-safety concepts
- Custom implementation correctness

## Running the Solution

```bash
cd 03-collections/SOLUTION
javac -d . Solution.java Test.java
java com.learning.lab.module03.solution.Solution
java com.learning.lab.module03.solution.Test
```

## Key Concepts

### List Operations
| Operation | ArrayList | LinkedList |
|-----------|-----------|------------|
| get(i)    | O(1)      | O(n)       |
| add(e)    | O(1) amort| O(1)       |
| add(i,e)  | O(n)      | O(1)       |
| remove(i) | O(n)      | O(1)       |

### Set Characteristics
| Implementation | Order | Time Complexity |
|----------------|-------|-----------------|
| HashSet        | None  | O(1)            |
| LinkedHashSet  | Insertion | O(1)      |
| TreeSet        | Sorted| O(log n)        |

### Map Operations
| Operation | HashMap | TreeMap | LinkedHashMap |
|-----------|---------|---------|---------------|
| put       | O(1)    | O(log n)| O(1)          |
| get       | O(1)    | O(log n)| O(1)          |
| containsKey | O(1) | O(log n)| O(1)         |

### Queue Types
- **FIFO Queue**: Standard queue ordering
- **Priority Queue**: Elements ordered by priority
- **Deque**: Double-ended queue for FIFO/LIFO

## Collection Utilities

### Collections Class Methods
- `sort()`, `reverse()`, `shuffle()`, `swap()`
- `synchronizedList()`, `synchronizedSet()`, `synchronizedMap()`
- `unmodifiableList()`, `unmodifiableSet()`, `unmodifiableMap()`
- `fill()`, `disjoint()`, `frequency()`

### Arrays Class Methods
- `asList()`: Convert array to list
- `sort()`, `binarySearch()`
- `copyOf()`, `copyOfRange()`
- `fill()`, `equals()`

## Best Practices

1. **Choose the right collection**:
   - Need fast lookup? → HashMap/HashSet
   - Need sorted? → TreeMap/TreeSet
   - Need order preserved? → LinkedHashMap/LinkedHashSet

2. **Use interfaces**:
   - `List<String> list = new ArrayList<>()`
   - `Map<K,V> map = new HashMap<>()`

3. **Consider performance**:
   - ArrayList for frequent random access
   - LinkedList for frequent insertions/deletions

4. **Thread safety**:
   - Use concurrent collections in multi-threaded environments
   - Collections.synchronizedXxx for wrapper alternatives

5. **Immutability**:
   - Use Collections.unmodifiableXxx for read-only collections
   - List.of(), Set.of(), Map.of() for Java 9+ immutable factories

## Common Pitfalls

- Modifying collection while iterating (use Iterator.remove())
- Not choosing appropriate initial capacity
- Ignoring equals/hashCode contract for custom types
- Using mutable objects as keys
- Forgetting about null handling in different implementations

## When to Use Each Collection

| Use Case | Recommended Collection |
|----------|----------------------|
| Unique elements, fast lookup | HashSet |
| Unique sorted elements | TreeSet |
| Key-value pairs, fast lookup | HashMap |
| Key-value pairs sorted by key | TreeMap |
| Frequent insertions at ends | ArrayDeque/LinkedList |
| Priority-based processing | PriorityQueue |
| Thread-safe operations | ConcurrentHashMap |
| Enum as key | EnumMap/EnumSet |
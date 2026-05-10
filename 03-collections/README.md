# Java Collections Module

## Overview

This module covers the Java Collections Framework, essential data structures, and performance characteristics for building efficient Java applications.

## Learning Objectives

- Master all collection interfaces and implementations
- Choose appropriate collections for different scenarios
- Understand time complexity of operations
- Work with maps and advanced collections
- Implement custom comparators

## Module Structure

| Document | Purpose |
|----------|---------|
| README.md | Module overview |
| DEEP_DIVE.md | Comprehensive concepts |
| EXERCISES.md | Practice problems |
| QUIZZES.md | Knowledge tests |
| EDGE_CASES.md | Tricky scenarios |
| PEDAGOGIC_GUIDE.md | Teaching methodology |
| PROJECTS.md | Hands-on projects |

## Key Collections

### List Interface
- `ArrayList` - Dynamic array, fast random access
- `LinkedList` - Doubly linked list, fast insertions
- `Vector` - Synchronized ArrayList

### Set Interface
- `HashSet` - Hash-based, no duplicates
- `LinkedHashSet` - Maintains insertion order
- `TreeSet` - Sorted, red-black tree

### Map Interface
- `HashMap` - Key-value pairs, O(1) operations
- `LinkedHashMap` - Maintains order
- `TreeMap` - Sorted keys
- `Hashtable` - Synchronized

### Queue Interface
- `PriorityQueue` - Heap-based
- `ArrayDeque` - Resizable array
- `LinkedList` - Queue operations

## Quick Example

```java
List<String> list = new ArrayList<>();
list.add("Apple");
list.add("Banana");

Map<String, Integer> map = new HashMap<>();
map.put("Apple", 1);
map.put("Banana", 2);

Set<String> set = new TreeSet<>();
set.addAll(list);
```

## Next Steps

- **Module 4**: Stream API for collection processing
- **Module 5**: Concurrency with concurrent collections

## Resources

- [Java Collections Documentation](https://docs.oracle.com/javase/tutorial/collections/)

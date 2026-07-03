# Why the Collections Framework Exists

## The Problem Before Collections

Before Java 1.2 (1998), Java had:
- `Vector` — synchronized resizable array (slow)
- `Hashtable` — synchronized hash table (slow)
- `Stack` — extends Vector (poor design)
- `Enumeration` — verbose iteration
- `Arrays` — fixed-size, no dynamic resizing

There was no unified framework. Each class had its own API. Switching from `Vector` to `Hashtable` required completely different code. There were no standard algorithms (sorting, searching) that worked across data structures.

## The Solution: A Unified Framework

The Collections Framework introduced:
- **Interfaces** (`List`, `Set`, `Map`) — abstract the concept, decouple API from implementation
- **Implementations** (`ArrayList`, `HashSet`, `HashMap`) — choose based on performance needs
- **Algorithms** (`Collections.sort()`, `Collections.binarySearch()`) — reusable across implementations
- **Utility wrappers** (`synchronizedX`, `unmodifiableX`) — add behavior via composition

## Design Goals

1. **Small core**: Few interfaces, many implementations
2. **Interoperability**: Common supertype (`Collection`) enables generic algorithms
3. **Performance**: Each implementation documents time complexity
4. **Thread safety optional**: Unmodifiable and synchronized wrappers separate concerns
5. **Immutability**: Factory methods (`List.of()`) for immutable collections (Java 9+)

## Impact

The Collections Framework is the most used part of the Java standard library. Every Java application depends on it. It influenced C++ STL adoption patterns and .NET's `System.Collections.Generic` namespace.

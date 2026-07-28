# Interview Questions: Foreign Function & Memory API

## Basic
1. What is the Foreign Function & Memory (FFM) API?
2. What is a `MemorySegment`? What is an `Arena`?
3. How does FFM compare to JNI?

## Intermediate
4. Explain the different arena types: confined vs shared.
5. How do you define a C struct layout in FFM?
6. How do you call a native function using `Linker`?

## Advanced
7. What is an upcall and how does it work?
8. How does FFM handle memory alignment and endianness?
9. What happens if you access a segment after its arena is closed?
10. How do `MemoryLayout` paths work for nested structs?

## Expert
11. How does FFM integrate with the garbage collector for reachability?
12. What is the `SegmentAllocator` interface and how does it improve allocation?
13. How does the JIT optimize FFM downcall sites?
14. Compare FFM's `VaList` with C's `stdarg.h` for variadic functions.

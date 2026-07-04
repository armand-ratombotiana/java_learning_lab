# Reflection: Arrays

## What I Learned

- Arrays provide O(1) indexed access via contiguous memory layout and pointer arithmetic
- Dynamic arrays amortize resizing cost to O(1) per operation using geometric growth
- Java arrays are objects with runtime bounds checking — a key security advantage over C
- Cache locality makes sequential array access dramatically faster than pointer-chasing structures
- The 1.5x growth factor (ArrayList) is a deliberate trade-off between time and memory

## Questions to Consider

1. Why does Java use 1.5x instead of 2x for ArrayList growth? (Memory efficiency vs copy frequency)
2. When would you choose a linked list over an array despite arrays being faster? (Insertion/deletion at arbitrary positions)
3. How does the JVM optimize array bounds checking? (Loop unrolling, range-check elimination)
4. What happens when you create a really large array? (OutOfMemoryError, GC pressure)
5. How would you implement a sparse array with millions of elements where most are zero?

## Connections

Arrays connect to:
- **Dynamic arrays** (ArrayList, Vector)
- **Strings** (char[] internally)
- **Hash tables** (bucket arrays)
- **Heaps** (array-backed binary heap)
- **Graphs** (adjacency matrix)
- **Sorting** (all comparison sorts use arrays)

## Self-Assessment

- [ ] Can implement a dynamic array from scratch
- [ ] Understand amortized analysis for resizing
- [ ] Can analyze cache behavior of array access patterns
- [ ] Know when to use arrays vs other structures
- [ ] Can solve two-pointer and sliding window problems

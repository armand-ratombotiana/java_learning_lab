# Architecture — Sorting Basics

## Java Standard Library Sorting
`
java.util.Arrays
  ├── sort(int[]) — Dual-Pivot QuickSort
  ├── sort(Object[]) — TimSort
  └── parallelSort(int[]) — ForkJoin-based

java.util.Collections
  └── sort(List<T>) — TimSort
`

## When to Build Custom
- Sorting custom objects with specific comparison rules
- External sorting (data too large for memory)
- Embedded systems with constrained resources

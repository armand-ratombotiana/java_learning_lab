# History: Heaps

## Binary Heap (1964)

- **1964**: **J.W.J. Williams** invented the binary heap at the University of Cambridge. He published **Heapsort** as Algorithm 232 in the Communications of the ACM. The paper was titled "Algorithm 232: Heapsort."
- **1964**: **Robert W. Floyd** improved heapsort by discovering the **heapify** (build heap) algorithm, which runs in O(n) time instead of O(n log n). He published "Algorithm 245: Treesort 3."

## Heap Variants (1970s–1990s)

- **1972**: **Binomial heaps** by Jean Vuillemin — supports O(log n) merge
- **1977**: **Fibonacci heaps** by Michael L. Fredman and Robert E. Tarjan — O(1) amortized decrease-key, O(log n) extract-min. Used in optimized Dijkstra and Prim's algorithms
- **1986**: **Pairing heaps** by Fredman, Sedgewick, Sleator, and Tarjan — simpler than Fibonacci, practical for most uses
- **1987**: **Skew heaps** — self-adjusting heap variant of leftist heaps

## Priority Queues in Java

- **Java 1.2** (1998): `java.util.PriorityQueue` based on binary heap
- **Java 1.5** (2004): `PriorityQueue` with generics
- **Java 8**: Default capacity 11, custom comparator support
- **Java 8+**: Internal optimizations

## Modern Use

- **Java**: `PriorityQueue` is the standard heap implementation
- **Concurrency**: `PriorityBlockingQueue` for thread-safe priority operations
- **Third-party**: Guava's `MinMaxPriorityQueue` for both min and max access
- **Specialized**: `TreeMap` is often used when ordered traversal is needed along with priority

## Related Advances

- **Soft heaps** (2000, Chazelle): allows "corruptions" for O(1) amortized insert/delete
- **Brodal queues** (1996): theoretical optimal queue with O(1) all operations (impractical constant factors)
- **Cache-oblivious heaps**: optimized for multi-level memory hierarchies

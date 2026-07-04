# Why Heaps Matter

Heaps matter because **priority-based processing** is universal in computing, and heaps provide the most efficient implementation.

## Practical Impact

- **Priority queues**: every OS has a process scheduler using a heap or similar structure
- **Dijkstra's algorithm**: heap-based PQ enables O((V+E) log V) shortest paths
- **Huffman coding**: heap builds the optimal prefix code tree
- **Heap sort**: O(n log n) sorting with O(1) extra space
- **Streaming algorithms**:
  - Median of a stream (two heaps)
  - Top K elements (min-heap of size K)
  - Merge K sorted lists (min-heap of heads)
- **Event-driven simulation**: future events stored in a heap by timestamp

## Why Learn Heaps

1. **Priority queue foundation**: understanding heaps means understanding how PQs work
2. **Array-backed tree**: heaps demonstrate how a complete binary tree maps to an array efficiently
3. **Heapify analysis**: building a heap in O(n) is a classic algorithm analysis insight
4. **Interview essential**: K largest/smallest, merge K sorted, median of stream — all heap problems
5. **Algorithm building block**: heap is used in Dijkstra, Prim's, A*, Huffman, and more

Heaps are the **bridge between trees and arrays** — they demonstrate that an efficient tree structure can be stored in a simple array with no explicit pointers.

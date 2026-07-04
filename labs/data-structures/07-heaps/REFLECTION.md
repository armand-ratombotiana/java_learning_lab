# Reflection: Heaps

## What I Learned

- Heaps are complete binary trees stored in arrays, providing O(log n) insert/extract
- The heap property (parent ≤ children for min-heap) is a partial ordering
- siftUp and siftDown maintain the heap property after modifications
- Heapify builds a heap from an array in O(n) time (bottom-up approach)
- PriorityQueue in Java is a min-heap by default, configurable via Comparator
- Heap sort is an in-place O(n log n) sorting algorithm

## Questions to Consider

1. Why is heapify O(n) while n inserts are O(n log n)?
2. When would you choose a heap over a balanced BST for priority operations?
3. How does the array representation of a heap compare to a node-based tree in terms of cache behavior?
4. What are the trade-offs of decreasing the load factor for a heap? (not applicable — heaps don't have load factors)
5. How would you implement a mergeable heap (like a binomial heap)?

## Connections

Heaps connect to:
- **Priority queues** (PriorityQueue is a heap)
- **Sorting** (heap sort is the basis)
- **Graph algorithms** (Dijkstra, Prim's use heaps)
- **Trees** (a heap is a complete binary tree)
- **Arrays** (the heap is array-backed)
- **Median tracking** (two-heap pattern)

## Self-Assessment

- [ ] Can implement a binary heap from scratch with array backing
- [ ] Can implement heap sort
- [ ] Understand heapify (build heap) O(n) analysis
- [ ] Can solve top K and median of stream problems
- [ ] Understand Java PriorityQueue internals
- [ ] Know when to use heap vs other structures

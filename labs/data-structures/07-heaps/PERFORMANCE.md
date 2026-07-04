# Performance: Heaps

## Time Complexity

| Operation | Binary Heap | Fibonacci Heap |
|-----------|------------|---------------|
| Insert | O(log n) | O(1) |
| Extract min | O(log n) | O(log n) |
| Peek | O(1) | O(1) |
| Decrease key | O(log n) | O(1)* |
| Delete | O(log n) | O(log n) |
| Merge | O(n) | O(1) |
| Build (heapify) | O(n) | O(n) |

\*Amortized

## Space Complexity

Binary heap: O(n) for the array + O(1) extra space.

## Heapify Complexity Analysis

Bottom-up heapify:
- Number of nodes at height h: ≤ n / 2^{h+1}
- siftDown time at height h: O(h)
- Total: Σ_{h=0}^{log n} (n / 2^{h+1}) × O(h) = O(n)

## Cache Performance

Binary heap (array-backed) has excellent cache locality:
- Sequential array traversal for siftDown
- Parents and children are close in memory (index arithmetic)
- Contrast with node-based BSTs (pointer chasing)

## PriorityQueue Performance in Practice

| Operation | Time (1M elements) |
|-----------|-------------------|
| Insert | ~50-100 ns |
| Extract | ~50-100 ns |
| Peek | ~10 ns |
| Heapify | ~30 ms |

## Java PriorityQueue vs TreeMap

| Aspect | PriorityQueue | TreeMap |
|--------|--------------|---------|
| Structure | Heap (array) | Red-Black tree |
| Insert | O(log n) | O(log n) |
| Find min | O(1) | O(1) |
| Remove min | O(log n) | O(log n) |
| Remove arbitrary | O(n) | O(log n) |
| Memory | Less (array) | More (node refs) |
| Cache | Better | Worse |

## Profiling Tips

- Use `ArrayDeque` if you don't need priority ordering (faster)
- Use `TreeSet` if you need to remove arbitrary elements frequently
- Batch inserts are faster with `heapify` than individual inserts
- Consider `ArrayList + sort` for one-time sorting of small collections

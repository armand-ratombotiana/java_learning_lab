# Performance: Stacks & Queues

## Time Complexity

| Operation | ArrayDeque (Stack) | ArrayDeque (Queue) | LinkedList | PriorityQueue |
|-----------|-------------------|-------------------|------------|---------------|
| push/offer | O(1)* | O(1)* | O(1) | O(log n) |
| pop/poll | O(1) | O(1) | O(1) | O(log n) |
| peek | O(1) | O(1) | O(1) | O(1) |
| size | O(1) | O(1) | O(1) | O(1) |

\*Amortized — may resize by doubling

## Memory Comparison

For 1M elements:

| Structure | Memory | Notes |
|-----------|--------|-------|
| ArrayDeque (primitives) | ~8 MB | int[] backed |
| ArrayDeque (references) | ~8 MB + object overhead | Object[] backed |
| LinkedList | ~40 MB + object overhead | Node per element |
| Stack | ~8 MB + synchronized overhead | Extends Vector |

## ArrayDeque vs LinkedList Performance

| Aspect | ArrayDeque | LinkedList |
|--------|-----------|------------|
| Cache locality | Excellent (contiguous array) | Poor (scattered nodes) |
| Null elements | Not allowed | Allowed |
| Memory per element | ~4 bytes (ref) | ~28 bytes (node) |
| addFirst/addLast | O(1) amortized | O(1) |
| Remove from middle | O(n) | O(n) |
| Random access | Not supported | O(n) |

## PriorityQueue Performance

- **Insert**: O(log n) — siftUp from leaf
- **Remove head**: O(log n) — siftDown from root
- **Build heap**: O(n) — Floyd's heapify (bottom-up)
- **Remove arbitrary element**: O(n) — linear search + O(log n) sift
- **contains**: O(n) — linear scan

## Profiling Notes

- `ArrayDeque` is the fastest stack/queue in Java — prefer it for new code
- `PriorityQueue` uses array-backed binary heap — good cache behavior
- For concurrent access: `LinkedBlockingQueue` (separate locks) or `ConcurrentLinkedDeque` (lock-free)

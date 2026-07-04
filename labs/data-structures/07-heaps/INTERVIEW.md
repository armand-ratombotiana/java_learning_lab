# Interview Questions: Heaps

## Easy

1. **Kth largest element** — Find the kth largest element in an array (min-heap of size k or quickselect).

2. **Kth smallest element** — Find the kth smallest element (max-heap of size k).

3. **Min vs max heap** — Given an array, determine if it's a min-heap or max-heap.

4. **Last stone weight** — Smash stones; always smash two heaviest (max-heap).

## Medium

5. **Top K frequent elements** — Most frequent k elements (HashMap + min-heap of size k).

6. **K closest points to origin** — Euclidean distance (max-heap of size k).

7. **Merge k sorted lists** — Min-heap of list heads.

8. **Find median from data stream** — Two heaps (max for lower half, min for upper half).

9. **Task scheduler** — Schedule tasks with cooldown (max-heap + queue for cooldown).

10. **Ugly number II** — Find nth ugly number (min-heap or DP).

## Hard

11. **Sliding window median** — Median of every window of size k (two heaps with lazy deletion).

12. **Trapping rain water II** — Water trapped in 2D grid (BFS + min-heap of boundary cells).

13. **IPO (Maximum Capital)** — Projects with capital and profit; maximize capital given initial w (two heaps: min by capital, max by profit).

14. **Kth smallest in sorted matrix** — Matrix sorted row-wise and column-wise (min-heap of first row/column).

## Key Patterns

- **Min-heap**: keep k largest elements
- **Max-heap**: keep k smallest elements
- **Two heaps**: median tracking, sliding window median
- **Heap + map**: top K frequent, task scheduler
- **Custom comparator**: ordering by specific criteria

## Java-Specific Topics

- `PriorityQueue` with custom `Comparator` (lambda or Comparator.comparing)
- `PriorityBlockingQueue` for thread-safe operations
- Min vs max heap via Comparator direction
- O(n) `remove(Object)` — avoid when possible
- Size-limited heap pattern: `if (pq.size() > k) pq.poll()`

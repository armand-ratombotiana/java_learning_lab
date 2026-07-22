# Interview Questions: Heaps

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 215 Kth Largest Element in an Array](https://leetcode.com/problems/kth-largest-element-in-an-array/) | Medium | Amazon, Meta, Google, Microsoft, Apple | Min-heap / quickselect |
| [LC 347 Top K Frequent Elements](https://leetcode.com/problems/top-k-frequent-elements/) | Medium | Amazon, Meta, Google, Microsoft, Apple | HashMap + min-heap / bucket sort |
| [LC 23 Merge k Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/) | Hard | Amazon, Meta, Google, Microsoft, Apple | Min-heap of list heads |
| [LC 295 Find Median from Data Stream](https://leetcode.com/problems/find-median-from-data-stream/) | Hard | Google, Amazon, Meta, Microsoft, Apple | Two heaps (max + min) |
| [LC 378 Kth Smallest Element in a Sorted Matrix](https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/) | Medium | Amazon, Meta, Google, Microsoft | Min-heap row-by-row |
| [LC 973 K Closest Points to Origin](https://leetcode.com/problems/k-closest-points-to-origin/) | Medium | Amazon, Meta, Google, Microsoft, Apple | Max-heap / quickselect |
| [LC 1046 Last Stone Weight](https://leetcode.com/problems/last-stone-weight/) | Easy | Amazon, Google, Meta, Microsoft | Max-heap simulation |
| [LC 621 Task Scheduler](https://leetcode.com/problems/task-scheduler/) | Medium | Amazon, Meta, Google, Microsoft | Max-heap + cooldown queue |
| [LC 480 Sliding Window Median](https://leetcode.com/problems/sliding-window-median/) | Hard | Google, Amazon, Meta | Two heaps with lazy deletion |
| [LC 703 Kth Largest Element in a Stream](https://leetcode.com/problems/kth-largest-element-in-a-stream/) | Easy | Amazon, Google, Meta, Microsoft | Min-heap of size k |
| [LC 502 IPO](https://leetcode.com/problems/ipo/) | Hard | Amazon, Google, Meta | Two heaps (capital, profit) |
| [LC 407 Trapping Rain Water II](https://leetcode.com/problems/trapping-rain-water-ii/) | Hard | Google, Amazon, Meta | BFS + min-heap boundary |

## NeetCode Reference
NeetCode 150: Heap / Priority Queue category — 8 problems covering all essential patterns.

## Company-Specific Questions

### Google
- Design a data structure to find the median of a stream of integers (two heaps — proof of correctness)
- Kth largest element in an array — compare min-heap vs quickselect (Hoare's selection algorithm)
- Find all k closest stars to Earth given streaming star coordinates
- How would you implement a priority queue from scratch using a binary heap?

### Microsoft
- Merge k sorted lists — compare min-heap vs divide-and-conquer approaches
- Sliding window median — how to remove elements from the middle of a heap (lazy deletion pattern)
- Task scheduler — how to minimize idle time given cooldown between same tasks

### Meta
- Top K frequent elements — compare heap approach vs bucket sort (counting sort by frequency)
- K closest points to origin — max-heap of size k vs quickselect, trade-offs
- Last stone weight — why max-heap is the natural choice for this simulation

### Amazon
- Merge k sorted lists — what if the lists are streaming (infinite length)?
- Find median from data stream — how would you handle the data if it's on disk?
- Design an order matching engine that prioritizes highest bid and lowest ask (two priority queues)

### Apple
- How would you implement a bounded priority queue (only keep top K items)?
- Kth smallest in sorted matrix — what are the space vs time trade-offs?
- Design a real-time audio sample priority buffer (sample timestamp priority)

### Oracle
- How does `PriorityQueue` handle ordering? What happens with equal-priority elements?
- How would you make a priority queue thread-safe? What about `PriorityBlockingQueue`?
- What is the time complexity of `remove(Object)` on a `PriorityQueue`? How to avoid it?
- Compare heap-based priority queue vs balanced BST for priority queue operations

## Real Production Scenarios

- **Scenario 1: Order Matching Engine** — A trading exchange maintains two priority queues: a max-heap for buy orders (highest price first) and a min-heap for sell orders (lowest price first). When a new order arrives, it's matched against the best opposing order. The heaps process thousands of orders per second.

- **Scenario 2: Streaming Analytics** — A real-time dashboard processes a stream of events (page views, errors, latencies). For "top K" queries (top 10 visited pages, slowest 5 endpoints), a min-heap of size K maintains the top K elements in O(log K) per event.

- **Scenario 3: Task Scheduler** — A job scheduler with priority-based execution maintains a `PriorityBlockingQueue` of tasks. Higher-priority tasks (specified by the user) are dequeued first. Workers poll the queue; when a task completes, it checks if dependent tasks are ready to submit.

## Interview Tips

- Time: O(log n) for push/poll, O(n) for building heap (heapify), O(k log n) for top k, O(1) for peek
- Space: O(k) for top-k problems, O(n) for storing all elements
- Common edge cases: k = 0, k > n, empty heap, all equal values, negative numbers
- Two-heap pattern: maintain max-heap for lower half and min-heap for upper half (median tracking)
- Lazy deletion pattern: use a secondary HashMap to track elements to be "removed" from heap; skip them when they pop
- Quickselect is O(n) average but O(n²) worst case; heap is O(n log k) guaranteed

## Java-Specific Considerations

- `PriorityQueue<E>` — binary min-heap by default, max-heap via `Comparator.reverseOrder()` or `(a, b) -> b - a`
- `PriorityBlockingQueue<E>` — thread-safe, unbounded, uses ReentrantLock with condition
- `Comparator` direction: natural ordering is ascending (min-heap); `Comparator.reversed()` gives descending (max-heap)
- `PriorityQueue` is not stable — elements with equal priority have no guaranteed order
- `remove(Object)` and `contains(Object)` are O(n) — avoid in performance-critical code
- Size-limited heap pattern: `if (pq.size() > k) pq.poll();` — maintains heap of exactly k elements
- `pq.toArray()` returns an array — `Arrays.sort(pq.toArray())` if you need sorted order without destroying the heap
- `PriorityQueue` uses a growable array (initial capacity 11, grows by 50% or less)
- For integer priority: use `Comparator.comparingInt(a -> a)` to avoid boxing overhead of subtraction-based comparators

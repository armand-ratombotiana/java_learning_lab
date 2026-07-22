# Interview Questions: Priority Queue

## Company-Specific Focus

### Google
- PriorityQueue: binary min-heap implementation (by default)
- Ordering: natural ordering or Comparator; head is smallest
- Internal heap: Object[] queue with heap property

### Microsoft
- PriorityQueue vs C# PriorityQueue<T>: similar heap-based implementation
- Time complexity: offer/poll O(log n), peek O(1)

### Amazon
- PriorityQueue for task scheduling: prioritize urgent tasks
- Merge K sorted streams: using PriorityQueue for k-way merge
- topK problems: maintain a min-heap of size K for top K elements

### Meta
- Heap operations: siftUp (insert), siftDown (remove root)
- Heapify: O(n) bottom-up heap construction from existing array
- Not thread-safe: use PriorityBlockingQueue for concurrent access

### Apple
- PriorityQueue with custom Comparators
- Fail-fast iterator: modCount tracked
- No null elements allowed: throws NullPointerException

### Oracle
- PriorityQueue JCF specification: unbounded priority queue based on heap
- Serialization support: not guaranteed for Comparator
- Capacity: default initial capacity 11, grows automatically

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 215 Kth Largest Element in an Array | Medium | Google, Amazon, Meta | Min-heap of size K |
| 347 Top K Frequent Elements | Medium | Amazon, Google, Apple | Frequency map + min-heap |
| 23 Merge k Sorted Lists | Hard | Amazon, Google, Apple | Min-heap for k-way merge |
| 295 Find Median from Data Stream | Hard | Google, Amazon, Meta | Two-heap (min + max heap) |
| 973 K Closest Points to Origin | Medium | Google, Amazon, Meta | Min-heap or max-heap |
| 378 Kth Smallest Element in a Sorted Matrix | Medium | Amazon, Google, Apple | Min-heap of row heads |
| 767 Reorganize String | Medium | Amazon, Google | Max-heap for frequency-based ordering |
| 703 Kth Largest Element in a Stream | Easy | Google, Amazon | Heap of size K |
| 239 Sliding Window Maximum | Hard | Amazon, Google | Deque alternative to heap |
| 313 Super Ugly Number | Medium | Google, Amazon | Min-heap for ugly number generation |

## Real Production Scenarios
- **Uber**: PriorityQueue for ride-fare adjustment processing — urgent requests processed first
- **LinkedIn**: PriorityQueue for news-feed ranking — top K stories computed efficiently

## Interview Patterns & Tips
- **K-th largest uses min-heap of size K**: keep K largest elements
- **K-th smallest uses max-heap of size K**: keep K smallest elements
- **Merge K sorted**: heap of K elements, extract min, insert next from the same source
- **Median tracking**: maintain two heaps (max-heap for left half, min-heap for right half)

## Deep Dive Questions
- **Binary heap**: How is a binary heap represented in an array?
- **siftUp/siftDown**: What is the difference between siftUp and siftDown?
- **Heapify**: How is heapify implemented in O(n) time?
- **Heap sort**: How is PriorityQueue related to heap sort?
- **PriorityBlockingQueue**: How does it achieve thread-safety?
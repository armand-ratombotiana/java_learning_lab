# Interview Preparation: Queue & Deque Advanced

This document covers advanced questions related to Queue implementations, Binary Heaps, and BlockingQueues in concurrent environments.

## Q1: Explain the internal data structure of a `PriorityQueue` and its time complexities.
**Answer:**
A `PriorityQueue` is implemented as a **Min-Binary Heap** (or Max-Heap depending on the Comparator), which is backed by a resizable array.
*   **Structure**: It is a complete binary tree where the value of each parent node is less than or equal to the values of its children. The root (index 0) is always the minimum element.
*   **Time Complexity**:
    *   `peek()` (find min): $O(1)$
    *   `offer()` (insert): $O(\log N)$ - The element is added to the end of the array and "bubbles up" to its correct position.
    *   `poll()` (remove min): $O(\log N)$ - The root is removed, the last element in the array is moved to the root, and it "bubbles down" to maintain the heap property.
    *   `remove(Object)`: $O(N)$ - It must linearly scan the array to find the object before removing and re-heaping.

## Q2: Why should you use `ArrayDeque` instead of `LinkedList` or `Stack`?
**Answer:**
*   **vs. `Stack`**: `java.util.Stack` extends `Vector`, which means every single method is `synchronized`. This adds massive, usually unnecessary, performance overhead in single-threaded environments. `ArrayDeque` is not synchronized.
*   **vs. `LinkedList`**: `LinkedList` requires instantiating a new `Node` object for every element added, leading to memory overhead and cache misses (nodes are scattered across the heap). `ArrayDeque` is backed by a contiguous circular array, making it incredibly cache-friendly and significantly faster for both Queue (FIFO) and Stack (LIFO) operations.

## Q3: What is the difference between `ArrayBlockingQueue` and `LinkedBlockingQueue`?
**Answer:**
Both are thread-safe queues used in Producer-Consumer patterns, but they differ in internal locking mechanics:
*   **`ArrayBlockingQueue`**: Bounded (fixed size) and backed by an array. It uses a **single lock** for both `put()` and `take()` operations. This means a producer and a consumer cannot operate on the queue simultaneously.
*   **`LinkedBlockingQueue`**: Optionally bounded and backed by linked nodes. It uses a **two-lock queue** algorithm (one lock for `put()`, one lock for `take()`). This allows a producer and a consumer to operate on the queue concurrently, often resulting in higher throughput in highly concurrent applications.

## Q4: In a `BlockingQueue`, what is the difference between `add()`, `offer()`, and `put()`?
**Answer:**
These methods handle a full queue differently:
*   `add(e)`: Throws an `IllegalStateException("Queue full")` if the queue is full.
*   `offer(e)`: Returns `false` if the queue is full (does not block, does not throw exception).
*   `put(e)`: **Blocks** (waits) until space becomes available in the queue. This is the method you must use for proper backpressure in a producer-consumer design.

## Q5: What is a `SynchronousQueue`?
**Answer:**
A `SynchronousQueue` is a `BlockingQueue` with a capacity of zero. It does not hold any elements internally.
Every `put()` operation must wait for a corresponding `take()` operation by another thread, and vice versa. It acts as a direct hand-off mechanism between threads. It is heavily used in thread pools (like `Executors.newCachedThreadPool()`) to hand off tasks directly to idle threads without buffering them.
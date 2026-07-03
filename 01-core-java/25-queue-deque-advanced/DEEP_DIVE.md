# Deep Dive: Advanced Queue & Deque

## 1. Introduction to Queues and Deques
While `List` and `Set` are general-purpose collections, `Queue` (FIFO - First In, First Out) and `Deque` (Double Ended Queue) are specifically designed for holding elements prior to processing. They are fundamental to task scheduling, breadth-first search algorithms, and producer-consumer architectures.

## 2. PriorityQueue Internals
A standard queue processes elements in the exact order they were added. A `PriorityQueue` processes elements based on their priority (defined by natural ordering or a `Comparator`).

### The Binary Heap
Internally, a `PriorityQueue` is NOT a sorted list. It is implemented as a **Min-Binary Heap** (an array-based complete binary tree).
*   **The Heap Property**: The value of a parent node is always less than or equal to the values of its children. The smallest element is always at the root (index 0 of the array).
*   **Performance**: 
    *   Finding the minimum element (`peek()`): $O(1)$
    *   Inserting an element (`offer()`): $O(\log N)$ (The element is added at the end and "bubbles up" to its correct position).
    *   Removing the minimum element (`poll()`): $O(\log N)$ (The root is removed, the last element is moved to the root, and it "bubbles down").
*   **Note**: Iterating over a `PriorityQueue` does *not* return elements in sorted order. It returns them in heap order (which appears somewhat random).

## 3. Deque (Double Ended Queue) Implementations
A `Deque` allows insertion and removal at both ends. It can function as both a Queue (FIFO) and a Stack (LIFO).
*   *Note*: The legacy `java.util.Stack` class is synchronized and slow. Modern Java developers should always use a `Deque` (like `ArrayDeque`) when they need a Stack.

### `ArrayDeque` vs `LinkedList`
Both implement `Deque`, but their performance characteristics differ wildly:
*   **`ArrayDeque`**: Backed by a resizable circular array. It is significantly faster than `LinkedList` for most operations because it avoids the memory overhead and cache misses of node allocation. It does not allow `null` elements.
*   **`LinkedList`**: Backed by a doubly-linked list. Useful only if you need to perform many insertions/removals in the *middle* of the collection using an iterator.

## 4. BlockingQueue Variants (Concurrency)
`BlockingQueue` is an interface in `java.util.concurrent` that extends `Queue`. It is the backbone of the Producer-Consumer pattern. If a consumer tries to `take()` from an empty queue, it blocks (waits) until an element is available. If a producer tries to `put()` into a full bounded queue, it blocks until space is available.

*   **`ArrayBlockingQueue`**: Bounded (fixed size), backed by an array. Uses a single lock for both reading and writing. Highly predictable performance.
*   **`LinkedBlockingQueue`**: Optionally bounded, backed by linked nodes. Uses "two-lock" queues (one for putting, one for taking), which allows higher concurrency between producers and consumers compared to `ArrayBlockingQueue`.
*   **`PriorityBlockingQueue`**: An unbounded blocking queue that uses the same ordering rules as class `PriorityQueue`.
*   **`SynchronousQueue`**: A queue with a capacity of zero! Every insert operation must wait for a corresponding remove operation by another thread, and vice versa. It is a direct hand-off mechanism.

## 5. TransferQueue
`TransferQueue` (introduced in Java 7) extends `BlockingQueue`. It adds a critical feature: a producer can wait for a consumer to actually *receive* the element before continuing.
*   `put(e)`: Adds element, blocks if full (standard BlockingQueue behavior).
*   `transfer(e)`: Adds element, and **blocks until a consumer retrieves it**.
*   **`LinkedTransferQueue`**: The standard implementation. It is highly optimized and often outperforms `LinkedBlockingQueue` even for standard queue operations due to its non-blocking algorithm internals (Dual Data Structures).
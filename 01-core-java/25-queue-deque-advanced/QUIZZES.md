# Quizzes: Queue & Deque Advanced

Test your knowledge of PriorityQueues, Deques, and BlockingQueues.

## Quiz 1: PriorityQueue Internals

**Q1: What is the underlying data structure of Java's `PriorityQueue`?**
- A) A Red-Black Tree
- B) A Linked List
- C) A Min-Binary Heap (implemented using an array)
- D) A Hash Table
*Answer: C*

**Q2: What is the time complexity of adding an element (`offer()`) to a `PriorityQueue`?**
- A) $O(1)$
- B) $O(\log N)$
- C) $O(N)$
- D) $O(N \log N)$
*Answer: B (The element is added to the end of the array and "bubbles up" the tree, which takes logarithmic time).*

## Quiz 2: Queue Contracts

**Q1: What is the difference between `queue.poll()` and `queue.remove()`?**
- A) `remove()` takes an argument, `poll()` does not.
- B) `poll()` returns `null` if the queue is empty; `remove()` throws a `NoSuchElementException`.
- C) `poll()` removes the element, `remove()` only looks at it.
- D) There is no difference.
*Answer: B*

**Q2: Why is `ArrayDeque` generally preferred over `LinkedList` when you need a Stack or a Queue?**
- A) `ArrayDeque` allows nulls, `LinkedList` does not.
- B) `ArrayDeque` is thread-safe.
- C) `ArrayDeque` avoids the memory overhead and cache misses associated with allocating Node objects for every element.
- D) `ArrayDeque` is sorted.
*Answer: C*

## Quiz 3: Concurrency and BlockingQueues

**Q1: In a Producer-Consumer scenario, if the consumer thread calls `take()` on an empty `BlockingQueue`, what happens?**
- A) It throws an `IllegalStateException`.
- B) It returns `null`.
- C) It blocks (waits) until a producer puts an element into the queue.
- D) It returns a default value.
*Answer: C*

**Q2: What makes `LinkedTransferQueue`'s `transfer(e)` method unique compared to `put(e)`?**
- A) `transfer(e)` uses UDP instead of TCP.
- B) `transfer(e)` blocks the producer until a consumer actually receives the element, whereas `put(e)` only blocks if the queue is full.
- C) `transfer(e)` is non-blocking.
- D) `transfer(e)` bypasses the queue entirely.
*Answer: B*
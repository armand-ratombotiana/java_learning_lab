# Edge Cases & Pitfalls: Queue & Deque

Queues are heavily used in concurrent and performance-sensitive code. Misunderstanding their contracts can lead to thread starvation, memory leaks, and silent failures.

## 1. The `add()` vs `offer()` Trap
*   **The Scenario**: You are using a bounded queue (e.g., `ArrayBlockingQueue(10)`). The queue is currently full.
*   **The Pitfall**: 
    *   If you call `queue.add(e)`, it will throw an `IllegalStateException("Queue full")`.
    *   If you call `queue.offer(e)`, it will silently return `false`.
*   **Mitigation**: If you are in a producer-consumer scenario and want the thread to wait for space to become available, you must use `queue.put(e)` (which blocks). Using `offer()` without checking the boolean return value leads to silently dropped data.

## 2. Iterating a `PriorityQueue`
*   **The Scenario**: You add elements to a `PriorityQueue` and then use an enhanced `for` loop to print them out, expecting them to be in sorted order.
*   **The Pitfall**: The iterator of a `PriorityQueue` does *not* return elements in any particular order. It simply traverses the underlying array (the binary heap). The array is partially ordered (parents are smaller than children), but it is not fully sorted.
*   **Mitigation**: To retrieve elements in sorted order, you must continuously call `poll()` until the queue is empty.
    ```java
    while (!pq.isEmpty()) {
        System.out.println(pq.poll()); // Prints in sorted order
    }
    ```

## 3. Modifying Elements in a `PriorityQueue`
*   **The Scenario**: You add a `Task` object to a `PriorityQueue`. The queue orders tasks by their `priority` field. Later, you change the `priority` field of a task that is already sitting in the queue.
*   **The Pitfall**: The `PriorityQueue` does not know the object was mutated. The binary heap is now corrupted. Subsequent calls to `poll()` may return elements in the wrong order.
*   **Mitigation**: If you must change the priority of an element, you must `remove(task)` from the queue, mutate it, and then `offer(task)` it back into the queue so the heap can re-balance.

## 4. `NullPointerException` in `ArrayDeque`
*   **The Scenario**: You are migrating code from `LinkedList` to `ArrayDeque` for performance reasons. The old code occasionally added `null` values to the queue.
*   **The Pitfall**: `LinkedList` permits `null` elements. `ArrayDeque` strictly forbids `null` elements. Attempting to `add(null)` will immediately throw a `NullPointerException`. The designers of `ArrayDeque` made this choice so that methods like `poll()` returning `null` unambiguously mean "the queue is empty."
*   **Mitigation**: Ensure your queues do not rely on `null` as a valid element. Use `Optional` or a dedicated "Empty/Poison Pill" object if needed.

## 5. The Unbounded Queue Memory Leak
*   **The Scenario**: You use a `LinkedBlockingQueue` (without specifying a capacity) or a `PriorityBlockingQueue` to buffer tasks between a fast producer and a slow consumer.
*   **The Pitfall**: An unbounded queue will grow indefinitely. If the producer consistently outpaces the consumer, the queue will eventually consume all available heap memory, causing an `OutOfMemoryError` and crashing the application.
*   **Mitigation**: Always use **bounded queues** in production systems. Applying backpressure (forcing the producer to block when the queue is full) is essential for system stability.
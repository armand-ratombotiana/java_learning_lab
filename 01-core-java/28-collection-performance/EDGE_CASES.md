# Edge Cases & Pitfalls: Collection Performance Tuning

Tuning collections requires a deep understanding of JVM mechanics. Blindly applying "optimizations" can sometimes make performance worse.

## 1. The "HashMap(100)" Resizing Trap
*   **The Scenario**: You know you will receive exactly 100 records from a database. You initialize your map: `Map<String, User> map = new HashMap<>(100);`.
*   **The Pitfall**: As discussed in the Deep Dive, `HashMap` resizes based on its threshold (`Capacity * LoadFactor`). The threshold here is `100 * 0.75 = 75`. When you insert the 76th record, the `HashMap` will undergo an expensive resizing and rehashing operation, completely defeating the purpose of pre-allocating the capacity.
*   **Mitigation**: Calculate the correct capacity: `Math.ceil(expectedSize / 0.75)`. For 100 items, use `134`. Or, in Java 19+, use `HashMap.newHashMap(100)`.

## 2. The `LinkedList` Iteration Trap
*   **The Scenario**: You choose `LinkedList` because you need to frequently add elements to the middle of the list. You then process the list using a standard `for` loop: 
    ```java
    for (int i = 0; i < list.size(); i++) { process(list.get(i)); }
    ```
*   **The Pitfall**: `LinkedList.get(i)` is an $O(N)$ operation because it must traverse the list from the head to index `i`. Putting an $O(N)$ operation inside an $O(N)$ loop results in $O(N^2)$ time complexity. Processing a list of 100,000 items this way will bring the application to a crawl.
*   **Mitigation**: Always use an `Iterator` or an enhanced `for-each` loop when traversing a `LinkedList`. This maintains a pointer to the current node, resulting in $O(N)$ total traversal time.

## 3. Bad `hashCode()` Collisions
*   **The Scenario**: You create a custom object to use as a key in a `HashMap`. You implement `hashCode()` poorly (e.g., returning a constant `1`, or returning the string length).
*   **The Pitfall**: If many keys return the same hash code, they all end up in the exact same bucket in the `HashMap`. The bucket degrades into a Linked List (or a Tree in Java 8+), and the map loses its $O(1)$ lookup performance, degrading to $O(\log N)$ or even $O(N)$.
*   **Mitigation**: Always use high-quality hash functions (like `Objects.hash(...)`) that distribute values evenly across the integer space.

## 4. Retaining Shrunk Collections (Memory Leak)
*   **The Scenario**: You have an `ArrayList` that temporarily spikes to 1,000,000 elements during a batch process, but usually only holds 10 elements. You call `list.clear()` after the batch process.
*   **The Pitfall**: `ArrayList.clear()` nulls out the references in the array so the objects can be garbage collected, but **it does not shrink the underlying array itself**. The `ArrayList` will continue to hold a massive `Object[]` array in memory forever.
*   **Mitigation**: If a collection experiences massive, temporary spikes, you should either let the collection object itself be garbage collected and create a new one, or call `list.trimToSize()` to force the array to shrink down to its current element count.

## 5. Over-Optimizing Prematurely
*   **The Pitfall**: Spending hours writing custom primitive collections or complex capacity math for lists that will never hold more than 50 items. The memory savings are measured in bytes, and the CPU savings are unmeasurable, but the code complexity increases significantly.
*   **Mitigation**: Follow the rule of "Measure First." Only optimize collections that are proven to be bottlenecks via profiling tools (like VisualVM, JFR, or YourKit).
# Interview Questions: Lists (Deep Dive)

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 206 Reverse Linked List](https://leetcode.com/problems/reverse-linked-list/) | Easy | Microsoft, Amazon, Apple | Iterative/recursive reversal |
| [LC 21 Merge Two Sorted Lists](https://leetcode.com/problems/merge-two-sorted-lists/) | Easy | Google, Microsoft, Amazon | Two-pointer merge |
| [LC 141 Linked List Cycle](https://leetcode.com/problems/linked-list-cycle/) | Easy | Amazon, Microsoft, Google | Slow/fast pointer |
| [LC 142 Linked List Cycle II](https://leetcode.com/problems/linked-list-cycle-ii/) | Medium | Google, Amazon | Cycle detection + math |
| [LC 19 Remove Nth Node From End of List](https://leetcode.com/problems/remove-nth-node-from-end-of-list/) | Medium | Meta, Microsoft, Amazon | Two-pointer offset |
| [LC 143 Reorder List](https://leetcode.com/problems/reorder-list/) | Medium | Amazon, Google | Midpoint + reverse + merge |
| [LC 23 Merge k Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/) | Hard | Amazon, Meta, Microsoft | Min-heap / divide-and-conquer |
| [LC 25 Reverse Nodes in k-Group](https://leetcode.com/problems/reverse-nodes-in-k-group/) | Hard | Microsoft, Amazon, Google | Recursive reversal |
| [LC 138 Copy List with Random Pointer](https://leetcode.com/problems/copy-list-with-random-pointer/) | Medium | Amazon, Microsoft, Google | HashMap / interleaving |

## NeetCode Reference
NeetCode 150: Linked List category — all 11 problems. NeetCode 250 adds List Deep Copy and Merge In Between.

## Company-Specific Questions

### Google
- Design a skip list-based list implementation (performance vs TreeMap)
- Implement a persistent (immutable) linked list with structural sharing
- ArrayList vs LinkedList trade-offs for specific workloads
- How would you implement subList() efficiently?

### Microsoft
- Reverse linked list in-place iteratively and recursively
- Add two numbers represented as linked lists (forward and reverse order)
- Sort a linked list in O(n log n) time, O(1) space (merge sort on linked list)

### Meta
- Flatten a multilevel doubly linked list
- Clone a linked list with random pointers
- Odd-even linked list (group odd indices before even)

### Amazon
- LRU Cache design (HashMap + doubly linked list)
- Merge k sorted lists (priority queue approach)
- Detect cycle and find cycle start

### Apple
- Implement a thread-safe linked list
- Design a list-backed deque supporting O(1) head/tail operations
- LinkedList vs ArrayDeque for different workloads

### Oracle
- ArrayList vs Vector in legacy Java — what changed with Collections Framework
- CopyOnWriteArrayList — when would you use it in enterprise applications?
- How does ArrayList.sublist() work internally and what are the pitfalls?

## Real Production Scenarios

- **Scenario 1: Cache Eviction (LRU)** — A caching layer needs O(1) get and put with eviction of least-recently used entries. Use HashMap + doubly linked list. The list tracks access order; the map provides O(1) lookup to list nodes.

- **Scenario 2: Message Queue Backpressure** — A producer-consumer system using a linked list-based blocking queue. When the queue exceeds a threshold, backpressure is applied. The linked list enables O(1) enqueue/dequeue at both ends.

- **Scenario 3: Memory Pool with ArrayList** — A real-time trading system pre-allocates an ArrayList with exact capacity to avoid resizing pauses. Array-backed structures avoid GC pressure from node objects.

## Interview Tips

- Time: O(n) for traversal-based operations, O(1) for head/tail insert/delete, O(n) for index access in linked lists vs O(1) in arrays.
- Space: O(1) extra for in-place operations; O(n) for recursive stack (recursion depth may cause stack overflow).
- Common edge cases: empty list, single node, two nodes, cycle, k > length, null pointers.
- Always draw out pointer changes before coding linked list manipulation.

## Java-Specific Considerations

- `ArrayList` vs `LinkedList` — `ArrayList` is almost always faster except for frequent head insertions
- `Arrays.asList()` returns a fixed-size list backed by the array — modifications to the array reflect in the list
- `Vector` is synchronized but obsolete; `CopyOnWriteArrayList` for concurrent reads with infrequent writes
- `LinkedList` implements both `List` and `Deque` — can be used as stack, queue, or deque
- `subList()` returns a view, not a copy — structural modifications to the parent list invalidate the sublist
- `ArrayList` grows by 50% (Java 8+) — tuning initial capacity avoids repeated resizing

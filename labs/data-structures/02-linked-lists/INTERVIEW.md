# Interview Questions: Linked Lists

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 206 Reverse Linked List](https://leetcode.com/problems/reverse-linked-list/) | Easy | Microsoft, Amazon, Apple, Google | Iterative/recursive reversal |
| [LC 21 Merge Two Sorted Lists](https://leetcode.com/problems/merge-two-sorted-lists/) | Easy | Google, Amazon, Microsoft, Meta | Two-pointer merge |
| [LC 141 Linked List Cycle](https://leetcode.com/problems/linked-list-cycle/) | Easy | Amazon, Microsoft, Google, Apple | Slow/fast pointer |
| [LC 142 Linked List Cycle II](https://leetcode.com/problems/linked-list-cycle-ii/) | Medium | Google, Amazon, Microsoft | Floyd's cycle + math |
| [LC 19 Remove Nth Node From End of List](https://leetcode.com/problems/remove-nth-node-from-end-of-list/) | Medium | Meta, Microsoft, Amazon, Google | Two-pointer offset |
| [LC 143 Reorder List](https://leetcode.com/problems/reorder-list/) | Medium | Amazon, Google, Meta | Midpoint + reverse + merge |
| [LC 23 Merge k Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/) | Hard | Amazon, Meta, Microsoft, Google | Min-heap / divide & conquer |
| [LC 25 Reverse Nodes in k-Group](https://leetcode.com/problems/reverse-nodes-in-k-group/) | Hard | Microsoft, Amazon, Google, Meta | Recursive reversal |
| [LC 138 Copy List with Random Pointer](https://leetcode.com/problems/copy-list-with-random-pointer/) | Medium | Amazon, Microsoft, Google, Meta | HashMap / interleaving |
| [LC 2 Add Two Numbers](https://leetcode.com/problems/add-two-numbers/) | Medium | Amazon, Meta, Google, Microsoft | Carry propagation |
| [LC 234 Palindrome Linked List](https://leetcode.com/problems/palindrome-linked-list/) | Easy | Amazon, Meta, Google, Microsoft | Midpoint + reverse half |
| [LC 24 Swap Nodes in Pairs](https://leetcode.com/problems/swap-nodes-in-pairs/) | Medium | Microsoft, Amazon, Google, Meta | Recursive swap |
| [LC 328 Odd Even Linked List](https://leetcode.com/problems/odd-even-linked-list/) | Medium | Meta, Amazon, Microsoft | In-place regrouping |
| [LC 147 Insertion Sort List](https://leetcode.com/problems/insertion-sort-list/) | Medium | Microsoft, Google | Linked list insertion sort |
| [LC 148 Sort List](https://leetcode.com/problems/sort-list/) | Medium | Amazon, Meta, Microsoft, Google | Merge sort on linked list |

## NeetCode Reference
NeetCode 150: Linked List category — 11 problems. Essential: Reverse Linked List, Merge Two Sorted Lists, Reorder List, Remove Nth Node, Copy List with Random Pointer.

## Company-Specific Questions

### Google
- Design a skip list (probabilistic linked list with O(log n) search)
- Reverse a doubly linked list in-place
- Implement a persistent linked list with structural sharing
- How would you implement the `LinkedList` class in Java?

### Microsoft
- Reverse linked list iteratively and recursively — which is better and why?
- Sort a linked list in O(n log n) time using O(1) extra space (merge sort on list)
- Add two numbers represented as linked lists in reverse and forward order

### Meta
- Flatten a multilevel doubly linked list (tree → list conversion)
- Clone a linked list with random pointers in O(n) time using O(1) extra space
- Odd-even linked list — maintain constant space

### Amazon
- LRU Cache design — HashMap + doubly linked list, LinkedHashMap implementation
- Merge k sorted lists using divide-and-conquer and using min-heap
- Detect cycle and return node where cycle begins (Floyd's algorithm proof)

### Apple
- Implement a thread-safe linked list (lock-free singly linked list with CAS)
- Design a play queue for music app using linked list (shuffle, repeat, next/prev)
- LinkedList vs ArrayDeque for performance-critical UI rendering

### Oracle
- `LinkedList` implements both `List` and `Deque` — what is the performance impact of this dual nature?
- `Collections.synchronizedList(new LinkedList<>())` — what are the threading limitations?
- How does `LinkedList` handle concurrent modification (fail-fast iterator)?

## Real Production Scenarios

- **Scenario 1: Cache Eviction (LRU)** — A content delivery network cache uses a HashMap + doubly linked list. The linked list tracks access order: when a key is accessed, the corresponding node is moved to the head. When the cache is full, the tail node is evicted. Both operations are O(1).

- **Scenario 2: Memory Management** — A garbage collector uses a linked list of free memory blocks (free list). When an allocation request comes in, the GC searches the free list for a suitable block using a strategy like first-fit or best-fit.

- **Scenario 3: Undo Buffer** — A text editor maintains an undo stack backed by a doubly linked list. Each node stores a state or delta. The user can undo/redo by moving forward/backward through the list. The list supports O(1) insertion at current position.

## Interview Tips

- Time: O(n) traversal, O(1) head/tail insertion, O(n) index access (avoid if possible)
- Space: O(1) for in-place operations; O(n) for recursive stack may overflow for long lists
- Common edge cases: empty list, single node, two nodes, cycle, k > length, null pointers
- Always draw the pointer changes before coding linked list manipulation
- Use dummy/sentinel node to simplify head removal and insertion edge cases

## Java-Specific Considerations

- `LinkedList<E>` implements both `List<E>` and `Deque<E>` — node-based doubly linked list
- `LinkedList` almost always underperforms `ArrayList` except for repeated head insertions
- `Deque` with `ArrayDeque` is preferred over `LinkedList` for stack/queue use cases
- `ConcurrentLinkedDeque` — lock-free, non-blocking, CAS-based thread-safe deque
- `Collections.synchronizedList()` wraps any list for thread safety but requires external locking for iteration
- `ListIterator` supports backward traversal, which is efficient for `LinkedList` but not `ArrayList`
- Node object overhead: each `LinkedList` node stores 3 references (item, next, prev) + object header

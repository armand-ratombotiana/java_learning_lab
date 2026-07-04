# Interview Questions: Linked Lists

## Easy

1. **Reverse a linked list** — Iterative and recursive solutions (both O(n) time, O(1) space).

2. **Middle of linked list** — Slow/fast pointer approach.

3. **Delete node** — Delete a given node when you only have access to that node (not head).

4. **Remove duplicates** — Remove duplicates from an unsorted linked list (use HashSet or two pointers).

## Medium

5. **Linked list cycle** — Detect cycle and return the node where cycle begins.

6. **Intersection of two linked lists** — Find where two singly linked lists merge.

7. **Remove nth from end** — One-pass solution with two pointers (dummy head for edge cases).

8. **Add two numbers** — Two linked lists representing numbers in reverse, sum them.

9. **Palindrome linked list** — O(n) time, O(1) space (reverse second half).

10. **Odd even linked list** — Group odd-indexed nodes before even-indexed.

## Hard

11. **Merge k sorted lists** — Use a min-heap (PriorityQueue) for O(n log k).

12. **Reverse nodes in k-group** — Reverse every k nodes of the list.

13. **LRU Cache** — Combination of doubly linked list and HashMap.

14. **Flatten multilevel doubly linked list** — DFS or iterative stack approach.

## Key Patterns

- **Slow/fast pointer**: cycle detection, middle node, nth from end
- **Dummy head**: simplifies removal and reversal edge cases
- **Pointer manipulation**: draw diagrams for all pointer changes
- **Temporary nodes**: always save next before redirecting pointers
- **Two-pointer merge**: merging sorted lists, adding numbers

## Java-Specific Topics

- Difference between `ArrayList` and `LinkedList` (random access vs insertion)
- When `LinkedList` is the right choice (frequent add/remove at ends)
- `Deque` interface and its implementations
- Fail-fast vs fail-safe iterators

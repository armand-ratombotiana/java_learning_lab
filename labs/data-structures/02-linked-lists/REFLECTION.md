# Reflection: Linked Lists

## What I Learned

- Linked lists provide O(1) insertion/deletion at known positions, unlike arrays
- Node-based structure means non-contiguous memory allocation and pointer overhead
- Doubly linked lists enable bidirectional traversal and O(1) deletion at known nodes
- Sentinel nodes eliminate special-case null checks
- Floyd's algorithm detects cycles in O(n) time, O(1) space
- Java's LinkedList is highly optimized but memory-heavy compared to ArrayList

## Questions to Consider

1. When is a linked list better than an ArrayList despite worse cache performance?
2. How would you implement a thread-safe linked list without using locks everywhere?
3. What is the trade-off between singly and doubly linked lists (memory vs capability)?
4. How does the HashMap collision chain switching to tree (Java 8) illustrate linked list limitations?
5. Could you implement an indexed linked list (like a skip list) for O(log n) access?

## Connections

Linked lists connect to:
- **Graphs** (adjacency lists use linked lists per vertex)
- **Hash tables** (chaining uses linked lists for collisions)
- **Stacks/Queues** (LinkedList implements Deque for both)
- **Memory management** (free lists, buddy allocator)
- **File systems** (FAT allocation table)
- **Functional programming** (immutable linked lists are the core of Lisp)

## Self-Assessment

- [ ] Can implement a singly linked list from scratch
- [ ] Can implement a doubly linked list from scratch
- [ ] Can reverse a linked list (iterative and recursive)
- [ ] Can detect and resolve cycles
- [ ] Understand when to use LinkedList vs ArrayList
- [ ] Understand memory overhead and cache behavior

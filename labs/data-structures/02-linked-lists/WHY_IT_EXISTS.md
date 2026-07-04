# Why Linked Lists Exist

Linked lists solve the problem of **efficient insertion and deletion at arbitrary positions** without shifting data.

## Problems with Arrays

- Inserting at the beginning costs O(n) — all elements must shift
- Deleting from the middle costs O(n) — all subsequent elements shift
- Fixed capacity (static) or occasional expensive resize (dynamic)
- Contiguous memory may be unavailable for large blocks

## What Linked Lists Provide

| Need | Linked List Solution |
|------|---------------------|
| O(1) insertion at head | Create node, point to old head |
| O(1) deletion at head | Move head pointer |
| No resize cost | Node-by-node allocation |
| Non-contiguous | Nodes live anywhere in heap |
| Arbitrary size | Limited only by total heap |
| Simplified concatenation | Point tail of one list to head of another |

## Java-Specific Context

- `java.util.LinkedList` implements both `List` and `Deque` — it's a doubly linked list serving as both a list and a queue/stack
- Used when frequent insertion/removal at both ends is needed
- Used when you need a guaranteed O(1) `addFirst`/`removeFirst`
- Foundations for: undo/redo, LRU caches, adjacency lists in graphs

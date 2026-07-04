# Why Heaps Exist

Heaps exist to efficiently support **priority-based operations** — specifically, fast insertion and fast extraction of the highest-priority element.

## Problems with Other Structures

- **Arrays**: inserting and removing min requires O(n) search or O(n log n) sorting
- **Linked lists**: sorted insertion is O(n), finding min is O(1) but inserting out of order is O(n)
- **BSTs**: O(log n) for insert and delete-min, but overhead of pointers and balance maintenance
- **Hash tables**: no ordering at all

## What Heaps Provide

| Need | Heap Solution |
|------|--------------|
| Fast insert | O(log n) — append + siftUp |
| Fast min/max | O(1) peek, O(log n) extract |
| In-place sorting | Heap sort: O(n log n), no extra memory |
| Dynamic median | Two heaps (min + max) for O(log n) median |
| Task scheduling | Priority queue for highest-priority task |
| Dijkstra's algorithm | PQ of (distance, vertex) for shortest paths |
| Stream processing | K largest/smallest elements in a stream |

## Key Insight

Heaps provide a **partial ordering** — they don't maintain full sorted order (like BST), but guarantee that the highest-priority element is always at the top. This relaxed constraint allows simpler, faster operations.

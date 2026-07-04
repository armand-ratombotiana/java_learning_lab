# Theory: Heaps

## Heap Definition

A **binary heap** is a complete binary tree that satisfies the **heap property**:
- **Min-heap**: parent ≤ both children (root is minimum)
- **Max-heap**: parent ≥ both children (root is maximum)

## Complete Binary Tree

All levels are completely filled except possibly the last, which is filled from left to right. This property enables efficient array representation.

## Array Representation

For a node at index i (0-based):
- Left child: `2i + 1`
- Right child: `2i + 2`
- Parent: `(i - 1) / 2`

```
Array: [50, 30, 20, 15, 10, 8, 5]
Tree:
        50
       /  \
      30   20
     / \   / \
    15 10 8   5
```

## Core Operations

### Insert (push)
1. Add element at the end (maintains completeness)
2. **siftUp**: bubble up until heap property restored

### Extract Min/Max (pop)
1. Swap root with last element
2. Remove last element
3. **siftDown**: bubble down until heap property restored

### Build Heap (heapify)
Bottom-up approach: siftDown from last non-leaf node to root. O(n) time.

### Peek
Return root without removing: O(1)

## Time Complexity

| Operation | Time |
|-----------|------|
| Insert | O(log n) |
| Extract min/max | O(log n) |
| Peek | O(1) |
| Heapify | O(n) |
| Delete arbitrary | O(n) search + O(log n) sift |

## Heap Sort

1. Build heap: O(n)
2. Repeatedly extract root: n × O(log n) = O(n log n)
3. Total: O(n log n), in-place

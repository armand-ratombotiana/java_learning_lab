# Performance: Trees

## Time Complexity

| Operation | Binary Tree | BST (balanced) | BST (degenerate) |
|-----------|------------|----------------|-------------------|
| Search    | O(n)       | O(log n)       | O(n)              |
| Insert    | O(1)*      | O(log n)       | O(n)              |
| Delete    | O(1)*      | O(log n)       | O(n)              |
| Height    | O(n)       | O(log n)       | O(n)              |
| Traversal | O(n)       | O(n)           | O(n)              |

\*Insert at arbitrary position; search is still O(n)

## Space Complexity

### Node Overhead

| Component | Size per Node |
|-----------|--------------|
| Object header | 12 bytes |
| `val` reference | 4 bytes (compressed) |
| `left` reference | 4 bytes |
| `right` reference | 4 bytes |
| **Total** | **~24–28 bytes** |

For n nodes:
- Binary tree: ~28n bytes
- BST: ~28n bytes
- B-tree: node overhead amortized across many keys

### Recursive Traversal Stack

| Tree Height | Recursive Stack | Iterative Stack |
|-------------|----------------|-----------------|
| h (balanced, n=1M) | ~20 frames | ~20 elements |
| h (degenerate, n=1M) | 1M frames → **overflow** | 1M elements → OOM |

## Cache Behavior

- **Node-based trees** have scattered memory allocation → poor cache locality
- **Array-heap representation** (complete binary tree) → excellent cache locality
- **B-trees** are cache-friendly because each node contains many keys in a contiguous array

## Recursion vs Iteration

| Aspect | Recursive | Iterative |
|--------|-----------|-----------|
| Code clarity | High | Medium |
| Stack usage | O(h) on call stack | O(h) on heap |
| Risk | StackOverflow for deep trees | Safe |
| Speed | Slightly slower (function calls) | Slightly faster |

## Java-Specific Optimizations

- JVM inlines small recursive methods (up to a depth limit)
- `TreeMap` operations (Red-Black) are O(log n) with loop-based implementations
- HashMap treeification only triggers at bucket size ≥ 8 — most buckets never treeify

# Theory: Advanced Trees

## Binary Search Tree (BST)

A BST is a binary tree where for every node: left < root < right.

```
    8
   / \
  3   10
 / \    \
1   6    14
```

- Average: O(log n) for insert/search/delete
- Worst (degenerate): O(n) when input is sorted

## AVL Tree

A self-balancing BST where the **balance factor** (height(left) - height(right)) ∈ {-1, 0, 1} for every node.

- **Balance factor**: `bf = height(left) - height(right)`
- **Rotations**: left, right, left-right, right-left
- **Height**: ≤ 1.44 × log₂(n)
- **Operations**: O(log n) guaranteed

## Red-Black Tree

A self-balancing BST with **5 invariants**:

1. Every node is either red or black
2. Root is always black
3. Leaves (null) are black
4. Red nodes cannot have red children (no consecutive reds)
5. Every path from root to leaf has the same number of black nodes

- **Height**: ≤ 2 × log₂(n+1)
- **Operations**: O(log n) guaranteed
- Used in: `java.util.TreeMap`, `java.util.TreeSet`

## B-Tree

A multi-way search tree optimized for disk storage. Each node can have up to m keys and m+1 children.

- **Order m**: max children per node = m
- **Property**: all leaves at same depth
- **Branching factor**: hundreds or thousands (matches disk block size)
- **Operations**: O(log n) with very shallow depth

## Fenwick Tree (Binary Indexed Tree)

An array-based tree for prefix sum queries and point updates in O(log n).

- **Query**: prefix sum up to index i
- **Update**: add value at index i
- **Build**: O(n log n) or O(n)

## Segment Tree

A binary tree for range queries and range updates.

- **Query**: sum/min/max over range [l, r] in O(log n)
- **Update**: modify element or range in O(log n)
- **Memory**: O(4n) for array-based implementation

## Comparison

| Tree | Height | Insert | Search | Delete | Use Case |
|------|--------|--------|--------|--------|----------|
| BST | O(n)* | O(log n)* | O(log n)* | O(log n)* | Simple search |
| AVL | 1.44 log n | O(log n) | O(log n) | O(log n) | Read-heavy |
| Red-Black | 2 log n | O(log n) | O(log n) | O(log n) | Write-heavy |
| B-Tree | log_m n | O(log n) | O(log n) | O(log n) | Disk storage |
| Fenwick | log n | O(log n) | O(1)** | O(log n) | Prefix sums |
| Segment | log n | O(log n) | O(log n) | O(log n) | Range queries |

\*Average; worst O(n) for degenerate
\*\*Point query only

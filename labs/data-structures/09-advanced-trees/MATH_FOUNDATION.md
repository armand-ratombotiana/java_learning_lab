# Math Foundation: Advanced Trees

## BST Height Analysis

For a BST with n nodes:
- **Average height** (random insert order): ~4.3 × ln(n)
- **Expected height**: O(log n)
- **Worst height**: n - 1 (sorted insert)
- **Optimal height**: ⌈log₂(n+1)⌉ - 1

## AVL Tree

### Balance Factor

Balance factor = height(left) - height(right) ∈ {-1, 0, 1}

### Minimum Nodes for Given Height

```
N(h) = N(h-1) + N(h-2) + 1
N(0) = 1, N(1) = 2
```

Closed form: N(h) ≈ φ^{h+2}/√5 - 1 where φ = (1+√5)/2

### Maximum Height

```
h < 1.44 × log₂(n+2) - 1.33
```

## Red-Black Tree

### Height Bound

```
h ≤ 2 × log₂(n + 1)
```

### Black Height

Let bh(x) = number of black nodes on path from x to leaf:
- bh(root) ≥ ⌈h/2⌉ (at least half the nodes on any path are black)
- n ≥ 2^{bh(root)} - 1 (minimum nodes for a given black height)

### Insert/Delete Rotations

- Insert: at most 2 rotations (O(1))
- Delete: at most 3 rotations (O(1))
- Recoloring: O(log n) worst case, but amortized O(1)

## B-Tree

For a B-tree of order m (max children = m):
- **Max keys per node**: m - 1
- **Min keys per node** (non-root): ⌈m/2⌉ - 1
- **Height**: 1 + log_m((n+1)/2) ≤ h ≤ 1 + log_{⌈m/2⌉}((n+1)/2)
- **Node splits per insert**: O(log_m n)

### Disk Optimization

Typical B-tree order: m = 200-2000
For m = 500, a tree indexing 1 billion keys has height ≈ 4.
(1 + log_{250}(500M) ≈ 4.6)

## Fenwick Tree

### Binary Indexing

For index i (1-based):
- bit[i] stores sum of range (i - LSB(i), i]
- LSB(i) = i & -i (least significant set bit)
- Update: i += LSB(i) to propagate changes
- Query: i -= LSB(i) to accumulate prefix

### Time Complexity

- Build: O(n) — initialize then add each element
- Point update: O(log n)
- Prefix query: O(log n)
- Range query: O(log n)

## Segment Tree

### Array Size

For n elements, segment tree array size:
- Next power of 2: 2^{⌈log₂n⌉}
- Tree array size: 2 × 2^{⌈log₂n⌉} (= 4n in worst case)

### Time Complexity

- Build: O(n)
- Point update: O(log n)
- Range update (lazy): O(log n)
- Range query: O(log n)

### Lazy Propagation

Lazy array of same size as tree:
- Stores pending updates that haven't been pushed to children
- Push before traversing children
- Each update: O(log n) — touches at most 4 × log₂n nodes

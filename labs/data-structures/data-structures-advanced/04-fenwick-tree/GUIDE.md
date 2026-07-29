# Guide: Fenwick Tree (Binary Indexed Tree)

## Overview

A **Fenwick Tree** (also called **Binary Indexed Tree** or **BIT**) is a data structure that provides efficient methods for calculating prefix sums and updating point values in an array. It was invented by Peter Fenwick in 1994 as an alternative to segment trees for cumulative frequency tables.

The core idea is that each index stores the sum of a range of elements, where the range length corresponds to the least significant bit (LSB) of the index.

### Why Not Use a Prefix Sum Array?

| Aspect | Prefix Sum Array | Fenwick Tree |
|--------|-----------------|--------------|
| Prefix sum | O(1) | O(log n) |
| Point update | O(n) | O(log n) |
| Space | O(n) | O(n) |
| Implementation | Trivial | Moderate |

**Key Insight**: When you need both point updates AND prefix sum queries (read-write mix), BIT wins. For read-only workloads, prefix sum array is better.

---

## ASCII Diagram

```
Index:  1   2   3   4   5   6   7   8
        +---+---+---+---+---+---+---+---+
Array:  | 3 | 2 |-1 | 6 | 5 | 4 | 2 | 1 |
        +---+---+---+---+---+---+---+---+

BIT Representation:
Tree[1] = A[1]          = 3          (range [1,1])
Tree[2] = A[1] + A[2]   = 5          (range [1,2])
Tree[3] = A[3]          = -1         (range [3,3])
Tree[4] = A[1..4]       = 10         (range [1,4])
Tree[5] = A[5]          = 5          (range [5,5])
Tree[6] = A[5] + A[6]   = 9          (range [5,6])
Tree[7] = A[7]          = 2          (range [7,7])
Tree[8] = A[1..8]       = 22         (range [1,8])

Range covered by each index:
Tree[i] covers [i - LSB(i) + 1 .. i]
```

### The LSB (Lowest Set Bit) Trick

```
i           Binary          LSB (i & -i)
1           0001            1
2           0010            2
3           0011            1
4           0100            4
5           0101            1
6           0110            2
7           0111            1
8           1000            8
```

- **Update**: `i += i & -i` → add to parent indexes
- **Prefix query**: `i -= i & -i` → accumulate from children

---

## Source Code Walkthrough

The implementation is in `src/FenwickTree.java`.

### Structure (lines ~3-8)

```java
class FenwickTree {
    private final int[] tree;
    private final int n;

    public FenwickTree(int n) {
        this.n = n;
        this.tree = new int[n + 1]; // 1-indexed internally
    }
}
```

**Note**: 1-indexed! Index 0 is unused. All internal operations use 1..n.

### Point Update (lines ~10-16)

```java
public void update(int idx, int delta) {
    while (idx <= n) {
        tree[idx] += delta;
        idx += idx & -idx;   // move to parent
    }
}
```

**Walkthrough with `update(3, 5)`:**

```
idx = 3, delta = 5
Step 1: tree[3] += 5, idx = 3 + (3 & -3) = 3 + 1 = 4
Step 2: tree[4] += 5, idx = 4 + (4 & -4) = 4 + 4 = 8
Step 3: tree[8] += 5, idx = 8 + (8 & -8) = 8 + 8 = 16 > n, stop
```

Updated positions: 3, 4, 8. These are the positions that cover index 3 in their range.

### Prefix Sum Query (lines ~18-24)

```java
public int prefixSum(int idx) {
    int sum = 0;
    while (idx > 0) {
        sum += tree[idx];
        idx -= idx & -idx;   // move to child
    }
    return sum;
}
```

**Walkthrough with `prefixSum(7)`:**

```
idx = 7
Step 1: sum += tree[7], idx = 7 - (7 & -7) = 7 - 1 = 6
Step 2: sum += tree[6], idx = 6 - (6 & -6) = 6 - 2 = 4
Step 3: sum += tree[4], idx = 4 - (4 & -4) = 4 - 4 = 0, stop
```

Positions visited: 7, 6, 4. These cover ranges [7,7], [5,6], [1,4] — together covering [1,7].

### Range Sum (lines ~26-29)

```java
public int rangeSum(int l, int r) {
    return prefixSum(r) - prefixSum(l - 1);
}
```

### Range Update, Point Query (lines ~31-45)

```java
public void rangeUpdate(int l, int r, int delta) {
    // Add delta to [l, r]
    // Diff array technique: diff[l] += delta, diff[r+1] -= delta
    // BIT over diff array
    update(l, delta);
    update(r + 1, -delta);
}

public int pointQuery(int idx) {
    return prefixSum(idx);
}
```

This uses a BIT over the **difference array** `diff[i] = A[i] - A[i-1]`. Adding delta to A[l..r] becomes: `diff[l] += delta, diff[r+1] -= delta`. Point query = prefix sum of diff.

---

## Complexity Table

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Build | O(n) | O(n) | Initialize from array |
| Point update | O(log n) | O(1) | Add delta to position |
| Prefix sum | O(log n) | O(1) | Sum from 1 to idx |
| Range sum | O(log n) | O(1) | prefixSum(r) - prefixSum(l-1) |
| Range update + point query | O(log n) | O(1) | Diff array technique |
| Range update + range query | O(log n) | O(2n) | Two BITs (explained below) |

### Why O(log n)?

Each operation visits O(log n) nodes. With n up to 10⁶, log₂ n ≈ 20 — very fast.

---

## Comparison with Alternatives

| Feature | Fenwick Tree | Segment Tree | Prefix Array | Balanced BST |
|---------|-------------|-------------|-------------|-------------|
| Build | O(n) | O(n) | O(n) | O(n log n) |
| Point update | O(log n) | O(log n) | O(n) | O(log n) |
| Range sum | O(log n) | O(log n) | O(1) | O(k) (k = range size) |
| Range min | No | O(log n) | O(k) | O(log n) |
| Range update | O(log n) (diff) | O(log n) (lazy) | O(n) | O(log n) |
| Memory | O(n) | O(4n) | O(n) | O(n) |
| Implementation | Simple | Moderate | Trivial | Complex |

**Key difference with Segment Tree**: BIT can only do **sum/xor** operations (any invertible associative operation). Segment Tree can do **min/max/GCD** (any associative operation).

**When NOT to use BIT:**
- Need range minimum/maximum query (use Segment Tree)
- Need non-invertible operations (use Segment Tree)
- Need lazy propagation for range updates AND range queries of non-sum aggregates
- n < 100: naive array is fine

---

## Use Cases

### 1. Inversion Count
**Problem**: Count pairs (i, j) where i < j and A[i] > A[j]
**BIT approach**: Iterate right to left. For each element, query prefix sum to count smaller elements seen, then update BIT at value index by 1.

### 2. Stock Order Book
**System**: HFT exchange tracking cumulative volume at each price level
**BIT per price level**: Price index → volume. Prefix sum of [500, 1000] gives volume between prices 500 and 1000.

### 3. Online Median Tracker
**System**: Real-time data stream
**BIT approach**: Maintain frequency BIT over value range. Binary search on BIT (find smallest idx where prefixSum(idx) >= total/2) gives median.

### 4. Arithmetic Compression
**System**: Arithmetic encoding/decoding
**Why BIT**: Cumulative frequency table update for each symbol. BIT allows O(log A) update per symbol (A = alphabet size).

### 5. Range Sum in 2D Grid
**System**: Image processing, geospatial analytics
**2D BIT**: Nested BITs. Update: O(log² n). Query: O(log² n). Memory: O(n²).

### 6. Counting Smaller After Self (LeetCode 315)
**Problem**: For each element, count elements to its right that are smaller.
**BIT approach**: Coordinate compress, traverse from right, query BIT for count of smaller values.

---

## Common Pitfalls

### 1. 0-Index Confusion
BIT is fundamentally 1-indexed. For 0-indexed arrays, always add 1 when converting.

```java
// 0-indexed wrapper
public void update0(int idx, int delta) {
    update(idx + 1, delta);
}
public int prefixSum0(int idx) {
    return prefixSum(idx + 1);
}
```

### 2. Overflow
BIT stores ints. For n = 10⁵ with sum up to 10¹² per position, use `long[]`.

### 3. Negative Values
BIT works with negative values — but prefix sums can be negative.

### 4. Not Using i & -i for LSB
Common bug: using `i & (i-1)` instead of `i & -i`. Remember: `i & -i` gives LSB, `i & (i-1)` removes LSB (used for different tree traversal in Fenwick).

### 5. One-Based vs Zero-Based in Range Sum
`rangeSum(l, r)` must be called with inclusive bounds: `prefixSum(r) - prefixSum(l-1)`.

---

## Advanced Variants

### 2D Fenwick Tree
For grid range sum queries:
```java
void update2D(int x, int y, int delta) {
    for (int i = x; i <= n; i += i & -i)
        for (int j = y; j <= m; j += j & -j)
            tree[i][j] += delta;
}
int query2D(int x, int y) { /* similar nesting */ }
```

### Point Update, Range Query (Non-sum)
BIT works for any **invertible associative operation** (xor, multiplication mod prime). Not for min/max.

### Range Update, Range Query
Uses two BITs:
- `B1`: diff update (range update, point query)
- `B2`: correction for range query
```java
// Update: add v to [l, r]
// B1.update(l, v), B1.update(r+1, -v)
// B2.update(l, v*(l-1)), B2.update(r+1, -v*r)
// Query: prefixSum(r)*r - B2.prefixSum(r) - (prefixSum(l-1)*(l-1) - B2.prefixSum(l-1))
```

---

## Testing the Implementation

```java
FenwickTree ft = new FenwickTree(10);
ft.update(1, 3);
ft.update(2, 5);
ft.update(3, 2);

assert ft.prefixSum(1) == 3;
assert ft.prefixSum(2) == 8;
assert ft.prefixSum(3) == 10;
assert ft.rangeSum(1, 3) == 10;
assert ft.rangeSum(2, 3) == 7;
```

### Edge Case Tests
```java
// Empty (n=0), single element
FenwickTree ft1 = new FenwickTree(1);
ft1.update(1, 5);
assert ft1.prefixSum(1) == 5;
assert ft1.rangeSum(1, 1) == 5;

// Range update, point query
FenwickTree ft2 = new FenwickTree(5);
ft2.rangeUpdate(2, 4, 10);
assert ft2.pointQuery(1) == 0;  // outside range
assert ft2.pointQuery(3) == 10; // inside range

// Large values
FenwickTree ft3 = new FenwickTree(100000);
for (int i = 1; i <= 100000; i++) ft3.update(i, i);
assert ft3.prefixSum(100000) == 100000 * 100001 / 2;
```

---

## Key Interview Takeaways

1. **BIT = O(log n) point update + prefix sum**. The simplest structure for cumulative frequency.

2. **i & -i is the core operation**. Understand how it navigates the tree (up: add LSB, down: subtract LSB).

3. **BIT stores range sums**: tree[i] = sum of A[i - LSB(i) + 1 .. i]. Internalise this.

4. **Range update + point query** via difference array is a common trick.

5. **Range update + range query** requires 2 BITs (more complex, but still simpler than segment tree).

6. **Cannot do min/max** — use segment tree for those.
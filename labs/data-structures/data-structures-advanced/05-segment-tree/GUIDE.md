# Guide: Segment Tree

## Overview

A **Segment Tree** is a binary tree data structure used for storing information about intervals or segments. It allows querying of which segments contain a given point, or aggregation over a range (sum, min, max, GCD, etc.) in O(log n) time, while supporting updates in O(log n) time.

Unlike a Fenwick Tree (which only handles sum/xor), a segment tree can handle **any associative operation**, making it the most versatile range query data structure.

### Why Not Use a Fenwick Tree?

| Aspect | Fenwick Tree | Segment Tree |
|--------|-------------|--------------|
| Operation | Sum/Xor only | Any associative (sum, min, max, GCD, AND, OR) |
| Lazy propagation | Complex (2 BITs) | Built-in |
| Memory | O(n) | O(4n) |
| Constants | Low | Higher (recursion) |
| Implementation | ~15 lines | ~40+ lines |

**Key Insight**: If you need min/max/GCD over a range with updates, segment tree is your only choice among simple tree structures.

---

## ASCII Diagram

```
Array A = [2, 5, -1, 3, 7, 1, 4, 6]

Segment Tree (sum operation):

                [0,7] sum=27
               /         \
          [0,3] sum=9   [4,7] sum=18
          /     \        /      \
      [0,1]    [2,3]  [4,5]   [6,7]
      sum=7    sum=2  sum=8   sum=10
      /   \    /   \   /  \    /  \
    [0]  [1] [2] [3] [4] [5] [6] [7]
     2    5  -1   3   7   1   4   6
```

### Node Coverage

Each node represents a range `[l, r]`:
- **Leaf**: range of length 1 → array element
- **Internal node**: union of its two children's ranges → aggregated value

### Recursive Structure

```java
query(node, ql, qr, l, r):
  if ql <= l && r <= qr → return tree[node]    // full overlap
  if qr < l || r < ql → return identity        // no overlap
  mid = (l + r) / 2
  left = query(2*node, ql, qr, l, mid)
  right = query(2*node+1, ql, qr, mid+1, r)
  return combine(left, right)                  // partial overlap: combine
```

---

## Source Code Walkthrough

The implementation supports sum, min, and max operations via a generic combine function.

### Node Structure (arrays)

```java
int[] tree;    // segment tree values
int[] lazy;    // pending updates for lazy propagation
int n;         // array size
int operation; // 0=sum, 1=min, 2=max
```

Three arrays of size 4n (or 2*nextPowerOfTwo for iterative version).

### Build (lines ~15-25)

```java
public void build(int[] arr) {
    buildHelper(1, 0, n - 1, arr);
}

private void buildHelper(int node, int l, int r, int[] arr) {
    if (l == r) {
        tree[node] = arr[l];
        return;
    }
    int mid = l + (r - l) / 2;
    buildHelper(node * 2, l, mid, arr);
    buildHelper(node * 2 + 1, mid + 1, r, arr);
    tree[node] = combine(tree[node * 2], tree[node * 2 + 1]);
}
```

**Complexity**: O(n) — each element visited once, each internal node computed once.

### Range Query (lines ~27-40)

```java
public int query(int ql, int qr) {
    return queryHelper(1, 0, n - 1, ql, qr);
}

private int queryHelper(int node, int l, int r, int ql, int qr) {
    if (ql > r || qr < l) return identity();   // no overlap
    if (ql <= l && r <= qr) return tree[node];  // full overlap

    push(node); // propagate lazy before recursing
    int mid = l + (r - l) / 2;
    int left = queryHelper(node * 2, l, mid, ql, qr);
    int right = queryHelper(node * 2 + 1, mid + 1, r, ql, qr);
    return combine(left, right);
}
```

**Three cases**:
1. **No overlap**: return identity (0 for sum, ∞ for min, -∞ for max)
2. **Full overlap**: return stored node value
3. **Partial overlap**: push lazy flag, recurse on children, combine results

### Point Update (lines ~42-52)

```java
public void pointUpdate(int idx, int value) {
    pointUpdateHelper(1, 0, n - 1, idx, value);
}

private void pointUpdateHelper(int node, int l, int r, int idx, int value) {
    if (l == r) {
        tree[node] = value;
        return;
    }
    int mid = l + (r - l) / 2;
    if (idx <= mid) pointUpdateHelper(node * 2, l, mid, idx, value);
    else pointUpdateHelper(node * 2 + 1, mid + 1, r, idx, value);
    tree[node] = combine(tree[node * 2], tree[node * 2 + 1]);
}
```

### Range Update with Lazy Propagation (lines ~54-75)

```java
public void rangeUpdate(int ql, int qr, int delta) {
    rangeUpdateHelper(1, 0, n - 1, ql, qr, delta);
}

private void rangeUpdateHelper(int node, int l, int r, int ql, int qr, int delta) {
    if (ql > r || qr < l) return;
    if (ql <= l && r <= qr) {
        apply(node, delta, l, r);  // apply update, mark lazy
        return;
    }
    push(node);
    int mid = l + (r - l) / 2;
    rangeUpdateHelper(node * 2, l, mid, ql, qr, delta);
    rangeUpdateHelper(node * 2 + 1, mid + 1, r, ql, qr, delta);
    tree[node] = combine(tree[node * 2], tree[node * 2 + 1]);
}

private void apply(int node, int delta, int l, int r) {
    if (operation == 0) { // sum
        tree[node] += delta * (r - l + 1);
        lazy[node] += delta;
    } else if (operation == 1) { // min
        tree[node] += delta;
        lazy[node] += delta;
    } else if (operation == 2) { // max
        tree[node] += delta;
        lazy[node] += delta;
    }
}

private void push(int node) {
    if (lazy[node] != 0) {
        int mid = ...; // we need l,r or store them
        apply(node * 2, lazy[node], l, mid);
        apply(node * 2 + 1, lazy[node], mid + 1, r);
        lazy[node] = 0;
    }
}
```

**Lazy propagation**: Instead of updating all leaf nodes in a range, mark a node as "lazy" and only update children when needed (during partial overlap traversal).

---

## Complexity Table

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Build | O(n) | O(4n) | Recursive or iterative |
| Point query | O(log n) | O(log n) | Recursion stack |
| Point update | O(log n) | O(log n) | Recursion stack |
| Range query | O(log n) | O(log n) | At most 4·log n nodes visited |
| Range update (lazy) | O(log n) | O(log n) | Same as query |
| Range update (no lazy) | O(n·log n) | — | Update each leaf individually |

### Why 4n Space?

For an array of size n, the segment tree has:
- Height: ceil(log₂ n) + 1
- Nodes: 2^(h+1) - 1 ≤ 4n - 1

**Safety**: Always allocate `int[4 * n]` or `int[2 * nextPowerOfTwo]`.

---

## Comparison with Alternatives

| Feature | Segment Tree | Fenwick Tree | Sparse Table | SQRT Decomposition |
|---------|-------------|-------------|-------------|-------------------|
| Build | O(n) | O(n) | O(n log n) | O(n) |
| Range sum | O(log n) | O(log n) | O(1) | O(√n) |
| Range min | O(log n) | No | O(1) | O(√n) |
| Range update | O(log n) | O(log n) (diff) | No | O(√n) |
| Point update | O(log n) | O(log n) | No | O(1) |
| Memory | O(4n) | O(n) | O(n log n) | O(n) |
| Lazy propagation | Yes | Complex | No | Yes |

**When NOT to use segment tree:**
- Only need sum queries with point updates (use BIT — simpler, faster)
- Read-only static array (use sparse table for O(1) min/max)
- Small n (< 1000): naive array is fine
- Memory is critical (use BIT or SQRT decomposition)

---

## Use Cases

### 1. Range Minimum Query (RMQ) with Updates
**System**: Stock price monitoring — min price in date range
**Why ST**: Supports updates (new prices) and range queries

### 2. Meeting Room Booking
**System**: Calendar app checking room availability
**Why ST**: Range update (book time slot) + range query (check max over time range)

### 3. Skyline Problem / Rectangle Overlap
**System**: Graphics / UI layout
**Why ST**: 2D segment tree or segment tree + line sweep

### 4. Range GCD Queries
**System**: Financial report — GCD of range with point updates
**Why ST**: GCD is associative, BIT can't do it

### 5. Lazy Propagation for Range Assignment
**System**: Database snapshot versioning
**Why ST**: Set entire range to a value in O(log n), query at any point

### 6. Online Median with Segment Tree (over frequency array)
**System**: Streaming analytics
**Why ST**: BIT would also work for sum, but ST gives flexibility for other aggregates

---

## Common Pitfalls

### 1. Off-by-One in Recursion Range
Always use inclusive `[l, r]` for node's covered range. Mid = `l + (r - l) / 2`.

### 2. Forgetting `push` in Query
If there are pending lazy updates, query must push before recursing. Otherwise query returns stale values.

### 3. Array Size
Allocating `int[2 * n]` instead of `int[4 * n]` causes IndexOutOfBounds. Use 4n for safety.

### 4. Identity Value
Wrong identity value: For sum, identity = 0. For min, identity = Integer.MAX_VALUE. For max, identity = Integer.MIN_VALUE. For GCD, identity = 0.

### 5. Stack Overflow
Recursive segment tree on large arrays (n > 10⁵) can cause stack overflow due to deep recursion. Use iterative segment tree or increase stack size.

---

## Advanced Variants

### Iterative Segment Tree
Non-recursive, uses 2n space. Faster but no lazy propagation support.

```java
class IterativeSegmentTree {
    int[] tree;
    int n;

    IterativeSegmentTree(int[] arr) {
        n = arr.length;
        tree = new int[2 * n];
        System.arraycopy(arr, 0, tree, n, n);
        for (int i = n - 1; i > 0; i--)
            tree[i] = tree[2*i] + tree[2*i+1];
    }

    void update(int idx, int val) {
        idx += n;
        tree[idx] = val;
        for (idx /= 2; idx > 0; idx /= 2)
            tree[idx] = tree[2*idx] + tree[2*idx+1];
    }

    int query(int l, int r) {
        l += n; r += n;
        int sum = 0;
        while (l <= r) {
            if ((l & 1) == 1) sum += tree[l++];
            if ((r & 1) == 0) sum += tree[r--];
            l /= 2; r /= 2;
        }
        return sum;
    }
}
```

### Persistent Segment Tree
Versioned segment tree. Each update creates new nodes along the path to the root. Allows querying any historical version. Used in competitive programming (range k-th order statistic).

### 2D Segment Tree
Segment tree over x-coordinates, each node contains a segment tree over y-coordinates. O(log² n) per query. High memory: O(n²) worst case.

### Dynamic Segment Tree (Sparse)
Create nodes on-demand. No pre-allocation. Used when range is large (e.g., 10⁹) but only O(n log R) operations.

---

## Testing the Implementation

```java
SegmentTree st = new SegmentTree(new int[]{2, 5, -1, 3, 7, 1, 4, 6}, 0); // sum

assert st.query(0, 7) == 27;  // total sum
assert st.query(0, 3) == 9;   // 2+5+(-1)+3
assert st.query(2, 4) == 9;   // -1+3+7

st.pointUpdate(2, 10);
assert st.query(0, 7) == 38;  // 27 + 11 (delta)
assert st.query(2, 4) == 20;  // 10+3+7
```

### Edge Cases
```java
// Single element
SegmentTree st1 = new SegmentTree(new int[]{42}, 0);
assert st1.query(0, 0) == 42;

// All zeros
SegmentTree st2 = new SegmentTree(new int[1000], 0);
assert st2.query(0, 999) == 0;

// Negative values
SegmentTree st3 = new SegmentTree(new int[]{-5, -3, -10}, 1); // min
assert st3.query(0, 2) == -10;

// Lazy propagation
SegmentTree st4 = new SegmentTree(new int[]{1, 2, 3, 4}, 0);
st4.rangeUpdate(0, 2, 10);
assert st4.query(0, 3) == 1+10 + 2+10 + 3+10 + 4;
assert st4.query(1, 1) == 12;
```

---

## Key Interview Takeaways

1. **Segment tree = any associative operation over ranges**. The most versatile range DS.

2. **Lazy propagation** makes range updates O(log n). Without it, they're O(n log n).

3. **Recursive vs Iterative**: Recursive is clearer, iterative is faster/less memory. Know both.

4. **4n space**: Always allocate 4n for recursive, 2n for iterative (no lazy).

5. **Generic operations**: Pass a `combine` function for reusable segment tree (sum, min, max, GCD, custom).

6. **Real-world**: Used in databases (interval indexes), game engines (spatial), calendars (overlap detection), and stock exchanges (VWAP calculations).
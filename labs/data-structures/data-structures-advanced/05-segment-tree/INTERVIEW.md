# Interview Questions: Segment Tree

## 17 FAANG-Style Interview Questions

### Question 1
> Implement a segment tree for range sum query with point updates.

**Answer:**

```java
class SegmentTree {
    int[] tree;
    int n;

    SegmentTree(int[] arr) {
        n = arr.length;
        tree = new int[4 * n];
        build(arr, 1, 0, n - 1);
    }

    private void build(int[] arr, int node, int l, int r) {
        if (l == r) { tree[node] = arr[l]; return; }
        int mid = l + (r - l) / 2;
        build(arr, node * 2, l, mid);
        build(arr, node * 2 + 1, mid + 1, r);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    void update(int idx, int val) {
        update(1, 0, n - 1, idx, val);
    }

    private void update(int node, int l, int r, int idx, int val) {
        if (l == r) { tree[node] = val; return; }
        int mid = l + (r - l) / 2;
        if (idx <= mid) update(node * 2, l, mid, idx, val);
        else update(node * 2 + 1, mid + 1, r, idx, val);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    int query(int ql, int qr) {
        return query(1, 0, n - 1, ql, qr);
    }

    private int query(int node, int l, int r, int ql, int qr) {
        if (ql > r || qr < l) return 0;
        if (ql <= l && r <= qr) return tree[node];
        int mid = l + (r - l) / 2;
        return query(node * 2, l, mid, ql, qr)
             + query(node * 2 + 1, mid + 1, r, ql, qr);
    }
}
```

**Complexity**: O(log n) per operation. Build O(n).

---

### Question 2
> Compare a segment tree with a Fenwick tree. When would you choose each?

**Answer:**
**Segment Tree** when:
- Need min/max/GCD/AND/OR (non-sum operations)
- Need lazy propagation for range updates
- Familiarity with recursion is OK
- Memory (4n) is available

**Fenwick Tree** when:
- Only need sum (or XOR)
- Want faster code (lower constant)
- Memory constrained (n vs 4n)
- Want simpler implementation
- Range update + range query with 2 BITs suffices

---

### Question 3
> Implement lazy propagation for range updates.

**Answer:**

```java
class LazySegmentTree {
    int[] tree, lazy;
    int n;

    LazySegmentTree(int[] arr) {
        n = arr.length;
        tree = new int[4 * n];
        lazy = new int[4 * n];
        build(arr, 1, 0, n - 1);
    }

    void update(int ql, int qr, int delta) {
        update(1, 0, n - 1, ql, qr, delta);
    }

    private void update(int node, int l, int r, int ql, int qr, int delta) {
        if (ql > r || qr < l) return;
        if (ql <= l && r <= qr) {
            tree[node] += delta * (r - l + 1);
            lazy[node] += delta;
            return;
        }
        push(node, l, r);
        int mid = l + (r - l) / 2;
        update(node * 2, l, mid, ql, qr, delta);
        update(node * 2 + 1, mid + 1, r, ql, qr, delta);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    int query(int ql, int qr) {
        return query(1, 0, n - 1, ql, qr);
    }

    private int query(int node, int l, int r, int ql, int qr) {
        if (ql > r || qr < l) return 0;
        if (ql <= l && r <= qr) return tree[node];
        push(node, l, r);
        int mid = l + (r - l) / 2;
        return query(node * 2, l, mid, ql, qr)
             + query(node * 2 + 1, mid + 1, r, ql, qr);
    }

    private void push(int node, int l, int r) {
        if (lazy[node] != 0) {
            int mid = l + (r - l) / 2;
            tree[node * 2] += lazy[node] * (mid - l + 1);
            tree[node * 2 + 1] += lazy[node] * (r - mid);
            lazy[node * 2] += lazy[node];
            lazy[node * 2 + 1] += lazy[node];
            lazy[node] = 0;
        }
    }
}
```

---

### Question 4
> Implement a My Calendar I/II/III class using a segment tree.

**Answer:**
LC 732 (My Calendar III) — find the maximum overlapping intervals.

```java
class MyCalendarThree {
    LazySegmentTree st;
    int maxTime = 1_000_000_000; // or compress coordinates

    MyCalendarThree() {
        int[] arr = new int[maxTime + 1];
        st = new LazySegmentTree(arr); // max operation
    }

    int book(int start, int end) {
        st.update(start, end - 1, 1); // range increment
        return st.query(0, maxTime);
    }
}
```

For sparse calendars, use a dynamic segment tree with coordinate compression. Complexity: O(n log R) for n bookings.

---

### Question 5
> Find the k-th smallest element in the union of two sorted arrays using a segment tree.

**Answer:**
This is typically done with binary search. However, for a dynamic multiset with range queries, a segment tree over the frequency array works: binary search on the segment tree to find the smallest index where prefix sum ≥ k (walk the tree in O(log n)).

---

### Question 6
> Solve "Falling Squares" (LC 699) using a segment tree.

**Answer:**
Each square falling at position (x, width, height) updates the range [x, x+width-1] with `max(currentHeight, newHeight)`. Query the range for current max height, then range update with that max + height.

**Algorithm:**
```java
int fallingSquares(int[][] positions) {
    // coordinate compress
    TreeSet<Integer> coords = new TreeSet<>();
    for (int[] p : positions) {
        coords.add(p[0]);
        coords.add(p[0] + p[1] - 1);
    }
    // ... map to compressed indices
    SegmentTree st = new SegmentTree(maxArr, maxOp);
    int maxH = 0;
    for (int[] p : positions) {
        int l = map.get(p[0]);
        int r = map.get(p[0] + p[1] - 1);
        int cur = st.query(l, r);
        st.update(l, r, cur + p[2]);
        maxH = Math.max(maxH, cur + p[2]);
    }
    return maxH;
}
```

---

### Question 7
> Given an array, find the first index where prefix sum >= target (binary search on segment tree).

**Answer:**
Walk the segment tree: at each node, if left child sum >= target, go left; else subtract left sum and go right.

---

### Question 8
> Design a dynamic range GCD query structure with point updates.

**Answer:**
Segment tree with GCD as the combine function:
```java
tree[node] = gcd(tree[2*node], tree[2*node+1]);
```

GCD identity: gcd(x, 0) = x. So identity = 0. Query and update same as standard segment tree.

---

### Question 9
> Implement an iterative segment tree (non-recursive).

**Answer:**
```java
class IterativeSumST {
    int n;
    int[] tree;

    IterativeSumST(int[] arr) {
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

---

### Question 10
> Design a persistent segment tree. What's its use?

**Answer:**
Each update creates a new root and O(log n) new nodes (only the path from root to the updated leaf). All other nodes are shared.

**Use cases:**
- Range k-th smallest in subarray (with coordination of value segment trees)
- Query historical versions of data
- Multi-dimensional queries by building prefix-sum segment trees

**Space**: O(n + m log n) for m updates.

---

### Question 11
> Given an array of intervals, find the point with maximum overlap.

**Answer:**
Coordinate compress interval endpoints. Use segment tree with range updates (+1 for each interval). Query max over the compressed range.

---

### Question 12
> How do you handle 10⁹ range with segment tree?

**Answer:**
**Dynamic segment tree**: Create nodes only for ranges that are actually updated/queried. Initial root covers [1, 10⁹] with null children.

```java
class DynamicNode {
    int val, lazy;
    DynamicNode left, right;
}
```

Create left/right children lazily during update/query traversal. O(log R) per operation, O(m log R) space for m updates.

---

### Question 13
> Design a segment tree that supports both range increment and range assignment (set all to value).

**Answer:**
Use two lazy values: one for increment, one for assignment. Assignment has priority over increment. When pushing, if assignment is pending, set children to assigned value and clear their increment.

---

### Question 14
> Given two arrays A and B, find max sum subarray of A after adding B values at certain points.

**Answer:**
Use segment tree storing (sum, best prefix, best suffix, best). Merge:
```java
Node merge(Node l, Node r) {
    return new Node(
        l.sum + r.sum,
        Math.max(l.bestPrefix, l.sum + r.bestPrefix),
        Math.max(r.bestSuffix, r.sum + l.bestSuffix),
        Math.max(l.best, Math.max(r.best, l.bestSuffix + r.bestPrefix))
    );
}
```

This is the classic maximum subarray sum segment tree. Supports point updates to A and range queries.

---

### Question 15
> Implement 2D segment tree for grid sum queries.

**Answer:**
Outer tree over rows, each node contains inner tree over columns.

```java
class SegmentTree2D {
    int[][] tree;
    int n, m;

    SegmentTree2D(int[][] grid) {
        n = grid.length; m = grid[0].length;
        tree = new int[4 * n][4 * m];
        // build recursively
    }

    void update(int x, int y, int val) {
        updateY(1, 0, n-1, x, y, val);
    }

    int query(int x1, int y1, int x2, int y2) {
        return queryX(1, 0, n-1, x1, x2, y1, y2);
    }
}
```

---

### Question 16
> Explain how a segment tree can be used to count the number of inversions.

**Answer:**
Same as BIT approach: coordinate compress, traverse right to left, query range [1, rank-1] for count of smaller elements, update at rank by +1. Segment tree works identically to BIT here, but with larger constant overhead.

---

### Question 17
> Design an LRU cache with range queries (find oldest entry within time range).

**Answer:**
Use TreeMap for key ordering + segment tree over timestamps for range max queries. Or use a balanced BST (Red-Black) directly (Java TreeMap).
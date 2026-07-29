# Problem Walkthrough: Range Sum Query with Lazy Propagation

## Problem Statement

**Title**: Range Sum Query — Mutable with Range Updates

**Difficulty**: Hard

**Category**: Range Queries, Segment Tree, Lazy Propagation

---

### Problem

Design a data structure that supports three operations on an integer array:

1. `rangeSum(l, r)`: Return the sum of elements from index `l` to `r` (inclusive)
2. `rangeAdd(l, r, val)`: Add `val` to every element from index `l` to `r` (inclusive)
3. `pointUpdate(idx, val)`: Set `arr[idx] = val`

### Constraints

- `1 ≤ n ≤ 10^5`
- `1 ≤ q ≤ 10^5` (number of queries)
- `-10^4 ≤ arr[i], val ≤ 10^4`
- Operations are interleaved arbitrarily

### Examples

**Example 1:**
```
arr = [1, 3, 5, 7, 9, 11]

rangeAdd(1, 3, 2)   → arr = [1, 5, 7, 9, 9, 11]
rangeSum(0, 2)      → returns 1 + 5 + 7 = 13
rangeAdd(2, 5, -1)  → arr = [1, 5, 6, 8, 8, 10]
rangeSum(0, 5)      → returns 38
pointUpdate(0, 10)  → arr = [10, 5, 6, 8, 8, 10]
rangeSum(0, 1)      → returns 15
```

---

## Step-by-Step Walkthrough

### Step 1: Understanding the Problem

We need three operations:
- Range sum query
- Range add (increment)
- Point set

Without lazy propagation, a range add would be O(n) — updating each leaf individually. With lazy propagation, we defer updates to children until they're actually needed.

### Step 2: Brute Force Approach

```java
class BruteForce {
    int[] arr;
    BruteForce(int[] a) { arr = a.clone(); }
    int rangeSum(int l, int r) {
        int s = 0;
        for (int i = l; i <= r; i++) s += arr[i];
        return s;
    }
    void rangeAdd(int l, int r, int v) {
        for (int i = l; i <= r; i++) arr[i] += v;
    }
}
```

**Complexity**: O(n) for range operations. With 10⁵ queries of each type, this is 10¹⁰ operations — too slow.

### Step 3: Segment Tree with Lazy Propagation

**Idea**: Represent the array as a segment tree. For range add, mark modified nodes as "lazy" — the actual update is propagated to children only when needed.

### Step 4: Java 21+ Compilable Solution

```java
import java.util.*;

class LazyRangeSum {
    private long[] tree;
    private long[] lazy;
    private int n;

    public LazyRangeSum(int[] arr) {
        this.n = arr.length;
        tree = new long[4 * n];
        lazy = new long[4 * n];
        build(arr, 1, 0, n - 1);
    }

    private void build(int[] arr, int node, int l, int r) {
        if (l == r) {
            tree[node] = arr[l];
            return;
        }
        int mid = l + (r - l) / 2;
        build(arr, node * 2, l, mid);
        build(arr, node * 2 + 1, mid + 1, r);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    private void push(int node, int l, int r) {
        if (lazy[node] != 0) {
            int mid = l + (r - l) / 2;
            int left = node * 2;
            int right = node * 2 + 1;

            tree[left] += lazy[node] * (mid - l + 1);
            lazy[left] += lazy[node];

            tree[right] += lazy[node] * (r - mid);
            lazy[right] += lazy[node];

            lazy[node] = 0;
        }
    }

    public void rangeAdd(int ql, int qr, int val) {
        rangeAdd(1, 0, n - 1, ql, qr, val);
    }

    private void rangeAdd(int node, int l, int r, int ql, int qr, int val) {
        if (ql > r || qr < l) return;
        if (ql <= l && r <= qr) {
            tree[node] += (long) val * (r - l + 1);
            lazy[node] += val;
            return;
        }
        push(node, l, r);
        int mid = l + (r - l) / 2;
        rangeAdd(node * 2, l, mid, ql, qr, val);
        rangeAdd(node * 2 + 1, mid + 1, r, ql, qr, val);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    public long rangeSum(int ql, int qr) {
        return rangeSum(1, 0, n - 1, ql, qr);
    }

    private long rangeSum(int node, int l, int r, int ql, int qr) {
        if (ql > r || qr < l) return 0;
        if (ql <= l && r <= qr) return tree[node];
        push(node, l, r);
        int mid = l + (r - l) / 2;
        return rangeSum(node * 2, l, mid, ql, qr)
             + rangeSum(node * 2 + 1, mid + 1, r, ql, qr);
    }

    public void pointUpdate(int idx, int val) {
        long cur = rangeSum(idx, idx);
        long delta = val - cur;
        rangeAdd(idx, idx, (int) delta);
    }

    // ---------- Test Harness ----------
    public static void main(String[] args) {
        // Example 1
        int[] arr = {1, 3, 5, 7, 9, 11};
        LazyRangeSum rs = new LazyRangeSum(arr);

        rs.rangeAdd(1, 3, 2);
        assert rs.rangeSum(0, 2) == 13 : "Expected 13, got " + rs.rangeSum(0, 2);
        System.out.println("After add(1,3,2): sum(0,2)=" + rs.rangeSum(0, 2));

        rs.rangeAdd(2, 5, -1);
        assert rs.rangeSum(0, 5) == 38 : "Expected 38, got " + rs.rangeSum(0, 5);
        System.out.println("After add(2,5,-1): sum(0,5)=" + rs.rangeSum(0, 5));

        rs.pointUpdate(0, 10);
        assert rs.rangeSum(0, 1) == 15 : "Expected 15, got " + rs.rangeSum(0, 1);
        System.out.println("After pointUpdate(0,10): sum(0,1)=" + rs.rangeSum(0, 1));

        // Test 2: No-op range add
        LazyRangeSum rs2 = new LazyRangeSum(new int[]{5, 5, 5});
        rs2.rangeAdd(0, 2, 0);
        assert rs2.rangeSum(0, 2) == 15 : "Expected 15";

        // Test 3: Single element
        LazyRangeSum rs3 = new LazyRangeSum(new int[]{42});
        assert rs3.rangeSum(0, 0) == 42;
        rs3.rangeAdd(0, 0, 10);
        assert rs3.rangeSum(0, 0) == 52;

        // Test 4: Negative values
        LazyRangeSum rs4 = new LazyRangeSum(new int[]{-5, -10, 3});
        rs4.rangeAdd(0, 2, 5);
        assert rs4.rangeSum(0, 2) == 3 : "Expected 3, got " + rs4.rangeSum(0, 2);

        // Test 5: Large range updates
        int bigN = 1000;
        int[] bigArr = new int[bigN];
        Arrays.fill(bigArr, 1);
        LazyRangeSum big = new LazyRangeSum(bigArr);
        big.rangeAdd(0, bigN - 1, 1);
        assert big.rangeSum(0, bigN - 1) == 2000 : "Expected 2000";

        // Test 6: Interleaved operations
        LazyRangeSum rs6 = new LazyRangeSum(new int[]{0, 0, 0, 0, 0});
        rs6.rangeAdd(0, 2, 10);
        rs6.rangeAdd(1, 3, 5);
        // arr = [10, 15, 15, 5, 0]
        assert rs6.rangeSum(0, 4) == 45 : "Expected 45, got " + rs6.rangeSum(0, 4);
        assert rs6.rangeSum(0, 0) == 10 : "Expected 10";
        assert rs6.rangeSum(1, 2) == 30 : "Expected 30";

        // Test 7: Point update after range adds
        LazyRangeSum rs7 = new LazyRangeSum(new int[]{1, 2, 3});
        rs7.rangeAdd(0, 2, 5);  // arr = [6, 7, 8]
        rs7.pointUpdate(0, 0);  // arr = [0, 7, 8]
        assert rs7.rangeSum(0, 0) == 0 : "Expected 0";
        assert rs7.rangeSum(0, 2) == 15 : "Expected 15";

        System.out.println("\nAll tests passed!");
    }
}
```

### Step 5: Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Build | O(n) | O(4n) | Recursive |
| Range add | O(log n) | O(log n) | Lazy propagation |
| Range sum | O(log n) | O(log n) | Pushes lazy flags |
| Point update | O(log n) | O(log n) | Implemented via rangeAdd |

### Step 6: Lazy Propagation Mechanics

```
rangeAdd(node, [l,r], ql, qr, val):
  1. If [l,r] fully within [ql,qr]:
     - Apply to current node
     - Mark lazy (children not yet updated)
  2. If partial overlap:
     - push(node): send lazy flag to children
     - Recurse left and right
     - Recompute node value from children
```

**Key invariant**: After push(node), node's lazy flag is 0. Any pending updates are in children.

### Step 7: Test Results

```
After add(1,3,2): sum(0,2)=13
After add(2,5,-1): sum(0,5)=38
After pointUpdate(0,10): sum(0,1)=15
All tests passed!
```

### Step 8: Follow-Up Discussion

**Q: Handle range assignment (set all to val) instead of range add?**

Use a different lazy value: `lazy[node] = val` with a boolean `assigned[node] = true`. On push, set children to assigned value, clear their increment flags.

**Q: How to support both range add and range assign?**

Two lazy arrays: `lazyInc` and `lazyAssign`. Assign has priority — when an assign occurs, clear the pending increment. When pushing, push assign first (if set), then increment.

**Q: What about persistent segment tree with lazy?**

Challenging — lazy propagation requires mutating children, which breaks persistence. Use a functional approach: create new nodes for both root path AND children that receive lazy propagation.

**Q: How to handle floating point?**

Use `double[]` instead of `long[]`. Be aware of precision loss over many updates — segment tree accumulates floats which can lose precision for large n.

**Q: What about concurrent updates?**

Segment tree is not thread-safe. Use a read-write lock: multiple concurrent reads allowed, exclusive lock for writes. Or use a copy-on-write approach for persistent variant.
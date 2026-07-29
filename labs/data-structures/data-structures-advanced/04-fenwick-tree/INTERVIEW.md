# Interview Questions: Fenwick Tree (Binary Indexed Tree)

## 17 FAANG-Style Interview Questions

### Question 1
> Implement a Fenwick Tree with point update and prefix sum query.

**Answer:**

```java
class FenwickTree {
    int[] tree;
    int n;

    FenwickTree(int n) {
        this.n = n;
        tree = new int[n + 1];
    }

    void update(int idx, int delta) {
        while (idx <= n) {
            tree[idx] += delta;
            idx += idx & -idx;
        }
    }

    int prefixSum(int idx) {
        int sum = 0;
        while (idx > 0) {
            sum += tree[idx];
            idx -= idx & -idx;
        }
        return sum;
    }

    int rangeSum(int l, int r) {
        return prefixSum(r) - prefixSum(l - 1);
    }
}
```

**Complexity**: O(log n) per operation, O(n) space.

---

### Question 2
> Explain the `i & -i` operations. Why do they work?

**Answer:**
`i & -i` isolates the lowest set bit of `i`. In two's complement, `-i = ~i + 1`.

**Example**: i = 12 (1100), -i = 0100 (0100). i & -i = 0100 = 4.

**Update** (`i += LSB`): Jump to the next index whose range includes `i`. Each jump moves to the parent in the BIT tree.

**Query** (`i -= LSB`): Remove the current index's contribution, move to the next non-overlapping range. Each step removes the covered segment.

---

### Question 3
> When would you choose a Fenwick Tree over a Segment Tree?

**Answer:**
Choose BIT when:
1. Operation is **sum/xor** (invertible)
2. **Memory constrained** (BIT: n+1 ints vs Segment Tree: 4n ints)
3. **Implementation simplicity** matters
4. **Constants matter** — BIT is ~3x faster than recursive segment tree

Choose Segment Tree when:
1. Need **min/max/GCD** operations
2. Need **lazy propagation** for range updates
3. Need **2D operations** with non-sum semantics

---

### Question 4
> How do you implement range update and range query using a Fenwick Tree?

**Answer:**
Use two BITs. Let `A` be the original array, `diff[i] = A[i] - A[i-1]`.

For range update `A[l..r] += v`:
```
B1: point update at l by +v, at r+1 by -v
B2: point update at l by +v*(l-1), at r+1 by -v*r
```

For prefix sum query `sum(A[1..i])`:
```
B1.prefixSum(i) * i - B2.prefixSum(i)
```

```java
class RangeBIT {
    FenwickTree B1, B2;

    void rangeUpdate(int l, int r, int v) {
        B1.update(l, v);
        B1.update(r + 1, -v);
        B2.update(l, v * (l - 1));
        B2.update(r + 1, -v * r);
    }

    int prefixSum(int i) {
        return B1.prefixSum(i) * i - B2.prefixSum(i);
    }

    int rangeSum(int l, int r) {
        return prefixSum(r) - prefixSum(l - 1);
    }
}
```

---

### Question 5
> Given an array, count the number of inversions (i < j, A[i] > A[j]).

**Answer:**
LC 315 (Count of Smaller Numbers After Self) and generic inversion count.

```java
int countInversions(int[] arr) {
    // Coordinate compression
    int[] sorted = arr.clone();
    Arrays.sort(sorted);
    Map<Integer, Integer> rank = new HashMap<>();
    for (int i = 0; i < sorted.length; i++)
        rank.put(sorted[i], i + 1); // 1-indexed for BIT

    FenwickTree ft = new FenwickTree(arr.length);
    int inv = 0;

    // Traverse right to left
    for (int i = arr.length - 1; i >= 0; i--) {
        int r = rank.get(arr[i]);
        inv += ft.prefixSum(r - 1); // count elements smaller
        ft.update(r, 1);
    }
    return inv;
}
```

**Complexity**: O(n log n), better than O(n²) brute force.

---

### Question 6
> Find the k-th smallest element in a dynamic multiset.

**Answer:**
Use BIT over value range. BIT stores frequency of each value. Binary search on BIT (or walk the tree) to find the smallest index where prefix sum ≥ k.

```java
int kthSmallest(FenwickTree ft, int k) {
    int lo = 1, hi = ft.n;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (ft.prefixSum(mid) >= k) hi = mid;
        else lo = mid + 1;
    }
    return lo;
}
```

Alternatively, walk the BIT tree using LSB for O(log n):

```java
int kth(FenwickTree ft, int k) {
    int idx = 0;
    int bitMask = Integer.highestOneBit(ft.n);
    while (bitMask != 0) {
        int next = idx + bitMask;
        if (next <= ft.n && ft.tree[next] < k) {
            k -= ft.tree[next];
            idx = next;
        }
        bitMask >>= 1;
    }
    return idx + 1;
}
```

---

### Question 7
> Given an array of integers, find the number of subarrays whose sum equals k.

**Answer:**
LC 560 (Subarray Sum Equals K) can use BIT for dynamic frequency counting.

Alternatively, use prefix sum + HashMap (simpler). BIT is useful when the value range is bounded and we need dynamic updates to frequency of prefix sums.

---

### Question 8
> Implement a 2D Fenwick Tree for range sum queries on a grid.

**Answer:**

```java
class BIT2D {
    int[][] tree;
    int n, m;

    BIT2D(int n, int m) {
        this.n = n; this.m = m;
        tree = new int[n + 1][m + 1];
    }

    void update(int x, int y, int delta) {
        for (int i = x; i <= n; i += i & -i)
            for (int j = y; j <= m; j += j & -j)
                tree[i][j] += delta;
    }

    int query(int x, int y) {
        int sum = 0;
        for (int i = x; i > 0; i -= i & -i)
            for (int j = y; j > 0; j -= j & -j)
                sum += tree[i][j];
        return sum;
    }

    int rangeSum(int x1, int y1, int x2, int y2) {
        return query(x2, y2) - query(x1-1, y2) - query(x2, y1-1) + query(x1-1, y1-1);
    }
}
```

**Complexity**: O(log² n) per operation.

---

### Question 9
> Given an array, find the longest increasing subsequence (LIS) length using BIT.

**Answer:**
Coordinate compress the array. For each element in order, query BIT for max LIS ending at values < current value. Update BIT at current value with current LIS length + 1.

```java
int lis(int[] arr) {
    // coordinate compress
    int[] sorted = arr.clone();
    Arrays.sort(sorted);
    Map<Integer, Integer> rank = new HashMap<>();
    for (int i = 0; i < sorted.length; i++)
        rank.put(sorted[i], i + 1);

    FenwickTree ft = new FenwickTree(arr.length);
    int maxLen = 0;

    for (int v : arr) {
        int r = rank.get(v);
        int best = ft.prefixSum(r - 1); // max length ending before this value
        best++; // include current element
        ft.update(r, best); // BIT for max (non-standard)
        maxLen = Math.max(maxLen, best);
    }
    return maxLen;
}
```

**Note**: Standard BIT does sum. For max, we modify: `tree[idx] = Math.max(tree[idx], value)` and `max(tree[idx], max)` during query.

---

### Question 10
> Range sum query with updates — compare BIT and Segment Tree.

**Answer:**
Both support O(log n) point update + range sum. Differences:

| Aspect | BIT | Segment Tree |
|--------|-----|-------------|
| Code length | ~15 lines | ~40 lines (recursive) |
| Memory | n+1 | 4n |
| Recursion | No | Yes (iterative exists) |
| Range max/min | No | Yes |
| Lazy propagation | Complex (2 BITs) | Built-in |
| 2D extension | O(log² n) | O(log² n) |

**For sum queries only**: BIT is strictly better (simpler, faster, less memory).

---

### Question 11
> Design a data structure for the order book of a stock exchange using BIT.

**Answer:**
Each price level (integer cents) is an index. BIT stores:
- `bidVolume[i]`: volume at price i (for buys)
- `askVolume[i]`: volume at price i (for sells)

**Operations:**
- `addOrder(price, volume, side)`: update bidVolume[price] or askVolume[price]
- `cancelOrder(price, volume)`: update with negative delta
- `totalVolume(low, high)`: rangeSum between prices
- `bestBid()`: walk BIT from high to low, find first non-zero
- `bestAsk()`: walk BIT from low to high, find first non-zero

**Performance**: O(log P) per order, P = number of price levels (typically < 10⁴).

---

### Question 12
> How do you handle coordinate compression with BIT?

**Answer:**
When value range is large but count is small (e.g., 10⁵ values in range 10⁹):
1. Copy array, sort, remove duplicates
2. Map each original value to its 1-indexed rank
3. Use rank as BIT index

```java
int[] compress(int[] arr) {
    int[] sorted = arr.clone();
    Arrays.sort(sorted);
    Map<Integer, Integer> map = new HashMap<>();
    int rank = 1;
    for (int v : sorted) if (!map.containsKey(v)) map.put(v, rank++);
    int[] compressed = new int[arr.length];
    for (int i = 0; i < arr.length; i++) compressed[i] = map.get(arr[i]);
    return compressed;
}
```

---

### Question 13
> Given two arrays, count the number of good triplets (LC 2179).

**Answer:**
For each possible middle element, count elements before it that are smaller and elements after it that are larger. Use BIT traversing left-to-right and right-to-left.

```java
long countGoodTriplets(int[] nums1, int[] nums2) {
    int n = nums1.length;
    int[] pos = new int[n + 1];
    for (int i = 0; i < n; i++) pos[nums2[i]] = i;

    int[] arr = new int[n];
    for (int i = 0; i < n; i++) arr[i] = pos[nums1[i]];

    FenwickTree ft = new FenwickTree(n);
    long count = 0;
    for (int i = 0; i < n; i++) {
        int smallerLeft = ft.prefixSum(arr[i]);
        int largerRight = (n - arr[i] - 1) - (i - smallerLeft);
        count += (long) smallerLeft * largerRight;
        ft.update(arr[i] + 1, 1);
    }
    return count;
}
```

---

### Question 14
> Design a real-time analytics system tracking page views per minute for 100K URLs.

**Answer:**
Each URL has a BIT with 1440 slots (minutes per day). On page view:
```
bit.update(minuteOfDay, 1)
```

For analytics dashboard:
```
bit.rangeSum(startMinute, endMinute) → views in time range
```

**Scale**: 100K URLs × 1440 BIT slots × 27 bytes/slot ≈ 3.8GB. Use sharding by URL hash, each shard on one server. Aggregation layer combines results.

---

### Question 15
> Find the median in a stream of integers using BIT.

**Answer:**
Maintain frequency BIT over value range. On each insertion, increment BIT at value's index. To find median, binary search on BIT to find the smallest index where prefix sum > total/2.

---

### Question 16
> Given an array, find number of reverse pairs (i < j, nums[i] > 2 * nums[j]).

**Answer:**
LC 493 (Reverse Pairs). Coordinate compress the array AND 2*array. BIT over compressed values. Traverse left to right; for each element, query count of values seen so far that are > 2*current.

---

### Question 17
> How do you implement BIT in a concurrent environment?

**Answer:**
Use `AtomicIntegerArray` and CAS operations for thread-safe updates. Reads (prefix sum) don't need locks as long as writes use CAS — stale reads are acceptable in sum contexts (similar to ConcurrentHashMap).
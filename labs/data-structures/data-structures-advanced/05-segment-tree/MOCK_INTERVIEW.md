# Mock Interview: Segment Tree

## Setting

- **Round**: System design + data structures
- **Duration**: 60 minutes
- **Focus**: Range queries, lazy propagation

---

## Transcript

### Part 1: Warm-up (5 min)

**Interviewer:** Compare segment tree, Fenwick tree, and sparse table for range minimum queries.

**Candidate:**
| Structure | Build | Query | Update | Memory |
|-----------|-------|-------|--------|--------|
| Segment Tree | O(n) | O(log n) | O(log n) | O(4n) |
| Fenwick Tree | O(n) | — | — | — |
| Sparse Table | O(n log n) | O(1) | O(n) | O(n log n) |

Fenwick tree can't do RMQ at all. Sparse table is read-only. Segment tree is the only one that supports both RMQ and updates.

**Interviewer:** Good. Let's code a segment tree for range minimum with point updates.

**Candidate:**

```java
class RMQ {
    int[] tree;
    int n;

    RMQ(int[] arr) {
        n = arr.length;
        tree = new int[4 * n];
        build(arr, 1, 0, n - 1);
    }

    void build(int[] arr, int node, int l, int r) {
        if (l == r) { tree[node] = arr[l]; return; }
        int mid = l + (r - l) / 2;
        build(arr, node * 2, l, mid);
        build(arr, node * 2 + 1, mid + 1, r);
        tree[node] = Math.min(tree[node * 2], tree[node * 2 + 1]);
    }

    void update(int idx, int val) {
        update(1, 0, n - 1, idx, val);
    }

    void update(int node, int l, int r, int idx, int val) {
        if (l == r) { tree[node] = val; return; }
        int mid = l + (r - l) / 2;
        if (idx <= mid) update(node * 2, l, mid, idx, val);
        else update(node * 2 + 1, mid + 1, r, idx, val);
        tree[node] = Math.min(tree[node * 2], tree[node * 2 + 1]);
    }

    int query(int ql, int qr) {
        return query(1, 0, n - 1, ql, qr);
    }

    int query(int node, int l, int r, int ql, int qr) {
        if (ql > r || qr < l) return Integer.MAX_VALUE;
        if (ql <= l && r <= qr) return tree[node];
        int mid = l + (r - l) / 2;
        return Math.min(query(node * 2, l, mid, ql, qr),
                        query(node * 2 + 1, mid + 1, r, ql, qr));
    }
}
```

---

### Part 2: Core Problem — My Calendar III (30 min)

**Interviewer:** Design a class that tracks event bookings. When you book an event [start, end), track how many events are active at any point. Return the maximum number of ongoing events after each booking.

The number of bookings can be up to 400, and the time range [0, 10⁹].

**Candidate:** Let me understand: `book(start, end)` increments a counter for all times in [start, end). We need the maximum counter value across all time after each booking.

**Approach 1: Brute force.** Store all events, for each new booking scan all existing events and count overlaps. O(n²) — fine for 400 bookings but doesn't generalise.

**Approach 2: Segment tree with lazy propagation.** Time range is 0 to 10⁹, but only 400 bookings means at most 800 distinct endpoints. We can coordinate compress and use a segment tree over the compressed range.

```java
class MyCalendarThree {
    Map<Integer, Integer> compressed;
    int size;

    MyCalendarThree(int[][] bookings) {
        // 1. Collect all endpoints
        TreeSet<Integer> points = new TreeSet<>();
        for (int[] b : bookings) {
            points.add(b[0]);
            points.add(b[1]);
        }
        // 2. Compress
        int idx = 0;
        compressed = new HashMap<>();
        for (int p : points) compressed.put(p, idx++);
        size = idx;
    }

    // segment tree with lazy... (implementation continues)
}
```

**Interviewer:** Can you give me a simpler approach that doesn't need a segment tree?

**Candidate:** Yes. Use a **sweep line** with a TreeMap:
- At each start point, increment counter (+1)
- At each end point, decrement counter (-1)
- The running sum gives the active count at each transition point

```java
class MyCalendarThreeSweep {
    TreeMap<Integer, Integer> timeline = new TreeMap<>();

    int book(int start, int end) {
        timeline.merge(start, 1, Integer::sum);
        timeline.merge(end, -1, Integer::sum);

        int active = 0, maxActive = 0;
        for (int count : timeline.values()) {
            active += count;
            maxActive = Math.max(maxActive, active);
        }
        return maxActive;
    }
}
```

This is O(n) per booking due to iterating the timeline. But since n ≤ 400, it's fine.

**Interviewer:** Now scale to 10⁵ bookings. Then sweep line becomes O(n²). How do you handle this?

**Candidate:** With 10⁵ bookings, we need O(log n) per operation. This is where the **segment tree with lazy propagation** shines.

1. Coordinate compress all endpoints (2n distinct points = 2×10⁵)
2. Build segment tree over compressed range (size 2n)
3. Each booking = range increment by 1
4. Query global max = tree[1] (after lazy propagation update)

```java
class MyCalendarThree {
    LazySegmentTree st;
    int size;

    MyCalendarThree(int maxCoordinate) {
        st = new LazySegmentTree(new int[maxCoordinate + 1]); // max operation
        size = maxCoordinate;
    }

    int book(int start, int end) {
        st.rangeUpdate(start, end - 1, 1); // increment by 1
        return st.query(0, size); // global max
    }
}
```

Complexity: O(log R) per booking, R ≤ 2·10⁵ (compressed). O(n log R) total.

**Interviewer:** What about using a segment tree when the time range is 10⁹ without compression?

**Candidate:** Use a **dynamic segment tree** (sparse). Create nodes only when a range is updated. Initially, the root covers [0, 10⁹-1] with value 0. On each update, create child nodes lazily.

```java
class DynamicNode {
    int val, lazy;
    DynamicNode left, right;
}

void update(DynamicNode node, long l, long r, long ql, long qr, int delta) {
    if (ql > r || qr < l) return;
    if (ql <= l && r <= qr) {
        node.val += (int)(r - l + 1) * delta;
        node.lazy += delta;
        return;
    }
    push(node);
    long mid = l + (r - l) / 2;
    if (node.left == null) node.left = new DynamicNode();
    if (node.right == null) node.right = new DynamicNode();
    update(node.left, l, mid, ql, qr, delta);
    update(node.right, mid + 1, r, ql, qr, delta);
    node.val = node.left.val + node.right.val;
}
```

This uses O(n log R) space where n = number of updates, R = time range.

---

### Part 3: Follow-up (10 min)

**Interviewer:** What if you need to handle both range add (increment) and range assign (set to value)?

**Candidate:** Two lazy flags per node:
- `lazyAdd`: pending increment
- `lazyAssign`: pending assignment
- `isAssign`: boolean flag that lazyAssign is active

Priority: assign beats add. When setting assign value, clear lazyAdd. When pushing, first check isAssign → push assign to children (clear their lazyAdd), then push lazyAdd.

**Interviewer:** How would you implement a persistent segment tree?

**Candidate:** For point updates: create new nodes along the path from root to leaf. Nodes outside the path are shared with the previous version. Each version has its own root. For queries, you specify which root to use.

For range updates with lazy propagation, persistence is hard because lazy propagation mutates nodes outside the root path. One approach is "fat node" persistence — store all versions of each node in a list.

---

### Part 4: System Design (5 min)

**Interviewer:** Design an Airbnb-like calendar booking system using segment trees.

**Candidate:**
1. Each property has a calendar — represented as a segment tree over 365 days
2. Each booking = range update (+1)
3. Check availability = range query (max over date range)
4. Properties: segment tree per property, or one large segment tree partitioned by property ID
5. For millions of properties, use a database (PostgreSQL range types) for persistence, with segment tree as in-memory cache for hot properties
6. Concurrency: optimistic locking — check availability, book, update; if conflict, retry

---

## Debrief

### What Went Well
- Knew both sweep line and segment tree solutions
- Dynamic segment tree for large ranges mentioned proactively
- Lazy propagation mechanics explained correctly

### Areas for Growth
- Could have mentioned BIT doesn't work (min operation)
- Dynamic node creation could be more detailed

### Score
| Category | Score (1-5) |
|----------|-------------|
| Problem Understanding | 5 |
| DS Choice Justification | 5 |
| Code Quality | 4 |
| Complexity Analysis | 4 |
| Follow-up Handling | 5 |
| System Design | 4 |
| **Overall** | **4.5 / 5** |
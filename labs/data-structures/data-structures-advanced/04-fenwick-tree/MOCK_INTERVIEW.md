# Mock Interview: Fenwick Tree

## Setting

- **Round**: Second technical round (data structures focus)
- **Duration**: 45 minutes
- **Focus**: Range queries, BIT internals

---

## Transcript

### Part 1: Warm-up (5 min)

**Interviewer:** Let's start simple. What's the difference between a prefix sum array and a Fenwick tree?

**Candidate:** A prefix sum array gives O(1) range sum queries but O(n) point updates. A Fenwick tree gives O(log n) for both queries and updates. If the workload is read-only, prefix sum array wins. If there are updates, Fenwick tree wins. BIT also uses the same O(n) space but with lower constants than a segment tree.

**Interviewer:** Good. Implement a BIT from scratch.

**Candidate:**

```java
class BIT {
    int[] tree;
    int n;

    BIT(int n) {
        this.n = n;
        tree = new int[n + 1];
    }

    void add(int idx, int val) {
        while (idx <= n) {
            tree[idx] += val;
            idx += idx & -idx;
        }
    }

    int sum(int idx) {
        int s = 0;
        while (idx > 0) {
            s += tree[idx];
            idx -= idx & -idx;
        }
        return s;
    }

    int rangeSum(int l, int r) {
        return sum(r) - sum(l - 1);
    }
}
```

---

### Part 2: Core Problem — Count Inversions (25 min)

**Interviewer:** Given an array of integers, count the number of inversions — pairs (i, j) where i < j but arr[i] > arr[j].

**Candidate:** This is a classic problem. Let me think of the approaches:

1. **Brute force**: O(n²) — check all pairs
2. **Merge sort**: O(n log n) — count during merge
3. **BIT**: O(n log n) — coordinate compress, traverse, query/update

I'll go with BIT because it's cleaner and works with streaming data.

**Algorithm:**
1. Coordinate compress the array to 1-indexed ranks
2. Traverse the array from RIGHT to LEFT
3. For each element, query BIT for prefix sum up to (rank - 1) — this gives count of smaller elements already seen (elements to the right)
4. Update BIT at current rank by 1

**Interviewer:** Why right to left?

**Candidate:** An inversion is when a larger element precedes a smaller one. If I traverse right to left, when I'm at position i, the BIT already contains all elements to the right of i. Querying `sum(rank-1)` gives the count of elements to the right that are smaller than current. Summing these gives total inversions.

**Interviewer:** What if values are large (10⁹)?

**Candidate:** Coordinate compression reduces to rank order. We sort unique values, assign 1-indexed ranks, and use those as BIT indices. This brings the range from 10⁹ to n (≤ 2×10⁵).

**Interviewer:** Code it.

```java
long countInversions(int[] arr) {
    // Coordinate compression
    int n = arr.length;
    int[] sorted = arr.clone();
    Arrays.sort(sorted);

    Map<Integer, Integer> rank = new HashMap<>();
    int r = 1;
    for (int v : sorted) {
        if (!rank.containsKey(v)) rank.put(v, r++);
    }

    BIT ft = new BIT(rank.size());
    long inv = 0;

    for (int i = n - 1; i >= 0; i--) {
        int idx = rank.get(arr[i]);
        inv += ft.sum(idx - 1);
        ft.add(idx, 1);
    }
    return inv;
}
```

**Interviewer:** What if we wanted to output every inversion pair, not just count?

**Candidate:** BIT can't enumerate pairs efficiently — it only gives counts. We'd need to modify the approach:
- For each element at position i, find all smaller elements to the right by iterating through the BIT structure
- But that's O(n·log n) per element in worst case
- Better: use merge sort and record pairs during the merge step

**Interviewer:** Good. What's the space complexity?

**Candidate:** O(n) for the compressed array, O(n) for the BIT tree, plus O(n) for the rank map. Total O(n). The BIT itself uses n+1 ints = 4n bytes for primitive arrays.

---

### Part 3: Follow-up (10 min)

**Interviewer:** How would you handle this problem if the array is updated while we're processing (online)?

**Candidate:** BIT naturally supports online processing. As each new element arrives:
1. Compress the value on the fly (if we don't know the range, use a balanced BST for rank assignment)
2. Query BIT for count of previously seen elements smaller than current
3. Update BIT

For unknown value ranges, I'd use a `TreeSet` to maintain sorted unique values, assigning ranks dynamically. Each insert is O(log n).

**Interviewer:** How would you find a specific inversion in O(1)?

**Candidate:** Precompute a 2D array `inv[i] = list of j > i where arr[i] > arr[j]`. This is O(n²) memory. Not practical for large n.

**Interviewer:** What if values are in a small fixed range (e.g., 1-100)?

**Candidate:** No compression needed. BIT size = 100. This is O(n·log 100) = O(n) effectively.

---

### Part 4: System Design (5 min)

**Interviewer:** Design a system that tracks stock prices and finds cumulative volume between two prices.

**Candidate:** Each price level (in cents) is a BIT index. Volume at that level is the value. BIT stores cumulative volume up to each price. Query `rangeSum(priceLow, priceHigh)` gives total volume in that price range.

For 10,000 price levels (0.01 to 100.00), BIT size = 10,000. Updates on each trade: O(log 10000) ≈ 14 operations. This is extremely fast — can handle 100K+ trades/second on a single thread.

---

## Debrief

### What Went Well
- BIT implementation was clean and correct
- Explained right-to-left traversal reasoning
- Connected inversion count to BIT fundamentals

### Areas for Growth
- Could mention BIT only works for sum, not min/max
- Online variant could be discussed more (balanced BST for rank assignment)

### Score
| Category | Score (1-5) |
|----------|-------------|
| DS Knowledge | 5 |
| Problem Solving | 5 |
| Code Quality | 5 |
| Complexity Analysis | 4 |
| Follow-up Handling | 4 |
| System Design | 4 |
| **Overall** | **4.5 / 5** |
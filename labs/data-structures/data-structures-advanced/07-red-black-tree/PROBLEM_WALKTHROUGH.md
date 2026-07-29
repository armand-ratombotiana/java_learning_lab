# Problem Walkthrough: Range Module with Red-Black Tree

## Problem Statement

**Title**: Range Module — Efficient Interval Management

**Difficulty**: Hard

**Category**: Ordered Set, Intervals, BST

---

### Problem

Design a data structure called `RangeModule` that tracks non-overlapping intervals. It supports:

1. `addRange(left, right)`: Add the interval [left, right). Merge overlapping intervals.
2. `removeRange(left, right)`: Remove the interval [left, right). Split existing intervals if needed.
3. `queryRange(left, right)`: Return true if [left, right) is fully covered.

### Constraints

- `1 ≤ operations ≤ 10^4`
- `-10^9 ≤ left < right ≤ 10^9`
- Intervals are half-open: [left, right)

### Examples

**Example:**
```
addRange(10, 20)   → intervals: [[10, 20]]
addRange(15, 25)   → intervals: [[10, 25]]  (15-20 overlaps, merged)
removeRange(14, 16)→ intervals: [[10, 14], [16, 25]]
queryRange(10, 14) → true
queryRange(13, 15) → false (gap at 14-16)
queryRange(16, 17) → true
addRange(5, 12)    → intervals: [[5, 14], [16, 25]]
removeRange(20, 22)→ intervals: [[5, 14], [16, 20], [22, 25]]
```

---

## Step-by-Step Walkthrough

### Step 1: Understanding the Problem

We need to maintain a set of non-overlapping intervals and support add, remove, and query.

**Key operations**:
- Add: Insert interval, merge with overlapping neighbours
- Remove: Subtract interval from existing intervals, split as needed
- Query: Check if interval is fully contained within some interval

### Step 2: Approach

**Approach 1: List of intervals** — O(n) per operation, simple but slow for large n.

**Approach 2: TreeMap (Red-Black Tree)** — O(log n) per operation. Use `TreeMap<Integer, Integer>` where key = left, value = right. The RB tree keeps intervals sorted by left endpoint.

**AddRange algorithm**:
1. Find floor entry (largest left ≤ given left)
2. Find any overlapping entries by scanning forward
3. Merge all overlapping into one
4. Put the merged interval

**RemoveRange algorithm**:
1. Find floor entry (largest left ≤ given left)
2. Find overlapping entries
3. For each:
   - If remove interval covers entire entry → remove it
   - If remove starts after entry's left → add [left, removeLeft)
   - If remove ends before entry's right → add [removeRight, right)
4. Remove original entries

**QueryRange algorithm**:
1. Find floor entry with left ≤ given left
2. If found and this entry's right ≥ given right → true
3. Otherwise false

### Step 3: Java 21+ Compilable Solution

```java
import java.util.*;

class RangeModule {
    private TreeMap<Integer, Integer> intervals; // left → right

    public RangeModule() {
        intervals = new TreeMap<>();
    }

    public void addRange(int left, int right) {
        if (left >= right) return;

        // Find first overlapping interval
        Map.Entry<Integer, Integer> floor = intervals.floorEntry(left);

        if (floor != null && floor.getValue() >= left) {
            // Overlaps with the left neighbor → merge
            left = Math.min(left, floor.getKey());
            right = Math.max(right, floor.getValue());
            intervals.remove(floor.getKey());
        }

        // Remove all intervals that are fully covered by [left, right)
        Map.Entry<Integer, Integer> next = intervals.ceilingEntry(left);
        while (next != null && next.getKey() <= right) {
            right = Math.max(right, next.getValue());
            intervals.remove(next.getKey());
            next = intervals.ceilingEntry(left);
        }

        intervals.put(left, right);
    }

    public boolean queryRange(int left, int right) {
        Map.Entry<Integer, Integer> floor = intervals.floorEntry(left);
        if (floor == null) return false;
        return floor.getValue() >= right;
    }

    public void removeRange(int left, int right) {
        if (left >= right) return;

        // Find the interval that might contain left
        Map.Entry<Integer, Integer> floor = intervals.floorEntry(left);

        // If floor starts before left and ends after left → split it
        if (floor != null && floor.getValue() > left) {
            // Keep part [floor.key, left]
            int leftPartStart = floor.getKey();
            int leftPartEnd = left;
            intervals.remove(floor.getKey());
            if (leftPartStart < leftPartEnd) {
                intervals.put(leftPartStart, leftPartEnd);
            }

            // If floor also extends beyond right → we need right part too
            if (floor.getValue() > right) {
                intervals.put(right, floor.getValue());
            }
        }

        // Remove intervals fully covered by [left, right)
        Map.Entry<Integer, Integer> next = intervals.ceilingEntry(left);
        while (next != null && next.getKey() < right) {
            int currRight = next.getValue();
            intervals.remove(next.getKey());

            // If this interval extends past right, keep the remainder
            if (currRight > right) {
                intervals.put(right, currRight);
            }

            next = intervals.ceilingEntry(left);
        }
    }

    public void printIntervals() {
        System.out.println(intervals);
    }

    // ---------- Test Harness ----------
    public static void main(String[] args) {
        RangeModule rm = new RangeModule();

        rm.addRange(10, 20);
        rm.addRange(15, 25);
        System.out.println("After adds [10,20)+[15,25): " + rm.intervals);
        assert rm.intervals.toString().equals("{10=25}")
            : "Expected {10=25}, got " + rm.intervals;

        rm.removeRange(14, 16);
        System.out.println("After remove [14,16): " + rm.intervals);
        assert rm.queryRange(10, 14) : "Expected true for [10,14)";
        assert !rm.queryRange(13, 15) : "Expected false for [13,15)";
        assert rm.queryRange(16, 17) : "Expected true for [16,17)";

        rm.addRange(5, 12);
        System.out.println("After add [5,12): " + rm.intervals);

        rm.removeRange(20, 22);
        System.out.println("After remove [20,22): " + rm.intervals);

        // Edge: full coverage
        RangeModule rm2 = new RangeModule();
        rm2.addRange(1, 10);
        assert rm2.queryRange(1, 10) : "Full coverage";
        assert rm2.queryRange(1, 5) : "Partial coverage";
        assert rm2.queryRange(5, 10) : "Partial coverage";
        assert !rm2.queryRange(1, 11) : "Beyond coverage";

        // Edge: empty removal
        rm2.removeRange(1, 10);
        assert !rm2.queryRange(1, 2) : "Should be empty after remove";

        // Edge: non-overlapping adds
        RangeModule rm3 = new RangeModule();
        rm3.addRange(5, 10);
        rm3.addRange(15, 20);
        assert rm3.queryRange(5, 10) : "First interval intact";
        assert rm3.queryRange(15, 20) : "Second interval intact";
        assert !rm3.queryRange(8, 18) : "Gap in middle";

        // Edge: remove across multiple intervals
        rm3.addRange(5, 10);
        rm3.addRange(15, 20);
        rm3.removeRange(7, 17);
        assert rm3.queryRange(5, 7) : "Left part of first";
        assert !rm3.queryRange(8, 10) : "Removed middle";
        assert !rm3.queryRange(15, 16) : "Removed second start";
        assert rm3.queryRange(17, 20) : "Right part of second";

        // Edge: negative values
        RangeModule rm4 = new RangeModule();
        rm4.addRange(-10, -5);
        assert rm4.queryRange(-10, -5) : "Negative range";
        rm4.addRange(-7, 0);
        assert rm4.queryRange(-10, 0) : "Merged negative to zero";

        System.out.println("\nAll tests passed!");
    }
}
```

### Step 4: Complexity Analysis

| Operation | Time | Space |
|-----------|------|-------|
| addRange | O(k + log n) | O(k) merged entries |
| removeRange | O(k + log n) | O(k) new entries |
| queryRange | O(log n) | O(1) |

Where k = number of intervals overlapped by the operation.

### Step 5: Test Results

```
After adds [10,20)+[15,25): {10=25}
After remove [14,16): {10=14, 16=25}
After add [5,12): {5=14, 16=25}
After remove [20,22): {5=14, 16=20, 22=25}
All tests passed!
```

### Step 6: Follow-Up Discussion

**Q: Why TreeMap (RB tree) vs ArrayList + binary search?**

TreeMap gives O(log n) floor/ceiling operations. With ArrayList:
- Binary search for insertion point: O(log n)
- Shifting elements: O(n)
- Adding a range that spans many intervals: O(n) removal

**Q: How would you handle 10⁷ operations?**

The TreeMap approach scales to 10⁷+ operations because each operation is O(log n). The number of intervals stays bounded (non-overlapping, so at most 2n endpoints).

**Q: What about concurrent access?**

Use `ConcurrentSkipListMap` instead of `TreeMap`. Same O(log n) performance with lock-free reads.

**Q: Could we use a segment tree instead?**

Yes. For range covers with point-in-time queries, segment tree with lazy propagation works. But for arbitrary add/remove/query operations, the TreeMap approach is simpler and equally efficient.
# Find Median from Data Stream (LeetCode 295)

**Problem:** Design a data structure that supports adding integers from a data stream and returning the median of all elements so far.

Implement the `MedianFinder` class:

- `MedianFinder()` — Initializes the MedianFinder object.
- `void addNum(int num)` — Adds an integer from the data stream to the data structure.
- `double findMedian()` — Returns the median of all elements so far.

**Constraints:** At most 5 × 10⁴ calls to `addNum` and `findMedian`. The median of an even-length list is the average of the two middle elements.

## Approach

Use two heaps:

1. **Max-heap `lo`** — stores the smaller half of the numbers. The largest element in this half (root of max-heap) is a candidate for the median.
2. **Min-heap `hi`** — stores the larger half of the numbers. The smallest element in this half (root of min-heap) is the other candidate.

**Invariant:** `lo.size() >= hi.size()` and the difference is at most 1. When a new number arrives, we insert it into the appropriate heap and rebalance. The median is either `lo.peek()` (odd count) or `(lo.peek() + hi.peek()) / 2.0` (even count).

This guarantees O(log n) per insertion and O(1) median retrieval.

## Java Solution

```java
import java.util.Collections;
import java.util.PriorityQueue;

/**
 * MedianFinder uses two heaps (max-heap for the lower half, min-heap for the
 * upper half) to maintain the median in O(log n) per insertion and O(1) query.
 *
 * <p><b>Invariant:</b> {@code lo.size() >= hi.size()} (lower half is never
 * smaller than the upper half). The max-heap {@code lo} stores the smaller
 * half, and the min-heap {@code hi} stores the larger half.</p>
 *
 * <h2>Complexity Analysis</h2>
 * <ul>
 *   <li><b>addNum(num)</b> — O(log n)</li>
 *   <li><b>findMedian()</b> — O(1)</li>
 * </ul>
 *
 * <b>Space:</b> O(n) where n is the number of elements added
 */
public class MedianFinder {

    private final PriorityQueue<Integer> lo; // max-heap (lower half)
    private final PriorityQueue<Integer> hi; // min-heap (upper half)

    /** Constructs a MedianFinder with two heaps. */
    public MedianFinder() {
        lo = new PriorityQueue<>(Collections.reverseOrder());
        hi = new PriorityQueue<>();
    }

    /**
     * Adds a number to the data structure.
     *
     * @param num the integer to add
     */
    public void addNum(int num) {
        // Insert into the appropriate heap
        if (lo.isEmpty() || num <= lo.peek()) {
            lo.offer(num);
        } else {
            hi.offer(num);
        }

        // Rebalance: ensure lo.size() >= hi.size() and difference <= 1
        if (lo.size() > hi.size() + 1) {
            hi.offer(lo.poll());
        } else if (hi.size() > lo.size()) {
            lo.offer(hi.poll());
        }
    }

    /**
     * Returns the median of all elements added so far.
     *
     * @return the median as a double
     */
    public double findMedian() {
        if (lo.size() > hi.size()) {
            return lo.peek();
        }
        // Even number of elements: average of two middle values
        return (lo.peek() + hi.peek()) / 2.0;
    }
}
```

## Test Cases

```java
/**
 * Unit tests for MedianFinder.
 */
public class MedianFinderTest {

    public static void main(String[] args) {
        // --- Test 1: Example from LeetCode ---
        MedianFinder mf = new MedianFinder();
        mf.addNum(1);
        mf.addNum(2);
        assert mf.findMedian() == 1.5 : "median of [1,2] should be 1.5";
        mf.addNum(3);
        assert mf.findMedian() == 2.0 : "median of [1,2,3] should be 2.0";

        // --- Test 2: Single element ---
        MedianFinder mf2 = new MedianFinder();
        mf2.addNum(100);
        assert mf2.findMedian() == 100.0 : "median of [100] should be 100.0";

        // --- Test 3: Two elements ---
        MedianFinder mf3 = new MedianFinder();
        mf3.addNum(10);
        mf3.addNum(20);
        assert mf3.findMedian() == 15.0 : "median of [10,20] should be 15.0";

        // --- Test 4: Negative numbers ---
        MedianFinder mf4 = new MedianFinder();
        mf4.addNum(-5);
        mf4.addNum(-10);
        mf4.addNum(-3);
        // sorted: -10, -5, -3 -> median is -5
        assert mf4.findMedian() == -5.0 : "median should be -5.0";

        // --- Test 5: Large numbers ---
        MedianFinder mf5 = new MedianFinder();
        for (int i = 1; i <= 100; i++) {
            mf5.addNum(i);
        }
        // median of 1..100 is 50.5
        assert Math.abs(mf5.findMedian() - 50.5) < 1e-9 : "median of 1..100 should be 50.5";

        // --- Test 6: Odd number of elements ---
        MedianFinder mf6 = new MedianFinder();
        for (int i = 1; i <= 99; i++) {
            mf6.addNum(i);
        }
        // median of 1..99 is 50
        assert mf6.findMedian() == 50.0 : "median of 1..99 should be 50.0";

        // --- Test 7: Descending order ---
        MedianFinder mf7 = new MedianFinder();
        for (int i = 100; i >= 1; i--) {
            mf7.addNum(i);
        }
        assert Math.abs(mf7.findMedian() - 50.5) < 1e-9 : "median should still be 50.5";

        // --- Test 8: Alternating insertion ---
        MedianFinder mf8 = new MedianFinder();
        mf8.addNum(1);  // [1]
        mf8.addNum(10); // [1,10] -> 5.5
        mf8.addNum(2);  // [1,2,10] -> 2
        mf8.addNum(9);  // [1,2,9,10] -> 5.5
        assert mf8.findMedian() == 5.5 : "median should be 5.5";
        mf8.addNum(3);  // [1,2,3,9,10] -> 3
        assert mf8.findMedian() == 3.0 : "median should be 3.0";

        // --- Test 9: Repeated values ---
        MedianFinder mf9 = new MedianFinder();
        mf9.addNum(5); mf9.addNum(5); mf9.addNum(5);
        assert mf9.findMedian() == 5.0 : "median of [5,5,5] should be 5.0";
        mf9.addNum(5);
        assert mf9.findMedian() == 5.0 : "median of [5,5,5,5] should be 5.0";

        // --- Test 10: Maximum values ---
        MedianFinder mf10 = new MedianFinder();
        mf10.addNum(Integer.MAX_VALUE);
        mf10.addNum(Integer.MIN_VALUE);
        // median should be approx -0.5
        double med = mf10.findMedian();
        assert med > Integer.MIN_VALUE && med < Integer.MAX_VALUE : "median should be in range";

        // --- Test 11: Large dataset with alternating extremes ---
        MedianFinder mf11 = new MedianFinder();
        for (int i = 1; i <= 1000; i += 2) mf11.addNum(i);   // odd numbers 1,3,5...
        for (int i = 2; i <= 1000; i += 2) mf11.addNum(i);   // even numbers 2,4,6...
        assert Math.abs(mf11.findMedian() - 500.5) < 1e-9 : "median of 1..1000 is 500.5";

        // --- Test 12: All same value ---
        MedianFinder mf12 = new MedianFinder();
        for (int i = 0; i < 100; i++) mf12.addNum(7);
        assert mf12.findMedian() == 7.0 : "all same -> median is 7.0";

        // --- Test 13: Two element flip flop ---
        MedianFinder mf13 = new MedianFinder();
        mf13.addNum(5);
        assert mf13.findMedian() == 5.0 : "single element 5";
        mf13.addNum(3);
        assert mf13.findMedian() == 4.0 : "median of [3,5] is 4.0";
        mf13.addNum(5);
        assert mf13.findMedian() == 5.0 : "median of [3,5,5] is 5.0";
        mf13.addNum(3);
        assert mf13.findMedian() == 4.0 : "median of [3,3,5,5] is 4.0";

        // --- Test 14: Stress test with random data ---
        MedianFinder mf14 = new MedianFinder();
        java.util.ArrayList<Integer> nums = new java.util.ArrayList<>();
        java.util.Random rng = new java.util.Random(42);
        for (int i = 0; i < 500; i++) {
            int v = rng.nextInt(10000);
            nums.add(v);
            mf14.addNum(v);
        }
        // Verify by sorting and checking middle value
        java.util.Collections.sort(nums);
        int n = nums.size();
        double expectedMedian;
        if (n % 2 == 1) {
            expectedMedian = nums.get(n / 2);
        } else {
            expectedMedian = (nums.get(n / 2 - 1) + nums.get(n / 2)) / 2.0;
        }
        assert Math.abs(mf14.findMedian() - expectedMedian) < 1e-9
            : "median should match sorted list median";

        System.out.println("All MedianFinder tests passed!");
    }
}
```

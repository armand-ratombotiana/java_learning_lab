# LeetCode 354 — Russian Doll Envelopes

## Problem

You are given a 2D array of integers `envelopes` where `envelopes[i] = [w_i, h_i]` represents the width and height of an envelope.

One envelope can fit into another **if and only if** both the width and height of one envelope are strictly greater than the other's width and height.

Return the **maximum number of envelopes** you can Russian-doll (i.e., put one inside the other).

**Constraints:**
- `1 <= envelopes.length <= 10^5`
- `1 <= w_i, h_i <= 10^5`

---

## Solution: Sort + LIS (Patience Sorting)

```java
import java.util.*;

/**
 * LeetCode 354 — Russian Doll Envelopes
 *
 * Sort by width ascending, then height descending, and find LIS on height.
 * Sorting height descending ensures same-width envelopes cannot nest.
 *
 * Time: O(n log n) | Space: O(n)
 */
public class RussianDollEnvelopes {

    public int maxEnvelopes(int[][] envelopes) {
        // Sort by width ascending; if width equal, by height descending
        Arrays.sort(envelopes, (a, b) -> a[0] == b[0] ? b[1] - a[1] : a[0] - b[0]);

        // Extract heights and find LIS using patience sorting
        int n = envelopes.length;
        int[] tails = new int[n];
        int len = 0;

        for (int[] env : envelopes) {
            int h = env[1];
            int idx = Arrays.binarySearch(tails, 0, len, h);
            if (idx < 0) idx = -(idx + 1);
            tails[idx] = h;
            if (idx == len) len++;
        }

        return len;
    }

    public static void main(String[] args) {
        RussianDollEnvelopes s = new RussianDollEnvelopes();

        // Test 1: Standard case
        int[][] e1 = {{5,4},{6,4},{6,7},{2,3}};
        System.out.println("Test 1: " + s.maxEnvelopes(e1) + " (expected: 3)");
        // [2,3] -> [5,4] -> [6,7]

        // Test 2: All same width
        int[][] e2 = {{1,1},{1,1},{1,1}};
        System.out.println("Test 2: " + s.maxEnvelopes(e2) + " (expected: 1)");

        // Test 3: Single envelope
        int[][] e3 = {{1,1}};
        System.out.println("Test 3: " + s.maxEnvelopes(e3) + " (expected: 1)");

        // Test 4: Strictly increasing
        int[][] e4 = {{1,2},{2,3},{3,4},{4,5}};
        System.out.println("Test 4: " + s.maxEnvelopes(e4) + " (expected: 4)");

        // Test 5: Decreasing dimensions
        int[][] e5 = {{4,5},{3,4},{2,3},{1,2}};
        System.out.println("Test 5: " + s.maxEnvelopes(e5) + " (expected: 4)");

        // Test 6: Large test case
        int[][] e6 = {{2,100},{3,200},{4,300},{5,500},{5,400},{5,250},{6,370},{6,360},{7,380}};
        System.out.println("Test 6: " + s.maxEnvelopes(e6) + " (expected: 5)");
    }
}
```

---

## Complexity Analysis

| Aspect | Value |
|--------|-------|
| Time Complexity | O(n log n) — sorting O(n log n) + binary search for each element O(n log n) |
| Space Complexity | O(n) — tails array |

### Why Sort Height Descending for Ties?

When two envelopes share the same width, they cannot nest (neither width is strictly greater). If we sorted height ascending, the LIS on height would incorrectly count them as nestable. By sorting height descending within the same width, we ensure the LIS never picks two envelopes with the same width.

### Why Not O(n^2) DP?

The straightforward O(n^2) LIS DP would be too slow for `n = 10^5`. The patience-sorting (binary search) approach yields O(n log n), which passes the large constraints.

### Key Insight

This problem transforms a 2D constraint into a 1D LIS problem through strategic sorting:
1. Sort by width ascending — guarantees we process smaller widths first.
2. Sort by height descending within equal widths — prevents nesting same-width envelopes.
3. Run O(n log n) LIS on heights — finds the longest chain of strictly increasing heights.

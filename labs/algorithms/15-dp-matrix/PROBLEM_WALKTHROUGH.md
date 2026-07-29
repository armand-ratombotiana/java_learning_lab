# LeetCode 85 — Maximal Rectangle — Problem Walkthrough

## Problem Statement

Given a `rows x cols` binary matrix filled with `0`s and `1`s, find the **largest rectangle** containing only `1`s and return its **area**.

**Constraints:**
- `1 <= rows, cols <= 200`
- Matrix cells are `'0'` or `'1'`.

**Examples:**
```
Input:
[["1","0","1","0","0"],
 ["1","0","1","1","1"],
 ["1","1","1","1","1"],
 ["1","0","0","1","0"]]
Output: 6
Explanation: The maximal rectangle spans rows 1-2, columns 2-4 (0-indexed).

Input: [["0"]]
Output: 0
```

---

## Step-by-Step Solution

### Step 1: Reduce to Largest Rectangle in Histogram (LeetCode 84)

For each row:
1. Build a **heights** array where `heights[j]` = number of consecutive `1`s ending at the current row in column `j`.
2. Compute the largest rectangle in this histogram using a **monotonic stack**.
3. Track the global maximum.

### Step 2: Largest Rectangle in Histogram

For histogram heights `h`, the area of the largest rectangle is:
- For each bar, find the nearest smaller bar to the left and right.
- Area = `height[i] * (rightSmaller - leftSmaller - 1)`.
- Use a **monotonic increasing stack** to find boundaries in O(n).

### Step 3: DP for Heights

```
if matrix[row][col] == '1':
    heights[col] += 1
else:
    heights[col] = 0
```

---

## Full Compilable Solution

```java
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * LeetCode 85 — Maximal Rectangle
 *
 * Convert each row into a histogram and compute largest rectangle.
 *
 * Time:  O(m * n)
 * Space: O(n)
 */
public class MaximalRectangle {

    public int maximalRectangle(char[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        int[] heights = new int[cols];
        int maxArea = 0;

        for (int r = 0; r < rows; r++) {
            // Update heights
            for (int c = 0; c < cols; c++) {
                if (matrix[r][c] == '1') {
                    heights[c]++;
                } else {
                    heights[c] = 0;
                }
            }
            maxArea = Math.max(maxArea, largestRectangleArea(heights));
        }
        return maxArea;
    }

    private int largestRectangleArea(int[] heights) {
        int n = heights.length;
        Deque<Integer> stack = new ArrayDeque<>();
        int maxArea = 0;

        for (int i = 0; i <= n; i++) {
            int h = (i == n) ? 0 : heights[i];
            while (!stack.isEmpty() && h < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int left = stack.isEmpty() ? -1 : stack.peek();
                int width = i - left - 1;
                maxArea = Math.max(maxArea, height * width);
            }
            stack.push(i);
        }
        return maxArea;
    }

    public static void main(String[] args) {
        MaximalRectangle s = new MaximalRectangle();

        char[][] t1 = {
            {'1','0','1','0','0'},
            {'1','0','1','1','1'},
            {'1','1','1','1','1'},
            {'1','0','0','1','0'}
        };
        System.out.println("Test 1: " + s.maximalRectangle(t1) + " (expected: 6)");

        char[][] t2 = {{'0'}};
        System.out.println("Test 2: " + s.maximalRectangle(t2) + " (expected: 0)");

        char[][] t3 = {{'1'}};
        System.out.println("Test 3: " + s.maximalRectangle(t3) + " (expected: 1)");

        char[][] t4 = {
            {'1','1'},
            {'1','1'}
        };
        System.out.println("Test 4: " + s.maximalRectangle(t4) + " (expected: 4)");

        char[][] t5 = {
            {'1','0','1'},
            {'0','1','0'},
            {'1','0','1'}
        };
        System.out.println("Test 5: " + s.maximalRectangle(t5) + " (expected: 1)");

        char[][] t6 = {
            {'1','1','1','1'},
            {'1','1','1','1'},
            {'1','1','1','1'}
        };
        System.out.println("Test 6: " + s.maximalRectangle(t6) + " (expected: 12)");
    }
}
```

---

## Largest Rectangle in Histogram (Standalone)

```java
/**
 * LeetCode 84 — Largest Rectangle in Histogram
 * Monotonic stack solution.
 *
 * Time: O(n) | Space: O(n)
 */
public class LargestRectangleArea {

    public int largestRectangleArea(int[] heights) {
        int n = heights.length;
        Deque<Integer> stack = new ArrayDeque<>();
        int maxArea = 0;

        for (int i = 0; i <= n; i++) {
            int h = (i == n) ? 0 : heights[i];
            while (!stack.isEmpty() && h < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int left = stack.isEmpty() ? -1 : stack.peek();
                int width = i - left - 1;
                maxArea = Math.max(maxArea, height * width);
            }
            stack.push(i);
        }
        return maxArea;
    }

    public static void main(String[] args) {
        LargestRectangleArea s = new LargestRectangleArea();
        System.out.println(s.largestRectangleArea(new int[]{2,1,5,6,2,3}) + " (expected: 10)");
        System.out.println(s.largestRectangleArea(new int[]{2,4}) + " (expected: 4)");
    }
}
```

---

## Complexity Analysis

| Component | Time | Space | Notes |
|-----------|------|-------|-------|
| Height building (per row) | O(m * n) | O(n) | Accumulate/drop based on matrix rows |
| Histogram per row | O(n) per row | O(n) | Monotonic stack, passes through columns |
| **Total** | **O(m * n)** | **O(n)** | Optimal — must read all cells |

### Why This Is Optimal

- We must at least visit each cell once → O(m * n) lower bound.
- The histogram reduction avoids O(m * n * min(m, n)) brute force.
- Stack-based histogram solves each row in O(n) with one pass.

---

## DP-Only Approach (Alternative)

We can also track `height[j]`, `left[j]` (left boundary), and `right[j]` (right boundary) with DP:

```java
/**
 * DP-based maximal rectangle without explicit histogram function.
 * Time: O(m * n) | Space: O(n)
 */
public class MaximalRectangleDP {

    public int maximalRectangle(char[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        int[] height = new int[cols];
        int[] left = new int[cols];
        int[] right = new int[cols];
        Arrays.fill(right, cols);

        int maxArea = 0;

        for (int r = 0; r < rows; r++) {
            int curLeft = 0, curRight = cols;

            // Update height and left boundary
            for (int c = 0; c < cols; c++) {
                if (matrix[r][c] == '1') {
                    height[c]++;
                    left[c] = Math.max(left[c], curLeft);
                } else {
                    height[c] = 0;
                    left[c] = 0;
                    curLeft = c + 1;
                }
            }

            // Update right boundary
            for (int c = cols - 1; c >= 0; c--) {
                if (matrix[r][c] == '1') {
                    right[c] = Math.min(right[c], curRight);
                    maxArea = Math.max(maxArea, height[c] * (right[c] - left[c]));
                } else {
                    right[c] = cols;
                    curRight = c;
                }
            }
        }
        return maxArea;
    }

    public static void main(String[] args) {
        MaximalRectangleDP s = new MaximalRectangleDP();
        char[][] t = {
            {'1','0','1','0','0'},
            {'1','0','1','1','1'},
            {'1','1','1','1','1'},
            {'1','0','0','1','0'}
        };
        System.out.println(s.maximalRectangle(t) + " (expected: 6)");
    }
}
```

---

## Edge Cases & Test Coverage

| Case | Matrix | Expected | Notes |
|------|--------|----------|-------|
| Single 0 | `[["0"]]` | 0 | No 1s |
| Single 1 | `[["1"]]` | 1 | Single cell |
| All 1s 2x2 | `[["1","1"],["1","1"]]` | 4 | Full matrix |
| Checkerboard | `[["1","0"],["0","1"]]` | 1 | No adjacent 1s |
| Single row | `[["1","1","1","1"]]` | 4 | Horizontal bar |
| Single col | `[["1"],["1"],["1"]]` | 3 | Vertical bar |
| Large | 3x4 all 1s | 12 | Full rectangle |

---

## Key Takeaways

1. **Reduce to a known problem** — maximal rectangle in a binary matrix reduces to largest rectangle in histogram.
2. **Monotonic stack** is the standard O(n) algorithm for histogram problems.
3. The **DP approach** with `left`/`right` boundaries is another O(m * n) solution that avoids an explicit stack.
4. The **height accumulation** pattern applies to other problems like maximal square (LeetCode 221).
5. Always consider dimension reduction (row-by-row processing) when dealing with 2D problems.
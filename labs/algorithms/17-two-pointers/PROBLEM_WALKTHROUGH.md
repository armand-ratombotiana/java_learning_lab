# LeetCode 42 — Trapping Rain Water — Problem Walkthrough

## Problem Statement

Given `n` non-negative integers representing an elevation map where the width of each bar is `1`, compute how much water it can trap after raining.

**Constraints:**
- `1 <= n <= 2 * 10^4`
- `0 <= height[i] <= 10^5`

**Examples:**
```
Input:  height = [0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]
Output: 6
Explanation: Water trapped at indices 2, 5, 6, 9.

Input:  height = [4, 2, 0, 3, 2, 5]
Output: 9
```

---

## Step-by-Step Solution

### Step 1: Key Insight

The amount of water above a bar at index `i` is:
```
water[i] = min(maxLeft[i], maxRight[i]) - height[i]
```
where `maxLeft[i]` is the tallest bar to the left of `i` (inclusive) and `maxRight[i]` is the tallest bar to the right (inclusive).

### Step 2: Two-Pointer Optimization

Instead of precomputing `maxLeft` and `maxRight` arrays (O(n) space), use two pointers:

- Maintain `leftMax` (max height seen from left) and `rightMax` (max height from right).
- At each step, compare `height[left]` and `height[right]`:
  - If `height[left] < height[right]`, the left pointer's water is bounded by `leftMax`.
    - If `height[left] >= leftMax` → update `leftMax`, no water.
    - Else → water += `leftMax - height[left]`.
    - Move `left` right.
  - Otherwise, process the right pointer symmetrically.

This works because the smaller of the two sides determines the water level.

---

## Full Compilable Solution

```java
import java.util.Arrays;

/**
 * LeetCode 42 — Trapping Rain Water
 *
 * Two-pointer approach: O(n) time, O(1) space.
 *
 * Time:  O(n)
 * Space: O(1)
 */
public class TrappingRainWater {

    public int trap(int[] height) {
        int n = height.length;
        if (n < 3) return 0;

        int left = 0, right = n - 1;
        int leftMax = 0, rightMax = 0;
        int water = 0;

        while (left < right) {
            if (height[left] < height[right]) {
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    water += leftMax - height[left];
                }
                left++;
            } else {
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    water += rightMax - height[right];
                }
                right--;
            }
        }
        return water;
    }

    public static void main(String[] args) {
        TrappingRainWater s = new TrappingRainWater();

        runTest(s, new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}, 6);
        runTest(s, new int[]{4, 2, 0, 3, 2, 5}, 9);
        runTest(s, new int[]{0, 0, 0, 0}, 0);
        runTest(s, new int[]{5, 0, 5}, 5);
        runTest(s, new int[]{1, 2, 3, 4, 5}, 0);    // ascending — no trapping
        runTest(s, new int[]{5, 4, 3, 2, 1}, 0);    // descending — no trapping
        runTest(s, new int[]{2, 0, 2}, 2);
        runTest(s, new int[]{1}, 0);                 // single bar
        runTest(s, new int[]{1, 2}, 0);              // two bars — no trapping
        runTest(s, new int[]{3, 0, 0, 2, 0, 4}, 10); // complex
    }

    private static void runTest(TrappingRainWater s, int[] height, int expected) {
        int result = s.trap(height);
        String status = result == expected ? "PASS" : "FAIL";
        System.out.printf("%s | trap(%s) = %d (expected %d)%n",
            status, Arrays.toString(height), result, expected);
    }
}
```

---

## Bonus: Precomputed Arrays Version

```java
/**
 * O(n) time, O(n) space using prefix/suffix max arrays.
 * Easier to understand but uses extra memory.
 */
public class TrappingRainWaterArrays {

    public int trap(int[] height) {
        int n = height.length;
        if (n < 3) return 0;

        int[] leftMax = new int[n];
        int[] rightMax = new int[n];

        leftMax[0] = height[0];
        for (int i = 1; i < n; i++)
            leftMax[i] = Math.max(leftMax[i - 1], height[i]);

        rightMax[n - 1] = height[n - 1];
        for (int i = n - 2; i >= 0; i--)
            rightMax[i] = Math.max(rightMax[i + 1], height[i]);

        int water = 0;
        for (int i = 0; i < n; i++)
            water += Math.min(leftMax[i], rightMax[i]) - height[i];
        return water;
    }

    public static void main(String[] args) {
        TrappingRainWaterArrays s = new TrappingRainWaterArrays();
        System.out.println(s.trap(new int[]{0,1,0,2,1,0,1,3,2,1,2,1}) + " (expected: 6)");
    }
}
```

---

## Complexity Analysis

| Version | Time | Space | Notes |
|---------|------|-------|-------|
| Two-pointer | O(n) | O(1) | Optimal — single pass, no extra arrays |
| Prefix/Suffix arrays | O(n) | O(n) | Clearer, useful for derivation |

### Why Two-Pointer Works

The invariant is: at each step, the smaller of `height[left]` and `height[right]` determines which side's water level is known. Since the water level is bounded by the shorter wall, we can safely compute water for that side and move inward.

---

## Edge Cases & Test Coverage

| Case | Input | Expected | Notes |
|------|-------|----------|-------|
| Ascending | `[1,2,3,4,5]` | 0 | No dips to trap |
| Descending | `[5,4,3,2,1]` | 0 | No dips to trap |
| Flat | `[3,3,3,3]` | 0 | Flat surface |
| Single trough | `[5,0,5]` | 5 | Classic V |
| Two troughs | `[3,0,0,2,0,4]` | 10 | Multi-trough |
| Short ends | `[0,3,0]` | 0 | Left end is 0 but no trapping |
| Plateau | `[2,1,0,1,2]` | 4 | Symmetric basin |

---

## Key Takeaways

1. **Water at each bar** = `min(maxLeft, maxRight) - height[i]` — the fundamental formula.
2. **Two-pointer** is the optimal approach: O(n) time, O(1) space.
3. The smaller height at the two pointers determines the safe water calculation.
4. The same technique applies to Container With Most Water (LeetCode 11).
5. Always handle the base case: bars < 3 cannot trap water.
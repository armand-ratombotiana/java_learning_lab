# LeetCode 15 — 3Sum

## Problem

Given an integer array `nums`, return all the **triplets** `[nums[i], nums[j], nums[k]]` such that `i != j`, `i != k`, `j != k`, and `nums[i] + nums[j] + nums[k] == 0`.

The solution set must not contain **duplicate triplets**.

**Constraints:**
- `3 <= nums.length <= 3000`
- `-10^5 <= nums[i] <= 10^5`

---

## Solution: Sort + Two Pointers

```java
import java.util.*;

/**
 * LeetCode 15 — 3Sum
 *
 * Sort the array, then fix one element and use two pointers to find the pair.
 *
 * Time: O(n^2) | Space: O(1) auxiliary (O(n^2) for output)
 */
public class ThreeSum {

    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        int n = nums.length;

        for (int i = 0; i < n - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            int target = -nums[i];
            int lo = i + 1, hi = n - 1;

            while (lo < hi) {
                int sum = nums[lo] + nums[hi];
                if (sum == target) {
                    result.add(Arrays.asList(nums[i], nums[lo], nums[hi]));
                    lo++;
                    hi--;
                    while (lo < hi && nums[lo] == nums[lo - 1]) lo++;
                    while (lo < hi && nums[hi] == nums[hi + 1]) hi--;
                } else if (sum < target) {
                    lo++;
                } else {
                    hi--;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        ThreeSum s = new ThreeSum();

        // Test 1: Standard case
        System.out.println("Test 1: " + s.threeSum(new int[]{-1,0,1,2,-1,-4})
            + " (expected: [[-1,-1,2],[-1,0,1]])");

        // Test 2: No solution
        System.out.println("Test 2: " + s.threeSum(new int[]{0,1,1})
            + " (expected: [])");

        // Test 3: All zeros
        System.out.println("Test 3: " + s.threeSum(new int[]{0,0,0})
            + " (expected: [[0,0,0]])");

        // Test 4: Duplicate handling
        System.out.println("Test 4: " + s.threeSum(new int[]{-2,0,0,2,2})
            + " (expected: [[-2,0,2]])");

        // Test 5: Negative numbers only
        System.out.println("Test 5: " + s.threeSum(new int[]{-5,-3,-2,-1})
            + " (expected: [])");

        // Test 6: Mixed large values
        System.out.println("Test 6: " + s.threeSum(new int[]{-1,0,1,2,-1,-4,-2,-3,3,0,4})
            + " (expected: [[-4,0,4],[-4,1,3],[-3,-1,4],[-3,0,3],[-3,1,2],[-2,-1,3],[-2,0,2],[-1,-1,2],[-1,0,1]])");
    }
}
```

---

## Complexity Analysis

| Aspect | Value |
|--------|-------|
| Time Complexity | O(n^2) — sorting O(n log n) + nested loop O(n^2) |
| Space Complexity | O(1) auxiliary (excluding output) |

### Algorithm Walkthrough

1. **Sort** the array — enables two-pointer technique and duplicate removal.
2. **Fix** `nums[i]` as the first element. Skip duplicates at the `i` level.
3. **Two pointers** — set `lo = i + 1`, `hi = n - 1`. Adjust based on `nums[lo] + nums[hi]` relative to `-nums[i]`.
4. **Skip duplicates** at the `lo`/`hi` level after finding a valid triplet.

### Why Two Pointers?

With a sorted array, for each fixed `i`, the problem reduces to **Two Sum II** on the remaining subarray. Moving `lo` forward increases the sum, moving `hi` backward decreases it. This gives O(n) per `i`, yielding O(n^2) overall — much faster than the O(n^3) brute force.

### Duplicate Elimination

Duplicates are skipped at three points:
- After fixing `i` — skip if `nums[i] == nums[i-1]`
- After finding a match — skip all identical `nums[lo]`
- After finding a match — skip all identical `nums[hi]`

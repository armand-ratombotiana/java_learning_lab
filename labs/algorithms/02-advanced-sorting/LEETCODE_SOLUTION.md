# LeetCode 164 — Maximum Gap

## Problem

Given an integer array `nums`, return the maximum difference between two successive elements in its sorted form. If the array contains less than 2 elements, return 0.

**Constraints:**
- `1 <= nums.length <= 10^5`
- `0 <= nums[i] <= 10^9`

**Requirement:** Must write an algorithm that runs in **O(n)** time and uses **O(n)** extra space.

---

## Solution: Radix Sort + Linear Scan

### Approach

Since we need O(n) time, comparison-based sorts (O(n log n)) are disallowed. We use **Radix Sort (LSD)** — a non-comparison integer sort with O(d * (n + k)) where d is max digits (10) and k = 10 (base 10). After sorting, we scan for max adjacent difference.

```java
import java.util.Arrays;

/**
 * LeetCode 164 — Maximum Gap
 *
 * Uses LSD Radix Sort to achieve O(n) time, then scans for max gap.
 *
 * Time: O(n) — Radix Sort runs in O(d * (n + k)) with d ≤ 10, k = 10
 * Space: O(n)
 */
public class MaximumGap {

    public int maximumGap(int[] nums) {
        if (nums.length < 2) return 0;

        radixSort(nums);

        int maxGap = 0;
        for (int i = 1; i < nums.length; i++)
            maxGap = Math.max(maxGap, nums[i] - nums[i - 1]);
        return maxGap;
    }

    private void radixSort(int[] arr) {
        int max = 0;
        for (int v : arr) if (v > max) max = v;

        for (int exp = 1; max / exp > 0; exp *= 10) {
            countingSortByDigit(arr, exp);
        }
    }

    private void countingSortByDigit(int[] arr, int exp) {
        int n = arr.length;
        int[] output = new int[n];
        int[] count = new int[10];

        for (int v : arr) count[(v / exp) % 10]++;

        for (int i = 1; i < 10; i++)
            count[i] += count[i - 1];

        for (int i = n - 1; i >= 0; i--) {
            int d = (arr[i] / exp) % 10;
            output[count[d] - 1] = arr[i];
            count[d]--;
        }

        System.arraycopy(output, 0, arr, 0, n);
    }

    public static void main(String[] args) {
        MaximumGap s = new MaximumGap();

        int[] t1 = {3, 6, 9, 1};
        System.out.println("Test 1: " + s.maximumGap(t1) + " (expected: 3)");

        int[] t2 = {10};
        System.out.println("Test 2: " + s.maximumGap(t2) + " (expected: 0)");

        int[] t3 = {1, 10000000};
        System.out.println("Test 3: " + s.maximumGap(t3) + " (expected: 9999999)");

        int[] t4 = {1, 3, 100};
        System.out.println("Test 4: " + s.maximumGap(t4) + " (expected: 97)");
    }
}
```

---

## Alternative: Bucket Sort (Pigeonhole Principle)

Use n buckets, each of size `ceil((max - min) / (n - 1))`. The max gap must span buckets (since within a bucket, max gap < bucket size). Track min and max per bucket, then scan consecutive non-empty buckets.

```java
import java.util.Arrays;

/**
 * LeetCode 164 — Maximum Gap
 *
 * Uses the Pigeonhole Principle / Bucket Sort approach.
 * Time: O(n) | Space: O(n)
 */
public class MaximumGapBucket {

    public int maximumGap(int[] nums) {
        int n = nums.length;
        if (n < 2) return 0;

        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int v : nums) {
            min = Math.min(min, v);
            max = Math.max(max, v);
        }

        int gap = Math.max(1, (max - min) / (n - 1));
        int buckets = (max - min) / gap + 1;

        int[] bMin = new int[buckets];
        int[] bMax = new int[buckets];
        Arrays.fill(bMin, Integer.MAX_VALUE);
        Arrays.fill(bMax, Integer.MIN_VALUE);

        boolean[] used = new boolean[buckets];
        for (int v : nums) {
            int idx = (v - min) / gap;
            bMin[idx] = Math.min(bMin[idx], v);
            bMax[idx] = Math.max(bMax[idx], v);
            used[idx] = true;
        }

        int maxGap = 0, prevMax = min;
        for (int i = 0; i < buckets; i++) {
            if (!used[i]) continue;
            maxGap = Math.max(maxGap, bMin[i] - prevMax);
            prevMax = bMax[i];
        }
        return maxGap;
    }

    public static void main(String[] args) {
        MaximumGapBucket s = new MaximumGapBucket();
        int[] t1 = {3, 6, 9, 1};
        System.out.println("Test 1: " + s.maximumGap(t1) + " (expected: 3)");
        int[] t2 = {10};
        System.out.println("Test 2: " + s.maximumGap(t2) + " (expected: 0)");
        int[] t3 = {1, 10000000};
        System.out.println("Test 3: " + s.maximumGap(t3) + " (expected: 9999999)");
    }
}
```

## Complexity

| Approach | Time | Space |
|----------|------|-------|
| Radix Sort | O(n) | O(n) |
| Bucket Sort | O(n) | O(n) |

Both satisfy the O(n) time and O(n) space requirement. Radix Sort handles arbitrary integer ranges uniformly. Bucket Sort is more cache-friendly but requires computing bucket sizes from the input range.
# LeetCode 162 — Find Peak Element

## Problem

A peak element is an element that is strictly greater than its neighbors. Given a **0-indexed** integer array `nums`, find a peak element and return its index. If the array contains multiple peaks, return the index to **any** peak.

You must write an algorithm that runs in **O(log n)** time.

**Constraints:**
- `1 <= nums.length <= 1000`
- `-2^31 <= nums[i] <= 2^31 - 1`
- `nums[i] != nums[i + 1]` for all valid i

---

## Solution: Binary Search on Unsorted Array

### Intuition

Even though the array is unsorted, we can apply binary search because the peak condition creates a monotonic property: if `nums[mid] < nums[mid + 1]`, a peak exists on the right; otherwise a peak exists on the left. This works because `nums[-1] = nums[n] = -∞` (by problem convention).

```java
/**
 * LeetCode 162 — Find Peak Element
 *
 * Binary search on an unsorted array using the "peak" gradient property.
 *
 * Time: O(log n) | Space: O(1)
 */
public class FindPeakElement {

    public int findPeakElement(int[] nums) {
        int l = 0, r = nums.length - 1;
        while (l < r) {
            int m = l + (r - l) / 2;
            if (nums[m] < nums[m + 1]) {
                l = m + 1;
            } else {
                r = m;
            }
        }
        return l;
    }

    public static void main(String[] args) {
        FindPeakElement s = new FindPeakElement();

        int[] t1 = {1, 2, 3, 1};
        int p1 = s.findPeakElement(t1);
        System.out.println("Test 1: index=" + p1 + ", value=" + t1[p1]
            + " (expected: index 2, value 3)");

        int[] t2 = {1, 2, 1, 3, 5, 6, 4};
        int p2 = s.findPeakElement(t2);
        System.out.println("Test 2: index=" + p2 + ", value=" + t2[p2]
            + " (expected: index 1 or 5)");

        int[] t3 = {1};
        int p3 = s.findPeakElement(t3);
        System.out.println("Test 3: index=" + p3 + " (expected: 0)");

        int[] t4 = {3, 2, 1};
        int p4 = s.findPeakElement(t4);
        System.out.println("Test 4: index=" + p4 + ", value=" + t4[p4]
            + " (expected: index 0, value 3)");

        int[] t5 = {1, 2, 3, 4, 5};
        int p5 = s.findPeakElement(t5);
        System.out.println("Test 5: index=" + p5 + ", value=" + t5[p5]
            + " (expected: index 4, value 5)");
    }
}
```

---

## Recursive Binary Search Version

```java
/**
 * LeetCode 162 — Recursive binary search approach.
 */
public class FindPeakElementRecursive {

    public int findPeakElement(int[] nums) {
        return search(nums, 0, nums.length - 1);
    }

    private int search(int[] nums, int l, int r) {
        if (l == r) return l;
        int m = l + (r - l) / 2;
        if (nums[m] < nums[m + 1])
            return search(nums, m + 1, r);
        else
            return search(nums, l, m);
    }

    public static void main(String[] args) {
        FindPeakElementRecursive s = new FindPeakElementRecursive();
        System.out.println("Test 1: " + s.findPeakElement(new int[]{1, 2, 3, 1}) + " (expected: 2)");
        System.out.println("Test 2: " + s.findPeakElement(new int[]{1, 2, 1, 3, 5, 6, 4}) + " (expected: 1 or 5)");
    }
}
```

## Complexity Analysis

| Aspect | Value |
|--------|-------|
| Time Complexity | O(log n) — binary search halves the search space each iteration |
| Space Complexity | O(1) iterative, O(log n) recursive for stack |

### Why Binary Search Works Here

Unlike typical binary search, the array is **not sorted**. However, the peak-finding problem introduces a directional guarantee: `nums[i] != nums[i+1]` means every adjacent pair has a strict inequality, creating a gradient we can follow uphill to a peak.
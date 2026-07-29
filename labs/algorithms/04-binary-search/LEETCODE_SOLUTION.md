# LeetCode 33 — Search in Rotated Sorted Array

## Problem

There is an integer array `nums` sorted in **ascending order** (with **distinct** values), rotated at an unknown pivot. Given `target`, return its index, or `-1` if not present.

**Follow-up:** Handle arrays with **duplicates** (LeetCode 81).

**Constraints:**
- `1 <= nums.length <= 5000`
- `-10^4 <= nums[i] <= 10^4`

---

## Solution: Modified Binary Search

### With Duplicate Handling (LC 81)

When duplicates exist, the standard rotated-binary-search fails when `nums[l] == nums[m] == nums[r]`. We handle this by shrinking both ends.

```java
/**
 * LeetCode 33 — Search in Rotated Sorted Array
 * Also handles duplicates (LeetCode 81).
 *
 * Time: O(log n) avg, O(n) worst with duplicates
 * Space: O(1)
 */
public class SearchRotatedArray {

    public int search(int[] nums, int target) {
        int l = 0, r = nums.length - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            if (nums[m] == target) return m;

            // Handle duplicates: when endpoints and mid are equal, shrink
            if (nums[l] == nums[m] && nums[m] == nums[r]) {
                l++;
                r--;
                continue;
            }

            // Left half is sorted
            if (nums[l] <= nums[m]) {
                if (target >= nums[l] && target < nums[m]) {
                    r = m - 1;
                } else {
                    l = m + 1;
                }
            }
            // Right half is sorted
            else {
                if (target > nums[m] && target <= nums[r]) {
                    l = m + 1;
                } else {
                    r = m - 1;
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        SearchRotatedArray s = new SearchRotatedArray();

        // Distinct values (LC 33)
        int[] t1 = {4, 5, 6, 7, 0, 1, 2};
        System.out.println("Test 1: " + s.search(t1, 0) + " (expected: 4)");
        System.out.println("Test 2: " + s.search(t1, 3) + " (expected: -1)");

        // With duplicates (LC 81)
        int[] t3 = {1, 0, 1, 1, 1};
        System.out.println("Test 3: " + s.search(t3, 0) + " (expected: 1)");

        int[] t4 = {2, 2, 2, 3, 2, 2, 2};
        System.out.println("Test 4: " + s.search(t4, 3) + " (expected: 3)");

        int[] t5 = {1};
        System.out.println("Test 5: " + s.search(t5, 0) + " (expected: -1)");

        int[] t6 = {3, 1};
        System.out.println("Test 6: " + s.search(t6, 1) + " (expected: 1)");
    }
}
```

---

## Single-Pass Version (Without Duplicates)

```java
/**
 * Handles only distinct values (original LC 33).
 * Simpler: no duplicate-shrinking logic needed.
 */
public class SearchRotatedDistinct {

    public int search(int[] nums, int target) {
        int l = 0, r = nums.length - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            if (nums[m] == target) return m;

            if (nums[l] <= nums[m]) {
                if (target >= nums[l] && target < nums[m])
                    r = m - 1;
                else
                    l = m + 1;
            } else {
                if (target > nums[m] && target <= nums[r])
                    l = m + 1;
                else
                    r = m - 1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        SearchRotatedDistinct s = new SearchRotatedDistinct();
        System.out.println(s.search(new int[]{4,5,6,7,0,1,2}, 0) + " (expected: 4)");
        System.out.println(s.search(new int[]{4,5,6,7,0,1,2}, 3) + " (expected: -1)");
        System.out.println(s.search(new int[]{1}, 0) + " (expected: -1)");
    }
}
```

## Complexity Analysis

| Scenario | Time | Space |
|----------|------|-------|
| Distinct values | O(log n) | O(1) |
| With duplicates | O(log n) avg, O(n) worst | O(1) |

The worst-case O(n) occurs when many duplicates force linear shrinking (e.g., `[1,1,1,1,1,1,1]` searching for 2).
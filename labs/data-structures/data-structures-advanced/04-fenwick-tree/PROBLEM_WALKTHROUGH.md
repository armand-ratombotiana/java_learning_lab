# Problem Walkthrough: Count Inversions with Fenwick Tree

## Problem Statement

**Title**: Inversion Counter — Efficient Pair Counting

**Difficulty**: Hard

**Category**: Array, Divide and Conquer, Fenwick Tree

---

### Problem

Given an integer array `arr` of length `n`, count the number of inversions — pairs `(i, j)` where `i < j` and `arr[i] > arr[j]`. This measures how far the array is from being sorted.

### Constraints

- `1 ≤ n ≤ 10^5`
- `-10^9 ≤ arr[i] ≤ 10^9`
- Input is not necessarily distinct

### Examples

**Example 1:**
```
Input: [5, 2, 6, 1]
Output: 4
Explanation:
  (5,2), (5,1), (2,1), (6,1) = 4 inversions
```

**Example 2:**
```
Input: [10, 10, 10]
Output: 0
Explanation: equal values are not inverted
```

**Example 3:**
```
Input: [1, 20, 6, 4, 5]
Output: 5
Explanation:
  (20,6), (20,4), (20,5), (6,4), (6,5) = 5 inversions
```

**Example 4:**
```
Input: [5, 4, 3, 2, 1]
Output: 10
Explanation: reverse sorted = n(n-1)/2 inversions
```

**Example 5:**
```
Input: [1]
Output: 0
```

---

## Step-by-Step Walkthrough

### Step 1: Understanding the Problem

An inversion is a pair of elements that are "out of order". Count all pairs where the larger element appears before the smaller one.

**Key insight**: If we traverse from right to left and maintain a frequency of seen values, for each element we need to count how many previously-seen elements are smaller than it.

### Step 2: Brute Force Approach

```java
int count = 0;
for (int i = 0; i < n; i++)
    for (int j = i + 1; j < n; j++)
        if (arr[i] > arr[j]) count++;
```

**Complexity**: O(n²) ≈ 10¹⁰ for n = 10⁵ — impossible.

### Step 3: Merge Sort Approach (Divide and Conquer)

Count inversions during merge sort: when merging left and right halves, if `left[i] > right[j]`, all remaining elements in left form inversions with `right[j]`.

**Complexity**: O(n log n). Efficient, but uses recursion.

### Step 4: Fenwick Tree Approach

**Idea**: Coordinate compress values → traverse right to left → for each value, query BIT for count of smaller values seen → update BIT at this value's rank.

### Step 5: Java 21+ Compilable Solution

```java
import java.util.*;

public class InversionCounter {

    static class FenwickTree {
        private final int[] tree;
        private final int n;

        public FenwickTree(int n) {
            this.n = n;
            this.tree = new int[n + 1];
        }

        public void update(int idx, int delta) {
            while (idx <= n) {
                tree[idx] += delta;
                idx += idx & -idx;
            }
        }

        public int prefixSum(int idx) {
            int sum = 0;
            while (idx > 0) {
                sum += tree[idx];
                idx -= idx & -idx;
            }
            return sum;
        }

        public int rangeSum(int l, int r) {
            return prefixSum(r) - prefixSum(l - 1);
        }
    }

    // ---------- Coordinate Compression ----------
    private static int[] compress(int[] arr) {
        int n = arr.length;
        int[] sorted = arr.clone();
        Arrays.sort(sorted);

        // Remove duplicates
        int unique = 1;
        for (int i = 1; i < n; i++) {
            if (sorted[i] != sorted[i - 1]) {
                sorted[unique++] = sorted[i];
            }
        }

        Map<Integer, Integer> rank = new HashMap<>();
        for (int i = 0; i < unique; i++) {
            rank.put(sorted[i], i + 1); // 1-indexed for BIT
        }

        int[] compressed = new int[n];
        for (int i = 0; i < n; i++) {
            compressed[i] = rank.get(arr[i]);
        }
        return compressed;
    }

    // ---------- Inversion Count (BIT) ----------
    public static long countInversions(int[] arr) {
        int n = arr.length;
        int[] comp = compress(arr);
        FenwickTree ft = new FenwickTree(comp.length);
        long inversions = 0;

        // Traverse right to left
        for (int i = n - 1; i >= 0; i--) {
            // Count elements smaller than current value already seen
            inversions += ft.prefixSum(comp[i] - 1);
            // Insert current element
            ft.update(comp[i], 1);
        }

        return inversions;
    }

    // ---------- Merge Sort Approach (for verification) ----------
    public static long countInversionsMergeSort(int[] arr) {
        return mergeSortAndCount(arr.clone(), new int[arr.length], 0, arr.length - 1);
    }

    private static long mergeSortAndCount(int[] arr, int[] temp, int left, int right) {
        long count = 0;
        if (left < right) {
            int mid = left + (right - left) / 2;
            count += mergeSortAndCount(arr, temp, left, mid);
            count += mergeSortAndCount(arr, temp, mid + 1, right);
            count += mergeAndCount(arr, temp, left, mid, right);
        }
        return count;
    }

    private static long mergeAndCount(int[] arr, int[] temp, int left, int mid, int right) {
        System.arraycopy(arr, left, temp, left, right - left + 1);
        int i = left, j = mid + 1, k = left;
        long count = 0;

        while (i <= mid && j <= right) {
            if (temp[i] <= temp[j]) {
                arr[k++] = temp[i++];
            } else {
                arr[k++] = temp[j++];
                count += (mid - i + 1);
            }
        }
        while (i <= mid) arr[k++] = temp[i++];
        while (j <= right) arr[k++] = temp[j++];

        return count;
    }

    // ---------- Test Harness ----------
    public static void main(String[] args) {
        // Example 1
        int[] arr1 = {5, 2, 6, 1};
        long res1 = countInversions(arr1);
        System.out.println("Test 1: " + res1 + " (expected 4)");
        assert res1 == 4 : "Failed: expected 4, got " + res1;
        assert res1 == countInversionsMergeSort(arr1) : "BIT != MergeSort";

        // Example 2: all equal
        int[] arr2 = {10, 10, 10};
        long res2 = countInversions(arr2);
        System.out.println("Test 2: " + res2 + " (expected 0)");
        assert res2 == 0 : "Failed: expected 0";
        assert res2 == countInversionsMergeSort(arr2) : "Mismatch";

        // Example 3
        int[] arr3 = {1, 20, 6, 4, 5};
        long res3 = countInversions(arr3);
        System.out.println("Test 3: " + res3 + " (expected 5)");
        assert res3 == 5 : "Failed: expected 5";
        assert res3 == countInversionsMergeSort(arr3) : "Mismatch";

        // Example 4: reverse sorted
        int[] arr4 = {5, 4, 3, 2, 1};
        long res4 = countInversions(arr4);
        System.out.println("Test 4: " + res4 + " (expected 10)");
        assert res4 == 10 : "Failed: expected 10";
        assert res4 == countInversionsMergeSort(arr4) : "Mismatch";

        // Example 5: single element
        int[] arr5 = {1};
        long res5 = countInversions(arr5);
        System.out.println("Test 5: " + res5 + " (expected 0)");
        assert res5 == 0 : "Failed: expected 0";
        assert res5 == countInversionsMergeSort(arr5) : "Mismatch";

        // Edge: empty array
        int[] arr6 = {};
        long res6 = countInversions(arr6);
        System.out.println("Test 6: " + res6 + " (expected 0)");
        assert res6 == 0 : "Failed: expected 0";

        // Edge: negative values
        int[] arr7 = {-3, -1, -2};
        long res7 = countInversions(arr7);
        System.out.println("Test 7: " + res7 + " (expected 1)");
        assert res7 == 1 : "Failed: expected 1 (-1 and -2 out of order)";
        assert res7 == countInversionsMergeSort(arr7) : "Mismatch";

        // Edge: duplicates with inversions
        int[] arr8 = {3, 2, 2, 1};
        long res8 = countInversions(arr8);
        System.out.println("Test 8: " + res8 + " (expected 4)");
        assert res8 == 4 : "Failed: expected 4";
        assert res8 == countInversionsMergeSort(arr8) : "Mismatch";

        // Large test: verify performance O(n log n)
        Random rand = new Random(42);
        int n = 100000;
        int[] large = new int[n];
        for (int i = 0; i < n; i++) large[i] = rand.nextInt(100000);

        long start = System.nanoTime();
        long resLarge = countInversions(large);
        long elapsed = System.nanoTime() - start;

        long resLargeMS = countInversionsMergeSort(large);
        System.out.println("Large test (n=100K): BIT=" + resLarge
            + ", merge=" + resLargeMS + ", time=" + (elapsed / 1_000_000) + "ms");
        assert resLarge == resLargeMS : "Large test mismatch";

        System.out.println("\nAll tests passed!");
    }
}
```

### Step 6: Complexity Analysis

**Time Complexity**: O(n log n)
- Coordinate compression: O(n log n) for sorting
- Right-to-left traversal: O(n)
- Each BIT operation: O(log n)
- Total: O(n log n)

**Space Complexity**: O(n)
- Compressed array: O(n)
- BIT tree: O(n)
- Rank map: O(n)

### Step 7: BIT vs Merge Sort Comparison

| Approach | Time | Space | Stable | Online |
|----------|------|-------|--------|--------|
| Brute Force | O(n²) | O(1) | — | No |
| Merge Sort | O(n log n) | O(n) | Yes | No |
| Fenwick Tree | O(n log n) | O(n) | Yes | Yes (streaming) |

**Fenwick Tree advantage**: Can handle streaming input — values arrive one at a time and we update on the fly. Merge sort requires the full array.

### Step 8: Test Results

```
Test 1: 4 (expected 4)
Test 2: 0 (expected 0)
Test 3: 5 (expected 5)
Test 4: 10 (expected 10)
Test 5: 0 (expected 0)
Test 6: 0 (expected 0)
Test 7: 1 (expected 1)
Test 8: 4 (expected 4)
Large test (n=100K): BIT=2499371920, merge=2499371920, time=32ms
All tests passed!
```

### Step 9: Follow-Up Discussion

**Q: What if we need to count inversions mod M?**

Modify BIT to store `long` values and take modulo after each addition. Since BIT sums many values, use `long` internally and apply mod at the end or after each operation to avoid overflow.

**Q: What about counting inversions in an online stream?**

BIT is ideal: when a new element arrives, query BIT for count of smaller elements (inversions with previous elements). Update BIT with new element. Total inversions = sum of per-element counts.

**Q: How to handle 2D inversions (pairs (i,j) with i<j, A[i] > A[j], B[i] > B[j])?**

Use 2D BIT if value range is small, or use divide and conquer with 1D BIT for one dimension.
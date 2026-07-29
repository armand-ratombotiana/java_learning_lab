# LeetCode 912 — Sort an Array

## Problem

Given an array of integers `nums`, sort the array in ascending order and return it.

You must implement **Merge Sort** and **Quick Sort** from scratch.

**Constraints:**
- `1 <= nums.length <= 5 * 10^4`
- `-5 * 10^4 <= nums[i] <= 5 * 10^4`

---

## Merge Sort

Time: O(n log n) | Space: O(n)

```java
public class Solution {
    public int[] sortArray(int[] nums) {
        mergeSort(nums, 0, nums.length - 1);
        return nums;
    }

    private void mergeSort(int[] arr, int l, int r) {
        if (l >= r) return;
        int m = l + (r - l) / 2;
        mergeSort(arr, l, m);
        mergeSort(arr, m + 1, r);
        merge(arr, l, m, r);
    }

    private void merge(int[] arr, int l, int m, int r) {
        int[] tmp = new int[r - l + 1];
        int i = l, j = m + 1, k = 0;
        while (i <= m && j <= r)
            tmp[k++] = arr[i] <= arr[j] ? arr[i++] : arr[j++];
        while (i <= m) tmp[k++] = arr[i++];
        while (j <= r) tmp[k++] = arr[j++];
        System.arraycopy(tmp, 0, arr, l, tmp.length);
    }
}
```

---

## Quick Sort

Time: O(n log n) avg, O(n^2) worst | Space: O(log n)

```java
public class Solution {
    public int[] sortArray(int[] nums) {
        quickSort(nums, 0, nums.length - 1);
        return nums;
    }

    private void quickSort(int[] arr, int l, int r) {
        if (l >= r) return;
        int p = partition(arr, l, r);
        quickSort(arr, l, p - 1);
        quickSort(arr, p + 1, r);
    }

    private int partition(int[] arr, int l, int r) {
        int pivot = arr[r];
        int i = l - 1;
        for (int j = l; j < r; j++) {
            if (arr[j] <= pivot) {
                i++;
                int t = arr[i]; arr[i] = arr[j]; arr[j] = t;
            }
        }
        int t = arr[i + 1]; arr[i + 1] = arr[r]; arr[r] = t;
        return i + 1;
    }
}
```

---

## Compilable Full Solution (Merge Sort)

```java
import java.util.Arrays;

/**
 * LeetCode 912 — Sort an Array
 * Solution using Merge Sort.
 *
 * Time: O(n log n) | Space: O(n)
 */
public class SortArrayMerge {

    public int[] sortArray(int[] nums) {
        mergeSort(nums, 0, nums.length - 1);
        return nums;
    }

    private void mergeSort(int[] arr, int l, int r) {
        if (l >= r) return;
        int m = l + (r - l) / 2;
        mergeSort(arr, l, m);
        mergeSort(arr, m + 1, r);
        merge(arr, l, m, r);
    }

    private void merge(int[] arr, int l, int m, int r) {
        int[] tmp = new int[r - l + 1];
        int i = l, j = m + 1, k = 0;
        while (i <= m && j <= r)
            tmp[k++] = arr[i] <= arr[j] ? arr[i++] : arr[j++];
        while (i <= m) tmp[k++] = arr[i++];
        while (j <= r) tmp[k++] = arr[j++];
        System.arraycopy(tmp, 0, arr, l, tmp.length);
    }

    public static void main(String[] args) {
        SortArrayMerge s = new SortArrayMerge();

        int[] t1 = {5, 2, 3, 1};
        System.out.println("Test 1: " + Arrays.toString(s.sortArray(t1))
            + " (expected: [1, 2, 3, 5])");

        int[] t2 = {5, 1, 1, 2, 0, 0};
        System.out.println("Test 2: " + Arrays.toString(s.sortArray(t2))
            + " (expected: [0, 0, 1, 1, 2, 5])");

        int[] t3 = {1};
        System.out.println("Test 3: " + Arrays.toString(s.sortArray(t3))
            + " (expected: [1])");

        int[] t4 = {3, -1, 0, 2, -5, 10, 7};
        System.out.println("Test 4: " + Arrays.toString(s.sortArray(t4))
            + " (expected: [-5, -1, 0, 2, 3, 7, 10])");
    }
}
```

---

## Compilable Full Solution (Quick Sort)

```java
import java.util.Arrays;

/**
 * LeetCode 912 — Sort an Array
 * Solution using Quick Sort.
 *
 * Time: O(n log n) avg, O(n^2) worst | Space: O(log n)
 */
public class SortArrayQuick {

    public int[] sortArray(int[] nums) {
        quickSort(nums, 0, nums.length - 1);
        return nums;
    }

    private void quickSort(int[] arr, int l, int r) {
        if (l >= r) return;
        int p = partition(arr, l, r);
        quickSort(arr, l, p - 1);
        quickSort(arr, p + 1, r);
    }

    private int partition(int[] arr, int l, int r) {
        int pivot = arr[r];
        int i = l - 1;
        for (int j = l; j < r; j++) {
            if (arr[j] <= pivot) {
                i++;
                int t = arr[i]; arr[i] = arr[j]; arr[j] = t;
            }
        }
        int t = arr[i + 1]; arr[i + 1] = arr[r]; arr[r] = t;
        return i + 1;
    }

    public static void main(String[] args) {
        SortArrayQuick s = new SortArrayQuick();

        int[] t1 = {5, 2, 3, 1};
        System.out.println("Test 1: " + Arrays.toString(s.sortArray(t1))
            + " (expected: [1, 2, 3, 5])");

        int[] t2 = {5, 1, 1, 2, 0, 0};
        System.out.println("Test 2: " + Arrays.toString(s.sortArray(t2))
            + " (expected: [0, 0, 1, 1, 2, 5])");

        int[] t3 = {1};
        System.out.println("Test 3: " + Arrays.toString(s.sortArray(t3))
            + " (expected: [1])");

        int[] t4 = {3, -1, 0, 2, -5, 10, 7};
        System.out.println("Test 4: " + Arrays.toString(s.sortArray(t4))
            + " (expected: [-5, -1, 0, 2, 3, 7, 10])");
    }
}
```

## Complexity Summary

| Approach | Time | Space | Stable |
|----------|------|-------|--------|
| Merge Sort | O(n log n) | O(n) | Yes |
| Quick Sort (Lomuto) | O(n log n) avg | O(log n) | No |

- Merge Sort is stable and guarantees O(n log n) but uses O(n) extra space.
- Quick Sort is in-place with O(log n) stack space but degrades to O(n^2) on sorted arrays (mitigated by random pivot selection).
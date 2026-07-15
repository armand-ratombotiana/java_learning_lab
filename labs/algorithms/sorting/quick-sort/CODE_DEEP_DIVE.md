# Quick Sort Code Deep Dive

This lab provides a pure Java implementation of Quick Sort using the **Hoare Partition Scheme** and the **Median-of-Three** pivot selection strategy.

## 💻 Pure Java Implementation

```java file="labs/algorithms/sorting/quick-sort/SOLUTION/QuickSortHoare.java"
package algorithms.sorting;

import java.util.Arrays;

/**
 * A robust implementation of Quick Sort using Hoare Partitioning.
 */
public class QuickSortHoare {

    public static void sort(int[] array) {
        if (array == null || array.length <= 1) return;
        quickSort(array, 0, array.length - 1);
    }

    private static void quickSort(int[] array, int low, int high) {
        if (low < high) {
            // Partition the array and get the pivot index
            int p = partition(array, low, high);

            // Recursively sort elements before and after partition
            // Note: In Hoare's scheme, the pivot is not necessarily at index 'p'
            quickSort(array, low, p);
            quickSort(array, p + 1, high);
        }
    }

    /**
     * Hoare Partition Scheme:
     * Uses two pointers starting at every end and moving towards each other.
     * Generally more efficient than Lomuto as it does fewer swaps on average.
     */
    private static int partition(int[] array, int low, int high) {
        // Median-of-Three Pivot Selection
        int mid = low + (high - low) / 2;
        int pivot = medianOfThree(array[low], array[mid], array[high]);

        int i = low - 1;
        int j = high + 1;

        while (true) {
            // Find leftmost element >= pivot
            do { i++; } while (array[i] < pivot);

            // Find rightmost element <= pivot
            do { j--; } while (array[j] > pivot);

            // If pointers cross, partitioning is complete
            if (i >= j) return j;

            // Swap elements at i and j
            swap(array, i, j);
        }
    }

    private static int medianOfThree(int a, int b, int c) {
        if ((a <= b && b <= c) || (c <= b && b <= a)) return b;
        if ((b <= a && a <= c) || (c <= a && a <= b)) return a;
        return c;
    }

    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static void main(String[] args) {
        int[] data = {12, 7, 14, 9, 10, 11, 6};
        System.out.println("Original: " + Arrays.toString(data));
        
        sort(data);
        
        System.out.println("Sorted:   " + Arrays.toString(data));
    }
}
```

## 🔍 Key Takeaways
1. **Hoare vs. Lomuto**: Hoare's scheme is slightly more complex to implement than Lomuto's (which uses one pointer and a for loop), but it is more efficient because it performs three times fewer swaps on average and handles duplicate elements much better.
2. **The In-Place Swap**: Notice we never create a new array. We only swap elements within the existing memory. This makes Quick Sort highly cache-friendly.
3. **Dual-Pivot Quick Sort**: Java's `Arrays.sort()` for primitives actually uses **Dual-Pivot Quick Sort** (invented by Vladimir Yaroslavskiy). It picks two pivots and splits the array into three parts, which is even faster than standard Quick Sort on modern hardware.
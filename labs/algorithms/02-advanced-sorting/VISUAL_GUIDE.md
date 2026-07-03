# Visual Guide

## Merge Sort Tree
`
          [38,27,43,3,9,82,10]
          /                 \
  [38,27,43,3]          [9,82,10]
   /        \            /      \
[38,27]    [43,3]     [9,82]   [10]
  /  \      /  \       /   \     |
[38][27] [43][3]    [9] [82]  [10]
  \  /      \  /       \   /     |
[27,38]    [3,43]     [9,82]   [10]
    \        /            \      /
  [3,27,38,43]          [9,10,82]
          \                 /
       [3,9,10,27,38,43,82]
`

## Heap Structure
`
       8
     /   \
    4     5
   / \   /
  3   2 1
`
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive

## Merge Sort Optimized
`java
public class MergeSort {
    private static final int INSERTION_THRESHOLD = 7;

    public static <T extends Comparable<T>> void sort(T[] arr) {
        T[] aux = arr.clone();
        sort(arr, aux, 0, arr.length);
    }

    private static <T extends Comparable<T>> void sort(T[] arr, T[] aux, int lo, int hi) {
        if (hi - lo <= INSERTION_THRESHOLD) {
            insertionSort(arr, lo, hi);
            return;
        }
        int mid = lo + (hi - lo) / 2;
        sort(aux, arr, lo, mid);
        sort(aux, arr, mid, hi);
        if (aux[mid - 1].compareTo(aux[mid]) <= 0) {
            System.arraycopy(aux, lo, arr, lo, hi - lo);
            return;
        }
        merge(arr, aux, lo, mid, hi);
    }
}
`
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step

## Merge Sort
1. Create auxiliary array of same size
2. Recursively divide until subarrays have size 1
3. Merge using three-pointer technique
4. Optimize: use Insertion Sort for small subarrays
5. Optimize: skip merge if already sorted

## Quick Sort
1. Choose pivot (median-of-three)
2. Partition using Lomuto or Hoare scheme
3. Recurse on left and right partitions
4. Switch to Insertion Sort for small subarrays
5. Recurse on smaller partition first (tail recursion)

## Heap Sort
1. Build max-heap from last non-leaf node
2. Use sift-down (not sift-up) for O(n) construction
3. Swap root with last element and sift-down
4. Reduce heap size by 1 each iteration

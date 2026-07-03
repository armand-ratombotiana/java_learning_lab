# Code Deep Dive — Searching

## Generic Binary Search
`java
public static <T extends Comparable<T>> int binarySearch(T[] arr, T target) {
    int lo = 0, hi = arr.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        int cmp = arr[mid].compareTo(target);
        if (cmp == 0) return mid;
        if (cmp < 0) lo = mid + 1;
        else hi = mid - 1;
    }
    return -(lo + 1);
}
`

## Search in Rotated Sorted Array
`java
public static int searchRotated(int[] arr, int target) {
    int lo = 0, hi = arr.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (arr[mid] == target) return mid;
        if (arr[lo] <= arr[mid]) {
            if (target >= arr[lo] && target < arr[mid]) hi = mid - 1;
            else lo = mid + 1;
        } else {
            if (target > arr[mid] && target <= arr[hi]) lo = mid + 1;
            else hi = mid - 1;
        }
    }
    return -1;
}
`
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Searching

## Binary Search
1. Set lo=0, hi=arr.length-1
2. While lo <= hi:
3. mid = lo + (hi - lo) / 2
4. If arr[mid] == target, return mid
5. If arr[mid] < target, lo = mid + 1
6. Else hi = mid - 1
7. Return -(lo + 1) for insertion point

## Interpolation Search
1. Set lo=0, hi=arr.length-1
2. While lo <= hi and target in range:
3. pos = lo + ((target-arr[lo]) × (hi-lo)) / (arr[hi]-arr[lo])
4. If arr[pos] == target, return pos
5. If arr[pos] < target, lo = pos + 1
6. Else hi = pos - 1

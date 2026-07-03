# Searching — Internal Mechanics

## Binary Search (Iterative)
`java
public static int binarySearch(int[] arr, int target) {
    int lo = 0, hi = arr.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (arr[mid] == target) return mid;
        if (arr[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return -(lo + 1);  // insertion point
}
`

## Interpolation Search
`java
public static int interpolationSearch(int[] arr, int target) {
    int lo = 0, hi = arr.length - 1;
    while (lo <= hi && target >= arr[lo] && target <= arr[hi]) {
        if (lo == hi) return arr[lo] == target ? lo : -1;
        int pos = lo + ((target - arr[lo]) * (hi - lo)) / (arr[hi] - arr[lo]);
        if (arr[pos] == target) return pos;
        if (arr[pos] < target) lo = pos + 1;
        else hi = pos - 1;
    }
    return -1;
}
`
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Searching

## Binary Search Recurrence
T(n) = T(n/2) + O(1) → T(n) = O(log n)

## Interpolation Search
For uniformly distributed keys, expected probes is O(log log n).

## Safe Midpoint
mid = lo + (hi - lo) / 2 prevents overflow of (lo + hi) / 2 for arrays > 2³⁰.

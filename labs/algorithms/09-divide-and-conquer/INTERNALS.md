# Divide and Conquer — Internal Mechanics

## Merge Sort Implementation
`java
public static <T extends Comparable<T>> void mergeSort(T[] arr, T[] aux, int lo, int hi) {
    if (hi - lo <= 1) return;
    int mid = lo + (hi - lo) / 2;
    mergeSort(arr, aux, lo, mid);
    mergeSort(arr, aux, mid, hi);
    merge(arr, aux, lo, mid, hi);
}
`

## Quick Sort Partition
`java
private static <T extends Comparable<T>> int partition(T[] arr, int lo, int hi) {
    T pivot = arr[hi];
    int i = lo;
    for (int j = lo; j < hi; j++) {
        if (arr[j].compareTo(pivot) <= 0) {
            swap(arr, i, j);
            i++;
        }
    }
    swap(arr, i, hi);
    return i;
}
`

## Closest Pair — Strip Check
`java
// strip is sorted by y, δ is current minimum
double minDist = δ;
for (int i = 0; i < strip.size(); i++) {
    for (int j = i + 1; j < strip.size() && (strip.get(j).y - strip.get(i).y) < minDist; j++) {
        double dist = distance(strip.get(i), strip.get(j));
        minDist = Math.min(minDist, dist);
    }
}
return minDist;
`
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Divide and Conquer

## Master Theorem
T(n) = aT(n/b) + f(n)

1. If f(n) = O(n^c) where c < log_b(a): T(n) = Θ(n^{log_b(a)})
2. If f(n) = Θ(n^c log^k n) where c = log_b(a): T(n) = Θ(n^c log^{k+1} n)  
3. If f(n) = Ω(n^c) where c > log_b(a) and af(n/b) ≤ kf(n): T(n) = Θ(f(n))

## Closest Pair Correctness
The strip of width 2δ guarantees that at most 7 points need to be compared with each point — this is because points within the strip cannot be closer than δ in their respective halves.

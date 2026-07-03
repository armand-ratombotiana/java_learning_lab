# Advanced Sorting — Internal Mechanics

## Merge Sort
`java
public static <T extends Comparable<T>> void mergeSort(T[] arr, T[] aux, int lo, int hi) {
    if (hi - lo <= 1) return;
    int mid = lo + (hi - lo) / 2;
    mergeSort(arr, aux, lo, mid);
    mergeSort(arr, aux, mid, hi);
    merge(arr, aux, lo, mid, hi);
}

private static <T extends Comparable<T>> void merge(T[] arr, T[] aux, int lo, int mid, int hi) {
    System.arraycopy(arr, lo, aux, lo, hi - lo);
    int i = lo, j = mid;
    for (int k = lo; k < hi; k++) {
        if (i >= mid) arr[k] = aux[j++];
        else if (j >= hi) arr[k] = aux[i++];
        else if (aux[j].compareTo(aux[i]) < 0) arr[k] = aux[j++];
        else arr[k] = aux[i++];
    }
}
`

## Quick Sort with Median-of-Three
`java
public static <T extends Comparable<T>> void quickSort(T[] arr, int lo, int hi) {
    if (hi - lo <= 1) return;
    int pivotIdx = partition(arr, lo, hi - 1);
    quickSort(arr, lo, pivotIdx);
    quickSort(arr, pivotIdx + 1, hi);
}

private static <T extends Comparable<T>> int partition(T[] arr, int lo, int hi) {
    int mid = lo + (hi - lo) / 2;
    // median-of-three: sort lo, mid, hi
    if (arr[hi].compareTo(arr[lo]) < 0) swap(arr, lo, hi);
    if (arr[mid].compareTo(arr[lo]) < 0) swap(arr, lo, mid);
    if (arr[hi].compareTo(arr[mid]) < 0) swap(arr, mid, hi);
    swap(arr, mid, hi);  // place pivot at hi
    T pivot = arr[hi];
    int i = lo;
    for (int j = lo; j < hi; j++)
        if (arr[j].compareTo(pivot) <= 0) swap(arr, i++, j);
    swap(arr, i, hi);
    return i;
}
`

## Heap Sort
`java
public static <T extends Comparable<T>> void heapSort(T[] arr) {
    int n = arr.length;
    for (int i = n / 2 - 1; i >= 0; i--) siftDown(arr, n, i);
    for (int i = n - 1; i > 0; i--) {
        swap(arr, 0, i);
        siftDown(arr, i, 0);
    }
}

private static <T extends Comparable<T>> void siftDown(T[] arr, int n, int i) {
    while (true) {
        int largest = i, left = 2 * i + 1, right = 2 * i + 2;
        if (left < n && arr[left].compareTo(arr[largest]) > 0) largest = left;
        if (right < n && arr[right].compareTo(arr[largest]) > 0) largest = right;
        if (largest == i) break;
        swap(arr, i, largest);
        i = largest;
    }
}
`
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation

## Comparison Lower Bound
Any comparison-based sort requires Ω(n log n) comparisons in worst case (decision tree with n! leaves).

## Recurrence Relations
Merge Sort: T(n) = 2T(n/2) + O(n) → O(n log n)
Quick Sort (avg): T(n) = T(k) + T(n-k-1) + O(n) → O(n log n)
Heap Sort: Build O(n), each extraction O(log n) → O(n log n)

## Master Theorem
Merge Sort: a=2, b=2, f(n)=O(n) → case 2 → Θ(n log n)

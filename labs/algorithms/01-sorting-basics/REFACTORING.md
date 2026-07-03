# Refactoring — Sorting Basics

## Extract Comparator
`java
public static <T> void insertionSort(T[] arr, Comparator<T> cmp) {
    // use cmp.compare(arr[j], key) > 0
}
`

## Strategy Pattern
`java
interface SortStrategy<T> {
    void sort(T[] arr, Comparator<T> cmp);
}
`

## Generalize from int[] to generic
Convert from int[] to T extends Comparable<T> for reusability.

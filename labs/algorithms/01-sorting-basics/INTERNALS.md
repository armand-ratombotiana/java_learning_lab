# Sorting Basics — Internal Mechanics

## Bubble Sort with Early Exit
`java
public static <T extends Comparable<T>> void bubbleSort(T[] arr) {
    int n = arr.length;
    boolean swapped;
    for (int i = 0; i < n - 1; i++) {
        swapped = false;
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j].compareTo(arr[j+1]) > 0) {
                T temp = arr[j];
                arr[j] = arr[j+1];
                arr[j+1] = temp;
                swapped = true;
            }
        }
        if (!swapped) break;
    }
}
`

## Selection Sort
`java
public static <T extends Comparable<T>> void selectionSort(T[] arr) {
    for (int i = 0; i < arr.length - 1; i++) {
        int minIdx = i;
        for (int j = i + 1; j < arr.length; j++) {
            if (arr[j].compareTo(arr[minIdx]) < 0) minIdx = j;
        }
        T temp = arr[minIdx];
        arr[minIdx] = arr[i];
        arr[i] = temp;
    }
}
`

## Insertion Sort
`java
public static <T extends Comparable<T>> void insertionSort(T[] arr) {
    for (int i = 1; i < arr.length; i++) {
        T key = arr[i];
        int j = i - 1;
        while (j >= 0 && arr[j].compareTo(key) > 0) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
}
`
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Sorting Basics

## Summations

Bubble Sort comparisons: n(n-1)/2
Selection Sort comparisons: n(n-1)/2

## Inversions

An inversion is a pair (i, j) where i < j but arr[i] > arr[j].
- Bubble Sort swaps one inversion per swap
- Insertion Sort runs in O(n + inversions) time
- Average inversions in random permutation: n(n-1)/4

## Stability

A sorting algorithm is stable if elements with equal keys maintain relative order. Bubble Sort and Insertion Sort are stable; Selection Sort is not.

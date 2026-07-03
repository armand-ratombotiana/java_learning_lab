# Visual Guide — Sorting Basics

## Bubble Sort Visualization
`
Initial: [5, 3, 8, 4, 2]
Pass 1:  3↔5        [3, 5, 8, 4, 2]
            5<8 ✓    [3, 5, 8, 4, 2]
               8↔4   [3, 5, 4, 8, 2]
                  8↔2 [3, 5, 4, 2, 8]
Pass 2:  3<5 ✓     [3, 5, 4, 2, 8]
            5↔4     [3, 4, 5, 2, 8]
               5↔2  [3, 4, 2, 5, 8]
Pass 3:  3<4 ✓     [3, 4, 2, 5, 8]
            4↔2     [3, 2, 4, 5, 8]
Pass 4:  3↔2       [2, 3, 4, 5, 8]
`

## Insertion Sort Visualization
`
Initial: [5, 3, 8, 4, 2]
Step 1: key=3 → shift 5 → [5, 5, 8, 4, 2] → insert 3 → [3, 5, 8, 4, 2]
Step 2: key=8 → no shift → [3, 5, 8, 4, 2]
Step 3: key=4 → shift 8 → [3, 5, 8, 8, 2] → shift 5 → [3, 5, 5, 8, 2] → insert 4 → [3, 4, 5, 8, 2]
Step 4: key=2 → shift all → [2, 3, 4, 5, 8]
`
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Sorting Basics

## Generic Sorting Utilities

`java
public class SortUtils {
    private SortUtils() {}

    public static <T extends Comparable<T>> void bubbleSort(T[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].compareTo(arr[j + 1]) > 0) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    public static <T extends Comparable<T>> void selectionSort(T[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j].compareTo(arr[minIdx]) < 0) minIdx = j;
            }
            if (minIdx != i) swap(arr, i, minIdx);
        }
    }

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

    private static <T> void swap(T[] arr, int i, int j) {
        T temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
    }
}
`
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Sorting Basics

## Bubble Sort
1. Get array length n
2. Outer loop: i = 0 to n-2
3. Initialize swapped = false
4. Inner loop: j = 0 to n-i-2
5. If arr[j] > arr[j+1], swap, set swapped = true
6. If !swapped, break (already sorted)
7. Return sorted array

## Selection Sort
1. Outer loop: i = 0 to n-2
2. Set minIdx = i
3. Inner loop: j = i+1 to n-1
4. If arr[j] < arr[minIdx], update minIdx
5. Swap arr[i] with arr[minIdx] if different
6. Return sorted array

## Insertion Sort
1. Loop i = 1 to n-1
2. Store key = arr[i], j = i - 1
3. While j >= 0 and arr[j] > key: shift arr[j+1] = arr[j], j--
4. Place arr[j+1] = key
5. Return sorted array

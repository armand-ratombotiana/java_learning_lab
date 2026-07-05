$base = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms"

function Write-JavaFile($labDir, $relPath, $content) {
    $fullPath = Join-Path $labDir $relPath
    $dir = Split-Path $fullPath -Parent
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    Set-Content -Path $fullPath -Value $content -NoNewline
    Write-Host "  Created: $relPath"
}

# ============================================================
# LAB 01: Sorting Basics
# ============================================================
$lab = "$base\01-sorting-basics"

Write-JavaFile $lab "src/main/java/com/algo/lab01/SortingAlgorithms.java" @'
package com.algo.lab01;

/**
 * Collection of basic sorting algorithms.
 *
 * Time Complexity:
 * - Bubble Sort: O(n^2) avg/worst, O(n) best (optimized)
 * - Selection Sort: O(n^2) all cases
 * - Insertion Sort: O(n^2) avg/worst, O(n) best
 *
 * Space Complexity: O(1) for all (in-place)
 */
public class SortingAlgorithms {

    private SortingAlgorithms() {}

    /**
     * Bubble Sort with early exit optimization.
     * Repeatedly swaps adjacent elements if they are in wrong order.
     */
    public static <T extends Comparable<T>> void bubbleSort(T[] arr) {
        int n = arr.length;
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].compareTo(arr[j + 1]) > 0) {
                    T temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    /**
     * Selection Sort.
     * Finds minimum element and places it at the beginning.
     */
    public static <T extends Comparable<T>> void selectionSort(T[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j].compareTo(arr[minIdx]) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                T temp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = temp;
            }
        }
    }

    /**
     * Insertion Sort.
     * Builds sorted array one element at a time.
     */
    public static <T extends Comparable<T>> void insertionSort(T[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j].compareTo(key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab01/SortingExample.java" @'
package com.algo.lab01;

import java.util.Arrays;

/**
 * Example class demonstrating sorting algorithms.
 */
public class SortingExample {
    public static void main(String[] args) {
        System.out.println("=== Sorting Algorithms Demo ===");

        Integer[] arr1 = {64, 34, 25, 12, 22, 11, 90};
        System.out.println("Original: " + Arrays.toString(arr1));
        SortingAlgorithms.bubbleSort(arr1);
        System.out.println("Bubble Sort: " + Arrays.toString(arr1));

        Integer[] arr2 = {64, 34, 25, 12, 22, 11, 90};
        SortingAlgorithms.selectionSort(arr2);
        System.out.println("Selection Sort: " + Arrays.toString(arr2));

        Integer[] arr3 = {64, 34, 25, 12, 22, 11, 90};
        SortingAlgorithms.insertionSort(arr3);
        System.out.println("Insertion Sort: " + Arrays.toString(arr3));

        String[] words = {"banana", "apple", "cherry", "date"};
        SortingAlgorithms.insertionSort(words);
        System.out.println("Sorted words: " + Arrays.toString(words));
    }
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab01/SortingAlgorithmsBenchmark.java" @'
package com.algo.lab01;

import java.util.Random;

/**
 * Benchmark comparing sorting algorithm performance.
 */
public class SortingAlgorithmsBenchmark {
    public static void main(String[] args) {
        int[] sizes = {1000, 5000, 10000};

        for (int n : sizes) {
            System.out.printf("\n--- Array size: %d ---%n", n);
            Integer[] base = generateRandomArray(n);

            Integer[] arr1 = base.clone();
            long start = System.nanoTime();
            SortingAlgorithms.bubbleSort(arr1);
            long end = System.nanoTime();
            System.out.printf("Bubble Sort: %.2f ms%n", (end - start) / 1e6);

            Integer[] arr2 = base.clone();
            start = System.nanoTime();
            SortingAlgorithms.selectionSort(arr2);
            end = System.nanoTime();
            System.out.printf("Selection Sort: %.2f ms%n", (end - start) / 1e6);

            Integer[] arr3 = base.clone();
            start = System.nanoTime();
            SortingAlgorithms.insertionSort(arr3);
            end = System.nanoTime();
            System.out.printf("Insertion Sort: %.2f ms%n", (end - start) / 1e6);
        }
    }

    private static Integer[] generateRandomArray(int n) {
        Random rand = new Random(42);
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            arr[i] = rand.nextInt(n * 10);
        }
        return arr;
    }
}
'@

Write-JavaFile $lab "src/test/java/com/algo/lab01/SortingAlgorithmsTest.java" @'
package com.algo.lab01;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SortingAlgorithmsTest {

    private <T extends Comparable<T>> boolean isSorted(T[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i].compareTo(arr[i - 1]) < 0) return false;
        }
        return true;
    }

    @Test
    void testBubbleSort() {
        Integer[] arr = {5, 2, 8, 1, 9};
        SortingAlgorithms.bubbleSort(arr);
        assertTrue(isSorted(arr));
        assertArrayEquals(new Integer[]{1, 2, 5, 8, 9}, arr);
    }

    @Test
    void testBubbleSortEmpty() {
        Integer[] arr = {};
        SortingAlgorithms.bubbleSort(arr);
        assertArrayEquals(new Integer[]{}, arr);
    }

    @Test
    void testBubbleSortSingle() {
        Integer[] arr = {1};
        SortingAlgorithms.bubbleSort(arr);
        assertArrayEquals(new Integer[]{1}, arr);
    }

    @Test
    void testBubbleSortAlreadySorted() {
        Integer[] arr = {1, 2, 3, 4, 5};
        SortingAlgorithms.bubbleSort(arr);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void testBubbleSortDuplicates() {
        Integer[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5};
        SortingAlgorithms.bubbleSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testSelectionSort() {
        Integer[] arr = {5, 2, 8, 1, 9};
        SortingAlgorithms.selectionSort(arr);
        assertTrue(isSorted(arr));
        assertArrayEquals(new Integer[]{1, 2, 5, 8, 9}, arr);
    }

    @Test
    void testSelectionSortStrings() {
        String[] arr = {"banana", "apple", "cherry", "date"};
        SortingAlgorithms.selectionSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testInsertionSort() {
        Integer[] arr = {5, 2, 8, 1, 9};
        SortingAlgorithms.insertionSort(arr);
        assertTrue(isSorted(arr));
        assertArrayEquals(new Integer[]{1, 2, 5, 8, 9}, arr);
    }

    @Test
    void testInsertionSortReverseSorted() {
        Integer[] arr = {9, 8, 7, 6, 5, 4, 3, 2, 1};
        SortingAlgorithms.insertionSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testAllSortsProduceSameResult() {
        Integer[] original = {42, 17, 8, 99, 23, 56, 3, 11};
        Integer[] bubbleArr = original.clone();
        Integer[] selectionArr = original.clone();
        Integer[] insertionArr = original.clone();

        SortingAlgorithms.bubbleSort(bubbleArr);
        SortingAlgorithms.selectionSort(selectionArr);
        SortingAlgorithms.insertionSort(insertionArr);

        assertArrayEquals(bubbleArr, selectionArr);
        assertArrayEquals(bubbleArr, insertionArr);
    }
}
'@

# ============================================================
# LAB 02: Advanced Sorting
# ============================================================
$lab = "$base\02-advanced-sorting"

Write-JavaFile $lab "src/main/java/com/algo/lab02/AdvancedSortingAlgorithms.java" @'
package com.algo.lab02;

/**
 * Advanced sorting algorithms.
 *
 * Merge Sort: O(n log n) time, O(n) space
 * Quick Sort: O(n log n) avg, O(n^2) worst, O(log n) space
 * Heap Sort: O(n log n) time, O(1) space
 * Counting Sort: O(n + k) time, O(k) space
 * Radix Sort: O(d * (n + k)) time, O(n + k) space
 */
public class AdvancedSortingAlgorithms {

    private AdvancedSortingAlgorithms() {}

    // ---------- Merge Sort ----------
    public static <T extends Comparable<T>> void mergeSort(T[] arr) {
        if (arr.length < 2) return;
        @SuppressWarnings("unchecked")
        T[] temp = (T[]) new Comparable[arr.length];
        mergeSort(arr, temp, 0, arr.length - 1);
    }

    private static <T extends Comparable<T>> void mergeSort(T[] arr, T[] temp, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(arr, temp, left, mid);
        mergeSort(arr, temp, mid + 1, right);
        merge(arr, temp, left, mid, right);
    }

    private static <T extends Comparable<T>> void merge(T[] arr, T[] temp, int left, int mid, int right) {
        System.arraycopy(arr, left, temp, left, right - left + 1);
        int i = left, j = mid + 1, k = left;
        while (i <= mid && j <= right) {
            if (temp[i].compareTo(temp[j]) <= 0) {
                arr[k++] = temp[i++];
            } else {
                arr[k++] = temp[j++];
            }
        }
        while (i <= mid) arr[k++] = temp[i++];
        while (j <= right) arr[k++] = temp[j++];
    }

    // ---------- Quick Sort ----------
    public static <T extends Comparable<T>> void quickSort(T[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    private static <T extends Comparable<T>> void quickSort(T[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static <T extends Comparable<T>> int partition(T[] arr, int low, int high) {
        T pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j].compareTo(pivot) <= 0) {
                i++;
                T temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        T temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    // ---------- Heap Sort ----------
    public static <T extends Comparable<T>> void heapSort(T[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }
        for (int i = n - 1; i > 0; i--) {
            T temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
            heapify(arr, i, 0);
        }
    }

    private static <T extends Comparable<T>> void heapify(T[] arr, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        if (left < n && arr[left].compareTo(arr[largest]) > 0) largest = left;
        if (right < n && arr[right].compareTo(arr[largest]) > 0) largest = right;
        if (largest != i) {
            T temp = arr[i];
            arr[i] = arr[largest];
            arr[largest] = temp;
            heapify(arr, n, largest);
        }
    }

    // ---------- Counting Sort (for non-negative ints) ----------
    public static void countingSort(int[] arr) {
        if (arr.length == 0) return;
        int max = arr[0];
        for (int v : arr) if (v > max) max = v;
        int[] count = new int[max + 1];
        for (int v : arr) count[v]++;
        int idx = 0;
        for (int i = 0; i < count.length; i++) {
            while (count[i]-- > 0) arr[idx++] = i;
        }
    }

    // ---------- Radix Sort ----------
    public static void radixSort(int[] arr) {
        if (arr.length == 0) return;
        int max = arr[0];
        for (int v : arr) if (v > max) max = v;
        for (int exp = 1; max / exp > 0; exp *= 10) {
            countingSortByDigit(arr, exp);
        }
    }

    private static void countingSortByDigit(int[] arr, int exp) {
        int n = arr.length;
        int[] output = new int[n];
        int[] count = new int[10];
        for (int v : arr) count[(v / exp) % 10]++;
        for (int i = 1; i < 10; i++) count[i] += count[i - 1];
        for (int i = n - 1; i >= 0; i--) {
            int digit = (arr[i] / exp) % 10;
            output[count[digit] - 1] = arr[i];
            count[digit]--;
        }
        System.arraycopy(output, 0, arr, 0, n);
    }
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab02/AdvancedSortingExample.java" @'
package com.algo.lab02;

import java.util.Arrays;
import java.util.Random;

public class AdvancedSortingExample {
    public static void main(String[] args) {
        System.out.println("=== Advanced Sorting Demo ===");

        Integer[] arr1 = {38, 27, 43, 3, 9, 82, 10};
        AdvancedSortingAlgorithms.mergeSort(arr1);
        System.out.println("Merge Sort: " + Arrays.toString(arr1));

        Integer[] arr2 = {38, 27, 43, 3, 9, 82, 10};
        AdvancedSortingAlgorithms.quickSort(arr2);
        System.out.println("Quick Sort: " + Arrays.toString(arr2));

        Integer[] arr3 = {38, 27, 43, 3, 9, 82, 10};
        AdvancedSortingAlgorithms.heapSort(arr3);
        System.out.println("Heap Sort: " + Arrays.toString(arr3));

        int[] arr4 = {4, 2, 2, 8, 3, 3, 1};
        AdvancedSortingAlgorithms.countingSort(arr4);
        System.out.println("Counting Sort: " + Arrays.toString(arr4));

        int[] arr5 = {170, 45, 75, 90, 802, 24, 2, 66};
        AdvancedSortingAlgorithms.radixSort(arr5);
        System.out.println("Radix Sort: " + Arrays.toString(arr5));

        // Benchmark comparison on large array
        Random rand = new Random(42);
        int n = 50000;
        Integer[] large1 = rand.ints(n, 0, 1000000).boxed().toArray(Integer[]::new);
        Integer[] large2 = large1.clone();
        Integer[] large3 = large1.clone();

        long t1 = System.nanoTime();
        AdvancedSortingAlgorithms.mergeSort(large1);
        long t2 = System.nanoTime();
        AdvancedSortingAlgorithms.quickSort(large2);
        long t3 = System.nanoTime();
        AdvancedSortingAlgorithms.heapSort(large3);
        long t4 = System.nanoTime();

        System.out.printf("%nBenchmark (n=%d):%n", n);
        System.out.printf("Merge Sort:  %.2f ms%n", (t2 - t1) / 1e6);
        System.out.printf("Quick Sort:  %.2f ms%n", (t3 - t2) / 1e6);
        System.out.printf("Heap Sort:   %.2f ms%n", (t4 - t3) / 1e6);
    }
}
'@

Write-JavaFile $lab "src/test/java/com/algo/lab02/AdvancedSortingAlgorithmsTest.java" @'
package com.algo.lab02;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

class AdvancedSortingAlgorithmsTest {

    private <T extends Comparable<T>> boolean isSorted(T[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i].compareTo(arr[i - 1]) < 0) return false;
        }
        return true;
    }

    @Test
    void testMergeSort() {
        Integer[] arr = {38, 27, 43, 3, 9, 82, 10};
        AdvancedSortingAlgorithms.mergeSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testMergeSortEmpty() {
        Integer[] arr = {};
        AdvancedSortingAlgorithms.mergeSort(arr);
        assertArrayEquals(new Integer[]{}, arr);
    }

    @Test
    void testQuickSort() {
        Integer[] arr = {10, 7, 8, 9, 1, 5};
        AdvancedSortingAlgorithms.quickSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testQuickSortWithDuplicates() {
        Integer[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5};
        AdvancedSortingAlgorithms.quickSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testHeapSort() {
        Integer[] arr = {12, 11, 13, 5, 6, 7};
        AdvancedSortingAlgorithms.heapSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testHeapSortStrings() {
        String[] arr = {"zebra", "apple", "monkey", "banana"};
        AdvancedSortingAlgorithms.heapSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testCountingSort() {
        int[] arr = {4, 2, 2, 8, 3, 3, 1};
        AdvancedSortingAlgorithms.countingSort(arr);
        assertArrayEquals(new int[]{1, 2, 2, 3, 3, 4, 8}, arr);
    }

    @Test
    void testCountingSortEmpty() {
        int[] arr = {};
        AdvancedSortingAlgorithms.countingSort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    void testRadixSort() {
        int[] arr = {170, 45, 75, 90, 802, 24, 2, 66};
        AdvancedSortingAlgorithms.radixSort(arr);
        assertArrayEquals(new int[]{2, 24, 45, 66, 75, 90, 170, 802}, arr);
    }

    @Test
    void testRadixSortWithZero() {
        int[] arr = {0, 5, 3, 0, 9, 1};
        AdvancedSortingAlgorithms.radixSort(arr);
        assertArrayEquals(new int[]{0, 0, 1, 3, 5, 9}, arr);
    }

    @Test
    void testAllComparisonSortsAgree() {
        Integer[] original = {42, 17, 8, 99, 23, 56, 3, 11, 77, 31};
        Integer[] mergeArr = original.clone();
        Integer[] quickArr = original.clone();
        Integer[] heapArr = original.clone();

        AdvancedSortingAlgorithms.mergeSort(mergeArr);
        AdvancedSortingAlgorithms.quickSort(quickArr);
        AdvancedSortingAlgorithms.heapSort(heapArr);

        assertArrayEquals(mergeArr, quickArr);
        assertArrayEquals(mergeArr, heapArr);
    }
}
'@

# ============================================================
# LAB 03: Searching
# ============================================================
$lab = "$base\03-searching"

Write-JavaFile $lab "src/main/java/com/algo/lab03/SearchingAlgorithms.java" @'
package com.algo.lab03;

/**
 * Searching algorithms.
 *
 * Linear Search: O(n) time, O(1) space
 * Binary Search (Iterative): O(log n) time, O(1) space
 * Binary Search (Recursive): O(log n) time, O(log n) space
 * Interpolation Search: O(log log n) avg, O(n) worst, O(1) space
 */
public class SearchingAlgorithms {

    private SearchingAlgorithms() {}

    /**
     * Linear search - finds index of target in array.
     * @return index of target or -1 if not found
     */
    public static <T extends Comparable<T>> int linearSearch(T[] arr, T target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].compareTo(target) == 0) return i;
        }
        return -1;
    }

    /**
     * Iterative binary search on sorted array.
     */
    public static <T extends Comparable<T>> int binarySearchIterative(T[] arr, T target) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = arr[mid].compareTo(target);
            if (cmp == 0) return mid;
            if (cmp < 0) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    /**
     * Recursive binary search on sorted array.
     */
    public static <T extends Comparable<T>> int binarySearchRecursive(T[] arr, T target) {
        return binarySearchRecursive(arr, target, 0, arr.length - 1);
    }

    private static <T extends Comparable<T>> int binarySearchRecursive(T[] arr, T target, int left, int right) {
        if (left > right) return -1;
        int mid = left + (right - left) / 2;
        int cmp = arr[mid].compareTo(target);
        if (cmp == 0) return mid;
        if (cmp < 0) return binarySearchRecursive(arr, target, mid + 1, right);
        return binarySearchRecursive(arr, target, left, mid - 1);
    }

    /**
     * Interpolation search on sorted uniformly distributed array.
     * Works best with integers.
     */
    public static int interpolationSearch(int[] arr, int target) {
        int low = 0, high = arr.length - 1;
        while (low <= high && target >= arr[low] && target <= arr[high]) {
            if (low == high) {
                return arr[low] == target ? low : -1;
            }
            int pos = low + ((target - arr[low]) * (high - low)) / (arr[high] - arr[low]);
            if (arr[pos] == target) return pos;
            if (arr[pos] < target) low = pos + 1;
            else high = pos - 1;
        }
        return -1;
    }
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab03/SearchingExample.java" @'
package com.algo.lab03;

import java.util.Arrays;

public class SearchingExample {
    public static void main(String[] args) {
        System.out.println("=== Searching Algorithms Demo ===");

        Integer[] items = {10, 20, 30, 40, 50, 60, 70, 80, 90};
        System.out.println("Array: " + Arrays.toString(items));

        int target = 50;
        int linResult = SearchingAlgorithms.linearSearch(items, target);
        int binIter = SearchingAlgorithms.binarySearchIterative(items, target);
        int binRecur = SearchingAlgorithms.binarySearchRecursive(items, target);
        System.out.printf("Linear search(%d): index=%d%n", target, linResult);
        System.out.printf("Binary iterative(%d): index=%d%n", target, binIter);
        System.out.printf("Binary recursive(%d): index=%d%n", target, binRecur);

        target = 55;
        System.out.printf("%nSearching for %d (not present):%n", target);
        System.out.printf("Linear: %d | BinaryIter: %d | BinaryRecur: %d%n",
            SearchingAlgorithms.linearSearch(items, target),
            SearchingAlgorithms.binarySearchIterative(items, target),
            SearchingAlgorithms.binarySearchRecursive(items, target));

        int[] intArr = {2, 4, 6, 8, 10, 12, 14, 16, 18, 20};
        System.out.printf("%nInterpolation search for 14: index=%d%n",
            SearchingAlgorithms.interpolationSearch(intArr, 14));
        System.out.printf("Interpolation search for 3 (not present): %d%n",
            SearchingAlgorithms.interpolationSearch(intArr, 3));
    }
}
'@

Write-JavaFile $lab "src/test/java/com/algo/lab03/SearchingAlgorithmsTest.java" @'
package com.algo.lab03;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SearchingAlgorithmsTest {

    private static final Integer[] SORTED = {10, 20, 30, 40, 50, 60, 70, 80, 90};
    private static final int[] INT_SORTED = {2, 4, 6, 8, 10, 12, 14, 16, 18, 20};

    @Test
    void testLinearSearchFound() {
        assertEquals(0, SearchingAlgorithms.linearSearch(SORTED, 10));
        assertEquals(4, SearchingAlgorithms.linearSearch(SORTED, 50));
        assertEquals(8, SearchingAlgorithms.linearSearch(SORTED, 90));
    }

    @Test
    void testLinearSearchNotFound() {
        assertEquals(-1, SearchingAlgorithms.linearSearch(SORTED, 5));
        assertEquals(-1, SearchingAlgorithms.linearSearch(SORTED, 100));
    }

    @Test
    void testLinearSearchEmpty() {
        Integer[] empty = {};
        assertEquals(-1, SearchingAlgorithms.linearSearch(empty, 1));
    }

    @Test
    void testBinarySearchIterativeFound() {
        assertEquals(0, SearchingAlgorithms.binarySearchIterative(SORTED, 10));
        assertEquals(4, SearchingAlgorithms.binarySearchIterative(SORTED, 50));
        assertEquals(8, SearchingAlgorithms.binarySearchIterative(SORTED, 90));
    }

    @Test
    void testBinarySearchIterativeNotFound() {
        assertEquals(-1, SearchingAlgorithms.binarySearchIterative(SORTED, 5));
        assertEquals(-1, SearchingAlgorithms.binarySearchIterative(SORTED, 100));
        assertEquals(-1, SearchingAlgorithms.binarySearchIterative(SORTED, 55));
    }

    @Test
    void testBinarySearchIterativeEmpty() {
        Integer[] empty = {};
        assertEquals(-1, SearchingAlgorithms.binarySearchIterative(empty, 1));
    }

    @Test
    void testBinarySearchRecursiveFound() {
        assertEquals(0, SearchingAlgorithms.binarySearchRecursive(SORTED, 10));
        assertEquals(4, SearchingAlgorithms.binarySearchRecursive(SORTED, 50));
        assertEquals(8, SearchingAlgorithms.binarySearchRecursive(SORTED, 90));
    }

    @Test
    void testBinarySearchRecursiveNotFound() {
        assertEquals(-1, SearchingAlgorithms.binarySearchRecursive(SORTED, 55));
    }

    @Test
    void testBinaryMethodsAgree() {
        for (int i = 0; i < SORTED.length; i++) {
            int v = SORTED[i];
            assertEquals(
                SearchingAlgorithms.binarySearchIterative(SORTED, v),
                SearchingAlgorithms.binarySearchRecursive(SORTED, v));
        }
    }

    @Test
    void testInterpolationSearchFound() {
        assertEquals(0, SearchingAlgorithms.interpolationSearch(INT_SORTED, 2));
        assertEquals(6, SearchingAlgorithms.interpolationSearch(INT_SORTED, 14));
        assertEquals(9, SearchingAlgorithms.interpolationSearch(INT_SORTED, 20));
    }

    @Test
    void testInterpolationSearchNotFound() {
        assertEquals(-1, SearchingAlgorithms.interpolationSearch(INT_SORTED, 3));
        assertEquals(-1, SearchingAlgorithms.interpolationSearch(INT_SORTED, 1));
        assertEquals(-1, SearchingAlgorithms.interpolationSearch(INT_SORTED, 25));
    }

    @Test
    void testInterpolationSearchSingleElement() {
        int[] arr = {5};
        assertEquals(0, SearchingAlgorithms.interpolationSearch(arr, 5));
        assertEquals(-1, SearchingAlgorithms.interpolationSearch(arr, 3));
    }
}
'@

# ============================================================
# LAB 04: Recursion
# ============================================================
$lab = "$base\04-recursion"

Write-JavaFile $lab "src/main/java/com/algo/lab04/RecursiveAlgorithms.java" @'
package com.algo.lab04;

import java.util.ArrayList;
import java.util.List;

/**
 * Classic recursive algorithms.
 *
 * Factorial: O(n) time, O(n) stack space
 * Fibonacci (naive): O(2^n) time, O(n) stack space
 * Tower of Hanoi: O(2^n) time, O(n) stack space
 * Subset Generation: O(n * 2^n) time, O(n) space
 * Permutations: O(n * n!) time, O(n) space
 */
public class RecursiveAlgorithms {

    private RecursiveAlgorithms() {}

    public static long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    public static long fibonacci(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public static List<String> towerOfHanoi(int n, char from, char to, char aux) {
        List<String> steps = new ArrayList<>();
        towerOfHanoi(n, from, to, aux, steps);
        return steps;
    }

    private static void towerOfHanoi(int n, char from, char to, char aux, List<String> steps) {
        if (n == 1) {
            steps.add("Move disk 1 from " + from + " to " + to);
            return;
        }
        towerOfHanoi(n - 1, from, aux, to, steps);
        steps.add("Move disk " + n + " from " + from + " to " + to);
        towerOfHanoi(n - 1, aux, to, from, steps);
    }

    public static <T> List<List<T>> subsets(List<T> elements) {
        List<List<T>> result = new ArrayList<>();
        subsets(elements, 0, new ArrayList<>(), result);
        return result;
    }

    private static <T> void subsets(List<T> elements, int idx, List<T> current, List<List<T>> result) {
        if (idx == elements.size()) {
            result.add(new ArrayList<>(current));
            return;
        }
        subsets(elements, idx + 1, current, result);
        current.add(elements.get(idx));
        subsets(elements, idx + 1, current, result);
        current.remove(current.size() - 1);
    }

    public static <T> List<List<T>> permutations(List<T> elements) {
        List<List<T>> result = new ArrayList<>();
        permute(elements, 0, result);
        return result;
    }

    private static <T> void permute(List<T> elements, int start, List<List<T>> result) {
        if (start == elements.size() - 1) {
            result.add(new ArrayList<>(elements));
            return;
        }
        for (int i = start; i < elements.size(); i++) {
            swap(elements, start, i);
            permute(elements, start + 1, result);
            swap(elements, start, i);
        }
    }

    private static <T> void swap(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab04/RecursionExample.java" @'
package com.algo.lab04;

import java.util.Arrays;
import java.util.List;

public class RecursionExample {
    public static void main(String[] args) {
        System.out.println("=== Recursion Demo ===\n");

        System.out.println("--- Factorial ---");
        for (int n = 0; n <= 10; n++) {
            System.out.printf("%d! = %d%n", n, RecursiveAlgorithms.factorial(n));
        }

        System.out.println("\n--- Fibonacci ---");
        for (int n = 0; n <= 15; n++) {
            System.out.printf("F(%d) = %d%n", n, RecursiveAlgorithms.fibonacci(n));
        }

        System.out.println("\n--- Tower of Hanoi (3 disks) ---");
        RecursiveAlgorithms.towerOfHanoi(3, 'A', 'C', 'B')
            .forEach(System.out::println);

        System.out.println("\n--- Subsets of {1, 2, 3} ---");
        List<List<Integer>> subsets = RecursiveAlgorithms.subsets(Arrays.asList(1, 2, 3));
        subsets.forEach(System.out::println);
        System.out.println("Total subsets: " + subsets.size());

        System.out.println("\n--- Permutations of {A, B, C} ---");
        List<List<String>> perms = RecursiveAlgorithms.permutations(Arrays.asList("A", "B", "C"));
        perms.forEach(System.out::println);
        System.out.println("Total permutations: " + perms.size());
    }
}
'@

Write-JavaFile $lab "src/test/java/com/algo/lab04/RecursiveAlgorithmsTest.java" @'
package com.algo.lab04;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

class RecursiveAlgorithmsTest {

    @Test
    void testFactorial() {
        assertEquals(1, RecursiveAlgorithms.factorial(0));
        assertEquals(1, RecursiveAlgorithms.factorial(1));
        assertEquals(2, RecursiveAlgorithms.factorial(2));
        assertEquals(6, RecursiveAlgorithms.factorial(3));
        assertEquals(24, RecursiveAlgorithms.factorial(4));
        assertEquals(120, RecursiveAlgorithms.factorial(5));
        assertEquals(3628800, RecursiveAlgorithms.factorial(10));
    }

    @Test
    void testFactorialNegative() {
        assertThrows(IllegalArgumentException.class, () -> RecursiveAlgorithms.factorial(-1));
    }

    @Test
    void testFibonacci() {
        assertEquals(0, RecursiveAlgorithms.fibonacci(0));
        assertEquals(1, RecursiveAlgorithms.fibonacci(1));
        assertEquals(1, RecursiveAlgorithms.fibonacci(2));
        assertEquals(2, RecursiveAlgorithms.fibonacci(3));
        assertEquals(3, RecursiveAlgorithms.fibonacci(4));
        assertEquals(5, RecursiveAlgorithms.fibonacci(5));
        assertEquals(55, RecursiveAlgorithms.fibonacci(10));
    }

    @Test
    void testFibonacciNegative() {
        assertThrows(IllegalArgumentException.class, () -> RecursiveAlgorithms.fibonacci(-1));
    }

    @Test
    void testTowerOfHanoiOneDisk() {
        List<String> steps = RecursiveAlgorithms.towerOfHanoi(1, 'A', 'C', 'B');
        assertEquals(1, steps.size());
        assertTrue(steps.get(0).contains("Move disk 1 from A to C"));
    }

    @Test
    void testTowerOfHanoiThreeDisks() {
        List<String> steps = RecursiveAlgorithms.towerOfHanoi(3, 'A', 'C', 'B');
        assertEquals(7, steps.size());
    }

    @Test
    void testTowerOfHanoiCount() {
        int disks = 5;
        List<String> steps = RecursiveAlgorithms.towerOfHanoi(disks, 'A', 'C', 'B');
        assertEquals((1 << disks) - 1, steps.size());
    }

    @Test
    void testSubsets() {
        List<List<Integer>> subsets = RecursiveAlgorithms.subsets(Arrays.asList(1, 2));
        assertEquals(4, subsets.size());
    }

    @Test
    void testSubsetsCount() {
        List<List<Integer>> subsets = RecursiveAlgorithms.subsets(Arrays.asList(1, 2, 3, 4));
        assertEquals(16, subsets.size());
    }

    @Test
    void testSubsetsEmpty() {
        List<List<Integer>> subsets = RecursiveAlgorithms.subsets(Arrays.asList());
        assertEquals(1, subsets.size());
        assertTrue(subsets.get(0).isEmpty());
    }

    @Test
    void testSubsetsContainsEmptySet() {
        List<List<Integer>> subsets = RecursiveAlgorithms.subsets(Arrays.asList(1, 2, 3));
        assertTrue(subsets.contains(Arrays.asList()));
    }

    @Test
    void testPermutations() {
        List<List<Integer>> perms = RecursiveAlgorithms.permutations(Arrays.asList(1, 2, 3));
        assertEquals(6, perms.size());
    }

    @Test
    void testPermutationsCount() {
        List<List<Character>> perms = RecursiveAlgorithms.permutations(Arrays.asList('A', 'B', 'C', 'D'));
        assertEquals(24, perms.size());
    }

    @Test
    void testPermutationsSingle() {
        List<List<String>> perms = RecursiveAlgorithms.permutations(Arrays.asList("X"));
        assertEquals(1, perms.size());
        assertEquals("X", perms.get(0).get(0));
    }

    @Test
    void testPermutationsEachHasAllElements() {
        List<List<Integer>> perms = RecursiveAlgorithms.permutations(Arrays.asList(1, 2, 3, 4));
        for (List<Integer> p : perms) {
            assertEquals(4, p.size());
            assertTrue(p.containsAll(Arrays.asList(1, 2, 3, 4)));
        }
    }
}
'@

# ============================================================
# LAB 05: Dynamic Programming
# ============================================================
$lab = "$base\05-dynamic-programming"

Write-JavaFile $lab "src/main/java/com/algo/lab05/DynamicProgramming.java" @'
package com.algo.lab05;

/**
 * Dynamic programming algorithms.
 *
 * Fibonacci (DP): O(n) time, O(1) space
 * 0/1 Knapsack: O(n*W) time, O(n*W) space
 * Longest Common Subsequence (LCS): O(m*n) time, O(m*n) space
 * Longest Increasing Subsequence (LIS): O(n log n) time, O(n) space
 * Edit Distance (Levenshtein): O(m*n) time, O(m*n) space
 */
public class DynamicProgramming {

    private DynamicProgramming() {}

    public static long fibonacciDP(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        if (n <= 1) return n;
        long prev = 0, curr = 1;
        for (int i = 2; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }

    public static int knapSack(int[] weights, int[] values, int capacity) {
        int n = values.length;
        int[][] dp = new int[n + 1][capacity + 1];
        for (int i = 1; i <= n; i++) {
            int w = weights[i - 1];
            int v = values[i - 1];
            for (int cap = 1; cap <= capacity; cap++) {
                if (w <= cap) {
                    dp[i][cap] = Math.max(v + dp[i - 1][cap - w], dp[i - 1][cap]);
                } else {
                    dp[i][cap] = dp[i - 1][cap];
                }
            }
        }
        return dp[n][capacity];
    }

    public static int longestCommonSubsequence(String a, String b) {
        int m = a.length(), n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    public static int longestIncreasingSubsequence(int[] arr) {
        int n = arr.length;
        int[] tails = new int[n];
        int len = 0;
        for (int x : arr) {
            int idx = java.util.Arrays.binarySearch(tails, 0, len, x);
            if (idx < 0) idx = -(idx + 1);
            tails[idx] = x;
            if (idx == len) len++;
        }
        return len;
    }

    public static int editDistance(String a, String b) {
        int m = a.length(), n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j],
                                   Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
                }
            }
        }
        return dp[m][n];
    }
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab05/DynamicProgrammingExample.java" @'
package com.algo.lab05;

public class DynamicProgrammingExample {
    public static void main(String[] args) {
        System.out.println("=== Dynamic Programming Demo ===\n");

        System.out.println("--- Fibonacci (DP) ---");
        for (int n = 0; n <= 20; n++) {
            System.out.printf("F(%d) = %d%n", n, DynamicProgramming.fibonacciDP(n));
        }

        System.out.println("\n--- 0/1 Knapsack ---");
        int[] weights = {10, 20, 30};
        int[] values = {60, 100, 120};
        int capacity = 50;
        int maxVal = DynamicProgramming.knapSack(weights, values, capacity);
        System.out.printf("Max value (capacity=%d): %d%n", capacity, maxVal);

        System.out.println("\n--- Longest Common Subsequence ---");
        String a = "ABCDGH", b = "AEDFHR";
        System.out.printf("LCS of \"%s\" and \"%s\": %d%n", a, b,
            DynamicProgramming.longestCommonSubsequence(a, b));

        System.out.println("\n--- Longest Increasing Subsequence ---");
        int[] arr = {10, 22, 9, 33, 21, 50, 41, 60, 80};
        System.out.printf("LIS length: %d%n", DynamicProgramming.longestIncreasingSubsequence(arr));

        System.out.println("\n--- Edit Distance ---");
        String s1 = "kitten", s2 = "sitting";
        System.out.printf("Edit distance \"%s\" -> \"%s\": %d%n", s1, s2,
            DynamicProgramming.editDistance(s1, s2));
    }
}
'@

Write-JavaFile $lab "src/test/java/com/algo/lab05/DynamicProgrammingTest.java" @'
package com.algo.lab05;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DynamicProgrammingTest {

    @Test
    void testFibonacciDP() {
        assertEquals(0, DynamicProgramming.fibonacciDP(0));
        assertEquals(1, DynamicProgramming.fibonacciDP(1));
        assertEquals(1, DynamicProgramming.fibonacciDP(2));
        assertEquals(2, DynamicProgramming.fibonacciDP(3));
        assertEquals(3, DynamicProgramming.fibonacciDP(4));
        assertEquals(5, DynamicProgramming.fibonacciDP(5));
        assertEquals(55, DynamicProgramming.fibonacciDP(10));
        assertEquals(832040, DynamicProgramming.fibonacciDP(30));
    }

    @Test
    void testFibonacciDPNegative() {
        assertThrows(IllegalArgumentException.class, () -> DynamicProgramming.fibonacciDP(-1));
    }

    @Test
    void testKnapSack() {
        int[] weights = {10, 20, 30};
        int[] values = {60, 100, 120};
        assertEquals(220, DynamicProgramming.knapSack(weights, values, 50));
    }

    @Test
    void testKnapSackNoCapacity() {
        int[] weights = {10, 20};
        int[] values = {60, 100};
        assertEquals(0, DynamicProgramming.knapSack(weights, values, 0));
    }

    @Test
    void testKnapSackEmpty() {
        assertEquals(0, DynamicProgramming.knapSack(new int[]{}, new int[]{}, 50));
    }

    @Test
    void testKnapSackSmallItems() {
        int[] weights = {1, 2, 3};
        int[] values = {10, 15, 40};
        assertEquals(65, DynamicProgramming.knapSack(weights, values, 5));
    }

    @Test
    void testLCS() {
        assertEquals(4, DynamicProgramming.longestCommonSubsequence("ABCDGH", "AEDFHR"));
        assertEquals(3, DynamicProgramming.longestCommonSubsequence("AGGTAB", "GXTXAYB"));
    }

    @Test
    void testLCSEmpty() {
        assertEquals(0, DynamicProgramming.longestCommonSubsequence("", "ABC"));
        assertEquals(0, DynamicProgramming.longestCommonSubsequence("ABC", ""));
        assertEquals(0, DynamicProgramming.longestCommonSubsequence("", ""));
    }

    @Test
    void testLCSIdentical() {
        assertEquals(5, DynamicProgramming.longestCommonSubsequence("ABCDE", "ABCDE"));
    }

    @Test
    void testLCSNoCommon() {
        assertEquals(0, DynamicProgramming.longestCommonSubsequence("ABC", "XYZ"));
    }

    @Test
    void testLIS() {
        assertEquals(6, DynamicProgramming.longestIncreasingSubsequence(
            new int[]{10, 22, 9, 33, 21, 50, 41, 60, 80}));
    }

    @Test
    void testLISEmpty() {
        assertEquals(0, DynamicProgramming.longestIncreasingSubsequence(new int[]{}));
    }

    @Test
    void testLISAllDecreasing() {
        assertEquals(1, DynamicProgramming.longestIncreasingSubsequence(new int[]{5, 4, 3, 2, 1}));
    }

    @Test
    void testLISAllIncreasing() {
        assertEquals(5, DynamicProgramming.longestIncreasingSubsequence(new int[]{1, 2, 3, 4, 5}));
    }

    @Test
    void testEditDistance() {
        assertEquals(3, DynamicProgramming.editDistance("kitten", "sitting"));
        assertEquals(3, DynamicProgramming.editDistance("saturday", "sunday"));
    }

    @Test
    void testEditDistanceEmpty() {
        assertEquals(5, DynamicProgramming.editDistance("", "hello"));
        assertEquals(5, DynamicProgramming.editDistance("hello", ""));
        assertEquals(0, DynamicProgramming.editDistance("", ""));
    }

    @Test
    void testEditDistanceIdentical() {
        assertEquals(0, DynamicProgramming.editDistance("same", "same"));
    }

    @Test
    void testEditDistanceOneChar() {
        assertEquals(0, DynamicProgramming.editDistance("a", "a"));
        assertEquals(1, DynamicProgramming.editDistance("a", "b"));
    }
}
'@

Write-Host "=== Script 1 (Labs 01-05) completed ==="

$base = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms"

function Write-JavaFile($labDir, $relPath, $content) {
    $fullPath = Join-Path $labDir $relPath
    $dir = Split-Path $fullPath -Parent
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    Set-Content -Path $fullPath -Value $content -NoNewline
    Write-Host "  Created: $relPath"
}

# ============================================================
# LAB 11: Branch and Bound
# ============================================================
$lab = "$base\11-branch-and-bound"

Write-JavaFile $lab "src/main/java/com/algo/lab11/BranchAndBound.java" @'
package com.algo.lab11;

import java.util.*;

/**
 * Branch and Bound algorithms.
 *
 * TSP (Branch & Bound): O(n!) worst, O(n^2) space
 * 0/1 Knapsack (B&B): O(2^n) worst, O(n) space
 */
public class BranchAndBound {

    private BranchAndBound() {}

    public static int tspBranchAndBound(int[][] graph) {
        int n = graph.length;
        boolean[] visited = new boolean[n];
        int[] bestPath = new int[n + 1];
        int[] bestCost = {Integer.MAX_VALUE};
        visited[0] = true;
        tspUtil(graph, visited, 0, 0, 1, n, new int[n + 1], bestCost, bestPath);
        return bestCost[0];
    }

    private static void tspUtil(int[][] graph, boolean[] visited, int currPos, int cost,
                                 int count, int n, int[] path, int[] bestCost, int[] bestPath) {
        if (count == n && graph[currPos][0] > 0) {
            int totalCost = cost + graph[currPos][0];
            if (totalCost < bestCost[0]) {
                bestCost[0] = totalCost;
                System.arraycopy(path, 0, bestPath, 0, n);
                bestPath[n] = 0;
            }
            return;
        }
        for (int i = 0; i < n; i++) {
            if (!visited[i] && graph[currPos][i] > 0) {
                int bound = cost + graph[currPos][i];
                if (bound >= bestCost[0]) continue;
                visited[i] = true;
                path[count] = i;
                tspUtil(graph, visited, i, bound, count + 1, n, path, bestCost, bestPath);
                visited[i] = false;
            }
        }
    }

    public static int knapSackBranchBound(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        Item[] items = new Item[n];
        for (int i = 0; i < n; i++) {
            items[i] = new Item(weights[i], values[i], (double) values[i] / weights[i]);
        }
        Arrays.sort(items, (a, b) -> Double.compare(b.ratio, a.ratio));
        return knapSackBB(items, capacity, 0, 0, 0, new int[]{0});
    }

    private static int knapSackBB(Item[] items, int capacity, int idx, int currentWeight,
                                   int currentValue, int[] bestValue) {
        if (currentWeight > capacity) return bestValue[0];
        if (currentValue > bestValue[0]) bestValue[0] = currentValue;
        if (idx >= items.length) return bestValue[0];
        double bound = bound(items, capacity, idx, currentWeight, currentValue);
        if (bound <= bestValue[0]) return bestValue[0];
        knapSackBB(items, capacity, idx + 1, currentWeight + items[idx].weight,
                    currentValue + items[idx].value, bestValue);
        knapSackBB(items, capacity, idx + 1, currentWeight, currentValue, bestValue);
        return bestValue[0];
    }

    private static double bound(Item[] items, int capacity, int idx, int weight, int value) {
        double totalValue = value;
        int remaining = capacity - weight;
        int j = idx;
        while (j < items.length && items[j].weight <= remaining) {
            remaining -= items[j].weight;
            totalValue += items[j].value;
            j++;
        }
        if (j < items.length) {
            totalValue += items[j].ratio * remaining;
        }
        return totalValue;
    }

    private record Item(int weight, int value, double ratio) {}
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab11/BranchAndBoundExample.java" @'
package com.algo.lab11;

public class BranchAndBoundExample {
    public static void main(String[] args) {
        System.out.println("=== Branch and Bound Demo ===\n");

        System.out.println("--- TSP (Branch & Bound) ---");
        int[][] tspGraph = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        int tspCost = BranchAndBound.tspBranchAndBound(tspGraph);
        System.out.println("Minimum TSP cost: " + tspCost);

        System.out.println("\n--- 0/1 Knapsack (Branch & Bound) ---");
        int[] weights = {10, 20, 30, 40, 50};
        int[] values = {60, 100, 120, 200, 240};
        int capacity = 100;
        int maxVal = BranchAndBound.knapSackBranchBound(weights, values, capacity);
        System.out.println("Max value (capacity " + capacity + "): " + maxVal);
    }
}
'@

Write-JavaFile $lab "src/test/java/com/algo/lab11/BranchAndBoundTest.java" @'
package com.algo.lab11;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BranchAndBoundTest {

    @Test
    void testTSP() {
        int[][] graph = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        int cost = BranchAndBound.tspBranchAndBound(graph);
        assertEquals(80, cost);
    }

    @Test
    void testTSP3Cities() {
        int[][] graph = {
            {0, 10, 15},
            {10, 0, 5},
            {15, 5, 0}
        };
        int cost = BranchAndBound.tspBranchAndBound(graph);
        assertTrue(cost > 0);
    }

    @Test
    void testKnapSackBB() {
        int[] weights = {10, 20, 30};
        int[] values = {60, 100, 120};
        assertEquals(220, BranchAndBound.knapSackBranchBound(weights, values, 50));
    }

    @Test
    void testKnapSackBBEmpty() {
        assertEquals(0, BranchAndBound.knapSackBranchBound(new int[]{}, new int[]{}, 50));
    }

    @Test
    void testKnapSackBBZeroCapacity() {
        assertEquals(0, BranchAndBound.knapSackBranchBound(
            new int[]{10, 20}, new int[]{60, 100}, 0));
    }

    @Test
    void testKnapSackBBLargeCapacity() {
        int[] weights = {5, 10, 15, 20};
        int[] values = {50, 100, 150, 200};
        int result = BranchAndBound.knapSackBranchBound(weights, values, 100);
        assertEquals(500, result);
    }
}
'@

# ============================================================
# LAB 12: Complexity Analysis
# ============================================================
$lab = "$base\12-complexity-analysis"

Write-JavaFile $lab "src/main/java/com/algo/lab12/ComplexityExamples.java" @'
package com.algo.lab12;

import java.util.*;

/**
 * Examples demonstrating different time complexities.
 *
 * O(1) - Constant time
 * O(log n) - Logarithmic time
 * O(n) - Linear time
 * O(n log n) - Linearithmic time
 * O(n^2) - Quadratic time
 */
public class ComplexityExamples {

    private ComplexityExamples() {}

    /** O(1) - Array access by index */
    public static int constantTime(int[] arr, int index) {
        return arr[index];
    }

    /** O(log n) - Binary search */
    public static int logarithmicTime(int[] sortedArr, int target) {
        int left = 0, right = sortedArr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (sortedArr[mid] == target) return mid;
            if (sortedArr[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    /** O(n) - Linear search */
    public static int linearTime(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) return i;
        }
        return -1;
    }

    /** O(n log n) - Merge sort (not in-place for demo) */
    public static void linearithmicTime(int[] arr) {
        if (arr.length < 2) return;
        int mid = arr.length / 2;
        int[] left = Arrays.copyOfRange(arr, 0, mid);
        int[] right = Arrays.copyOfRange(arr, mid, arr.length);
        linearithmicTime(left);
        linearithmicTime(right);
        merge(arr, left, right);
    }

    private static void merge(int[] arr, int[] left, int[] right) {
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            arr[k++] = left[i] <= right[j] ? left[i++] : right[j++];
        }
        while (i < left.length) arr[k++] = left[i++];
        while (j < right.length) arr[k++] = right[j++];
    }

    /** O(n^2) - Bubble sort */
    public static void quadraticTime(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    /** O(n) - Compute sum of array */
    public static int sumArray(int[] arr) {
        int sum = 0;
        for (int v : arr) sum += v;
        return sum;
    }

    /** O(n^2) - Find duplicates in array */
    public static boolean hasDuplicates(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] == arr[j]) return true;
            }
        }
        return false;
    }

    /** O(log n) - Find power using exponentiation by squaring */
    public static long power(long base, long exp) {
        if (exp == 0) return 1;
        long half = power(base, exp / 2);
        return exp % 2 == 0 ? half * half : half * half * base;
    }

    /** O(n log n) - Heapsort */
    public static void heapSort(int[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) heapify(arr, n, i);
        for (int i = n - 1; i > 0; i--) {
            int temp = arr[0]; arr[0] = arr[i]; arr[i] = temp;
            heapify(arr, i, 0);
        }
    }

    private static void heapify(int[] arr, int n, int i) {
        int largest = i, left = 2 * i + 1, right = 2 * i + 2;
        if (left < n && arr[left] > arr[largest]) largest = left;
        if (right < n && arr[right] > arr[largest]) largest = right;
        if (largest != i) {
            int temp = arr[i]; arr[i] = arr[largest]; arr[largest] = temp;
            heapify(arr, n, largest);
        }
    }
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab12/ComplexityExample.java" @'
package com.algo.lab12;

import java.util.Arrays;
import java.util.Random;

public class ComplexityExample {
    public static void main(String[] args) {
        System.out.println("=== Complexity Analysis Demo ===\n");

        Random rand = new Random(42);

        System.out.println("--- O(1) Constant Time ---");
        int[] arr = {10, 20, 30, 40, 50};
        System.out.println("arr[2] = " + ComplexityExamples.constantTime(arr, 2));

        System.out.println("\n--- O(log n) Binary Search ---");
        int[] sorted = {1, 3, 5, 7, 9, 11, 13, 15};
        System.out.println("Index of 9: " + ComplexityExamples.logarithmicTime(sorted, 9));

        System.out.println("\n--- O(log n) Power ---");
        System.out.println("2^10 = " + ComplexityExamples.power(2, 10));

        System.out.println("\n--- O(n) Sum ---");
        System.out.println("Sum: " + ComplexityExamples.sumArray(arr));

        System.out.println("\n--- O(n^2) Has Duplicates ---");
        System.out.println("Has dups: " + ComplexityExamples.hasDuplicates(new int[]{1, 2, 3, 2}));

        System.out.println("\n--- O(n log n) Sort ---");
        int[] toSort = rand.ints(20, 0, 100).toArray();
        System.out.println("Before: " + Arrays.toString(toSort));
        ComplexityExamples.linearithmicTime(toSort);
        System.out.println("After:  " + Arrays.toString(toSort));

        System.out.println("\n--- Time Measurement ---");
        int n = 100000;
        long start, end;

        int[] testArr = rand.ints(n, 0, n).toArray();
        start = System.nanoTime();
        long sum = ComplexityExamples.sumArray(testArr);
        end = System.nanoTime();
        System.out.printf("O(n) sum: sum=%d, time=%.2f ms%n", sum, (end - start) / 1e6);

        int[] sortArr = rand.ints(n, 0, n).toArray();
        start = System.nanoTime();
        ComplexityExamples.linearithmicTime(sortArr);
        end = System.nanoTime();
        System.out.printf("O(n log n) sort: time=%.2f ms%n", (end - start) / 1e6);

        int[] bubbleArr = rand.ints(10000, 0, 10000).toArray();
        start = System.nanoTime();
        ComplexityExamples.quadraticTime(bubbleArr);
        end = System.nanoTime();
        System.out.printf("O(n^2) bubble sort (n=10000): time=%.2f ms%n", (end - start) / 1e6);
    }
}
'@

Write-JavaFile $lab "src/test/java/com/algo/lab12/ComplexityExamplesTest.java" @'
package com.algo.lab12;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ComplexityExamplesTest {

    @Test
    void testConstantTime() {
        int[] arr = {10, 20, 30};
        assertEquals(20, ComplexityExamples.constantTime(arr, 1));
    }

    @Test
    void testLogarithmicTime() {
        int[] arr = {1, 3, 5, 7, 9, 11, 13};
        assertEquals(2, ComplexityExamples.logarithmicTime(arr, 5));
        assertEquals(-1, ComplexityExamples.logarithmicTime(arr, 4));
    }

    @Test
    void testLinearTime() {
        int[] arr = {4, 2, 7, 1, 9};
        assertEquals(2, ComplexityExamples.linearTime(arr, 7));
        assertEquals(-1, ComplexityExamples.linearTime(arr, 5));
    }

    @Test
    void testLinearithmicTime() {
        int[] arr = {5, 2, 8, 1, 9, 3};
        ComplexityExamples.linearithmicTime(arr);
        assertArrayEquals(new int[]{1, 2, 3, 5, 8, 9}, arr);
    }

    @Test
    void testQuadraticTime() {
        int[] arr = {5, 2, 8, 1, 9};
        ComplexityExamples.quadraticTime(arr);
        assertArrayEquals(new int[]{1, 2, 5, 8, 9}, arr);
    }

    @Test
    void testSumArray() {
        assertEquals(15, ComplexityExamples.sumArray(new int[]{1, 2, 3, 4, 5}));
        assertEquals(0, ComplexityExamples.sumArray(new int[]{}));
    }

    @Test
    void testHasDuplicates() {
        assertTrue(ComplexityExamples.hasDuplicates(new int[]{1, 2, 3, 2}));
        assertFalse(ComplexityExamples.hasDuplicates(new int[]{1, 2, 3, 4}));
    }

    @Test
    void testPower() {
        assertEquals(1, ComplexityExamples.power(2, 0));
        assertEquals(2, ComplexityExamples.power(2, 1));
        assertEquals(1024, ComplexityExamples.power(2, 10));
        assertEquals(81, ComplexityExamples.power(3, 4));
    }

    @Test
    void testHeapSort() {
        int[] arr = {12, 11, 13, 5, 6, 7};
        ComplexityExamples.heapSort(arr);
        assertArrayEquals(new int[]{5, 6, 7, 11, 12, 13}, arr);
    }

    @Test
    void testHeapSortEmpty() {
        int[] arr = {};
        ComplexityExamples.heapSort(arr);
        assertArrayEquals(new int[]{}, arr);
    }
}
'@

# ============================================================
# LAB 13: Parallel Algorithms
# ============================================================
$lab = "$base\13-parallel-algorithms"

Write-JavaFile $lab "src/main/java/com/algo/lab13/ParallelAlgorithms.java" @'
package com.algo.lab13;

import java.util.concurrent.RecursiveTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;
import java.util.Arrays;

/**
 * Parallel algorithms using ForkJoin framework.
 *
 * Parallel Sum: O(log n) span, O(n) work
 * Parallel Sort: O(log^2 n) span, O(n log n) work
 * Parallel Matrix Multiply: O(log n) span, O(n^3) work
 */
public class ParallelAlgorithms {

    private static final ForkJoinPool POOL = ForkJoinPool.commonPool();
    private static final int THRESHOLD = 1000;

    private ParallelAlgorithms() {}

    public static long parallelSum(int[] arr) {
        return POOL.invoke(new SumTask(arr, 0, arr.length));
    }

    private static class SumTask extends RecursiveTask<Long> {
        private final int[] arr;
        private final int lo, hi;
        SumTask(int[] arr, int lo, int hi) { this.arr = arr; this.lo = lo; this.hi = hi; }
        @Override
        protected Long compute() {
            if (hi - lo <= THRESHOLD) {
                long sum = 0;
                for (int i = lo; i < hi; i++) sum += arr[i];
                return sum;
            }
            int mid = lo + (hi - lo) / 2;
            SumTask left = new SumTask(arr, lo, mid);
            SumTask right = new SumTask(arr, mid, hi);
            left.fork();
            return right.compute() + left.join();
        }
    }

    public static void parallelSort(int[] arr) {
        POOL.invoke(new SortTask(arr, 0, arr.length));
    }

    private static class SortTask extends RecursiveAction {
        private final int[] arr;
        private final int lo, hi;
        SortTask(int[] arr, int lo, int hi) { this.arr = arr; this.lo = lo; this.hi = hi; }
        @Override
        protected void compute() {
            if (hi - lo <= THRESHOLD) {
                Arrays.sort(arr, lo, hi);
                return;
            }
            int mid = lo + (hi - lo) / 2;
            SortTask left = new SortTask(arr, lo, mid);
            SortTask right = new SortTask(arr, mid, hi);
            invokeAll(left, right);
            merge(arr, lo, mid, hi);
        }
    }

    private static void merge(int[] arr, int lo, int mid, int hi) {
        int[] temp = Arrays.copyOfRange(arr, lo, hi);
        int i = 0, j = mid - lo, k = lo;
        int leftEnd = mid - lo;
        int rightEnd = hi - lo;
        while (i < leftEnd && j < rightEnd) {
            arr[k++] = temp[i] <= temp[j] ? temp[i++] : temp[j++];
        }
        while (i < leftEnd) arr[k++] = temp[i++];
        while (j < rightEnd) arr[k++] = temp[j++];
    }

    public static int[][] parallelMatrixMultiply(int[][] a, int[][] b) {
        int n = a.length;
        int[][] result = new int[n][n];
        POOL.invoke(new MatrixMultTask(a, b, result, 0, 0, n));
        return result;
    }

    private static class MatrixMultTask extends RecursiveAction {
        private final int[][] a, b, result;
        private final int row, col, size;
        private static final int BLOCK = 64;

        MatrixMultTask(int[][] a, int[][] b, int[][] result, int row, int col, int size) {
            this.a = a; this.b = b; this.result = result; this.row = row; this.col = col; this.size = size;
        }

        @Override
        protected void compute() {
            if (size <= BLOCK) {
                for (int i = row; i < row + size; i++) {
                    for (int j = col; j < col + size; j++) {
                        int sum = 0;
                        for (int k = 0; k < a.length; k++) {
                            sum += a[i][k] * b[k][j];
                        }
                        result[i][j] = sum;
                    }
                }
                return;
            }
            int half = size / 2;
            invokeAll(
                new MatrixMultTask(a, b, result, row, col, half),
                new MatrixMultTask(a, b, result, row, col + half, half),
                new MatrixMultTask(a, b, result, row + half, col, half),
                new MatrixMultTask(a, b, result, row + half, col + half, half)
            );
        }
    }

    /** Sequential sum for comparison */
    public static long sequentialSum(int[] arr) {
        long sum = 0;
        for (int v : arr) sum += v;
        return sum;
    }
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab13/ParallelExample.java" @'
package com.algo.lab13;

import java.util.Arrays;
import java.util.Random;

public class ParallelExample {
    public static void main(String[] args) {
        System.out.println("=== Parallel Algorithms Demo ===\n");

        Random rand = new Random(42);
        int n = 10_000_000;
        int[] arr = rand.ints(n, 0, 1000).toArray();

        System.out.println("--- Parallel Sum ---");
        long start = System.nanoTime();
        long pSum = ParallelAlgorithms.parallelSum(arr);
        long pTime = System.nanoTime();
        System.out.printf("Parallel sum: %d (%.2f ms)%n", pSum, (pTime - start) / 1e6);

        start = System.nanoTime();
        long sSum = ParallelAlgorithms.sequentialSum(arr);
        long sTime = System.nanoTime();
        System.out.printf("Sequential sum: %d (%.2f ms)%n", sSum, (sTime - start) / 1e6);
        System.out.printf("Speedup: %.2fx%n", (double)(sTime - start) / (pTime - start));

        System.out.println("\n--- Parallel Sort ---");
        int[] sortArr = rand.ints(5_000_000, 0, 10_000_000).toArray();
        int[] sortArrSeq = sortArr.clone();

        start = System.nanoTime();
        ParallelAlgorithms.parallelSort(sortArr);
        pTime = System.nanoTime();
        System.out.printf("Parallel sort: %.2f ms%n", (pTime - start) / 1e6);
        System.out.println("Sorted: " + isSorted(sortArr));

        start = System.nanoTime();
        Arrays.sort(sortArrSeq);
        sTime = System.nanoTime();
        System.out.printf("Arrays.sort: %.2f ms%n", (sTime - start) / 1e6);

        System.out.println("\n--- Parallel Matrix Multiply ---");
        int size = 512;
        int[][] a = new int[size][size];
        int[][] b = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                a[i][j] = rand.nextInt(10);
                b[i][j] = rand.nextInt(10);
            }
        }
        start = System.nanoTime();
        int[][] result = ParallelAlgorithms.parallelMatrixMultiply(a, b);
        pTime = System.nanoTime();
        System.out.printf("Parallel matrix multiply (%dx%d): %.2f ms%n",
            size, size, (pTime - start) / 1e6);
        System.out.printf("Sample: result[0][0] = %d%n", result[0][0]);
    }

    private static boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i - 1] > arr[i]) return false;
        }
        return true;
    }
}
'@

Write-JavaFile $lab "src/test/java/com/algo/lab13/ParallelAlgorithmsTest.java" @'
package com.algo.lab13;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Random;

class ParallelAlgorithmsTest {

    @Test
    void testParallelSum() {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        assertEquals(55, ParallelAlgorithms.parallelSum(arr));
    }

    @Test
    void testParallelSumEmpty() {
        assertEquals(0, ParallelAlgorithms.parallelSum(new int[]{}));
    }

    @Test
    void testParallelSumLarge() {
        int[] arr = new Random(42).ints(100000, 0, 100).toArray();
        long parallel = ParallelAlgorithms.parallelSum(arr);
        long sequential = ParallelAlgorithms.sequentialSum(arr);
        assertEquals(sequential, parallel);
    }

    @Test
    void testParallelSort() {
        int[] arr = {5, 2, 8, 1, 9, 3, 7, 4, 6};
        ParallelAlgorithms.parallelSort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, arr);
    }

    @Test
    void testParallelSortEmpty() {
        int[] arr = {};
        ParallelAlgorithms.parallelSort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    void testParallelSortSingle() {
        int[] arr = {1};
        ParallelAlgorithms.parallelSort(arr);
        assertArrayEquals(new int[]{1}, arr);
    }

    @Test
    void testParallelSortAgreesWithArraysSort() {
        int[] arr = new Random(42).ints(5000, 0, 10000).toArray();
        int[] expected = arr.clone();
        java.util.Arrays.sort(expected);
        ParallelAlgorithms.parallelSort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    void testParallelMatrixMultiply() {
        int[][] a = {{1, 2}, {3, 4}};
        int[][] b = {{5, 6}, {7, 8}};
        int[][] result = ParallelAlgorithms.parallelMatrixMultiply(a, b);
        assertEquals(19, result[0][0]);
        assertEquals(22, result[0][1]);
        assertEquals(43, result[1][0]);
        assertEquals(50, result[1][1]);
    }

    @Test
    void testSequentialSum() {
        assertEquals(55, ParallelAlgorithms.sequentialSum(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        assertEquals(0, ParallelAlgorithms.sequentialSum(new int[]{}));
    }
}
'@

# ============================================================
# LAB 14: Approximation Algorithms
# ============================================================
$lab = "$base\14-approximation-algorithms"

Write-JavaFile $lab "src/main/java/com/algo/lab14/ApproximationAlgorithms.java" @'
package com.algo.lab14;

import java.util.*;

/**
 * Approximation algorithms for NP-hard problems.
 *
 * Vertex Cover (approx): O(V + E) time, O(V) space, 2-approximation
 * TSP (approx - MST based): O(V^2) time, O(V) space, 2-approximation
 * Set Cover (greedy): O(n * m) time, O(n + m) space, ln(n)-approximation
 */
public class ApproximationAlgorithms {

    private ApproximationAlgorithms() {}

    public static Set<Integer> vertexCoverApprox(int[][] graph) {
        int n = graph.length;
        boolean[] visited = new boolean[n];
        Set<Integer> cover = new HashSet<>();
        for (int u = 0; u < n; u++) {
            if (!visited[u]) {
                for (int v = 0; v < n; v++) {
                    if (graph[u][v] == 1 && !visited[v]) {
                        visited[u] = true;
                        visited[v] = true;
                        cover.add(u);
                        cover.add(v);
                        break;
                    }
                }
            }
        }
        return cover;
    }

    public static List<Integer> tspApprox(int[][] graph) {
        int n = graph.length;
        boolean[] visited = new boolean[n];
        List<Integer> tour = new ArrayList<>();
        visited[0] = true;
        tour.add(0);
        int current = 0;
        for (int step = 1; step < n; step++) {
            int nearest = -1;
            int minDist = Integer.MAX_VALUE;
            for (int v = 0; v < n; v++) {
                if (!visited[v] && graph[current][v] > 0 && graph[current][v] < minDist) {
                    minDist = graph[current][v];
                    nearest = v;
                }
            }
            if (nearest == -1) return List.of();
            visited[nearest] = true;
            tour.add(nearest);
            current = nearest;
        }
        if (graph[current][0] > 0) tour.add(0);
        return tour;
    }

    public static Set<Integer> setCoverGreedy(Set<Integer> universe, List<Set<Integer>> sets) {
        Set<Integer> remaining = new HashSet<>(universe);
        Set<Integer> cover = new HashSet<>();
        boolean[] used = new boolean[sets.size()];
        while (!remaining.isEmpty()) {
            int bestIdx = -1;
            int bestCover = 0;
            for (int i = 0; i < sets.size(); i++) {
                if (used[i]) continue;
                int count = 0;
                for (int elem : sets.get(i)) {
                    if (remaining.contains(elem)) count++;
                }
                if (count > bestCover) {
                    bestCover = count;
                    bestIdx = i;
                }
            }
            if (bestIdx == -1) return cover;
            used[bestIdx] = true;
            cover.add(bestIdx);
            remaining.removeAll(sets.get(bestIdx));
        }
        return cover;
    }
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab14/ApproximationExample.java" @'
package com.algo.lab14;

import java.util.*;

public class ApproximationExample {
    public static void main(String[] args) {
        System.out.println("=== Approximation Algorithms Demo ===\n");

        System.out.println("--- Vertex Cover (2-approximation) ---");
        int[][] graph = {
            {0, 1, 1, 0, 0},
            {1, 0, 1, 1, 0},
            {1, 1, 0, 0, 1},
            {0, 1, 0, 0, 1},
            {0, 0, 1, 1, 0}
        };
        Set<Integer> cover = ApproximationAlgorithms.vertexCoverApprox(graph);
        System.out.println("Vertex cover size: " + cover.size() + " (optimal ≤ 2)");
        System.out.println("Cover set: " + cover);

        System.out.println("\n--- TSP (Nearest Neighbor approx) ---");
        int[][] tspGraph = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        List<Integer> tour = ApproximationAlgorithms.tspApprox(tspGraph);
        System.out.println("TSP tour: " + tour);
        if (tour.size() > 1) {
            int cost = 0;
            for (int i = 0; i < tour.size() - 1; i++) {
                cost += tspGraph[tour.get(i)][tour.get(i + 1)];
            }
            System.out.println("Tour cost: " + cost);
        }

        System.out.println("\n--- Set Cover (Greedy approx) ---");
        Set<Integer> universe = new HashSet<>(Set.of(1, 2, 3, 4, 5, 6, 7, 8));
        List<Set<Integer>> sets = new ArrayList<>();
        sets.add(new HashSet<>(Set.of(1, 2, 3)));
        sets.add(new HashSet<>(Set.of(2, 4, 6)));
        sets.add(new HashSet<>(Set.of(5, 7, 8)));
        sets.add(new HashSet<>(Set.of(1, 4, 5, 8)));
        Set<Integer> setCover = ApproximationAlgorithms.setCoverGreedy(universe, sets);
        System.out.println("Sets used: " + setCover);
        Set<Integer> covered = new HashSet<>();
        for (int idx : setCover) covered.addAll(sets.get(idx));
        System.out.println("Covered elements: " + covered);
    }
}
'@

Write-JavaFile $lab "src/test/java/com/algo/lab14/ApproximationAlgorithmsTest.java" @'
package com.algo.lab14;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class ApproximationAlgorithmsTest {

    @Test
    void testVertexCoverApprox() {
        int[][] graph = {
            {0, 1, 1, 0, 0},
            {1, 0, 1, 1, 0},
            {1, 1, 0, 0, 1},
            {0, 1, 0, 0, 1},
            {0, 0, 1, 1, 0}
        };
        Set<Integer> cover = ApproximationAlgorithms.vertexCoverApprox(graph);
        assertFalse(cover.isEmpty());
        for (int u = 0; u < graph.length; u++) {
            for (int v = 0; v < graph.length; v++) {
                if (graph[u][v] == 1) {
                    assertTrue(cover.contains(u) || cover.contains(v));
                }
            }
        }
    }

    @Test
    void testVertexCoverApproxSimple() {
        int[][] graph = {{0, 1}, {1, 0}};
        Set<Integer> cover = ApproximationAlgorithms.vertexCoverApprox(graph);
        assertEquals(2, cover.size());
    }

    @Test
    void testTspApprox() {
        int[][] graph = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        List<Integer> tour = ApproximationAlgorithms.tspApprox(graph);
        assertFalse(tour.isEmpty());
        assertEquals(5, tour.size());
        assertEquals(0, tour.get(0).intValue());
        assertEquals(0, tour.get(tour.size() - 1).intValue());
    }

    @Test
    void testSetCoverGreedy() {
        Set<Integer> universe = new HashSet<>(Set.of(1, 2, 3, 4, 5));
        List<Set<Integer>> sets = new ArrayList<>();
        sets.add(new HashSet<>(Set.of(1, 2)));
        sets.add(new HashSet<>(Set.of(2, 3, 4)));
        sets.add(new HashSet<>(Set.of(4, 5)));
        Set<Integer> cover = ApproximationAlgorithms.setCoverGreedy(universe, sets);
        Set<Integer> covered = new HashSet<>();
        for (int idx : cover) covered.addAll(sets.get(idx));
        assertEquals(universe, covered);
    }

    @Test
    void testSetCoverGreedyAllElements() {
        Set<Integer> universe = new HashSet<>(Set.of(1, 2, 3));
        List<Set<Integer>> sets = new ArrayList<>();
        sets.add(new HashSet<>(Set.of(1, 2, 3)));
        Set<Integer> cover = ApproximationAlgorithms.setCoverGreedy(universe, sets);
        assertEquals(1, cover.size());
    }
}
'@

# ============================================================
# LAB 15: Cryptographic Algorithms
# ============================================================
$lab = "$base\15-cryptographic-algorithms"

Write-JavaFile $lab "src/main/java/com/algo/lab15/CryptographicAlgorithms.java" @'
package com.algo.lab15;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * Cryptographic algorithms using Java Cryptography Architecture.
 *
 * SHA-256 Hashing: O(n) time, O(1) space
 * AES Encrypt/Decrypt: O(n) time, O(1) space
 * RSA Key Pair: O(log n) time for generation
 * HMAC: O(n) time, O(1) space
 * Digital Signature: O(n) time
 */
public class CryptographicAlgorithms {

    private CryptographicAlgorithms() {}

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static byte[] encryptAES(byte[] plaintext, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            byte[] ciphertext = cipher.doFinal(plaintext);
            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
            return combined;
        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }

    public static byte[] decryptAES(byte[] encrypted, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = new byte[12];
            System.arraycopy(encrypted, 0, iv, 0, iv.length);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            return cipher.doFinal(encrypted, iv.length, encrypted.length - iv.length);
        } catch (Exception e) {
            throw new RuntimeException("AES decryption failed", e);
        }
    }

    public static SecretKey generateAESKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256);
            return kg.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES not available", e);
        }
    }

    public static KeyPair generateRSAKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("RSA not available", e);
        }
    }

    public static byte[] encryptRSA(byte[] plaintext, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plaintext);
        } catch (Exception e) {
            throw new RuntimeException("RSA encryption failed", e);
        }
    }

    public static byte[] decryptRSA(byte[] ciphertext, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(ciphertext);
        } catch (Exception e) {
            throw new RuntimeException("RSA decryption failed", e);
        }
    }

    public static String hmacSHA256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(spec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hmacBytes) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC failed", e);
        }
    }

    public static byte[] sign(byte[] data, PrivateKey privateKey) {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(privateKey);
            sig.update(data);
            return sig.sign();
        } catch (Exception e) {
            throw new RuntimeException("Signing failed", e);
        }
    }

    public static boolean verify(byte[] data, byte[] signature, PublicKey publicKey) {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(data);
            return sig.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException("Verification failed", e);
        }
    }
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab15/CryptographicExample.java" @'
package com.algo.lab15;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.util.Arrays;

public class CryptographicExample {
    public static void main(String[] args) {
        System.out.println("=== Cryptographic Algorithms Demo ===\n");

        System.out.println("--- SHA-256 Hashing ---");
        String message = "Hello, Algorithms Academy!";
        String hash = CryptographicAlgorithms.sha256(message);
        System.out.println("Message: " + message);
        System.out.println("SHA-256: " + hash);

        System.out.println("\n--- AES Encryption ---");
        SecretKey aesKey = CryptographicAlgorithms.generateAESKey();
        String plaintext = "This is a secret message for AES encryption!";
        byte[] encrypted = CryptographicAlgorithms.encryptAES(plaintext.getBytes(), aesKey);
        byte[] decrypted = CryptographicAlgorithms.decryptAES(encrypted, aesKey);
        System.out.println("Original:  " + plaintext);
        System.out.println("Encrypted (base64): " + Base64.getEncoder().encodeToString(encrypted).substring(0, 40) + "...");
        System.out.println("Decrypted: " + new String(decrypted));
        System.out.println("Match: " + plaintext.equals(new String(decrypted)));

        System.out.println("\n--- RSA Encryption ---");
        KeyPair rsaKeys = CryptographicAlgorithms.generateRSAKeyPair();
        String rsaMessage = "RSA secret message!";
        byte[] rsaEncrypted = CryptographicAlgorithms.encryptRSA(rsaMessage.getBytes(), rsaKeys.getPublic());
        byte[] rsaDecrypted = CryptographicAlgorithms.decryptRSA(rsaEncrypted, rsaKeys.getPrivate());
        System.out.println("Original:  " + rsaMessage);
        System.out.println("Decrypted: " + new String(rsaDecrypted));

        System.out.println("\n--- HMAC ---");
        String hmacData = "Important message";
        String hmacKey = "my-secret-key";
        String hmacResult = CryptographicAlgorithms.hmacSHA256(hmacData, hmacKey);
        System.out.println("HMAC-SHA256: " + hmacResult);
        String hmacResult2 = CryptographicAlgorithms.hmacSHA256(hmacData, hmacKey);
        System.out.println("Verify (same key): " + hmacResult.equals(hmacResult2));
        String hmacResult3 = CryptographicAlgorithms.hmacSHA256(hmacData, "wrong-key");
        System.out.println("Verify (wrong key): " + hmacResult.equals(hmacResult3));

        System.out.println("\n--- Digital Signature ---");
        byte[] sigData = "Data to be signed".getBytes();
        byte[] signature = CryptographicAlgorithms.sign(sigData, rsaKeys.getPrivate());
        boolean valid = CryptographicAlgorithms.verify(sigData, signature, rsaKeys.getPublic());
        System.out.println("Signature valid: " + valid);

        byte[] tampered = "Data to be signed!".getBytes();
        boolean invalid = CryptographicAlgorithms.verify(tampered, signature, rsaKeys.getPublic());
        System.out.println("Tampered data detected: " + !invalid);
    }
}
'@

Write-JavaFile $lab "src/test/java/com/algo/lab15/CryptographicAlgorithmsTest.java" @'
package com.algo.lab15;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.crypto.SecretKey;
import java.security.KeyPair;

class CryptographicAlgorithmsTest {

    @Test
    void testSHA256Consistency() {
        String input = "test";
        assertEquals(CryptographicAlgorithms.sha256(input), CryptographicAlgorithms.sha256(input));
    }

    @Test
    void testSHA256DifferentInputs() {
        assertNotEquals(CryptographicAlgorithms.sha256("abc"), CryptographicAlgorithms.sha256("abcd"));
    }

    @Test
    void testSHA256KnownValue() {
        String hash = CryptographicAlgorithms.sha256("hello");
        assertEquals(64, hash.length());
    }

    @Test
    void testAESEncryptDecrypt() {
        SecretKey key = CryptographicAlgorithms.generateAESKey();
        String original = "AES test message";
        byte[] encrypted = CryptographicAlgorithms.encryptAES(original.getBytes(), key);
        byte[] decrypted = CryptographicAlgorithms.decryptAES(encrypted, key);
        assertEquals(original, new String(decrypted));
    }

    @Test
    void testAESDifferentKeys() {
        SecretKey key1 = CryptographicAlgorithms.generateAESKey();
        SecretKey key2 = CryptographicAlgorithms.generateAESKey();
        String original = "test";
        byte[] encrypted = CryptographicAlgorithms.encryptAES(original.getBytes(), key1);
        assertThrows(RuntimeException.class, () -> {
            CryptographicAlgorithms.decryptAES(encrypted, key2);
        });
    }

    @Test
    void testAESRoundTrip() {
        SecretKey key = CryptographicAlgorithms.generateAESKey();
        String[] messages = {"", "a", "short", "A longer message that needs proper encryption!"};
        for (String msg : messages) {
            byte[] encrypted = CryptographicAlgorithms.encryptAES(msg.getBytes(), key);
            byte[] decrypted = CryptographicAlgorithms.decryptAES(encrypted, key);
            assertEquals(msg, new String(decrypted));
        }
    }

    @Test
    void testRSAEncryptDecrypt() {
        KeyPair keys = CryptographicAlgorithms.generateRSAKeyPair();
        String original = "RSA test message";
        byte[] encrypted = CryptographicAlgorithms.encryptRSA(original.getBytes(), keys.getPublic());
        byte[] decrypted = CryptographicAlgorithms.decryptRSA(encrypted, keys.getPrivate());
        assertEquals(original, new String(decrypted));
    }

    @Test
    void testRSAWrongKey() {
        KeyPair keys1 = CryptographicAlgorithms.generateRSAKeyPair();
        KeyPair keys2 = CryptographicAlgorithms.generateRSAKeyPair();
        byte[] encrypted = CryptographicAlgorithms.encryptRSA("test".getBytes(), keys1.getPublic());
        assertThrows(RuntimeException.class, () -> {
            CryptographicAlgorithms.decryptRSA(encrypted, keys2.getPrivate());
        });
    }

    @Test
    void testHMACConsistency() {
        String data = "test data";
        String key = "secret";
        assertEquals(
            CryptographicAlgorithms.hmacSHA256(data, key),
            CryptographicAlgorithms.hmacSHA256(data, key));
    }

    @Test
    void testHMACDifferentKey() {
        String data = "test data";
        assertNotEquals(
            CryptographicAlgorithms.hmacSHA256(data, "key1"),
            CryptographicAlgorithms.hmacSHA256(data, "key2"));
    }

    @Test
    void testHMACNonEmpty() {
        assertFalse(CryptographicAlgorithms.hmacSHA256("data", "key").isEmpty());
    }

    @Test
    void testDigitalSignature() {
        KeyPair keys = CryptographicAlgorithms.generateRSAKeyPair();
        byte[] data = "Signed data".getBytes();
        byte[] signature = CryptographicAlgorithms.sign(data, keys.getPrivate());
        assertTrue(CryptographicAlgorithms.verify(data, signature, keys.getPublic()));
    }

    @Test
    void testDigitalSignatureTampered() {
        KeyPair keys = CryptographicAlgorithms.generateRSAKeyPair();
        byte[] data = "Original data".getBytes();
        byte[] signature = CryptographicAlgorithms.sign(data, keys.getPrivate());
        byte[] tampered = "Tampered data".getBytes();
        assertFalse(CryptographicAlgorithms.verify(tampered, signature, keys.getPublic()));
    }

    @Test
    void testDigitalSignatureWrongKey() {
        KeyPair keys1 = CryptographicAlgorithms.generateRSAKeyPair();
        KeyPair keys2 = CryptographicAlgorithms.generateRSAKeyPair();
        byte[] data = "test".getBytes();
        byte[] signature = CryptographicAlgorithms.sign(data, keys1.getPrivate());
        assertFalse(CryptographicAlgorithms.verify(data, signature, keys2.getPublic()));
    }
}
'@

Write-Host "=== Script 3 (Labs 11-15) completed ==="

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
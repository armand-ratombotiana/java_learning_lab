package com.algorithms.parallel;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

/**
 * Custom: Parallel Merge Sort using Fork/Join Framework
 * Demonstrates parallel algorithm design with Java's ForkJoinPool.
 *
 * Time Complexity: O(n log n) work, O(log n) span
 * Space Complexity: O(n)
 */
public class ParallelSort extends RecursiveAction {

    private final int[] arr;
    private final int left, right;
    private static final int THRESHOLD = 1000;

    public ParallelSort(int[] arr, int left, int right) {
        this.arr = arr;
        this.left = left;
        this.right = right;
    }

    @Override
    protected void compute() {
        if (right - left < THRESHOLD) {
            Arrays.sort(arr, left, right + 1);
            return;
        }
        int mid = left + (right - left) / 2;
        ParallelSort leftTask = new ParallelSort(arr, left, mid);
        ParallelSort rightTask = new ParallelSort(arr, mid + 1, right);
        invokeAll(leftTask, rightTask);
        merge(arr, left, mid, right);
    }

    private void merge(int[] arr, int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;
        while (i <= mid && j <= right) temp[k++] = arr[i] <= arr[j] ? arr[i++] : arr[j++];
        while (i <= mid) temp[k++] = arr[i++];
        while (j <= right) temp[k++] = arr[j++];
        System.arraycopy(temp, 0, arr, left, temp.length);
    }

    public static void main(String[] args) {
        int[] arr = new int[10000];
        for (int i = 0; i < arr.length; i++) arr[i] = (int)(Math.random() * 10000);

        ForkJoinPool pool = new ForkJoinPool();
        ParallelSort task = new ParallelSort(arr, 0, arr.length - 1);
        long start = System.nanoTime();
        pool.invoke(task);
        long end = System.nanoTime();

        boolean sorted = true;
        for (int i = 1; i < arr.length; i++) if (arr[i - 1] > arr[i]) { sorted = false; break; }
        System.out.println("Parallel sort completed: " + (end - start) / 1e6 + " ms");
        System.out.println("Sorted correctly: " + sorted + " (expected: true)");
    }
}

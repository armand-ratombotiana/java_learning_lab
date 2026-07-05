package com.algo.lab09;

import java.util.*;

/**
 * Divide and Conquer algorithms.
 *
 * MergeSort: O(n log n) time, O(n) space
 * QuickSort: O(n log n) avg, O(n^2) worst, O(log n) space
 * Closest Pair: O(n log n) time, O(n) space
 * Max Subarray (Kadane): O(n) time, O(1) space
 */
public class DivideAndConquer {

    private DivideAndConquer() {}

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
            if (temp[i].compareTo(temp[j]) <= 0) arr[k++] = temp[i++];
            else arr[k++] = temp[j++];
        }
        while (i <= mid) arr[k++] = temp[i++];
        while (j <= right) arr[k++] = temp[j++];
    }

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
                T temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
            }
        }
        T temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;
        return i + 1;
    }

    public static double closestPair(Point[] points) {
        Point[] sortedByX = points.clone();
        Point[] sortedByY = points.clone();
        Arrays.sort(sortedByX, Comparator.comparingDouble(p -> p.x));
        Arrays.sort(sortedByY, Comparator.comparingDouble(p -> p.y));
        return closestPair(sortedByX, sortedByY, 0, points.length - 1);
    }

    private static double closestPair(Point[] px, Point[] py, int left, int right) {
        if (right - left <= 3) {
            double min = Double.MAX_VALUE;
            for (int i = left; i <= right; i++) {
                for (int j = i + 1; j <= right; j++) {
                    min = Math.min(min, dist(px[i], px[j]));
                }
            }
            return min;
        }
        int mid = left + (right - left) / 2;
        double midX = px[mid].x;
        Point[] pyLeft = Arrays.stream(py).filter(p -> p.x <= midX).toArray(Point[]::new);
        Point[] pyRight = Arrays.stream(py).filter(p -> p.x > midX).toArray(Point[]::new);
        double dl = closestPair(px, pyLeft, left, mid);
        double dr = closestPair(px, pyRight, mid + 1, right);
        double d = Math.min(dl, dr);
        List<Point> strip = new ArrayList<>();
        for (Point p : py) {
            if (Math.abs(p.x - midX) < d) strip.add(p);
        }
        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1; j < strip.size() && (strip.get(j).y - strip.get(i).y) < d; j++) {
                d = Math.min(d, dist(strip.get(i), strip.get(j)));
            }
        }
        return d;
    }

    private static double dist(Point a, Point b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    public record Point(double x, double y) {}

    public static int maxSubarraySum(int[] arr) {
        int maxSoFar = arr[0], maxEndingHere = arr[0];
        for (int i = 1; i < arr.length; i++) {
            maxEndingHere = Math.max(arr[i], maxEndingHere + arr[i]);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }
        return maxSoFar;
    }
}
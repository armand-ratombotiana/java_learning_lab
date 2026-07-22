package com.alglab.comparisonsorts;

import java.util.Comparator;

public class InsertionSort {

    public static <T extends Comparable<? super T>> void sort(T[] arr) {
        sort(arr, Comparator.naturalOrder());
    }

    public static <T> void sort(T[] arr, Comparator<? super T> cmp) {
        if (arr == null || arr.length < 2) {
            return;
        }
        for (int i = 1; i < arr.length; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= 0 && cmp.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    public static void sort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    public static int binaryInsertionSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return 0;
        }
        int swaps = 0;
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int lo = 0, hi = i;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (arr[mid] <= key) {
                    lo = mid + 1;
                } else {
                    hi = mid;
                }
            }
            int pos = lo;
            if (pos < i) {
                System.arraycopy(arr, pos, arr, pos + 1, i - pos);
                arr[pos] = key;
                swaps += i - pos;
            }
        }
        return swaps;
    }
}

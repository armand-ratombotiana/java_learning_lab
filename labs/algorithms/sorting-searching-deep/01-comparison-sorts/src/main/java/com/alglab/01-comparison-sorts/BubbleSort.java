package com.alglab.comparisonsorts;

public class BubbleSort {

    public static void sort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    public static void cocktailShakerSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        int start = 0, end = arr.length - 1;
        boolean swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = start; i < end; i++) {
                if (arr[i] > arr[i + 1]) {
                    int tmp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = tmp;
                    swapped = true;
                }
            }
            end--;
            if (!swapped) break;
            swapped = false;
            for (int i = end; i > start; i--) {
                if (arr[i] < arr[i - 1]) {
                    int tmp = arr[i];
                    arr[i] = arr[i - 1];
                    arr[i - 1] = tmp;
                    swapped = true;
                }
            }
            start++;
        }
    }

    public static void combSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        int n = arr.length;
        int gap = n;
        boolean swapped = true;
        while (gap > 1 || swapped) {
            gap = (int) (gap / 1.3);
            if (gap < 1) gap = 1;
            swapped = false;
            for (int i = 0; i + gap < n; i++) {
                if (arr[i] > arr[i + gap]) {
                    int tmp = arr[i];
                    arr[i] = arr[i + gap];
                    arr[i + gap] = tmp;
                    swapped = true;
                }
            }
        }
    }

    public static void gnomeSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        int pos = 0;
        while (pos < arr.length) {
            if (pos == 0 || arr[pos] >= arr[pos - 1]) {
                pos++;
            } else {
                int tmp = arr[pos];
                arr[pos] = arr[pos - 1];
                arr[pos - 1] = tmp;
                pos--;
            }
        }
    }
}

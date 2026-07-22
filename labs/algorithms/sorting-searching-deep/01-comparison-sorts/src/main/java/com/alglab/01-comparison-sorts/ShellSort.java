package com.alglab.comparisonsorts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public class ShellSort {

    @FunctionalInterface
    public interface GapSequence {
        int[] generate(int n);
    }

    public static final GapSequence SHELL_GAPS = n -> {
        List<Integer> gaps = new ArrayList<>();
        for (int g = n / 2; g > 0; g /= 2) {
            gaps.add(g);
        }
        return gaps.stream().mapToInt(Integer::intValue).toArray();
    };

    public static final GapSequence HIBBARD_GAPS = n -> {
        List<Integer> gaps = new ArrayList<>();
        for (int k = 1; (1 << k) - 1 < n; k++) {
            gaps.add((1 << k) - 1);
        }
        int[] result = new int[gaps.size()];
        for (int i = 0; i < gaps.size(); i++) {
            result[i] = gaps.get(gaps.size() - 1 - i);
        }
        return result;
    };

    public static final GapSequence SEDGEWICK_GAPS = n -> {
        List<Integer> gaps = new ArrayList<>();
        for (int k = 0; ; k++) {
            int g = (int) (Math.pow(4, k) + 3 * Math.pow(2, k - 1) + 1);
            if (k == 0) g = 1;
            if (g >= n) break;
            gaps.add(g);
        }
        int[] result = new int[gaps.size()];
        for (int i = 0; i < gaps.size(); i++) {
            result[i] = gaps.get(gaps.size() - 1 - i);
        }
        return result;
    };

    public static final GapSequence PRATT_GAPS = n -> {
        List<Integer> gaps = new ArrayList<>();
        int p = 1;
        while (p < n) {
            int q = p;
            while (q < n) {
                gaps.add(q);
                q *= 3;
            }
            p *= 2;
        }
        gaps.sort(Integer::compareTo);
        int[] result = new int[gaps.size()];
        for (int i = 0; i < gaps.size(); i++) {
            result[i] = gaps.get(gaps.size() - 1 - i);
        }
        return result;
    };

    public static void sort(int[] arr, GapSequence gapSeq) {
        if (arr == null || arr.length < 2) {
            return;
        }
        int[] gaps = gapSeq.generate(arr.length);
        for (int gap : gaps) {
            for (int i = gap; i < arr.length; i++) {
                int key = arr[i];
                int j = i;
                while (j >= gap && arr[j - gap] > key) {
                    arr[j] = arr[j - gap];
                    j -= gap;
                }
                arr[j] = key;
            }
        }
    }

    public static void shellSort(int[] arr) {
        sort(arr, SHELL_GAPS);
    }

    public static void hibbardSort(int[] arr) {
        sort(arr, HIBBARD_GAPS);
    }

    public static void sedgewickSort(int[] arr) {
        sort(arr, SEDGEWICK_GAPS);
    }

    public static void prattSort(int[] arr) {
        sort(arr, PRATT_GAPS);
    }
}

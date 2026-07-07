package com.algo.lab32;

import java.util.Arrays;

public class MeetInTheMiddle {

    public static boolean subsetSum(int[] arr, int target) {
        int n = arr.length;
        int n1 = n / 2;
        int n2 = n - n1;

        long[] sums1 = new long[1 << n1];
        for (int mask = 0; mask < (1 << n1); mask++) {
            long sum = 0;
            for (int i = 0; i < n1; i++) {
                if ((mask & (1 << i)) != 0) sum += arr[i];
            }
            sums1[mask] = sum;
        }

        long[] sums2 = new long[1 << n2];
        for (int mask = 0; mask < (1 << n2); mask++) {
            long sum = 0;
            for (int i = 0; i < n2; i++) {
                if ((mask & (1 << i)) != 0) sum += arr[n1 + i];
            }
            sums2[mask] = sum;
        }

        Arrays.sort(sums2);
        for (long s1 : sums1) {
            int idx = Arrays.binarySearch(sums2, target - s1);
            if (idx >= 0) return true;
        }
        return false;
    }
}

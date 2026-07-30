package com.math.deep.lab05;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Combinatorics {

    public static BigInteger factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) result = result.multiply(BigInteger.valueOf(i));
        return result;
    }

    public static BigInteger permutation(int n, int k) {
        if (k < 0 || k > n) return BigInteger.ZERO;
        BigInteger result = BigInteger.ONE;
        for (int i = n; i > n - k; i--) result = result.multiply(BigInteger.valueOf(i));
        return result;
    }

    public static BigInteger combination(int n, int k) {
        if (k < 0 || k > n) return BigInteger.ZERO;
        if (k > n - k) k = n - k;
        BigInteger result = BigInteger.ONE;
        for (int i = 1; i <= k; i++) {
            result = result.multiply(BigInteger.valueOf(n - k + i));
            result = result.divide(BigInteger.valueOf(i));
        }
        return result;
    }

    public static long combinationMod(int n, int k, int mod) {
        if (k < 0 || k > n) return 0;
        long[][] dp = new long[n + 1][k + 1];
        for (int i = 0; i <= n; i++) {
            dp[i][0] = 1;
            for (int j = 1; j <= Math.min(i, k); j++) {
                dp[i][j] = (dp[i - 1][j - 1] + dp[i - 1][j]) % mod;
            }
        }
        return dp[n][k];
    }

    public static long inclusionExclusion(long total, long[]... sets) {
        int n = sets.length;
        long result = 0;
        for (int mask = 1; mask < (1 << n); mask++) {
            long intersection = total;
            int bits = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    intersection = intersection / total * sets[i][0];
                    bits++;
                }
            }
            if (bits % 2 == 1) result += intersection;
            else result -= intersection;
        }
        return result;
    }

    public static List<int[]> permutations(int[] arr) {
        List<int[]> result = new ArrayList<>();
        heapPermutation(arr, arr.length, result);
        return result;
    }

    private static void heapPermutation(int[] arr, int size, List<int[]> result) {
        if (size == 1) {
            result.add(Arrays.copyOf(arr, arr.length));
            return;
        }
        for (int i = 0; i < size; i++) {
            heapPermutation(arr, size - 1, result);
            if (size % 2 == 1) {
                int tmp = arr[0];
                arr[0] = arr[size - 1];
                arr[size - 1] = tmp;
            } else {
                int tmp = arr[i];
                arr[i] = arr[size - 1];
                arr[size - 1] = tmp;
            }
        }
    }

    public static int[] nthPermutation(int n, int k) {
        int[] result = new int[k];
        List<Integer> elements = new ArrayList<>();
        for (int i = 0; i < k; i++) elements.add(i);
        BigInteger[] facts = new BigInteger[k + 1];
        for (int i = 0; i <= k; i++) facts[i] = factorial(i);
        BigInteger idx = BigInteger.valueOf(n);
        for (int i = 0; i < k; i++) {
            BigInteger blockSize = facts[k - 1 - i];
            int pos = idx.divide(blockSize).intValue();
            result[i] = elements.remove(pos);
            idx = idx.mod(blockSize);
        }
        return result;
    }

    public static BigInteger fibonacci(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        if (n <= 1) return BigInteger.valueOf(n);
        BigInteger a = BigInteger.ZERO, b = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            BigInteger c = a.add(b);
            a = b;
            b = c;
        }
        return b;
    }
}

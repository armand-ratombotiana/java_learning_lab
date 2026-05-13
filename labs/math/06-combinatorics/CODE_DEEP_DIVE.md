# Code Deep Dive: Combinatorics

```java
package com.mathacademy.combinatorics;

import java.util.*;

public class Combinatorics {
    
    public static long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException();
        long result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }
    
    public static long permutations(int n, int r) {
        if (r > n) return 0;
        return factorial(n) / factorial(n - r);
    }
    
    public static long combinations(int n, int r) {
        if (r > n) return 0;
        if (r > n - r) r = n - r;
        long result = 1;
        for (int i = 0; i < r; i++) result = result * (n - i) / (i + 1);
        return result;
    }
    
    public static List<List<Integer>> generatePermutations(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackPermutations(result, new ArrayList<>(), nums, new boolean[nums.length]);
        return result;
    }
    
    private static void backtrackPermutations(List<List<Integer>> result, List<Integer> current, int[] nums, boolean[] used) {
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            current.add(nums[i]);
            backtrackPermutations(result, current, nums, used);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }
    
    public static List<List<Integer>> generateCombinations(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackCombinations(result, new ArrayList<>(), n, k, 1);
        return result;
    }
    
    private static void backtrackCombinations(List<List<Integer>> result, List<Integer> current, int n, int k, int start) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i <= n; i++) {
            current.add(i);
            backtrackCombinations(result, current, n, k, i + 1);
            current.remove(current.size() - 1);
        }
    }
    
    public static long countDerangements(int n) {
        long[] d = new long[n + 1];
        d[0] = 1; d[1] = 0;
        for (int i = 2; i <= n; i++) d[i] = (i - 1) * (d[i - 1] + d[i - 2]);
        return d[n];
    }
    
    public static long countPartitions(int n) {
        long[] p = new long[n + 1];
        p[0] = 1;
        for (int i = 1; i <= n; i++) {
            for (int j = i; j <= n; j++) p[j] += p[j - i];
        }
        return p[n];
    }
}
```
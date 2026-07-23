package com.algorithms.exactexp;

import java.util.*;

/**
 * Custom: Exact Exponential Algorithms
 * Meet-in-the-middle, subset sum, inclusion-exclusion.
 *
 * Time Complexity: O(2^(n/2)) for meet-in-the-middle
 * Space Complexity: O(2^(n/2))
 */
public class ExactExponential {

    // Subset Sum using Meet-in-the-Middle
    public boolean subsetSum(int[] nums, int target) {
        int n = nums.length;
        List<Integer> leftSums = generateAllSums(nums, 0, n / 2);
        List<Integer> rightSums = generateAllSums(nums, n / 2, n);
        Collections.sort(rightSums);

        for (int sum : leftSums) {
            int needed = target - sum;
            if (Collections.binarySearch(rightSums, needed) >= 0) return true;
        }
        return false;
    }

    private List<Integer> generateAllSums(int[] nums, int start, int end) {
        List<Integer> sums = new ArrayList<>();
        int len = end - start;
        for (int mask = 0; mask < (1 << len); mask++) {
            int sum = 0;
            for (int i = 0; i < len; i++) {
                if ((mask & (1 << i)) != 0) sum += nums[start + i];
            }
            sums.add(sum);
        }
        return sums;
    }

    public static void main(String[] args) {
        ExactExponential ee = new ExactExponential();
        int[] nums = { 3, 5, 7, 11, 13 };
        System.out.println("Subset sum to 20: " + ee.subsetSum(nums, 20) + " (expected: true, 7+13)");
        System.out.println("Subset sum to 33: " + ee.subsetSum(nums, 33) + " (expected: false)");
        System.out.println("Subset sum to 0: " + ee.subsetSum(nums, 0) + " (expected: true, empty set)");
        System.out.println("Subset sum to 3: " + ee.subsetSum(nums, 3) + " (expected: true)");

        // Performance note
        System.out.println("Meet-in-the-middle reduces 2^n to 2^(n/2)");
        System.out.println("For n=40: 2^40 ≈ 10^12 → 2^20 ≈ 10^6 (feasible)");
    }
}

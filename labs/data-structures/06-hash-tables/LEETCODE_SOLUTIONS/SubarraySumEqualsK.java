package com.leetcode.hashtable;

import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 560: Subarray Sum Equals K
 * https://leetcode.com/problems/subarray-sum-equals-k/
 *
 * Given an array of integers nums and an integer k, return the total number
 * of subarrays whose sum equals k.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */
public class SubarraySumEqualsK {

    /**
     * Approach 1 (Optimal): Prefix Sum + HashMap
     * Track running sum and count of previous prefix sums.
     * For each prefix sum, check if sum - k was seen before.
     * Time: O(n), Space: O(n)
     */
    public int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> prefixSumCount = new HashMap<>();
        prefixSumCount.put(0, 1);
        int sum = 0;
        int count = 0;

        for (int num : nums) {
            sum += num;
            count += prefixSumCount.getOrDefault(sum - k, 0);
            prefixSumCount.put(sum, prefixSumCount.getOrDefault(sum, 0) + 1);
        }

        return count;
    }

    /**
     * Approach 2: Brute Force
     * Check every subarray.
     * Time: O(n^2), Space: O(1)
     */

    public static void main(String[] args) {
        SubarraySumEqualsK ssek = new SubarraySumEqualsK();

        System.out.println("Test 1: " + ssek.subarraySum(new int[] { 1, 1, 1 }, 2) + " (expected: 2)");
        System.out.println("Test 2: " + ssek.subarraySum(new int[] { 1, 2, 3 }, 3) + " (expected: 2)");
        System.out.println("Test 3: " + ssek.subarraySum(new int[] { 1, -1, 0 }, 0) + " (expected: 3)");
        System.out.println("Test 4: " + ssek.subarraySum(new int[] {}, 0) + " (expected: 0)");
        System.out.println("Test 5: " + ssek.subarraySum(new int[] { 1 }, 0) + " (expected: 0)");
    }
}

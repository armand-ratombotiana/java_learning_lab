package com.leetcode.arrays;

import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 1: Two Sum
 * https://leetcode.com/problems/two-sum/
 *
 * Given an array of integers nums and an integer target,
 * return indices of the two numbers such that they add up to target.
 *
 * Time Complexity: O(n) - single pass through the array
 * Space Complexity: O(n) - HashMap stores up to n elements
 */
public class TwoSum {

    /**
     * Approach 1 (Optimal): HashMap
     * For each element, check if complement (target - nums[i]) exists in map.
     * Time: O(n), Space: O(n)
     */
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[] { map.get(complement), i };
            }
            map.put(nums[i], i);
        }
        return new int[] { -1, -1 };
    }

    /**
     * Approach 2: Brute Force
     * Check every pair.
     * Time: O(n^2), Space: O(1)
     */
    public int[] twoSumBruteForce(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[] { i, j };
                }
            }
        }
        return new int[] { -1, -1 };
    }

    public static void main(String[] args) {
        TwoSum ts = new TwoSum();

        // Test case 1: Basic
        int[] result1 = ts.twoSum(new int[] { 2, 7, 11, 15 }, 9);
        System.out.println("Test 1: [" + result1[0] + ", " + result1[1] + "] (expected: [0, 1])");

        // Test case 2: Unsorted with duplicates
        int[] result2 = ts.twoSum(new int[] { 3, 2, 4 }, 6);
        System.out.println("Test 2: [" + result2[0] + ", " + result2[1] + "] (expected: [1, 2])");

        // Test case 3: Negative numbers
        int[] result3 = ts.twoSum(new int[] { -3, 4, 3, 90 }, 0);
        System.out.println("Test 3: [" + result3[0] + ", " + result3[1] + "] (expected: [0, 2])");

        // Test case 4: Brute force verification
        int[] result4 = ts.twoSumBruteForce(new int[] { 2, 7, 11, 15 }, 9);
        System.out.println("Test 4 (BF): [" + result4[0] + ", " + result4[1] + "] (expected: [0, 1])");
    }
}

package com.leetcode.hashtable;

import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 1: Two Sum
 * https://leetcode.com/problems/two-sum/
 *
 * Given an array of integers nums and an integer target,
 * return indices of the two numbers that add up to target.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */
public class TwoSum {

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

    public static void main(String[] args) {
        TwoSum ts = new TwoSum();
        int[] r1 = ts.twoSum(new int[] { 2, 7, 11, 15 }, 9);
        System.out.println("Test 1: [" + r1[0] + "," + r1[1] + "] (expected: [0,1])");

        int[] r2 = ts.twoSum(new int[] { 3, 2, 4 }, 6);
        System.out.println("Test 2: [" + r2[0] + "," + r2[1] + "] (expected: [1,2])");

        int[] r3 = ts.twoSum(new int[] { 3, 3 }, 6);
        System.out.println("Test 3: [" + r3[0] + "," + r3[1] + "] (expected: [0,1])");
    }
}

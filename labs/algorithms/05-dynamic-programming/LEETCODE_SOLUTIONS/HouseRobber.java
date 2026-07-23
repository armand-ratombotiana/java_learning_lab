package com.algorithms.dp;

/**
 * LeetCode 198: House Robber
 * https://leetcode.com/problems/house-robber/
 *
 * Given an array of money in houses, find max amount you can rob
 * without robbing adjacent houses.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class HouseRobber {

    /**
     * Approach 1 (Optimal): DP with O(1) space
     * rob = max(rob[i-1], rob[i-2] + nums[i])
     */
    public int rob(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        int prev2 = 0, prev1 = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int curr = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = curr;
        }
        return prev1;
    }

    public static void main(String[] args) {
        HouseRobber hr = new HouseRobber();
        System.out.println("Test 1: " + hr.rob(new int[] { 1, 2, 3, 1 }) + " (expected: 4)");
        System.out.println("Test 2: " + hr.rob(new int[] { 2, 7, 9, 3, 1 }) + " (expected: 12)");
        System.out.println("Test 3: " + hr.rob(new int[] { 2, 1, 1, 2 }) + " (expected: 4)");
        System.out.println("Test 4: " + hr.rob(new int[] { 5 }) + " (expected: 5)");
        System.out.println("Test 5: " + hr.rob(new int[] {}) + " (expected: 0)");
    }
}

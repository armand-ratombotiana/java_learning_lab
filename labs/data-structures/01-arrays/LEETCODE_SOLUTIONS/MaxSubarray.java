package com.leetcode.arrays;

/**
 * LeetCode 53: Maximum Subarray
 * https://leetcode.com/problems/maximum-subarray/
 *
 * Find the contiguous subarray (containing at least one number)
 * which has the largest sum and return its sum.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class MaxSubarray {

    /**
     * Approach 1 (Optimal): Kadane's Algorithm
     * Track current sum and reset to 0 if it goes negative.
     * Time: O(n), Space: O(1)
     */
    public int maxSubArray(int[] nums) {
        int maxSum = nums[0];
        int currentSum = nums[0];

        for (int i = 1; i < nums.length; i++) {
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            maxSum = Math.max(maxSum, currentSum);
        }

        return maxSum;
    }

    /**
     * Approach 2: Divide and Conquer
     * Split array, find max in left, right, and cross.
     * Time: O(n log n), Space: O(log n)
     */
    public int maxSubArrayDnC(int[] nums) {
        return maxSubArrayHelper(nums, 0, nums.length - 1);
    }

    private int maxSubArrayHelper(int[] nums, int left, int right) {
        if (left == right) return nums[left];
        int mid = left + (right - left) / 2;

        int leftMax = maxSubArrayHelper(nums, left, mid);
        int rightMax = maxSubArrayHelper(nums, mid + 1, right);
        int crossMax = maxCrossingSum(nums, left, mid, right);

        return Math.max(Math.max(leftMax, rightMax), crossMax);
    }

    private int maxCrossingSum(int[] nums, int left, int mid, int right) {
        int sum = 0, leftSum = Integer.MIN_VALUE;
        for (int i = mid; i >= left; i--) {
            sum += nums[i];
            leftSum = Math.max(leftSum, sum);
        }

        sum = 0;
        int rightSum = Integer.MIN_VALUE;
        for (int i = mid + 1; i <= right; i++) {
            sum += nums[i];
            rightSum = Math.max(rightSum, sum);
        }

        return leftSum + rightSum;
    }

    public static void main(String[] args) {
        MaxSubarray ms = new MaxSubarray();

        System.out.println("Test 1: " + ms.maxSubArray(new int[] { -2, 1, -3, 4, -1, 2, 1, -5, 4 }) + " (expected: 6)");
        System.out.println("Test 2: " + ms.maxSubArray(new int[] { 1 }) + " (expected: 1)");
        System.out.println("Test 3: " + ms.maxSubArray(new int[] { 5, 4, -1, 7, 8 }) + " (expected: 23)");
        System.out.println("Test 4: " + ms.maxSubArray(new int[] { -1 }) + " (expected: -1)");

        // Divide and Conquer verification
        System.out.println("Test 5 (DnC): " + ms.maxSubArrayDnC(new int[] { -2, 1, -3, 4, -1, 2, 1, -5, 4 }) + " (expected: 6)");
    }
}

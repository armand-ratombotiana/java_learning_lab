package com.algorithms.divideconquer;

/**
 * LeetCode 53: Maximum Subarray (Divide & Conquer version)
 * https://leetcode.com/problems/maximum-subarray/
 *
 * Find the contiguous subarray with the largest sum.
 *
 * Time Complexity: O(n log n) for D&C, O(n) for Kadane
 * Space Complexity: O(log n)
 */
public class MaxSubarray {

    public int maxSubArray(int[] nums) {
        return maxSubArrayHelper(nums, 0, nums.length - 1);
    }

    private int maxSubArrayHelper(int[] nums, int left, int right) {
        if (left == right) return nums[left];
        int mid = left + (right - left) / 2;

        int leftSum = maxSubArrayHelper(nums, left, mid);
        int rightSum = maxSubArrayHelper(nums, mid + 1, right);
        int crossSum = maxCrossingSum(nums, left, mid, right);

        return Math.max(Math.max(leftSum, rightSum), crossSum);
    }

    private int maxCrossingSum(int[] nums, int left, int mid, int right) {
        int sum = 0, leftMax = Integer.MIN_VALUE;
        for (int i = mid; i >= left; i--) {
            sum += nums[i];
            leftMax = Math.max(leftMax, sum);
        }
        sum = 0;
        int rightMax = Integer.MIN_VALUE;
        for (int i = mid + 1; i <= right; i++) {
            sum += nums[i];
            rightMax = Math.max(rightMax, sum);
        }
        return leftMax + rightMax;
    }

    public static void main(String[] args) {
        MaxSubarray ms = new MaxSubarray();
        System.out.println("Test 1: " + ms.maxSubArray(new int[] { -2, 1, -3, 4, -1, 2, 1, -5, 4 }) + " (expected: 6)");
        System.out.println("Test 2: " + ms.maxSubArray(new int[] { 1 }) + " (expected: 1)");
        System.out.println("Test 3: " + ms.maxSubArray(new int[] { 5, 4, -1, 7, 8 }) + " (expected: 23)");
        System.out.println("Test 4: " + ms.maxSubArray(new int[] { -1 }) + " (expected: -1)");
    }
}

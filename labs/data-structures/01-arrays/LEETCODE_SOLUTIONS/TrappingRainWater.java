package com.leetcode.arrays;

/**
 * LeetCode 42: Trapping Rain Water
 * https://leetcode.com/problems/trapping-rain-water/
 *
 * Given n non-negative integers representing an elevation map, compute
 * how much water it can trap after raining.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class TrappingRainWater {

    /**
     * Approach 1 (Optimal): Two Pointer
     * Move pointers inward, tracking left max and right max.
     * Time: O(n), Space: O(1)
     */
    public int trap(int[] height) {
        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0;
        int water = 0;

        while (left < right) {
            if (height[left] < height[right]) {
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    water += leftMax - height[left];
                }
                left++;
            } else {
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    water += rightMax - height[right];
                }
                right--;
            }
        }
        return water;
    }

    /**
     * Approach 2: Dynamic Programming (prefix/suffix max)
     * Precompute left max and right max for each position.
     * Time: O(n), Space: O(n)
     */
    public int trapDP(int[] height) {
        int n = height.length;
        if (n == 0) return 0;

        int[] leftMax = new int[n];
        int[] rightMax = new int[n];

        leftMax[0] = height[0];
        for (int i = 1; i < n; i++) {
            leftMax[i] = Math.max(leftMax[i - 1], height[i]);
        }

        rightMax[n - 1] = height[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            rightMax[i] = Math.max(rightMax[i + 1], height[i]);
        }

        int water = 0;
        for (int i = 0; i < n; i++) {
            water += Math.min(leftMax[i], rightMax[i]) - height[i];
        }
        return water;
    }

    public static void main(String[] args) {
        TrappingRainWater trw = new TrappingRainWater();

        System.out.println("Test 1: " + trw.trap(new int[] { 0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1 }) + " (expected: 6)");
        System.out.println("Test 2: " + trw.trap(new int[] { 4, 2, 0, 3, 2, 5 }) + " (expected: 9)");
        System.out.println("Test 3: " + trw.trap(new int[] { 1, 0, 1 }) + " (expected: 1)");
        System.out.println("Test 4: " + trw.trap(new int[] { 0 }) + " (expected: 0)");

        // DP verification
        System.out.println("Test 5 (DP): " + trw.trapDP(new int[] { 0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1 }) + " (expected: 6)");
    }
}

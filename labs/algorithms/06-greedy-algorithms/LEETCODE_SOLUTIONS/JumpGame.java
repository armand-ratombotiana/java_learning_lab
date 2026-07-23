package com.algorithms.greedy;

/**
 * LeetCode 55: Jump Game
 * https://leetcode.com/problems/jump-game/
 *
 * Given an array where each element is the max jump length,
 * determine if you can reach the last index.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class JumpGame {

    /**
     * Approach 1 (Optimal): Greedy
     * Track the farthest reachable index. If current index > farthest, cannot reach.
     */
    public boolean canJump(int[] nums) {
        int farthest = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > farthest) return false;
            farthest = Math.max(farthest, i + nums[i]);
            if (farthest >= nums.length - 1) return true;
        }
        return false;
    }

    /**
     * Approach 2: DP (O(n^2))
     */

    public static void main(String[] args) {
        JumpGame jg = new JumpGame();
        System.out.println("Test 1: " + jg.canJump(new int[] { 2, 3, 1, 1, 4 }) + " (expected: true)");
        System.out.println("Test 2: " + jg.canJump(new int[] { 3, 2, 1, 0, 4 }) + " (expected: false)");
        System.out.println("Test 3: " + jg.canJump(new int[] { 0 }) + " (expected: true)");
        System.out.println("Test 4: " + jg.canJump(new int[] { 2, 0, 0 }) + " (expected: true)");
        System.out.println("Test 5: " + jg.canJump(new int[] { 1, 0, 1, 0 }) + " (expected: false)");
    }
}

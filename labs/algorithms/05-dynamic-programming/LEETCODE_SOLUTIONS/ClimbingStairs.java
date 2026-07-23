package com.algorithms.dp;

/**
 * LeetCode 70: Climbing Stairs
 * https://leetcode.com/problems/climbing-stairs/
 *
 * You are climbing a staircase. It takes n steps to reach the top.
 * Each time you can climb 1 or 2 steps. How many distinct ways to reach the top?
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class ClimbingStairs {

    /**
     * Approach 1 (Optimal): Fibonacci DP with O(1) space
     * dp[i] = dp[i-1] + dp[i-2] where dp[i] is ways to reach step i.
     */
    public int climbStairs(int n) {
        if (n <= 2) return n;
        int prev2 = 1, prev1 = 2;
        for (int i = 3; i <= n; i++) {
            int curr = prev1 + prev2;
            prev2 = prev1;
            prev1 = curr;
        }
        return prev1;
    }

    /**
     * Approach 2: Recursive with memoization
     */

    public static void main(String[] args) {
        ClimbingStairs cs = new ClimbingStairs();
        System.out.println("n=1: " + cs.climbStairs(1) + " (expected: 1)");
        System.out.println("n=2: " + cs.climbStairs(2) + " (expected: 2)");
        System.out.println("n=3: " + cs.climbStairs(3) + " (expected: 3)");
        System.out.println("n=4: " + cs.climbStairs(4) + " (expected: 5)");
        System.out.println("n=5: " + cs.climbStairs(5) + " (expected: 8)");
    }
}

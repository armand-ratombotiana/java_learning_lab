package com.algorithms.gametheory;

/**
 * Custom: Game Theory Algorithms
 * Minimax, Nim game, and game tree evaluation.
 *
 * Time Complexity: O(n) for Nim, O(b^d) for minimax
 * Space Complexity: O(d) for recursion depth
 */
public class GameTheory {

    // LC 292: Nim Game
    public boolean canWinNim(int n) {
        return n % 4 != 0;
    }

    // Minimax: simple game where two players pick from ends of an array
    public int minimax(int[] nums) {
        int n = nums.length;
        int[][] dp = new int[n][n];
        for (int i = 0; i < n; i++) dp[i][i] = nums[i];
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                dp[i][j] = Math.max(nums[i] - dp[i + 1][j], nums[j] - dp[i][j - 1]);
            }
        }
        return dp[0][n - 1];
    }

    // Predict the Winner (LC 486)
    public boolean predictTheWinner(int[] nums) {
        return minimax(nums) >= 0;
    }

    public static void main(String[] args) {
        GameTheory gt = new GameTheory();
        System.out.println("Nim game (n=4): " + gt.canWinNim(4) + " (expected: false)");
        System.out.println("Nim game (n=5): " + gt.canWinNim(5) + " (expected: true)");
        System.out.println("Predict winner [1,5,2]: " + gt.predictTheWinner(new int[]{1,5,2}) + " (expected: false)");
        System.out.println("Predict winner [1,5,233,7]: " + gt.predictTheWinner(new int[]{1,5,233,7}) + " (expected: true)");
    }
}

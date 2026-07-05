package com.algo.lab05;

public class DynamicProgrammingExample {
    public static void main(String[] args) {
        System.out.println("=== Dynamic Programming Demo ===\n");

        System.out.println("--- Fibonacci (DP) ---");
        for (int n = 0; n <= 20; n++) {
            System.out.printf("F(%d) = %d%n", n, DynamicProgramming.fibonacciDP(n));
        }

        System.out.println("\n--- 0/1 Knapsack ---");
        int[] weights = {10, 20, 30};
        int[] values = {60, 100, 120};
        int capacity = 50;
        int maxVal = DynamicProgramming.knapSack(weights, values, capacity);
        System.out.printf("Max value (capacity=%d): %d%n", capacity, maxVal);

        System.out.println("\n--- Longest Common Subsequence ---");
        String a = "ABCDGH", b = "AEDFHR";
        System.out.printf("LCS of \"%s\" and \"%s\": %d%n", a, b,
            DynamicProgramming.longestCommonSubsequence(a, b));

        System.out.println("\n--- Longest Increasing Subsequence ---");
        int[] arr = {10, 22, 9, 33, 21, 50, 41, 60, 80};
        System.out.printf("LIS length: %d%n", DynamicProgramming.longestIncreasingSubsequence(arr));

        System.out.println("\n--- Edit Distance ---");
        String s1 = "kitten", s2 = "sitting";
        System.out.printf("Edit distance \"%s\" -> \"%s\": %d%n", s1, s2,
            DynamicProgramming.editDistance(s1, s2));
    }
}
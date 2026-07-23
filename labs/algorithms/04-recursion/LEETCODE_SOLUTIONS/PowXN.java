package com.algorithms.recursion;

/**
 * LeetCode 50: Pow(x, n)
 * https://leetcode.com/problems/powx-n/
 *
 * Implement pow(x, n) using recursion.
 *
 * Time Complexity: O(log n)
 * Space Complexity: O(log n)
 */
public class PowXN {

    /**
     * Approach 1 (Optimal): Fast Power (Binary Exponentiation)
     * x^n = (x^(n/2))^2 if n is even, x * (x^(n-1)) if n is odd.
     */
    public double myPow(double x, int n) {
        if (n == 0) return 1;
        long exp = n;
        if (exp < 0) {
            x = 1 / x;
            exp = -exp;
        }
        return fastPow(x, exp);
    }

    private double fastPow(double x, long n) {
        if (n == 0) return 1;
        double half = fastPow(x, n / 2);
        if (n % 2 == 0) return half * half;
        return half * half * x;
    }

    /**
     * Approach 2: Iterative
     */
    public double myPowIterative(double x, int n) {
        long exp = n;
        if (exp < 0) { x = 1 / x; exp = -exp; }
        double result = 1;
        while (exp > 0) {
            if ((exp & 1) == 1) result *= x;
            x *= x;
            exp >>= 1;
        }
        return result;
    }

    public static void main(String[] args) {
        PowXN p = new PowXN();
        System.out.println("Test 1: " + p.myPow(2.0, 10) + " (expected: 1024.0)");
        System.out.println("Test 2: " + p.myPow(2.1, 3) + " (expected: ~9.261)");
        System.out.println("Test 3: " + p.myPow(2.0, -2) + " (expected: 0.25)");
        System.out.println("Test 4: " + p.myPow(0, 5) + " (expected: 0.0)");
        System.out.println("Test 5: " + p.myPow(5, 0) + " (expected: 1.0)");
        System.out.println("Test 6: " + p.myPowIterative(2.0, 10) + " (expected: 1024.0)");
    }
}

package com.algo.lab05;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DynamicProgrammingTest {

    @Test
    void testFibonacciDP() {
        assertEquals(0, DynamicProgramming.fibonacciDP(0));
        assertEquals(1, DynamicProgramming.fibonacciDP(1));
        assertEquals(1, DynamicProgramming.fibonacciDP(2));
        assertEquals(2, DynamicProgramming.fibonacciDP(3));
        assertEquals(3, DynamicProgramming.fibonacciDP(4));
        assertEquals(5, DynamicProgramming.fibonacciDP(5));
        assertEquals(55, DynamicProgramming.fibonacciDP(10));
        assertEquals(832040, DynamicProgramming.fibonacciDP(30));
    }

    @Test
    void testFibonacciDPNegative() {
        assertThrows(IllegalArgumentException.class, () -> DynamicProgramming.fibonacciDP(-1));
    }

    @Test
    void testKnapSack() {
        int[] weights = {10, 20, 30};
        int[] values = {60, 100, 120};
        assertEquals(220, DynamicProgramming.knapSack(weights, values, 50));
    }

    @Test
    void testKnapSackNoCapacity() {
        int[] weights = {10, 20};
        int[] values = {60, 100};
        assertEquals(0, DynamicProgramming.knapSack(weights, values, 0));
    }

    @Test
    void testKnapSackEmpty() {
        assertEquals(0, DynamicProgramming.knapSack(new int[]{}, new int[]{}, 50));
    }

    @Test
    void testKnapSackSmallItems() {
        int[] weights = {1, 2, 3};
        int[] values = {10, 15, 40};
        assertEquals(65, DynamicProgramming.knapSack(weights, values, 5));
    }

    @Test
    void testLCS() {
        assertEquals(4, DynamicProgramming.longestCommonSubsequence("ABCDGH", "AEDFHR"));
        assertEquals(3, DynamicProgramming.longestCommonSubsequence("AGGTAB", "GXTXAYB"));
    }

    @Test
    void testLCSEmpty() {
        assertEquals(0, DynamicProgramming.longestCommonSubsequence("", "ABC"));
        assertEquals(0, DynamicProgramming.longestCommonSubsequence("ABC", ""));
        assertEquals(0, DynamicProgramming.longestCommonSubsequence("", ""));
    }

    @Test
    void testLCSIdentical() {
        assertEquals(5, DynamicProgramming.longestCommonSubsequence("ABCDE", "ABCDE"));
    }

    @Test
    void testLCSNoCommon() {
        assertEquals(0, DynamicProgramming.longestCommonSubsequence("ABC", "XYZ"));
    }

    @Test
    void testLIS() {
        assertEquals(6, DynamicProgramming.longestIncreasingSubsequence(
            new int[]{10, 22, 9, 33, 21, 50, 41, 60, 80}));
    }

    @Test
    void testLISEmpty() {
        assertEquals(0, DynamicProgramming.longestIncreasingSubsequence(new int[]{}));
    }

    @Test
    void testLISAllDecreasing() {
        assertEquals(1, DynamicProgramming.longestIncreasingSubsequence(new int[]{5, 4, 3, 2, 1}));
    }

    @Test
    void testLISAllIncreasing() {
        assertEquals(5, DynamicProgramming.longestIncreasingSubsequence(new int[]{1, 2, 3, 4, 5}));
    }

    @Test
    void testEditDistance() {
        assertEquals(3, DynamicProgramming.editDistance("kitten", "sitting"));
        assertEquals(3, DynamicProgramming.editDistance("saturday", "sunday"));
    }

    @Test
    void testEditDistanceEmpty() {
        assertEquals(5, DynamicProgramming.editDistance("", "hello"));
        assertEquals(5, DynamicProgramming.editDistance("hello", ""));
        assertEquals(0, DynamicProgramming.editDistance("", ""));
    }

    @Test
    void testEditDistanceIdentical() {
        assertEquals(0, DynamicProgramming.editDistance("same", "same"));
    }

    @Test
    void testEditDistanceOneChar() {
        assertEquals(0, DynamicProgramming.editDistance("a", "a"));
        assertEquals(1, DynamicProgramming.editDistance("a", "b"));
    }
}
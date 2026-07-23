package com.algorithms.recursion;

import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 78: Subsets
 * https://leetcode.com/problems/subsets/
 *
 * Given an array of distinct integers, return all possible subsets (the power set).
 *
 * Time Complexity: O(n * 2^n)
 * Space Complexity: O(n * 2^n)
 */
public class Subsets {

    /**
     * Approach 1: Backtracking/Recursion
     * At each index, decide to include or skip the element.
     */
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), nums, 0);
        return result;
    }

    private void backtrack(List<List<Integer>> result, List<Integer> current, int[] nums, int start) {
        result.add(new ArrayList<>(current));
        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);
            backtrack(result, current, nums, i + 1);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Approach 2: Iterative (cascading)
     * Start with empty set, for each number add it to all existing subsets.
     */

    public static void main(String[] args) {
        Subsets s = new Subsets();
        System.out.println("Test 1: " + s.subsets(new int[] { 1, 2, 3 }));
        System.out.println("  (expected: [[], [1], [1, 2], [1, 2, 3], [1, 3], [2], [2, 3], [3]])");
        System.out.println("Test 2: " + s.subsets(new int[] { 0 }) + " (expected: [[], [0]])");
        System.out.println("Test 3: " + s.subsets(new int[] {}) + " (expected: [[]])");
    }
}

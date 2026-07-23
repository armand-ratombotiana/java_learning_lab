package com.algorithms.recursion;

import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 46: Permutations
 * https://leetcode.com/problems/permutations/
 *
 * Given an array of distinct integers, return all possible permutations.
 *
 * Time Complexity: O(n * n!)
 * Space Complexity: O(n * n!)
 */
public class Permutations {

    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), nums, new boolean[nums.length]);
        return result;
    }

    private void backtrack(List<List<Integer>> result, List<Integer> current, int[] nums, boolean[] used) {
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            current.add(nums[i]);
            backtrack(result, current, nums, used);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }

    public static void main(String[] args) {
        Permutations p = new Permutations();
        System.out.println("Test 1: " + p.permute(new int[] { 1, 2, 3 }));
        System.out.println("  (expected: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]])");
        System.out.println("Test 2: " + p.permute(new int[] { 0, 1 }) + " (expected: [[0,1],[1,0]])");
        System.out.println("Test 3: " + p.permute(new int[] { 1 }) + " (expected: [[1]])");
    }
}

package com.leetcode.hashtable;

import java.util.HashSet;
import java.util.Set;

/**
 * LeetCode 128: Longest Consecutive Sequence
 * https://leetcode.com/problems/longest-consecutive-sequence/
 *
 * Given an unsorted array of integers, return the length of the longest consecutive
 * elements sequence. Must run in O(n) time.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */
public class LongestConsecutive {

    /**
     * Approach 1 (Optimal): HashSet
     * Add all numbers to a set. For each number, check if it's the start of a sequence
     * (no number-1 exists), then count consecutive numbers.
     * Time: O(n), Space: O(n)
     */
    public int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int num : nums) set.add(num);

        int longest = 0;

        for (int num : set) {
            if (!set.contains(num - 1)) {
                int currentNum = num;
                int count = 1;
                while (set.contains(currentNum + 1)) {
                    currentNum++;
                    count++;
                }
                longest = Math.max(longest, count);
            }
        }
        return longest;
    }

    public static void main(String[] args) {
        LongestConsecutive lc = new LongestConsecutive();

        System.out.println("Test 1: " + lc.longestConsecutive(new int[] { 100, 4, 200, 1, 3, 2 }) + " (expected: 4)");
        System.out.println("Test 2: " + lc.longestConsecutive(new int[] { 0, 3, 7, 2, 5, 8, 4, 6, 0, 1 }) + " (expected: 9)");
        System.out.println("Test 3: " + lc.longestConsecutive(new int[] { 1, 2, 0, 1 }) + " (expected: 3)");
        System.out.println("Test 4: " + lc.longestConsecutive(new int[] {}) + " (expected: 0)");
        System.out.println("Test 5: " + lc.longestConsecutive(new int[] { 1 }) + " (expected: 1)");
    }
}

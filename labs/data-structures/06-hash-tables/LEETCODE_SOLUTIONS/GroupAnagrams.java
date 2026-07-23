package com.leetcode.hashtable;

import java.util.*;

/**
 * LeetCode 49: Group Anagrams
 * https://leetcode.com/problems/group-anagrams/
 *
 * Given an array of strings, group the anagrams together.
 *
 * Time Complexity: O(n * k log k) where k is max string length
 * Space Complexity: O(n * k)
 */
public class GroupAnagrams {

    /**
     * Approach 1 (Optimal): Sort each string as the key
     * Time: O(n * k log k), Space: O(n * k)
     */
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();

        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }

        return new ArrayList<>(map.values());
    }

    /**
     * Approach 2: Character count as key (avoid sorting)
     * Use an array of 26 counts as key. O(n * k) time.
     */

    public static void main(String[] args) {
        GroupAnagrams ga = new GroupAnagrams();

        List<List<String>> r1 = ga.groupAnagrams(new String[] { "eat", "tea", "tan", "ate", "nat", "bat" });
        System.out.println("Test 1: " + r1);
        System.out.println("  expected: [[eat, tea, ate], [tan, nat], [bat]]");

        List<List<String>> r2 = ga.groupAnagrams(new String[] { "" });
        System.out.println("Test 2: " + r2 + " (expected: [[]])");

        List<List<String>> r3 = ga.groupAnagrams(new String[] { "a" });
        System.out.println("Test 3: " + r3 + " (expected: [[a]])");
    }
}

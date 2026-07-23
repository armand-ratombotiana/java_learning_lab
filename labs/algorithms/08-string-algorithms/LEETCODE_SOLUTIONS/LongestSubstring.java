package com.algorithms.string;

import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 3: Longest Substring Without Repeating Characters
 * https://leetcode.com/problems/longest-substring-without-repeating-characters/
 *
 * Find the length of the longest substring without repeating characters.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(k) where k is charset size
 */
public class LongestSubstring {

    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> map = new HashMap<>();
        int maxLen = 0, left = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (map.containsKey(c) && map.get(c) >= left) {
                left = map.get(c) + 1;
            }
            map.put(c, right);
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    public static void main(String[] args) {
        LongestSubstring ls = new LongestSubstring();
        System.out.println("Test 1: " + ls.lengthOfLongestSubstring("abcabcbb") + " (expected: 3)");
        System.out.println("Test 2: " + ls.lengthOfLongestSubstring("bbbbb") + " (expected: 1)");
        System.out.println("Test 3: " + ls.lengthOfLongestSubstring("pwwkew") + " (expected: 3)");
        System.out.println("Test 4: " + ls.lengthOfLongestSubstring("") + " (expected: 0)");
        System.out.println("Test 5: " + ls.lengthOfLongestSubstring(" ") + " (expected: 1)");
    }
}

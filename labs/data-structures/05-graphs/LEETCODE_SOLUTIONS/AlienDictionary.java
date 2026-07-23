package com.leetcode.graphs;

import java.util.*;

/**
 * LeetCode 269: Alien Dictionary
 * https://leetcode.com/problems/alien-dictionary/
 *
 * Given a sorted list of words from an alien language, return the order
 * of characters in that language.
 *
 * Time Complexity: O(C) where C is total characters across all words
 * Space Complexity: O(1) — at most 26 characters
 */
public class AlienDictionary {

    /**
     * Approach 1 (Optimal): Topological Sort
     * Build a graph from adjacent word comparisons, then perform topological sort.
     * Time: O(C), Space: O(1)
     */
    public String alienOrder(String[] words) {
        Map<Character, Set<Character>> graph = new HashMap<>();
        Map<Character, Integer> indegree = new HashMap<>();

        for (String word : words) {
            for (char c : word.toCharArray()) {
                graph.putIfAbsent(c, new HashSet<>());
                indegree.putIfAbsent(c, 0);
            }
        }

        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i];
            String w2 = words[i + 1];
            int minLen = Math.min(w1.length(), w2.length());

            if (w1.length() > w2.length() && w1.startsWith(w2)) return "";

            for (int j = 0; j < minLen; j++) {
                char c1 = w1.charAt(j);
                char c2 = w2.charAt(j);
                if (c1 != c2) {
                    if (!graph.get(c1).contains(c2)) {
                        graph.get(c1).add(c2);
                        indegree.put(c2, indegree.get(c2) + 1);
                    }
                    break;
                }
            }
        }

        Queue<Character> queue = new LinkedList<>();
        for (char c : indegree.keySet()) {
            if (indegree.get(c) == 0) queue.offer(c);
        }

        StringBuilder result = new StringBuilder();
        while (!queue.isEmpty()) {
            char c = queue.poll();
            result.append(c);
            for (char neighbor : graph.get(c)) {
                indegree.put(neighbor, indegree.get(neighbor) - 1);
                if (indegree.get(neighbor) == 0) queue.offer(neighbor);
            }
        }

        return result.length() == indegree.size() ? result.toString() : "";
    }

    public static void main(String[] args) {
        AlienDictionary ad = new AlienDictionary();

        System.out.println("Test 1: " + ad.alienOrder(new String[] { "wrt", "wrf", "er", "ett", "rftt" }) + " (expected: wertf)");
        System.out.println("Test 2: " + ad.alienOrder(new String[] { "z", "x" }) + " (expected: zx)");
        System.out.println("Test 3: " + ad.alienOrder(new String[] { "z", "x", "z" }) + " (expected: )");
        System.out.println("Test 4: " + ad.alienOrder(new String[] { "a" }) + " (expected: a)");
        System.out.println("Test 5: " + ad.alienOrder(new String[] { "ab", "adc" }) + " (expected: abdc)");
    }
}

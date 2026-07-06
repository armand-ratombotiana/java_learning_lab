package com.algo.lab16;

import java.util.*;

/**
 * Aho-Corasick automaton for multi-pattern string matching.
 * Builds a trie with failure links for simultaneous pattern matching.
 * Time: O(n + m + k) for n text length, m total pattern length, k matches
 */
public class AhoCorasick {

    private final TrieNode root;

    public AhoCorasick() {
        root = new TrieNode();
    }

    public void addPattern(String pattern) {
        if (pattern == null || pattern.isEmpty()) return;
        TrieNode node = root;
        for (char c : pattern.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.patterns.add(pattern);
    }

    public void buildFailureLinks() {
        Queue<TrieNode> queue = new LinkedList<>();
        for (TrieNode child : root.children.values()) {
            child.fail = root;
            queue.add(child);
        }
        while (!queue.isEmpty()) {
            TrieNode current = queue.poll();
            for (Map.Entry<Character, TrieNode> entry : current.children.entrySet()) {
                char c = entry.getKey();
                TrieNode child = entry.getValue();
                TrieNode fail = current.fail;
                while (fail != root && !fail.children.containsKey(c)) {
                    fail = fail.fail;
                }
                child.fail = fail.children.getOrDefault(c, root);
                child.patterns.addAll(child.fail.patterns);
                queue.add(child);
            }
        }
    }

    public Map<String, List<Integer>> search(String text) {
        Map<String, List<Integer>> results = new HashMap<>();
        if (text == null || text.isEmpty()) return results;
        TrieNode node = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            while (node != root && !node.children.containsKey(c)) {
                node = node.fail;
            }
            node = node.children.getOrDefault(c, root);
            for (String pattern : node.patterns) {
                results.computeIfAbsent(pattern, k -> new ArrayList<>()).add(i - pattern.length() + 1);
            }
        }
        return results;
    }

    private static class TrieNode {
        final Map<Character, TrieNode> children = new HashMap<>();
        TrieNode fail;
        final List<String> patterns = new ArrayList<>();
    }
}

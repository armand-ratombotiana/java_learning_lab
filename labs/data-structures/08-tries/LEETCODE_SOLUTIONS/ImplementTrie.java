package com.leetcode.tries;

/**
 * LeetCode 208: Implement Trie (Prefix Tree)
 * https://leetcode.com/problems/implement-trie-prefix-tree/
 *
 * A trie (prefix tree) with insert, search, and startsWith operations.
 *
 * Time Complexity: O(k) per operation where k is word length
 * Space Complexity: O(n * k) where n is number of words
 */
public class ImplementTrie {

    public static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd = false;
    }

    private final TrieNode root;

    public ImplementTrie() {
        root = new TrieNode();
    }

    /** Inserts a word into the trie. */
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new TrieNode();
            }
            node = node.children[idx];
        }
        node.isEnd = true;
    }

    /** Returns true if the word is in the trie. */
    public boolean search(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return false;
            node = node.children[idx];
        }
        return node.isEnd;
    }

    /** Returns true if there is any word in the trie that starts with the given prefix. */
    public boolean startsWith(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return false;
            node = node.children[idx];
        }
        return true;
    }

    public static void main(String[] args) {
        ImplementTrie trie = new ImplementTrie();
        trie.insert("apple");
        System.out.println("Search 'apple': " + trie.search("apple") + " (expected: true)");
        System.out.println("Search 'app': " + trie.search("app") + " (expected: false)");
        System.out.println("StartsWith 'app': " + trie.startsWith("app") + " (expected: true)");
        trie.insert("app");
        System.out.println("Search 'app' after insert: " + trie.search("app") + " (expected: true)");

        // Empty prefix
        System.out.println("StartsWith '': " + trie.startsWith("") + " (expected: true)");

        // Non-existent
        System.out.println("Search 'orange': " + trie.search("orange") + " (expected: false)");
        System.out.println("StartsWith 'ora': " + trie.startsWith("ora") + " (expected: false)");
    }
}

package com.leetcode.tries;

/**
 * LeetCode 211: Design Add and Search Words Data Structure
 * https://leetcode.com/problems/design-add-and-search-words-data-structure/
 *
 * Design a data structure that supports adding words and searching with '.' wildcards.
 *
 * Time Complexity: O(k) for add, O(26^k) worst for search with wildcards
 * Space Complexity: O(n * k)
 */
public class DesignAddSearchWords {

    public static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd = false;
    }

    private final TrieNode root;

    public DesignAddSearchWords() {
        root = new TrieNode();
    }

    public void addWord(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) node.children[idx] = new TrieNode();
            node = node.children[idx];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        return searchHelper(word, 0, root);
    }

    private boolean searchHelper(String word, int index, TrieNode node) {
        if (node == null) return false;
        if (index == word.length()) return node.isEnd;

        char c = word.charAt(index);
        if (c == '.') {
            for (TrieNode child : node.children) {
                if (searchHelper(word, index + 1, child)) return true;
            }
            return false;
        } else {
            return searchHelper(word, index + 1, node.children[c - 'a']);
        }
    }

    public static void main(String[] args) {
        DesignAddSearchWords das = new DesignAddSearchWords();
        das.addWord("bad");
        das.addWord("dad");
        das.addWord("mad");

        System.out.println("Search 'pad': " + das.search("pad") + " (expected: false)");
        System.out.println("Search 'bad': " + das.search("bad") + " (expected: true)");
        System.out.println("Search '.ad': " + das.search(".ad") + " (expected: true)");
        System.out.println("Search 'b..': " + das.search("b..") + " (expected: true)");
        System.out.println("Search '...': " + das.search("...") + " (expected: true)");
        System.out.println("Search 'b': " + das.search("b") + " (expected: false)");

        // Empty search
        das.addWord("");
        System.out.println("Search '': " + das.search("") + " (expected: true)");
    }
}

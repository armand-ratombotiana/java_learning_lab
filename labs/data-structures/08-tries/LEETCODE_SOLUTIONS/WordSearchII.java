package com.leetcode.tries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * LeetCode 212: Word Search II
 * https://leetcode.com/problems/word-search-ii/
 *
 * Given an m x n board of characters and a list of words, find all words on the board.
 * Each word must be formed from adjacent letters (no reuse of same cell).
 *
 * Time Complexity: O(M * N * 4^L) where L is average word length
 * Space Complexity: O(total characters in words)
 */
public class WordSearchII {

    public static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word = null;
    }

    private TrieNode buildTrie(String[] words) {
        TrieNode root = new TrieNode();
        for (String w : words) {
            TrieNode node = root;
            for (char c : w.toCharArray()) {
                int idx = c - 'a';
                if (node.children[idx] == null) node.children[idx] = new TrieNode();
                node = node.children[idx];
            }
            node.word = w;
        }
        return root;
    }

    /**
     * Approach: Trie + Backtracking
     * Insert all words into a trie, then DFS on the board.
     */
    public List<String> findWords(char[][] board, String[] words) {
        TrieNode root = buildTrie(words);
        Set<String> result = new HashSet<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                dfs(board, i, j, root, result);
            }
        }
        return new ArrayList<>(result);
    }

    private void dfs(char[][] board, int i, int j, TrieNode node, Set<String> result) {
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length) return;
        char c = board[i][j];
        if (c == '#' || node.children[c - 'a'] == null) return;

        node = node.children[c - 'a'];
        if (node.word != null) result.add(node.word);

        board[i][j] = '#';
        dfs(board, i - 1, j, node, result);
        dfs(board, i + 1, j, node, result);
        dfs(board, i, j - 1, node, result);
        dfs(board, i, j + 1, node, result);
        board[i][j] = c;
    }

    public static void main(String[] args) {
        WordSearchII ws = new WordSearchII();

        char[][] board = {
            { 'o', 'a', 'a', 'n' },
            { 'e', 't', 'a', 'e' },
            { 'i', 'h', 'k', 'r' },
            { 'i', 'f', 'l', 'v' }
        };
        String[] words = { "oath", "pea", "eat", "rain" };
        List<String> found = ws.findWords(board, words);
        System.out.println("Found: " + found + " (expected: [eat, oath])");

        // Simple test
        char[][] board2 = { { 'a', 'b' }, { 'c', 'd' } };
        String[] words2 = { "ab", "ac", "bd", "abc", "abd" };
        List<String> found2 = ws.findWords(board2, words2);
        System.out.println("Found 2: " + found2 + " (expected: [ab, ac, bd])");
    }
}

package com.ds08;

import java.util.*;

/*
 * Trie - Prefix tree for string storage and retrieval.
 *
 * Time Complexity:
 * - insert: O(L) where L is word length
 * - search: O(L)
 * - delete: O(L)
 * - startsWith: O(L)
 * - autocomplete: O(L + results)
 *
 * Space Complexity: O(N * L) where N is number of words
 */
public class Trie {

    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord;
        int count;
    }

    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        current.isEndOfWord = true;
        current.count++;
    }

    public boolean search(String word) {
        TrieNode node = searchNode(word);
        return node != null && node.isEndOfWord;
    }

    public boolean startsWith(String prefix) {
        return searchNode(prefix) != null;
    }

    public boolean delete(String word) {
        return delete(root, word, 0);
    }

    private boolean delete(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndOfWord) return false;
            if (current.count > 1) {
                current.count--;
                return false;
            }
            current.isEndOfWord = false;
            return current.children.isEmpty();
        }
        char ch = word.charAt(index);
        TrieNode node = current.children.get(ch);
        if (node == null) return false;
        boolean shouldDeleteChild = delete(node, word, index + 1);
        if (shouldDeleteChild) {
            current.children.remove(ch);
            return current.children.isEmpty() && !current.isEndOfWord;
        }
        return false;
    }

    public int countWords() {
        return countWords(root);
    }

    private int countWords(TrieNode node) {
        int count = node.count;
        for (TrieNode child : node.children.values()) {
            count += countWords(child);
        }
        return count;
    }

    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = searchNode(prefix);
        if (node == null) return results;
        collectAllWords(node, new StringBuilder(prefix), results);
        return results;
    }

    public List<String> getAllWords() {
        List<String> results = new ArrayList<>();
        collectAllWords(root, new StringBuilder(), results);
        return results;
    }

    private void collectAllWords(TrieNode node, StringBuilder prefix, List<String> results) {
        if (node.isEndOfWord) {
            results.add(prefix.toString());
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            prefix.append(entry.getKey());
            collectAllWords(entry.getValue(), prefix, results);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    private TrieNode searchNode(String str) {
        TrieNode current = root;
        for (char ch : str.toCharArray()) {
            current = current.children.get(ch);
            if (current == null) return null;
        }
        return current;
    }

    public boolean isEmpty() {
        return root.children.isEmpty();
    }

    public void clear() {
        root.children.clear();
    }
}

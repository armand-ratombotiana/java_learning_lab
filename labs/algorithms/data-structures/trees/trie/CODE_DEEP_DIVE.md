# Trie Code Deep Dive

This lab provides a pure Java implementation of a Trie with support for full word search and prefix matching.

## 💻 Pure Java Implementation

```java file="labs/algorithms/data-structures/trees/trie/SOLUTION/Trie.java"
package algorithms.datastructures.trees;

import java.util.HashMap;
import java.util.Map;

/**
 * A fundamental implementation of a Prefix Tree (Trie).
 */
public class Trie {

    static class TrieNode {
        // Using a Map for children allows for any character (Unicode support)
        // For lowercase A-Z only, a TrieNode[26] array would be faster.
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
    }

    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    /**
     * Inserts a word into the Trie.
     */
    public void insert(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.children.computeIfAbsent(ch, k -> new TrieNode());
        }
        current.isEndOfWord = true;
    }

    /**
     * Returns true if the word is in the Trie.
     */
    public boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }

    /**
     * Returns true if there is any word in the Trie that starts with the given prefix.
     */
    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    private TrieNode findNode(String str) {
        TrieNode current = root;
        for (char ch : str.toCharArray()) {
            current = current.children.get(ch);
            if (current == null) return null;
        }
        return current;
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("apple");
        
        System.out.println("Search 'apple': " + trie.search("apple"));   // true
        System.out.println("Search 'app': " + trie.search("app"));       // false
        System.out.println("Prefix 'app': " + trie.startsWith("app"));   // true
        
        trie.insert("app");
        System.out.println("Search 'app' after insert: " + trie.search("app")); // true
    }
}
```

## 🔍 Key Takeaways
1. **The `isEndOfWord` Flag**: This is the most important field. Without it, you couldn't tell the difference between the word "app" being in the dictionary versus "app" just being a prefix of "apple".
2. **Recursive vs Iterative**: Tries are naturally recursive, but an iterative approach (using a loop) is generally preferred in Java to avoid `StackOverflowError` on very long strings and for better performance.
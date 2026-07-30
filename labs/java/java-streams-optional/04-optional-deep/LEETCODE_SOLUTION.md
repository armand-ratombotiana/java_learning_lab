# LEETCODE_SOLUTION — 208. Implement Trie (Prefix Tree)

## Problem
Implement a trie with insert, search, and startsWith.

## Optional Context
`Optional` can model the nullable child node in the trie map.

```java
class Trie {
    private TrieNode root = new TrieNode();

    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
    }

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray())
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        node.isEnd = true;
    }

    private TrieNode searchPrefix(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return null;
        }
        return node;
    }

    public boolean search(String word) {
        return Optional.ofNullable(searchPrefix(word))
            .map(n -> n.isEnd).orElse(false);
    }

    public boolean startsWith(String prefix) {
        return Optional.ofNullable(searchPrefix(prefix)).isPresent();
    }
}
```

## Complexity
- Time: O(n) per operation
- Space: O(n * m)

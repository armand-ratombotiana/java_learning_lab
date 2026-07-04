# How Tries Work

## Trie Implementation

```java
class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Insert a word into the trie
    public void insert(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        current.isEndOfWord = true;
    }

    // Search for exact word
    public boolean search(String word) {
        TrieNode node = searchPrefix(word);
        return node != null && node.isEndOfWord;
    }

    // Check if any word starts with given prefix
    public boolean startsWith(String prefix) {
        return searchPrefix(prefix) != null;
    }

    // Delete a word
    public boolean delete(String word) {
        return delete(root, word, 0);
    }

    private boolean delete(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndOfWord) return false;
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

    private TrieNode searchPrefix(String prefix) {
        TrieNode current = root;
        for (char ch : prefix.toCharArray()) {
            current = current.children.get(ch);
            if (current == null) return null;
        }
        return current;
    }

    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
    }
}
```

## Autocomplete Implementation

```java
public List<String> autocomplete(String prefix) {
    TrieNode node = searchPrefix(prefix);
    List<String> results = new ArrayList<>();
    if (node != null) {
        dfs(node, new StringBuilder(prefix), results);
    }
    return results;
}

private void dfs(TrieNode node, StringBuilder prefix, List<String> results) {
    if (node.isEndOfWord) {
        results.add(prefix.toString());
    }
    for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
        prefix.append(entry.getKey());
        dfs(entry.getValue(), prefix, results);
        prefix.deleteCharAt(prefix.length() - 1);
    }
}
```

## Array-Based Children (Faster, Fixed Alphabet)

```java
class TrieNode {
    TrieNode[] children = new TrieNode[26];  // lowercase a-z
    boolean isEndOfWord;

    TrieNode get(char ch) { return children[ch - 'a']; }
    void set(char ch) { children[ch - 'a'] = new TrieNode(); }
    boolean contains(char ch) { return children[ch - 'a'] != null; }
}
```

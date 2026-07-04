# Common Mistakes with Tries

## Not Handling Case Sensitivity

```java
// WRONG — treats uppercase and lowercase as different paths
trie.insert("Hello");  // path: H → e → l → l → o
trie.search("hello");  // false! 'h' != 'H'

// CORRECT — normalize to lowercase
void insert(String word) {
    word = word.toLowerCase();
    // ... rest of insert
}
```

## Forgetting to Mark Terminal

```java
// WRONG — no terminal marking
void insert(String word) {
    TrieNode current = root;
    for (char ch : word.toCharArray()) {
        current = current.children.computeIfAbsent(ch, c -> new TrieNode());
    }
    // forgot: current.isEndOfWord = true;
}

// Now "app" would be found even if only "apple" was inserted
// because search stops at the 'p' node which exists as part of "apple"
```

## Deleting Without Pruning

```java
// WRONG — just unmarks terminal, doesn't remove unused nodes
void delete(String word) {
    TrieNode node = searchPrefix(word);
    if (node != null) node.isEndOfWord = false;
    // Memory leak! Nodes for deleted word remain.
}

// CORRECT — recursive deletion with pruning
boolean delete(TrieNode current, String word, int index) {
    if (index == word.length()) {
        if (!current.isEndOfWord) return false;
        current.isEndOfWord = false;
        return current.children.isEmpty();
    }
    // ... recursive deletion, remove child if canDeleteChild
}
```

## Null Check on Child Nodes

```java
// WRONG — NullPointerException if child missing
TrieNode child = current.children.get(ch);
child.isEndOfWord = true;  // NPE if child == null

// CORRECT
TrieNode child = current.children.get(ch);
if (child == null) {
    child = new TrieNode();
    current.children.put(ch, child);
}
// then use child
```

## Confusing Search and StartsWith

```java
// search checks isEndOfWord
// startsWith only checks if prefix exists

trie.insert("apple");
trie.search("app");    // false (not a complete word)
trie.startsWith("app"); // true (prefix exists as part of "apple")
```

## Not Considering Alphabet Size

```java
// Array of 26 is fine for English lowercase
// WRONG for Unicode, Chinese, etc.
TrieNode[] children = new TrieNode[26];

// For general text, use HashMap
Map<Character, TrieNode> children = new HashMap<>();
```

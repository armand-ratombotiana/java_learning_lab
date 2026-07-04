# Debugging Tries

## Common Issues

| Symptom | Likely Cause |
|---------|-------------|
| Search returns false for inserted word | isEndOfWord not set |
| Search returns true for non-existent word | Node exists as prefix of another word |
| NullPointerException | Child node not created before access |
| Autocomplete returns too many words | Missing DFS depth limit |
| Slow insertion | Using array for non-English characters |

## Debugging Techniques

### Print Trie Structure

```java
void printTrie() {
    printNode(root, "");
}

void printNode(TrieNode node, String prefix) {
    if (node.isEndOfWord) {
        System.out.println(prefix + " (*)");
    }
    for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
        System.out.print(prefix + entry.getKey() + " → ");
        printNode(entry.getValue(), prefix + "  ");
    }
}
```

### Verify Trie Integrity

```java
boolean isValid() {
    return isValidNode(root, "");
}

boolean isValidNode(TrieNode node, String path) {
    if (node == null) return false;
    for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
        if (entry.getValue() == null) {
            System.err.println("Null child at " + path + entry.getKey());
            return false;
        }
        if (!isValidNode(entry.getValue(), path + entry.getKey())) return false;
    }
    return true;
}
```

### Count Words

```java
int countWords() {
    return countWords(root);
}

int countWords(TrieNode node) {
    int count = node.isEndOfWord ? 1 : 0;
    for (TrieNode child : node.children.values()) {
        count += countWords(child);
    }
    return count;
}
```

### Unit Testing

```java
@Test
void testTrie() {
    Trie trie = new Trie();
    trie.insert("apple");
    assertTrue(trie.search("apple"));
    assertFalse(trie.search("app"));
    assertTrue(trie.startsWith("app"));
    trie.insert("app");
    assertTrue(trie.search("app"));
    assertFalse(trie.search("ap"));
}

@Test
void testAutocomplete() {
    Trie trie = new Trie();
    trie.insert("dog"); trie.insert("deer");
    trie.insert("deal"); trie.insert("door");
    List<String> results = trie.autocomplete("de");
    assertEquals(2, results.size());
    assertTrue(results.contains("deer"));
    assertTrue(results.contains("deal"));
}
```

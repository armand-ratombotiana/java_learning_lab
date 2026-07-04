# Internals: Trie Implementation Details

## Memory Layout

### Map-Based Children

```java
class TrieNode {
    Map<Character, TrieNode> children;  // HashMap or TreeMap
    boolean isEndOfWord;
    int count;  // optional: word frequency
}
```

Each node: ~48 bytes (object header + HashMap ref + boolean + padding)
Each edge: ~32 bytes (HashMap.Entry with Character key + TrieNode value)

### Array-Based Children (Alphabet = 26)

```java
class TrieNode {
    TrieNode[] children;  // TrieNode[26], most slots null
    boolean isEndOfWord;
}
```

Each node: ~28 bytes (object header + array ref + boolean) + array overhead (40 bytes)
Total per node: ~68 bytes regardless of how many children exist.

## Trade-offs

| Aspect | Map Children | Array Children |
|--------|-------------|---------------|
| Memory (sparse) | Good — O(edges) | Poor — O(nodes × alphabet) |
| Memory (dense) | More overhead per edge | Better — direct array |
| Child lookup | O(1) hash, some constant | O(1) direct index |
| Alphabet size | Unlimited | Fixed |
| Cache behavior | Poor (hash map) | Good (contiguous array) |

## Word Suggestion (Levenshtein Distance)

```java
public List<String> suggest(String word, int maxDistance) {
    List<String> results = new ArrayList<>();
    int[] currentRow = new int[word.length() + 1];
    for (int i = 0; i <= word.length(); i++) currentRow[i] = i;
    for (Map.Entry<Character, TrieNode> entry : root.children.entrySet()) {
        suggestRecursive(entry.getValue(), entry.getKey().toString(),
                        word, currentRow, results, maxDistance);
    }
    return results;
}

private void suggestRecursive(TrieNode node, String prefix, String word,
        int[] prevRow, List<String> results, int maxDistance) {
    int[] currentRow = new int[word.length() + 1];
    currentRow[0] = prevRow[0] + 1;
    for (int i = 1; i <= word.length(); i++) {
        int insert = currentRow[i - 1] + 1;
        int delete = prevRow[i] + 1;
        int replace = prevRow[i - 1] +
            (word.charAt(i - 1) != prefix.charAt(prefix.length() - 1) ? 1 : 0);
        currentRow[i] = Math.min(Math.min(insert, delete), replace);
    }
    if (currentRow[word.length()] <= maxDistance && node.isEndOfWord) {
        results.add(prefix);
    }
    if (min(currentRow) <= maxDistance) {
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            suggestRecursive(entry.getValue(), prefix + entry.getKey(),
                word, currentRow, results, maxDistance);
        }
    }
}
```

## Serialization

Tries can be serialized to arrays:
- **Double-array trie**: base and check arrays for fast traversal
- **JSON**: nested JSON objects
- **Binary**: node count + edges as adjacency list

## Java Ecosystem

- **No standard trie** in `java.util`
- Apache Commons Collections: `PatriciaTrie`
- Guava: no trie (uses `ImmutableSet` + binary search for prefixes)
- Custom implementations common for specific use cases

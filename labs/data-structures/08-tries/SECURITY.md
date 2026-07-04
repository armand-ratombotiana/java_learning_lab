# Security Considerations for Tries

## Denial of Service

### Deep Trie Insertion

```java
// Attacker inserts very long strings
for (int i = 0; i < 1000; i++) {
    trie.insert(longString);  // deep traversal each time
}
// Can cause memory exhaustion or stack overflow in DFS
```

Mitigation: limit maximum string length before insertion.

### Prefix Bomb

```java
// Inserting strings with no shared prefixes creates maximal nodes
trie.insert("a");   // 1 node
trie.insert("b");   // 2 nodes
trie.insert("c");   // 3 nodes
// ... with no shared prefixes, each insert adds L new nodes
```

If an attacker inserts n strings of length L with no shared prefixes, n × L nodes are created. This can exhaust memory.

### Memory Exhaustion via Character Set

Using array-based children with Unicode (65,536 possible characters):
```java
TrieNode[] children = new TrieNode[65536];  // 256KB per node!
```

Never use fixed-size arrays for Unicode alphabets. Use HashMap-based children for general text.

## Information Leakage

- **Search timing**: how long a search takes reveals how deep the prefix matches
- **Failed prefix search**: timing reveals where in the prefix the mismatch occurs
- **Autocomplete results**: may leak sensitive or private terms

## Injection via Malicious Input

If user input is inserted into a trie:
- Very long strings: memory DoS
- Strings with null characters: may terminate traversal early in C/C++ (Java handles Unicode correctly)
- Special characters may create unexpected paths

## Secure Practices

- Limit input string length (e.g., 256 characters)
- Limit total number of words (bound memory)
- Use HashMap-based children for variable alphabets
- Normalize input (lowercase, trim whitespace)
- Avoid exposing autocomplete results without access control
- Clear sensitive data from tries after use

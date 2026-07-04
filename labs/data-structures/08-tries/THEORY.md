# Theory: Tries

## Trie Definition

A **trie** (prefix tree) is an N-ary tree where:
- Each node represents a **prefix** (or partial prefix) of keys
- Each edge is labeled with a character
- Nodes store a flag indicating whether they represent the end of a key
- The root represents an empty string

## Node Representation

```java
class TrieNode {
    Map<Character, TrieNode> children;
    boolean isEndOfWord;
    // Optional: int count for word frequency
}
```

Alternative: array of size 26 (for lowercase letters) for faster access but more memory.

## Core Operations

- **Insert**: O(L) where L = string length. Traverse/create nodes for each character. Mark last node as terminal.
- **Search**: O(L). Traverse nodes; check terminal flag at end.
- **StartsWith (prefix search)** : O(L). Traverse nodes; return true if traversal succeeds.
- **Delete**: O(L). Traverse to node, unmark terminal. Prune unnecessary branches.

## Time Complexity

| Operation | Trie | HashMap | BST |
|-----------|------|---------|-----|
| Insert | O(L) | O(L)* | O(L log n) |
| Search | O(L) | O(L)* | O(L log n) |
| Prefix search | O(L) | O(nL) | O(nL) |
| Delete | O(L) | O(L)* | O(L log n) |
| List all words | O(NL) | O(NL) | O(NL) |

L = string length, n = number of keys, N = number of matches

\*Plus hash computation overhead

## Memory Analysis

Trie total memory: O(total characters across all keys × branching factor)

- Array-based children: O(26 × nodes) for lowercase letters
- Map-based children: O(edges) = O(total characters inserted)

## Trie Variants

- **Compressed trie (Radix tree)** : merge single-child nodes into one
- **Ternary search tree**: binary tree per character, less memory
- **Suffix tree**: trie of all suffixes (used in string matching)
- **Hamming distance trie**: trie with wildcard support

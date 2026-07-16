# Mathematical Foundation of the Trie

## 📐 Time Complexity Analysis
Let $L$ be the length of the string being searched or inserted, and $N$ be the number of strings in the Trie.

1. **Insertion**: $O(L)$. We traverse the characters of the string one by one.
2. **Search**: $O(L)$. We also traverse character by character.
3. **Prefix Search**: $O(L)$.

**Comparison with HashMap**:
A `HashMap<String, T>` also has a search complexity of $O(L)$ because it must compute the hash code of the string (which requires looking at every character).
However, a Trie is superior for:
- **Prefix Matching**: Finding all words starting with "app" is $O(L)$ in a Trie, but $O(N \times L)$ in a HashMap (you'd have to check every key).
- **Space Efficiency**: If many words share the same prefix (e.g., "apple", "apply", "applied"), they share the same physical nodes in the Trie.

## 📈 Space Complexity
$O(W \times L)$ where $W$ is the number of words and $L$ is the average length.
In the worst case (no shared prefixes), a Trie uses more memory than a HashMap because of the overhead of `Node` objects and their child arrays.
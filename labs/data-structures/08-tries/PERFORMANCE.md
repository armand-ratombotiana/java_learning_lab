# Performance: Tries

## Time Complexity

| Operation | Trie | HashMap | BST |
|-----------|------|---------|-----|
| Insert | O(L) | O(L)* | O(L log n) |
| Search | O(L) | O(L)* | O(L log n) |
| Prefix search | O(L + M) | O(nL) | O(nL) |
| Delete | O(L) | O(L)* | O(L log n) |

L = string length, n = number of keys, M = number of matches

\*Plus hash computation

## Memory Comparison

For 100,000 English words (average length ~7):

| Structure | Estimated Memory |
|-----------|-----------------|
| Trie (HashMap children) | ~10-15 MB |
| Trie (array children 26) | ~25-35 MB |
| HashMap<String> | ~8-12 MB |
| Sorted array of strings | ~3-5 MB |
| Radix tree (compressed) | ~5-8 MB |

## Trie vs HashMap

| Aspect | Trie | HashMap |
|--------|------|---------|
| Exact search | O(L) | O(L)* |
| Prefix search | O(L + M) | O(nL) |
| Memory | Higher (node overhead) | Lower (single array) |
| Hash computation | None | O(L) per operation |
| Worst-case | O(L) all operations | O(nL) with collisions |
| Ordering | Lexicographic traversal | None |
| Wildcard search | Easy (DFS) | Impossible |

## Node Count Analysis

For n strings of average length L:
- Trie nodes ≤ n × L (worst-case, no shared prefixes)
- Trie nodes ≥ n (best case, all strings share root)
- Typically: nodes ≈ n × L × (1 - sharing_factor)

## Cache Behavior

- **HashMap-backed children**: poor — hash map entry objects scattered in memory
- **Array-backed children**: better — contiguous array for each node
- **Trie traversal**: follows pointers across heap (cache-unfriendly)
- **HashMap**: single object reference per key (cache-friendly)

## Optimizations

- **Ternary search tree**: O(log n) average, less memory than trie
- **Double-array trie**: two arrays (base, check) for O(1) traversal
- **Radix tree**: compress single-child edges into one node
- **Burst trie**: hybrid of trie and array for memory efficiency
- **HAT-trie**: cache-conscious trie using hash tables at leaves

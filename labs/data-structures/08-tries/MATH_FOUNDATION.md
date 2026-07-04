# Math Foundation: Tries

## Trie Node Count

For n strings of average length L over alphabet size A:
- **Worst-case** (no shared prefixes): n × L nodes
- **Best-case** (all strings share prefixes): ≤ n × L, often much less
- **Array-based children**: each node costs A × pointer_size
- **Map-based children**: each edge costs hash_entry + key + pointer

## Trie Search Complexity

```
Trie search length: L (string length)
Hash table search: O(L) + hash of L chars

For n keys of length L:
  Trie: O(L) always
  Hash: O(L) average, O(nL) worst (hash collision)
```

## Random Tries (Expected Behavior)

For n random strings of length L over alphabet size A:
- Expected number of nodes: O(n)
- Average depth of nodes: O(log n)
- Expected size ratio vs naive: A/(A-1) × n (for binary tries)

## Autocomplete Search Space

For a prefix of length P with average branching factor A:
- Number of nodes in subtree at depth P:
  - Expected: A^P (could be large!)
  - Actual: min(A^P, n - P + 1)
- DFS of subtree visits each node once
- Return top K: collect all, sort by frequency, take K

## Ternary Search Tree Comparison

TST memory vs trie:
- **Trie**: O(nodes × branching factor) — each node has A pointers
- **TST**: O(n × L + A) — each node has 3 pointers
- Typically: TST uses 3-10x less memory than array-backed trie

## Compression Ratios

Radix tree (compressed trie) vs standard trie:
- Standard trie: n × L nodes (worst case)
- Radix tree: O(m) where m = number of unique prefixes
- Typical compression: 40-70% reduction in node count

## Double-Array Trie

Two arrays (base and check) enable O(1) traversal:
- base[i] = base index for transitions from state i
- check[i] = check index confirming valid transition
- Transition on char c: next = base[i] + c, valid if check[next] = i
- Space: ~2 × nodes × 4 bytes (integer arrays)

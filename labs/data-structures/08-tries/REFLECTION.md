# Reflection: Tries

## What I Learned

- Tries are prefix trees optimized for string operations, especially prefix search
- Insert, search, and prefix search are all O(L) where L = string length
- Memory is the main trade-off: O(nodes × branching factor) vs O(nL) for HashMap
- HashMap-based children are flexible; array-based children are faster for fixed alphabets
- Autocomplete, spell check, IP routing are key trie applications
- No standard trie in Java — must implement custom

## Questions to Consider

1. Why doesn't Java have a standard trie in the Collections Framework?
2. When is a trie better than a HashSet for word lookup? (prefix operations, ordering)
3. How does the memory of a trie compare to a HashMap for a dictionary of 100K words?
4. What is the advantage of compressing a trie (radix tree) in terms of memory and performance?
5. How would you implement a concurrent trie for a multi-threaded autocomplete system?

## Connections

Tries connect to:
- **Trees** (trie is an N-ary tree)
- **Hash tables** (alternative for string storage with different trade-offs)
- **Graphs** (suffix trees are tries of all suffixes)
- **Compression** (radix trees compress single-child paths)
- **Networking** (binary tries for routing tables)
- **Autocomplete** (the most common real-world use case)

## Self-Assessment

- [ ] Can implement a trie with insert, search, startsWith
- [ ] Can implement autocomplete using DFS from a prefix node
- [ ] Understand array vs HashMap children trade-offs
- [ ] Can implement trie with delete and branch pruning
- [ ] Can solve wildcard search with backtracking
- [ ] Know when to use trie vs other data structures

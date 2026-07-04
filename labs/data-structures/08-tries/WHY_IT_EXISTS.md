# Why Tries Exist

Tries exist to solve the problem of **efficient prefix-based operations** on strings.

## Problems with Other Structures

- **Arrays**: O(nL) to find all strings with a given prefix — linear scan of all strings
- **BSTs**: O(L log n) search, but prefix search still requires O(nL) traversal
- **Hash tables**: O(1) exact search, but cannot do prefix search at all
- **Sorting**: binary search on sorted array works for exact match, not prefix

## What Tries Provide

| Need | Trie Solution |
|------|--------------|
| Prefix search | O(L) — follow characters of prefix |
| Autocomplete | O(L + M) where M is number of matches |
| Spell check | O(L) search + suggestions via traversal |
| Longest common prefix | Simple traversal until branch |
| Word frequency | Store count at terminal nodes |
| Wildcard matching | DFS with backtracking |
| IP routing (CIDR) | Binary trie for prefix matching |

## Key Insight

Tries **share prefixes** — the common prefix "pro" is stored once for "program", "progress", "produce". This is both a memory optimization and a search speed advantage.

Tries are essential for any system that needs **prefix-based lookup**: search engines (autocomplete), databases (LIKE queries), networking (routing tables), and dictionaries.

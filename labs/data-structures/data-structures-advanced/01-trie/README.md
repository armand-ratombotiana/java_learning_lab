# 01 Trie (Prefix Tree)

A trie (digital tree / prefix tree) is an ordered tree data structure used to store a dynamic set of strings, keyed by character prefixes.

## Learning Objectives
- Understand prefix-based tree traversal
- Implement insert, search, delete, startsWith
- Build auto-complete and prefix matching
- Analyze space vs time trade-offs

## Complexity Table

| Operation | Time  | Space |
|-----------|-------|-------|
| Insert    | O(L)  | O(L·A)|
| Search    | O(L)  | O(1)  |
| Delete    | O(L)  | O(1)  |
| Prefix Scan| O(L+R)| O(R)  |

L = word length, A = alphabet size, R = results count
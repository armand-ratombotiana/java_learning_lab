# Interview Questions: Tries

## Easy

1. **Implement Trie (LeetCode 208)** — Insert, search, startsWith.

2. **Longest common prefix** — Find LCP of an array of strings (trie or simple scan).

3. **Word frequency** — Count word occurrences using a trie.

## Medium

4. **Autocomplete system** — Return top k suggestions for a given prefix (trie + DFS).

5. **Word break** — Check if string can be segmented using dictionary words (DP + trie).

6. **Replace words** — Replace words with their shortest root from a dictionary (trie prefix traversal).

7. **Map sum pairs (LeetCode 677)** — Insert key-value, sum all values for a prefix.

8. **Add and search word (LeetCode 211)** — Support '.' wildcard matching.

9. **Search suggestions system (LeetCode 1268)** — Return top 3 suggestions for each prefix of a search word.

## Hard

10. **Word search II (LeetCode 212)** — Find all words from dictionary in a 2D board. Use trie for pruning.

11. **Palindrome pairs (LeetCode 336)** — Find all pairs where concatenation forms a palindrome.

12. **Concatenated words (LeetCode 472)** — Find words that are concatenations of other words in the list.

13. **Design search autocomplete (LeetCode 642)** — Real-time autocomplete with frequency ranking.

## Key Patterns

- **DFS + Trie**: traverse both trie and another structure (grid, string)
- **Backtracking + Trie**: prune search paths when prefix doesn't exist
- **DP + Trie**: word break optimization using trie for substring matching
- **Wildcard search**: DFS with backtracking for '.' characters

## Java-Specific Topics

- No standard trie in Java — implement from scratch
- HashMap vs array children decision
- Recursive DFS for autocomplete
- Using `computeIfAbsent` for clean insertion:
  ```java
  current = current.children.computeIfAbsent(c, k -> new TrieNode());
  ```

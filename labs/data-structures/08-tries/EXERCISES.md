# Exercises: Tries

## Basic

1. **Trie implementation** — Implement a trie with insert, search, and startsWith methods (LeetCode 208).

2. **Array-backed trie** — Implement a trie for lowercase English letters using `TrieNode[26]` instead of HashMap.

3. **Word counter** — Modify your trie to count how many times each word was inserted.

4. **Prefix counter** — Add a method that returns how many words start with a given prefix.

## Intermediate

5. **Autocomplete** — Implement autocomplete that returns all words with a given prefix, limited to top K results.

6. **Longest common prefix** — Find the longest common prefix among all words in the trie.

7. **Spell checker** — Given a dictionary (in a trie), suggest corrections for misspelled words using Levenshtein distance ≤ 2.

8. **Word search in grid** — Given a 2D board and a list of words, find all words on the board (LeetCode 212).

9. **Map sum pairs** — Implement a MapSum class with insert(key, val) and sum(prefix) (LeetCode 677).

## Advanced

10. **Word break** — Given a string and a dictionary, determine if the string can be segmented into dictionary words (DP + trie).

11. **Replace words** — Replace words in a sentence with their shortest root from a dictionary (trie prefix replacement).

12. **Add and search word** — Design a data structure supporting addWord and search, where '.' can match any character (LeetCode 211).

13. **Compressed trie (radix tree)** — Implement a compressed trie where single-child nodes are merged into labeled edges.

14. **Palindrome pairs** — Given a list of words, find all pairs where word[i] + word[j] forms a palindrome (trie of reversed words).

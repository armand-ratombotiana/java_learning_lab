# String Algorithms — Theoretical Foundation

## Naive Pattern Matching
- Slide pattern over text, compare each position
- Time: O(nm) where n = text length, m = pattern length

## Knuth-Morris-Pratt (KMP)
- Precomputes failure function (longest proper prefix that is also suffix)
- Avoids re-examining matched characters
- Time: O(n + m)

## Rabin-Karp
- Uses rolling hash to compare pattern with substrings
- Hash collision resolution needed
- Average: O(n + m), Worst: O(nm)
- Ideal for multiple pattern search

## Trie (Prefix Tree)
- Tree where each node represents a character
- Path from root to node represents a prefix
- Search: O(m) for m-length string
- Space: O(alphabet × total characters)

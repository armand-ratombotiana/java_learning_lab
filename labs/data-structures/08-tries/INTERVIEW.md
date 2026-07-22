# Interview Questions: Tries

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 208 Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/) | Medium | Amazon, Google, Meta, Microsoft, Apple | Trie insert/search/startsWith |
| [LC 211 Design Add and Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/) | Medium | Amazon, Google, Meta, Microsoft, Oracle | Trie with wildcard DFS |
| [LC 212 Word Search II](https://leetcode.com/problems/word-search-ii/) | Hard | Amazon, Google, Meta, Microsoft, Apple | Trie + backtracking on grid |
| [LC 336 Palindrome Pairs](https://leetcode.com/problems/palindrome-pairs/) | Hard | Google, Amazon, Meta | Trie-based pair finding |
| [LC 642 Design Search Autocomplete System](https://leetcode.com/problems/design-search-autocomplete-system/) | Hard | Google, Amazon, Meta | Trie + frequency ranking |
| [LC 425 Word Squares](https://leetcode.com/problems/word-squares/) | Hard | Google, Amazon, Meta | Trie + backtracking |
| [LC 677 Map Sum Pairs](https://leetcode.com/problems/map-sum-pairs/) | Medium | Amazon, Google | Trie with cumulative sum |
| [LC 1268 Search Suggestions System](https://leetcode.com/problems/search-suggestions-system/) | Medium | Amazon, Google, Meta | Trie / binary search prefix |
| [LC 745 Prefix and Suffix Search](https://leetcode.com/problems/prefix-and-suffix-search/) | Hard | Google, Amazon | Trie of combined words |
| [LC 472 Concatenated Words](https://leetcode.com/problems/concatenated-words/) | Hard | Amazon, Meta, Google | Trie + DFS / DP |
| [LC 720 Longest Word in Dictionary](https://leetcode.com/problems/longest-word-in-dictionary/) | Medium | Amazon, Google | Trie / HashSet |
| [LC 676 Implement Magic Dictionary](https://leetcode.com/problems/implement-magic-dictionary/) | Medium | Google, Amazon, Meta | Trie with one-char variation |

## NeetCode Reference
NeetCode 150: Tries category — 3 problems (Implement Trie, Design Add and Search, Word Search II). NeetCode 250 adds palindrome pairs and autocomplete.

## Company-Specific Questions

### Google
- Implement autocomplete with top-k suggestions ranked by frequency (LC 642)
- Design a prefix tree for IP address routing (longest prefix matching)
- Word squares — generate all valid word squares using a trie (LC 425)
- How would you compress a trie to reduce memory? (Compressed trie / radix tree)

### Microsoft
- Implement a trie supporting insert, search, and prefix search. What is the space complexity?
- How would you implement a trie for URL prefix matching on a load balancer?
- Design a spell checker that suggests corrections for misspelled words (edit distance + trie)

### Meta
- Word Search II — how does the trie prune the backtracking search? Why is this more efficient than HashSet of words?
- Search suggestions system — what's the best approach when the dictionary has 100K+ words?
- Replace words with their shortest root from a dictionary (LC 648) — how does a trie help?

### Amazon
- Design a product search autocomplete that returns top 5 products as you type
- Palindrome pairs — how does the trie + reverse word approach work?
- How would you implement a routing trie for a database query planner?

### Apple
- Contact search on iPhone — how would you implement prefix-based contact lookup?
- Autocorrect keyboard — trie with n-gram frequency for next word prediction
- How would you store dictionary words for a word game like Scrabble?

### Oracle
- How does the JVM use trie-like structures for class name resolution?
- Implement a pattern matching algorithm with wildcards using a trie
- What are the trade-offs between array-based children (TrieNode[26]) and HashMap-based children for a trie?

## Real Production Scenarios

- **Scenario 1: Search Autocomplete** — A search engine serves millions of queries per second. A trie of popular queries is built offline from search logs. When a user types "ap", the system follows 'a' → 'p' in the trie and returns the top 10 suggestions with frequencies precomputed at each node.

- **Scenario 2: IP Routing Table** — A network router uses a Patricia trie (radix tree) for longest prefix matching of IP addresses. The trie compresses shared prefixes, enabling O(1) to O(32) matching of IPv4 addresses against thousands of routing table entries.

- **Scenario 3: Spell Checker** — An email client checks spelling against a dictionary stored in a trie (~100K words, ~1MB). When a word is not found, the system generates all words within edit distance 1 or 2 using recursive trie traversal with bounded depth. This provides suggestions in milliseconds.

## Interview Tips

- Time: O(L) for insert/search/startsWith where L = word length, O(L·N) worst-case for DFS on trie with N words
- Space: O(N·L) worst case for naive trie (each node stores 26 references). Compressed tries (radix trees) reduce space
- Common edge cases: empty string, prefix equals the word, duplicate insertions, very long words, non-alphabetic characters
- For wildcard search (LC 211), DFS on the trie handles '.' characters efficiently
- Array-based children (`TrieNode[26]`) is faster but uses more memory than HashMap-based children
- Each node typically stores: references to children, a boolean flag for end-of-word, and optionally a frequency counter

## Java-Specific Considerations

- No standard trie class in Java — must implement from scratch
- Node structure: `class TrieNode { TrieNode[] children = new TrieNode[26]; boolean isEnd; }` for lowercase letters
- HashMap-based trie: `class TrieNode { Map<Character, TrieNode> children = new HashMap<>(); boolean isEnd; }`
- Use `computeIfAbsent` for clean insertion: `node = node.children.computeIfAbsent(c, k -> new TrieNode())`
- Recursive DFS on trie may overflow stack for deep recursion — use iterative approach with explicit stack if needed
- Memory optimization: arrays of length 26 assume lowercase English letters; adjust for Unicode
- For performance: pre-allocate trie nodes in an `ArrayList<TrieNode>` pool to reduce GC pressure
- Java's `String.charAt()` is O(1) but `String.substring()` is O(n) in Java 6- (O(1) from Java 7+) — be aware in trie-traversal code

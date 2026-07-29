# Interview Questions: Trie (Prefix Tree)

## 15 FAANG-Style Interview Questions

### Question 1
> Implement a Trie with insert, search, and startsWith methods.

**Answer:**
This is the core trie implementation (LC 208). Use a `TrieNode` class with:
- `TrieNode[] children = new TrieNode[26]`
- `boolean isEndOfWord`

**Insert**: Walk characters, create nodes as needed, mark end.
**Search**: Walk characters, check `isEndOfWord` at the end.
**StartsWith**: Walk characters, return `true` if path exists.

**Complexity**: O(L) time, O(L·A) space per insertion.

**Follow-up**: How would you handle Unicode? Use `HashMap<Character, TrieNode>`.

**LeetCode:** [208. Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/)

---

### Question 2
> Design a data structure that supports adding words and searching with '.' wildcard.

**Answer:**
This is LC 211 (Design Add and Search Words Data Structure). Extend trie:
- Same insert logic
- Search becomes DFS: when char is '.', recurse on all non-null children
- When char is a letter, follow that child only

```java
public boolean search(String word) {
    return searchHelper(root, word, 0);
}

private boolean searchHelper(TrieNode node, String word, int idx) {
    if (node == null) return false;
    if (idx == word.length()) return node.isEndOfWord;
    char c = word.charAt(idx);
    if (c == '.') {
        for (TrieNode child : node.children) {
            if (searchHelper(child, word, idx + 1)) return true;
        }
        return false;
    } else {
        return searchHelper(node.children[c - 'a'], word, idx + 1);
    }
}
```

**Complexity**: O(26^W) worst case for wildcard-only search (e.g., "....."), but O(L) for literal words.

---

### Question 3
> Given a 2D board of characters and a dictionary of words, find all words that can be formed by adjacent letters (no repeats). Optimise for large dictionaries.

**Answer:**
This is LC 212 (Word Search II). Use Trie + DFS:
1. Build trie from dictionary words
2. DFS each cell on the board
3. At each step, follow current character in trie
4. If node.isEndOfWord, add to results (and optionally remove from trie to avoid duplicates)
5. Mark visited cells (temporary '#' or boolean array)

**Optimisation**: Remove found words from trie (mark `isEndOfWord = false`) to avoid re-processing duplicates.

**Complexity**: O(N·M·4^L) where L = max word length, but trie prunes impossible paths.

---

### Question 4
> Implement a function to replace words in a sentence with their shortest root from a dictionary.

**Answer:**
LC 648 (Replace Words). Build trie from root words. For each word in the sentence:
1. Walk trie character by character
2. If a node with `isEndOfWord` is reached, stop — that's the shortest root
3. If trie path ends before word ends, use the original word

**Key**: Shortest root = first `isEndOfWord` encountered during trie traversal.

---

### Question 5
> Given a stream of characters, design a data structure to find the longest word in a dictionary that can be formed by concatenating words from the stream.

**Answer:**
Use trie with breadth-first or depth-first search. Variant of LC 720 (Longest Word in Dictionary).

**Approach**: Insert all words into trie. Then DFS from root, tracking depth. Only continue down paths where every prefix (every node) is an end of a word. This ensures the word can be built one character at a time.

---

### Question 6
> How would you implement a prefix-based search for millions of domain names (reverse DNS lookup)?

**Answer:**
Domain names are reversed (e.g., "com.google.mail") so that common suffixes become common prefixes in the trie. Use a compressed trie (radix tree) for memory efficiency. Each node stores a substring (not a single character).

**Optimisation**: Store first n characters as fixed array, rest as list (HAT-trie approach).

---

### Question 7
> Design an autocomplete system that returns top-k suggestions for a given prefix, ranked by frequency.

**Answer:**
Store at each trie node a min-heap (or priority queue) of top-k suggestions in that subtree. When inserting a word, update all nodes along its path.

**Alternative**: Post-process with DFS from prefix node, then sort by frequency. Precomputing top-k at each node gives O(1) response time at the cost of O(N·k) storage.

**LeetCode:** [Search Suggestions System](https://leetcode.com/problems/search-suggestions-system/) (LC 1268)

---

### Question 8
> Given a list of strings, find all palindrome pairs (i, j) such that words[i] + words[j] is a palindrome.

**Answer:**
LC 336 (Palindrome Pairs). Build trie of reversed words. For each word:
1. Check if the word's reverse exists as complete word → empty string case
2. Check if word's prefix matches a trie entry and the remaining suffix is palindrome
3. Check if trie entry's prefix matches word and remaining suffix is palindrome

**Trie extension**: Store index of word at each end node, and list of indices where the node's remaining path forms a palindrome.

---

### Question 9
> Given a trie and a string, find the longest prefix of the string that exists in the trie.

**Answer:**
Walk the trie character by character while tracking the last `isEndOfWord` node encountered. Return the prefix up to that point. If no full word is found along the path, return empty string (or the prefix up to the last end-of-word).

Used in IP routing (longest prefix match), dictionary word break, and text auto-completion.

---

### Question 10
> Implement a trie that supports delete without memory leak. What's the algorithm for pruning empty nodes?

**Answer:**
Recursive post-order traversal. At each node:
1. Recurse on the child matching the next character
2. After recursion, if child has `isEndOfWord == false` and `prefixCount == 0` (or all children are null), set the child pointer to null
3. Decrement current node's `prefixCount`

This ensures nodes not shared by other words are garbage collected.

---

### Question 11
> Compare trie, hash set, and binary search tree for a set of 1M strings. When would you choose each?

**Answer:**

| Criteria | Trie | HashSet | BST |
|----------|------|---------|-----|
| Exact search | O(L) | O(L) avg | O(L·log n) |
| Prefix query | O(L) | O(n·L) | O(n·L) |
| Memory | O(N·L·A) | O(N·L) | O(N) + strings |
| Ordering | Lexicographic | None | Sort order |
| Insert cost | O(L) | O(L) avg | O(L·log n) |

**Choose trie** when prefix queries dominate. **Choose HashSet** for exact-match-only, memory-constrained. **Choose BST** when ordered traversal needed.

---

### Question 12
> How does Java's `TreeMap` compare to a trie for prefix-based operations?

**Answer:**
`TreeMap` (Red-Black Tree) supports:
- `subMap(prefix, prefix + Character.MAX_VALUE)` to get all entries with a prefix
- O(log n) prefix query + O(k) iteration

Trie is faster (O(L) vs O(log n)) for the prefix lookup itself, but `TreeMap` uses less memory and supports all ordered map operations.

---

### Question 13
> Design a data structure to store and search for patterns in a large set of DNA sequences (substring queries).

**Answer:**
Use a suffix trie or suffix tree. For processing whole genomes, use a suffix array (see Lab 03). A suffix trie stores all suffixes of a string, enabling O(L) pattern search. However, suffix arrays use less memory and can be built in O(n) time.

---

### Question 14
> Given a trie, how do you serialise it for storage/transmission? How do you deserialise?

**Answer:**
**Serialisation (DFS pre-order):**
- For each node, write `isEndOfWord` + number of children
- Write each child's character and recursively serialise

**Binary format:**
```
[byte: flags (isEndOfWord | hasChildren)] [child indices...]
```

**Deserialisation**: Read byte-by-byte, create nodes, attach children.

**Alternative**: Use adjacency list with character labels. More compact: use BFS level-order.

---

### Question 15
> What's the difference between a trie and a deterministic finite automaton (DFA)?

**Answer:**
A trie IS a DFA where:
- States = trie nodes
- Alphabet = characters
- Transitions = child pointers
- Accept states = nodes with `isEndOfWord = true`
- Start state = root

The trie DFA recognises the set of strings exactly. Compressing the trie merges equivalent states (Aho-Corasick adds failure links for pattern matching automaton).

---

### Question 16
> Implement an in-memory file system with mkdir, ls, addContentToFile, readContentFromFile. Solve using trie.

**Answer:**
LC 588 (Design In-Memory File System). Use trie where each node represents a directory:
- `children`: Map<String, Node> where key = file/dir name
- `isFile`: leaf marker
- `content`: file content string (only for file nodes)

**ls(path)**: Navigate to path, return directory listing (children names) or file name.
**mkdir(path)**: Create intermediate directories as needed.
**addContentToFile(path, content)**: Navigate (create directories), append content to file node.
**readContentFromFile(path)**: Navigate, return file content.

---

### Question 17
> Given a non-empty list of words, find the longest word that can be built one character at a time where each intermediate prefix is also a word.

**Answer:**
LC 720 (Longest Word in Dictionary). Insert all words into trie. DFS from root, only proceeding through nodes where `isEndOfWord == true` (because each prefix must be a complete word). Track the deepest valid path.

---

### Question 18
> How would you implement a concurrent trie in Java?

**Answer:**
- `ConcurrentHashMap<Character, TrieNode>` for children (thread-safe)
- `AtomicBoolean` or `AtomicIntegerFieldUpdater` for `isEndOfWord`
- **Read**: No locking needed if structure is read-only after build
- **Write**: Use `putIfAbsent` for child insertion, CAS for end-of-word marking
- **Alternative**: Copy-on-write — build new root for batch updates, atomically swap

**Java standard library**: There is no concurrent trie. For concurrent prefix search, use `ConcurrentSkipListMap` (see Lab 06) with `subMap` for prefix queries.

---

### Question 19
> Given a 2D grid of characters, find if a word exists (adjacent cells, no repeats). Optimise for many words.

**Answer:**
For a single word: DFS with backtracking — O(N·M·4^L).
For many words (LC 212): Build trie of all words, DFS once over the grid, pruning when trie path ends.

**Further optimisation**: Use visited bitset per cell (int bitmask) instead of boolean array for faster copy on backtracking.

---

### Question 20
> Explain how a compressed trie (radix tree) reduces memory. When would you use it over a standard trie?

**Answer:**
A radix tree merges nodes with single children into a single edge labeled with a substring. This reduces node count from O(N·L) to O(N) in the best case (few branches).

**Use radix tree when:**
- Strings have long common prefixes (domain names, IP addresses)
- Memory is constrained (embedded systems, kernel)
- Insert/delete is infrequent (compressed trie is harder to update)
- **Stick with standard trie when:** frequent updates, or short strings with varied prefixes

**Real-world**: Linux kernel uses radix tree for page cache and IPv4 routing.
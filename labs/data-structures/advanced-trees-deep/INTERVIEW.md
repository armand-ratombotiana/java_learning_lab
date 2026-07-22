# Interview Questions: Advanced Trees (Deep Dive)

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 208 Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/) | Medium | Amazon, Google, Meta, Microsoft | Trie insert/search/startsWith |
| [LC 211 Design Add and Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/) | Medium | Amazon, Google, Meta, Microsoft | Trie with wildcard DFS |
| [LC 212 Word Search II](https://leetcode.com/problems/word-search-ii/) | Hard | Amazon, Google, Meta, Microsoft | Trie + backtracking |
| [LC 98 Validate Binary Search Tree](https://leetcode.com/problems/validate-binary-search-tree/) | Medium | Amazon, Meta, Google, Microsoft, Apple | BST property check |
| [LC 230 Kth Smallest Element in a BST](https://leetcode.com/problems/kth-smallest-element-in-a-bst/) | Medium | Amazon, Meta, Google, Microsoft, Oracle | Inorder traversal |
| [LC 235 Lowest Common Ancestor of a BST](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/) | Medium | Amazon, Google, Microsoft, Meta | BST property navigation |
| [LC 450 Delete Node in a BST](https://leetcode.com/problems/delete-node-in-a-bst/) | Medium | Amazon, Meta, Google, Microsoft | BST deletion |
| [LC 108 Convert Sorted Array to BST](https://leetcode.com/problems/convert-sorted-array-to-binary-search-tree/) | Easy | Amazon, Google, Meta, Microsoft | Divide and conquer |
| [LC 110 Balanced Binary Tree](https://leetcode.com/problems/balanced-binary-tree/) | Easy | Amazon, Meta, Google, Microsoft | Height-balanced check |
| [LC 449 Serialize and Deserialize BST](https://leetcode.com/problems/serialize-and-deserialize-bst/) | Medium | Google, Amazon, Meta | BST encoding |
| [LC 173 BST Iterator](https://leetcode.com/problems/binary-search-tree-iterator/) | Medium | Meta, Amazon, Google, Microsoft | Controlled inorder traversal |
| [LC 642 Design Search Autocomplete System](https://leetcode.com/problems/design-search-autocomplete-system/) | Hard | Google, Amazon | Trie + frequency ranking |
| [LC 388 Longest Absolute File Path](https://leetcode.com/problems/longest-absolute-file-path/) | Medium | Google, Meta | Trie-like tree traversal |
| [LC 336 Palindrome Pairs](https://leetcode.com/problems/palindrome-pairs/) | Hard | Google, Amazon, Meta | Trie-based pair finding |
| [LC 745 Prefix and Suffix Search](https://leetcode.com/problems/prefix-and-suffix-search/) | Hard | Google, Amazon | Trie of combined prefix+suffix |
| [LC 677 Map Sum Pairs](https://leetcode.com/problems/map-sum-pairs/) | Medium | Amazon, Google | Trie with cumulative sum |
| [LC 676 Implement Magic Dictionary](https://leetcode.com/problems/implement-magic-dictionary/) | Medium | Google, Amazon | Trie with one-character variation |

## NeetCode Reference
NeetCode 150: Trees and Tries categories cover BST, Trie, and tree operations. The Trie and BST problem families are essential for tree-focused interviews.

## Company-Specific Questions

### Google
- Implement a Red-Black tree from scratch with insert and fixup rotations
- Design an autocomplete system using a trie with top-k suggestions by frequency (LC 642)
- How does Bigtable use SSTables and bloom filters with LSM-tree structure (B-tree variant)?
- Given a prefix, find all words in a dictionary that have this prefix (trie DFS)

### Microsoft
- AVL tree vs Red-Black tree — compare balancing strategies and real-world usage
- Implement a B-tree with split and merge operations (disk-based indexing)
- How does SQL Server use B+ trees for clustered/non-clustered indexes?

### Meta
- Word Search II on a 2D board using a trie for pruning (LC 212)
- Design a type-ahead search that returns results as you type
- How would you store and query hierarchical data (e.g., Facebook comments tree)?

### Amazon
- Design DynamoDB's index structure (B-tree based) — how does range query work?
- Implement a trie for URL prefix matching in a load balancer rule engine
- Splay tree for cache-friendly access patterns in a recommendation engine

### Apple
- How are Core Data's persistent stores indexed (B-tree / hash-based)?
- Trie-based contact search with fuzzy matching on iPhone
- Implement a balanced BST for sorted display of music library metadata

### Oracle
- How does Oracle Database use B+ trees for indexes? What is the fan-out factor?
- Red-Black tree implementation in Java's TreeMap — explain the color fixup algorithm
- Why does the JVM use a trie-like structure for class loading / package lookup?

## Real Production Scenarios

- **Scenario 1: Database Indexing (B+ Tree)** — A relational database stores primary key indexes as B+ trees with high fan-out (500+ children per node). This minimizes disk I/O by keeping tree height low (3-4 levels for millions of rows). Range queries traverse leaf nodes via sibling pointers.

- **Scenario 2: Autocomplete Engine** — A search engine builds a prefix trie of billions of search queries with frequency counts at each node. The trie is memory-mapped and loaded from disk. Top-k suggestions per prefix are precomputed and cached at trie nodes.

- **Scenario 3: Compiler Symbol Table** — A compiler uses a hash map for the symbol table during parsing, but the global scope uses a balanced BST (AVL/Red-Black) when symbol lookup must be ordered for debug symbol generation.

## Interview Tips

- Time: O(log n) for balanced tree operations, O(h) for BST (worst O(n) if unbalanced), O(|S|) for trie operations (string length)
- Space: O(n) for most trees; O(n * avgLength) for trie; balancing metadata (color bit or height) adds 1-4 bytes per node
- Common edge cases: empty tree, single node, tree becomes a chain (BST without balancing), duplicate keys, skew in insert order
- Always clarify allowed operations: rotations allowed? can we use parent pointers? recursive vs iterative?

## Java-Specific Considerations

- `TreeMap<K,V>` / `TreeSet<E>` — Red-Black tree backed, O(log n) guarantees, `NavigableMap`/`NavigableSet` interface
- No built-in trie class — must implement from scratch using array children (faster) or HashMap children (flexible)
- No built-in AVL, Splay, B-Tree, or Red-Black tree (except TreeMap's internal tree) — these are implementation exercises
- Recursive tree algorithms may cause StackOverflow on deep trees — consider iterative approaches with explicit stack
- `Comparator` vs `Comparable` — TreeMap uses Comparator if provided, otherwise relies on Comparable natural order
- SubMap / headMap / tailMap return views backed by original TreeMap — modifications reflected in both
- Memory overhead: each TreeMap entry node has color bit, left/right/parent pointers (3 references + key + value + color)
- For tries: `HashMap<Character, TrieNode>` children simplifies implementation but has higher overhead than `TrieNode[26]`

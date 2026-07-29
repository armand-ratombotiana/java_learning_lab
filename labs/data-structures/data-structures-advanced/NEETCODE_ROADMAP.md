# NeetCode Roadmap: Advanced Data Structures

This guide maps the 10 advanced data structures to the NeetCode 150 / Blind 75 problem list, showing how each structure appears in coding interviews.

## NeetCode 150 Integration

### 01 Trie (Prefix Tree)

**NeetCode Problems:**
- **208. Implement Trie (Prefix Tree)** — Core implementation
- **211. Design Add and Search Words Data Structure** — Trie with wildcard '.'
- **212. Word Search II** — Trie + DFS/backtracking
- **648. Replace Words** — Trie prefix replacement
- **720. Longest Word in Dictionary** — Trie DFS

**NeetCode Roadmap Section:** Trees → Tries

**Blind 75 relevance:** Not in Blind 75, but appears in many company-specific lists

**Recommended study:**
1. Implement insert, search, startsWith from memory
2. Solve 211 (wildcard search) — DFS on trie
3. Solve 212 (Word Search II) — trie + grid DFS (hard but common)
4. Solve 648 (Replace Words) — practical usage

### 02 Bloom Filter

**NeetCode Problems (no direct match but relevant):**
- **705. Design HashSet** — conceptual cousin
- **706. Design HashMap** — conceptual cousin
- System Design: **Design a URL Shortener** (t9)
- System Design: **Design a Web Crawler** (Bloom for dedup)

**NeetCode Roadmap Section:** System Design / Design

**Core concept:** Probabilistic data structure not directly tested but crucial for system design rounds.

**Recommended study:**
1. Implement Bloom filter from scratch
2. Understand false positive rate formula
3. Design URL dedup system with Bloom filter
4. Compare with HashSet memory usage

### 03 Suffix Array

**NeetCode Problems (related):**
- **1044. Longest Duplicate Substring** — Suffix array + LCP
- **1923. Longest Common Subpath** — Suffix array + binary search
- **1062. Longest Repeating Substring** — Related to LCP
- **1698. Number of Distinct Substrings** — Suffix array + LCP

**NeetCode Roadmap Section:** Advanced String

**Recommended study:**
1. Build suffix array with n log n sort
2. Build LCP array with Kasai algorithm
3. Solve longest repeated substring
4. Solve distinct substrings count

### 04 Fenwick Tree (Binary Indexed Tree)

**NeetCode Problems:**
- **307. Range Sum Query - Mutable** — BIT or segment tree
- **315. Count of Smaller Numbers After Self** — BIT with coordinate compression
- **1649. Create Sorted Array through Instructions** — BIT
- **2179. Count Good Triplets in an Array** — BIT
- **2426. Number of Pairs Satisfying Inequality** — BIT

**NeetCode Roadmap Section:** Advanced Trees

**Recommended study:**
1. Implement BIT operations: build, update, prefix sum
2. Solve 307 (Range Sum Query)
3. Solve 315 (Count Smaller) — coordinate compression + BIT
4. Understand difference from segment tree

### 05 Segment Tree

**NeetCode Problems:**
- **307. Range Sum Query - Mutable** — Segment tree alternative
- **699. Falling Squares** — Segment tree with lazy + coordinate compression
- **729. My Calendar I** — Segment tree / TreeMap
- **731. My Calendar II** — Segment tree with overlap count
- **732. My Calendar III** — Segment tree with lazy propagation
- **850. Rectangle Area II** — 2D segment tree / line sweep

**NeetCode Roadmap Section:** Advanced Trees

**Recommended study:**
1. Implement segment tree: build, query, update (recursive)
2. Implement lazy propagation for range updates
3. Solve 699 (Falling Squares) — range max + lazy
4. Compare iterative vs recursive segment tree

### 06 Skip List

**NeetCode Problems:**
- **1206. Design Skiplist** — Direct implementation
- **352. Data Stream as Disjoint Intervals** — Related ordered structure
- **846. Hand of Straights** — Ordered map concept

**NeetCode Roadmap Section:** Design / Linked Lists

**Recommended study:**
1. Implement SkipList with insert, search, delete
2. Understand probabilistic balancing
3. Compare with TreeMap (Red-Black Tree)
4. Practice drawing skip list on whiteboard

### 07 Red-Black Tree

**NeetCode Problems (conceptual):**
- No direct Red-Black Tree implementation problem
- **220. Contains Duplicate III** — TreeSet with sliding window
- **352. Data Stream as Disjoint Intervals** — TreeSet/ordered DS
- **683. K Empty Slots** — TreeSet ordered operations
- **715. Range Module** — TreeMap intervals

**NeetCode Roadmap Section:** Trees / Balanced BST

**Recommended study:**
1. Understand RB-tree properties and invariants
2. Implement insert with fixup (hard — focus on understanding)
3. Practice TreeMap/TreeSet LeetCode problems
4. Know Java TreeMap internals for interview

### 08 Treap (Randomised BST)

**NeetCode Problems (no direct match):**
- No dedicated Treap problems on LeetCode
- Related problems for order statistics:
  - **230. Kth Smallest Element in BST**
  - **315. Count of Smaller Numbers After Self**
  - **1649. Create Sorted Array through Instructions**
- Range operations:
  - **699. Falling Squares**
  - **715. Range Module**

**NeetCode Roadmap Section:** Advanced Trees (supplementary)

**Recommended study:**
1. Implement treap with random priorities
2. Implement split/merge operations
3. Implement implicit treap for array operations
4. Solve order-statistic problems with treap

### 09 Union-Find (Disjoint Set Union)

**NeetCode Problems:**
- **200. Number of Islands** — Classic DSU or BFS/DFS
- **684. Redundant Connection** — Cycle detection with DSU
- **685. Redundant Connection II** — Directed graph variant
- **721. Accounts Merge** — DSU with string mapping
- **990. Satisfiability of Equality Equations** — DSU + constraints
- **1319. Number of Operations to Make Network Connected** — DSU
- **323. Number of Connected Components in an Undirected Graph** — DSU
- **128. Longest Consecutive Sequence** — DSU or HashSet
- **952. Largest Component Size by Common Factor** — DSU + prime factors
- **1101. The Earliest Moment When Everyone Become Friends** — DSU + timeline
- **1722. Minimize Hamming Distance After Swap Operations** — DSU
- **1971. Find if Path Exists in Graph** — DSU or DFS
- **2421. Number of Good Paths** — DSU + value ordering

**NeetCode Roadmap Section:** Graphs → Union-Find

**Blind 75 relevance:** Direct (200, 128)

**Recommended study:**
1. Implement DSU with path compression + union by rank
2. Solve 200 (Number of Islands)
3. Solve 684 (Redundant Connection)
4. Solve 721 (Accounts Merge) — string mapping DSU
5. Solve 952 (Largest Component) — prime factorisation + DSU

### 10 Merkle Tree (Hash Tree)

**NeetCode Problems (no direct match):**
- **951. Flip Equivalent Binary Trees** — Tree equality concept
- **100. Same Tree** — Tree equality
- **572. Subtree of Another Tree** — Tree hashing concept
- **1044. Longest Duplicate Substring** — Rolling hash idea

**NeetCode Roadmap Section:** System Design / Trees

**Recommended study:**
1. Implement Merkle tree: build, root hash, verify
2. Implement Merkle proof generation and verification
3. Understand role in blockchain (Bitcoin, Ethereum)
4. Understand role in git data model
5. Understand certificate transparency

## Blind 75 Mapping

The original Blind 75 does not directly cover most of these 10 structures. However, the following Blind 75 problems benefit from advanced DS knowledge:

| Blind 75 Problem | Advanced DS Insight |
|-----------------|-------------------|
| **128. Longest Consecutive Sequence** | Can use DSU instead of HashSet |
| **200. Number of Islands** | DSU alternative to BFS |
| **208. Implement Trie** | Direct trie problem |
| **212. Word Search II** | Trie optimisation |
| **295. Find Median from Data Stream** | BIT can replace two heaps |
| **297. Serialize/Deserialize BST** | Suffix array for string trees |
| Meeting Rooms II | Segment tree for range overlap |
| Alien Dictionary | Trie for string ordering |
| Design Tic-Tac-Toe | BIT for win detection (diagonals) |

## System Design Components for NeetCode

| NeetCode Design Problem | Advanced DS |
|------------------------|-------------|
| Design Autocomplete | Trie |
| Design Web Crawler | Bloom Filter |
| Design URL Shortener | Bloom Filter |
| Design Search Autocomplete | Trie |
| Design Distributed Key-Value | Merkle Tree (anti-entropy) |
| Design Dropbox / Google Drive | Merkle Tree (sync) |
| Design Google Docs | Merkle Tree (OT/CRDT integration) |
| Design Uber | Skip List (geospatial indexing) |
| Design Twitter Timeline | Union-Find (follower graph) |
| Design Typeahead Suggestion | Trie |

## Study Order (Recommended)

1. **Week 1-2**: Trie, Bloom Filter, Suffix Array (string DS)
2. **Week 3-4**: Fenwick Tree, Segment Tree (range queries)
3. **Week 5**: Skip List, Red-Black Tree, Treap (balanced trees)
4. **Week 6**: Union-Find, Merkle Tree (graph + crypto)
5. **Week 7**: Review + system design integration
6. **Week 8**: Mock interviews with all structures

## Progress Tracker

| Structure | Guide Done | Code Written | Problems Solved (NeetCode) | Interview Practice |
|-----------|------------|-------------|--------------------------|-------------------|
| 01 Trie | | | | |
| 02 Bloom Filter | | | | |
| 03 Suffix Array | | | | |
| 04 Fenwick Tree | | | | |
| 05 Segment Tree | | | | |
| 06 Skip List | | | | |
| 07 Red-Black Tree | | | | |
| 08 Treap | | | | |
| 09 Union-Find | | | | |
| 10 Merkle Tree | | | | |
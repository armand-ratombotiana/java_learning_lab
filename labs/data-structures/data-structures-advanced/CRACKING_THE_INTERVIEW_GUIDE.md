# Cracking the Interview: Advanced Data Structures

## Why Advanced Data Structures?

FAANG and top-tier companies test advanced data structures to evaluate:
- **System design readiness**: Many of these structures power core infrastructure
- **Problem-solving depth**: Beyond textbook DS, they test creative optimisation
- **Performance intuition**: Understanding space-time trade-offs at scale
- **Language mastery**: Java generics, concurrency, memory model

## The 10-Structure Study Plan

### Phase 1: Foundation (2 weeks)

#### Week 1: Trie, Bloom Filter, Suffix Array (String processing)
- Understand character-level vs string-level operations
- Focus: why trie beats HashSet for prefix queries
- Focus: why Bloom Filter uses less memory than HashSet
- Focus: suffix array LCP tricks

#### Week 2: Fenwick Tree, Segment Tree (Range queries)
- Understand the BIT index manipulation trick (i & -i)
- Understand segment tree recursion vs iterative versions
- Focus: range update + range query with lazy propagation
- Focus: when to use BIT vs Segment Tree

### Phase 2: Balancing + Randomisation (1 week)

#### Week 3: Skip List, Red-Black Tree, Treap
- Understand probabilistic vs deterministic balancing
- Focus: rotations in RB tree vs priority heap in treap
- Focus: skip list level generation and node promotion
- Focus: treap split/merge operations (implicit treap)

### Phase 3: Graph + Crypto (1 week)

#### Week 4: Union-Find, Merkle Tree
- Understand path compression + union by rank analysis
- Understand Merkle proof generation and verification
- Focus: offline queries, DSU with rollback (undo)
- Focus: Merkle tree in blockchain, git, database replication

## Daily Practice Routine (60 min)

| Time | Activity |
|------|----------|
| 0–10 min | Warm-up: implement one core operation from memory |
| 10–30 min | Solve one LeetCode medium/hard for the week's structure |
| 30–45 min | Analyse solution: complexity, edge cases, variations |
| 45–55 min | Mock explanation (out loud, as if to interviewer) |
| 55–60 min | Review INTERVIEW.md for that structure |

## Common Interview Patterns by DS

### Trie
- "Design a search autocomplete system"
- "Word search II in a grid"
- "Replace words with shortest root"
- "Design an IP routing table (longest prefix match)"
- "Palindrome pairs using trie"
- "Design a phone directory"

### Bloom Filter
- "Design a URL deduplication system"
- "Prevent cache stampede in distributed cache"
- "Spam detection with minimal memory"
- "Design a blockchain SPV node"
- "Weak password detection at signup"

### Suffix Array
- "Longest repeated substring in DNA"
- "Longest common substring of two strings"
- "Search for a pattern in a genome (read mapping)"
- "Shortest unique substring"
- "K-th lexicographically smallest substring"
- "String compression with LCP array"

### Fenwick Tree
- "Count inversions in array"
- "Range sum query with point updates"
- "Range update and point query"
- "Count of smaller numbers after self"
- "Online median tracker"
- "Reverse pairs (merge sort with BIT)"

### Segment Tree
- "Range minimum query with updates"
- "Range sum with lazy propagation"
- "Count of active segments in a range"
- "Skyline problem variant"
- "Rectangle area overlap (2D segment tree)"
- "Dynamic range GCD queries"

### Skip List
- "Design a lock-free concurrent set"
- "Implement a key-value store with range scan"
- "Design a in-memory database index"
- "Implement a leaderboard with O(log n) rank queries"
- "Merge two skip lists"
- "Design a time-series database storage engine"

### Red-Black Tree
- "Implement a TreeMap from scratch"
- "Design a consistent hash ring"
- "Java TreeMap internal walkthrough"
- "Range queries with dynamic insert/delete"
- "Design an interval tree for appointment scheduling"
- "Implement an LRU cache with RB-tree ordering"

### Treap
- "Ordered set with order statistics (k-th smallest)"
- "Range reverse with implicit treap"
- "Split and merge operations on balanced BST"
- "Design a rope data structure for text editor"
- "Dynamic array with insert/delete at arbitrary index"
- "Quad tree alternative for spatial partitioning"

### Union-Find
- "Number of islands II (dynamic grid)"
- "Accounts merge"
- "Verify a graph is a tree"
- "Redundant connection in graph"
- "Longest consecutive sequence"
- "Detect cycles in a directed graph (with path compression)"
- "Minimum spanning tree (Kruskal's)"

### Merkle Tree
- "Design a file integrity checker"
- "Implement a blockchain block header"
- "Design a peer-to-peer data sync (like git)"
- "Design a certificate transparency log"
- "Implement a verifiable log for audit"
- "Design a distributed database anti-entropy mechanism"

## Performance Tips for Interviews

1. **Always ask about constraints** before choosing a DS: how many elements, memory limit, latency target
2. **Draw the structure** on the whiteboard/notepad — it helps both you and the interviewer
3. **Start with brute force** then improve: shows you can iterate
4. **State complexity before coding**: "I'll use a segment tree, O(log n) per query, O(n) build"
5. **Edge cases**: empty input, single element, duplicates, large alphabet, worst-case skew
6. **Compare trade-offs explicitly**: "Bloom filter gives false positives but never false negatives, saving 90% memory"
7. **Java-specific**: mention `Arrays.binarySearch`, `PriorityQueue`, `TreeMap`, `BitSet` for comparison

## Recommended LeetCode Problem List by Difficulty

### Easy (warm-up)
- 208. Implement Trie (Prefix Tree)
- 705. Design HashSet (with bitset/Bloom)
- 303. Range Sum Query - Immutable
- 606. Construct String from Binary Tree
- 136. Single Number (XOR trick, related to Merkle)

### Medium (target practice)
- 211. Design Add and Search Words Data Structure
- 648. Replace Words (trie)
- 307. Range Sum Query - Mutable
- 684. Redundant Connection
- 721. Accounts Merge
- 990. Satisfiability of Equality Equations
- 1319. Number of Operations to Make Network Connected
- 1624. Largest Substring Between Two Equal Characters

### Hard (deep dive)
- 212. Word Search II
- 425. Word Squares
- 315. Count of Smaller Numbers After Self
- 327. Count of Range Sum
- 699. Falling Squares (segment tree with coordinate compression)
- 1206. Design Skiplist
- 1028. Recover a Tree From Preorder Traversal
- 1373. Maximum Sum BST in Binary Tree
- 1697. Checking Existence of Edge Length Limited Paths
- 1744. Can You Eat Your Favorite Candy on Your Favorite Day
- 1938. Maximum Genetic Difference Query
- 2192. All Ancestors of a Node in a Directed Acyclic Graph
- 2302. Count Subarrays With Score Less Than K

## Mock Interview Strategy

1. **Read problem aloud**: confirm understanding with interviewer
2. **Ask clarifying questions**: input size, data distribution, constraints, output format
3. **Propose 2-3 approaches**: brute force → optimisation → optimal
4. **Pick the best**: explain why based on constraints
5. **Write clean code**: use meaningful names, handle edge cases, add minimal comments
6. **Walk through example**: trace with the sample input
7. **State complexity again**: after coding, confirm time and space
8. **Handle follow-ups**: streaming data, concurrency, distributed environment

## Company-Specific Focus

| Company | Focus Structures | Frequency |
|---------|-----------------|-----------|
| Google | Trie, Segment Tree, Union-Find | High |
| Amazon | Bloom Filter, Red-Black Tree, Skip List | High |
| Meta | Trie, Suffix Array, Treap | Medium |
| Microsoft | Fenwick Tree, Union-Find, Segment Tree | Medium |
| Apple | Merkle Tree, Bloom Filter, Skip List | Low-Medium |

## Final Exam: 50 Questions Challenge

After completing all labs, solve at least 5 problems per structure (50 total). Track:
- Time per problem (target: <30 min for medium, <45 min for hard)
- Understanding of complexity (should be instinctive)
- Ability to explain trade-offs (should not need to look up)
- Code correctness (compiles first time, no off-by-one errors)
# Interview Questions: Persistent Segment Tree

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 307 Range Sum Query - Mutable](https://leetcode.com/problems/range-sum-query-mutable/) | Medium | Amazon, Google, Meta, Microsoft | Segment tree (baseline) |
| [LC 315 Count of Smaller Numbers After Self](https://leetcode.com/problems/count-of-smallers-numbers-after-self/) | Hard | Google, Amazon, Meta, Microsoft | Persistent segment tree / BIT |
| (No standard persistent segment tree LC problems) | — | Google, Amazon, Meta, Microsoft | Versioned range queries |
| [LC 327 Count of Range Sum](https://leetcode.com/problems/count-of-range-sum/) | Hard | Google, Amazon, Meta | Persistent segment tree |

## NeetCode Reference
Not in NeetCode 150. Persistent segment tree is an advanced topic in NeetCode 250 for competitive programming and hard interview rounds.

## Company-Specific Questions

### Google
- Explain how persistent segment trees enable querying any historical version of an array in O(log n)
- How does path copying create new versions while sharing unchanged subtrees?
- Design a persistent segment tree for K-th smallest element in a subarray (classic application)
- How would you implement a persistent segment tree for range sum queries across different time versions?

### Microsoft
- Implement a persistent segment tree that supports point updates and range sum queries across versions
- Compare persistent segment tree vs snapshot-based approach for versioned data
- How would you handle lazy propagation in a persistent segment tree? (Copy-on-write for lazy values)

### Meta
- Design a system for tracking metric changes over time using persistent segment trees
- How would you implement a rollback of a specific range of updates to a previous version?
- Compare persistent segment tree vs versioned database tables for temporal queries

### Amazon
- How would DynamoDB implement time-travel queries using persistent tree structures?
- Design a persistent segment tree for analyzing product price history (max/min/sum in any time range)
- Compare persistent segment tree vs immutable data structures for audit logging

### Apple
- How would you implement an undo/redo system for a drawing application using persistent segment trees?
- Design a persistent data structure for tracking versioned app configuration
- How would you query "what was the state of this canvas region at time t?"

### Oracle
- How does Oracle Database's Flashback Query relate to persistent data structures?
- What is the space overhead of persistent segment tree after N updates (O(N log M) where M = number of versions)?
- Compare persistent segment tree vs MVCC (Multi-Version Concurrency Control) for database versioning

## Real Production Scenarios

- **Scenario 1: Time-Travel Analytics** — A financial analytics platform uses a persistent segment tree to track stock prices over time. Each price update creates a new root. Queries can ask "what was the max price of AAPL between Jan 1 and Jan 15, as of Dec 31 vs as of Jan 20?" Both queries use different roots.

- **Scenario 2: K-th Smallest in Range** — A database of employee salaries supports queries like "find the k-th highest salary in the engineering department between 2020 and 2025." A persistent segment tree over the value domain (salaries) is built from sorted arrays of salaries per year. Querying versions v[2025] and v[2020-1] gives the ability to answer range statistics.

- **Scenario 3: Versioned Code Repository** — A version control system uses a persistent segment tree over file line counts. Each commit creates a new version. Queries like "how many lines of code were in the project 100 commits ago?" or "what file had the most lines at commit abc123?" are O(log n) range queries on the version tree.

## Interview Tips

- Time: O(log n) per update and per query, O(n log n) for building from scratch with n point updates
- Space: O(n + m log n) — initial tree takes O(n), each version adds O(log n) new nodes (path copying)
- Common edge cases: querying version that doesn't exist, empty queries, reverse order of versions
- Persistent segment tree creates a new root for each version; roots are stored in an array/list indexed by version number
- Each update copies the path from root to the modified leaf (log n new nodes); unchanged children are shared
- Querying: traverse the tree from the desired version's root, same as non-persistent segment tree
- To query K-th smallest in range [l, r]: query version r and version l-1, subtract frequencies
- Lazy propagation is difficult with persistence — copy-on-write for lazy values complicates sharing

## Java-Specific Considerations

- No standard persistent segment tree class in Java — implement from scratch
- Node structure: `class PSTNode { int val; PSTNode left, right; }` — nodes are never modified after creation
- `PSTNode build(int l, int r, int[] arr)` — build from initial array or start with all zeros
- `PSTNode update(PSTNode prev, int l, int r, int pos, int delta)` — creates new nodes along the path; returns new root
- `int query(PSTNode node, int l, int r, int ql, int qr)` — standard segment tree query on a given version
- Roots array: `List<PSTNode> roots = new ArrayList<>(); roots.add(initialRoot);` — each update adds a new root
- For K-th smallest: build over value domain (frequency segment tree). Query: `roots.get(r).freq - roots.get(l-1).freq > k`
- Coordinate compression: map values to [0, n-1] for the domain (since domain = number of distinct values)
- Array-based persistent segment tree: use `int[] leftChild, rightChild, val` instead of node objects (reduces GC pressure)
- Flat array approach: `int nextIndex = 1; int[] left = new int[MAX_NODES]; int[] right = new int[MAX_NODES]; int[] val = new int[MAX_NODES];`
- Allocate nodes from a pre-allocated pool: `int newNode() { return nextIndex++; }` — faster than object allocation
- Memory estimate: for n = 10^5 and 10^5 versions, each update creates ~17 new nodes (log₂ 10^5) → ~1.7M nodes ≈ 40MB (with int arrays)
- `ThreadLocal<Deque<Integer>>` for node reuse if doing heavy GC-sensitive work
- For N versions: roots stored in `int[] roots = new int[versions];` (array index, not object reference)

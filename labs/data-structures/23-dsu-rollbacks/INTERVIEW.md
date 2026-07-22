# Interview Questions: DSU with Rollbacks

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 684 Redundant Connection](https://leetcode.com/problems/redundant-connection/) | Medium | Amazon, Meta, Google, Microsoft | Union-Find cycle detection |
| [LC 200 Number of Islands](https://leetcode.com/problems/number-of-islands/) | Medium | Amazon, Meta, Google, Microsoft | Union-Find on grid |
| [LC 721 Accounts Merge](https://leetcode.com/problems/accounts-merge/) | Medium | Amazon, Meta, Google, Microsoft | Union-Find email grouping |
| [LC 990 Satisfiability of Equality Equations](https://leetcode.com/problems/satisfiability-of-equality-equations/) | Medium | Amazon, Meta, Google | Union-Find constraint checking |
| [LC 839 Similar String Groups](https://leetcode.com/problems/similar-string-groups/) | Hard | Google, Amazon, Meta | Union-Find with similarity check |
| [LC 305 Number of Islands II](https://leetcode.com/problems/number-of-islands-ii/) | Hard | Amazon, Google, Meta | Online Union-Find (dynamic grid) |
| (Offline dynamic connectivity — no standard LC) | — | Google, Amazon, Meta | DSU rollback + divide & conquer |

## NeetCode Reference
Not in NeetCode 150. DSU with rollbacks is an advanced technique for offline dynamic connectivity problems.

## Company-Specific Questions

### Google
- How would you implement Union-Find with undo operations? Store history of changes and roll back
- Design a DSU that supports point-in-time queries: "were nodes u and v connected at time t?"
- Implement offline dynamic connectivity using DSU with rollbacks and divide-and-conquer on time intervals
- How does rollback DSU differ from persistent DSU? (Rollback: single timeline, ordered undo; Persistent: multiple version branches)

### Microsoft
- Implement a DSU that stores parent and size changes in a stack for O(1) rollback
- Design a system to track connectivity during a planned network outage (edges added and removed over time)
- Compare DSU with rollbacks vs Euler tour tree for dynamic connectivity

### Meta
- How would you solve a problem where edges are added and removed in a sequence (dynamic graph)?
- Implement a DSU that supports: union(u,v), query(u,v), and snapshot/rollback
- Design a version control system for graph connectivity changes

### Amazon
- Design a system for warehouse network reliability — track connectivity as network switches go up and down
- How would you implement a DSU for offline query processing where edge deletions are batched?
- Compare rollback DSU vs link-cut trees for dynamic connectivity scenarios

### Apple
- How would you implement undo for device pairing operations (bluetooth mesh connectivity)?
- Design a DSU for parallel computation where each thread operates on a fork that can be merged/rolled back
- How would you track the evolution of connectivity as new devices join/leave a network?

### Oracle
- How does a DSU with rollbacks enable ACID properties in a graph database?
- Implement a persistent (versioned) DSU using path copying — what is the space overhead?
- Compare the memory usage of rollback (stack of changes) vs persistent (path-copied) DSU

## Real Production Scenarios

- **Scenario 1: Dynamic Graph Connectivity** — A network monitoring tool tracks connectivity of a computer network as links go up and down. Edges can be added (new fiber connection) and deleted (outage). A DSU with rollbacks enables answering "are nodes A and B connected?" at any point in time by rolling back/forward to the relevant state.

- **Scenario 2: Database Schema Migration** — A migration tool applies a sequence of schema changes (add/drop tables, alter columns) that affect a dependency graph. Each migration step is a DSU union operation. If a migration fails, all changes are rolled back by reverting the DSU to its previous state.

- **Scenario 3: Timeline Analysis** — A code review tool tracks merge conflicts across a repository's git history. For each commit, it maintains a DSU of merged branches. The DSU with rollbacks allows the tool to walk through history, querying which branches were connected at each commit.

## Interview Tips

- Time: O(log n) per union/find (without rollback — standard DSU); O(log n) per union with rollback (must avoid path compression, use union by size only)
- Space: O(n + number_of_operations) — history stack stores (parent, child, prevParent, prevSize) per union
- Common edge cases: rollback on empty history, rolling back to a state that never existed, concurrent modifications
- Path compression is incompatible with rollbacks because it changes parent pointers non-linearly — use union by size only (O(log n) find)
- Rollback stack: each union pushes a record (a, parentA, b, parentB, sizeA, sizeB) — to undo, restore these values
- Find must be iterative (no path compression) to allow rollback: `while (parent[x] != x) x = parent[x];`
- For offline dynamic connectivity: use divide and conquer over time intervals, add edges that are alive in the interval, rollback when leaving

## Java-Specific Considerations

- No standard rollback DSU class in Java — implement from scratch
- History stack: `Deque<int[]> history = new ArrayDeque<>()` or `List<int[]>` with offset pointer
- DSU structure: `int[] parent, size; Deque<int[]> stack; int snapshots;`
- Union: `void union(int a, int b) { ... stack.push(new int[]{a, parent[a], b, parent[b], size[a], size[b]}); ... }`
- Rollback: `void rollback() { int[] record = stack.pop(); parent[record[0]] = record[1]; parent[record[2]] = record[3]; size[record[0]] = record[4]; size[record[2]] = record[5]; }`
- Snapshot: `int save() { return stack.size(); }` — returns current history offset
- Rollback to snapshot: `void revertTo(int snapshot) { while (stack.size() > snapshot) rollback(); }`
- `long[]` for parent/size if size values exceed int range (unlikely)
- `ThreadLocal<Deque<int[]>>` for thread-local rollback history in parallel processing
- For divide-and-conquer: `void solve(int l, int r, List<Edge> edges, DSU dsu)` — add edges covering [l,r], call recursively, rollback
- DSU without path compression: find is O(log n) with union by size → acceptable for offline algorithm (total O(n log n))

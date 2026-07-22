# Interview Questions: Link-Cut Tree

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 685 Redundant Connection II](https://leetcode.com/problems/redundant-connection-ii/) | Hard | Google, Amazon, Meta | Directed graph cycle detection |
| (No standard LC problems for dynamic trees) | — | Google, Meta, Microsoft, Amazon | Dynamic connectivity / path queries |

## NeetCode Reference
Not in NeetCode. Link-cut trees are among the most advanced topics, relevant for competitive programming and Google-level research interviews.

## Company-Specific Questions

### Google
- Explain the Link-Cut Tree data structure — how does it maintain dynamic forest connectivity?
- How does the splay tree-based representation work? Why are preferred paths used?
- Implement expose(v) — the operation that makes v the root of its auxiliary tree and exposes the path from root to v
- Design a system for dynamic graph connectivity (edge insert/delete, connectivity queries) using link-cut trees

### Microsoft
- Compare Link-Cut Tree vs Euler Tour Tree vs DSU with Rollbacks for dynamic connectivity
- How would you implement link(v, w) and cut(v, w) operations on a link-cut tree?
- What is the time complexity of link-cut tree operations and why is it amortized O(log n)?

### Meta
- How would you use a link-cut tree to find the minimum weight edge on a path in a dynamic tree?
- Design a system that tracks network connectivity as fiber links are added and removed
- Compare link-cut tree vs heavy-light decomposition for static tree path queries

### Amazon
- Design a dynamic graph system for warehouse robot path planning (edges appear/disappear as aisles open/close)
- How would you use link-cut trees for dynamic minimum spanning tree (insert/delete edges)?
- Compare link-cut tree vs Hierholzer's algorithm for dynamic Euler tours

### Apple
- How would you implement a link-cut tree for dynamic connectivity in a mesh network?
- Design a system for dynamic routing in an ad-hoc wireless network (nodes join/leave)
- What are the practical implementation challenges of link-cut trees in production systems?

### Oracle
- How do link-cut trees relate to splay trees and what is the "splay" operation?
- What is aggregate path query and how does a link-cut tree support it?
- Compare the complexity (and practicality) of link-cut trees vs the Euler tour tree for dynamic trees
- Why are link-cut trees considered too complex for most production database systems?

## Real Production Scenarios

- **Scenario 1: Dynamic Network Connectivity** — A software-defined network controller tracks connectivity in a data center network as switches fail and recover. Link-cut trees answer "is there a path between switch A and B?" in O(log n) time after each link-state change. Edge insertions (link up) and deletions (link down) are handled in O(log n).

- **Scenario 2: Dynamic Minimum Spanning Tree** — A telecommunications network provider maintains the minimum-cost spanning tree of a dynamically changing network. When a cheaper link becomes available (insert), the link-cut tree finds the maximum-weight edge on the path between its endpoints and potentially swaps. When a link fails (delete), the tree reconnects the forest with the cheapest available replacement.

- **Scenario 3: Particle Simulation** — A molecular dynamics simulation tracks interactions between particles. As particles move, some connect (new bonds) and some disconnect (broken bonds). The link-cut tree efficiently maintains connectivity information, answering queries like "are atoms A and B in the same molecule?" after each simulation step.

## Interview Tips

- Time: O(log n) amortized per operation (link, cut, connected, path aggregate)
- Space: O(n) — each node stores splay tree pointers (left, right, parent), plus path-parent, and subtree aggregate
- Common edge cases: linking already-connected nodes (cycle detection), cutting a non-existent edge, operating on an empty tree
- Core operations: expose(v), link(v, w), cut(v, w), connected(v, w), path aggregate query
- Splay tree: each access (expose) splays the node to the root of its auxiliary tree
- Preferred paths: the tree is decomposed into node-disjoint preferred paths; each is stored as a splay tree keyed by depth
- The expose(v) operation makes v the root of its auxiliary tree and converts the path from root to v into a single preferred path
- After expose(v), the left subtree of v in its splay tree contains the nodes on the path from root to v (excluding v)
- Path aggregate: store subtree aggregates in splay tree nodes; after expose, v's subtree aggregate = path aggregate from root to v
- Link-cut trees are extremely complex to implement — expect this only from strong candidates in research-level interviews

## Java-Specific Considerations

- No standard link-cut tree class in Java — implement from scratch
- Node structure:
  ```java
  class LCTNode {
      int val, sum, lazy;
      LCTNode left, right, parent;
      LCTNode pathParent;
      boolean rev; // lazy reversal flag
  }
  ```
- Splay tree operations: `rotate(LCTNode x)`, `splay(LCTNode x)` — standard BST rotations
- `void push(LCTNode x)` — propagate lazy reversal and aggregate updates to children
- `void update(LCTNode x)` — recompute aggregate from children and self
- `void access(LCTNode v)` — the core expose operation. Sets v's path-parent to null, repeatedly splays and links
- `void makeroot(LCTNode v)` — access(v); splay(v); apply reverse flag (makes v the root of the represented tree)
- `void link(LCTNode v, LCTNode w)` — makeroot(v); if (findroot(w) != v) v.pathParent = w;
- `void cut(LCTNode v, LCTNode w)` — makeroot(v); access(w); splay(w); w.left.parent = null; w.left = null;
- `boolean connected(LCTNode v, LCTNode w)` — return findroot(v) == findroot(w);
- `int queryPath(LCTNode v, LCTNode w)` — makeroot(v); access(w); splay(w); return w.sum;
- `void updatePath(LCTNode v, LCTNode w, int delta)` — makeroot(v); access(w); splay(w); apply lazy; w.sum += delta;
- `LCTNode[] nodes = new LCTNode[n];` — pre-allocate all nodes for n elements
- `IntFunction<LCTNode>` supplier for creating nodes — but pre-allocation is preferred for performance
- Reversal lazy flag is needed for `makeroot` — enables reverse of preferred path direction
- The implementation is notoriously difficult — even experienced competitive programmers use off-the-shelf implementations
- Due to object overhead in Java, LCT may be slower than in C++. In practice, use `int[]` arrays for left/right/parent/pathParent indices
- Array-based LCT: store children as int indices (left[i], right[i], parent[i], pathParent[i]) — no node objects
- `splay()` is the most complex operation — practice it separately before coding the full LCT
- Consider using `ThreadLocal<Deque<LCTNode>>` for recursion-free splay path tracking

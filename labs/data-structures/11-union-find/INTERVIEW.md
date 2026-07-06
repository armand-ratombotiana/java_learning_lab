# Interview Questions: Union-Find

## Basic Questions

1. **Explain the Union-Find data structure and its operations.**
   DSU maintains disjoint sets. Find returns the set representative. Union merges two sets. With path compression and union by rank, both operations run in nearly O(1) amortized time.

2. **How does path compression work?**
   During Find, each node on the path from the queried node to the root has its parent updated to point directly to the root. This flattens the tree and speeds up future operations.

3. **What is union by rank?**
   When merging, attach the tree with smaller height (rank) under the tree with greater height. This prevents tree height from growing too quickly.

## Problem-Solving Questions

4. **Number of Connected Components in an Undirected Graph** (LeetCode 323)
   Given n nodes and edges, find connected components. Solution: Initialize DSU(n), union each edge, count distinct roots.

5. **Redundant Connection** (LeetCode 684)
   Find an edge that, when removed, makes the graph a tree. Solution: Add edges to DSU; when find(u) == find(v), that edge creates a cycle.

6. **Accounts Merge** (LeetCode 721)
   Merge accounts sharing emails. Solution: Map emails to IDs, use DSU to union accounts, group results.

7. **Longest Consecutive Sequence** (LeetCode 128)
   Find longest consecutive element sequence in O(n). Solution: Use HashMap to map elements to indices, union adjacent elements, track component size.

## Advanced Questions

8. **Evaluate Division** (LeetCode 399)
   Answer queries about division results based on equations. Solution: Weighted DSU where each element stores the ratio relative to its parent.

9. **Number of Islands II** (LeetCode 305)
   After each land addition, return island count. Solution: DSU with 2D to 1D mapping, check four adjacent cells.

10. **Minimize Malware Spread** (LeetCode 924)
    Find which node to remove to minimize infection. Solution: DSU with component size tracking.

## Tips for Interview

1. Always mention both path compression and union by rank
2. Use the terms "near-constant" or "amortized O(alpha(n))"
3. Clarify that DSU is for undirected graphs only
4. For 2D grid problems, convert (row, col) to unique ID = row * cols + col
5. Remember that DSU cannot split sets easily (monotonic merging only)

## Follow-Up Questions

Interviewers often ask:
- "Can you optimize this further?" (Both optimizations are optimal)
- "How would you handle dynamic additions?" (Use ArrayList for parent/rank)
- "What about thread safety?" (Use locks or atomic CAS)
- "What if we need to delete edges?" (Use persistent DSU or offline processing)

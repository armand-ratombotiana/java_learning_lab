# Solutions: Union-Find Exercises

Exercise solutions are provided in the `src/` directory tree.

## Exercise Solutions

1. **Basic DSU**: `DisjointSetUnion.java` — full implementation with both optimizations
2. **Connected Components**: `ConnectedComponents.countComponents()` — uses DSU to count distinct roots
3. **Cycle Detection**: `ConnectedComponents.hasCycle()` — if union returns false, cycle exists
4. **Kruskal's MST**: `KruskalMST.java` — sorts edges, processes with DSU
5. **Islands**: `ConnectedComponents.islandCount()` — 2D to 1D mapping in DSU

All solutions use O(alpha(n)) amortized time per operation.

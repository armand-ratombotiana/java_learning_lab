# Common Mistakes

- BFS/DFS: Forgetting to mark visited before enqueuing → infinite loop
- Dijkstra: Using with negative edges → wrong results
- Dijkstra: Not skipping stale entries in PQ → O(V log V) becomes O(V²)
- DFS: Stack overflow on deep/large graphs
- Topological sort: Missing cycle detection
- Kruskal: Not using Union-Find → O(VE) instead of O(E log E)

# Performance: Graphs

## Time Complexity

| Algorithm | Adjacency Matrix | Adjacency List |
|-----------|-----------------|---------------|
| BFS | O(V²) | O(V + E) |
| DFS | O(V²) | O(V + E) |
| Dijkstra (array) | O(V²) | O(V²) |
| Dijkstra (heap) | O(V²) | O((V+E) log V) |
| Bellman-Ford | O(V³) | O(VE) |
| Floyd-Warshall | O(V³) | O(V³) |
| Prim's MST | O(V²) | O((V+E) log V) |
| Kruskal's MST | O(E log E) | O(E log E) |
| Topological sort | O(V²) | O(V + E) |
| Tarjan's SCC | O(V²) | O(V + E) |

## Space Complexity

| Representation | Memory | Best For |
|---------------|--------|---------|
| Adjacency matrix | O(V²) | Dense graphs (E ≈ V²) |
| Adjacency list | O(V + E) | Sparse graphs (E << V²) |
| Edge list | O(E) | Algorithms needing edge iteration |
| Incidence matrix | O(V × E) | Hypergraphs |

## Memory Comparison (V = 10,000)

| Representation | Sparse (E = 20,000) | Dense (E = 50M) |
|---------------|--------------------|-----------------|
| Adjacency matrix | 400 MB | 400 MB |
| Adjacency list | ~0.5 MB | ~400 MB |
| Edge list | ~0.2 MB | ~400 MB |

## Graph Traversal Performance

### BFS
- **Adjacency list**: O(V + E) — visit each vertex and each edge once
- **Adjacency matrix**: O(V²) — check every possible edge

### DFS Recursive
- **Space**: O(V) for call stack (might overflow for large graphs)
- **Iterative DFS** uses explicit stack (O(V) on heap)

## Dijkstra PriorityQueue Optimization

| Implementation | Extract-min | Decrease-key | Total |
|---------------|------------|-------------|-------|
| Array | O(V) | O(1) | O(V²) |
| Binary heap | O(log V) | O(log V) | O((V+E) log V) |
| Fibonacci heap | O(log V) | O(1)* | O(V log V + E) |

\*Amortized

## Java-Specific Notes

- Adjacency list with `ArrayList` per vertex: good cache behavior for sequential neighbor access
- `PriorityQueue` for Dijkstra: O((V+E) log V), but `remove` is O(V) if decreasing key
- Better approach: allow stale entries in PQ (ignore outdated distances)
- For dense graphs, array-based Dijkstra (no PQ) is faster in practice

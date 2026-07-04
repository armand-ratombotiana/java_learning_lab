# Graph Theory Performance

## Algorithm Complexity

| Algorithm | Time | Space |
|-----------|------|-------|
| BFS/DFS | $O(V + E)$ | $O(V)$ |
| Dijkstra (binary heap) | $O((V+E)\log V)$ | $O(V)$ |
| Bellman-Ford | $O(VE)$ | $O(V)$ |
| Floyd-Warshall | $O(V^3)$ | $O(V^2)$ |
| Kruskal's MST | $O(E \log E)$ | $O(V)$ |
| Prim's MST | $O(E \log V)$ | $O(V)$ |
| Ford-Fulkerson | $O(E \cdot f)$ | $O(V)$ |

## Choose Right Representation

| Graph Density | Best Representation |
|--------------|-------------------|
| Dense ($E \approx V^2$) | Adjacency Matrix |
| Sparse ($E \ll V^2$) | Adjacency List |
| Very Large ($V > 10^6$) | Compressed Sparse Row (CSR) |

## Parallel Graph Algorithms

```java
// Parallel BFS using ForkJoinPool
IntStream.range(0, n).parallel()
    .filter(i -> level[i] == currentLevel)
    .forEach(v -> {
        for (int w : graph.get(v))
            if (level[w] == -1) level[w] = currentLevel + 1;
    });
```

## Prefetch & Cache Optimizations

Use array-based adjacency (CSR format) instead of HashMap/ArrayList for $2-5\times$ speedup.

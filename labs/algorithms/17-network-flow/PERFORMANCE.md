# Performance — Network Flow

## Algorithm Comparison

| Algorithm | Time Complexity | Practical Performance |
|-----------|----------------|---------------------|
| Ford-Fulkerson | O(C * E) | Slow for large capacities |
| Edmonds-Karp | O(V * E^2) | Consistent but slow |
| Dinic | O(V^2 * E) | Fast, widely used |
| Dinic (unit) | O(min(V^{2/3}, sqrt(E)) * E) | Very fast for bipartite matching |
| Push-Relabel (HL) | O(V^2 * sqrt(E)) | Fastest in practice |

## Benchmark Data

For a random graph with V=1000, E=10000:
- Ford-Fulkerson: 50-500ms (depends on capacities)
- Edmonds-Karp: 200-300ms
- Dinic: 10-50ms
- Push-Relabel: 5-20ms

## Optimization Tips

- Use adjacency with Edge objects for clarity; switch to arrays for speed
- Preallocate edge lists with expected capacity
- Use iterative DFS with explicit stack to avoid recursion depth issues

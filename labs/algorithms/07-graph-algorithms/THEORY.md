# Graph Algorithms — Theoretical Foundation

## Graph Traversal

### BFS (Breadth-First Search)
- Uses queue, explores level by level
- Finds shortest path in unweighted graphs
- Time: O(V + E), Space: O(V)

### DFS (Depth-First Search)
- Uses stack (recursive or explicit)
- Explores depth first, backtracks
- Time: O(V + E), Space: O(V) for stack

## Shortest Paths

### Dijkstra
- Greedy, non-negative weights only
- Time: O((V+E) log V) with PQ
- Space: O(V)

### Bellman-Ford
- Handles negative edges, detects negative cycles
- Time: O(VE)

### Floyd-Warshall
- All-pairs shortest paths
- Time: O(V³), Space: O(V²)

## Minimum Spanning Tree

### Prim's
- Greedy, grows tree from a start node
- Time: O(E log V) with PQ

### Kruskal's
- Greedy, adds smallest edges without cycles
- Time: O(E log E) with Union-Find

# Interview Questions: Graphs

## Easy

1. **Clone graph** — Deep copy of an undirected graph (BFS/DFS with HashMap).

2. **BFS/DFS on adjacency list** — Implement both traversals.

3. **Find if path exists** — BFS or DFS from start to target.

4. **Degree of a vertex** — Count neighbors in an undirected graph.

## Medium

5. **Number of islands** — Count connected components in a 2D grid (DFS).

6. **Course schedule** — Can all courses be completed given prerequisites? (Cycle detection in DAG).

7. **Course schedule II** — Return a valid order to take courses (topological sort).

8. **Clone graph** — Deep copy with random pointers.

9. **Word ladder** — Shortest transformation sequence length (BFS on implicit graph).

10. **Pacific Atlantic water flow** — BFS/DFS from both oceans.

## Hard

11. **Alien dictionary** — Given sorted words from an alien language, deduce character order (topological sort).

12. **Minimum height trees** — Find roots that minimize tree height (topological BFS, remove leaves).

13. **Critical connections** — Find edges whose removal disconnects the graph (Tarjan's algorithm).

14. **Cheapest flights within k stops** — Limited BFS or Bellman-Ford variant.

15. **Find all paths** — Return all paths from source to target (DFS backtracking).

## Key Patterns

- **BFS**: shortest path in unweighted graphs, level-order processing
- **DFS**: cycle detection, topological sort, connectivity, exhaustive search
- **Topological sort**: dependency resolution, DAG processing
- **Union-Find**: connected components, MST (Kruskal's)
- **Graph coloring**: bipartite detection, scheduling conflicts
- **Adjacency list vs matrix**: trade-offs based on density

## Java-Specific Topics

- No standard `Graph` class in Java — implement from scratch
- `PriorityQueue` for Dijkstra
- `ArrayList` vs `LinkedList` for adjacency list (ArrayList preferred)
- `HashMap` for implicit graph (infinite/unknown vertex set)

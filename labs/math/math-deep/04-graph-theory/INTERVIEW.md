# Interview: Graph Theory

## Q1: Conceptual Understanding
**Q**: Explain the difference between Eulerian and Hamiltonian paths.
**A**: Eulerian paths visit each edge exactly once; Hamiltonian paths visit each vertex exactly once. Eulerian is solvable in polynomial time (degree parity check); Hamiltonian is NP-complete.

## Q2: Implementation
**Q**: How would you check if a graph is bipartite?
**A**: Run BFS/DFS with two-coloring. If any edge connects same-colored vertices, it's not bipartite. Bipartite graphs are exactly 2-colorable.

## Q3: System Design
**Q**: Design a route-planning system using graph theory.
**A**: Weighted graph (roads = edges, intersections = vertices). Dijkstra/A* for shortest path. Use contraction hierarchies for precomputation and real-time querying.

## Coding Challenge
Implement Hierholzer's algorithm to find an Eulerian circuit in a directed graph.

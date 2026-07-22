# Interview Questions: Graph Algorithms

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 200 Number of Islands | Medium | Google, Meta, Amazon, Microsoft | DFS / BFS |
| LC 133 Clone Graph | Medium | Google, Meta, Amazon | BFS/DFS + HashMap |
| LC 207 Course Schedule | Medium | Google, Meta, Amazon | Topological sort |
| LC 210 Course Schedule II | Medium | Google, Meta, Amazon | Topological sort |
| LC 269 Alien Dictionary | Hard | Google, Meta, Amazon | Topological sort |
| LC 743 Network Delay Time | Medium | Google, Microsoft | Dijkstra |
| LC 787 Cheapest Flights Within K Stops | Medium | Google, Meta | Bellman-Ford / Dijkstra |
| LC 1584 Min Cost to Connect Points | Medium | Google, Amazon | Prim's / Kruskal's |
| LC 332 Reconstruct Itinerary | Hard | Google, Meta | Eulerian path |

## NeetCode Reference
- LC 200 Number of Islands (NeetCode 150)
- LC 133 Clone Graph (NeetCode 150)
- LC 207 Course Schedule (NeetCode 150)
- LC 210 Course Schedule II (NeetCode 150)
- LC 269 Alien Dictionary (NeetCode 150)
- LC 743 Network Delay Time (NeetCode 150)
- LC 787 Cheapest Flights (NeetCode 150)
- LC 1584 Min Cost to Connect Points (NeetCode 150)
- LC 332 Reconstruct Itinerary (NeetCode 150)

## Company-Specific Questions
### Google
- Graph algorithms are the most heavily tested topic at Google
- Alien Dictionary, Course Schedule, and Cheapest Flights are signature Google questions
- Expect complex graph constructions: convert real-world problem into graph, apply BFS/DFS/Dijkstra

### Microsoft
- Clone Graph and Number of Islands are Microsoft favorites
- How does Windows file system handle directory graph traversal?
- Design a graph-based dependency resolver for package management

### Meta
- Social graph traversal (friends of friends, degrees of separation)
- Number of Islands with large grid constraints (memory optimization)
- Graph problems with adjacency list representation (not matrix)

### Amazon
- Network topology routing for AWS regions
- Cheapest flights with constraints (k stops) for delivery routing
- MST for laying fiber optic cables across datacenters

### Apple
- Graph traversal for peer-to-peer AirDrop routing
- Memory-efficient graph representation for mobile devices
- Clone Graph with minimal object creation

### Oracle
- Database dependency graphs for schema migration ordering
- How does Oracle RAC handle cluster communication graphs?
- Design a graph-based query optimization planner

## Real Production Scenarios
- Scenario 1: Social network friend recommendations - using BFS on a social graph to find second-degree connections with mutual friends count (Meta)
- Scenario 2: Content delivery routing - using Dijkstra's algorithm to find the optimal CDN edge server path minimizing latency for a streaming video request
- Scenario 3: Microservice dependency resolution - debugging a circular dependency in service startup ordering using topological sort detection

## Interview Tips
- Choose the right representation: adjacency matrix (dense) vs adjacency list (sparse) vs edge list
- BFS for shortest path in unweighted graphs; Dijkstra for weighted; Bellman-Ford for negative weights
- Topological sort only works on DAGs; detect cycles with Kahn's algorithm or DFS coloring
- Common edge cases: disconnected graphs, self-loops, duplicate edges, large graphs

## Java-Specific Considerations
- `HashMap<Integer, List<Integer>>` for adjacency list; `List<List<Integer>>` for fixed vertex sets
- `Deque<Integer>` (ArrayDeque) for BFS queue; `Deque<Integer>` (LinkedList) as stack for DFS
- `PriorityQueue<int[]>` with `Comparator.comparingInt(a -> a[1])` for Dijkstra
- `UnionFind` class for Kruskal's MST and connectivity problems
- Pitfall: forgetting to mark visited nodes causing infinite loops in graph traversal
- Pitfall: not handling disconnected components separately

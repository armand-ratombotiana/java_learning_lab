# Interview Questions: Graphs

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 200 Number of Islands](https://leetcode.com/problems/number-of-islands/) | Medium | Amazon, Meta, Google, Microsoft, Apple | DFS / BFS / Union-Find |
| [LC 133 Clone Graph](https://leetcode.com/problems/clone-graph/) | Medium | Amazon, Google, Meta, Microsoft | BFS/DFS + HashMap |
| [LC 207 Course Schedule](https://leetcode.com/problems/course-schedule/) | Medium | Amazon, Meta, Google, Microsoft, Apple | Topological sort / cycle detection |
| [LC 210 Course Schedule II](https://leetcode.com/problems/course-schedule-ii/) | Medium | Amazon, Google, Meta, Microsoft | Topological order |
| [LC 261 Graph Valid Tree](https://leetcode.com/problems/graph-valid-tree/) | Medium | Amazon, Google, Meta, Microsoft | Union-Find / DFS |
| [LC 323 Number of Connected Components in an Undirected Graph](https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/) | Medium | Amazon, Meta, Google, Microsoft | Union-Find / DFS |
| [LC 127 Word Ladder](https://leetcode.com/problems/word-ladder/) | Hard | Amazon, Meta, Google, Microsoft | BFS on implicit graph |
| [LC 269 Alien Dictionary](https://leetcode.com/problems/alien-dictionary/) | Hard | Amazon, Meta, Google, Microsoft | Topological sort |
| [LC 417 Pacific Atlantic Water Flow](https://leetcode.com/problems/pacific-atlantic-water-flow/) | Medium | Amazon, Meta, Google, Microsoft | DFS/BFS from borders |
| [LC 994 Rotting Oranges](https://leetcode.com/problems/rotting-oranges/) | Medium | Amazon, Meta, Microsoft, Google | BFS multi-source |
| [LC 79 Word Search](https://leetcode.com/problems/word-search/) | Medium | Amazon, Meta, Microsoft, Google | DFS backtracking |
| [LC 130 Surrounded Regions](https://leetcode.com/problems/surrounded-regions/) | Medium | Amazon, Google, Meta, Microsoft | Border DFS / Union-Find |
| [LC 785 Is Graph Bipartite](https://leetcode.com/problems/is-graph-bipartite/) | Medium | Amazon, Google, Meta, Microsoft | BFS coloring |
| [LC 695 Max Area of Island](https://leetcode.com/problems/max-area-of-island/) | Medium | Amazon, Meta, Google, Microsoft | DFS area computation |
| [LC 286 Walls and Gates](https://leetcode.com/problems/walls-and-gates/) | Medium | Amazon, Google, Meta, Microsoft | BFS multi-source |
| [LC 787 Cheapest Flights Within K Stops](https://leetcode.com/problems/cheapest-flights-within-k-stops/) | Medium | Amazon, Google, Meta, Microsoft | Bellman-Ford / Dijkstra / BFS |
| [LC 1192 Critical Connections in a Network](https://leetcode.com/problems/critical-connections-in-a-network/) | Hard | Amazon, Google, Meta, Microsoft | Tarjan's algorithm |

## NeetCode Reference
NeetCode 150: Graphs category — 11 problems covering DFS, BFS, topological sort, Union-Find, and advanced graph algorithms.

## Company-Specific Questions

### Google
- Find the shortest path in a grid with obstacles and weighted cells (Dijkstra/A*)
- Design a system to detect cycles in a dependency graph (package manager)
- Alien dictionary — reconstruct order from sorted words (topological sort)
- Design a web crawler graph (BFS with politeness policy, deduplication)

### Microsoft
- Clone a graph using BFS and DFS — compare approaches
- Find all paths from source to target in a DAG (DFS backtracking)
- Detect if a graph is bipartite (graph coloring — BFS and Union-Find approaches)

### Meta
- Number of islands — multiple approaches (DFS, BFS, Union-Find) and follow-up (what if the grid is too large for recursion?)
- Social graph — find shortest path between two people (BFS on Facebook graph)
- Word ladder — bidirectional BFS reduces search space significantly

### Amazon
- Design a product recommendation graph (bipartite: users → products purchased)
- Course schedule prerequisites detection — cycle detection using Kahn's algorithm
- Design a graph for warehouse robot navigation (grid-based graph with obstacles)

### Apple
- Rotting oranges — why BFS is the natural choice for simultaneous spread
- How would you model a mesh network topology as a graph?
- Implement a simple Dijkstra for shortest path on device location data

### Oracle
- How would you represent a graph for query optimization (relational algebra trees)?
- What is the difference between adjacency list and adjacency matrix for large enterprise graphs?
- How does Oracle's SQL*Net represent network topology as a graph for connection routing?

## Real Production Scenarios

- **Scenario 1: Social Network Friend Recommendations** — A social network represents users and friendships as an undirected graph. BFS within 2-3 hops finds friend-of-friend recommendations. Graph partitioning (offline) distributes the graph across servers for horizontal scaling.

- **Scenario 2: Package Dependency Resolution** — A package manager (npm, Maven) resolves dependency graphs. It models each package as a node and each dependency as a directed edge. Topological sorting determines installation order. Cycle detection prevents unresolvable configurations.

- **Scenario 3: Ride-Share Navigation** — A ride-share app builds a real-time road network graph from map data. Dijkstra's algorithm computes the shortest ETA between driver and rider. Live traffic data adjusts edge weights dynamically. Bidirectional A* search reduces search space by 90%.

## Interview Tips

- Time: O(V+E) for DFS/BFS, O(E log V) for Dijkstra, O(V+E) for topological sort, O(V+E·α(V)) for Union-Find
- Space: O(V) for visited/color arrays, O(V) for queue/stack, O(V²) for adjacency matrix
- Common edge cases: disconnected graph, self-loops, parallel edges, empty graph, single node, cycles
- Always clarify: directed vs undirected, weighted vs unweighted, connected vs disconnected, sparse vs dense
- BFS gives shortest path in unweighted graphs; DFS uses less memory for deep graphs

## Java-Specific Considerations

- No standard `Graph` class — implement with `Map<Integer, List<Integer>>` (adjacency list) or `List<List<Integer>>`
- `HashSet<Integer>` for visited tracking — O(1) average contains check
- `Deque<Integer>` with `ArrayDeque` for BFS queue; `Deque<Integer>` for DFS stack
- `PriorityQueue<int[]>` for Dijkstra (comparator by distance)
- `int[][]` for adjacency matrix of dense graphs (faster array access but O(V²) memory)
- For large graphs, avoid recursive DFS (stack overflow) — use iterative with explicit stack
- `Graph` represented as `Map<Integer, List<int[]>>` for weighted edges (neighbor, weight)

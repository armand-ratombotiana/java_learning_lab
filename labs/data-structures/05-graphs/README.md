# Lab 05: Graphs

<div align="center">

![Difficulty](https://img.shields.io/badge/Difficulty-Intermediate-yellow?style=for-the-badge)
![Time](https://img.shields.io/badge/Time-5_6_hours-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

**Networked data structures — representing and traversing complex relationships**

</div>

---

## Learning Objectives

- Understand graph representations (adjacency matrix, adjacency list)
- Implement BFS and DFS traversal on graphs
- Detect cycles, connected components, and bipartite graphs
- Understand directed vs undirected, weighted vs unweighted graphs
- Implement topological sorting for DAGs
- Explore shortest path algorithms (Dijkstra, Bellman-Ford)

## Prerequisites

- Lab 03: Stacks & Queues (BFS uses queues, DFS uses stacks)
- Recursion and iterative traversal patterns
- Understanding of Big O notation

## Topics Covered

- Graph definitions: vertices/nodes, edges, degree
- Adjacency matrix: O(V²) memory, O(1) edge lookup
- Adjacency list: O(V+E) memory, O(degree) edge lookup
- Edge list representation
- BFS: shortest path in unweighted graphs, connected components
- DFS: cycle detection, topological sort, articulation points
- Graph coloring and bipartite detection
- Dijkstra's algorithm: O((V+E) log V) with heap
- Big O: traversal O(V+E), topological sort O(V+E)
- Common pitfalls: infinite loops on cycles, stack overflow on deep DFS

## Exercises

1. Implement a graph using adjacency list
2. BFS and DFS traversal (both recursive and iterative)
3. Detect a cycle in an undirected graph
4. Implement topological sort (Kahn's algorithm)
5. Find the shortest path (Dijkstra)

## Estimated Time: 5-6 hours

--- 

*Start with* `THEORY.md` *for conceptual understanding, then* `CODE_DEEP_DIVE.md` *for implementations.*

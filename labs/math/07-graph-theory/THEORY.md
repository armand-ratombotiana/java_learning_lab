# Graph Theory: Graphs, Trees, and Traversal Algorithms

## 1. Basic Definitions

### 1.1 Graph
- G = (V, E) where V = vertices, E = edges
- Simple graph: no loops, no multiple edges
- Directed: edges have direction
- Weighted: edges have weights

### 1.2 Degree
- Degree of vertex v: number of edges incident to v
- Sum of degrees = 2|E| (handshaking lemma)
- In-degree + out-degree in directed graphs

## 2. Special Graphs

### 2.1 Complete Graph (Kn)
- Every pair of vertices connected
- |E| = n(n-1)/2

### 2.2 Cycle Graph (Cn)
- n vertices in a cycle
- |E| = n

### 2.3 Path Graph (Pn)
- n vertices in a path
- |E| = n-1

### 2.4 Complete Bipartite (Km,n)
- Vertices split into two sets
- All edges between sets, none within

## 3. Trees

### 3.1 Properties
- Connected, acyclic
- |E| = |V| - 1
- Two vertices connected by unique path

### 3.2 Binary Trees
- Each node has at most 2 children
- Height of tree with n nodes: O(log n) to O(n)

### 3.3 Spanning Trees
- Subgraph containing all vertices
- Acyclic, minimal connecting subgraph

## 4. Graph Traversal

### 4.1 Breadth-First Search (BFS)
- Visit all neighbors before moving deeper
- Uses queue
- O(V + E) time

### 4.2 Depth-First Search (DFS)
- Explore as far as possible before backtracking
- Uses stack (or recursion)
- O(V + E) time

## 5. Shortest Path Algorithms

### 5.1 Dijkstra's Algorithm
- Non-negative weights
- O((V + E) log V)

### 5.2 Bellman-Ford
- Handles negative weights
- O(VE)

### 5.3 Floyd-Warshall
- All pairs shortest path
- O(V³)

## 6. Minimum Spanning Tree

### 6.1 Kruskal's Algorithm
- Sort edges by weight
- Add edge if it doesn't create cycle

### 6.2 Prim's Algorithm
- Start with arbitrary vertex
- Add cheapest edge to tree
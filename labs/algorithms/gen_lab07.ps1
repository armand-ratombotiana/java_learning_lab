$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\07-graph-algorithms"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Graph Algorithms — Overview

Covers BFS, DFS, shortest paths (Dijkstra, Bellman-Ford), and Minimum Spanning Trees (Prim, Kruskal).

## Learning Objectives
- Implement BFS and DFS for graph traversal
- Apply shortest path algorithms (Dijkstra, Bellman-Ford, Floyd-Warshall)
- Construct Minimum Spanning Trees (Prim, Kruskal)
- Detect cycles, find connected components, topological sort

## Prerequisites
- Graph data structure (adjacency list/matrix)
- Queue, stack, priority queue
- Basic recursion and DP concepts

## Estimated Time
- **Total**: 6–8 hours
"@

wf "THEORY.md" @"
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
"@

wf "WHY_IT_EXISTS.md" @"
# Why Graph Algorithms Exist

Graphs model relationships in networks, maps, social connections, and dependencies. Graph algorithms solve fundamental problems: finding paths (navigation), optimal connections (network design), and understanding structure (social networks).
"@

wf "WHY_IT_MATTERS.md" @"
# Why Graph Algorithms Matter

- GPS Navigation: Shortest paths (Dijkstra, A*)
- Social Networks: Friend recommendations (BFS), influence (PageRank)
- Computer Networks: Routing protocols (OSPF = Dijkstra)
- Dependency Resolution: Topological sort (package managers, build systems)
- Circuit Design: MST for wire routing
- AI: State space search (BFS, DFS)
"@

wf "HISTORY.md" @"
# History of Graph Algorithms

- 1736: Euler solved Königsberg bridge problem (graph theory birth)
- 1956: Dijkstra's algorithm
- 1956: Kruskal's MST algorithm
- 1957: Prim's MST algorithm
- 1958: Bellman-Ford algorithm
- 1962: Floyd-Warshall algorithm
- 1972: Tarjan's DFS-based algorithms (SCC, biconnectivity)
"@

wf "MENTAL_MODELS.md" @"
# Mental Models

## BFS — "Ripple in a Pond"
Drop a stone in water: ripples expand outward evenly. BFS explores all nodes at distance d before any at distance d+1.

## DFS — "Explore a Cave"
Walk down a tunnel until you reach a dead end, then backtrack to the last junction and try another path.

## Dijkstra — "Spreading Contamination"
Contamination spreads at speed proportional to edge weight. The first time a node is contaminated, that's the shortest path.

## MST — "Connecting Cities with Minimum Road Cost"
Build roads connecting all cities with minimum total cost.
"@

wf "HOW_IT_WORKS.md" @"
# How Graph Algorithms Work

## BFS
```
Graph: A-B-C, A-D, D-E, C-E
Start at A:
Queue: [A] → visit A → [B, D]
Queue: [B, D] → visit B → [D, C]
Queue: [D, C] → visit D → [C, E]
Queue: [C, E] → visit C → [E]
Queue: [E] → visit E → []
Order: A, B, D, C, E
```

## Dijkstra
```
From A to F:
A(0) → relax B(4), C(2)
C(2) → relax D(5)
B(4) → relax E(6)
D(5) → relax F(7)
E(6) → no update
F(7) → done
```
"@

wf "INTERNALS.md" @"
# Graph Algorithms — Internal Mechanics

## BFS
```java
public List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
    List<Integer> result = new ArrayList<>();
    Queue<Integer> queue = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    queue.offer(start);
    visited.add(start);
    while (!queue.isEmpty()) {
        int node = queue.poll();
        result.add(node);
        for (int neighbor : graph.getOrDefault(node, List.of())) {
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                queue.offer(neighbor);
            }
        }
    }
    return result;
}
```

## DFS Recursive
```java
public void dfs(Map<Integer, List<Integer>> graph, int node, Set<Integer> visited, List<Integer> result) {
    visited.add(node);
    result.add(node);
    for (int neighbor : graph.getOrDefault(node, List.of())) {
        if (!visited.contains(neighbor))
            dfs(graph, neighbor, visited, result);
    }
}
```

## Dijkstra
```java
public int[] dijkstra(Map<Integer, List<Edge>> graph, int n, int src) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    PriorityQueue<Node> pq = new PriorityQueue<>();
    pq.offer(new Node(src, 0));
    while (!pq.isEmpty()) {
        Node curr = pq.poll();
        if (curr.dist > dist[curr.id]) continue;
        for (Edge e : graph.getOrDefault(curr.id, List.of())) {
            int nd = curr.dist + e.weight;
            if (nd < dist[e.to]) {
                dist[e.to] = nd;
                pq.offer(new Node(e.to, nd));
            }
        }
    }
    return dist;
}
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Graph Algorithms

## Graph Definitions
- V vertices, E edges
- Adjacency matrix: O(V²) space
- Adjacency list: O(V + E) space

## Shortest Path Properties
- Triangle inequality: dist(u,v) ≤ dist(u,w) + dist(w,v)
- Subpath of shortest path is also shortest
- No shortest path exists if negative cycle is reachable

## MST Properties
- Cut property: Lightest edge crossing any cut is in some MST
- Cycle property: Heaviest edge in any cycle is not in any MST
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Graph Algorithms

## BFS vs DFS Tree
```
Graph:     BFS from A:     DFS from A:
  A        A (level 0)      A
 / \       |                |
B   C      B-C (level 1)    B
|   |      |                |
D   E      D-E (level 2)    D
                            C
                            E
```

## MST Example
```
Graph:         MST (Prim/Kruskal):
 A--6--B        A--1--B
 |     |        |     |
 5     3        5     3
 |     |        |     |
 D--2--C        D--2--C
```
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Graph Algorithms

## Bellman-Ford (Negative Edge Detection)
```java
public int[] bellmanFord(List<Edge> edges, int n, int src) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    for (int i = 1; i < n; i++) {
        for (Edge e : edges) {
            if (dist[e.u] != Integer.MAX_VALUE && dist[e.u] + e.w < dist[e.v])
                dist[e.v] = dist[e.u] + e.w;
        }
    }
    for (Edge e : edges) {
        if (dist[e.u] != Integer.MAX_VALUE && dist[e.u] + e.w < dist[e.v])
            throw new RuntimeException("Negative cycle detected");
    }
    return dist;
}
```

## Union-Find for Kruskal
```java
class UnionFind {
    int[] parent, rank;
    UnionFind(int n) { parent = IntStream.range(0, n).toArray(); rank = new int[n]; }
    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }
    boolean union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return false;
        if (rank[px] < rank[py]) parent[px] = py;
        else if (rank[px] > rank[py]) parent[py] = px;
        else { parent[py] = px; rank[px]++; }
        return true;
    }
}
```
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Graph Algorithms

## BFS
1. Initialize queue with start node, mark visited
2. While queue not empty:
3.   Poll node, process it
4.   For each unvisited neighbor: mark visited, enqueue

## DFS (Recursive)
1. Mark current node visited, process it
2. For each unvisited neighbor: recursively call DFS

## Dijkstra
1. Initialize dist[src]=0, others=∞
2. Add src to priority queue
3. While queue not empty:
4.   Poll node with minimum distance
5.   If stale (dist > recorded), skip
6.   For each edge: if newDist < dist[neighbor], update and push
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes

- BFS/DFS: Forgetting to mark visited before enqueuing → infinite loop
- Dijkstra: Using with negative edges → wrong results
- Dijkstra: Not skipping stale entries in PQ → O(V log V) becomes O(V²)
- DFS: Stack overflow on deep/large graphs
- Topological sort: Missing cycle detection
- Kruskal: Not using Union-Find → O(VE) instead of O(E log E)
"@

wf "DEBUGGING.md" @"
# Debugging — Graph Algorithms

## Print Graph
```java
private void printGraph(Map<Integer, List<Integer>> graph) {
    for (var entry : graph.entrySet())
        System.out.println(entry.getKey() + " → " + entry.getValue());
}
```

## Visualize Step by Step
```java
System.out.println("Visited: " + visited + " Queue: " + queue);
```

## Test Small Graphs
```java
Map<Integer, List<Integer>> g = Map.of(0, List.of(1), 1, List.of(2), 2, List.of(0));
assertTrue(hasCycle(g));  // triangle
```
"@

wf "REFACTORING.md" @"
# Refactoring — Graph Algorithms

## Graph Interface
```java
public interface Graph {
    void addEdge(int u, int v, int weight);
    List<Integer> bfs(int start);
    int[] shortestPath(int src);
}
```

## Visitor Pattern for Traversal
```java
interface NodeVisitor { void visit(int node); }
```
"@

wf "PERFORMANCE.md" @"
# Performance — Graph Algorithms

| Algorithm | Time | Space | Use Case |
|-----------|------|-------|----------|
| BFS | O(V+E) | O(V) | Unweighted shortest path |
| DFS | O(V+E) | O(V) | Connectivity, cycles |
| Dijkstra | O((V+E) log V) | O(V) | Non-negative weights |
| Bellman-Ford | O(VE) | O(V) | Negative edge detection |
| Floyd-Warshall | O(V³) | O(V²) | All-pairs |
| Prim | O(E log V) | O(V) | MST |
| Kruskal | O(E log E) | O(V) | MST |
"@

wf "SECURITY.md" @"
# Security — Graph Algorithms

- Denial of Service: Maliciously crafted large graphs consume resources
- Dijkstra manipulation: Edge weight injection by untrusted sources
- Cycle detection: Needed to prevent infinite loops in routing
- Negative cycle exploitation: In pricing/financial graph models
"@

wf "ARCHITECTURE.md" @"
# Architecture — Graph Algorithms

## Java Libraries
- JGraphT: Comprehensive graph library
- Guava: Graph, ValueGraph, Network interfaces
- Neo4j Java Driver: Graph database

## Real-World Applications
- GPS: Dijkstra, A* with heuristics
- Social Networks: BFS for degrees of separation
- Web Crawlers: BFS/DFS for page discovery
- Package Managers: Topological sort for dependency resolution
- Network Routing: OSPF = Dijkstra, BGP = path vector
"@

wf "EXERCISES.md" @"
# Exercises — Graph

## Beginner
1. BFS traversal of graph
2. DFS traversal (recursive and iterative)
3. Detect cycle in directed graph
4. Check if graph is bipartite

## Intermediate
5. Number of connected components
6. Topological sort (Kahn's algorithm)
7. Dijkstra's shortest path
8. Detect cycle in undirected graph

## Advanced
9. Bellman-Ford with negative cycle detection
10. Kruskal's MST with Union-Find
11. Prim's MST
12. Floyd-Warshall all-pairs shortest paths
13. Strongly connected components (Tarjan)
"@

wf "QUIZ.md" @"
# Quiz — Graph Algorithms

1. BFS vs DFS: which uses a queue vs stack?
2. Why can't Dijkstra handle negative edges?
3. What is the relaxation operation?
4. How does Bellman-Ford detect negative cycles?
5. What property does Prim's algorithm use?
6. Time complexity of Kruskal's algorithm?
"@

wf "FLASHCARDS.md" @"
# Flashcards

- Q: BFS data structure? → A: Queue
- Q: DFS data structure? → A: Stack (or recursion)
- Q: Dijkstra requirement? → A: Non-negative weights
- Q: Bellman-Ford time? → A: O(VE)
- Q: MST algorithms? → A: Prim (grow tree), Kruskal (add edges)
- Q: Topological sort precondition? → A: DAG
"@

wf "INTERVIEW.md" @"
# Interview Questions

1. "Clone a graph." — BFS/DFS with HashMap
2. "Number of islands." — 2D grid DFS/BFS
3. "Course schedule." — Topological sort / cycle detection
4. "Word ladder." — BFS shortest path
5. "Network delay time." — Dijkstra
6. "Alien dictionary." — Topological sort from letter comparisons
7. "Cheapest flights within K stops." — Bellman-Ford variation
"@

wf "REFLECTION.md" @"
# Reflection

- How does graph representation (adjacency list vs matrix) affect algorithm choice?
- When would you choose BFS over DFS, and vice versa?
- Why is Dijkstra greedy? Does it always find optimal?
- How do shortest path algorithms relate to DP?
- What real-world systems depend on MST algorithms?
"@

wf "REFERENCES.md" @"
# References

- CLRS, Chapters 22-24 (Graph algorithms)
- Sedgewick, R. "Algorithms", Part 5
- Tarjan, R. "Data Structures and Network Algorithms"
- JGraphT library documentation
"@

Write-Host "07-graph-algorithms: All 24 files created"

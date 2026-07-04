# Refactoring Graphs

## Extract Graph Interface

```java
// Before — concrete adjacency matrix
int[][] graph = new int[n][n];
for (...) { graph[u][v] = 1; }

// After — interface-based
interface Graph {
    void addEdge(int u, int v, int weight);
    List<Integer> neighbors(int v);
    int edgeWeight(int u, int v);
}

class AdjacencyListGraph implements Graph { ... }
class AdjacencyMatrixGraph implements Graph { ... }
```

## Use Streams for Processing

```java
// Before — loops
List<Integer> reachable = new ArrayList<>();
boolean[] visited = new boolean[n];
Queue<Integer> q = new LinkedList<>();
q.offer(start);
while (!q.isEmpty()) { ... }

// After — when simple traversal is needed
// (Graph algorithms rarely benefit from streams)
```

## Isolate Graph Algorithms

```java
// Before — mixed concerns
class GraphWithAlgorithms {
    void addEdge(int u, int v) { ... }
    int[] dijkstra(int start) { ... }  // mixed with graph data
}

// After — separated concerns
class Graph {
    void addEdge(int u, int v) { ... }
}

class ShortestPathFinder {
    int[] dijkstra(Graph g, int start) { ... }
}
```

## Replace Manual Iteration with for-each

```java
// Before
for (int i = 0; i < adjList[v].size(); i++) {
    int neighbor = adjList[v].get(i);
}

// After
for (int neighbor : adjList[v]) { ... }
```

## Use Map for Vertex Labels

```java
// Before — integer vertices only
List<Integer>[] adj = new List[10];

// After — generic vertex type
Map<String, Set<String>> graph = new HashMap<>();
graph.computeIfAbsent("A", k -> new HashSet<>()).add("B");
```

# Common Mistakes with Graphs

## Infinite Loop in Graph Traversal

```java
// WRONG — no visited check! Infinite loop on cycles.
void dfs(int v) {
    process(v);
    for (int neighbor : adj[v]) {
        dfs(neighbor);  // endless on cycle!
    }
}

// CORRECT
void dfs(int v, boolean[] visited) {
    visited[v] = true;
    process(v);
    for (int neighbor : adj[v]) {
        if (!visited[neighbor]) dfs(neighbor, visited);
    }
}
```

## Confusing Directed vs Undirected Add Edge

```java
void addEdge(int u, int v) {
    adj[u].add(v);
    // For undirected graph, must also add reverse!
    // adj[v].add(u);  ← FORGETTING THIS IS COMMON
}
```

## Not Handling Disconnected Graphs

```java
// WRONG — only traverses from start vertex
void bfs(int start) {
    Queue<Integer> q = new LinkedList<>();
    boolean[] visited = new boolean[n];
    visited[start] = true;
    q.offer(start);
    // ... rest of BFS ...
    // Misses vertices not reachable from start!
}

// CORRECT — iterate over all vertices
void bfsAll() {
    boolean[] visited = new boolean[n];
    for (int v = 0; v < n; v++) {
        if (!visited[v]) bfs(v, visited);
    }
}
```

## IndexOutOfBounds on Adjacency List

```java
// WRONG — adding edge with vertex >= vertices
addEdge(10, 5);  // if vertices = 6 → IndexOutOfBoundsException

// CORRECT — validate
void addEdge(int u, int v) {
    if (u < 0 || u >= vertices || v < 0 || v >= vertices) {
        throw new IllegalArgumentException();
    }
    adj[u].add(v);
}
```

## Dijkstra Without Priority Queue (O(V²))

```java
// WRONG — O(V²) for dense graphs, but wrong approach reused for sparse
int u = minDistance(dist, visited);  // linear scan each iteration → O(V²)

// For sparse graphs, use PriorityQueue for O((V+E) log V)
```

## Off-by-One in Weighted Graph Distances

```java
// WRONG — initializing with Integer.MAX_VALUE then adding weight overflows
if (dist[u] + w < dist[v]) → Integer.MAX_VALUE + w = negative!
```

Fix: check `dist[u] != Integer.MAX_VALUE` before adding.

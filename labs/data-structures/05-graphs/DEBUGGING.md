# Debugging Graphs

## Common Issues

| Symptom | Likely Cause |
|---------|-------------|
| Infinite loop | No visited check in cyclic graph |
| Missing vertices | Disconnected components not handled |
| Wrong shortest path | Wrong edge relaxation order |
| StackOverflowError | Deep DFS recursion on large graph |
| IndexOutOfBounds | Vertex ID >= number of vertices |

## Debugging Techniques

### Print Graph Structure

```java
void printGraph() {
    for (int v = 0; v < vertices; v++) {
        System.out.print(v + ": ");
        for (int neighbor : adjList[v]) {
            System.out.print(neighbor + " ");
        }
        System.out.println();
    }
}
```

### Trace BFS/DFS

```java
void bfsDebug(int start) {
    boolean[] visited = new boolean[vertices];
    Queue<Integer> queue = new LinkedList<>();
    visited[start] = true;
    queue.offer(start);
    System.out.println("BFS starting from " + start);

    while (!queue.isEmpty()) {
        int v = queue.poll();
        System.out.println("Visiting " + v + ", neighbors: " + adjList[v]);
        for (int n : adjList[v]) {
            if (!visited[n]) {
                visited[n] = true;
                queue.offer(n);
                System.out.println("  Discovered " + n + " from " + v);
            }
        }
    }
}
```

### Verify Graph Invariants

```java
boolean isValidGraph() {
    for (int v = 0; v < vertices; v++) {
        for (int neighbor : adjList[v]) {
            if (neighbor < 0 || neighbor >= vertices) {
                return false;  // invalid vertex reference
            }
        }
    }
    if (directed) return true;
    // For undirected, check symmetry
    for (int u = 0; u < vertices; u++) {
        for (int v : adjList[u]) {
            if (!adjList[v].contains(u)) return false;
        }
    }
    return true;
}
```

### Unit Testing

```java
@Test
void testShortestPath() {
    Graph g = new Graph(5, true);
    g.addEdge(0, 1, 4);
    g.addEdge(0, 2, 1);
    g.addEdge(2, 1, 2);
    int[] dist = g.dijkstra(0);
    assertEquals(0, dist[0]);
    assertEquals(3, dist[1]);  // 0→2→1 = 1+2 = 3
    assertEquals(1, dist[2]);
}

@Test
void testCycleDetection() {
    Graph g = new Graph(3, false);
    g.addEdge(0, 1);
    g.addEdge(1, 2);
    g.addEdge(2, 0);
    assertTrue(g.hasCycle());
}
```

# How Graphs Work

## Adjacency List Implementation

```java
public class Graph {
    private final int vertices;
    private final List<Integer>[] adjList;
    private final boolean directed;

    @SuppressWarnings("unchecked")
    public Graph(int vertices, boolean directed) {
        this.vertices = vertices;
        this.directed = directed;
        adjList = new List[vertices];
        for (int i = 0; i < vertices; i++) {
            adjList[i] = new ArrayList<>();
        }
    }

    public void addEdge(int u, int v) {
        adjList[u].add(v);
        if (!directed) adjList[v].add(u);
    }

    public List<Integer> neighbors(int v) {
        return adjList[v];
    }
}
```

## BFS (Breadth-First Search)

```java
void bfs(int start) {
    boolean[] visited = new boolean[vertices];
    Queue<Integer> queue = new LinkedList<>();
    visited[start] = true;
    queue.offer(start);

    while (!queue.isEmpty()) {
        int v = queue.poll();
        process(v);
        for (int neighbor : adjList[v]) {
            if (!visited[neighbor]) {
                visited[neighbor] = true;
                queue.offer(neighbor);
            }
        }
    }
}
```

## DFS (Depth-First Search)

```java
void dfs(int start) {
    boolean[] visited = new boolean[vertices];
    dfsRecursive(start, visited);
}

void dfsRecursive(int v, boolean[] visited) {
    visited[v] = true;
    process(v);
    for (int neighbor : adjList[v]) {
        if (!visited[neighbor]) {
            dfsRecursive(neighbor, visited);
        }
    }
}
```

## Dijkstra's Shortest Path

```java
int[] dijkstra(int start, int[][] graph) {
    int n = graph.length;
    int[] dist = new int[n];
    boolean[] visited = new boolean[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[start] = 0;

    for (int i = 0; i < n - 1; i++) {
        int u = minDistance(dist, visited);
        visited[u] = true;
        for (int v = 0; v < n; v++) {
            if (!visited[v] && graph[u][v] != 0
                && dist[u] != Integer.MAX_VALUE
                && dist[u] + graph[u][v] < dist[v]) {
                dist[v] = dist[u] + graph[u][v];
            }
        }
    }
    return dist;
}
```

## Topological Sort (Kahn's Algorithm)

```java
List<Integer> topologicalSort() {
    int[] inDegree = new int[vertices];
    for (int u = 0; u < vertices; u++) {
        for (int v : adjList[u]) inDegree[v]++;
    }
    Queue<Integer> queue = new LinkedList<>();
    for (int i = 0; i < vertices; i++) {
        if (inDegree[i] == 0) queue.offer(i);
    }
    List<Integer> result = new ArrayList<>();
    while (!queue.isEmpty()) {
        int u = queue.poll();
        result.add(u);
        for (int v : adjList[u]) {
            if (--inDegree[v] == 0) queue.offer(v);
        }
    }
    return result.size() == vertices ? result : null;  // null if cycle
}
```

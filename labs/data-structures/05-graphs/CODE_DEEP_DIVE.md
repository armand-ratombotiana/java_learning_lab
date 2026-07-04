# Code Deep Dive: Graph Algorithms

## Cycle Detection (Undirected Graph)

```java
boolean hasCycle() {
    boolean[] visited = new boolean[vertices];
    for (int v = 0; v < vertices; v++) {
        if (!visited[v]) {
            if (dfsCycle(v, -1, visited)) return true;
        }
    }
    return false;
}

boolean dfsCycle(int v, int parent, boolean[] visited) {
    visited[v] = true;
    for (int neighbor : adjList[v]) {
        if (!visited[neighbor]) {
            if (dfsCycle(neighbor, v, visited)) return true;
        } else if (neighbor != parent) {
            return true;  // back edge to non-parent
        }
    }
    return false;
}
```

## Bipartite Graph Check

```java
boolean isBipartite() {
    int[] color = new int[vertices];  // 0=uncolored, 1=red, -1=blue
    Arrays.fill(color, 0);
    for (int v = 0; v < vertices; v++) {
        if (color[v] == 0) {
            color[v] = 1;
            Queue<Integer> queue = new LinkedList<>();
            queue.offer(v);
            while (!queue.isEmpty()) {
                int u = queue.poll();
                for (int w : adjList[u]) {
                    if (color[w] == 0) {
                        color[w] = -color[u];
                        queue.offer(w);
                    } else if (color[w] == color[u]) {
                        return false;
                    }
                }
            }
        }
    }
    return true;
}
```

## Dijkstra with PriorityQueue (Optimized)

```java
int[] dijkstra(int start) {
    int[] dist = new int[vertices];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[start] = 0;
    PriorityQueue<int[]> pq = new PriorityQueue<>(
        (a, b) -> a[1] - b[1]  // (vertex, distance)
    );
    pq.offer(new int[]{start, 0});

    while (!pq.isEmpty()) {
        int[] current = pq.poll();
        int u = current[0];
        int d = current[1];
        if (d > dist[u]) continue;  // stale entry
        for (Edge e : adjList[u]) {
            int v = e.to;
            int newDist = dist[u] + e.weight;
            if (newDist < dist[v]) {
                dist[v] = newDist;
                pq.offer(new int[]{v, newDist});
            }
        }
    }
    return dist;
}
```

## Bellman-Ford (Handles Negative Weights)

```java
int[] bellmanFord(int start) {
    int[] dist = new int[vertices];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[start] = 0;
    List<int[]> edges = getAllEdges();  // [from, to, weight]

    for (int i = 0; i < vertices - 1; i++) {
        for (int[] e : edges) {
            if (dist[e[0]] != Integer.MAX_VALUE
                && dist[e[0]] + e[2] < dist[e[1]]) {
                dist[e[1]] = dist[e[0]] + e[2];
            }
        }
    }
    // Check for negative cycles
    for (int[] e : edges) {
        if (dist[e[0]] != Integer.MAX_VALUE
            && dist[e[0]] + e[2] < dist[e[1]]) {
            throw new RuntimeException("Negative cycle detected");
        }
    }
    return dist;
}
```

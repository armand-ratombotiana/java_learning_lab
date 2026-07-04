# Step-by-Step: Graph Theory in Java

## Detect Cycle in Undirected Graph (DFS)

```java
public static boolean hasCycle(Map<Integer, List<Integer>> graph, int n) {
    boolean[] visited = new boolean[n];
    for (int i = 0; i < n; i++)
        if (!visited[i] && dfsCycle(graph, i, -1, visited))
            return true;
    return false;
}

private static boolean dfsCycle(Map<Integer, List<Integer>> graph, int v,
                                 int parent, boolean[] visited) {
    visited[v] = true;
    for (int w : graph.getOrDefault(v, List.of())) {
        if (!visited[w]) {
            if (dfsCycle(graph, w, v, visited)) return true;
        } else if (w != parent) {
            return true; // back edge found
        }
    }
    return false;
}
```

## Check if Graph is Bipartite (BFS)

```java
public static boolean isBipartite(Map<Integer, List<Integer>> graph, int n) {
    int[] color = new int[n]; // 0 = uncolored, 1, -1
    for (int i = 0; i < n; i++) {
        if (color[i] != 0) continue;
        Queue<Integer> q = new LinkedList<>();
        q.add(i);
        color[i] = 1;
        while (!q.isEmpty()) {
            int v = q.poll();
            for (int w : graph.getOrDefault(v, List.of())) {
                if (color[w] == 0) {
                    color[w] = -color[v];
                    q.add(w);
                } else if (color[w] == color[v]) {
                    return false;
                }
            }
        }
    }
    return true;
}
```

## Detect Cycle in Directed Graph

```java
public static boolean hasCycleDirected(Map<Integer, List<Integer>> graph, int n) {
    int[] state = new int[n]; // 0 = unvisited, 1 = visiting, 2 = done
    for (int i = 0; i < n; i++)
        if (state[i] == 0 && dfsDirected(graph, i, state))
            return true;
    return false;
}

private static boolean dfsDirected(Map<Integer, List<Integer>> graph, int v, int[] state) {
    state[v] = 1;
    for (int w : graph.getOrDefault(v, List.of())) {
        if (state[w] == 1) return true;
        if (state[w] == 0 && dfsDirected(graph, w, state)) return true;
    }
    state[v] = 2;
    return false;
}
```

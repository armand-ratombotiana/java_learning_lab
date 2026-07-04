# How Graph Theory Works

## Representations

### Adjacency Matrix
$$
A_{ij} = \begin{cases}
1 & \text{if edge } i \to j \text{ exists} \\
0 & \text{otherwise}
\end{cases}
$$

### Adjacency List
Each vertex maps to a list of neighbors.

## Core Algorithms

### BFS (Breadth-First Search)

```java
public static List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
    Queue<Integer> queue = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    List<Integer> order = new ArrayList<>();
    queue.add(start);
    visited.add(start);
    while (!queue.isEmpty()) {
        int v = queue.poll();
        order.add(v);
        for (int w : graph.getOrDefault(v, List.of()))
            if (!visited.contains(w)) {
                visited.add(w);
                queue.add(w);
            }
    }
    return order;
}
```

### Dijkstra's Shortest Path

```java
public static Map<Integer, Integer> dijkstra(Map<Integer, Map<Integer, Integer>> graph, int start) {
    Map<Integer, Integer> dist = new HashMap<>();
    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    pq.add(new int[]{start, 0});
    dist.put(start, 0);
    while (!pq.isEmpty()) {
        int[] cur = pq.poll();
        int v = cur[0], d = cur[1];
        if (d > dist.getOrDefault(v, Integer.MAX_VALUE)) continue;
        for (var entry : graph.getOrDefault(v, Map.of()).entrySet()) {
            int nd = d + entry.getValue();
            if (nd < dist.getOrDefault(entry.getKey(), Integer.MAX_VALUE)) {
                dist.put(entry.getKey(), nd);
                pq.add(new int[]{entry.getKey(), nd});
            }
        }
    }
    return dist;
}
```

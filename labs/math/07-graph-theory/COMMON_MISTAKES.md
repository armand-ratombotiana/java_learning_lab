# Common Mistakes in Graph Theory

## Directed vs Undirected

```java
// WRONG: adding edge only one way
graph.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
// Missing: graph.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
```

## Off-by-One Vertex Indexing

```java
// If vertices are 1-indexed but array is 0-indexed:
boolean[] visited = new boolean[n + 1]; // need size n+1!
```

## Stack Overflow in DFS

```java
// Recursive DFS on large graph: stack overflow!
// Use iterative DFS with explicit Stack
```

## Infinite Loop in BFS

```java
// WRONG: forgetting to mark as visited when enqueuing
queue.add(start);
while (!queue.isEmpty()) {
    int v = queue.poll();
    for (int w : graph.get(v)) {
        if (!visited.contains(w)) {
            queue.add(w);
            // missing: visited.add(w); ← enqueues w multiple times!
        }
    }
}
```

## Negative Weights with Dijkstra

Dijkstra fails with negative edge weights. Use Bellman-Ford for graphs with negative weights (and detect negative cycles).

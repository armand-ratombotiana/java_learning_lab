# Mathematical Foundation of Bellman-Ford

## 📐 The Recurrence Relation
Bellman-Ford is fundamentally a dynamic programming approach. Let $dp[i][v]$ be the shortest path from source $s$ to vertex $v$ using at most $i$ edges.

The recurrence is:
$$ dp[i][v] = \min(dp[i-1][v], \min_{(u,v) \in E} (dp[i-1][u] + weight(u, v))) $$

Since we only care about the previous iteration, we can optimize space and use a 1D array $dist[v]$.

## 📈 Complexity Analysis

### 1. Time Complexity
- We iterate $V-1$ times.
- In each iteration, we iterate over all $E$ edges.
- **Total Time**: $O(V \times E)$.

Compare this to Dijkstra's $O((V+E) \log V)$. Bellman-Ford is significantly slower, which is why it's only used when negative weights are possible.

### 2. Space Complexity
- We only need to store the current distances to each node.
- **Total Space**: $O(V)$.

## 🛡️ Negative Cycle Proof
If there is no negative cycle, the shortest path between any two vertices has at most $V-1$ edges. Thus, after $V-1$ iterations, $dist[v]$ must be the absolute shortest path.

If $dist[v] > dist[u] + weight(u, v)$ for any edge $(u, v)$ in the $V$-th iteration, it implies that a path with $V$ edges is shorter than any path with $V-1$ edges. This only happens if there is a cycle with a negative sum.
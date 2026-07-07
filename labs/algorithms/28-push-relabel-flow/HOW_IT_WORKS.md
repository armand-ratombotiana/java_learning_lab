# How Push-Relabel Works

## Data Structures

Maintain for each node:
- height[v]: integer label (initially height[s] = n, others = 0).
- excess[v]: flow into v minus flow out of v (initially excess[s] = INF).
- For each edge (u,v), maintain flow f[u][v] and capacity c[u][v].
The residual capacity is c[u][v] - f[u][v] (forward) and f[u][v] (reverse).

## Push Operation

To push flow from u to v:
- delta = min(excess[u], residualCapacity(u, v)).
- f[u][v] += delta; f[v][u] -= delta.
- excess[u] -= delta; excess[v] += delta.
An edge (u,v) is admissible if height[u] == height[v] + 1 and residualCapacity(u,v) > 0.

## Relabel Operation

If excess[u] > 0 and no outgoing edge from u is admissible:
- height[u] = min(height[v] + 1) over all v with residualCapacity(u,v) > 0.
If no such v exists (disconnected from sink), set height[u] = n.

## Main Loop

While there exists an overflowing vertex (excess[v] > 0, v != s, v != t):
- Select an overflowing vertex.
- Try to push along admissible edges.
- If no push possible, relabel the vertex.

## Gap Heuristic

Maintain count[height] for each height value. If after a relabel, count[oldHeight] becomes 0, then all nodes with height > oldHeight are disconnected from the sink. Set their height to n immediately. This dramatically accelerates convergence.

## Min-Cost Max Flow

Maintain potentials phi[v] (Johnson potentials). For each edge (u,v) with cost cost[u][v], the reduced cost is cost_p[u][v] = cost[u][v] + phi[u] - phi[v]. Initially compute phi via Bellman-Ford. In each iteration, run Dijkstra on reduced costs to find shortest augmenting path, send flow, and update potentials: phi[v] += dist[v].
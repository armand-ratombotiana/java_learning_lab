# Push-Relabel — Code Deep Dive

The PushRelabel class uses adjacency lists of Edge objects. Each Edge has to, rev (index of reverse edge), and cap (residual capacity). The reverse edge's cap tracks flow on the forward edge. Initialization sets height[source] = n and pushes flow from source on all outgoing edges.

The push method: for each edge e from u, if height[u] == height[e.to] + 1 && e.cap > 0: delta = min(excess[u], e.cap); e.cap -= delta; graph[e.to].get(e.rev).cap += delta; excess[u] -= delta; excess[e.to] += delta. If e.to != sink, add to overflow queue.

The relabel method: find min height among neighbors with residual capacity. If no such neighbor, set height[u] = n. Otherwise, height[u] = minHeight + 1. After relabel, check gap heuristic: if --count[oldHeight] == 0, scan all vertices and set height[v] = n for any v with height[v] > oldHeight.

The main loop: while overflow queue is not empty: u = queue.poll(); if u != source && excess[u] > 0: try to push from u. If no push possible, relabel u. If after relabel excess[u] > 0, push u back to queue.

The MinCostMaxFlow class uses edge objects with cost, cap, and to/rev. Potentials array phi is initialized with Bellman-Ford. Dijkstra on reduced costs (cost_p = cost + phi[u] - phi[v]) finds shortest augmenting path. After sending flow, update potentials: phi[v] += dist[v].

The CapacityScaling class: start with delta = highest power of two <= max capacity. In each scaling phase, add edges with capacity >= delta. Run max flow on the scaled network. Delta = delta / 2. Repeat until delta = 0.
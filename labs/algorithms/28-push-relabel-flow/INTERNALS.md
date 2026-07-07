# Push-Relabel — Internal Implementation Details

The PushRelabel class implements the generic push-relabel algorithm with FIFO selection rule. Internal structures:
- cap[u][v]: capacity of edge (u,v). Stored in adjacency list with reverse edge pointers.
- flow[u][v]: current flow on edge (u,v).
- height[v]: current height of node v.
- excess[v]: current excess flow at node v.
- count[h]: number of nodes with height == h (for gap heuristic).
- queue: FIFO queue of overflowing nodes.

Edge representation uses edge objects (to, rev, cap). Each undirected edge is stored as two directed edges. The reverse edge's cap field tracks the current flow on the forward edge.

The push operation checks height[u] == height[v] + 1. The relabel operation finds min height[v] over all residual neighbors and sets height[u] = minHeight + 1. If no residual neighbor exists, height[u] = n.

The gap heuristic checks if count[height[u]] == 0 after relabeling u. If so, all nodes with height > height[u] are marked with height = n.

The MinCostMaxFlow class uses potentials for reduced costs. Edge objects include cost and cap fields. The Bellman-Ford initialization computes initial potentials. Each Dijkstra run on reduced costs finds the shortest augmenting path. After augmentation, potentials are updated: potential[v] += dist[v]. Edges with zero residual capacity after augmentation are skipped.

The CapacityScaling class scales capacities by examining the highest bit of capacities and progressively adding lower-order bits. The inner max flow computation uses Dinic or push-relabel on the scaled capacities.
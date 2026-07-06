# Flashcards — Network Flow

- Q: Max-flow min-cut theorem? -> A: Max flow value = min cut capacity
- Q: Ford-Fulkerson worst-case? -> A: O(C*E) with integer capacities
- Q: Edmonds-Karp complexity? -> A: O(V * E^2)
- Q: Dinic complexity? -> A: O(V^2 * E)
- Q: Dinic for unit capacities? -> A: O(sqrt(V) * E)
- Q: Purpose of level graph? -> A: Ensures forward progress toward sink
- Q: Bipartite matching via flow? -> A: Add source/sink with unit capacities
- Q: Min-cut from max flow? -> A: Reachable nodes from source in residual

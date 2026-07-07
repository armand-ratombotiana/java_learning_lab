# History of Push-Relabel and Flow Algorithms

1956: Ford and Fulkerson published the first max flow algorithm, introducing augmenting paths and the max-flow min-cut theorem.

1969: Jack Edmonds and Richard Karp showed that using shortest augmenting paths (Edmonds-Karp) guarantees polynomial time O(V * E^2).

1972: Dinic's algorithm introduced blocking flows and level graphs, improving the bound to O(V^2 * E) and O(E * sqrt(V)) for unit capacities.

1986: Andrew V. Goldberg and Robert E. Tarjan published the push-relabel algorithm (also called preflow-push), the first algorithm to break the O(V * E) barrier asymptotically with O(V^3) bound.

1988: Goldberg introduced the FIFO variant and the gap heuristic, making push-relabel practical. The highest-label selection rule achieved O(V^2 * sqrt(E)).

1990: The relabel-to-front variant was published, providing a clean O(V^3) implementation.

1993: Min-cost max flow was formalized with potentials (Johnson's reweighting), enabling Dijkstra-based augmentations rather than Bellman-Ford.

2000s: Capacity scaling and cost scaling algorithms pushed the theoretical bounds further. Push-relabel with dynamic trees improved to O(V * E * log(V^2/E)).

2010s: GPU-based parallel implementations of push-relabel emerged for computer vision and large-scale network analysis.
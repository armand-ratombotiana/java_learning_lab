# Why Network Flow Algorithms Exist

Network flow theory emerged from operations research and the need to optimize resource distribution through networks. The original problem was motivated by the Soviet railway network: during the Cold War, the US Air Force wanted to determine the maximum amount of cargo that could be transported through Soviet rail lines under capacity constraints.

The Ford-Fulkerson method (1956) provided the first algorithmic framework for solving max flow problems. The key insight was the residual network and augmenting paths, which transformed a complex optimization problem into a sequence of path-finding steps.

However, Ford-Fulkerson with DFS could be exponential in pathological cases. The Edmonds-Karp algorithm (1972) fixed this by using BFS, guaranteeing O(VE^2) time. This was a major theoretical breakthrough that made max flow computations practical for real-world problems.

Dinic's algorithm (1970) independently discovered a similar approach but with the crucial innovation of level graphs and blocking flows. This reduced the number of augmenting phases dramatically, giving O(V^2 E) performance and O(min(V^{2/3}, sqrt(E)) E) for unit capacities.

Network flow is a remarkably versatile framework. It models transportation networks, communication networks, bipartite matching, project selection, baseball elimination, image segmentation, and many other problems. The max-flow min-cut theorem provides a duality that gives deep insights into the structure of these problems.

Modern applications include assigning tasks to processors, routing traffic in computer networks, segmenting images in computer vision, and analyzing social networks. The push-relabel algorithm with its global relabeling heuristic is among the fastest in practice for large sparse graphs.

# Push-Relabel — Mathematical Foundation

## Max-Flow Min-Cut Theorem

For any flow network, the value of the maximum flow equals the capacity of the minimum cut. A cut (S, T) with s in S, t in T has capacity sum of capacities of edges from S to T. The max flow is bounded by this capacity. Push-relabel constructs a cut when no overflowing vertices remain: S = {v | height[v] >= n}, T = V \ S.

## Height Function Invariant

Throughout push-relabel, height is a valid labeling: height[s] = n, height[t] = 0, and for every edge (u,v) with residual capacity > 0, height[u] <= height[v] + 1. This invariant ensures that flow always moves downhill toward the sink. The height of any vertex never exceeds 2n - 1.

## Push-Relabel Complexity

Each relabel increases height[v]. Since height[v] <= 2n and there are n vertices, there are at most O(n^2) relabels. Each push sends flow from a higher to a lower vertex. Saturation pushes (which fill an edge) occur O(n * m) times. Non-saturating pushes can be bounded by O(n^2 * m) in the generic algorithm, or O(n^3) with the FIFO or relabel-to-front variant.

## Min-Cost Flow Optimality

A flow is optimal if there is no negative-cost cycle in the residual graph. Using potentials, reduced costs become non-negative, and Dijkstra finds shortest paths correctly. The optimality condition: for all edges (u,v), reduced cost cost_p(u,v) = cost(u,v) + potential[u] - potential[v] >= 0.

## Capacity Scaling Lemma

With capacity scaling parameter delta, each scaling phase adds at most O(m) augmentations, where each augmentation pushes at least delta flow. Total augmentations across all phases: O(m log U) where U is the maximum capacity.
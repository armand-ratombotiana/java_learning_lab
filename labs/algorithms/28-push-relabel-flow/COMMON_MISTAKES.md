# Push-Relabel — Common Mistakes

1. Not updating reverse edge capacities correctly: when pushing flow forward, the reverse edge's capacity must increase by the same amount. This is critical for the residual graph to be consistent.

2. Forgetting to update excess for both the sending and receiving vertices. Push reduces excess[u] and increases excess[v].

3. Incorrect admissibility condition: an edge is admissible when height[u] == height[v] + 1 AND residual capacity > 0. Many implementations check height[u] > height[v], which violates the invariant.

4. Gap heuristic implementation: after relabeling a vertex, check if count[oldHeight] becomes 0. But the vertex itself was just removed from oldHeight, so decrement count BEFORE checking for gap.

5. The source should not be relabeled: its height stays at n permanently. The sink's height stays at 0.

6. In min-cost flow, potentials must be updated after each augmentation. Forgetting to update potentials leads to negative reduced costs in subsequent Dijkstra runs.

7. Bellman-Ford initialization for potentials must handle negative cost edges. If the graph has no negative cycles, Bellman-Ford will converge.

8. Overflow in capacity: use long (64-bit) for excess and flow when capacities can exceed 2^31 - 1.

9. Not handling disconnected vertices: if a vertex has no path to the sink, its height should be set to n to push flow back to the source.
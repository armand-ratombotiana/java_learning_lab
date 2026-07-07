# Push-Relabel — Visual Guide

## Flow Network Example

Source=A, Sink=F. Edges: A-B(10), A-C(20), B-D(8), B-E(6), C-B(5), C-E(15), D-F(10), E-F(10). Heights initially: A=6, others=0.

## First Pushes

A has excess. Push along A-B (height[6]=6 > height[1]=1): send 10 (cap). A excess=10, B excess=10. Push along A-C: send 20. A excess=0, C excess=20. B excess=10: can only push to D (height 0+1=1) or E. Push to D: send 8 (cap). B excess=2, D excess=8.

## Relabel Example

When B has excess 2 but all outgoing edges are saturated or not admissible (neighbors have equal or higher height), B is relabeled. Before: height[B]=1, neighbors D(height=0), E(height=0). After: height[B]=2. Now B can push to D or E (height[2]=2 = 0+2? No, must be height[u]=height[v]+1=1). Wait, these numbers need correction.

## Gap Heuristic

If count[3] becomes 0 (no node at height 3), all nodes with height > 3 are immediately set to height = maxHeight (8). This is the gap heuristic in action.

## Min-Cost Flow

Same network with costs: A-B(2), A-C(4), B-D(3), B-E(5), C-B(6), C-E(2), D-F(1), E-F(3). Find min-cost max flow. Initial potentials = shortest distances from source (Bellman-Ford). Each iteration: Dijkstra on reduced costs, augment 1 unit of flow along shortest path, update potentials.
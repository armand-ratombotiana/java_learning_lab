# Push-Relabel &amp; Min-Cost Flow — Theoretical Foundation

## Maximum Flow Problem

Given a directed graph G = (V, E) with capacities c(e) &gt;= 0 on edges, source s, and sink t, find the maximum flow from s to t that respects capacity constraints. The push-relabel algorithm approaches this differently from augmenting-path algorithms: it allows flow to temporarily exceed edge capacity (preflow) and uses local operations to gradually convert it into a valid flow.

## Push-Relabel Algorithm

The algorithm maintains a preflow where flow may enter a node but not leave (excess flow). Each node has a height label (distance estimate to the sink). The algorithm repeatedly selects an overflowing vertex and pushes flow along admissible edges (edges where height[u] = height[v] + 1). If no admissible edge exists, the vertex is relabeled (height increased by 1). The algorithm terminates when no overflowing vertices remain except the source.

## Gap Heuristic

A significant optimization is the gap heuristic: if at some point there is a height value h such that no vertex has height h, then all vertices with height &gt; h are disconnected from the sink and can be immediately relabeled to n. This dramatically improves practical performance, especially on hard instances.

## Relabel-to-Front and FIFO Variants

The relabel-to-front variant maintains a list of overflowing vertices and processes them in order. When a vertex is relabeled, it is moved to the front. The FIFO variant uses a queue of overflowing vertices. Both maintain the O(V^2 * sqrt(E)) bound in practice, though the theoretical worst case is O(V^3).

## Min-Cost Max Flow

In min-cost max flow, each edge has both capacity and cost per unit flow. The goal is to find a maximum flow of minimum total cost. The successive shortest augmenting path algorithm finds the minimum-cost augmenting path from s to t using potentials (Johnson's algorithm) to ensure non-negative reduced costs, allowing Dijkstra in each iteration.

## Capacity Scaling

Capacity scaling improves flow algorithms by initially considering only the highest-order bit of capacities and progressively adding lower bits. This technique reduces the number of augmentations and can improve the running time of both max flow and min-cost flow algorithms, particularly when capacities are large.
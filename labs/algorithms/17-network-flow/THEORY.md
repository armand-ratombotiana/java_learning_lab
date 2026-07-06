# Network Flow — Theoretical Foundation

## Flow Network Definition

A flow network is a directed graph G = (V, E) with source s and sink t. Each edge (u,v) has a non-negative capacity c(u,v). A flow f satisfies: (1) capacity constraint: f(u,v) <= c(u,v), (2) skew symmetry: f(u,v) = -f(v,u), (3) flow conservation: sum of flow into v equals sum of flow out of v for all v != s,t.

## Max-Flow Min-Cut Theorem

The maximum value of an s-t flow equals the minimum capacity of an s-t cut. This theorem, proven by Ford and Fulkerson in 1954, is fundamental to flow theory. It shows that finding a maximum flow is equivalent to finding a cut of minimum capacity separating s from t.

## Augmenting Path Algorithms

The residual network G_f has edges with residual capacity c_f(u,v) = c(u,v) - f(u,v). An augmenting path is a path from s to t in G_f. Ford-Fulkerson repeatedly finds any augmenting path and increases flow by the minimum residual capacity along the path.

## Dinic's Algorithm

Dinic's algorithm improves on Edmonds-Karp by computing a level graph (BFS layers from s) and then finding a blocking flow using DFS. Multiple augmentations are performed in each phase, reducing the number of BFS phases to O(sqrt(E)) for unit capacities.

## Push-Relabel Algorithm

Push-Relabel takes a different approach, allowing flow to accumulate at intermediate nodes (excess) and then pushing it forward or relabeling (increasing height) when stuck. This local operation approach leads to O(V^2 E) worst-case time with O(V^3) for the highest-label variant.

## Bipartite Matching via Max Flow

A bipartite graph can be reduced to a flow network by adding a source connected to left vertices and a sink connected to right vertices, with all capacities set to 1. The max flow value equals the size of the maximum matching.

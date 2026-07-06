# Code Deep Dive — Network Flow

## Dinic Algorithm Complete Implementation

The Dinic algorithm implementation requires three components: graph construction with residual edges, BFS for level graph computation, and DFS for blocking flow. The it[] array (current edge pointer) is crucial for the optimizations — it ensures each edge is examined at most once per phase.

## Ford-Fulkerson Edge Cases

With integer capacities, Ford-Fulkerson always terminates. However, if capacities are irrational and the algorithm repeatedly picks bad augmenting paths, it may converge only in the limit (infinite steps). This is a theoretical concern, not practical.

## Bipartite Matching Construction

To use max flow for bipartite matching:
1. Create a source node linked to all left-side vertices (capacity 1)
2. Connect each left vertex to its adjacent right vertices (capacity 1)
3. Connect each right vertex to the sink (capacity 1)
4. Run max flow
5. A flow of 1 on a left-to-right edge indicates a matched pair

## Min-Cut Construction

After max flow is computed, run BFS/DFS from s in the residual network. Vertices reachable from s are in S; others are in T. Any original edge from S to T that is saturated (0 residual capacity) is part of the min-cut.

## Push-Relabel Overview

Each node v has a height h(v) and excess e(v). Initially, h(s) = V, all others 0. Push from source to all neighbors saturates outgoing edges. The main loop pushes excess from higher nodes to lower nodes, relabeling (increasing height) when no push is possible. When no node has excess except s and t, the algorithm terminates.

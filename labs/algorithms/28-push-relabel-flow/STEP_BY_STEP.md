# Push-Relabel — Step by Step Guide

## Step 1: Define Edge and Graph

Create Edge class with to, rev, cap. Create graph as ArrayList<ArrayList<Edge>> of size n. Add edge: forward with given cap, backward with cap 0. store reverse indices.

## Step 2: Initialize Heights and Preflow

Set height[source] = n. Set excess[source] = INF. For each outgoing edge from source: push flow equal to cap. Update edge capacities and excess accordingly.

## Step 3: Create FIFO Queue

Initialize queue with all overflowing vertices (excess > 0, not source, not sink). Process while queue not empty: pop vertex u. If excess[u] == 0, continue. Try pushing from u along each edge where height[u] == height[v] + 1 and cap > 0. If delta > 0: push, update excess, add v to queue if v becomes overflowing.

## Step 4: Relabel If Necessary

If u still has excess after pushing all edges: relabel u. Find min height[v] + 1 over neighbors with cap > 0. If no neighbor, set height[u] = n. Update count array. Check gap heuristic. Push u back to queue.

## Step 5: Terminate

When queue is empty, excess[source] and excess[sink] hold the max flow value. Sum of flow from source = max flow.

## Step 6: Implement Min-Cost Flow

Add cost to edges. Run Bellman-Ford for initial potentials. In each iteration: Dijkstra on reduced costs. Augment 1 unit along shortest path. Update potentials. Continue until no more flow possible.

## Step 7: Test on Known Networks

Test on simple triangle network, then more complex examples. Verify max flow value matches min cut capacity. Verify min-cost flow is optimal (no negative cost cycles in residual).
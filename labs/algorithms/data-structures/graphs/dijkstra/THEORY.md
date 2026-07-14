# Dijkstra's Algorithm Theory & Intuition

## 💡 The Problem: Finding the Best Route
Imagine you are a delivery driver for Amazon. You need to get from the warehouse (Source) to a customer's house (Destination). There are many possible roads, and each road has a "cost" associated with it (distance, time, or fuel).

A standard Breadth-First Search (BFS) finds the path with the fewest number of edges. But in a **Weighted Graph**, the path with 2 long edges might be more expensive than a path with 10 short edges. We need an algorithm that respects the weights.

## 🚀 The Solution: Dijkstra's Algorithm
Published in 1959 by Edsger W. Dijkstra, this algorithm finds the shortest path from a starting node to all other nodes in a graph with non-negative edge weights.

### The Greedy Strategy
Dijkstra's is a **Greedy Algorithm**. It maintains a set of "visited" nodes and a set of "unvisited" nodes.
1. Start at the Source. Set its distance to 0 and all other nodes to $\infty$.
2. Pick the unvisited node with the **smallest known distance** from the source.
3. For that node, look at all its unvisited neighbors.
4. Calculate their distance through the current node. If this new distance is smaller than the previously known distance, update it (**Relaxing the Edge**).
5. Mark the current node as visited.
6. Repeat until all nodes are visited.

## 🛑 The Limitation: Negative Weights
Dijkstra's algorithm **fails** if the graph contains negative edge weights. 
Because it is greedy, once it marks a node as "visited", it assumes it has found the absolute shortest path to that node. A negative edge found later could invalidate that assumption, but Dijkstra's won't go back to re-evaluate. 
For graphs with negative weights, the **Bellman-Ford algorithm** must be used.
# HNSW Internals

## 🌐 Navigable Small World (NSW) Graphs
Before understanding HNSW, we must understand NSW.
Imagine a graph where every node is a vector. Edges connect nodes that are close to each other in the vector space.
To search for a query vector $Q$:
1. Start at a random entry node.
2. Look at all its neighbors.
3. Move to the neighbor that is closest to $Q$.
4. Repeat until you reach a node where no neighbor is closer to $Q$ than the node itself (a local minimum).

*Problem*: If the graph is massive, traversing node by node takes too long.

## 🏢 Hierarchical Navigable Small World (HNSW)
HNSW solves the search speed problem by introducing **layers** (like a skip list).

1. **Layer 0 (Bottom)**: Contains every single vector in the database, fully connected to its nearest neighbors.
2. **Layer 1**: Contains a random subset (e.g., 10%) of the vectors from Layer 0. Edges here cover larger distances.
3. **Layer 2**: Contains a subset of Layer 1.
4. **Layer L (Top)**: Contains very few nodes (often just one entry point).

### The Search Process
1. Start at the top layer. Find the node closest to the query $Q$.
2. Drop down to the next layer, using the node found in the previous step as the starting point.
3. Search this layer for the closest node to $Q$.
4. Repeat until you reach Layer 0.
5. In Layer 0, perform a localized search to find the exact $K$-nearest neighbors.

This hierarchical approach allows the algorithm to take massive "leaps" across the vector space in the upper layers, zooming in on the correct neighborhood logarithmically. This reduces search time from $O(N)$ to **$O(\log N)$**.

## ⚙️ Insertion Mechanics
When a new vector is inserted:
1. An integer level $l$ is assigned randomly (with an exponentially decaying probability). Most nodes get level 0, a few get level 1, very few get level 2.
2. The algorithm searches from the top layer down to level $l$ to find the entry point.
3. From level $l$ down to level 0, the node is inserted and connected to its $M$ nearest neighbors at that specific layer.
4. If a node's connections exceed $M_{max}$, the longest edges are pruned to maintain graph efficiency.
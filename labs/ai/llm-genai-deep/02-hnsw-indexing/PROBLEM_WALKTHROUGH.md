# PROBLEM WALKTHROUGH: Hierarchical Navigable Small World (HNSW) Index

## Problem Statement

**Difficulty: Hard | Category: Vector Search / Approximate Nearest Neighbor**

Implement a simplified Hierarchical Navigable Small World (HNSW) graph for approximate nearest neighbor (ANN) search. Given a set of high-dimensional vectors, build a multi-layer graph structure that enables fast approximate k-nearest neighbor queries.

**Interview Context:** HNSW is the state-of-the-art algorithm for vector search, used by Pinecone, Weaviate, Qdrant, and Milvus. Interviewers expect you to understand multi-layer graph navigation, the probabilistic layer assignment, and the trade-off between search speed and recall.

### Requirements

1. Represent each data point as a node in a multi-layer graph.
2. Each node is assigned to a maximum layer `l = floor(-ln(uniform(0,1)) * mL)` where `mL = 1/ln(M)`.
3. Implement **Insert(node)**: connect to `M` nearest neighbors in layer 0, `M_max` in upper layers.
4. Implement **Search(query, k, ef)**: beam search from the entry point, descending layers.
5. Implement **KNNSearch(query, k)**: return top-k nearest neighbors with distance computations.
6. Support configurable `M` (connections per node), `efConstruction` (search width during insertion), and `M_max` (max connections).

### Input/Output Contract

```
Input:  Set of vectors V = {v_1, ..., v_n}, each of dimension d.
        Query vector q, number of nearest neighbors k, ef parameter.
Output: Ordered list of top-k nearest neighbors (index + distance).
```

---

## Step-by-Step Solution Walkthrough

### 1. Understanding Navigable Small World Graphs

A Navigable Small World (NSW) graph connects each node to its nearest neighbors. When navigating from an entry point to a query, greedy search follows edges that reduce distance. The "small world" property ensures the path length grows as O(log n) rather than O(n).

**The problem with single-layer NSW:** As the graph grows, search can get stuck in local optima because there's no "zoom out" mechanism.

### 2. Hierarchical Navigation

HNSW solves this by organizing nodes across layers:

- **Layer 0:** Contains all nodes with up to `M_max` connections.
- **Layer L:** The topmost layer, sparse, contains only a few nodes.
- **Layer l:** A node present in layer `l` is also present in all layers `0` through `l`.

Navigation proceeds top-down:
1. Start at the entry point (a node in the topmost layer).
2. Greedily descend through each layer, using the found nearest neighbor in layer `h+1` as the entry point for layer `h`.
3. At layer 0, perform a more exhaustive beam search.

### 3. Probabilistic Layer Assignment

The layer for a new node is determined by:

```
l = floor(-ln(uniform(0,1)) * mL)
```

where `mL = 1 / ln(M)`. This produces an exponential distribution where:
- P(layer >= 0) = 1.0 (all nodes in layer 0)
- P(layer >= 1) = exp(-1/mL) ≈ 1 - 1/M
- P(layer >= 2) = exp(-2/mL) ≈ (1 - 1/M)²

The expected number of layers is `~log_{M}(n)`, ensuring logarithmic search complexity.

### 4. Beam Search (Search Layer)

Within a layer, search proceeds as a beam search:

```
Input: query q, entry point ep, ef (beam size), layer lc
Output: ef nearest neighbors in layer lc

visited = {ep}
candidates = min-heap ordered by distance(ep, q)
results = max-heap ordered by -distance(ep, q) (size ef)

while candidates not empty:
    c = candidates.pop()  // closest to query
    f = results.peek()     // farthest in results
    
    if distance(c, q) > distance(f, q):
        break  // cannot improve
    
    for each neighbor n of c:
        if n not visited:
            visited.add(n)
            candidates.push(n)
            results.push(n)
            if results.size() > ef:
                results.pop()
```

**Key insight:** The break condition works because if the closest candidate is already farther than the farthest result, no other candidate can improve the current results.

### 5. Layer Selection During Insert

When inserting a node `new_node` at layer `l`:

1. Start at the entry point of the topmost layer.
2. For layers `topmost` down to `l+1`: greedy search to find the closest node (ef=1).
3. For layer `l` down to `0`: beam search with `ef = efConstruction` to find neighbors, then connect bidirectionally.

**Bidirectional connection rule:**
- Add `new_node` to neighbor lists of its nearest neighbors.
- If a neighbor's list exceeds `M_max`, prune it to keep only the `M_max` closest.
- This maintains the small-world property while capping graph degree.

---

## Java Implementation

```java
package com.llm.genai.deep.hnsw;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implements a Hierarchical Navigable Small World (HNSW) index for
 * approximate nearest neighbor search in high-dimensional spaces.
 * <p>
 * HNSW organizes vectors into a multi-layer graph where upper layers
 * provide long-range connections for efficient navigation and lower
 * layers provide dense local connectivity.
 */
public class HNSW {

    private final int dimension;
    private final int M;
    private final int Mmax;
    private final int Mmax0;
    private final int efConstruction;
    private final double mL;
    private final List<Node> nodes;
    private int entryPoint;
    private int maxLayer;

    /**
     * Represents a graph node with its vector and neighbor lists per layer.
     */
    private static class Node {
        final float[] vector;
        final List<Set<Integer>> neighbors; // neighbors[layer] = set of node indices

        Node(float[] vector) {
            this.vector = vector;
            this.neighbors = new ArrayList<>();
        }

        void ensureLayer(int layer) {
            while (neighbors.size() <= layer) {
                neighbors.add(new HashSet<>());
            }
        }

        int numLayers() {
            return neighbors.size();
        }
    }

    /**
     * Constructs an HNSW index with specified parameters.
     *
     * @param dimension      dimensionality of vectors
     * @param M              number of bi-directional connections per element (default 16)
     * @param efConstruction beam search width during construction (default 200)
     */
    public HNSW(int dimension, int M, int efConstruction) {
        this.dimension = dimension;
        this.M = M;
        this.Mmax = M;
        this.Mmax0 = 2 * M;
        this.efConstruction = efConstruction;
        this.mL = 1.0 / Math.log(M);
        this.nodes = new ArrayList<>();
        this.entryPoint = -1;
        this.maxLayer = -1;
    }

    /**
     * Default constructor with standard parameters.
     */
    public HNSW(int dimension) {
        this(dimension, 16, 200);
    }

    /**
     * Computes Euclidean (L2) distance between two vectors.
     *
     * @param a first vector
     * @param b second vector
     * @return Euclidean distance
     */
    private float distance(float[] a, float[] b) {
        float sum = 0.0f;
        for (int i = 0; i < a.length; i++) {
            float diff = a[i] - b[i];
            sum += diff * diff;
        }
        return (float) Math.sqrt(sum);
    }

    /**
     * Assigns a random layer for a new node using the exponential decay
     * distribution: l = floor(-ln(uniform(0,1)) * mL).
     *
     * @return layer index (0 = bottom layer containing all nodes)
     */
    private int randomLayer() {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        double u = rng.nextDouble();
        return (int) (-Math.log(u) * mL);
    }

    /**
     * Inserts a vector into the HNSW index.
     *
     * @param vector the vector to insert
     * @return the index of the new node
     */
    public int insert(float[] vector) {
        if (vector.length != dimension) {
            throw new IllegalArgumentException("Vector dimension mismatch");
        }

        int nodeIdx = nodes.size();
        Node node = new Node(vector);
        nodes.add(node);
        int nodeLayer = randomLayer();

        // Initialize neighbor sets for all layers the node will occupy
        node.ensureLayer(nodeLayer);

        if (entryPoint == -1) {
            // First node: set as entry point and return
            entryPoint = nodeIdx;
            maxLayer = nodeLayer;
            return nodeIdx;
        }

        // Determine the topmost layer to start search from
        int searchLayer = Math.max(maxLayer, nodeLayer);
        int currEntryPoint = entryPoint;

        // Phase 1: traverse upper layers (ef=1 greedy, no connections made)
        for (int layer = searchLayer; layer > nodeLayer; layer--) {
            currEntryPoint = searchLayerGreedy(vector, currEntryPoint, layer);
        }

        // Phase 2: insert into layers nodeLayer down to 0
        Set<Integer> visitedSet = new HashSet<>();
        for (int layer = Math.min(nodeLayer, maxLayer); layer >= 0; layer--) {
            int ef = (layer == nodeLayer) ? efConstruction : 1;
            Set<Integer> neighbors = searchLayerBeam(vector, currEntryPoint, layer, ef, visitedSet);

            // Select M closest neighbors
            List<Integer> topNeighbors = selectNeighbors(neighbors, vector,
                    layer == 0 ? Mmax0 : M);

            // Connect bidirectionally
            for (int neighborIdx : topNeighbors) {
                node.neighbors.get(layer).add(neighborIdx);
                Node neighborNode = nodes.get(neighborIdx);
                neighborNode.ensureLayer(layer);
                neighborNode.neighbors.get(layer).add(nodeIdx);

                // Prune if neighbor exceeds Mmax
                int limit = (layer == 0) ? Mmax0 : Mmax;
                if (neighborNode.neighbors.get(layer).size() > limit) {
                    pruneNeighbors(neighborIdx, layer, limit);
                }
            }

            currEntryPoint = topNeighbors.get(0);
        }

        // Update entry point if new node is in a higher layer
        if (nodeLayer > maxLayer) {
            maxLayer = nodeLayer;
            entryPoint = nodeIdx;
        }

        return nodeIdx;
    }

    /**
     * Greedy search within a single layer returning the single closest node.
     */
    private int searchLayerGreedy(float[] query, int entryIdx, int layer) {
        int best = entryIdx;
        float bestDist = distance(query, nodes.get(best).vector);
        boolean improved = true;

        while (improved) {
            improved = false;
            Node bestNode = nodes.get(best);
            if (layer < bestNode.numLayers()) {
                for (int neighborIdx : bestNode.neighbors.get(layer)) {
                    float d = distance(query, nodes.get(neighborIdx).vector);
                    if (d < bestDist) {
                        bestDist = d;
                        best = neighborIdx;
                        improved = true;
                    }
                }
            }
        }
        return best;
    }

    /**
     * Beam search within a single layer, returning up to ef nearest neighbors.
     */
    private Set<Integer> searchLayerBeam(float[] query, int entryIdx, int layer,
                                         int ef, Set<Integer> visitedSet) {
        // Min-heap of candidates (closest first)
        PriorityQueue<int[]> candidates = new PriorityQueue<>(
                Comparator.comparingDouble(a -> a[1]));
        // Max-heap of results (farthest first, limited to ef)
        PriorityQueue<int[]> results = new PriorityQueue<>(
                Comparator.<int[]>comparingDouble(a -> a[1]).reversed());

        float entryDist = distance(query, nodes.get(entryIdx).vector);
        candidates.add(new int[]{entryIdx, Float.floatToIntBits(entryDist)});
        results.add(new int[]{entryIdx, Float.floatToIntBits(entryDist)});

        Set<Integer> visited = new HashSet<>();
        visited.add(entryIdx);

        while (!candidates.isEmpty()) {
            int[] closest = candidates.poll();
            float closestDist = Float.intBitsToFloat(closest[1]);

            if (!results.isEmpty()) {
                int[] farthestResult = results.peek();
                float farthestDist = Float.intBitsToFloat(farthestResult[1]);
                if (closestDist > farthestDist) {
                    break;
                }
            }

            Node closestNode = nodes.get(closest[0]);
            if (layer < closestNode.numLayers()) {
                for (int neighborIdx : closestNode.neighbors.get(layer)) {
                    if (!visited.contains(neighborIdx)) {
                        visited.add(neighborIdx);
                        if (visitedSet != null) visitedSet.add(neighborIdx);

                        float d = distance(query, nodes.get(neighborIdx).vector);
                        int[] entry = new int[]{neighborIdx, Float.floatToIntBits(d)};

                        if (results.size() < ef) {
                            candidates.add(entry);
                            results.add(entry);
                        } else {
                            int[] farthest = results.peek();
                            float farthestDist = Float.intBitsToFloat(farthest[1]);
                            if (d < farthestDist) {
                                candidates.add(entry);
                                results.add(entry);
                                results.poll(); // remove farthest
                            }
                        }
                    }
                }
            }
        }

        Set<Integer> resultSet = new HashSet<>();
        for (int[] r : results) {
            resultSet.add(r[0]);
        }
        return resultSet;
    }

    /**
     * Selects top-M nearest neighbors from a candidate set for connection.
     */
    private List<Integer> selectNeighbors(Set<Integer> candidates,
                                          float[] query, int M) {
        return candidates.stream()
                .map(idx -> new int[]{idx, Float.floatToIntBits(
                        distance(query, nodes.get(idx).vector))})
                .sorted(Comparator.comparingDouble(a -> Float.intBitsToFloat(a[1])))
                .limit(M)
                .map(a -> a[0])
                .toList();
    }

    /**
     * Prunes a node's neighbor list at a given layer.
     */
    private void pruneNeighbors(int nodeIdx, int layer, int limit) {
        Node node = nodes.get(nodeIdx);
        float[] nodeVec = node.vector;
        Set<Integer> neighbors = node.neighbors.get(layer);

        List<Integer> sorted = neighbors.stream()
                .map(idx -> new int[]{idx, Float.floatToIntBits(
                        distance(nodeVec, nodes.get(idx).vector))})
                .sorted(Comparator.comparingDouble(
                        a -> Float.intBitsToFloat(a[1])))
                .limit(limit)
                .map(a -> a[0])
                .toList();

        node.neighbors.set(layer, new HashSet<>(sorted));
    }

    /**
     * Performs approximate k-nearest neighbor search.
     *
     * @param query the query vector
     * @param k     number of nearest neighbors to return
     * @return list of (index, distance) pairs sorted by distance ascending
     */
    public List<Map.Entry<Integer, Float>> knnSearch(float[] query, int k) {
        return knnSearch(query, k, 100);
    }

    /**
     * Performs approximate k-nearest neighbor search with configurable ef.
     *
     * @param query the query vector
     * @param k     number of nearest neighbors to return
     * @param ef    beam search width (higher = better recall, slower)
     * @return list of (index, distance) pairs sorted by distance ascending
     */
    public List<Map.Entry<Integer, Float>> knnSearch(float[] query, int k, int ef) {
        if (nodes.isEmpty()) return Collections.emptyList();
        if (k > nodes.size()) k = nodes.size();
        if (ef < k) ef = k;

        int currEntryPoint = entryPoint;

        // Phase 1: traverse from top layer down to layer 0 (greedy)
        for (int layer = maxLayer; layer > 0; layer--) {
            currEntryPoint = searchLayerGreedy(query, currEntryPoint, layer);
        }

        // Phase 2: beam search at layer 0
        Set<Integer> candidates = searchLayerBeam(query, currEntryPoint, 0, ef, null);

        return candidates.stream()
                .map(idx -> Map.entry(idx, distance(query, nodes.get(idx).vector)))
                .sorted(Map.Entry.comparingByValue())
                .limit(k)
                .collect(ArrayList::new,
                        (list, e) -> list.add(e),
                        (l1, l2) -> l1.addAll(l2));
    }

    /**
     * Returns the number of nodes in the index.
     */
    public int size() {
        return nodes.size();
    }

    /**
     * Returns the total number of edges across all layers.
     */
    public long totalEdges() {
        long count = 0;
        for (Node node : nodes) {
            for (Set<Integer> layer : node.neighbors) {
                count += layer.size();
            }
        }
        return count / 2; // each edge counted twice
    }

    /**
     * Main method demonstrating HNSW construction and search.
     */
    public static void main(String[] args) {
        int dimension = 128;
        HNSW index = new HNSW(dimension, 16, 200);

        // Insert 1000 random vectors
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int i = 0; i < 1000; i++) {
            float[] vec = new float[dimension];
            for (int d = 0; d < dimension; d++) {
                vec[d] = rng.nextFloat();
            }
            index.insert(vec);
        }

        System.out.println("Index size: " + index.size());
        System.out.println("Total edges (undirected): " + index.totalEdges());

        // Query with a random vector
        float[] query = new float[dimension];
        for (int d = 0; d < dimension; d++) {
            query[d] = rng.nextFloat();
        }

        List<Map.Entry<Integer, Float>> results = index.knnSearch(query, 10, 100);
        System.out.println("\nTop-10 nearest neighbors:");
        for (int i = 0; i < results.size(); i++) {
            System.out.printf("  %d: idx=%d, distance=%.4f%n",
                    i + 1, results.get(i).getKey(), results.get(i).getValue());
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

**Insertion:**
- Phase 1 (upper layer search): O(L × deg × d) where `L = maxLayer - nodeLayer`. Each step does greedy search across neighbors. Upper layers have small degree (≈ M).
- Phase 2 (insertion search): O(efConstruction × M × d) per target layer.
- Pruning: O(M × d) per node.
- **Expected total:** O(log n × M × d) per insertion. The `log n` factor comes from the expected number of layers.

**Search (top-k):**
- Phase 1 (descending greedy): O(log n × M × d).
- Phase 2 (beam search, layer 0): O(ef × M × d).
- **Total:** O((log n × M × d) + (ef × M × d)) = O(log n × M × d) when ef is moderate.

**Comparison with brute force:** Brute force kNN is O(n × d). HNSW is O(log n × d), typically 10-100x faster for n=10^6.

### Space Complexity

- Nodes: O(n × d) for vectors.
- Edges: O(n × M × L_avg) where `L_avg = 1/(1 - exp(-1/mL))` ≈ log_{M}(n).
- Total graph memory: O(n × M × log_M(n)).
- **Dominant factor:** The vector storage at O(n × d).

**Example:** n=10^6, d=768, M=16 → vectors ≈ 3 GB, edges ≈ 10^6 × 16 × 2 = 32M edges ≈ 256 MB.

---

## Follow-Up Questions

### Q1: What is the relationship between M, efConstruction, and recall?

**Answer:**
- **M** (connections per node): Higher M increases graph density → higher recall but more memory. Too high M creates a "mega-hub" effect where some nodes connect to everyone. Optimal M is 12-48 depending on dimensionality.
- **efConstruction** (search width during insertion): Higher values find better neighbors → better connectivity → higher recall at query time. Diminishing returns beyond 200-500.
- **Trade-off:** Doubling M increases memory by 2x and insertion time by 2x, but gives 2-5% recall improvement. Doubling efConstruction gives similar recall gains with lower memory cost.

### Q2: How does HNSW compare to IVF (Inverted File Index)?

**Answer:**
| Aspect | HNSW | IVF |
|--------|------|-----|
| Search speed | O(log n) | O(n^(1/2)) with k-means |
| Memory overhead | O(n × M × log n) edges | O(n + k) cluster centroids |
| Insert dynamics | Dynamic (no retrain) | Requires re-clustering |
| Recall at high speed | Better for high-dimensional | Better for low-dimensional |
| Concurrent inserts | Hard (graph locking) | Easier (add to cluster) |

HNSW dominates in high-dimensional (d > 100) spaces where most vectors are nearly equidistant. IVF is simpler and preferred for low-dimensional or metric spaces.

### Q3: How do you handle deletions in HNSW?

**Answer:** HNSW deletions are non-trivial because removing a node breaks connectivity. Three strategies:
1. **Lazy deletion (mark-and-sweep):** Mark as deleted, rebuild periodically.
2. **Full reconnection:** Remove the node, then reconnect its neighbors by re-running the insertion neighbor selection for each neighbor (expensive).
3. **Delta index:** Maintain a separate tombstone list and filter search results. Simple but degrades over time.

Most production systems use lazy deletion with periodic re-indexing.

### Q4: Explain the distance metric choices for HNSW.

**Answer:** HNSW works with any metric distance. Common choices:
- **L2 (Euclidean):** Default for most embeddings. Sensitive to scale.
- **Cosine (via L2 normalization):** Use `1 - cos_sim` as distance. Pre-normalize vectors to unit length, then L2 distance = 2 - 2*cos(θ).
- **Inner product:** Not a true metric but convertible by sorting reversed scores.
- **Hamming:** For binary embeddings. Use popcount-based distance.

**Important:** The triangle inequality is not required for HNSW to work, but recall degrades for non-metric distances.

### Q5: How do you parallelize HNSW construction?

**Answer:** Naive sequential insertion is slow for large datasets. Parallelization strategies:
1. **Bulk loading:** Sort vectors by some criterion, insert in batches, use a single-threaded "insert from scratch" approach with batches of 100-1000.
2. **Lock-free approaches:** Use atomic compare-and-swap for neighbor updates. Complex but fast.
3. **Distributed HNSW:** Shard across machines by vector ID hash. Each shard builds its own HNSW. Query all shards and merge results.
4. **GPU HNSW:** Parallelize distance computations on GPU during beam search.

---

## Test Cases

### Test Case 1: Single Node Insert

```
Input: insert([1.0, 0.0, 0.0])
Expected: entryPoint = 0, maxLayer >= 0, size = 1
Search for any query returns node 0.
```

### Test Case 2: Two Nodes

```
Input: insert([0.0, 0.0]), insert([1.0, 0.0])
Expected: 
  - Node 0 connected to node 1 in layer 0 (and possibly above)
  - Total edges = 2 (bidirectional)
  - Query for [0.5, 0.0] returns both, with node 0 as closest
```

### Test Case 3: Self-Query is Exact

```
Input: insert many random vectors, then query for one that was inserted
Expected: The inserted vector itself should be the #1 result with distance ≈ 0.0
```

### Test Case 4: Recall vs Brute Force

```
Input: 1000 random 4D vectors, HNSW(M=16, efConstruction=200)
Query: 10 random vectors, compute recall@10
Expected: recall@10 > 0.90 (90% of true top-10 are among HNSW results)
```

### Test Case 5: Distinct Top-K

```
Input: knnSearch(query, 10)
Expected: All 10 results have distinct indices (no duplicates)
```

### Test Case 6: Layer Assignment Distribution

```
Input: Insert 10,000 vectors, record their layer assignments
Expected distribution:
  Layer 0: 10,000 (100%)
  Layer 1: ~10,000 * (1 - 1/M) ≈ 9,375
  Layer 2: ~9,375 * (1 - 1/M) ≈ 8,789
  ...
  Max layer: roughly log_{M}(n) ≈ log_{16}(10,000) ≈ 3.3
```

---

## Summary

HNSW achieves O(log n) approximate nearest neighbor search by combining:
1. **Hierarchical layering** for multi-scale navigation (zoom out / zoom in).
2. **Small-world connectivity** where each node connects to its neighbors and neighbors-of-neighbors.
3. **Beam search** within layers to balance exploration vs exploitation.
4. **Probabilistic layer assignment** that scales naturally with dataset size.

The Java implementation above captures the core algorithm with proper separation of concerns. Production HNSW systems add SIMD-optimized distance computations, concurrent insertion, and persistence layers, but the fundamental algorithm remains the same.
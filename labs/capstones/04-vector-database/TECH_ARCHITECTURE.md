# Technical Architecture: Vector Database

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     Vector Database                          │
├─────────────────────────────────────────────────────────────┤
│  ┌───────────────────┐     ┌──────────────────────────┐    │
│  │   HNSW Graph       │     │   Flat Index (Fallback)   │    │
│  │   (ANN Search)     │     │   (Exact Search)          │    │
│  ├───────────────────┤     ├──────────────────────────┤    │
│  │ Layer 3: 1 node    │     │ O(N) distance computation│    │
│  │ Layer 2: 100 nodes │     │ Used for:                │    │
│  │ Layer 1: 10K nodes │     │ • Small datasets <10K    │    │
│  │ Layer 0: all nodes │     │ • Ground truth for eval  │    │
│  └────────┬──────────┘     └──────────────────────────┘    │
│           │                                                 │
│  ┌────────▼──────────────────────────────────────┐         │
│  │         Vector Storage Engine                   │         │
│  │  ┌────────────┐ ┌──────────┐ ┌─────────────┐  │         │
│  │  │ Float[]    │ │ Metadata │ │ Tombstone    │  │         │
│  │  │ Vectors    │ │ Store    │ │ Set          │  │         │
│  │  └────────────┘ └──────────┘ └─────────────┘  │         │
│  └───────────────────────────────────────────────┘         │
│           │                                                 │
│  ┌────────▼──────────────────────────────────────┐         │
│  │         Persistence Layer                       │         │
│  │  ┌────────────┐ ┌──────────┐ ┌─────────────┐  │         │
│  │  │ vectors.bin│ │ hnsw.idx │ │ metadata.json│  │         │
│  │  │ (Binary)   │ │(Serial)  │ │ (JSON)       │  │         │
│  │  └────────────┘ └──────────┘ └─────────────┘  │         │
│  └───────────────────────────────────────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

## Component Breakdown

### 1. HNSW Graph Index
- **Purpose**: Approximate Nearest Neighbor (ANN) search with O(log N) complexity
- **Structure**: Multi-layer graph where upper layers are sparser (low connectivity) and lower layers are denser (high connectivity)
- **Layer 0**: Contains all vectors, M connections per node, ~16 neighbors each
- **Upper layers**: Exponential decay via mL parameter; layer k contains ~N * exp(-k/ml) nodes
- **Entry point**: Topmost node in highest layer; starting point for all searches
- **Insertion process**: Traverse top-down to find neighborhood → connect to M nearest neighbors → bidirectionally update neighbors' connections → prune if exceeding Mmax

### 2. Vector Storage Engine
- **Memory layout**: Flat float[] arrays stored in ConcurrentHashMap<Integer, float[]> for O(1) access
- **Normalization**: Vectors normalized to unit length on insert; all operations use normalized vectors
- **Metadata**: Separate ConcurrentHashMap<Integer, Map<String, String>> for attribute filtering
- **Tombstones**: ConcurrentHashMap.newKeySet() for deleted vectors; skipped during search

### 3. Persistence Layer
- **vectors.bin**: Binary format: [count:int][dim:int][id:int][float...] for each vector
- **hnsw.idx**: Java serialization of HNSWGraph object (production: use mmap + custom binary format)
- **metadata.json**: Jackson serialization of metadata map to JSON file
- **Recovery**: On startup, load vectors first, then rebuild index if index file is stale
- **Checkpointing**: Periodic incremental checkpoint every 1000 inserts; flush to disk on clean shutdown

### 4. Distance Metrics
- **Cosine similarity**: Default metric for text embeddings; implemented as dot product on normalized vectors
- **L2 distance**: Euclidean distance; stored as squared distance to avoid sqrt in comparisons
- **Inner product**: Direct dot product; no vector normalization required

## HNSW Graph Structure

```
Layer 3 (top):     ●───●───●
                     /     \
Layer 2:         ●───●───●───●───●
                   /   / \   \   \
Layer 1:      ●───●───●───●───●───●───●───●
              /   /   /   |   \   \   \   \
Layer 0:     ●───●───●───●───●───●───●───●───●───● (all nodes)
(base)

Search path: Entry at Layer 3 → greedy descent → Layer 2 → greedy descent → Layer 1 → greedy descent → Layer 0 → efSearch expansion
```

## Search Algorithm

```
1. Start at entry point (top layer)
2. For each layer from top to layer 1:
   a. Greedy search: evaluate all neighbors, move to nearest
   b. Continue until no nearer neighbor found
3. At layer 0:
   a. Initialize result set with entry point
   b. Maintain priority queue of candidates (nearest first)
   c. Maintain max-heap of results (farthest first, size = efSearch)
   d. While candidates not empty:
      - Pop nearest candidate
      - If farther than farthest result, break
      - For each neighbor:
        - Compute distance
        - If closer than farthest result or results < efSearch:
          - Add to candidates and results
          - If results > efSearch, pop farthest
4. Return top-K from result set
```

## Tech Stack

| Component | Technology | Purpose |
|-----------|------------|---------|
| Language | Java 21 | Runtime |
| Vector API | jdk.incubator.vector | SIMD-optimized math |
| Indexing | HNSW (custom) | ANN search |
| Serialization | Jackson + custom binary | Persistence |
| Concurrency | ConcurrentHashMap, ForkJoinPool | Parallel operations |
| Metrics | Micrometer + Prometheus | Observability |
| Testing | JUnit 5 + jmh | Unit + performance tests |

## Configuration

```yaml
index:
  type: HNSW
  m: 16              # Max connections per node (per layer)
  mmax: 32           # Max connections for upper layers
  efConstruction: 200  # Dynamic candidate list during construction
  mL: 0.277          # Level normalization = 1/ln(M)
  distanceMetric: COSINE  # COSINE, L2, INNER_PRODUCT

storage:
  vectorDimension: 768
  persistencePath: /data/vector-db/
  autoPersistIntervalMs: 60000

search:
  defaultEfSearch: 100
  defaultTopK: 10
  timeoutMs: 1000

performance:
  bulkInsertBatchSize: 1000
  parallelism: 4
  useSIMD: true
```

## Performance Characteristics

| Metric | Value | Notes |
|--------|-------|-------|
| Search complexity | O(log N) per query | HNSW greedy search |
| Insert complexity | O(log N) per vector | + efConstruction candidates |
| Memory per node | ~16 * 4 bytes = 64 bytes (edges) + 768 * 4 bytes = 3072 bytes (vector) | Plus overhead |
| Memory for 1M vectors | ~4 GB (vectors) + ~640 MB (edges) | Total ~5 GB |
| Build time for 1M | ~8 minutes | Single-threaded |
| Serialization size | ~3 GB for 1M | Binary format |
| Recovery time | ~10 seconds | mmap + lazy loading |

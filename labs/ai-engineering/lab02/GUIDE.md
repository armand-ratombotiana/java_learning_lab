# Vector Database Integration — Deep Dive Guide

## Core Concepts

Vector databases store and index high-dimensional embeddings for fast similarity search. Unlike traditional databases that use exact matching, they find "nearest neighbors" in vector space.

## Distance Metrics

### Cosine Similarity
- Measures the angle between vectors
- Range: [-1, 1] (1 = identical direction)
- Best for normalized text embeddings
- `1 - cosine_similarity` gives a distance metric

### Euclidean Distance (L2)
- Measures straight-line distance between vectors
- Range: [0, ∞) (0 = identical)
- Sensitive to vector magnitude
- Good for embeddings where magnitude matters

### Dot Product
- Unnormalized similarity measure
- Not a true distance metric
- Often used in GPU-optimized kernels

## Indexing Strategies

### Flat Index (Brute Force)
- Stores all vectors in a list
- O(n) search time, O(n) memory
- Exact results, 100% recall
- Good for: small datasets (< 100K vectors)

### IVF (Inverted File Index)
- Partitions space into cells (Voronoi diagram)
- Only searches nearest k cells during query
- O(log n) approximate search
- Trade-off: accuracy vs. speed (controlled by nprobe)

### HNSW (Hierarchical Navigable Small World)
- Multi-layer graph structure
- O(log n) search time
- State-of-the-art for high-dimensional data
- Higher memory usage than IVF

## Code Walkthrough

The demo implements:
1. `FlatIndex` — Exhaustive search with configurable distance
2. `IVFIndex` — Centroid-based partitioning with limited search
3. Both support pluggable `DistanceFunction` (cosine or Euclidean)

## Real-World Systems

- **Pinecone**: Managed, HNSW-based, auto-scaling
- **Weaviate**: Open-source, hybrid search, multi-tenancy
- **Qdrant**: Rust-based, custom HNSW, filtering support
- **Milvus**: GPU-accelerated, multiple index types
- **pgvector**: PostgreSQL extension, IVFFlat and HNSW

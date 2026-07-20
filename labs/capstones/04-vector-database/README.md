# Vector Database

A vector database implementation in Java supporting brute-force and HNSW (Hierarchical Navigable Small World) approximate nearest neighbor search with cosine, L2, and inner product similarity metrics, metadata filtering, and file-based persistence.

## Architecture Overview

```
┌──────────────┐    insert     ┌──────────────────┐    search     ┌──────────────┐
│   Client     │──────────────►│   Vector Store   │◄─────────────│   Client     │
└──────────────┘               │                  │              └──────────────┘
                               │  ┌────────────┐  │
                               │  │VectorIndex │  │  Brute-force (exact)
                               │  │ (brute     │  │
                               │  │  force)    │  │
                               │  └────────────┘  │
                               │  ┌────────────┐  │
                               │  │ HNSWGraph  │  │  Approximate (fast)
                               │  │ (ANN)      │  │
                               │  └────────────┘  │
                               │  ┌────────────┐  │
                               │  │ Persistence│  │  File-based serialization
                               │  └────────────┘  │
                               └──────────────────┘
```

## Features

- **VectorIndex**: Brute-force exact search with cosine/L2/inner product, metadata filtering, CRUD
- **HNSWGraph**: Multi-layer navigable small world graph for approximate search, select-neighbors with pruning
- **CosineSimilarity**: Cosine, normalized cosine, batch normalization, centroid calculation
- **VectorStore**: Combined index + HNSW + persistence layer with version tracking

## Similarity Metrics

| Metric | Formula | Use Case |
|--------|---------|----------|
| Cosine | A·B / (|A||B|) | Text/document similarity |
| L2 | -√(Σ(Aᵢ-Bᵢ)²) | Euclidean distance (negated) |
| Inner Product | Σ(Aᵢ×Bᵢ) | Magnitude-sensitive similarity |

## Usage

```java
var store = new VectorStore(Path.of("data"));
store.insert("doc1", new float[]{0.1f, 0.2f, 0.3f}, Map.of("type", "article"));
store.insert("doc2", new float[]{0.4f, 0.5f, 0.6f}, Map.of("type", "book"));

var results = store.search(new float[]{0.15f, 0.25f, 0.35f}, 5);
results = store.search(query, 5, Map.of("type", "article"), SearchMode.HNSW);

store.persist(); // Save to disk
```

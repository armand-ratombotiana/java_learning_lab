# Lab 02: Vector Database Integration

## Learning Objectives
- Understand embedding storage and retrieval mechanics
- Implement cosine similarity and Euclidean distance metrics
- Compare flat (brute-force) with approximate indexing (IVF, HNSW)
- Build a similarity search engine in-memory

## Concepts Covered
- **Embeddings**: Dense vector representations of data
- **Distance Metrics**: Cosine, Euclidean, Dot Product
- **Flat Indexing**: Brute-force exact nearest neighbor search
- **IVF (Inverted File Index)**: Partition-based approximate search
- **HNSW (Hierarchical Navigable Small World)**: Graph-based ANN

## Setup
```bash
cd lab02
javac src/com/aiengineering/lab02/VectorDatabaseDemo.java
java com.aiengineering.lab02.VectorDatabaseDemo
```

## Key Takeaways
- Flat index gives exact results but O(n) search time
- IVF trades accuracy for speed via partitioning
- Cosine similarity is preferred for normalized embeddings

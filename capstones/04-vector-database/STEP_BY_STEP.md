# Step by Step: Vector Database

## Inserting a Vector

1. Client sends PUT /collections/products/vectors with `{"vector": [0.1, 0.2, ...], "metadata": {"category": "electronics", "price": 99.99}}`
2. Vector is assigned a unique 64-bit ID (auto-increment based on WAL sequence)
3. Vector is normalized (L2) if using cosine distance
4. HNSW index assigns a level via `floor(-ln(random()) * mL)`
5. Entry point is the current top-level node (or first insertion sets it)
6. If level > current top level, new node becomes entry point (top layers have no neighbors yet)
7. For each layer from top down to level+1: just find nearest neighbor (ef=1)
8. For each layer from level down to 0: find efConstruction nearest neighbors, connect bidirectional
9. Vector data written to memory-mapped file, WAL entry appended
10. Metadata indexed (category -> set of IDs, price -> sorted set of IDs)

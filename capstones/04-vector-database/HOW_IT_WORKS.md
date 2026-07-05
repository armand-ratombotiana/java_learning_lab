# How It Works: Vector Database

1. Client inserts a vector with metadata (PUT /collections/{name}/vectors)
2. Vector is normalized (L2 normalization) and stored with an auto-generated ID
3. Vector is added to the HNSW graph: navigated from top layer, find nearest neighbors, connect
4. Metadata is stored in a separate inverted index for filtering
5. Client queries with a query vector (POST /collections/{name}/search)
6. Search traverses HNSW from top layer, greedily descending to find nearest neighbors
7. Optional metadata filter is applied (e.g., "category = electronics")
8. Top-K results are returned with similarity scores and metadata
9. Optional: Vector is deleted or updated (update = delete + re-insert)

# Interview: Vector Database

## Common Questions

### Q: Design a vector database for 1 billion vectors with 768 dimensions.
Use IVF + PQ for memory efficiency (compression to ~32 bytes/vector). Multi-layer HNSW per partition. Distributed sharding with consistent hashing. GPU acceleration for distance computation.

### Q: How does HNSW differ from other ANN algorithms?
HNSW offers better recall-latency trade-off than IVF. It's a graph-based approach with multi-layer navigation. IVF is simpler but requires more tuning (nprobe). PQ alone needs coarse quantizer.

### Q: How do you handle real-time vector updates in HNSW?
Insert: navigate graph, connect to efConstruction neighbors. Delete: mark as deleted, periodically compact. Update: delete + insert. Real-time inserts don't significantly degrade recall if efConstruction is sufficient.

### Q: How do you choose distance metrics?
Cosine for text embeddings (angle matters, not magnitude). L2 for learned embeddings with normalized outputs. Dot product for models trained with dot-product loss. IP for maximum inner product search.

### Q: How do you ensure crash recovery?
WAL for all mutations. Periodic checkpoints (serialize HNSW graph + vectors). On restart, replay WAL from last checkpoint. Use atomic rename for checkpoint consistency.

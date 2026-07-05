# Internals: Vector Database

## Core Components
- **HNSWIndex**: Main index structure with multi-layer graph, entry point, and search/insert operations
- **MetadataStore**: Inverted index for metadata filtering (equality, range, text match)
- **VectorStore**: Memory-mapped file for persistent vector storage
- **DistanceCalculator**: Configurable distance metric (cosine, L2, dot product, inner product)
- **IndexManager**: Handles concurrent reads/writes, checkpointing, recovery

## HNSW Implementation
- Parameters: M (max connections per layer) = 16, efConstruction = 200, efSearch = 50, mL = 1/ln(M)
- Layer assignment: level = floor(-ln(uniform(0,1)) * mL)
- Search algorithm: priority-queue-based greedy traversal with distance computations
- Insert: find entry point, search ef neighbors at each level, connect bidirectional

## Storage Format
- WAL (Write-Ahead Log) for crash recovery
- Periodic checkpointing to SSTable-like format
- Memory-mapped vector files (MappedByteBuffer)

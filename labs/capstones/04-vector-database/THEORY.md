# Vector Database - Theory

## Core Concepts

### 1. VectorIndex provides brute-force exact search with cosine, L2, and inner product similarity. Supports CRUD operations, metadata filtering, and configurable result limits.

VectorIndex provides brute-force exact search with cosine, L2, and inner product similarity. Supports CRUD operations, metadata filtering, and configurable result limits.

Key implementation details: the system uses concurrent data structures (ConcurrentHashMap, CopyOnWriteArrayList) for thread safety. Error handling follows fail-fast principles with IllegalArgumentException for validation and IllegalStateException for invalid operations. All components are designed for testability with clear interfaces and dependency injection.

### 2. HNSWGraph 

HNSWGraph implements Hierarchical Navigable Small World graphs for approximate nearest neighbor search. Uses multi-layer graph construction with select-neighbors and pruning for high recall.

Key implementation details: the system uses concurrent data structures (ConcurrentHashMap, CopyOnWriteArrayList) for thread safety. Error handling follows fail-fast principles with IllegalArgumentException for validation and IllegalStateException for invalid operations. All components are designed for testability with clear interfaces and dependency injection.

### 3. CosineSimilarity provides static methods for cosine computation, normalization, batch normalization, and centroid calculation. Handles edge cases like zero vectors.

CosineSimilarity provides static methods for cosine computation, normalization, batch normalization, and centroid calculation. Handles edge cases like zero vectors.

Key implementation details: the system uses concurrent data structures (ConcurrentHashMap, CopyOnWriteArrayList) for thread safety. Error handling follows fail-fast principles with IllegalArgumentException for validation and IllegalStateException for invalid operations. All components are designed for testability with clear interfaces and dependency injection.

### 4. VectorStore combines brute-force and HNSW indices with file-based persistence using Java serialization. Supports version tracking for consistency checks.

VectorStore combines brute-force and HNSW indices with file-based persistence using Java serialization. Supports version tracking for consistency checks.

Key implementation details: the system uses concurrent data structures (ConcurrentHashMap, CopyOnWriteArrayList) for thread safety. Error handling follows fail-fast principles with IllegalArgumentException for validation and IllegalStateException for invalid operations. All components are designed for testability with clear interfaces and dependency injection.

### 5. Metadata filtering allows constraining search results to vectors matching specific key-value pairs, enabling faceted search use cases.

Metadata filtering allows constraining search results to vectors matching specific key-value pairs, enabling faceted search use cases.

Key implementation details: the system uses concurrent data structures (ConcurrentHashMap, CopyOnWriteArrayList) for thread safety. Error handling follows fail-fast principles with IllegalArgumentException for validation and IllegalStateException for invalid operations. All components are designed for testability with clear interfaces and dependency injection.

## Design Principles

The architecture follows SOLID principles with single-responsibility classes, open-for-extension design, and dependency inversion. Thread safety is achieved through immutable records, atomic operations, and synchronized blocks where necessary. All components include comprehensive JUnit 5 test coverage with parameterized tests and edge case handling.

## Performance Characteristics

Operations are O(1) for hash-based lookups, O(log n) for tree-based structures, and O(n) for linear scans. Memory usage is proportional to the number of stored elements with per-entry overhead for indexing structures. Concurrent access patterns use lock striping and non-blocking algorithms where possible to minimize contention.


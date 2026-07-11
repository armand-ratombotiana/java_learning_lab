# Vector Database Implementation Guide

This guide outlines the step-by-step process to build the Vector Database Capstone in Java.

## 🛠️ Phase 1: Core Data Structures & Math
1. **Vector Representation**: Create a `DenseVector` class wrapping a `float[]`.
2. **Distance Metrics**: Implement a `DistanceFunction` interface with implementations for:
   - `CosineSimilarity` (Remember: Cosine distance is $1 - \text{CosineSimilarity}$).
   - `EuclideanDistance` (L2 Norm).
3. **Optimization**: (Optional but recommended) Use the Java Incubator Vector API to vectorize the math operations for SIMD execution.

## 🕸️ Phase 2: The HNSW Graph
1. **Node Structure**: Create an `HnswNode` class that holds:
   - An internal ID.
   - A reference to the `DenseVector`.
   - An array of Lists representing connections at each layer.
2. **Graph Structure**: Create the `HnswIndex` class.
   - Store nodes in a `ConcurrentHashMap<Integer, HnswNode>`.
   - Track the current `entryPointNodeId` and `maxLayer`.
3. **Search Algorithm**: Implement the greedy search algorithm to traverse the graph layer by layer.
4. **Insertion Algorithm**: Implement the probabilistic layer assignment and connection pruning logic.

## 💾 Phase 3: Storage & Durability
1. **Write-Ahead Log (WAL)**:
   - Every insert command must be serialized (e.g., using Protocol Buffers or JSON) and appended to a `wal.log` file using a `FileChannel` before returning success to the user.
2. **Crash Recovery**:
   - On startup, the database must read `wal.log` line by line and re-insert the vectors into the in-memory HNSW graph.

## 🌐 Phase 4: API & Concurrency
1. **ReadWriteLocks**: The HNSW graph is highly concurrent. Use `ReentrantReadWriteLock` to allow multiple parallel searches while safely handling insertions.
2. **gRPC Server**: Define a `.proto` file with two RPCs:
   - `rpc Insert(InsertRequest) returns (InsertResponse)`
   - `rpc Search(SearchRequest) returns (SearchResponse)`
3. **Spring Boot Integration**: Wrap the core engine in a Spring Boot application to provide a REST API alternative and manage configuration.

## 🧪 Phase 5: Benchmarking
1. **Dataset**: Download the SIFT1M dataset (1 million 128-dimensional vectors).
2. **JMH**: Write JMH benchmarks to measure:
   - Insertion throughput (vectors per second).
   - Query latency (milliseconds per query).
   - Recall (percentage of true nearest neighbors successfully found compared to a brute-force search). Target: >95% recall at <5ms latency.
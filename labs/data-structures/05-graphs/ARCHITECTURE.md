# Architecture: Graphs in System Design

## Graph Database Architecture

```
Application
    ↓
Graph Query (Cypher/Gremlin)
    ↓
Graph Engine (traversal, pattern matching)
    ↓
Storage Layer (adjacency lists, index-free adjacency)
    ↓
Disk (B-tree backed node/edge stores)
```

Neo4j uses **index-free adjacency**: each node stores pointers to its neighbors directly (like linked list), enabling O(1) edge traversal regardless of graph size.

## Distributed Graph Processing

### Bulk Synchronous Parallel (BSP)

```
Superstep 0: initialize all vertices
    ↓
Superstep 1: vertices process messages, send new messages
    ↓
Superstep 2: ... repeat until convergence
    ↓
Done
```

Apache Giraph and Spark GraphX use this model (Pregel paper, 2010).

### PageRank Architecture

```
Input: Web graph (billions of pages)
Map: emit (page, out-link) pairs
Reduce: aggregate in-links, compute rank
Iterate until convergence
```

## Graph Partitioning

Large graphs must be partitioned across machines:
- **Vertex-cut**: each vertex stored on one machine, edges may cross machines
- **Edge-cut**: each edge stored on one machine, vertices may be replicated
- Goal: minimize cross-machine communication

## Common Architectural Patterns

### Recommendation System

```
User-Item Graph (bipartite)
Users ──rated──→ Items
Users ──bought──→ Items
Items ──similar──→ Items (co-purchase)
```

### Dependency Resolution

```
Component Dependency DAG
    ┌── WebApp ──┐
    ↓            ↓
  AuthLib     DatabaseLib
    ↓            ↓
  CryptoLib   ConnectionPool
```

### Social Network

```
Friend Graph → Feed Generation → Ranking → Notification
```

## Java Ecosystem

- **JGraphT**: comprehensive graph algorithm library
- **Apache TinkerPop**: graph computing framework (Gremlin query language)
- **Neo4j Java Driver**: connect to Neo4j graph database
- **Apache Spark GraphX**: distributed graph processing on Spark

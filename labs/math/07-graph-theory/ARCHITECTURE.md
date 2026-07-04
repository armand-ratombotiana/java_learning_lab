# Architecture of Graph Theory

## Graph Processing Systems

```
Single-node
├── JGraphT        — pure Java graph library
├── Guava Graph    — Google's common.graph API
└── Neo4j          — graph database (Cypher query language)

Distributed
├── Apache Giraph  — Pregel-like (vertex-centric)
├── GraphX (Spark) — RDD-based graph processing
└── Flink Gelly    — stream-based graph processing
```

## Graph Query Languages

- **SPARQL**: RDF graph queries
- **Cypher**: property graph queries (Neo4j)
- **Gremlin**: graph traversal language (Apache TinkerPop)
- **GraphQL**: API query language (inspired by graph structure)

## Software Architecture as Graph

```
Module Dependency Graph (DG)
  └─ Module A depends on Module B
     └─ Cycle detection prevents circular deps

Call Graph
  └─ Method A calls Method B
     └─ Dead code elimination

Inheritance Graph
  └─ Class A extends Class B
     └─ Diamond problem detection
```

## Graph Database Schema (Property Graph)

```
(:Person)-[:FRIENDS_WITH]->(:Person)
(:Person)-[:WORKS_AT]->(:Company)
(:Company)-[:LOCATED_IN]->(:City)
```

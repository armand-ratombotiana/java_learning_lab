# Internals: Multi-Model Database Engines

## ArangoDB Internals

| Component | Role |
|---|---|
| **Storage Engine** | RocksDB-based (LSM-tree) |
| **Document Store** | JSON documents, indexed by primary key |
| **Graph Store** | Edge collections, adjacency list in document |
| **Key-Value Store** | Optimized KV lookup with RocksDB |
| **AQL** | Unified query language (SQL-like for all models) |
| **Foxx** | In-DB microservices (JavaScript) |

## Cosmos DB Internals

| Component | Role |
|---|---|
| **Resource Governor** | Partition management, throughput (RU/s) |
| **Consistency Levels** | Strong, Bounded Staleness, Session, Consistent Prefix, Eventual |
| **API Layer** | SQL, MongoDB, Cassandra, Gremlin, Table APIs |
| **Replication** | Multi-master, global distribution |
| **Storage** | SSD-backed B-tree on local SSDs per partition |

## Polyglot Integration Patterns

```
2PC / XA ── RDBMS + RDBMS (same vendor, coordinated)
Saga     ── Orchestrated sequence of local transactions
CDC      ── Debezium → Kafka → downstream DBs
Outbox   ── Write to outbox table in RDBMS, CDC picks up
Dual Write ── Write to multiple DBs in application code (risk)
```

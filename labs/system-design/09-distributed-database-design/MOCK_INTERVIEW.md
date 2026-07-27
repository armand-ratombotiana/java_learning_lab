# Mock Interview: Distributed Database Design

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Database Infrastructure Engineer Interviewer  
**Candidate Level**: Senior Staff Engineer (L6)  
**Problem**: Design a distributed document database inspired by MongoDB/Couchbase.

---

## Transcript

**Interviewer**: "Design a distributed document database that supports: JSON documents, indexing, secondary indexes, acid transactions within a document, and horizontal scaling."

**Candidate**: "I'll design a system with three layers: 1) A routing layer that handles query routing and shard mapping, 2) A shard layer that stores and processes data, 3) A replication layer for durability and high availability."

**Interviewer**: "Start with the routing layer."

**Candidate**: "The router is stateless — it maintains a mapping of key ranges to shards using consistent hashing with virtual nodes. When a request comes in, it hashes the document's primary key, looks up which shard is responsible, and routes the request. The router also handles connection pooling and query parsing."

**Interviewer**: "How do you store documents within a shard?"

**Candidate**: "Each shard uses an LSM-tree based storage engine (similar to RocksDB or WiredTiger). Documents are stored as key-value pairs (key = document_id, value = serialized JSON). Writes go to a MemTable (in-memory sorted structure), then flushed to immutable SSTable files. Reads check MemTable first, then SSTable files from newest to oldest."

**Interviewer**: "How do you handle secondary indexes?"

**Candidate**: "Two approaches: 1) Local indexes — each shard maintains its own secondary indexes for its documents. Querying by secondary index requires a scatter-gather across all shards. 2) Global indexes — a separate index service that stores the secondary index across a different sharding scheme. I'd start with local indexes (simplicity) and move to global indexes when scatter-gather becomes too expensive."

**Interviewer**: "How do you handle replication and failover?"

**Candidate**: "Each shard has a replica set: 1 primary + 2 replicas (RAFT consensus for leader election and log replication). Writes go to the primary, which replicates to followers synchronously. If the primary fails, followers elect a new primary. Reads can go to any replica (eventual consistency) or only the primary (strong consistency)."

**Interviewer**: "How does the system handle schema changes?"

**Candidate**: "Since it's schemaless, no explicit schema migration needed. But for index management: CREATE INDEX command triggers background index building. The indexer scans documents, extracts the indexed field, and populates the index. Reads during index building still work — they just don't use the new index until it's ready."

**Interviewer**: "How do you handle large documents (10MB+)?"

**Candidate**: "Documents larger than 16MB (MongoDB's limit) are split into multiple chunks. The document's metadata (id, version, shard key) stays as the primary record. The content is stored in a separate collection of chunks, linked by a content_id. The application transparently reassembles the document on read."

---

## Key Takeaways

- **Three-layer architecture**: Router → Shard → Replication
- **LSM-tree storage**: MemTable + SSTables for write-optimized storage
- **Consistent hashing**: Virtual nodes for even distribution
- **Local vs global indexes**: Trade-off between write cost and query efficiency
- **RAFT replication**: Strong consistency within a shard
- **Document splitting**: Transparent chunking for large documents

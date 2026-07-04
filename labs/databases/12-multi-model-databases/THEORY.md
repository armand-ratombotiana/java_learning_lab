# Theory: Multi-Model Databases & Polyglot Persistence

## Data Models

| Model | Strengths | Weaknesses | Best For |
|---|---|---|---|
| Relational | ACID, joins, integrity enforcement | Schema rigidity, scaling complexity | Transactions, financial data |
| Document | Flexibility, nested data, fast dev | Weak joins, no schema enforcement | Catalogs, CMS, user profiles |
| Key-Value | Extreme speed, simple model | No query capability beyond key | Caching, session store, counters |
| Graph | Relationship traversal, pattern matching | Complex for non-graph data | Social networks, recommendations |
| Columnar | Compression, analytical queries | Write complexity | Time-series, analytics |
| Vector | Similarity search, ML embeddings | Specialized use | RAG, recommendations, search |

## CAP Theorem in Polyglot
Different databases prioritize different CAP properties:
- **CP** (Consistency + Partition tolerance): Relational DBs, MongoDB
- **AP** (Availability + Partition tolerance): Cassandra, CouchDB
- **CA** (Consistency + Availability): Not possible in distributed systems

## Consistency Strategies
- **Strong Consistency**: All reads see latest write (RDBMS)
- **Eventual Consistency**: Reads may see stale data, converges (Cassandra, DynamoDB)
- **Read-Your-Writes**: Session-level consistency

## Multi-Model vs Polyglot
- **Multi-Model**: Single database with multiple APIs (ArangoDB, OrientDB, Cosmos DB)
- **Polyglot**: Multiple databases, each specialized for a data type

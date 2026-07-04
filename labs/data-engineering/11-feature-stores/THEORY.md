# Feature Store Theory

## Core Components
- **Online Store**: Low-latency serving (Redis, Cassandra)
- **Offline Store**: Historical features (S3, Delta Lake)
- **Feature Registry**: Catalog of features, metadata, lineage

## Key Problems Solved
1. Feature reuse (no duplicated engineering)
2. Training/serving consistency
3. Point-in-time correctness
4. Feature discovery
5. Governance and lineage

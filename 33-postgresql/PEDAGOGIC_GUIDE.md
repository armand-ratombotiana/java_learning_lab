# Pedagogic Guide - PostgreSQL

## Learning Path

### Phase 1: Basics & Schema Design
1. SQL fundamentals (SELECT, INSERT, UPDATE, DELETE)
2. Table constraints and indexes
3. Foreign key relationships
4. Schema migration strategies

### Phase 2: Advanced Data Types
1. JSONB for semi-structured data
2. ARRAY types for multi-value columns
3. Range types for intervals
4. Composite types and domains

### Phase 3: Query Optimization
1. EXPLAIN ANALYZE interpretation
2. Index types: B-tree, Hash, GIN, GiST, BRIN
3. Partial indexes and expressions
4. Statistics and query planner

### Phase 4: Advanced SQL
1. Window functions for analytics
2. CTEs (Common Table Expressions)
3. Recursive queries for hierarchies
4. Lateral joins and correlated subqueries

### Phase 5: Operations & Maintenance
1. VACUUM and autovacuum tuning
2. Connection pooling with PgBouncer
3. Point-in-time recovery
4. Partitioning strategies

## Interview Topics
- ACID properties and transaction isolation
- Index selection strategies
- Query optimization techniques
- When to denormalize vs. normalize
- PostgreSQL vs. other databases
- Partitioning and sharding approaches

## Key Functions

| Function | Purpose |
|----------|---------|
| `jsonb_extract_path` | Navigate JSON |
| `array_agg` | Aggregate to array |
| `ts_rank` | Text search relevance |
| `row_to_json` | Convert row to JSON |
| `generate_series` | Create sequences |

## Next Steps
- Explore PostGIS for geospatial data
- Learn pg_partman for partitioning
- Study materialized views for performance
- Explore logical replication
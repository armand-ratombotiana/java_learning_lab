# Pedagogic Guide - Elasticsearch

## Learning Path

### Phase 1: Core Concepts
1. Index, document, shard concepts
2. JSON document structure
3. Mapping and data types
4. CRUD operations via API

### Phase 2: Query DSL
1. Match and term queries
2. Bool queries with must/should/filter
3. Range and exists queries
4. Wildcard and regexp queries

### Phase 3: Search Features
1. Full-text search and analyzers
2. Relevance scoring and boosting
3. Highlighting results
4. Sorting and pagination

### Phase 4: Aggregations
1. Metric aggregations (avg, sum, stats)
2. Bucket aggregations (terms, range, histogram)
3. Nested and pipeline aggregations
4. Faceted search implementation

### Phase 5: Operations
1. Index lifecycle management
2. Shard allocation and rebalancing
3. Performance tuning
4. Monitoring with _cluster/stats

## Interview Topics

| Topic | Question |
|-------|----------|
| Architecture | How does ES handle distributed indexing? |
| Scaling | When to add shards vs replicas? |
| Consistency | What happens on split-brain? |
| Search | How is relevance scoring calculated? |
| Performance | How to optimize slow queries? |

## Query Types

| Type | Use Case |
|------|----------|
| match | Full-text search |
| term | Exact value filter |
| range | Numeric/date filters |
| bool | Combine multiple conditions |
| function_score | Custom scoring |

## Next Steps
- Explore Elasticsearch SQL for SQL-like queries
- Learn Elasticsearch安全 for security features
- Study cross-cluster search for federated data
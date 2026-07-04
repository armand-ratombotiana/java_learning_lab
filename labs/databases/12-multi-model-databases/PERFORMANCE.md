# Performance: Multi-Model & Polyglot

## Performance by Data Model

| Operation | PostgreSQL | MongoDB | Redis | Neo4j | Elasticsearch |
|---|---|---|---|---|---|
| Single row by PK | 1-5ms | 1-5ms | <0.1ms | 2-10ms | 2-10ms |
| Range query (1000 rows) | 10-50ms | 20-100ms | N/A | 50-200ms | 5-20ms |
| Graph traversal (3 hops) | 100-500ms (JOINs) | N/A | N/A | 1-10ms | N/A |
| Full-text search | 50-200ms | 20-100ms | N/A | N/A | 2-50ms |
| Aggregation | 10-100ms | 10-100ms | N/A | N/A | 5-50ms |

## Query Optimization Per Model

### Relational: Indexing
```sql
CREATE INDEX CONCURRENTLY idx_orders_user_id ON orders(user_id);
```

### Document: Indexing
```javascript
db.products.createIndex({ "category": 1, "price": -1 });
```

### Graph: Relationship Indexing
```cypher
CREATE INDEX FOR (p:Person) ON (p.name);
```

### Search: Field Mappings
```json
{
  "mappings": {
    "properties": {
      "title": { "type": "text", "analyzer": "english" },
      "price": { "type": "float" }
    }
  }
}
```

## Latency Budget (Polyglot Request)
```
Total API response time = 200ms
  ├─ RDBMS query (order):     10ms
  ├─ Redis (cache hit):        1ms
  ├─ MongoDB (product):       15ms
  └─ Elasticsearch (search):  20ms
  └─ Network overhead:         5ms
  Total: 51ms ✓
```

# Pedagogic Guide - MongoDB

## Learning Path

### Phase 1: Document Fundamentals
1. Understand BSON document structure
2. Learn basic CRUD operations
3. Explore collection operations

### Phase 2: Querying & Filtering
1. Query operators (`$eq`, `$gt`, `$in`, `$regex`)
2. Projection to limit fields
3. Array queries and `$elemMatch`

### Phase 3: Data Modeling
1. Embedding vs. referencing decisions
2. One-to-many and many-to-many patterns
3. Schema design anti-patterns to avoid

### Phase 4: Aggregation Framework
1. Pipeline stages and syntax
2. Grouping, sorting, limiting
3. Working with arrays ($unwind, $filter)

### Phase 5: Performance Optimization
1. Index types: single, compound, text, geospatial
2. Query plan analysis with explain()
3. Write concern and read preferences

## Key Concepts

| Concept | Description |
|---------|-------------|
| Document | BSON structure with dynamic schema |
| Collection | Group of related documents |
| Database | Container for collections |
| `_id` | Auto-generated unique identifier |
| Cursor | Result set iteration |

## Common Operators

| Operator | Purpose |
|----------|---------|
| `$match` | Filter documents |
| `$group` | Aggregate by key |
| `$sort` | Order results |
| `$limit` | Restrict count |
| `$project` | Reshape documents |
| `$lookup` | JOIN collections |
| `$unwind` | Deconstruct arrays |

## Interview Topics
- When to choose MongoDB over RDBMS
- Document modeling trade-offs
- Sharding and horizontal scaling
- Transaction support in MongoDB
- Change Streams for real-time data

## Next Steps
- Explore MongoDB Atlas for cloud deployment
- Learn replica sets for high availability
- Study time-series collections for IoT data
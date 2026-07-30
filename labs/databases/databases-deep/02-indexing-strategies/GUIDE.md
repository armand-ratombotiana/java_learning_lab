# Indexing Strategies — Deep Dive Guide

## B-Tree (default)

Structure: balanced tree with fanout ~300-500. Leaf pages contain `(key, tid)` pairs.

- Supports `=`, `<`, `<=`, `>`, `>=`, `BETWEEN`, `IN`, `LIKE 'foo%'` (prefix)
- `ORDER BY` can be satisfied without sorting if index order matches

## Hash Indexes

- Only supports `=` comparisons
- Hides from `ANALYZE` stats in older PostgreSQL
- Useful for UUID lookups, natural keys

## Bitmap Indexes

- Each distinct value has a bitmap of row positions
- Efficient for low-cardinality (e.g., gender, status)
- Oracle specialty; PostgreSQL uses Bitmap Scan on B-trees

## GiST / GIN

- **GiST**: ranges, geometric, full-text (tsvector)
- **GIN**: inverted indexes for arrays, JSONB, full-text
- **SP-GiST**: space-partitioned (quad-tree, k-d tree)

## Partial Indexes

```sql
CREATE INDEX idx_active_orders ON orders(total_amount) WHERE status = 'ACTIVE';
```

Only rows matching the predicate are indexed — smaller, faster.

## Composite Indexes — Column Order

- Place equality columns first, range columns last
- `(a, b)` can satisfy `WHERE a = 1 AND b > 5` but NOT `WHERE b > 5`

## Covering Indexes

```sql
CREATE INDEX idx_covering ON orders(customer_id) INCLUDE (total_amount, status);
```

If all required columns are in index + INCLUDE, PostgreSQL can do an **Index-Only Scan** (visibility map permitting).
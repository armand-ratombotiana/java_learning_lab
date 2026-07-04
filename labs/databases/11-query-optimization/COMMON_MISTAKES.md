# Common Mistakes: Query Optimization

## Missing Index on Foreign Key
```sql
-- BAD: No index on user_id in orders table
SELECT * FROM orders WHERE user_id = 42;
-- Causes Seq Scan on orders every time
```
**Fix**: `CREATE INDEX idx_orders_user_id ON orders(user_id);`

## Using SELECT * in Production
```sql
-- BAD: Retrieves all columns, prevents index-only scans
SELECT * FROM users WHERE email = 'test@example.com';
-- GOOD: Select only needed columns
SELECT id, name FROM users WHERE email = 'test@example.com';
```

## Ignoring Stale Statistics
Outdated statistics cause the optimizer to choose bad plans.
**Fix**: `ANALYZE table_name;` or set `autovacuum` properly.

## Premature Optimization
Adding indexes before measuring actual slow queries creates unnecessary maintenance overhead.
**Fix**: Identify slow queries first, then add targeted indexes.

## Over-Indexing
Each index slows INSERT/UPDATE/DELETE operations.
**Fix**: Remove unused indexes.

## Not Using Covering Indexes
```sql
-- Without covering index: index scan + heap fetch
-- With covering:
CREATE INDEX idx_users_email ON users(email) INCLUDE (name);
-- Now: index-only scan (no heap visit)
```

# Common Mistakes: PostgreSQL

## 1. Not Setting `shared_buffers`
```conf
# WRONG: default 128MB
shared_buffers = 128MB

# RIGHT: ~25% of RAM
shared_buffers = 4GB
```

## 2. Using `SELECT *` with JSONB
```sql
-- WRONG: expensive full-row retrieval
SELECT * FROM events WHERE data @> '{"type":"login"}';

-- RIGHT: only fetch needed columns
SELECT id, data->>'user_id' FROM events WHERE data @> '{"type":"login"}';
```

## 3. Missing GIN Index on JSONB
```java
// WRONG: sequential scan on JSONB queries
@Column(columnDefinition = "jsonb")
private Map<String, Object> data;

// RIGHT: create GIN index explicitly
// CREATE INDEX idx_table_data ON table USING GIN (data);
```

## 4. Using `Integer` for Primary Keys (overflow risk)
```sql
-- WRONG: INTEGER max ~2.1B
CREATE TABLE logs (id INTEGER SERIAL PRIMARY KEY);

-- RIGHT: BIGSERIAL for large tables
CREATE TABLE logs (id BIGSERIAL PRIMARY KEY);
```

## 5. Ignoring `VACUUM` and Bloat
Large tables with frequent updates accumulate dead tuples.
```sql
-- Check bloat
SELECT schemaname, tablename, n_dead_tup, n_live_tup
FROM pg_stat_user_tables
WHERE n_dead_tup > 10000;
```

## 6. Not Using Connection Pooling
```java
// WRONG: new connection per request
Connection conn = DriverManager.getConnection(url, user, pass);

// RIGHT: use HikariCP connection pool
HikariDataSource ds = new HikariDataSource(config);
```

## 7. Wrong Sequence Allocation Size
```java
// WRONG: allocationSize=1 causes sequence lock contention
@SequenceGenerator(name = "seq", sequenceName = "seq", allocationSize = 1)

// RIGHT: batch sequence values
@SequenceGenerator(name = "seq", sequenceName = "seq", allocationSize = 50)
```

## 8. Not Using `CONCURRENTLY` for Index Creation on Live Tables
```sql
-- WRONG: blocks writes during index creation
CREATE INDEX idx_name ON table (col);

-- RIGHT: non-blocking index creation
CREATE INDEX CONCURRENTLY idx_name ON table (col);
```

## 9. Case-Folding Issues with Quoted Identifiers
```sql
-- WRONG: "MyTable" creates case-sensitive identifier
CREATE TABLE "MyTable" (...);
SELECT * FROM MyTable; -- fails, should be "MyTable"

-- RIGHT: use lowercase unquoted identifiers
CREATE TABLE my_table (...);
```

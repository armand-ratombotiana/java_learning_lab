# Quiz: PostgreSQL

## Question 1
Which PostgreSQL index type is best for JSONB querying?
- A) B-tree
- B) Hash
- C) GIN
- D) BRIN

**Answer: C) GIN**

## Question 2
What is the default transaction isolation level in PostgreSQL?
- A) READ UNCOMMITTED
- B) READ COMMITTED
- C) REPEATABLE READ
- D) SERIALIZABLE

**Answer: B) READ COMMITTED**

## Question 3
Which MVCC tuple header field tracks the creating transaction?
- A) t_ctid
- B) xmin
- C) xmax
- D) t_cid

**Answer: B) xmin**

## Question 4
What is the main advantage of `CREATE INDEX CONCURRENTLY`?
- A) Faster index creation
- B) Smaller index size
- C) Non-blocking for writes
- D) Supports all index types

**Answer: C) Non-blocking for writes**

## Question 5
Which of the following is NOT a valid PostgreSQL index type?
- A) GiST
- B) B-tree
- C) Hash
- D) R-tree

**Answer: D) R-tree**

## Question 6
What does the `@>` operator do with JSONB?
- A) Checks if key exists
- B) Checks if left contains right
- C) Extracts JSON path
- D) Concatenates JSON objects

**Answer: B) Checks if left contains right**

## Question 7
Which HikariCP property detects connection leaks?
- A) `connectionTimeout`
- B) `leakDetectionThreshold`
- C) `idleTimeout`
- D) `maxLifetime`

**Answer: B) leakDetectionThreshold**

## Question 8
What is the purpose of the TOAST table?
- A) Store table metadata
- B) Compress and store large values out-of-line
- C) Track transaction commit status
- D) Manage replication slots

**Answer: B) Compress and store large values out-of-line**

## Question 9
Which function creates a ranked full-text search query?
- A) `to_tsvector()`
- B) `to_tsquery()`
- C) `ts_rank()`
- D) `plainto_tsquery()`

**Answer: C) ts_rank()**

## Question 10
What does a BRIN index excel at?
- A) Equality lookups on random data
- B) Range scans on naturally ordered data
- C) Full-text search
- D) Geospatial queries

**Answer: B) Range scans on naturally ordered data**

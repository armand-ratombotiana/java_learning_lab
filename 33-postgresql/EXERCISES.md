# Exercises - PostgreSQL

## Exercise 1: Advanced Data Types
Explore PostgreSQL's rich data type support:

1. Create an entity using JSONB for flexible metadata
2. Store multiple tags using PostgreSQL ARRAY
3. Use UUID as primary key instead of BIGINT
4. Query JSONB fields with `->` and `->>` operators

## Exercise 2: Window Functions
Implement analytics using window functions:

1. Calculate running total of orders per customer
2. Rank products by sales within each category
3. Get month-over-month growth percentage
4. Implement running average using `OVER` clause

## Exercise 3: Recursive CTEs
Solve hierarchical data problems:

1. Model an organizational chart with self-referencing FK
2. Write recursive CTE to find all subordinates of a manager
3. Calculate depth level in hierarchy
4. Implement path enumeration (e.g., "/CEO/VP/Manager/")

## Exercise 4: Full-Text Search
Implement search functionality:

1. Create GIN index on text column
2. Configure tsvector column with trigger for auto-update
3. Write search query with `ts_rank` for relevance
4. Add prefix matching and stemming support

## Exercise 5: Bulk Operations
Handle large data volumes efficiently:

1. Use COPY for bulk insert (100k+ rows)
2. Implement batch insert with RETURNING clause
3. Use `INSERT ... ON CONFLICT` for upserts
4. Benchmark batch sizes vs. transaction size

## Bonus Challenge
Implement a multi-tenant SaaS pattern using row-level security (RLS). Each tenant should only see their own data without application-level filtering.
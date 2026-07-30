# Indexing Strategies — Interview Questions

## Beginner
1. How does a B-tree index physically store data?
2. What is the difference between clustered and non-clustered indexes?
3. When would a hash index be preferred over a B-tree?

## Intermediate
4. What is an index-only scan and what conditions must be met?
5. How do you choose the column order in a composite index?
6. What is a partial index and when is it beneficial?

## Advanced
7. How does PostgreSQL's visibility map enable index-only scans without checking every tuple?
8. Explain how GIN inverted indexes work internally for JSONB queries.
9. How would you index a table with 500M rows for mixed point lookups and range scans?

## System Design
10. Design an auto-indexing advisor that monitors query patterns and recommends indexes with minimal overhead.
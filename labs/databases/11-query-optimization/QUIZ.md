# Quiz: Query Optimization

## Question 1
Which scan type is optimal for retrieving <5% of rows from a large table?
- A) Seq Scan
- B) Index Scan
- C) Bitmap Heap Scan
- D) Parallel Seq Scan

<details><summary>Answer</summary>B</details>

## Question 2
What does the N+1 problem refer to?
- A) One query followed by N additional queries for each parent row
- B) N queries that run in parallel
- C) A query that takes N+1 seconds
- D) An index with N+1 columns

<details><summary>Answer</summary>A</details>

## Question 3
Which JPA annotation helps prevent N+1 queries?
- A) `@BatchSize`
- B) `@EntityGraph`
- C) `@Fetch`
- D) All of the above

<details><summary>Answer</summary>D</details>

## Question 4
True or False: Adding more indexes always improves query performance.

<details><summary>Answer</summary>False – indexes slow down INSERT/UPDATE/DELETE and can cause the optimizer to choose bad plans.</details>

## Question 5
What does `EXPLAIN (ANALYZE, BUFFERS)` show that plain `EXPLAIN` doesn't?
- A) Estimated query cost
- B) Actual execution time and buffer usage
- C) Index recommendations
- D) Query syntax errors

<details><summary>Answer</summary>B</details>

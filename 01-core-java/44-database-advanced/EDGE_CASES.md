# Module 44: Advanced Database Concepts - Edge Cases & Pitfalls

---

## Pitfall 1: Over-Indexing

### ❌ Wrong
Adding an index to every single column in a database table to "make all queries faster." 

### ✅ Correct
Every index requires disk space and slows down `INSERT`, `UPDATE`, and `DELETE` operations because the database must update the B-Tree structure for every change. Only index columns that are frequently used in `WHERE` clauses, `JOIN` conditions, or `ORDER BY` clauses.

---

## Pitfall 2: Using the Wrong Isolation Level

### ❌ Wrong
Using `READ UNCOMMITTED` for a financial transaction system to improve speed, resulting in users seeing money that was rolled back by a failed transaction (Dirty Read).

### ✅ Correct
Match the isolation level to the business requirements. Most databases default to `READ COMMITTED` or `REPEATABLE READ`. Use `SERIALIZABLE` only when absolutely necessary for absolute correctness, as it heavily restricts concurrent access and lowers throughput.

---

## Pitfall 3: N+1 Problem in ORMs

### ❌ Wrong
Using Hibernate/JPA to load a list of 100 Authors, and then looping through the list to load their Books. This executes 1 query for the Authors, and 100 queries for the Books (101 queries total), crippling performance.

### ✅ Correct
Use JOIN FETCH or Entity Graphs in your ORM queries to fetch the Authors and their associated Books in a single, optimized SQL query.
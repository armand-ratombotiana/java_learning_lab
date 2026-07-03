# Module 44: Advanced Database Concepts - Quizzes

---

## Q1: ACID Properties
Which ACID property ensures that a transaction is all-or-nothing (either all changes are applied, or none are)?

A) Consistency
B) Atomicity
C) Isolation
D) Durability

**Answer**: B
**Explanation**: Atomicity guarantees that a transaction is treated as a single, indivisible unit of work.

---

## Q2: Indexing
What is the primary trade-off of adding non-clustered indexes to a database table?

A) They speed up reads but slow down writes (`INSERT`/`UPDATE`/`DELETE`).
B) They speed up writes but slow down reads.
C) They require rewriting the database schema in NoSQL.
D) They reduce the amount of disk space used.

**Answer**: A
**Explanation**: Indexes provide fast lookup paths for `SELECT` queries, but every time data is modified, all relevant indexes must also be updated, adding overhead to write operations.

---

## Q3: Isolation Levels
Which concurrency anomaly is characterized by reading data from a transaction that has not yet been committed?

A) Phantom Read
B) Non-Repeatable Read
C) Dirty Read
D) Lost Update

**Answer**: C
**Explanation**: A "Dirty Read" occurs when transaction A reads data written by transaction B, but transaction B subsequently rolls back its changes, meaning transaction A read data that never technically existed.
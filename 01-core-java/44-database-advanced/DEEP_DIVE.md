# Module 44: Advanced Database Concepts - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-43 (especially Database Access with JDBC and Spring Data JPA)  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [ACID Properties vs BASE](#acid-base)
2. [Isolation Levels & Concurrency Anomalies](#isolation)
3. [Indexing Strategies and B-Trees](#indexing)
4. [NoSQL Databases (Document, Key-Value, Graph)](#nosql)
5. [Database Normalization & Denormalization](#normalization)

---

## 1. ACID Properties vs BASE <a name="acid-base"></a>
- **ACID (Relational DBs)**: Atomicity, Consistency, Isolation, Durability. Ensures strict transaction validity even in the event of errors, power failures, etc.
- **BASE (NoSQL DBs)**: Basically Available, Soft state, Eventual consistency. Prioritizes availability and partition tolerance (from the CAP theorem) over immediate consistency.

---

## 2. Isolation Levels & Concurrency Anomalies <a name="isolation"></a>
Concurrency anomalies occur when multiple transactions execute simultaneously:
- **Dirty Read**: Reading uncommitted data from another transaction.
- **Non-Repeatable Read**: Re-reading data and getting a different result because another transaction updated it.
- **Phantom Read**: Re-running a query and getting new rows because another transaction inserted them.

Isolation Levels (from weakest to strongest):
1. **Read Uncommitted**: Suffers from all anomalies.
2. **Read Committed**: Prevents Dirty Reads.
3. **Repeatable Read**: Prevents Dirty and Non-Repeatable Reads.
4. **Serializable**: Prevents all anomalies (uses strict locking).

---

## 3. Indexing Strategies and B-Trees <a name="indexing"></a>
Indexes drastically speed up `SELECT` queries by providing a fast lookup mechanism (like a book's index), usually implemented as B-Trees or B+Trees.
- **Clustered Index**: Determines the physical order of data in the table (usually the Primary Key). One per table.
- **Non-Clustered Index**: A separate structure pointing to the physical data. Multiple per table.

*Trade-off*: Indexes speed up reads but slow down writes (`INSERT`/`UPDATE`/`DELETE`) because the index must be updated.

---

## 4. NoSQL Databases (Document, Key-Value, Graph) <a name="nosql"></a>
When relational schemas become too rigid or scaling horizontally becomes difficult:
- **Document (MongoDB)**: Stores JSON-like documents. Great for semi-structured data.
- **Key-Value (Redis, DynamoDB)**: Extremely fast lookups by key. Ideal for caching and session management.
- **Column-Family (Cassandra)**: Optimized for huge volumes of write-heavy data across distributed nodes.
- **Graph (Neo4j)**: Optimized for traversing relationships (nodes and edges) like social networks or recommendation engines.

---

## 5. Database Normalization & Denormalization <a name="normalization"></a>
- **Normalization**: Organizing data to reduce redundancy and improve data integrity (1NF, 2NF, 3NF). Requires JOINs to reconstruct data.
- **Denormalization**: Intentionally adding redundant data to speed up read performance by avoiding expensive JOINs (common in NoSQL and data warehousing).
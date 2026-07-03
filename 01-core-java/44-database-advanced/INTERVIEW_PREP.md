# Module 44: Advanced Database Concepts - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the CAP Theorem, and how does it apply to NoSQL databases?
**Answer**:
The CAP Theorem states that a distributed data store can only guarantee two out of three properties simultaneously:
- **Consistency (C)**: Every read receives the most recent write.
- **Availability (A)**: Every request receives a non-error response.
- **Partition Tolerance (P)**: The system continues operating despite network failures between nodes.
Since network partitions (P) are unavoidable in distributed systems, the choice is always between CP and AP.
- **CP Systems** (e.g., MongoDB, HBase): If a network partition occurs, they shut down the partitioned nodes to prevent returning stale data (sacrificing Availability to ensure Consistency).
- **AP Systems** (e.g., Cassandra, DynamoDB): If a network partition occurs, they continue serving read/write requests on all nodes, meaning some users might temporarily see older data until the network heals (sacrificing immediate Consistency for Availability—this is known as *Eventual Consistency*).

### Q2: Explain the difference between a Clustered Index and a Non-Clustered Index.
**Answer**:
- **Clustered Index**: Dictates the actual physical sorting order of the data on the disk. Because data can only be sorted in one physical order, a table can only have **exactly one** clustered index (usually the Primary Key). Searching via a clustered index is extremely fast because the index *is* the data.
- **Non-Clustered Index**: A separate data structure (like a B-Tree) that lives independently of the actual data rows. It contains the indexed column values and a pointer (reference) to the physical location of the actual data row. A table can have multiple non-clustered indexes.

### Q3: What is Denormalization, and why would you use it?
**Answer**:
Normalization is the process of splitting data into multiple related tables to reduce redundancy and ensure data integrity (avoiding update anomalies). 
**Denormalization** is the intentional process of violating normalization rules by adding redundant data back into a table. 
You use denormalization to drastically improve **Read Performance**. In highly normalized databases, retrieving a single user's profile might require joining 6 different tables, which is CPU and I/O intensive. By duplicating (denormalizing) the most frequently accessed data into a single table (or a NoSQL document), you eliminate the need for expensive JOINs at read time, trading storage space and write-complexity for lightning-fast reads.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Preventing Race Conditions with Locks
**Problem**: In an e-commerce application, a user wants to buy the last remaining concert ticket. User A and User B click "Buy" at the exact same millisecond. They both query the database, see `tickets_available = 1`, pass the validation logic in Java, and both update the database to `tickets_available = 0`. You've just sold the same ticket to two people. How do you prevent this at the database level?

**Solution**:
You must implement a locking strategy:
1. **Optimistic Locking**: Add a `version` column to the Ticket table. 
   - User A reads `version 1`. User B reads `version 1`.
   - User A updates the ticket: `UPDATE tickets SET available=0, version=2 WHERE id=X AND version=1`. This succeeds.
   - User B attempts to update: `UPDATE tickets SET available=0, version=2 WHERE id=X AND version=1`. This fails because the version is now 2. Java throws an `OptimisticLockException`, and User B is told the ticket is sold out. Best for high-read, low-write environments.
2. **Pessimistic Locking**: Use a database-level lock.
   - User A queries the ticket using `SELECT * FROM tickets WHERE id=X FOR UPDATE`. This locks the row.
   - User B executes the same query, but the database blocks the query, forcing User B's thread to wait until User A commits or rolls back their transaction. Once User A buys the ticket, User B's query unblocks, reads `available=0`, and fails the business logic. Best for high-contention environments.
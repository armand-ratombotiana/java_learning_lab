# Databases Quiz

## Section 1: JDBC

**Question 1:** What is JDBC?
- A) Java Database Connectivity API
- B) Java Data Bank Connection
- C) Java Development Compiler
- D) JSON Database Connector

**Answer:** A) Java Database Connectivity API

---

**Question 2:** Which interface is used to execute SQL statements?
- A) Driver
- B) Connection
- C) Statement
- D) ResultSet

**Answer:** C) Statement

---

**Question 3:** What is the purpose of PreparedStatement?
- A) Store results
- B) Pre-compile SQL with parameters
- C) Connect to database
- D) Manage transactions

**Answer:** B) Pre-compile SQL with parameters (prevents SQL injection)

---

**Question 4:** What does try-with-resources do in JDBC?
- A) Allocates memory
- B) Automatically closes resources
- C) Handles exceptions
- D) Creates connections

**Answer:** B) Automatically closes resources (Connection, Statement, ResultSet)

---

**Question 5:** Which method is used to execute batch operations?
- A) executeQuery()
- B) execute()
- C) addBatch() and executeBatch()
- D) batch()

**Answer:** C) addBatch() and executeBatch()

---

## Section 2: JPA

**Question 6:** What does @Entity annotation indicate?
- A) Database table name
- B) This class maps to a database table
- C) Primary key
- D) Relationship

**Answer:** B) This class maps to a database table

---

**Question 7:** What is the default fetch type for @OneToMany?
- A) EAGER
- B) LAZY
- C) IMMEDIATE
- D) NONE

**Answer:** B) LAZY

---

**Question 8:** Which method persists an entity in JPA?
- A) save()
- B) persist()
- C) insert()
- D) create()

**Answer:** B) persist() (for new entities), save() (for Spring Data)

---

**Question 9:** What is the purpose of @Query annotation?
- A) Define entity relationships
- B) Write custom JPQL or SQL
- C) Create indexes
- D) Set primary key

**Answer:** B) Write custom JPQL or SQL

---

**Question 10:** What does @Modifying indicate in JPA?
- A) Entity is being modified
- B) Query modifies data
- C) Transaction is active
- D) Result is modified

**Answer:** B) Query modifies data (INSERT, UPDATE, DELETE)

---

## Section 3: Transactions

**Question 11:** What does @Transactional provide?
- A) Database connection
- B) Declarative transaction management
- C) Query execution
- D) Connection pooling

**Answer:** B) Declarative transaction management

---

**Question 12:** What is the default propagation in Spring transactions?
- A) REQUIRES_NEW
- B) REQUIRED
- C) SUPPORTS
- D) NOT_SUPPORTED

**Answer:** B) REQUIRED (uses existing or creates new)

---

**Question 13:** What is optimistic locking?
- A) Uses database locks
- B) Uses @Version field to detect conflicts
- C) Uses SELECT FOR UPDATE
- D) Prevents all concurrent access

**Answer:** B) Uses @Version field to detect conflicts

---

**Question 14:** Which isolation level prevents dirty reads?
- A) READ_UNCOMMITTED
- B) READ_COMMITTED
- C) REPEATABLE_READ
- D) SERIALIZABLE

**Answer:** B) READ_COMMITTED

---

**Question 15:** What is pessimistic locking?
- A) Checks version on update
- B) Uses SELECT FOR UPDATE to lock rows
- C) No locking
- D) Uses timestamps

**Answer:** B) Uses SELECT FOR UPDATE to lock rows immediately

---

## Section 4: Connection Pooling

**Question 16:** What is the main benefit of connection pooling?
- A) Faster queries
- B) Reuses connections to reduce overhead
- C) Better security
- D) More memory

**Answer:** B) Reuses connections to reduce overhead

---

**Question 17:** Which is the recommended connection pool for Spring?
- A) C3P0
- B) Apache DBCP
- C) HikariCP
- D) BoneCP

**Answer:** C) HikariCP (fastest and default in Spring Boot)

---

**Question 18:** What does maximumPoolSize configure?
- A) Minimum connections
- B) Maximum concurrent connections
- C) Connection timeout
- D) Idle timeout

**Answer:** B) Maximum concurrent connections in pool

---

**Question 19:** What is connection timeout?
- A) Time to get connection from pool
- B) Query execution time
- C) Idle connection time
- D) Maximum lifetime

**Answer:** A) Time to wait for available connection

---

**Question 20:** What does leakDetectionThreshold do?
- A) Detects slow queries
- B) Detects connections not returned to pool
- C) Detects failed connections
- D) Detects memory leaks

**Answer:** B) Detects connections not returned to pool

---

## Score Interpretation

| Score | Level |
|-------|-------|
| 18-20 | Expert |
| 14-17 | Advanced |
| 10-13 | Intermediate |
| 5-9 | Beginner |
| < 5 | Foundation needed |
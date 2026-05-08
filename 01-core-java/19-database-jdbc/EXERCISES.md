# Exercises: JDBC Database

## Exercise 1: Basic CRUD
Create a JDBC program to:
- Insert records
- Query with SELECT
- Update records
- Delete records

## Exercise 2: PreparedStatement
Refactor to use PreparedStatement.
Prevent SQL injection.

## Exercise 3: Transaction Management
Implement explicit transactions:
- setAutoCommit(false)
- commit() on success
- rollback() on failure

## Exercise 4: Batch Operations
Use addBatch() and executeBatch() for bulk inserts.
Measure performance difference.

## Exercise 5: ResultSet Metadata
Use ResultSetMetaData to inspect query results.
Dynamically process columns.

## Solutions
See src/main/java/com/learning/database/DatabaseTraining.java
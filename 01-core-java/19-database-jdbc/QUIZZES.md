# Module 19: Database Access with JDBC - Quizzes

---

## Q1: Preventing SQL Injection
Which JDBC interface provides protection against SQL injection attacks by precompiling the SQL statement?

A) `Statement`
B) `PreparedStatement`
C) `CallableStatement`
D) `Connection`

**Answer**: B
**Explanation**: `PreparedStatement` allows parameterizing the query using `?` placeholders, which safely escapes user input and prevents SQL injection.

---

## Q2: Managing Resources
What is the best way to ensure JDBC resources (`Connection`, `Statement`, `ResultSet`) are closed properly?

A) Call `.close()` inside the `try` block.
B) Rely on the Garbage Collector.
C) Use a `try-with-resources` block.
D) Do not close them to reuse them later.

**Answer**: C
**Explanation**: `try-with-resources` ensures that resources implementing `AutoCloseable` are automatically closed at the end of the block, even if an exception occurs.

---

## Q3: Transaction Management
In JDBC, what is the default state of a connection's auto-commit mode?

A) `false`
B) `true`
C) It depends on the database driver
D) Transactions are not supported by default

**Answer**: B
**Explanation**: By default, new JDBC connections operate in auto-commit mode (`true`), meaning every executed SQL statement is committed automatically. To manage transactions manually, you must call `setAutoCommit(false)`.
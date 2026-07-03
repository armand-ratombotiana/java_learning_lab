# Module 19: Database Access with JDBC - Edge Cases & Pitfalls

---

## Pitfall 1: SQL Injection

### ❌ Wrong
Concatenating user input directly into SQL queries opens the door to SQL injection attacks.
```java
// Vulnerable to SQL Injection
String username = request.getParameter("username");
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE name = '" + username + "'");
```

### ✅ Correct
Always use `PreparedStatement` with parameterized queries.
```java
// Safe
String sql = "SELECT * FROM users WHERE name = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, username);
ResultSet rs = pstmt.executeQuery();
```

---

## Pitfall 2: Resource Leaks

### ❌ Wrong
Failing to close `Connection`, `Statement`, or `ResultSet` can exhaust database connections.
```java
Connection conn = getConnection();
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM users");
// No close calls!
```

### ✅ Correct
Use try-with-resources to ensure all JDBC resources are closed automatically.
```java
try (Connection conn = getConnection();
     Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
    // Process results
}
```

---

## Pitfall 3: Incomplete Transactions

### ❌ Wrong
Performing multiple related database updates without managing the transaction boundaries.
```java
// If step 2 fails, step 1 is still committed (AutoCommit is true by default)
stmt.executeUpdate("UPDATE accounts SET balance = balance - 100 WHERE id = 1");
stmt.executeUpdate("UPDATE accounts SET balance = balance + 100 WHERE id = 2");
```

### ✅ Correct
Set `autoCommit(false)`, perform updates, and call `commit()` or `rollback()`.
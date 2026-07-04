# Security: SQL Injection Prevention

## Parameterized Queries (Non-Negotiable)

### JDBC (safe)
```java
PreparedStatement ps = conn.prepareStatement(
    "SELECT * FROM users WHERE email = ? AND status = ?");
ps.setString(1, email);
ps.setString(2, status);
```

### JPA (safe)
```java
@Query("SELECT u FROM User u WHERE u.email = :email")
User findByEmail(@Param("email") String email);
```

### Spring Data (safe)
```java
User findByEmailAndStatus(String email, String status);
```

## Unsafe Patterns

### WRONG: String Concatenation
```java
String sql = "SELECT * FROM users WHERE name = '" + name + "'";
// Input: Robert'; DROP TABLE users; --
```

### WRONG: LIKE with Unsanitized Input
```java
String sql = "SELECT * FROM products WHERE name LIKE '%" + search + "%'";
// Use CONCAT with parameter instead
```

### WRONG: Dynamic Table/Column Names
```java
String sql = "SELECT * FROM " + tableName;
// Use allowlist validation:
if (!Set.of("users", "products").contains(tableName)) throw;
```

## Safe Dynamic Sorting
```java
// Unsafe
ORDER BY " + sortColumn;

// Safe: use case expression
ORDER BY
  CASE WHEN :sortBy = 'name' THEN name END,
  CASE WHEN :sortBy = 'salary' THEN salary END
```

## Database User Management
```sql
-- App user: minimal privileges
CREATE USER app_user WITH PASSWORD 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
-- No DDL grants
REVOKE CREATE ON SCHEMA public FROM app_user;
```

## Encryption at Rest
- Use TDE (Transparent Data Encryption) at filesystem level
- Column-level encryption for PII
- Hash + salt passwords with bcrypt/scrypt
```java
// Never store raw passwords
@Column(nullable = false)
private String passwordHash;

public void setPassword(String rawPassword) {
    this.passwordHash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
}
```

# Why JDBC & JPA Matter

JDBC and JPA form the backbone of enterprise Java database access. Understanding them is essential for any Java developer working with databases.

## Industry Impact

### JDBC
- Standardized database connectivity since 1997
- Every major database provides a JDBC driver
- Foundation for all Java database frameworks (JPA, MyBatis, jOOQ, Spring Data)
- Still used directly for 30-40% of database operations in enterprise apps

### JPA / Hibernate
- Most popular ORM in the Java ecosystem
- Used by 80%+ of Spring Boot applications
- Hibernate is the most downloaded Java library (excluding test frameworks)
- Influenced .NET's Entity Framework, Python's SQLAlchemy, Ruby's ActiveRecord

## Why It Matters for Java Developers

```java
// Without JPA: boilerplate CRUD
User user = new User();
user.setName(name);
String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
try (PreparedStatement stmt = conn.prepareStatement(sql, RETURN_GENERATED_KEYS)) {
    stmt.setString(1, user.getName());
    stmt.setString(2, user.getEmail());
    stmt.executeUpdate();
    ResultSet keys = stmt.getGeneratedKeys();
    if (keys.next()) user.setId(keys.getLong(1));
}

// With JPA: one line
userRepository.save(user);
```

## Business Impact
- 5-10x reduction in data access code
- Database portability (switch databases without code changes)
- First-level cache reduces database load
- Automatic optimistic locking prevents lost updates
- Declarative transactions (no manual commit/rollback)

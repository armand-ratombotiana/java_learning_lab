# JDBC & JPA

## Overview
JDBC (Java Database Connectivity) is the standard Java API for database access. JPA (Jakarta Persistence API) is a higher-level ORM specification, commonly implemented by Hibernate. Together they form the foundation of Java database programming.

## Key Concepts
- **JDBC**: Low-level SQL execution, result set processing
- **Connection Pooling**: Reuse database connections (HikariCP)
- **JPA**: Object-relational mapping, entity lifecycle
- **Hibernate**: Most popular JPA implementation
- **JPQL**: Object-oriented query language

## Java Examples
```java
// JDBC
try (Connection conn = dataSource.getConnection();
     PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
    stmt.setLong(1, userId);
    ResultSet rs = stmt.executeQuery();
    while (rs.next()) {
        String name = rs.getString("name");
    }
}

// JPA / Hibernate
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
}
```

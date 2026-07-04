# Security: Relational Database Best Practices

## SQL Injection Prevention

### WRONG (string concatenation)
```java
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(
    "SELECT * FROM users WHERE name = '" + userName + "'");
```

### RIGHT (parameterized queries)
```java
PreparedStatement stmt = conn.prepareStatement(
    "SELECT * FROM users WHERE name = ?");
stmt.setString(1, userName);
ResultSet rs = stmt.executeQuery();
```

### JPA Native Query (parameterized)
```java
@Query(value = "SELECT * FROM users WHERE name = :name", nativeQuery = true)
List<User> findByName(@Param("name") String name);
```

## Authentication & Authorization

### Database Users
```sql
CREATE USER app_user WITH PASSWORD 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;
```

### Least Privilege Principle
```sql
-- Reporting user: read-only
CREATE USER reporting_user WITH PASSWORD 'password';
GRANT SELECT ON ALL TABLES IN SCHEMA public TO reporting_user;

-- Migration user: DDL only
CREATE USER migration_user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE mydb TO migration_user;
-- (revoke in post-migration script)
```

## Encryption

### Data-at-Rest (TDE)
```sql
-- PostgreSQL: pg_tde extension or filesystem-level encryption
-- MySQL: InnoDB tablespace encryption
```

### Data-in-Transit (TLS)
```yaml
# application.yml
spring.datasource.url: jdbc:postgresql://host:5432/db?ssl=true&sslmode=verify-full
```

### Column-Level Encryption
```java
@Column
@Convert(converter = EncryptionConverter.class)
private String ssn;
```

## Auditing with JPA
```java
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;
}
```

## Additional Security Measures
- Rotate credentials (Vault, AWS Secrets Manager)
- Network isolation (VPC, security groups)
- Regular backup encryption
- Audit logging (pgaudit extension)
- Row-Level Security (RLS) for multi-tenant apps

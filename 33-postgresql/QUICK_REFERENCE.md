# Quick Reference: PostgreSQL

<div align="center">

![Module](https://img.shields.io/badge/Module-33-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-PostgreSQL-green?style=for-the-badge)

**Quick lookup guide for PostgreSQL**

</div>

---

## 📋 Data Types

| Type | Description |
|------|-------------|
| **JSONB** | Binary JSON, indexed |
| **ARRAY** | PostgreSQL arrays |
| **UUID** | Universally unique ID |
| **ENUM** | Custom enumerated types |
| **HSTORE** | Key-value store |
| **POINT** | Geometric point |
| **CIDR/IPADDR** | Network types |

---

## 🔑 Key Commands

### SQL Basics
```sql
-- Create table with advanced types
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    status USER_STATUS DEFAULT 'active',
    preferences JSONB,
    tags TEXT[],
    created_at TIMESTAMP DEFAULT NOW()
);

-- Enum type
CREATE TYPE USER_STATUS AS ENUM ('active', 'inactive', 'suspended');

-- Index on JSONB
CREATE INDEX idx_preferences ON users USING GIN (preferences);

-- Full-text search
CREATE INDEX idx_search ON users USING GIN (to_tsvector('english', email || ' ' || name));
```

### Advanced Queries
```sql
-- Window functions
SELECT name, department, salary,
    AVG(salary) OVER (PARTITION BY department) as dept_avg,
    RANK() OVER (ORDER BY salary DESC) as salary_rank
FROM employees;

-- CTE and recursion
WITH RECURSIVE org_chart AS (
    SELECT id, name, manager_id, 1 as level
    FROM employees WHERE manager_id IS NULL
    UNION ALL
    SELECT e.id, e.name, e.manager_id, oc.level + 1
    FROM employees e JOIN org_chart oc ON e.manager_id = oc.id
)
SELECT * FROM org_chart;

-- JSONB queries
SELECT * FROM users WHERE preferences->>'theme' = 'dark';
SELECT preferences->'notifications'->'email' FROM users;
```

---

## 📊 Spring Data JPA

### Entity
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> preferences;
    
    private String[] tags;
}
```

### Repository
```java
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u FROM User u WHERE u.preferences->>'theme' = :theme")
    List<User> findByTheme(@Param("theme") String theme);
    
    @Query(value = "SELECT * FROM users WHERE preferences @> :prefs", nativeQuery = true)
    List<User> findByPreferencesJson(@Param("prefs") String prefs);
}
```

---

## 📊 PostgreSQL Functions

```sql
-- PL/pgSQL function
CREATE OR REPLACE FUNCTION get_user_stats(user_id UUID)
RETURNS TABLE(total_orders bigint, total_spent decimal) AS $$
BEGIN
    RETURN QUERY
    SELECT COUNT(*), COALESCE(SUM(amount), 0)
    FROM orders WHERE user_id = user_id;
END;
$$ LANGUAGE plpgsql;

-- Trigger
CREATE TRIGGER update_modified
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();
```

---

## ✅ Best Practices

- Use appropriate data types for your needs
- Index columns used in WHERE/JOIN clauses
- Use EXPLAIN ANALYZE for query optimization
- Implement connection pooling (HikariCP)

### ❌ DON'T
- Don't store large objects as text
- Don't ignore vacuum/autoanalyze

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>
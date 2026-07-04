# Performance: SQL Query Optimization

## Index Strategies

### Covering Index
```sql
CREATE INDEX idx_covering ON employees (dept_id) INCLUDE (name, salary);
-- Index Only Scan possible for: SELECT name, salary FROM employees WHERE dept_id = ?
```

### Partial Index
```sql
CREATE INDEX idx_active_employees ON employees (salary)
WHERE active = true;
-- Small index, only active employees
```

### Expression Index
```sql
CREATE INDEX idx_lower_email ON employees (LOWER(email));
SELECT * FROM employees WHERE LOWER(email) = 'alice@example.com';
```

## Query Rewriting for Performance

### Bad: Function on Indexed Column
```sql
-- INDEX not used!
SELECT * FROM orders WHERE DATE(created_at) = '2024-01-01';
```

### Good: Range Query
```sql
SELECT * FROM orders WHERE created_at >= '2024-01-01' AND created_at < '2024-01-02';
```

### Bad: DISTINCT for Dedup
```sql
SELECT DISTINCT customer_id FROM orders;  -- sort on potentially huge set
```

### Good: EXISTS
```sql
SELECT id FROM customers c WHERE EXISTS (SELECT 1 FROM orders o WHERE o.customer_id = c.id);
```

## Join Performance

| Join Type | Best For | Memory |
|---|---|---|
| Hash Join | Large, unsorted tables | High (builds hash table) |
| Merge Join | Pre-sorted data | Low |
| Nested Loop | One small table | Minimal |

## Connection Pool Sizing
```yaml
spring.datasource.hikari:
  maximum-pool-size: 20
  minimum-idle: 5
  connection-timeout: 5000
  idle-timeout: 300000
  max-lifetime: 1200000
```

## JPA Query Hints
```java
@QueryHints({
    @QueryHint(name = "org.hibernate.fetchSize", value = "100"),
    @QueryHint(name = "org.hibernate.comment", value = "My query")
})
@Query("SELECT e FROM Employee e WHERE e.salary > :min")
List<Employee> findHighEarners(@Param("min") BigDecimal min);
```

## Batch Processing
```yaml
spring.jpa.properties.hibernate.jdbc.batch_size: 50
spring.jpa.properties.hibernate.order_inserts: true
spring.jpa.properties.hibernate.order_updates: true
```

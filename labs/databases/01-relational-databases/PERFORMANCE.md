# Performance: Relational Database Optimization

## Indexing Strategy

| Index Type | Use Case | Example |
|---|---|---|
| B-tree | Equality + range queries | `WHERE price > 100` |
| Hash | Equality only | `WHERE id = ?` |
| GiST | Full-text, geometric | `WHERE vector @@ to_tsquery('cat')` |
| GIN | Array, JSONB, full-text | `WHERE tags @> ARRAY['urgent']` |
| BRIN | Correlated, large tables | `WHERE created_at > NOW() - '7d'::interval` |

## Query Performance Tips

### Use Covering Indexes
```sql
CREATE INDEX idx_covering ON orders (customer_id) INCLUDE (total, order_date);
```

### Avoid SELECT *
```java
// WRONG: fetches all columns
@Query("SELECT o FROM Order o")

// RIGHT: fetch only needed columns
@Query("SELECT o.id, o.total FROM Order o")
```

### Pagination with Keyset (not OFFSET)
```sql
-- Before (slow for high offsets)
SELECT * FROM orders ORDER BY id LIMIT 20 OFFSET 100000;

-- After (keyset pagination)
SELECT * FROM orders WHERE id > 100000 ORDER BY id LIMIT 20;
```

## Connection Pool Tuning

| Parameter | Default | Tuned Value |
|---|---|---|
| minimumIdle | 10 | Same as pool size |
| maximumPoolSize | 10 | `(core_count * 2) + effective_storage_count` |
| connectionTimeout | 30000 | 5000 |
| idleTimeout | 600000 | 300000 |
| maxLifetime | 1800000 | 1200000 |

## JPA Performance

### Batch Insert
```yaml
spring.jpa.properties.hibernate.jdbc.batch_size=50
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

### Read-Only Transactions
```java
@Transactional(readOnly = true)
public List<Customer> findAll() { ... }
```

### Second-Level Cache
```java
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class Product { ... }
```

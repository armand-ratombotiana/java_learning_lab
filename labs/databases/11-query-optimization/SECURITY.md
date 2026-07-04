# Security: Query Optimization

## SQL Injection in Dynamic Queries
```java
// DANGEROUS: string concatenation + dynamic sort
String sql = "SELECT * FROM users ORDER BY " + sortColumn + " " + sortDir;
// Attacker can inject: sortColumn = "id; DROP TABLE users--"
```

**Fix**: Validate sort columns against allowlist:
```java
private static final Set<String> ALLOWED_SORT_COLUMNS =
    Set.of("id", "name", "email", "created_at");

public List<User> findAllSorted(String sort, String dir) {
    if (!ALLOWED_SORT_COLUMNS.contains(sort)) {
        throw new IllegalArgumentException("Invalid sort column");
    }
    Sort.Direction direction = Sort.Direction.fromString(dir);
    return repository.findAll(Sort.by(direction, sort));
}
```

## Query Timeout
Prevent runaway queries from exhausting resources:
```java
@Query("SELECT * FROM heavy_report")
@QueryHints(@QueryHint(name = "javax.persistence.query.timeout", value = "30000"))
List<Report> getHeavyReport();
```

## Data Masking in Logs
```java
// Never log full query results with PII
// Log only query metrics:
log.debug("Query returned {} rows in {}ms", results.size(), elapsed);
```

## Row-Level Security
Use PostgreSQL Row-Level Security with optimizer-aware policies:
```sql
CREATE POLICY tenant_isolation ON orders
    USING (tenant_id = current_setting('app.tenant_id')::int);
```

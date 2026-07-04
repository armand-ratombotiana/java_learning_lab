# Refactoring: PostgreSQL Database Patterns

## Schema Migration Pattern

```java
// Step 1: Add column (nullable)
public class V2_Add_metadata_column extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        try (Statement stmt = context.getConnection().createStatement()) {
            stmt.execute("ALTER TABLE users ADD COLUMN metadata JSONB DEFAULT '{}'");
            stmt.execute("CREATE INDEX idx_users_metadata ON users USING GIN (metadata)");
        }
    }
}
```

## Denormalization Strategy
```sql
-- Before: 3NF normalized
SELECT p.*, c.name AS category, s.name AS supplier
FROM products p
JOIN categories c ON p.category_id = c.id
JOIN suppliers s ON p.supplier_id = s.id;

-- After: materialized view for reporting
CREATE MATERIALIZED VIEW product_report AS
SELECT p.id, p.name, p.price, c.name AS category, s.name AS supplier
FROM products p
JOIN categories c ON p.category_id = c.id
JOIN suppliers s ON p.supplier_id = s.id;

REFRESH MATERIALIZED VIEW CONCURRENTLY product_report;
```

## JSONB vs Normalized Schema Refactoring

```java
// Before: EAV anti-pattern
@Entity
@Table(name = "user_attributes")
public class UserAttribute {
    @Id private Long id;
    private Long userId;
    private String attrKey;
    private String attrValue;
}

// After: JSONB column
@Entity
@Table(name = "users")
public class User {
    @Id private Long id;
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> attributes;
}
```

## Index Refactoring

```sql
-- Before: separate indexes
CREATE INDEX idx_orders_user ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);

-- After: composite covering index
CREATE INDEX idx_orders_user_status ON orders (user_id, status)
    INCLUDE (total, created_at);
```

## Sequence to Identity Refactoring

```sql
-- Before: sequence + table default
CREATE SEQUENCE users_id_seq;
CREATE TABLE users (id BIGINT DEFAULT nextval('users_id_seq'));

-- After: identity column (PostgreSQL 10+)
CREATE TABLE users (id BIGINT GENERATED ALWAYS AS IDENTITY);
```

## Partitioning Refactoring

```sql
-- Before: single huge table
CREATE TABLE events (id BIGSERIAL, created_at TIMESTAMPTZ, data JSONB);

-- After: partitioned by month
CREATE TABLE events (id BIGSERIAL, created_at TIMESTAMPTZ, data JSONB)
    PARTITION BY RANGE (created_at);

CREATE TABLE events_2024_q1 PARTITION OF events
    FOR VALUES FROM ('2024-01-01') TO ('2024-04-01');
```

## Connection Pool Refactoring

```java
// Before: no pooling
Connection conn = DriverManager.getConnection(url, user, pass);

// After: HikariCP
HikariConfig config = new HikariConfig();
config.setJdbcUrl(url);
config.setUsername(user);
config.setPassword(pass);
config.setMaximumPoolSize(20);
config.setMinimumIdle(5);
config.addDataSourceProperty("ApplicationName", "myapp");
HikariDataSource ds = new HikariDataSource(config);
```

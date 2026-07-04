# Performance: PostgreSQL

## Configuration Tuning

```conf
# Memory
shared_buffers = 4GB                  # 25% of RAM
effective_cache_size = 12GB            # 75% of RAM
work_mem = 32MB                        # per sort/hash operation
maintenance_work_mem = 1GB             # VACUUM, CREATE INDEX

# WAL
wal_level = replica
max_wal_size = 4GB
min_wal_size = 1GB
wal_buffers = 16MB

# Query Tuning
random_page_cost = 1.1                 # for SSD storage
effective_io_concurrency = 200         # SSD concurrent I/O
default_statistics_target = 500        # better query plans
```

## Index Strategies

```sql
-- B-tree: equality + range queries (default)
CREATE INDEX idx_created_at ON orders (created_at);

-- Composite: multi-column filter + sort
CREATE INDEX idx_user_status_created ON orders (user_id, status, created_at DESC);

-- Partial: subset of rows
CREATE INDEX idx_active_orders ON orders (created_at) WHERE status = 'active';

-- Covering: include extra columns
CREATE INDEX idx_order_summary ON orders (user_id) INCLUDE (total, status);

-- GIN: JSONB/array/full-text search
CREATE INDEX idx_data_gin ON users USING GIN (data);

-- BRIN: large sequential data
CREATE INDEX idx_created_brin ON events USING BRIN (created_at) WITH (pages_per_range = 32);
```

## Query Optimization

```java
// WRONG: N+1 queries
List<Order> orders = orderRepo.findAll();
for (Order o : orders) {
    System.out.println(o.getCustomer().getName());
}

// RIGHT: join fetch
@Query("SELECT o FROM Order o JOIN FETCH o.customer")
List<Order> findAllWithCustomer();

// RIGHT: batch size
@Entity
@BatchSize(size = 50)
public class Order { ... }
```

## Bulk Operations

```java
// WRONG: individual inserts
for (User user : users) {
    userRepo.save(user);
}

// RIGHT: JDBC batch
String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    for (User user : users) {
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getEmail());
        stmt.addBatch();
    }
    stmt.executeBatch();
}

// RIGHT: JPA batch
// persistence.xml
// <property name="hibernate.jdbc.batch_size" value="50"/>
// <property name="hibernate.order_inserts" value="true"/>
```

## Monitoring Queries

```sql
-- Track query performance
SELECT queryid, query, calls,
       round(total_exec_time::numeric, 2) AS total_ms,
       round(mean_exec_time::numeric, 2) AS avg_ms,
       round(100 * total_exec_time / SUM(total_exec_time) OVER (), 2) AS pct
FROM pg_stat_statements
WHERE query NOT LIKE '%pg_%'
ORDER BY total_exec_time DESC
LIMIT 20;
```

## Vacuum Tuning

```sql
-- Per-table autovacuum configuration
ALTER TABLE orders SET (
    autovacuum_vacuum_scale_factor = 0.01,
    autovacuum_analyze_scale_factor = 0.005,
    autovacuum_vacuum_threshold = 1000
);
```

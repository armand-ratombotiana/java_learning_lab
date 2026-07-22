# Prevention: Avoiding Slow Queries and Batch Job Failures

**Incident**: INC-2024-0810-BATCH
**Category**: Database Query Performance
**Applies To**: All services using Oracle or any SQL database

## Prevention Strategies

### 1. Schema Change Index Review

Every schema change that adds a new column or modifies a JOIN condition MUST include an index review:

| Change Type | Index Check Required | DBA Approval Needed |
|-------------|---------------------|---------------------|
| New column added to WHERE clause | Yes | Yes |
| New column added to JOIN condition | Yes | Yes |
| New table referenced in existing query | Yes (all join columns) | Yes |
| Index-only change | N/A | Yes |
| Column type change affecting index | Yes | Yes |

### 2. Execution Plan Check in CI/CD

Every SQL query in the codebase must have its execution plan verified before deployment:

```yaml
# CI/CD Pipeline Step: Execution Plan Check
steps:
  - name: Check execution plans
    run: |
      for sql_file in $(find src/main/resources/sql -name "*.sql"); do
        echo "Checking: $sql_file"
        # Connect to test database and explain plan
        sqlplus -s app_user/password@testdb <<EOF
          SET LONG 1000000
          EXPLAIN PLAN FOR $(cat $sql_file)
          SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY)
        EOF
        # Check for full table scans on large tables
        if grep -q "TABLE ACCESS FULL" output.txt; then
          echo "WARNING: Full table scan detected in $sql_file"
          # Fail if the table is known to be large
        fi
      done
```

### 3. Query Performance Baseline

| Metric | Baseline | Warning | Critical |
|--------|----------|---------|----------|
| Elapsed time | < 60 min | 60-120 min | > 120 min |
| Temp space | < 100MB | 100MB-1GB | > 1GB |
| Full table scans | 0 on large tables | 1-2 | 3+ |
| Disk reads | < 1M | 1M-5M | > 5M |
| Logical reads | < 2M | 2M-10M | > 10M |

### 4. Index Creation Checklist

When adding a new column or query:

- [ ] Will this column be used in WHERE clauses?
- [ ] Will this column be used in JOIN conditions?
- [ ] Will this column be used in ORDER BY or GROUP BY?
- [ ] What is the cardinality of this column? (high = good for indexing)
- [ ] What is the expected table size at this column's introduction?
- [ ] What is the expected table size in 6 months?
- [ ] Is a composite index needed for covering queries?
- [ ] Should the index be created ONLINE (production)?
- [ ] Should the index use NOLOGGING (reduces redo)?

### 5. Composite Index Design Rules

```sql
-- Rule 1: Index columns in order: EQUALITY → RANGE → ORDER BY
CREATE INDEX idx_orders_status_created
ON orders (status, created_at DESC);
-- WHERE status = 'PENDING' ORDER BY created_at DESC

-- Rule 2: Put high-cardinality columns first for selectivity
-- status has 3 distinct values (low cardinality)
-- customer_id has 500K distinct values (high cardinality)
CREATE INDEX idx_orders_customer_status
ON orders (customer_id, status);

-- Rule 3: Add INCLUDE columns for covering indexes (Oracle 19c+)
CREATE INDEX idx_orders_status_created
ON orders (status, created_at DESC)
INCLUDE (total, currency);
-- Query can be satisfied entirely from the index (no table access)

-- Rule 4: For JOIN columns, index the foreign key side
-- BAD: No index on orders.customer_id
-- GOOD: INDEX on orders.customer_id
CREATE INDEX idx_orders_customer_id
ON orders (customer_id);

-- Rule 5: Consider function-based indexes for transformed columns
CREATE INDEX idx_orders_upper_status
ON orders (UPPER(status));
```

### 6. Execution Plan Rules

| Plan Operation | Acceptable? | Notes |
|----------------|-------------|-------|
| TABLE ACCESS FULL (table < 10K rows) | Yes | Small lookup tables |
| TABLE ACCESS FULL (table > 1M rows) | No | Must investigate missing index |
| INDEX RANGE SCAN | Yes | Good — index-driven access |
| INDEX UNIQUE SCAN | Best | Single row access |
| HASH JOIN | Sometimes | OK for moderate result sets |
| NESTED LOOPS | Yes | Good with index-driven inner table |
| SORT ORDER BY | Sometimes | Consider index for ORDER BY |
| SORT GROUP BY | Sometimes | Consider index for GROUP BY |
| Cartesian product (no join condition) | NEVER | Always a bug |

### 7. Batch Job Monitoring

Every batch job should have:

| Monitor | Purpose |
|---------|---------|
| Expected duration baseline | Detect regression over time |
| P95 deviation alert | Alert if job takes > 20% longer than P95 |
| Temp space usage trend | Alert if temp space growing |
| Row count comparison | Alert if row count deviates > 20% from expected |
| Execution plan snapshot | Compare plan changes over time |

### 8. Capacity Planning

Regular review of table growth and index effectiveness:

```sql
-- Monthly index usage report
SELECT
    i.index_name,
    i.table_name,
    s.num_rows,
    ROUND(i.clustering_factor / s.num_rows, 2) AS clustering_ratio,
    u.used AS index_used
FROM user_indexes i
JOIN user_tables s ON i.table_name = s.table_name
LEFT JOIN v$object_usage u ON i.index_name = u.index_name
ORDER BY s.num_rows DESC;

-- Identify unused indexes (no usage in 30 days)
SELECT index_name, table_name
FROM v$object_usage
WHERE used = 'NO' AND start_monitoring > SYSDATE - 30;
```

### 9. Training Requirements

| Topic | Audience | Frequency |
|-------|----------|-----------|
| Oracle execution plan reading | All developers | Onboarding |
| Index design and selection | All developers | Annually |
| SQL Tuning Advisor usage | Senior developers | Annually |
| AWR/ASH report analysis | Senior developers + DBAs | Quarterly |
| Partitioning and PEL | DBAs | Onboarding |
| Query optimization patterns | All developers | Annually |

### 10. DDL Change Approval Process

All DDL changes must go through a formal approval process:

```yaml
DDL Change Approval Checklist:
  - Table/column added for new feature
  - Index requirements reviewed:
      * Will any columns be used in WHERE?
      * Will any columns be used in JOIN?
      * Will any columns be used in ORDER BY?
  - Index creation script prepared
  - Online index creation (ONLINE clause)
  - NOLOGGING for large indexes
  - Post-deployment statistics gathering
  - Execution plan validation
  - Rollback script prepared (DROP INDEX)
```

### 11. Batch Job Performance Baselines

| Job Name | Expected Duration | P95 Duration | P99 Duration | Warning (2x) | Critical (3x) | Alert On |
|----------|-----------------|--------------|--------------|-------------|---------------|----------|
| EOD_SETTLEMENT | 45 min | 60 min | 75 min | 120 min | 180 min | Duration > P95 |
| NIGHTLY_PURGE | 30 min | 40 min | 50 min | 80 min | 120 min | Duration > P95 |
| WEEKLY_COMPACT | 120 min | 150 min | 180 min | 300 min | 360 min | Duration > P99 |

### 12. Execution Plan Comparison in CI/CD

```yaml
# CI/CD pipeline step: compare execution plans
steps:
  - name: Capture execution plan
    run: |
      sqlplus -s $DB_URL <<EOF
        EXPLAIN PLAN FOR $(cat src/main/resources/sql/batch_query.sql);
        SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);
      EOF > plan_new.txt

  - name: Compare with baseline
    run: |
      if [ -f plan_baseline.txt ]; then
        diff plan_baseline.txt plan_new.txt || {
          echo "WARNING: Execution plan changed!"
          echo "Running SQL Tuning Advisor..."
        }
      fi

  - name: Update baseline
    run: cp plan_new.txt plan_baseline.txt
```

### 13. Oracle Performance Best Practices

| Practice | Recommendation | Priority |
|----------|---------------|----------|
| Regular statistics | Gather stats daily during maintenance window | P1 |
| Index monitoring | Monitor unused indexes monthly | P2 |
| SQL Plan Management | Use SPM to prevent plan regression | P1 |
| Partition large tables | Partition tables > 10M rows | P1 |
| Query tuning | Use SQL Tuning Advisor quarterly | P2 |
| AWR reviews | Review AWR reports weekly | P2 |

### 14. Schema Change Approval Form

| Field | Value |
|-------|-------|
| Table name | |
| Column(s) added | |
| JOIN column? | Yes / No |
| Index created? | Yes / No |
| EXPLAIN PLAN verified? | Yes / No |
| Performance test result | |
| Rollback script prepared? | Yes / No |
| DBA approval | |
| Developer signature | |

## References

- Use The Index, Luke: "SQL Indexing and Tuning" — https://use-the-index-luke.com/
- Oracle: "Performance Tuning Guide" — Oracle Database Documentation
- Oracle: "SQL Tuning Advisor" — Oracle Documentation
- Oracle: "Partitioning Guide" — Oracle VLDB and Partitioning Guide
- Dan Tow: "SQL Tuning" — O'Reilly Media
- Oracle Support: "AWR Report Interpretation" — Doc ID 1357643.1
- Oracle Support: "DBMS_XPLAN Usage" — Doc ID 123456.1


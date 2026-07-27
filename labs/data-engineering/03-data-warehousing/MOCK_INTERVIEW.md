# Mock Interview: Data Warehousing (03-data-warehousing)

## Scenario: Migrate from legacy warehouse to modern cloud warehouse
Your company is migrating from a Teradata on-prem data warehouse to Snowflake. The warehouse has 500+ tables, 10TB of data, and 100+ SQL reports. Downtime must be < 4 hours.

### Time: 45 minutes | Difficulty: Hard

---

## Part 1: Migration Strategy (15 min)

**Approach: Phased migration (not lift-and-shift)**

**Phase 0: Assessment (2 weeks)**
- Inventory all 500+ tables: size, row count, primary keys, indexes
- Profile query patterns: top 50 queries by frequency and cost
- Identify data types that need mapping (VARBYTE, PERIOD, etc.)
- Document ETL dependencies and data lineage

**Phase 1: Schema migration (1 week)**
- Migrate DDL: Teradata → Snowflake SQL translation
- Set up dbt for schema management and testing
- Validate schema equivalency (column count, types, nullability)

**Phase 2: Data migration (2 weeks)**
- Export from Teradata: UNLOAD to S3 (Parquet format)
- Import to Snowflake: COPY INTO with automatic clustering
- Validate row counts, checksums, sample data comparison

**Phase 3: Query migration (3 weeks)**
- Translate top 100 reports from Teradata SQL to Snowflake SQL
- Set up performance benchmarks (compare runtimes)
- Optimize problematic queries with Snowflake features

**Phase 4: Cutover (weekend)**
- Final sync: catch-up migration for delta changes
- Validate data freshness, all 100 reports working
- Switch read traffic from Teradata to Snowflake
- Keep Teradata available for 2-week rollback window

---

## Part 2: SQL Translation (10 min)

**Teradata → Snowflake differences:**

| Teradata | Snowflake | Notes |
|----------|-----------|-------|
| `SELECT TOP N` | `SELECT ... LIMIT N` | Order of operations different |
| `QUALIFY ROW_NUMBER()` | `QUALIFY ROW_NUMBER()` | Snowflake supports QUALIFY too |
| `DATE` (form 'YYYY-MM-DD') | `DATE` (same, but `'YYYY-MM-DD'::DATE`) | Explicit cast recommended |
| `CAST(col AS DECIMAL(18,4))` | `col::NUMBER(18,4)` | Equivalent |
| `HASHAMP()+HASHBUCKET()` | No equivalent | Distribution is automatic |
| `RANDOM(1,100)` | `UNIFORM(1, 100, RANDOM())` | Different function |
| `SAMPLE 0.1` | `SELECT ... SAMPLE (0.1)` | Similar |
| `INDEXES` | Not applicable | Micro-partitions replace indexes |
| `STATS` | `AUTO_CLUSTERING` | Automatic statistics |
| `MACRO` | Stored procedure | Different syntax |

**Handling BTEQ scripts:**
- `.EXPORT` → Snowflake `COPY INTO LOCATION`
- `.IMPORT` → Snowflake `PUT` + `COPY INTO`
- `LOGON` → Snowflake session/connection
- Conditional logic → Rewrite in Python/Snowflake scripting

---

## Part 3: Performance Optimization (10 min)

**Sizing Snowflake for 10TB workload:**

| Decision | Recommendation | Rationale |
|----------|---------------|-----------|
| Warehouse initial | 2X-Large (16 credits/hr) | Balance cost and performance |
| Scaling | Multi-cluster (max 3) | For concurrent query workload |
| Clustering | By date on fact tables | Query range on last 30 days |
| Materialized views | For frequent aggregations | Reduce recomputation |
| Caching | Use results cache | Same queries benefit |
| Auto-suspend | 5 minutes | Save costs on idle |

**Performance validation:**
1. Run top 50 queries on both systems
2. Compare: wall time, bytes scanned, partitions pruned
3. Target: Snowflake within 1.5x of Teradata on equivalent warehouse cost
4. Iterate: clustering keys, warehouse sizing, query rewriting

**Query optimization checklist:**
- [ ] WHERE clause on clustering key (pruning)
- [ ] No `SELECT *` in production queries
- [ ] JOIN on same distribution key (avoid shuffle)
- [ ] Materialized views for sub-second responses
- [ ] Window functions instead of self-joins

---

## Part 4: Testing & Rollback (10 min)

**Data correctness testing:**
1. **Row count comparison:** `SELECT COUNT(*)` per table
2. **Checksum:** MD5 of all column values concatenated
3. **Sample data:** 10,000 random rows compared field-by-field
4. **Aggregate comparison:** SUM, AVG, MIN, MAX on key numeric columns
5. **Business logic tests:** Re-run top 10 reports, compare numbers

**Rollback plan:**
```
If data validation fails:
1. Keep Teradata read-only (users stay on old system)
2. Stop Snowflake ingest (preserve migrated data for retry)
3. Root cause: fix schema mapping or data issue
4. Truncate affected Snowflake tables
5. Re-run migration for affected tables
6. Full rollback: restore DNS/report connections to Teradata
```

**Progressive cutover:**
- Start with 1 non-critical team (2 weeks before full cutover)
- Then 2 more teams (1 week before)
- Final cutover: all remaining teams over weekend
- Rollback window: 2 weeks of parallel operation

---

## Follow-up Questions

**Choosing warehouse size:**
- Start sizing from query patterns: most queries read last 30 days of 10TB = ~800GB
- Partition pruning should reduce scanned data to ~50GB per query
- Medium warehouse (4 credits/hr) would suffice for queries scanning 50GB
- Scale to Large/X-Large during batch processing window

**Clustering key selection:**
- Fact tables: `CLUSTER BY (order_date, customer_id)` - date range + point lookup
- Dimension tables: No clustering (small enough for full scan)
- Monitor: `SYSTEM$CLUSTERING_RATIO` - recluster if > 4.0

**Materialized views vs streams + tasks:**
- Materialized view: Automatic, incremental refresh, for simple aggregations
- Streams + tasks: More flexible, for complex transformations with CDC
- Dynamic tables: Declarative, for multi-step transformations


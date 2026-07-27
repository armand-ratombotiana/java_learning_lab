# Mock Interview: ETL Processes (02-etl-processes)

## Scenario: Design ETL for a multi-source data warehouse
Your company needs to consolidate data from PostgreSQL (transactions), Salesforce (CRM), and REST API logs into Snowflake for analytics. You have a 4-hour nightly batch window.

### Time: 45 minutes | Difficulty: Medium-Hard

---

## Part 1: Extraction Strategy (15 min)

**Three sources, three different extraction approaches:**

**Source 1: PostgreSQL (transactions)**
- Volume: 50M rows/day, 500GB total
- Change tracking: updated_at timestamp, no native CDC
- Approach: Incremental timestamp-based with high-watermark table
- Fallback: Full refresh on weekends for reconciliation

**Source 2: Salesforce (CRM)**
- Volume: 100K records/day (accounts, contacts, opportunities)
- API limits: 100K API calls/day per org
- Approach: Bulk API 2.0 for daily full refresh (small volume)
- Optimization: Use PK chunking for large tables

**Source 3: REST API Logs**
- Volume: 200M log entries/day (JSON)
- Location: S3 bucket (hourly files)
- Approach: COPY INTO from S3 staging with file pattern matching
- Handling: Partition by ingestion hour, dedup by log_id

**Your extraction design should address:**
- How do you handle the 100K API/day limit for Salesforce?
- What's the watermark table schema?
- How do you handle DELETE operations if there's no CDC?
- What if the source takes longer than expected?

---

## Part 2: Transformation Layer (10 min)

**Schema differences across sources:**
- PostgreSQL: `customer_id INT, name VARCHAR(100), created_at TIMESTAMP`
- Salesforce: `Id (18-char), Name, CreatedDate`
- REST API: `{"user": {"id": 123, "full_name": "..."}, "ts": "..."}`

**Handling strategy:**
1. **Staging schema:** Land raw data exactly as-is (keep source types)
2. **Business mapping table:** Map source fields to canonical fields
3. **dbt staging models:** Standardize types (e.g., Salesforce 18-char ID → INT via hash)
4. **Data quality checks:** Type casting success rate, NULL checks, uniqueness
5. **Schema drift:** If a new field appears, add to staging automatically, flag for review

**Example dbt staging model:**
```sql
-- stg_salesforce_accounts.sql
SELECT
  Id AS sf_account_id,
  Name AS account_name,
  CAST(CreatedDate AS TIMESTAMP) AS created_at,
  Type AS account_type,
  'salesforce' AS source_system
FROM {{ source('salesforce', 'accounts') }}
```

---

## Part 3: Load Strategy (10 min)

**Per table type determination:**

| Table Type | Load Strategy | Reason |
|------------|--------------|--------|
| Transactions (fact) | Incremental MERGE by order_id | Append-only, large volume |
| Customers (dimension) | SCD Type 2 (MERGE) | Need address change history |
| Products (dimension) | SCD Type 1 (overwrite) | Small, just current values |
| Salesforce accounts | Full refresh daily | Small volume, simple |
| Logs | Incremental append | Append-only, massive volume |

**MERGE example for transactions:**
```sql
MERGE INTO dim_customer AS t
USING stg_customer AS s ON t.customer_id = s.customer_id
WHEN MATCHED AND t.hash_value != s.hash_value THEN
  UPDATE SET is_current = FALSE, end_date = CURRENT_DATE
WHEN NOT MATCHED THEN
  INSERT (customer_id, name, email, hash_value, start_date, end_date, is_current)
  VALUES (s.customer_id, s.name, s.email, s.hash_value, CURRENT_DATE, NULL, TRUE);
-- Then insert new current rows
```

---

## Part 4: Error Handling & Monitoring (10 min)

**API is down scenario:**
1. **Detect:** API returns 503 or timeout after 3 retries
2. **Retry:** Exponential backoff (30s, 2min, 5min, 15min) - max 4 retries
3. **Fallback:** Use last successful full refresh data (stale but available)
4. **Alert:** PagerDuty notification with severity based on SLA impact
5. **Catch-up:** Once API is back, run catch-up job for missed data

**Monitoring metrics:**
- Pipeline duration per stage (extract, transform, load)
- Row counts: source vs staging vs target (diff alerts)
- Freshness: max(event_timestamp) per table, lag from current time
- Error count: per source, per pipeline stage
- Cost: Snowflake credits consumed per pipeline run

**Data quality framework:**
- **Freshness SLA:** `max(ingested_at) > CURRENT_TIME - INTERVAL '4 hours'`
- **Volume check:** `abs(row_count - avg_7d_row_count) / avg_7d_row_count < 0.1`
- **Schema check:** Column list matches expected (run before MERGE)
- **Reconciliation:** Row-by-row comparison on 1% sample

---

## Follow-up Questions

**Late-arriving records:**
- How do you handle a transaction that arrives 3 days late (mobile offline)?
- Store in `late_arrivals` table, process separately, merge next day
- Track `source_transaction_date` vs `pipeline_process_date`

**ELT vs ETL:**
| Approach | When to Use | Example |
|----------|------------|---------|
| ETL | Heavy transformations, limited warehouse compute | Legacy on-prem |
| ELT | Cloud warehouse (Snowflake/BigQuery), SQL transformations | Modern cloud |
| ELT advantage | Leverage warehouse compute, simpler pipelines | dbt transforms in warehouse |

**Testing strategy:**
1. **Unit tests:** Python function tests for transformations
2. **Integration tests:** Run pipeline on 1 day of data, compare source→target
3. **Regression tests:** Re-run on known dataset, compare output hashes
4. **Schema tests:** dbt generic tests (unique, not_null, relationships)
5. **Performance tests:** Load test with peak data volume


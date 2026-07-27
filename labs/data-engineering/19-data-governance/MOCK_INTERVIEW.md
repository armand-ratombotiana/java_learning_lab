# Mock Interview: Data Governance (19-data-governance)

## Scenario: GDPR right-to-deletion implementation
Your company stores user data across Snowflake, S3 data lake, Kafka topics, and Redshift. A user requests deletion under GDPR Article 17. You have 30 days to comply.

### Time: 45 minutes | Difficulty: Hard

---

## Part 1: Data Mapping (15 min)

**Finding user data across all systems:**

```python
def find_user_data(user_id: str) -> dict:
    """
    Search all systems for user data. Return inventory of all datasets.
    """
    results = {}

    # 1. Snowflake: query all schemas/tables for user_id
    snowflake_results = []
    schemas = snowflake_conn.execute("SHOW SCHEMAS IN ACCOUNT")
    for schema in schemas:
        tables = snowflake_conn.execute(f"SHOW TABLES IN {schema.name}")
        for table in tables:
            # Check if table has user_id column
            columns = snowflake_conn.execute(f"""
                SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = '{schema.name}'
                  AND TABLE_NAME = '{table.name}'
                  AND COLUMN_NAME IN ('user_id', 'customer_id', 'email', 'ssn')
            """)
            if columns:
                row = snowflake_conn.execute(f"""
                    SELECT COUNT(*) FROM {schema.name}.{table.name}
                    WHERE user_id = '{user_id}'
                """)
                if row[0][0] > 0:
                    snowflake_results.append({
                        'table': f"{schema.name}.{table.name}",
                        'columns': columns,
                        'row_count': row[0][0]
                    })
    results['snowflake'] = snowflake_results

    # 2. S3: search catalog for tables with user_id
    glue = boto3.client('glue')
    tables = glue.search_tables(SearchText=user_id)
    # ... similar search logic

    # 3. Kafka: check Kafka topics (latest value per key via compacted topics)
    kafka_results = check_kafka_topics_for_user(user_id)

    # 4. Redshift: similar to Snowflake
    redshift_results = check_redshift_for_user(user_id)

    return results
```

**Data inventory catalog schema:**
```sql
CREATE TABLE data_inventory (
    dataset_id VARCHAR PRIMARY KEY,
    system VARCHAR,          -- snowflake, s3, kafka, redshift
    location VARCHAR,        -- fully qualified name
    user_key_column VARCHAR, -- column containing user identifier
    pii_columns ARRAY,       -- list of PII columns
    retention_days INT,
    deletion_method VARCHAR, -- hard_delete, anonymize, mask, aggregate_only
    data_steward VARCHAR,
    gdpr_applicable BOOLEAN
);

-- For each user deletion request, query exists
SELECT location, user_key_column, deletion_method
FROM data_inventory
WHERE gdpr_applicable = TRUE;
```

---

## Part 2: Deletion Strategy (10 min)

**Deletion strategy per system:**

| System | Method | Implementation | Impact |
|--------|--------|---------------|--------|
| **Snowflake** | Hard delete | `DELETE FROM table WHERE user_id = ?` | Immediate, but Time Travel retains |
| **Snowflake (aggregates)** | Anonymize | `UPDATE table SET user_id = HASH(user_id)` | Aggregates preserved, ID removed |
| **S3 (Parquet)** | Rewrite files | Read Parquet, filter out user, rewrite without user data | Requires full file rewrite |
| **S3 (raw JSON)** | Delete files | Delete specific files containing user data | May lose other users' data in same file (need rewrite) |
| **Kafka (compacted)** | Tombstone | Produce NULL value for user key | Compaction removes old messages |
| **Kafka (log)** | No deletion (log is immutable) | Exclude user data in consumer | Set consumer filter |
| **Redshift** | Hard delete | `DELETE FROM table WHERE user_id = ?` + VACUUM | VACUUM for space reclamation |
| **BI dashboards** | No action | Aggregates only, no individual data | Verify dashboard doesn't leak individual info |

**Implementation example (Snowflake):**
```sql
-- Step 1: Hard delete from fact/transaction tables
DELETE FROM analytics.fact_orders
WHERE customer_id = 12345;

-- Step 2: Anonymize dimension tables
UPDATE curated.dim_customer
SET
    email = SHA2(email || 'salt', 256),     -- One-way hash
    phone = NULL,
    first_name = 'REDACTED',
    last_name = 'REDACTED',
    address = NULL,
    is_deleted = TRUE,
    deleted_at = CURRENT_TIMESTAMP()
WHERE customer_id = 12345;

-- Step 3: Verify deletion
SELECT COUNT(*) AS remaining_rows
FROM analytics.fact_orders
WHERE customer_id = 12345
UNION ALL
SELECT COUNT(*) FROM curated.dim_customer
WHERE customer_id = 12345 AND is_deleted = FALSE;
```

---

## Part 3: Cascading & Verification (10 min)

**Cascade deletion through pipeline:**
```
User requests deletion
    │
    ├── 1. Find all user data (across systems)
    │
    ├── 2. For each system, execute deletion strategy
    │   ├── Snowflake: DELETE + anonymize
    │   ├── S3 data lake: rewrite affected Parquet files
    │   ├── Kafka: produce tombstone messages
    │   └── Redshift: DELETE + VACUUM
    │
    ├── 3. Handle downstream dependencies
    │   ├── dbt models: re-run models that depend on deleted data
    │   ├── Materialized views: refresh to exclude deleted user
    │   ├── Dashboards: verify no PII leaks in filters/drill-downs
    │   └── ML features: exclude user from feature computation
    │
    ├── 4. Handle retention policy conflicts
    │   ├── GDPR: right to deletion overrides retention
    │   ├── Compliance: may need to keep audit logs (anonymized)
    │   └── Solution: anonymize audit logs (remove user_id, keep aggregated)
    │
    ├── 5. Verification
    │   ├── Query: confirm user data returns 0 rows
    │   ├── Lineage: check no downstream datasets still reference user
    │   └── Spot check: random sample of downstream tables
    │
    └── 6. Logging
        ├── deletion_id: UUID for audit trail
        ├── user_id: the deleted user
        ├── timestamp: when deletion occurred
        ├── systems: which systems were impacted
        └── verifier: who verified the deletion
```

**Verification query:**
```sql
-- Verify deletion across all tables
CREATE OR REPLACE TABLE deletion_verification AS
SELECT 'analytics.fact_orders' AS table_name,
       COUNT(*) AS remaining_rows
FROM analytics.fact_orders WHERE customer_id = 12345
UNION ALL
SELECT 'curated.dim_customer',
       COUNT(*) FROM curated.dim_customer
       WHERE customer_id = 12345 AND is_deleted = FALSE
UNION ALL
SELECT 'analytics.daily_revenue',
       COUNT(*) FROM analytics.daily_revenue
       WHERE customer_id = 12345;

-- If any remaining_rows > 0, deletion is incomplete
SELECT * FROM deletion_verification WHERE remaining_rows > 0;
```

---

## Part 4: PII Classification & Consent (10 min)

**Auto-classify PII across 1000+ tables:**

```python
class PIIClassifier:
    def __init__(self):
        self.pii_patterns = {
            'direct_identifier': [
                r'email', r'email_address', r'@.*\.',
                r'ssn', r'social_security', r'\d{3}-\d{2}-\d{4}',
                r'phone', r'phone_number', r'\+\d{1,2}\s?\d{10}',
                r'passport', r'driver.?license', r'credit.?card',
            ],
            'quasi_identifier': [
                r'first_name', r'last_name', r'full_name',
                r'birth_date', r'dob', r'date_of_birth',
                r'zip', r'postal_code', r'address',
                r'gender', r'birth_year',
            ],
            'sensitive': [
                r'diagnosis', r'medical', r'health',
                r'income', r'salary', r'credit_score',
                r'religion', r'political', r'sexual',
            ]
        }

    def classify_column(self, column_name: str, sample_data: list = None) -> dict:
        results = {'classification': 'non_pii', 'confidence': 0.0, 'evidence': []}

        # 1. Name-based classification
        for category, patterns in self.pii_patterns.items():
            for pattern in patterns:
                if re.search(pattern, column_name, re.IGNORECASE):
                    results['classification'] = category
                    results['confidence'] += 0.3
                    results['evidence'].append(f"Name match: {pattern}")

        # 2. Value-based classification (sample 1000 rows)
        if sample_data:
            for value in sample_data[:1000]:
                if value and re.match(r'\d{3}-\d{2}-\d{4}', str(value)):
                    results['classification'] = 'direct_identifier'
                    results['confidence'] = 0.95
                    results['evidence'].append("SSN format detected in values")
                    break

        # 3. Apply classification tag in catalog
        if results['confidence'] >= 0.5:
            catalog_api.tag_column(column_name, f"pii:{results['classification']}")

        return results
```

**Consent management system:**
```yaml
consent_record:
  user_id: 12345
  consent_version: "v2.1"
  consent_timestamp: "2024-01-15T10:00:00Z"
  
  purposes:
    - purpose: "marketing_emails"
      consented: true
      consented_at: "2024-01-15T10:00:00Z"
      revoked_at: null
      
    - purpose: "personalized_recommendations"
      consented: false
      consented_at: "2023-06-01T08:00:00Z"
      revoked_at: "2024-01-15T10:00:00Z"
      
    - purpose: "analytics"
      consented: true
      consented_at: "2024-01-15T10:00:00Z"
      revoked_at: null

# Integration with pipelines:
# - Check consent before including user in feature computation
# - Exclude user from marketing campaigns if consent revoked
# - Anonymize analytics data if analytics consent revoked
# - Audit: track consent changes for compliance
```

---

## Follow-up Questions

**Data retention vs right-to-deletion:**
| Scenario | Resolution |
|----------|-----------|
| Compliance requires 7 years, user requests deletion | Anonymize data (remove PII, keep aggregate) |
| Time travel retention keeps deleted data | Set `DATA_RETENTION_TIME_IN_DAYS = 0` after deletion |
| S3 versioning keeps old versions | Delete object versions, or set lifecycle policy |
| Kafka log compaction | Produce tombstone, wait for compaction, verify |

**Streaming (Kafka) deletion:**
- Produce tombstone message: `key=user_id, value=null`
- Log compaction removes old messages after cleanup policy
- Compacted topics: `cleanup.policy=compact`
- Config: `min.cleanable.dirty.ratio=0.5` (how often compaction runs)
- After tombstone: consumer won't see the user data
- But: old messages in log still exist until compaction runs
- For immediate removal: not possible with Kafka (log is immutable)

**Tools for PII discovery:**
| Tool | Open Source? | Approach | Best For |
|------|-------------|----------|----------|
| AWS Macie | No (AWS service) | ML + patterns | S3 data |
| BigID | No | ML + catalog | Enterprise discovery |
| Privacera | No | Policy-based | Cloud data platforms |
| Apache Atlas | Yes | Tag-based | Hadoop ecosystem |
| Sifflet | No | Column profiling | Data warehouse |
| Custom (pandas/regex) | Yes | Rule-based | Small to medium scale |


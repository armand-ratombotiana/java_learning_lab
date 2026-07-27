# Mock Interview: Data Catalog (16-data-catalog)

## Scenario: Build an internal data catalog
Your company has 10K+ datasets across Snowflake, S3, and Kafka topics. Data scientists can't find the right data. They spend 40% of time discovering and understanding data.

### Time: 45 minutes | Difficulty: Medium-Hard

---

## Part 1: Catalog Data Model (15 min)

**Core entities and relationships:**

```yaml
# Data catalog data model
entities:
  - Dataset:
      attributes:
        id: UUID
        name: STRING                  # human-readable name
        fully_qualified_name: STRING  # catalog.schema.table
        description: TEXT
        dataset_type: ENUM(table, view, topic, file, dashboard, ml_model)
        platform: ENUM(snowflake, s3, kafka, redshift, bigquery)
        schema_json: JSON            # column definitions
        tags: ARRAY[STRING]
        domain: STRING               # marketing, finance, product
        owner: STRING                # team or person
        created_at: TIMESTAMP
        updated_at: TIMESTAMP
        tier: ENUM(critical, important, standard, best_effort)
        row_count: BIGINT
        size_bytes: BIGINT
        freshness_seconds: BIGINT     # time since last update
        quality_score: FLOAT          # 0-100 composite score

  - Column:
      attributes:
        id: UUID
        dataset_id: UUID (FK)
        name: STRING
        data_type: STRING
        description: TEXT
        is_nullable: BOOLEAN
        is_primary_key: BOOLEAN
        is_foreign_key: BOOLEAN
        pii_classification: ENUM(none, direct, quasi, sensitive)
        nullable: BOOLEAN
        default_value: STRING
        tags: ARRAY[STRING]
        stats: JSON                   # min, max, distinct_count, null_count

  - Lineage:
      attributes:
        id: UUID
        source_dataset_id: UUID (FK)
        target_dataset_id: UUID (FK)
        transformation_type: ENUM(sql, spark, dbt, airflow, manual)
        transformation_code: TEXT
        created_at: TIMESTAMP
        updated_at: TIMESTAMP

  - DataQuality:
      attributes:
        id: UUID
        dataset_id: UUID (FK)
        check_name: STRING
        check_type: ENUM(freshness, completeness, uniqueness, accuracy)
        status: ENUM(pass, fail, warning)
        metric_value: FLOAT
        executed_at: TIMESTAMP
        threshold: FLOAT

  - Usage:
      attributes:
        user_id: STRING
        dataset_id: UUID (FK)
        query_count: INT
        last_accessed: TIMESTAMP
        access_type: ENUM(read, write, export, dashboard)

  - Tag:
      attributes:
        id: UUID
        name: STRING
        category: ENUM(domain, pii, quality, business, technical)
        description: TEXT

  - User:
      attributes:
        email: STRING (PK)
        display_name: STRING
        team: STRING
        role: ENUM(data_engineer, data_scientist, analyst, steward)
```

---

## Part 2: Discovery & Search (10 min)

**Search architecture:**

```
User Query → NLP Parser → Query Expansion → Ranking → Results

Example: "find customer revenue data for marketing"
├── Tokenize: [customer, revenue, marketing]
├── Expand synonyms: [client, income, sales, campaign]
├── Match against: dataset.name, column.name, description, tags
│   ├── Dataset: marketing.customer_360 (score: 0.95)
│   ├── Dataset: finance.daily_revenue (score: 0.85)
│   └── Column: sales.customer_revenue (score: 0.70)
└── Rank by: text similarity, usage frequency, quality score
```

**Search features:**
- Full-text search across name, description, tags, columns
- Faceted filters: domain, platform, tier, owner, tag
- Auto-complete: suggest datasets as user types
- Popular datasets: ranked by query count and unique users
- Related datasets: "users who viewed this also viewed..."

**Discovery SQL query example:**
```sql
-- Find datasets matching search terms
SELECT d.name, d.description, d.domain, d.quality_score,
  u.query_count, u.last_accessed
FROM datasets d
LEFT JOIN (
  SELECT dataset_id, COUNT(*) AS query_count, MAX(last_accessed) AS last_accessed
  FROM usage_log GROUP BY dataset_id
) u ON d.id = u.dataset_id
WHERE d.is_deleted = FALSE
  AND (
    d.name ILIKE '%customer%' OR d.description ILIKE '%customer%'
    OR EXISTS (SELECT 1 FROM columns c WHERE c.dataset_id = d.id AND c.name ILIKE '%customer%')
    OR d.domain = 'marketing'
  )
ORDER BY d.quality_score DESC, u.query_count DESC
LIMIT 20;
```

---

## Part 3: Lineage & Integration (10 min)

**Lineage capture architecture:**

```
Sources:
├── dbt: manifest.json (model dependencies, column-level lineage)
├── Airflow: OpenLineage events (DAG runs, task inputs/outputs)
├── Spark: SparkListener events (DataFrame lineage)
├── Snowflake: ACCESS_HISTORY view (query-level lineage)
├── Tableau/Looker: API (dashboard source tables)
└── Manual: API for custom lineage (UI or CLI)

OpenLineage event → Kafka → Marquez/DataHub → Catalog API
```

**Lineage API integration:**
```python
# Emit lineage from dbt model
from openlineage.client import OpenLineageClient
from openlineage.client.run import RunEvent, RunState, Run, Job, Dataset

client = OpenLineageClient(url="http://marquez:5000/api/v1/lineage")

def emit_dbt_lineage(model_name, source_tables, target_table, run_id):
    event = RunEvent(
        eventType=RunState.COMPLETE,
        eventTime=datetime.now(),
        run=Run(runId=run_id),
        job=Job(namespace="dbt", name=model_name),
        inputs=[
            Dataset(namespace="snowflake://analytics", name=src)
            for src in source_tables
        ],
        outputs=[
            Dataset(namespace="snowflake://analytics", name=target_table)
        ],
        producer="dbt"
    )
    client.emit(event)
```

**Automated metadata extraction:**
```python
# Extract metadata from Snowflake
def extract_snowflake_metadata():
    query = """
        SELECT TABLE_CATALOG, TABLE_SCHEMA, TABLE_NAME,
               COLUMN_NAME, DATA_TYPE, IS_NULLABLE
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA NOT IN ('INFORMATION_SCHEMA')
    """
    metadata_df = snowflake_connector.execute(query)

    for row in metadata_df:
        catalog_api.upsert_dataset(
            name=f"{row.TABLE_CATALOG}.{row.TABLE_SCHEMA}.{row.TABLE_NAME}",
            columns=[{"name": row.COLUMN_NAME, "type": row.DATA_TYPE}]
        )

# Extract from S3 (Glue Data Catalog)
def extract_s3_metadata():
    glue = boto3.client('glue')
    tables = glue.get_tables(DatabaseName='analytics_db')

    for table in tables['TableList']:
        catalog_api.upsert_dataset(
            name=f"s3://data-lake/{table['Name']}",
            schema_json=table['StorageDescriptor']['Columns'],
            row_count=table.get('Parameters', {}).get('numRows'),
            platform='s3'
        )
```

---

## Part 4: Collaboration & Adoption (10 min)

**Collaboration features:**

```yaml
# Dataset detail page
dataset: marketing.customer_360
  description: "Unified customer profile from CRM, web, and support"
  
  # Tabs:
  1. Overview: schema, size, freshness, quality score
  2. Lineage: upstream sources → this dataset → downstream consumers
  3. Quality: recent test results, trend chart
  4. Usage: top queries, frequent consumers, access patterns
  5. Discussions: user comments, questions, answers
  6. Documentation: markdown guides, examples
  
  # Actions:
  - Bookmark: save to personal collection
  - Subscribe: get alerts on schema changes, quality issues
  - Request access: one-click access request to data steward
  - Export: download sample data
  - Report issue: flag data quality problem
```

**Measuring adoption and success:**

| KPI | Measurement | Current | Target |
|-----|-------------|---------|--------|
| Catalog coverage | % of datasets with complete metadata | 45% | > 90% |
| Search success rate | % of searches that result in a click | 60% | > 80% |
| Active users | Monthly active unique users | 50 | > 200 |
| Time to discovery | Average time to find a dataset | 15 min | < 2 min |
| Data quality score | Avg quality score across datasets | 72% | > 90% |
| Annotations | % of datasets with descriptions | 30% | > 80% |
| Lineage coverage | % of critical datasets with lineage | 25% | > 95% |

**Tool selection: DataHub vs Amundsen vs Atlan vs Collibra:**

| Criteria | DataHub | Amundsen | Atlan | Collibra |
|----------|---------|----------|-------|----------|
| Open source | Yes | Yes | No | No |
| Column-level lineage | Yes | Limited | Yes | Yes |
| ML/AI features | Yes (ML models, features) | Limited | Yes | Yes |
| Search | Excellent (elasticsearch) | Good | Excellent | Good |
| Setup complexity | Medium | High | Low | Low |
| Cost | Free (self-managed) | Free | Paid | Expensive |
| Best for | Tech companies, open-source lovers | Startups | Mid-market | Enterprise |

---

## Follow-up Questions

**Metadata extraction scheduling:**
| Source | Frequency | Method |
|--------|-----------|--------|
| Snowflake | Every 6 hours | INFORMATION_SCHEMA polling |
| S3 (Glue) | Every 12 hours | Glue crawler / API |
| Kafka | Every hour | Schema Registry API |
| dbt | On every run | Parse manifest.json (airflow callback) |
| Airflow | On every DAG run | OpenLineage events |
| Looker | Daily | Looker API |

**Data catalog anti-patterns:**
- Waiting for perfect metadata (start with 80% and iterate)
- Not integrating with data quality (catalog must show quality)
- No owner information (datasets without stewards become stale)
- No usage tracking (can't tell which datasets are important)
- Ignoring PII classification (compliance risk)


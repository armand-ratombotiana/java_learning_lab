# Mock Interview: Change Data Capture (10-change-data-capture)

## Scenario: CDC from Postgres to Snowflake
Your company needs real-time CDC from a PostgreSQL operational database to Snowflake for analytics. The database has 200 tables, handles 5K transactions/sec, peak 20K/sec.

### Time: 45 minutes | Difficulty: Hard

---

## Part 1: CDC Method Selection (15 min)

**Comparison: CDC approaches**

| Method | Latency | DB Impact | Schema Support | Complexity | Best For |
|--------|---------|-----------|----------------|------------|----------|
| **Debezium** (log-based) | < 1 sec | Minimal (0-5% overhead) | Automatic | Medium | Production, real-time needs |
| **Native logical replication** | < 1 sec | Minimal | Requires setup | Low | Simple PostgreSQL → PostgreSQL |
| **Timestamp-based polling** | 30 sec - 5 min | Moderate (SELECT load) | Manual mapping | Low | Dev/test, low volume |
| **Full table comparison** | 1+ hours | High (full table scan) | Manual | Low | Small tables, daily sync |

**Recommendation: Debezium with Kafka**
```
PostgreSQL WAL → Debezium Connector → Kafka (Avro) → Snowflake Connector
```
- Logical replication slot on PostgreSQL (minimal overhead)
- Debezium captures INSERT/UPDATE/DELETE from WAL
- Kafka for buffering, durability, and replay
- Snowflake Kafka Connector for loading

**Debezium configuration:**
```json
{
  "name": "postgres-cdc-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgres-primary",
    "database.port": "5432",
    "database.user": "debezium",
    "database.password": "***",
    "database.dbname": "orders_db",
    "database.server.name": "orders",
    "plugin.name": "pgoutput",
    "slot.name": "debezium_slot",
    "publication.name": "debezium_pub",
    "publication.autocreate.mode": "filtered",
    "table.include.list": "public.orders,public.customers,public.products",
    "tombstones.on.delete": "false",
    "key.converter": "io.confluent.connect.avro.AvroConverter",
    "value.converter": "io.confluent.connect.avro.AvroConverter",
    "decimal.handling.mode": "double",
    "interval.handling.mode": "string",
    "schema.history.internal": "io.confluent.connect.storage.kafka.KafkaSchemaHistory"
  }
}
```

---

## Part 2: Pipeline Design (10 min)

**End-to-end CDC pipeline:**

```
PostgreSQL WAL → Debezium → Kafka (avro topic: orders.public.orders)
                                │
                                ├── Snowflake Connector (distributed mode, 8 tasks)
                                │     └── Loads to staging tables (orders_staging)
                                │
                                ├── Flink (optional enrichment)
                                │     └── dedup, type casting, enrichment
                                │
                                └── Schema Registry (Avro schema evolution)
```

**Kafka topic design:**
| Topic | Partitions | Retention | Cleanup |
|-------|-----------|-----------|---------|
| `orders.public.orders` | 16 | 7 days | delete |
| `orders.public.customers` | 8 | 3 days | compact |
| `orders.public.products` | 4 | 3 days | compact |
| `orders.public.${table}` | 8 | 7 days | delete |

**Snowflake Kafka Connector configuration:**
```sql
CREATE OR REPLACE PIPE orders_cdc_pipe
  AUTO_INGEST = TRUE
  AWS_SNS_TOPIC = 'arn:aws:sns:us-east-1:123456:orders-cdc'
AS
  COPY INTO orders_staging
  FROM @cdc_stage/orders/
  FILE_FORMAT = (TYPE = AVRO, NULL_IF = ('', 'NULL'));
```

**Snowflake MERGE for CDC:**
```sql
MERGE INTO dim_customer t
USING (
  SELECT
    after:customer_id::INT AS customer_id,
    after:name::VARCHAR AS name,
    after:email::VARCHAR AS email,
    after:updated_at::TIMESTAMP AS updated_at,
    op
  FROM cdc_events
  WHERE source_table = 'customers'
    AND ts_ms > (SELECT last_lsn FROM cdc_watermark)
) s
ON t.customer_id = s.customer_id
WHEN MATCHED AND s.op = 'd' THEN
  UPDATE SET is_deleted = TRUE, deleted_at = CURRENT_TIMESTAMP()
WHEN MATCHED AND s.op IN ('u', 'c') 
  AND s.updated_at > t.last_modified THEN
  UPDATE SET name = s.name, email = s.email, last_modified = s.updated_at
WHEN NOT MATCHED AND s.op IN ('c', 'u') THEN
  INSERT (customer_id, name, email, last_modified)
  VALUES (s.customer_id, s.name, s.email, s.updated_at);
```

---

## Part 3: Schema Evolution (10 min)

**Schema drift handling strategy:**

| Change Type | Debezium Behavior | Pipeline Response |
|------------|-------------------|-------------------|
| **Column added** | New field appears in `after` | Auto-detect, add to staging as NULL-able |
| **Column dropped** | Field missing in `after` | Set to NULL in staging, update schema map |
| **Column renamed** | Debezium sees as drop + add | Requires manual mapping table |
| **Type change** | Avro may auto-convert | Validate compatibility, alert if breaking |
| **Table dropped** | Connector fails on missing table | Alert, stop connector, manual intervention |

**Schema evolution automation:**
```python
def autodetect_schema_drift(cdc_event, expected_schema):
    actual_columns = set(cdc_event['after'].keys()) if cdc_event['after'] else set()
    expected_columns = set(expected_schema.keys())

    new_columns = actual_columns - expected_columns
    missing_columns = expected_columns - actual_columns

    if new_columns:
        for col in new_columns:
            inferred_type = infer_type(cdc_event['after'][col])
            alter_target_table(col, inferred_type)
            update_schema_registry(col, inferred_type)
        notify(f"Schema evolved: +{new_columns}")

    if missing_columns:
        notify(f"Schema drift: -{missing_columns}")
        # Set to NULL in transformation, alert for review
```

**Schema Registry (Avro) compatibility:**
```json
{
  "compatibility": "BACKWARD",  // New schema can read old data
  "evolution": {
    "allow.forward.evolution": true,
    "allow.backward.evolution": true
  }
}
```

---

## Part 4: Exactly-Once & Monitoring (10 min)

**Exactly-once strategy:**

1. **Source:** Debezium commits LSN after Kafka produces message (Kafka Connect exactly-once)
2. **Kafka:** Idempotent producer + acks=all (no duplicates within session)
3. **Snowflake:** MERGE with dedup by source_key + LSN (idempotent)
4. **Recovery:** On failure, Snowflake task reads from last processed LSN

**Snowflake watermark tracking:**
```sql
CREATE TABLE cdc_watermark (
  source_table VARCHAR,
  last_lsn BIGINT,          -- PostgreSQL WAL position
  last_ts_ms TIMESTAMP,     -- Event timestamp
  row_count INT,            -- Rows processed in last batch
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
);
```

**Monitoring:**
```sql
-- Replication lag (most important metric)
SELECT source_table,
  DATEDIFF('second', MAX(last_ts_ms), CURRENT_TIMESTAMP) AS lag_seconds
FROM cdc_watermark
GROUP BY source_table;

-- WAL retention (too much lag = WAL files not cleaned up)
SELECT slot_name, active_pid, 
  pg_size_pretty(pg_wal_lsn_diff(
    pg_current_wal_lsn(), restart_lsn)) AS wal_behind
FROM pg_replication_slots;
```

**Lag dashboard (Grafana):**
1. **Replication lag per table:** seconds since last CDC event loaded to Snowflake
2. **Events per second:** Kafka topic throughput (msgs/sec)
3. **Consumer lag:** Kafka consumer group lag (messages behind latest)
4. **WAL retention:** PostgreSQL WAL files not yet consumed
5. **Error rate:** Dead letter queue size (failed CDC events)
6. **Schema drift events:** count of auto-detected schema changes per week

**Alert thresholds:**
- Warning: lag > 60 seconds, WAL size > 10GB
- Critical: lag > 5 minutes, consumer lag > 100K messages
- P1: Debezium connector stopped, WAL disk full risk

---

## Follow-up Questions

**Kafka cluster outage recovery:**
1. Debezium stops, PostgreSQL WAL accumulates (monitor WAL size!)
2. Kafka comes back: Debezium resumes from last committed offset
3. Backlog: WAL events have piled up, Debezium catches up at max throughput
4. Snowflake: backlog of CDC events in Kafka consumer group
5. Catch-up: increase Snowflake Connector tasks, larger warehouse for MERGE
6. Risk: if WAL is deleted due to retention, need full snapshot + catch-up

**Large transactions (millions of rows):**
- Debezium emits one event per row change (not one event per transaction)
- Large transactions create a burst of events
- Kafka: ensure sufficient partitions and throughput
- Snowflake: batch MERGE instead of per-row MERGE
- Consider: Debezium heartbeat for tracking progress

**Log-based CDC vs timestamp-based:**
| Criteria | Log-based (Debezium) | Timestamp-based |
|----------|---------------------|-----------------|
| Latency | Sub-second | 30s - 5min |
| DELETE capture | Yes | No (requires soft delete) |
| DB impact | Minimal (WAL read) | SELECT queries on source |
| Old value capture | Yes (before) | No |
| Schema changes | Automatic detection | Manual DDL tracking |
| Complexity | Medium-high | Low |
| Best for | Production, critical data | Dev/test, non-critical |


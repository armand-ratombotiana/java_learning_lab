#!/usr/bin/env python3
"""Template-based content generator for labs 09-12."""
# Each lab defines parameters; the template system expands them into 100+ line docs.

def make_docs(lab):
    """Expand templates for all 24 doc files."""
    t = lab["title"]
    s = lab["short"]
    concepts = "\n".join(f"- **{c[0]}**: {c[1]}" for c in lab["concepts"])
    objectives = "\n".join(f"{i+1}. {o}" for i, o in enumerate(lab["objectives"]))
    exercises = "\n".join(f"{i+1}. {e}" for i, e in enumerate(lab["exercises"]))
    qa_items = lab["quiz_qa"]
    quiz = "\n".join(f"{i+1}. {q}\n   A) {a[0]}\n   B) {a[1]}\n   C) {a[2]}\n   D) {a[3]}" for i,(q,a) in enumerate(qa_items))
    ans = "\n".join(f"{i+1}: {a}" for i,a in enumerate(lab["quiz_answers"]))
    flashcards = "\n".join(f"## Card {i+1}\n**Front**: {f}\n**Back**: {b}\n" for i,(f,b) in enumerate(lab["flashcards"]))
    interview = "\n".join(f"### {l}\n**Q**: {q}\n**A**: {a}\n" for l,q,a in lab["interview"])
    reflection = "\n".join(f"- {r}" for r in lab["reflection"])
    references = "\n".join(f"- {r}" for r in lab["references"])
    history_timeline = "\n".join(f"- **{y}**: {e}" for y,e in lab["history"])
    mental_models = "\n".join(f"## {i+1}. {m[0]}\n{m[1]}\n" for i,m in enumerate(lab["mental_models"]))
    mistakes = "\n".join(f"{i+1}. {m[0]}: {m[1]}" for i,m in enumerate(lab["common_mistakes"]))
    debugging = "\n".join(f"## {d[0]}\n{d[1]}\n" for d in lab["debugging"])
    refactoring = lab.get("refactoring", "N/A")
    performance = "\n".join(f"## {p[0]}\n{p[1]}\n" for p in lab["performance"])
    security = lab["security"]
    architecture = lab["architecture"]
    step_by_step = "\n".join(f"{i+1}. {s}" for i,s in enumerate(lab["step_by_step"]))
    theory_sections = "\n".join(f"## {ts[0]}\n{ts[1]}\n" for ts in lab["theory_sections"])
    internals = "\n".join(f"## {ins[0]}\n{ins[1]}\n" for ins in lab["internals_sections"])
    math_sections = "\n".join(f"## {ms[0]}\n```\n{ms[1]}\n```\n" for ms in lab["math"])
    visual = lab["visual"]
    code_dive = lab["code_dive"]
    how_it_works = lab["how_it_works"]
    why_exists = lab["why_exists"]
    why_matters = lab["why_matters"]

    return {
        "README": f"# {t}\n\n## Overview\n{lab['overview']}\n\n## Key Concepts\n{concepts}\n\n## Learning Objectives\n{objectives}",
        "THEORY": f"# {t} Theory\n\n{theory_sections}",
        "WHY_IT_EXISTS": f"# Why {t} Exists\n\n{why_exists}",
        "WHY_IT_MATTERS": f"# Why {t} Matters\n\n{why_matters}",
        "HISTORY": f"# History of {t}\n\n## Timeline\n{history_timeline}",
        "MENTAL_MODELS": f"# Mental Models for {t}\n\n{mental_models}",
        "HOW_IT_WORKS": f"# How {t} Works\n\n{how_it_works}",
        "INTERNALS": f"# {t} Internals\n\n{internals}",
        "MATH_FOUNDATION": f"# Math Foundation for {t}\n\n{math_sections}",
        "VISUAL_GUIDE": f"# Visual Guide to {t}\n\n{visual}",
        "CODE_DEEP_DIVE": f"# Code Deep Dive: {t}\n\n{code_dive}",
        "STEP_BY_STEP": f"# Step-by-Step: Working with {t}\n\n{step_by_step}",
        "COMMON_MISTAKES": f"# Common Mistakes with {t}\n\n{mistakes}",
        "DEBUGGING": f"# Debugging {t}\n\n{debugging}",
        "REFACTORING": f"# Refactoring {t} Pipelines\n\n{refactoring}",
        "PERFORMANCE": f"# Performance Optimization for {t}\n\n{performance}",
        "SECURITY": f"# Security for {t}\n\n{security}",
        "ARCHITECTURE": f"# {t} Architecture\n\n{architecture}",
        "EXERCISES": f"# Exercises for {t}\n\n{exercises}",
        "QUIZ": f"# Quiz: {t}\n\n{quiz}\n\n## Answer Key\n{ans}",
        "FLASHCARDS": f"# Flashcards: {t}\n\n{flashcards}",
        "INTERVIEW": f"# Interview Questions: {t}\n\n{interview}",
        "REFLECTION": f"# Reflection: {t}\n\n{reflection}",
        "REFERENCES": f"# References for {t}\n\n{references}",
    }

LABS = {}

# ===== LAB 09: SNOWFLAKE =====
l09 = {
    "title": "Snowflake Data Cloud",
    "short": "Snowflake",
    "overview": "Snowflake is a cloud-native data platform that separates storage and compute, enabling elastic scaling, concurrent workloads, and zero-copy cloning. This lab covers Snowflake's unique architecture, virtual warehouses, clustering, time travel, and data sharing.",
    "concepts": [
        ("Storage-Compute Separation", "Independent scaling of storage and compute resources"),
        ("Virtual Warehouses", "Elastic compute clusters for query execution"),
        ("Micro-Partitioning", "Automatic columnar storage and pruning (50-500 MB per partition)"),
        ("Time Travel", "Query and restore historical data with configurable retention (1-90 days)"),
        ("Zero-Copy Cloning", "Instant copy-on-write clones without additional storage cost"),
        ("Data Sharing", "Secure sharing across Snowflake accounts without data movement"),
    ],
    "objectives": [
        "Understand Snowflake's three-layer cloud-agnostic architecture",
        "Configure and manage virtual warehouses of varying sizes",
        "Implement clustering keys for query optimization on large tables",
        "Use Time Travel for data recovery, auditing, and point-in-time analysis",
        "Leverage Zero-Copy Cloning for dev/test environments",
        "Share data securely across Snowflake accounts using Reader Accounts",
    ],
    "exercises": [
        "Create warehouses of sizes X-Small through Large; run queries on each and compare latency and credit usage",
        "Set up Snowpipe for auto-ingestion from cloud storage; monitor pipe status and error handling",
        "Analyze clustering depth on a large table; design optimal clustering keys based on query patterns",
        "Simulate accidental data deletion; recover using Time Travel AT/BEFORE; verify data integrity",
        "Clone a production database for testing; run ETL transformations; compare storage costs before and after",
        "Create a share, add reader accounts, grant permissions, and verify cross-account data access",
    ],
    "quiz_qa": [
        ("What separates Snowflake from traditional data warehouses?", ["In-memory processing", "Storage-compute separation", "Columnar storage", "SQL support"]),
        ("What is the smallest virtual warehouse size?", ["Nano", "X-Small", "Small", "Micro"]),
        ("How long is Time Travel retention for Enterprise edition?", ["1 day", "7 days", "30 days", "90 days"]),
        ("What is a micro-partition's typical uncompressed size?", ["1-10 MB", "10-50 MB", "50-500 MB", "500 MB-1 GB"]),
        ("What does Fail-safe provide?", ["User-accessible time travel", "Snowflake-managed recovery", "Real-time replication", "Automatic scaling"]),
    ],
    "quiz_answers": ["B", "B", "D", "C", "B"],
    "flashcards": [
        ("What is a virtual warehouse?", "Elastic compute cluster that executes queries independently; can be suspended/resumed"),
        ("What are micro-partitions?", "Automatic 50-500 MB columnar storage units with embedded metadata statistics for pruning"),
        ("How does zero-copy cloning work?", "Creates metadata-only snapshot; copy-on-write for new data; instant regardless of table size"),
        ("What is the difference between Time Travel and Fail-safe?", "Time Travel: user-accessible (1-90 days); Fail-safe: Snowflake-managed recovery only (7 days)"),
        ("What is clustering depth?", "Average number of overlapping micro-partitions for a given range; lower = better pruning"),
    ],
    "interview": [
        ("Architecture", "Explain Snowflake's three-layer architecture.", "Storage (compressed columnar in cloud blob storage), Compute (virtual warehouses as elastic clusters), Services (authentication, metadata management, query optimization)"),
        ("Performance", "How would you optimize a slow query in Snowflake?", "Check pruning efficiency via Query Profile, analyze clustering depth, verify warehouse sizing, review joins and filters"),
        ("Cost", "How do you optimize Snowflake costs?", "Set AUTO_SUSPEND on warehouses, match warehouse size to workload, use multi-cluster only when needed, separate ETL from reporting warehouses"),
        ("Advanced", "Difference between transient and permanent tables?", "Transient: no Fail-safe, optional Time Travel; Permanent: full Time Travel + Fail-safe; both support zero-copy cloning"),
    ],
    "reflection": [
        "Storage-compute separation is Snowflake's revolutionary feature enabling elastic scaling and cost efficiency",
        "Zero-copy cloning fundamentally changes dev/test workflows by eliminating data duplication",
        "Time Travel provides a safety net but requires cost governance on high-churn tables",
        "Clustering is essential for large tables but must be monitored and periodically maintained",
        "Auto-suspend is the single most impactful cost optimization for development environments",
    ],
    "references": [
        "Snowflake Documentation: https://docs.snowflake.com/",
        "Snowflake JDBC Driver: https://docs.snowflake.com/en/developer-guide/jdbc",
        "Snowflake SQL Reference: https://docs.snowflake.com/en/sql-reference",
        "Zero-Copy Cloning Guide: https://docs.snowflake.com/en/user-guide/object-clone",
        "Time Travel: https://docs.snowflake.com/en/user-guide/data-time-travel",
        "Clustering Keys: https://docs.snowflake.com/en/user-guide/tables-clustering-keys",
    ],
    "history": [
        ("2012", "Snowflake founded by Benoit Dageville, Thierry Cruanes, and Marcin Zukowski"),
        ("2014", "First public beta with cloud-native architecture (storage-compute separation)"),
        ("2015", "General availability on AWS"),
        ("2018", "Available on Microsoft Azure"),
        ("2019", "Available on GCP; Snowflake Data Marketplace launched"),
        ("2020", "IPO (NYSE: SNOW) — largest software IPO at the time"),
        ("2021", "Unistore (transactional + analytical workloads), Snowpark"),
        ("2022", "Dynamic tables, Streams/Tasks enhancements"),
        ("2023", "Cortex AI, Snowpark ML, container services"),
        ("2024", "Arctic open-source LLM, Data Cloud enhancements"),
    ],
    "mental_models": [
        ("The Elastic Workforce", "Virtual warehouses are like hiring temporary workers. Scale up (hire more) for big jobs, suspend (release) when idle. You only pay for time worked."),
        ("The Library Archive", "Micro-partitions are like books on shelves. The catalog (metadata) tells you which shelf has the books you need (pruning). No need to search the entire library."),
        ("The Git Analogy", "Time Travel = git log (view history). Zero-Copy Clone = git branch (instant branch). Undrop = git reflog (recover deleted). Each version is a commit."),
    ],
    "theory_sections": [
        ("Architecture Layers", "Snowflake's three-layer architecture: Storage Layer (compressed columnar data in S3/Azure/GCS with micro-partitioning), Compute Layer (virtual warehouses as elastic clusters of VMs), and Services Layer (authentication, metadata, optimizer, security). Each layer scales independently."),
        ("Virtual Warehouse Sizing", "X-Small (1 credit/hr) through 5X-Large (256 credits/hr). Larger warehouses have more memory and CPU for complex queries. Multi-cluster warehouses auto-scale horizontally for concurrency."),
        ("Micro-Partitioning", "Data automatically divided into 50-500 MB micro-partitions. Columnar storage within partitions. Automatic metadata collection (min, max, null count, distinct count) enables pruning — eliminating irrelevant partitions at query time without manual indexing."),
        ("Time Travel & Fail-safe", "Standard: 1 day Time Travel. Enterprise: 90 days. Fail-safe: additional 7 days (Snowflake-managed recovery only). AT/BEFORE syntax for point-in-time queries. UNDROP for table recovery."),
        ("Zero-Copy Cloning", "Creates metadata-only snapshot pointing to same storage fragments. Copy-on-write: only new/modified data consumes additional storage. Metadata-only operation completes in seconds regardless of table size."),
    ],
    "internals_sections": [
        ("Query Execution Flow", "1. Client submits SQL to Services Layer. 2. Parser builds AST. 3. Optimizer generates plan using metadata statistics. 4. Planner prunes micro-partitions. 5. Scheduler assigns work. 6. Nodes scan partitions in columnar format. 7. Results shuffled/aggregated. 8. Final result returned."),
        ("Micro-Partition Metadata", "Each micro-partition stores: column min/max values (for pruning), column null counts, column distinct counts, compressed/uncompressed storage size, row count. This metadata is automatically maintained and updated."),
        ("Clustering Algorithm", "CLUSTER BY defines keys. Greedy algorithm minimizes overlap. Clustering depth and ratio monitored automatically. Background reclustering maintains optimal layout. Can be scheduled or triggered manually."),
        ("Concurrency Model", "Virtual warehouses are fully isolated — no resource contention between warehouses. Multi-cluster warehouses add clusters when queries queue up. Queued queries trigger cluster creation within 2-5 minutes."),
    ],
    "math": [
        ("Storage Estimation", "RawSize = SUM(row_count * avg_column_size)\nCompressedSize = RawSize / CompressionRatio (2-10x)\nTotalStorage = SUM(all micro-partition compressed sizes)"),
        ("Time Travel Cost", "TT_Storage = DailyChangeRate * RetentionDays * CompressedSize\nExample: 10% daily change on 1TB table with 90-day retention = 9TB additional storage"),
        ("Clustering Depth", "Depth = AVG(overlapping partitions for a given range)\nWell-clustered: 1-4\nUnclustered: 10+\nLower depth = better pruning = faster queries"),
        ("Credit Usage", "TotalCredits = SUM(warehouse_credits) + MAX(0, cloud_services - 0.1*warehouse_credits)\nCloud services credits are free up to 10% of warehouse credits"),
    ],
    "visual": "```\n+------------------------------------------------+\n|                 Services Layer                   |\n|  [Auth] [Metadata] [Optimizer] [Security]        |\n+------------------------+------------------------+\n                         |\n+------------------------v------------------------+\n|               Compute Layer                      |\n|  [WH_1: X-Large]    [WH_2: Medium]             |\n|  [Reporting]         [ETL Jobs]                 |\n|  [Multi-cluster: 3]  [Single-cluster]          |\n+------------------------+------------------------+\n                         |\n+------------------------v------------------------+\n|               Storage Layer                      |\n|  [Micro-partitions] [Columnar Format]           |\n|  [S3 / Azure Blob / GCS] [Encryption at rest]   |\n+------------------------------------------------+\n\nTime Travel Timeline:\nNow-90d         Now-1d          NOW\n[Fail-safe:7d]  [Time Travel]   [Active]\n  |                  |              |\nOnly Snowflake    Query any       Current\ncan restore       point in past   data\n",
    "code_dive": "See Java source files in src/main/java/com/dataeng/nine/ for complete implementations:\n\n- SnowflakeConnector.java: JDBC connection management, query execution, warehouse CRUD\n- WarehouseManager.java: Virtual warehouse lifecycle (create, resize, suspend, resume, list)\n- ClusteringManager.java: Clustering key management, depth analysis, reclustering\n\nKey patterns:\n```java\n// Connection\nProperties props = new Properties();\nprops.put(\"user\", user); props.put(\"password\", password);\nprops.put(\"warehouse\", warehouse);\nConnection conn = DriverManager.getConnection(\n    \"jdbc:snowflake://account.snowflakecomputing.com\", props);\n\n// Time Travel\nStatement stmt = conn.createStatement();\nstmt.execute(\"SELECT * FROM table AT (TIMESTAMP => '2024-01-01'::TIMESTAMP)\");\n\n// Zero-Copy Clone\nstmt.execute(\"CREATE TABLE clone_table CLONE source_table\");\n```",
    "step_by_step": [
        "Sign up for Snowflake trial; note account identifier (e.g., xy12345.us-east-1)",
        "Download Snowflake JDBC driver (snowflake-jdbc-x.x.x.jar)",
        "Configure connection properties: account, user, password, warehouse, database, schema",
        "Create virtual warehouse: CREATE WAREHOUSE dev_wh WITH WAREHOUSE_SIZE='X-SMALL' AUTO_SUSPEND=300 AUTO_RESUME=TRUE",
        "Load data: CREATE STAGE, CREATE FILE FORMAT, COPY INTO table FROM @stage",
        "Define clustering key: ALTER TABLE orders CLUSTER BY (order_date, customer_id)",
        "Query with Time Travel: SELECT * FROM orders AT (TIMESTAMP => 'timestamp'::TIMESTAMP)",
        "Clone for testing: CREATE DATABASE dev_sales CLONE sales_db",
        "Share data: CREATE SHARE, GRANT SELECT TO SHARE, ALTER SHARE SET ACCOUNTS = partner",
    ],
    "common_mistakes": [
        ("Warehouse Overprovisioning", "Using X-Large for simple queries; start with X-Small and scale based on actual needs"),
        ("No Auto-Suspend", "Warehouse runs 24/7 burning credits; set AUTO_SUSPEND = 300 seconds for dev environments"),
        ("Ignoring Clustering", "Large tables without clustering keys cause full scans; monitor clustering depth for >1TB tables"),
        ("Overusing Time Travel", "90-day retention on high-churn tables incurs significant costs; use shorter retention for transient data"),
        ("No Zero-Copy Clone for Testing", "Testing schema changes on production risks data loss; use CLONE for safe testing"),
    ],
    "debugging": [
        ("Warehouse Congestion", "Check TABLE(INFORMATION_SCHEMA.WAREHOUSE_LOAD_HISTORY) to see queue depth; add multi-cluster or resize"),
        ("Query Performance", "Use Snowsight Query Profile to view operator-level times, bytes scanned vs returned, pruning efficiency"),
        ("Pruning Inefficiency", "Calculate 1 - (PARTITIONS_SCANNED / PARTITIONS_TOTAL); low values indicate poor clustering or missing filters"),
        ("Permission Errors", "Verify role grants with SHOW GRANTS TO ROLE; check network policies and IP restrictions"),
    ],
    "refactoring": "## From Batch to Continuous\nBefore: Daily COPY INTO jobs with cron scheduling\nAfter: Snowpipe AUTO_INGEST=TRUE for real-time ingestion\n\n## Query Optimization\nBefore: SELECT * FROM orders WHERE YEAR(order_date) = 2024 (full scan)\nAfter: WHERE order_date >= '2024-01-01' AND order_date < '2025-01-01' (pruning)\n\n## Materialized Views\nBefore: Repeated aggregations scanning source table\nAfter: CREATE MATERIALIZED VIEW with automatic incremental refresh",
    "performance": [
        ("Warehouse Tuning", "Multi-cluster warehouses for high concurrency (MIN=1, MAX=10). Separate warehouses for ETL (Large) vs reporting (Medium). Scale down when not needed."),
        ("Clustering", "High-cardinality columns first. Composite keys like (date, customer_id). Monitor depth weekly. Avoid low-cardinality columns (booleans)."),
        ("Query Optimization", "SELECT only needed columns. Use filters on clustered columns. Limit ORDER BY on large results. Avoid Cartesian joins."),
        ("Data Loading", "Parquet format for best compression. Batch files to 100-500 MB each. Snowpipe for continuous loading. PURGE=TRUE after successful copy."),
    ],
    "security": "## Authentication\n- MFA for user accounts\n- Key-pair auth for automated processes\n- SCIM provisioning for SSO\n- OAuth for applications\n\n## Authorization\n- RBAC with custom roles\n- Network policies for IP restriction\n- Row-level security via secure views\n- Column-level masking policies for PII\n\n## Data Protection\n- Encryption at rest (AES-256) and in transit (TLS 1.2+)\n- Tri-Secret Secure for key management\n- Audit logs for all queries and access",
    "architecture": "## Production Architecture\n```\n[BI Tools] -> [Reporting_WH]    [ETL_WH]    [DS_WH]\n                |                    |           |\n           [Cloud Services: Auth, Metadata, Optimizer]\n                         |\n                    [Storage Layer]\n              [Raw -> Conformed -> Aggregated]\n              [Micro-partitions, Columnar, Encrypted]\n```\n\n## Pipeline Pattern\nSources -> Snowpipe -> Raw Schema -> dbt/SQL -> Mart Schema -> BI",
    "how_it_works": "1. Client connects and authenticates via Services Layer\n2. SQL is parsed, validated, and optimized using metadata statistics\n3. Optimizer builds efficient execution plan with partition pruning\n4. Plan executed on virtual warehouse nodes in parallel\n5. Each node reads relevant micro-partitions in columnar format\n6. Columnar data is decompressed and processed\n7. Results aggregated and returned to client\n8. Warehouse auto-suspends after configured idle period\n9. Data continuously protected via encryption, Time Travel, and Fail-safe",
    "why_exists": "Traditional data warehouses couple storage and compute, requiring upfront hardware choices that lead to overprovisioning (waste) or underprovisioning (poor performance). Cloud storage is cheap and elastic, but traditional architectures cannot leverage it. Snowflake decouples storage and compute, enabling elastic scaling, concurrency without contention, and pay-per-use pricing while eliminating management overhead (no indexing, partitioning, or vacuum commands).",
    "why_matters": "- **Cost Efficiency**: Pay only for storage (cheap) and compute (only when running)\n- **Concurrency**: Multiple warehouses query same data without contention\n- **Simplicity**: No indexing, partitioning, or manual tuning required\n- **Data Sharing**: Instant, secure sharing without copying data\n- **Ecosystem**: Integrates with all major BI tools, ETL platforms, and ML frameworks\n- **Cloud Agnostic**: Consistent experience across AWS, Azure, and GCP",
    "package": "com.dataeng.nine",
    "JavaSources": {},
    "Tests": {},
}
LABS["09-snowflake"] = l09

# ===== LAB 10: APACHE FLINK =====
l10 = {
    "title": "Apache Flink",
    "short": "Flink",
    "overview": "Apache Flink is a distributed stream processing framework for stateful computations over unbounded and bounded data streams. This lab covers stream processing fundamentals, Flink SQL, event time processing, watermarks, checkpointing, and savepoints.",
    "concepts": [
        ("Stream Processing", "Continuous processing of unbounded data streams with sub-second latency"),
        ("Event Time vs Processing Time", "Handling out-of-order events using embedded event timestamps"),
        ("Watermarks", "Progress metric for event-time processing, tracking completeness"),
        ("Checkpointing", "Distributed snapshots for exactly-once fault tolerance"),
        ("Savepoints", "User-initiated snapshots for job upgrades and maintenance"),
        ("Flink SQL", "Relational abstraction for stream processing with ANSI SQL"),
    ],
    "objectives": [
        "Understand Flink's streaming-first architecture and runtime model",
        "Implement event-time processing with watermarks and allowed lateness",
        "Use tumbling, sliding, and session windows for time-based aggregations",
        "Configure checkpointing and savepoints for fault tolerance",
        "Write Flink SQL queries for streaming data",
        "Manage state with different state backends (Heap, RocksDB)",
    ],
    "exercises": [
        "Implement word count with event-time processing and watermark strategy",
        "Create tumbling, sliding, and session window aggregations on sensor data",
        "Build a stateful enrichment pipeline using KeyedState (ValueState, MapState)",
        "Write Flink SQL queries with Kafka source and JDBC sink",
        "Configure checkpointing and savepoints; test failure recovery",
        "Implement exactly-once sink semantics with two-phase commit",
    ],
    "quiz_qa": [
        ("What mechanism does Flink use for fault tolerance?", ["Write-ahead log", "Distributed snapshots (checkpointing)", "Replication", "Journaling"]),
        ("What does a watermark represent?", ["Event processing time", "Progress in event time", "Number of events processed", "Resource utilization"]),
        ("Which state backend is best for large state (>1GB)?", ["MemoryStateBackend", "FsStateBackend", "RocksDBStateBackend", "JdbcStateBackend"]),
        ("What is the difference between aligned and unaligned checkpoints?", ["Sync vs async", "Blocking vs non-blocking", "Wait for all vs proceed immediately", "Local vs distributed"]),
        ("What does a savepoint enable?", ["Faster processing", "Job upgrades and rescaling", "Data compression", "Schema evolution"]),
    ],
    "quiz_answers": ["B", "B", "C", "C", "B"],
    "flashcards": [
        ("What is a watermark in Flink?", "A metric tracking event time progress, used for window trigger decisions and late-event handling"),
        ("What is the difference between aligned and unaligned checkpoints?", "Aligned: wait for barriers from all inputs; Unaligned: proceed immediately, better under backpressure"),
        ("What is a savepoint?", "User-initiated checkpoint created manually for job version upgrades, parallelism changes, and maintenance"),
        ("What state backends does Flink support?", "HashMapStateBackend (heap, fast, <1GB) and RocksDBStateBackend (disk, >1GB, memory-efficient)"),
        ("What is Flink SQL?", "Relational API for stream processing; same SQL semantics over bounded and unbounded data"),
    ],
    "interview": [
        ("Core Concepts", "How does Flink handle out-of-order events?", "Through watermarks and allowed lateness. Watermarks define event-time progress; late events within allowedLateness trigger another window evaluation."),
        ("Fault Tolerance", "Explain Flink's checkpointing algorithm.", "Chandy-Lamport distributed snapshots with barrier alignment; state snapshotted asynchronously to durable storage; on failure, all tasks restart from latest checkpoint."),
        ("State", "When would you use RocksDB vs Heap state backend?", "RocksDB for >1GB state or memory-constrained environments; Heap for low-latency, sub-second state access with smaller state sizes."),
        ("Performance", "How do you handle backpressure in Flink?", "Monitor backpressure in web UI; increase parallelism; optimize operator chains; use unaligned checkpoints; increase network buffers."),
    ],
    "reflection": [
        "Event-time processing with watermarks is Flink's killer feature for accurate streaming analytics",
        "Checkpointing at scale requires careful tuning of state backends and checkpoint intervals",
        "Flink SQL bridges the gap between stream and batch processing for SQL-savvy teams",
        "State management is the most complex aspect — choose backends wisely",
        "Savepoints are essential for production job lifecycle management",
    ],
    "references": [
        "Apache Flink Docs: https://flink.apache.org/",
        "Flink SQL: https://nightlies.apache.org/flink/flink-docs-stable/docs/dev/table/sql/",
        "Checkpointing: https://nightlies.apache.org/flink/flink-docs-stable/docs/ops/state/checkpoints/",
        "Watermarks: https://nightlies.apache.org/flink/flink-docs-stable/docs/concepts/time/",
        "State Backends: https://nightlies.apache.org/flink/flink-docs-stable/docs/ops/state/state_backends/",
    ],
    "history": [
        ("2010", "Started as 'Stratosphere' research project at TU Berlin"),
        ("2014", "Entered Apache Incubator as Flink"),
        ("2015", "Top-level Apache project; streaming-first runtime"),
        ("2016", "Flink 1.0 release with stable API"),
        ("2017", "Flink SQL GA; event-time processing improvements"),
        ("2019", "Flink 1.9 with new memory model and unification"),
        ("2021", "Flink 1.13 with unaligned checkpoints"),
        ("2023", "Flink 1.17 with speculative execution, adaptive batch"),
        ("2024", "Flink 2.0 preview with major API refactoring"),
    ],
    "mental_models": [
        ("The Assembly Line", "Operators are stations on an assembly line. Events move down the line, being processed at each station. Watermarks are clock ticks telling stations how far the shift has progressed."),
        ("The Bookmark Analogy", "Checkpoints are like bookmarking your page. If the book falls (failure), you resume from the bookmark. Savepoints are taking a photo for later reference."),
        ("The River and Dams", "Data flows like a river. Windows are dams that collect water for a period, then release the accumulated measurement. Watermarks show how far upstream the river has been measured."),
    ],
    "theory_sections": [
        ("Processing Models", "Batch: bounded data, periodic jobs, minutes-to-hours latency. Stream: unbounded data, continuous processing, millisecond latency. Flink unifies both — batch is a special case of streaming."),
        ("Event Time vs Processing Time", "Processing Time: wall clock when event arrives at operator. Event Time: timestamp embedded in the event. Ingestion Time: when event enters Flink. Event time enables accurate results despite out-of-order or late-arriving data."),
        ("Window Types", "Tumbling: fixed-size, non-overlapping (e.g., 1-hour windows). Sliding: fixed-size, overlapping (e.g., 5-min window every 2 min). Session: windows based on activity gaps (e.g., 30-min inactivity). Global: single window for all records."),
        ("Watermarks", "Signal how far event time has progressed. Watermark(t) = max(event_time_seen) - max_out_of_orderness. Late elements: those arriving after watermark. Allowed lateness: configurable grace period triggering another window evaluation."),
    ],
    "internals_sections": [
        ("Runtime Architecture", "JobManager (Master): schedules tasks, coordinates checkpoints, handles failure recovery. TaskManagers (Workers): execute tasks in slots; each slot = one thread; network buffers for data exchange between tasks."),
        ("Checkpointing Algorithm (Chandy-Lamport)", "1. JM injects checkpoint barrier into sources. 2. Barriers propagate through operators. 3. Each operator snapshots state upon receiving barrier. 4. Aligned: wait for all input barriers. 5. Unaligned: proceed immediately (for high backpressure)."),
        ("State Backends", "HashMapStateBackend: state in JVM heap, checkpoint to DFS — fast but limited to ~1GB. RocksDBStateBackend: state in RocksDB (disk), checkpoint to DFS — more memory-efficient, slower access, handles >1GB."),
        ("Network Stack", "Credit-based flow control. Buffer pools per TaskManager. Backpressure propagates upstream automatically. Configurable network buffer sizes and fractions."),
    ],
    "math": [
        ("Latency", "ProcessingLatency = ProcessingTime - EventTime\nWatermarkLatency = CurrentWatermark - CurrentEventTime\nEndToEndLatency = IngestionTime - ConsumptionTime"),
        ("Throughput", "Throughput = RecordsProcessed / Time\nParallelism = Tasks / Slots\nMaxThroughput = Parallelism * ThroughputPerSlot"),
        ("Watermark Computation", "Watermark(t) = max(event_time_seen) - max_out_of_orderness\nIdle sources need watermark idle timeout to avoid stalling"),
        ("Checkpoint Size", "CheckpointSize = SUM(state_backend_overhead + operator_state)\nDuration = SyncDuration + AsyncDuration\nRecoveryTime = LastCheckpointRestoreTime + ReprocessingTime"),
    ],
    "visual": "```\nFlink Architecture:\n+-----------+\n| JobManager| [Scheduler] [CheckpointCoordinator] [HA]\n+-----------+\n    |\n+-----------v-----------+\n|    TaskManager 1       |\n| [Slot1] [Slot2] [Slot3]|\n| DataStream operators   |\n+-----------------------+\n\nWatermark Timeline:\nEvents:  e1   e2   e3   e4   e5\nTime:    t1   t3   t4   t2   t7\n                          ↑\n                     e4 late\nWatermark: w(t3)   w(t4)     w(t7)\n```",
    "code_dive": "See Java source files in src/main/java/com/dataeng/ten/ for complete implementations:\n\n- SensorReadingProcessor.java: Full streaming pipeline with watermarks, keyed state, windowing\n- SensorReading.java: POJO with event timestamp\n- SensorReadingSource.java: Parallel source function generating sensor data\n- AlarmResult.java / AggregateResult.java: Output types\n- SensorAggregator.java: AggregateFunction for window computations\n\nKey patterns:\n```java\nDataStream<SensorReading> stream = env.addSource(new KafkaSource<>())\n    .assignTimestampsAndWatermarks(\n        WatermarkStrategy.<SensorReading>forBoundedOutOfOrderness(Duration.ofSeconds(5))\n            .withTimestampAssigner((r, ts) -> r.getTimestamp())\n    );\n\nstream.keyBy(SensorReading::getSensorId)\n    .window(TumblingEventTimeWindows.of(Time.minutes(1)))\n    .allowedLateness(Time.seconds(30))\n    .aggregate(new SensorAggregator());\n```",
    "step_by_step": [
        "Download Flink; start cluster with ./bin/start-cluster.sh",
        "Configure Kafka source connector with deserialization schema",
        "Assign timestamps and watermark strategy for event-time processing",
        "Apply transformations: map, filter, flatMap, keyBy, window",
        "Use KeyedState (ValueState, ListState, MapState) for stateful operations",
        "Configure checkpoint interval and state backend (RocksDB for large state)",
        "Write sink to Kafka, JDBC, Elasticsearch, or file system",
        "Execute: env.execute('job-name'); monitor via web UI (localhost:8081)",
    ],
    "common_mistakes": [
        ("No Watermark Strategy", "Defaults to ProcessingTime, causing incorrect event-time results; always define watermark strategy"),
        ("Large State Without RocksDB", "Heap state >1GB causes OOM; switch to RocksDBStateBackend for large state"),
        ("Aligned Checkpoints with Backpressure", "Aligned checkpoints increase duration under backpressure; use unaligned checkpoints"),
        ("Global Window Without Trigger", "Never emits results; specify a trigger or use a different window type"),
        ("Not Handling Idle Sources", "Watermarks stall without data; configure idle source timeout"),
        ("Wrong Parallelism", "Too few slots cause backpressure; too many waste resources; match to Kafka partitions"),
    ],
    "debugging": [
        ("Backpressure", "Check web UI backpressure tab (localhost:8081); increase parallelism or optimize operator chains"),
        ("OOM Errors", "Switch to RocksDB state backend; reduce state size with TTL; check managed memory fraction"),
        ("Checkpoint Failure", "CheckpointTimeout — increase timeout; reduce state size; use unaligned checkpoints"),
        ("Watermark Not Advancing", "Source not producing data; configure idle source timeout as fallback"),
    ],
    "refactoring": "## State Migration with Savepoints\n```bash\nflink savepoint <jobId> /savepoints/\n# Update job with new state schema\nflink run -s /savepoints/savepoint-<id> job.jar\n```\n\n## From Batch to Streaming\nBefore: DataSet API (batch processing)\nAfter: DataStream API with event-time windows for near-real-time results\n\n## Operator Chain Optimization\nBefore: Multiple separate map operations\nAfter: Combine into single map or chain explicitly via .startNewChain() / .disableChaining()",
    "performance": [
        ("Parallelism Tuning", "Match to Kafka partition count; ensure enough TaskManager slots; avoid idle slots wasting resources"),
        ("State Optimization", "RocksDB for >1GB state; configure state TTL to expire stale data; minimize key cardinality for distribution"),
        ("Checkpoint Tuning", "Async checkpoints for state backends; unaligned for backpressure; configure sufficient checkpoint timeout and minimum pause between"),
        ("Network Buffers", "Increase taskmanager.memory.network.fraction for shuffle-heavy jobs; configure netty threads and buffer sizes"),
    ],
    "security": "## Authentication\n- Kerberos authentication for cluster access\n- Mutual TLS for component communication\n- YARN/K8s security integration\n\n## Authorization\n- Role-based access control for cluster operations\n- Job submission ACLs\n- Resource quotas per user/group\n\n## Data Protection\n- Encrypted shuffle data transfer\n- Secure state backend storage (encrypted file systems)\n- Audit logging for all job operations",
    "architecture": "```\n[Kafka Source] -> [Flink Job: Operators] -> [Sink: Kafka/JDBC/ES]\n                      |\n             [State Backend]\n             (RocksDB/Heap)\n                      |\n        [JobManager <-> TaskManagers]\n                      |\n            [HA: ZooKeeper/K8s]\n\nTypical Pipeline:\nSource -> Map -> KeyBy -> Window -> Aggregate -> Sink\n```",
    "how_it_works": "1. Source operators read data from external systems (Kafka, files, sockets)\n2. Data flows through transformation operators (map, filter, flatMap, keyBy)\n3. Operators maintain state (counters, aggregations, caches) via state backends\n4. Windows group events by time or count for aggregation\n5. Sink operators write results to external systems\n6. Checkpoint coordinator periodically triggers distributed snapshots\n7. On failure, all tasks restart from the latest checkpoint\n8. Watermarks track event-time progress for trigger decisions",
    "why_exists": "Batch processing (MapReduce, Spark Streaming micro-batches) trades latency for throughput. Real-time applications require true stream processing with sub-second latency and exactly-once semantics. Flink was designed as a streaming-first engine, treating batch as a special case of streaming — enabling real-time fraud detection, live dashboards, streaming ETL, and event-driven applications.",
    "why_matters": "- **True Streaming**: Sub-millisecond latency, not micro-batching\n- **Event Time**: Accurate results despite out-of-order or late data\n- **State Management**: Rich state API with exactly-once fault tolerance\n- **Exactly-Once Semantics**: Correct results even during failures\n- **Unified Batch/Streaming**: Single API and runtime for both paradigms\n- **Scale**: Proven at thousands of nodes processing billions of events/day",
    "package": "com.dataeng.ten",
    "JavaSources": {},
    "Tests": {},
}
LABS["10-apache-flink"] = l10

# ===== LAB 11: APACHE AIRFLOW =====
l11 = {
    "title": "Apache Airflow",
    "short": "Airflow",
    "overview": "Apache Airflow is a platform for programmatically authoring, scheduling, and monitoring workflows using directed acyclic graphs (DAGs). This lab covers DAG design, operators, sensors, the TaskFlow API, executors, and XComs for inter-task communication.",
    "concepts": [
        ("DAG", "Directed Acyclic Graph defining workflow structure with task dependencies"),
        ("Operators", "Atomic units of work (PythonOperator, BashOperator, SparkSubmitOperator)"),
        ("Sensors", "Operators that wait for external conditions (file arrival, time, query result)"),
        ("TaskFlow API", "Decorator-based DAG creation using @dag and @task"),
        ("Executors", "How tasks run: Sequential, Local, Celery, Kubernetes"),
        ("XComs", "Cross-communication metadata store between tasks (max 48KB)"),
    ],
    "objectives": [
        "Design DAGs with proper task dependencies and scheduling",
        "Use TaskFlow API for clean, decorator-based DAGs",
        "Configure different executors for production deployments",
        "Use XComs for task communication",
        "Implement sensors for event-driven workflows",
        "Monitor and troubleshoot DAG execution",
    ],
    "exercises": [
        "Create a 3-task DAG with PythonOperator (extract, transform, load)",
        "Implement branching based on data quality check results",
        "Use ExternalTaskSensor to wait for an upstream DAG completion",
        "Create a SparkSubmitOperator to submit a Java Spark job",
        "Build a TaskFlow API DAG with XCom passing between tasks",
        "Implement SLA monitoring and alerting for critical DAGs",
    ],
    "quiz_qa": [
        ("What structure does Airflow use to define workflows?", ["Tree", "Directed Acyclic Graph", "Pipeline", "Queue"]),
        ("How do tasks communicate in Airflow?", ["Shared memory", "XComs", "Database", "HTTP"]),
        ("Which executor is best for production distributed workloads?", ["SequentialExecutor", "LocalExecutor", "CeleryExecutor", "DebugExecutor"]),
        ("What does a Sensor do?", ["Executes SQL", "Waits for a condition", "Writes to database", "Sends email"]),
        ("What is the maximum XCom value size?", ["1KB", "48KB", "1MB", "Unlimited"]),
    ],
    "quiz_answers": ["B", "B", "C", "B", "B"],
    "flashcards": [
        ("What is an Airflow DAG?", "Directed Acyclic Graph defining workflow tasks, their dependencies, and scheduling"),
        ("What is the difference between Operators and Sensors?", "Operators perform actions; Sensors wait for external conditions to be met"),
        ("What is XCom used for?", "Cross-task communication for small metadata (<48KB): task IDs, statuses, small results"),
        ("What are the executor types?", "Sequential (dev), Local (parallel single-node), Celery (distributed), K8s (container-based)"),
        ("What does catchup=True do?", "Creates DAG runs for all intervals between start_date and now that haven't been executed"),
    ],
    "interview": [
        ("Core", "How does Airflow handle task retries?", "Configure retries and retry_delay on operators; supports exponential backoff; task marked as up_for_retry"),
        ("DAG Design", "What is the difference between catchup and backfill?", "catchup: auto-runs missed intervals on scheduler start; backfill: explicit historical run via CLI"),
        ("Dependencies", "How to handle dependencies between different DAGs?", "ExternalTaskSensor (wait for upstream DAG), TriggerDagRunOperator (trigger downstream), or Dataset-driven scheduling"),
        ("Production", "How do you scale Airflow for production?", "CeleryExecutor with Redis/RabbitMQ broker; PostgreSQL backend; multiple workers; separate webserver and scheduler"),
    ],
    "reflection": [
        "Airflow's DAG-as-code model enables version control, testing, and code review of workflows",
        "Operator ecosystem with 1000+ providers makes it extensible to virtually any external system",
        "XCom limitations (48KB) enforce good practices — pass references, not data",
        "Scheduler scaling is the most common production bottleneck",
        "TaskFlow API is significantly cleaner than traditional DAG construction",
    ],
    "references": [
        "Airflow Docs: https://airflow.apache.org/docs/",
        "TaskFlow API: https://airflow.apache.org/docs/apache-airflow/stable/tutorial/taskflow.html",
        "Operators: https://airflow.apache.org/docs/apache-airflow/stable/howto/operator/index.html",
        "Executors: https://airflow.apache.org/docs/apache-airflow/stable/executor/index.html",
        "Airflow Providers: https://airflow.apache.org/docs/apache-airflow-providers/",
    ],
    "history": [
        ("2014", "Created at Airbnb by Maxime Beauchemin"),
        ("2015", "Open-sourced"),
        ("2016", "Entered Apache Incubator"),
        ("2019", "Top-level Apache project"),
        ("2020", "Airflow 2.0 with TaskFlow API, stability improvements"),
        ("2021", "Airflow 2.1 with deferrable operators"),
        ("2022", "Airflow 2.3 with dynamic task mapping"),
        ("2023", "Airflow 2.7 with data-aware scheduling (datasets)"),
        ("2024", "Airflow 2.9 with expanded dataset features, performance improvements"),
    ],
    "mental_models": [
        ("The Recipe Book", "DAG is a recipe. Tasks = steps. Dependencies = order of steps. Sensors = waiting for ingredients to arrive before proceeding."),
        ("The Factory Floor", "Tasks are machines, scheduler is the foreman, executor is the power source, workers are operators. Materials (data) flow between machines."),
        ("The Project Plan", "A DAG is a Gantt chart of your data pipeline. Each task has a duration, dependencies, and resource requirements. The scheduler executes the plan."),
    ],
    "theory_sections": [
        ("DAG Structure", "Nodes = Tasks (operator instances). Edges = Dependencies (upstream -> downstream). Direction = data/control flow. Acyclic = no cycles (prevents infinite processing). Scheduling via schedule_interval, start_date, catchup."),
        ("Execution Models", "SequentialExecutor: one task at a time (dev). LocalExecutor: parallel tasks on one machine (small production). CeleryExecutor: distributed across workers (production). K8sExecutor: each task in container (cloud-native). DebugExecutor: single process (debugging)."),
        ("Sensors", "ExternalTaskSensor: wait for upstream DAG. S3KeySensor: wait for file in S3. SqlSensor: check SQL query results. TimeSensor: wait until specific time. Sensors can be in poke (polling) or reschedule (deferred) mode."),
        ("XComs", "Key-value store for task communication. Push: ti.xcom_push(key, value). Pull: ti.xcom_pull(task_ids, key). Max size 48KB (metadata only). For large data, store reference (S3 path, table name)."),
    ],
    "internals_sections": [
        ("Scheduler Loop", "1. Parse DAG files every 30s. 2. Create DAGRuns for schedules/triggers. 3. Create TaskInstances for ready tasks. 4. Queue to executor. 5. Executor distributes to workers. 6. Workers execute and report. 7. Scheduler evaluates next downstream tasks."),
        ("Task Lifecycle", "none -> scheduled -> queued -> running -> success/failed/skipped/up_for_retry. Failed tasks with retries go back to scheduled state. SLA misses recorded but don't stop execution."),
        ("Metadata Database", "Key tables: dag (definitions), dag_run (executions), task_instance (task records), xcom (communication), log (execution logs), sla_miss (breach records). PostgreSQL or MySQL are production backends."),
        ("DAG Serialization", "DAGs serialized to DB for Webserver display (no DAG file parsing needed). File parsing happens only in Scheduler. This enables independent scaling of Webserver."),
    ],
    "math": [
        ("Scheduling", "NextExecution = schedule_interval.ceil(start_date + runs * interval)\nBackfillRuns = (now - start_date) / schedule_interval"),
        ("DAG Complexity", "ExecutionTime = SUM(critical_path tasks)\nParallelEfficiency = TotalTasks / CriticalPathTasks\nResourceUtilization = ActiveTasks / MaxActiveTasks"),
        ("SLA Metrics", "SuccessRate = Succeeded / (Succeeded + Failed) * 100\nDurationP50 = PERCENTILE(durations, 0.5)\nSLABreachRate = BreachedSLAs / TotalSLAs * 100"),
    ],
    "visual": "```\nDAG Visualization:\n[Start] --> [Extract] --> [Validate] --> [Load] --> [Notify]\n               |                          ^\n               +--> [Transform] ---------+\n\nAirflow Architecture:\n[Webserver]    [Scheduler]\n    |               |\n    +-------+-------+\n            |\n    [Metadata DB: PostgreSQL]\n            |\n    [Celery/Redis Broker]\n            |\n[Worker1] [Worker2] [Worker3]\n\nGantt View:\nExtract:    [============]\nValidate:       [==========]\nTransform:          [====================]\nLoad:                   [==========]\n```",
    "code_dive": "See Java source files in src/main/java/com/dataeng/eleven/ for complete implementations:\n\n- AirflowDagGenerator.java: Programmatic DAG construction with dependency management\n- DagValidator.java: Cycle detection, topological sort, critical path estimation\n- SlaMonitor.java: SLA definition registration, breach detection, alerting\n\nKey patterns:\n```java\n// DAG with dependencies\ndagGenerator.addTask(new Task(\"extract\", \"Extract from source\", 15));\ndagGenerator.addTask(new Task(\"load\", \"Load to warehouse\", 30));\ndagGenerator.addDependency(\"extract\", \"load\");\n\n// Cycle detection\nDagValidator validator = new DagValidator();\nvalidator.addEdge(\"A\", \"B\");\nif (validator.hasCycle()) { /* handle error */ }\n```",
    "step_by_step": [
        "Install Airflow: pip install apache-airflow",
        "Initialize database: airflow db init",
        "Create admin user: airflow users create --username admin --password admin --role Admin",
        "Write DAG: Create Python file in dags/ directory with DAG definition",
        "Start services: airflow webserver -p 8080 (UI) and airflow scheduler (task scheduling)",
        "Trigger DAG via Web UI or CLI: airflow dags trigger etl_pipeline",
        "Monitor execution via Tree, Graph, Gantt, Code, and Task Duration views",
        "Integrate Java/Spark: use SparkSubmitOperator with connection config",
    ],
    "common_mistakes": [
        ("Not Setting catchup=False", "Deploying new DAG triggers backfill of all missed intervals; set catchup=False unless backfill intended"),
        ("Large XCom Values", "XCom limit is 48KB; store large data in S3/GCS and pass the object path reference"),
        ("Long DAG Parse Time", "Too many DAG files or complex parsing; keep DAGs simple and minimize imports at top level"),
        ("Missing depends_on_past", "Tasks running concurrently that should be sequential; set depends_on_past=True when needed"),
        ("Pool Exhaustion", "Too many tasks competing for limited pool slots; add pools or increase default_pool size"),
    ],
    "debugging": [
        ("DAG Not Appearing", "airflow dags list-import-errors shows import errors; validate Python syntax with ast.parse"),
        ("Task Failure", "airflow tasks test my_dag my_task 2024-01-01 runs single task in isolation; check log files"),
        ("Scheduler Issues", "airflow jobs check --job-type SchedulerJob verifies scheduler health; check parsing_processes config"),
        ("Performance", "Config tuning: min_file_process_interval, parsing_processes, dag_dir_list_interval"),
    ],
    "refactoring": "## Traditional to TaskFlow API\nBefore:\n```python\nwith DAG('etl') as dag:\n    op1 = PythonOperator(task_id='extract', python_callable=extract, dag=dag)\n```\nAfter:\n```python\n@dag(schedule='0 6 * * *')\ndef etl():\n    @task\n    def extract(): return {'data': '...'}\n```\n\n## XComs to Object Store\nBefore: Push large DataFrames via XCom\nAfter: Save to S3, pass S3 key via XCom",
    "performance": [
        ("Scheduler Tuning", "Increase parsing_processes (default 2). Set min_file_process_interval to 30-60s. Adjust dag_dir_list_interval. Use DAG serialization."),
        ("Database Cleanup", "Periodically clean task_instance, xcom, and log tables. VACUUM / ANALYZE PostgreSQL. Use separate DB for Airflow."),
        ("Deferrable Operators", "Use mode='reschedule' for long-running sensors to free worker slots. Write custom deferrable operators for custom waits."),
        ("Pools and Concurrency", "Set pool_slots per task. Configure max_active_tasks and max_active_runs per DAG. Use priority_weight for queue ordering."),
    ],
    "security": "## Authentication & Authorization\n- LDAP, OAuth, OpenID Connect, or Google Auth\n- RBAC roles: Admin, Op, User, Viewer, Public\n- Fernet key for variable/connection encryption\n\n## Secrets Management\n```python\nfrom airflow.models import Variable\nsecret = Variable.get('my_secret', deserialize_json=True)\nconn = BaseHook.get_connection('my_db')\n```\n\n## Network\n- Private subnets for workers\n- TLS for webserver\n- Separate metadata database with encrypted connections",
    "architecture": "## Production Architecture\n```\n[ELB] -> [Webserver x2] -> [PostgreSQL Primary + Replica]\n                              |\n[Scheduler x2 (Active/Standby)]\n                              |\n[Redis/Sentinel] -> [Celery Workers x N]\n```\n\n## Deployment Options\nDocker Compose (dev), Helm Chart (K8s), AWS MWAA (managed), Google Cloud Composer (managed)",
    "how_it_works": "1. Scheduler continuously scans DAG files in dags/ directory\n2. For each DAG, checks if schedule condition is met\n3. Creates DAG Run and Task Instances for ready tasks\n4. Queues tasks to configured Executor\n5. Executor distributes tasks to workers (local processes or remote)\n6. Workers execute task code and report status\n7. Scheduler marks downstream tasks as ready when dependencies succeed\n8. Web server reads metadata DB for UI display\n9. On failure, tasks are retried per configuration\n10. Completed runs are retained per configuration",
    "why_exists": "Cron jobs lack dependency management, monitoring, failure handling, and visibility. Airflow provides declarative DAGs, automatic retries, dependency resolution, centralized monitoring UI, extensive logging, and integration with virtually every data system through 1000+ providers.",
    "why_matters": "- **Reliability**: Auto-retry, alerting, SLA monitoring, failure tracking\n- **Visibility**: Web UI with Tree/Graph/Gantt/Code views for all runs\n- **Extensibility**: 1000+ providers for Spark, Kafka, AWS, GCP, Azure, DBs\n- **Code-as-Config**: Version-controlled, testable, reviewable workflows\n- **Community**: Most active Apache project with widespread adoption",
    "package": "com.dataeng.eleven",
    "JavaSources": {},
    "Tests": {},
}
LABS["11-apache-airflow"] = l11

# ===== LAB 12: APACHE BEAM =====
l12 = {
    "title": "Apache Beam",
    "short": "Beam",
    "overview": "Apache Beam is a unified programming model for batch and streaming data processing pipelines. It provides portable SDKs (Java, Python, Go) that run on multiple runners (Flink, Spark, Dataflow, Samza). This lab covers Beam pipelines, PCollections, windowing, triggers, and portable runners.",
    "concepts": [
        ("Pipeline", "End-to-end data processing workflow as a DAG of PTransforms"),
        ("PCollection", "Distributed dataset — bounded (batch) or unbounded (streaming)"),
        ("PTransform", "Processing operation that transforms one or more PCollections"),
        ("Windowing", "Time-based grouping of unbounded data into finite windows"),
        ("Trigger", "Policy for when to emit window results (early, on-time, late)"),
        ("Runner", "Execution engine — DirectRunner, FlinkRunner, SparkRunner, DataflowRunner"),
    ],
    "objectives": [
        "Construct Beam pipelines with ParDo, GroupByKey, Combine, and Composite transforms",
        "Apply windowing (Fixed, Sliding, Sessions) to unbounded PCollections",
        "Configure triggers for early and late firings",
        "Run the same pipeline on multiple runners (Direct, Flink, Dataflow)",
        "Use stateful processing with ValueState, ListState, MapState",
        "Build custom I/O connectors with Splittable DoFn",
    ],
    "exercises": [
        "Build word count pipeline with ParDo, Count, and TextIO",
        "Create streaming pipeline with KafkaIO, windowing, and trigger configuration",
        "Implement stateful DoFn for sessionization of user events",
        "Use side inputs for enrichment from external data source",
        "Create custom composite transform combining multiple PTransforms",
        "Run the same pipeline on DirectRunner and FlinkRunner",
    ],
    "quiz_qa": [
        ("What is a PCollection?", ["A processing step", "A distributed dataset", "A pipeline", "A runner"]),
        ("What is needed for unbounded PCollections?", ["Windowing", "Fixed size", "Batch processing", "DirectRunner"]),
        ("What does ParDo represent?", ["Grouping operation", "Element-wise parallel processing", "Write operation", "Read operation"]),
        ("What is a trigger?", ["Window type", "When to emit results", "Data source", "Pipeline option"]),
        ("What is Beam's portability mechanism?", ["REST API", "SDK Harness + Fn API", "Shared library", "gRPC streaming"]),
    ],
    "quiz_answers": ["B", "A", "B", "B", "B"],
    "flashcards": [
        ("What is Apache Beam?", "Unified programming model for batch and streaming pipelines, portable across runners"),
        ("What is a PTransform?", "An operation that transforms one or more PCollections into new PCollections"),
        ("What is Beam's portability architecture?", "SDK Harness manages user code execution; Fn API communicates with runner via protocol buffers"),
        ("What is windowing?", "Grouping unbounded data into finite time-based collections for aggregation"),
        ("What is the purpose of a trigger?", "Defines when window results are emitted: early (before watermark), on-time (at watermark), late (after watermark)"),
    ],
    "interview": [
        ("Core", "How does Beam handle late data?", "allowedLateness specifies how long to wait for late arrivals; late firings trigger additional pane emissions"),
        ("Architecture", "What is the difference between Beam and Flink?", "Beam is an abstraction layer providing a unified API; Flink is a runner. Beam provides API portability across runners."),
        ("Optimization", "What is fusion in Beam?", "Optimization merging adjacent ParDo transforms into a single stage to avoid serialization/communication overhead"),
        ("Windowing", "Explain the difference between Fixed, Sliding, and Session windows.", "Fixed: non-overlapping; Sliding: overlapping with slide period; Session: dynamic based on inactivity gap"),
    ],
    "reflection": [
        "Beam's abstraction is powerful for multi-runner portability but adds debugging complexity",
        "Portability concept is ahead of its time, but runner feature parity remains a challenge",
        "Windowing/trigger model is the most complex yet most powerful aspect of Beam",
        "Stateful processing is elegant but requires careful resource management",
        "Composite transforms are essential for building reusable pipeline components",
    ],
    "references": [
        "Apache Beam Docs: https://beam.apache.org/",
        "Beam Programming Guide: https://beam.apache.org/documentation/programming-guide/",
        "Beam Java SDK: https://beam.apache.org/releases/javadoc/",
        "Runner Capability Matrix: https://beam.apache.org/documentation/runners/capability-matrix/",
        "Splittable DoFn: https://beam.apache.org/documentation/programming-guide/#splittable-dofns",
    ],
    "history": [
        ("2015", "Google open-sources Cloud Dataflow SDK (predecessor to Beam)"),
        ("2016", "Donated to Apache, renamed Beam"),
        ("2017", "Apache Beam 2.0 with stable API and multi-language support"),
        ("2018", "Beam 2.4 with SQL, portability framework introduced"),
        ("2019", "Beam 2.13 with Splittable DoFn for custom I/O"),
        ("2020", "Beam 2.22 with schema transforms, row-based API"),
        ("2021", "Beam 2.33 with cross-language transforms"),
        ("2023", "Beam 2.50 with runner v2, multi-language improvements"),
        ("2024", "Beam 2.55 with enhanced portable batch and streaming"),
    ],
    "mental_models": [
        ("The Recipe", "Pipeline = Recipe. PCollection = Ingredients. PTransform = Cooking steps. Runner = Kitchen equipment (oven, stove, microwave). Same recipe works on different equipment."),
        ("The Assembly Line", "PCollection is conveyor belt. ParDo is worker station. Each worker processes one item. Multiple workers at same station = parallelism. Windows are bins for time-based grouping."),
        ("The Universal Remote", "Beam is a universal remote. Write one pipeline, run on any runner (Dataflow TV, Flink Sound System, Spark Player). The remote API is consistent regardless of the device."),
    ],
    "theory_sections": [
        ("Pipeline Model", "Pipeline = DAG of PTransforms applied to PCollections. PCollection may be bounded (batch file) or unbounded (Kafka stream). Transforms include ParDo (element-wise), GroupByKey (grouping), Combine (aggregation), Flatten (merge), Partition (split)."),
        ("Windowing", "Fixed: fixed-size non-overlapping windows. Sliding: fixed-size overlapping windows. Sessions: dynamic windows based on inactivity gaps. Global: single window (default for batch). Windowing is required for unbounded PCollections."),
        ("Triggers", "AfterWatermark: fire when watermark passes window end. AfterProcessingTime: fire after wall-clock delay. AfterPane: fire after N elements. Repeatedly: repeat trigger. AfterAny/AfterAll: composition. Default: watermark with early firings."),
        ("Runners", "DirectRunner: local execution for testing. FlinkRunner: true streaming, best for production streaming. SparkRunner: micro-batch. DataflowRunner: managed service with auto-scaling. SamzaRunner: Yahoo's stream processor."),
    ],
    "internals_sections": [
        ("Pipeline Execution Stages", "1. Pipeline construction (applying transforms). 2. Pipeline translation (Runner API protos). 3. Optimization (fusion, combining). 4. Execution on runner. 5. Element processing through stages in bundles."),
        ("Fusion Optimization", "Adjacent ParDo transforms are fused into a single stage to avoid serialization/deserialization between them. Fusion can be prevented with Reshuffle for load balancing."),
        ("Bundle Processing", "Elements processed in bundles for efficiency. Checkpointing at bundle boundaries enables fault tolerance. Bundle size affects throughput and latency."),
        ("Portability Architecture", "SDK Harness (user code execution) <-> Fn API (protocol buffers) <-> Runner. This allows any SDK (Java, Python, Go) to work with any runner."),
    ],
    "math": [
        ("Throughput", "ElementsPerSecond = TotalElements / WallTime\nBundleThroughput = BundleSize / BundleProcessingTime\nParallelism = NumWorkers * CoresPerWorker"),
        ("Windowing Math", "Fixed Windows: NumWindows = TotalTime / WindowSize\nSliding Windows: NumWindows = TotalTime / SlidePeriod\nSession Windows: Variable, depends on gap duration and event distribution"),
        ("Watermark", "Watermark(t) = min(event_timestamps_processed) + expected_skew\nSkew = max expected out-of-orderness\nLateData = events with timestamp < watermark"),
        ("Trigger Latency", "OnTime = WindowEnd - WatermarkArrival\nEarly = OnTime - EarlyFiringPeriod\nLate = OnTime + AllowedLateness"),
    ],
    "visual": "```\nPipeline DAG:\n[Read] -> [ParDo(Parse)] -> [ParDo(Extract)] -> [Count] -> [Write]\n\nWindowing:\nEvents:  e1   e2   e3   e4   e5   e6   e7\nTime:    0    2    4    6    8    10   12\nFixed(5):[0-5)    [5-10)      [10-15)\n         e1,e2,e3 e4,e5,e6    e7\n\nTriggers:\nDefault: Fire at watermark\nEarly:   Fire every 1min + at watermark\nLate:    Fire at watermark + allowedLateness window\n```",
    "code_dive": "See Java source files in src/main/java/com/dataeng/twelve/ for complete implementations:\n\n- WordCountPipeline.java: Complete batch pipeline with Beam options\n- StreamingPipeline.java: Kafka streaming with windowing and triggers\n- BeamPipelineValidator.java: Utility transforms for validation\n\nKey patterns:\n```java\nPipeline p = Pipeline.create(options);\np.apply(\"Read\", TextIO.read().from(input))\n .apply(\"Words\", FlatMapElements.via((String line) -> ...))\n .apply(\"Count\", Count.perElement())\n .apply(\"Write\", TextIO.write().to(output));\np.run().waitUntilFinish();\n\n// Streaming\np.apply(KafkaIO.read().withTopic(\"events\"))\n .apply(Window.into(FixedWindows.of(5, MINUTES))\n     .triggering(AfterWatermark.pastEndOfWindow()\n         .withEarlyFirings(AfterProcessingTime.pastFirstElementInPane())))\n .apply(Count.perKey());\n```",
    "step_by_step": [
        "Define Pipeline: create Pipeline instance with PipelineOptions",
        "Read Data: apply Read transform (TextIO, KafkaIO, JdbcIO)",
        "Transform: apply ParDo (element-wise), GroupByKey, Combine",
        "Window: apply Window into FixedWindows/SlidingWindows/Sessions for unbounded",
        "Trigger: configure trigger strategy (early, on-time, late firings)",
        "Write: apply Write transform to output results",
        "Run: specify runner via --runner=DirectRunner|FlinkRunner|DataflowRunner",
        "Monitor: use runner's monitoring UI (Flink web UI, Dataflow console)",
    ],
    "common_mistakes": [
        ("Missing Window for Unbounded", "Streaming pipelines without windowing cause indefinite state growth or no output"),
        ("Late Data Without AllowedLateness", "Late data is silently dropped by default; configure allowedLateness for late arrivals"),
        ("No Trigger Configuration", "Default trigger (watermark only) may not meet latency requirements; configure early firings"),
        ("Large State Without Cleanup", "Stateful DoFn must clear state periodically; use state TTL or explicit cleanup"),
        ("Wrong Runner Selection", "Not all runners support all features (e.g., state, timers, Splittable DoFn); check capability matrix"),
    ],
    "debugging": [
        ("DirectRunner Execution", "Use DirectRunner for local debugging: mvn compile exec:java -Dexec.args='--runner=DirectRunner'"),
        ("Metrics", "Use Metrics API: Counters, Distributions, Gauages for pipeline monitoring"),
        ("Logging", "Use @ProcessElement with LOG.debug() for element-level tracing; configure log level via --defaultSdkHarnessLogLevel"),
        ("Pipeline Graph", "View pipeline graph via runner UI; Dataflow: Pipeline Graph in Console; Flink: execution plan"),
    ],
    "refactoring": "## Composite Transforms\nBefore: Multiple inline transforms\nAfter: Extract into composite PTransform for reuse\n\n## Fusion Prevention\nBefore: Adjacent ParDos fused, no parallelism\nAfter: Insert Reshuffle.via() between heavy transforms\n\n## Schema-Based Processing\nBefore: Raw KV<String, String> with manual parsing\nAfter: Use Beam Schema with Row type for type-safe processing",
    "performance": [
        ("Fusion", "Beam fuses adjacent ParDo transforms automatically. Use Reshuffle.via() to prevent fusion and enable parallelism between heavy stages."),
        ("Parallelism", "Use .withHintMatchesManyFiles() for TextIO reads. Reshuffle after skewed transforms. Set --maxNumWorkers for auto-scaling runners."),
        ("Bundle Size", "Control bundle size via pipeline options. Smaller bundles = lower latency, more overhead. Larger bundles = higher throughput, more memory."),
        ("Serialization", "Use efficient coders (Avro, Proto, custom). Avoid Java serialization. Register custom coders for better performance."),
    ],
    "security": "## Credentials\n```java\npipeline.getOptions().as(DataflowPipelineOptions.class)\n    .setGcpCredential(credentials);\n```\n\n## Encryption\n- CMEK for disk encryption on Dataflow\n- KMS integration for encrypted connections\n- TLS for all data in transit\n\n## Network\n- VPC Service Controls for Dataflow\n- Private IP for workers (no public internet access)\n- Subnet configuration for data isolation",
    "architecture": "```\n[Pipeline] -> [Runner API] -> [Runner: Flink/Spark/Dataflow]\n    |                            |\n[SDK Harness]            [Execution Engine]\n[PCollections]           [Workers/Containers]\n[PTransforms]\n    |\n[User Code]\n\nPortability: SDK Harness <-> Fn API <-> Runner\nWrite once in Java/Python/Go, run on any runner\n```",
    "how_it_works": "1. Construct pipeline by applying PTransforms to PCollections\n2. Pipeline is a DAG of transforms with explicit inputs and outputs\n3. Runner translates pipeline to its native execution plan\n4. Runner executes transforms in topological order\n5. Elements flow through transforms in bundles (micro-batches)\n6. Windows group elements by time for unbounded data\n7. Triggers decide when to emit pane results\n8. Stateful DoFn maintains per-key state across elements\n9. Checkpointing enables fault tolerance for streaming pipelines\n10. Metrics emitted for monitoring and debugging",
    "why_exists": "Each stream processing engine (Spark Streaming, Flink, Kafka Streams, Samza) has its own API. Porting pipelines between engines requires complete rewrites. Beam provides a unified API that runs on any engine, enabling vendor independence, migration flexibility, and reduced lock-in.",
    "why_matters": "- **Portability**: Run same pipeline on Flink, Spark, Dataflow, Samza\n- **Unified Model**: Single API for batch and streaming with consistent semantics\n- **Language Choice**: Java, Python, Go, Scala, SQL\n- **Industry Backing**: Google, Cloudera, Talend, PayPal\n- **Extensibility**: Custom I/O via Splittable DoFn\n- **Schema Support**: Type-safe processing with Beam schemas",
    "package": "com.dataeng.twelve",
    "JavaSources": {},
    "Tests": {},
}
LABS["12-apache-beam"] = l12

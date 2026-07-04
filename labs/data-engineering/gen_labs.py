#!/usr/bin/env python3
import os, pathlib

BASE = r"C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\data-engineering"

labs = [
    "01-data-pipelines", "02-etl-processes", "03-data-warehousing",
    "04-apache-spark", "05-apache-flink", "06-kafka-streaming",
    "07-data-lake", "08-workflow-orchestration", "09-data-quality",
    "10-change-data-capture", "11-feature-stores", "12-data-governance"
]

files_24 = [
    "README.md","THEORY.md","WHY_IT_EXISTS.md","WHY_IT_MATTERS.md",
    "HISTORY.md","MENTAL_MODELS.md","HOW_IT_WORKS.md","INTERNALS.md",
    "MATH_FOUNDATION.md","VISUAL_GUIDE.md","CODE_DEEP_DIVE.md",
    "STEP_BY_STEP.md","COMMON_MISTAKES.md","DEBUGGING.md","REFACTORING.md",
    "PERFORMANCE.md","SECURITY.md","ARCHITECTURE.md","EXERCISES.md",
    "QUIZ.md","FLASHCARDS.md","INTERVIEW.md","REFLECTION.md","REFERENCES.md"
]

def write_if_missing(lab_dir, fname, content):
    fpath = os.path.join(lab_dir, fname)
    if not os.path.exists(fpath):
        with open(fpath, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"  WROTE: {fname}")
    else:
        print(f"  EXISTS: {fname}")

# =====================================================
# CONTENT GENERATORS
# =====================================================

def make_theory(lab_name, text):
    return f"# {lab_name} Theory\n\n{text}\n"

def make_why_exists(lab_name, text):
    return f"# Why {lab_name} Exists\n\n{text}\n"

def make_why_matters(lab_name, text):
    return f"# Why {lab_name} Matters\n\n{text}\n"

def make_history(lab_name, timeline, milestones):
    m = "\n".join(f"{i}. {ms}" for i, ms in enumerate(milestones,1))
    return f"# History of {lab_name}\n\n## Timeline\n{timeline}\n\n## Key Milestones\n{m}\n"

def make_internals(text):
    return f"# Internal Architecture\n\n{text}\n"

def make_math(text):
    return f"# Math Foundation\n\n{text}\n"

def make_visual(text):
    return f"# Visual Guide\n\n{text}\n"

def make_code_dive(text):
    return f"# Code Deep Dive\n\n{text}\n"

def make_step_by_step(text):
    return f"# Step-by-Step Guide\n\n{text}\n"

def make_common_mistakes(text):
    return f"# Common Mistakes\n\n{text}\n"

def make_debugging(text):
    return f"# Debugging\n\n{text}\n"

def make_refactoring(text):
    return f"# Refactoring\n\n{text}\n"

def make_performance(text):
    return f"# Performance Optimization\n\n{text}\n"

def make_security(text):
    return f"# Security\n\n{text}\n"

def make_architecture(text):
    return f"# Architecture\n\n{text}\n"

def make_exercises(items):
    m = "\n".join(f"## Exercise {i+1}: {t}\n{d}\n" for i,(t,d) in enumerate(items))
    return f"# Exercises\n\n{m}\n"

def make_quiz(qa):
    m = "\n".join(f"## Q{i+1}\n{q}\nA) {a[0]}\nB) {a[1]}\nC) {a[2]}\nD) {a[3]}\n" for i,(q,a) in enumerate(qa))
    ans = "\n".join(f"{i+1}: {a}" for i,(_,_,a) in enumerate(qa))
    return f"# Quiz\n\n{m}\n## Answer Key\n{ans}\n"

def make_flashcards(cards):
    m = "\n".join(f"## Card {i+1}\n**Front**: {f}\n**Back**: {b}\n" for i,(f,b) in enumerate(cards))
    return f"# Flashcards\n\n{m}\n"

def make_interview(qa):
    m = "\n".join(f"### {l}\n**Q**: {q}\n**A**: {a}\n" for l,q,a in qa)
    return f"# Interview Questions\n\n{m}\n"

def make_reflection(items):
    m = "\n".join(f"- {i}" for i in items)
    return f"# Reflection\n\n## Key Takeaways\n{m}\n"

def make_references(items):
    m = "\n".join(f"- {i}" for i in items)
    return f"# References\n\n{m}\n"

# =====================================================
# Lab 01: Data Pipelines
# =====================================================
def gen_lab01(base):
    d = os.path.join(base, "01-data-pipelines")
    write_if_missing(d, "THEORY.md", make_theory("Data Pipelines", """
Data pipelines are the backbone of modern data engineering, enabling movement and transformation of data across heterogeneous systems.

## Batch vs Streaming
- **Batch**: Processes bounded data in discrete chunks at scheduled intervals. Lower complexity, minutes to hours latency.
- **Streaming**: Processes unbounded data continuously as it arrives. Higher complexity, milliseconds latency.

## Architecture Patterns
1. **ETL** - Extract, Transform, Load (transform before loading)
2. **ELT** - Extract, Load, Transform (transform in warehouse)
3. **Data Lakehouse** - Combines data lake flexibility with warehouse reliability

## Java/Spring Implementation
```java
@Configuration
@EnableConfigurationProperties(PipelineProperties.class)
public class PipelineConfig {
    @Bean
    public DataSource sourceDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://source:5432/database")
            .username("reader")
            .password(System.getenv("DB_PASSWORD"))
            .build();
    }
    @Bean
    public JdbcCursorItemReader<Transaction> reader() {
        return new JdbcCursorItemReaderBuilder<Transaction>()
            .name("transactionReader")
            .dataSource(sourceDataSource())
            .sql("SELECT * FROM transactions WHERE processed = false")
            .rowMapper(new TransactionRowMapper())
            .build();
    }
}
```
"""))
    write_if_missing(d, "WHY_IT_EXISTS.md", make_why_exists("Data Pipelines", """
Organizations collect data from databases, APIs, logs, and IoT devices. Without pipelines, data remains siloed and unusable.

## Root Causes
1. Raw data is rarely queryable or analyzable in native format
2. Data needs to be **centralized** from distributed sources
3. Data needs to be **standardized** (formats, schemas)
4. Data needs to be **cleaned** and validated

## Real-World Analogy
A data pipeline is like a city water system - raw water (data) is collected from reservoirs (sources), treated (transformed), and distributed through pipes to consumers.

```java
Pipeline pipeline = PipelineBuilder.newInstance()
    .source(new PostgresSource("jdbc:postgresql://oltp-db:5432/orders"))
    .transform(new EnrichmentTransform("customer-360-api"))
    .sink(new SnowflakeSink("ANALYTICS.WAREHOUSE.FACT_ORDERS"))
    .schedule(CronExpression.EVERY_5_MINUTES)
    .build();
pipeline.start();
```
"""))
    write_if_missing(d, "WHY_IT_MATTERS.md", make_why_matters("Data Pipelines", """
## Business Impact
- **Data-Driven Decisions**: Clean reliable data enables accurate analytics
- **Operational Efficiency**: Automated pipelines replace manual data movement
- **Real-Time Insights**: Streaming enables instant response to events
- **Data Quality**: Pipelines enforce validation and schema enforcement

## Key Metrics
| Metric | Without Pipeline | With Pipeline |
|--------|-----------------|---------------|
| Time to insight | Days | Minutes |
| Data freshness | Stale | Near real-time |
| Error rate | High | < 0.1% |
| Engineering overhead | 80% wrangling | 20% wrangling |

```java
SparkSession spark = SparkSession.builder()
    .config("spark.sql.streaming.schemaInference", true)
    .config("spark.sql.streaming.checkpointLocation", "/checkpoints")
    .getOrCreate();
```
"""))
    write_if_missing(d, "HISTORY.md", make_history("Data Pipelines",
        "- **1970s**: Batch processing with mainframe COBOL\n- **1980s**: ETL tools (Informatica, DataStage)\n- **1990s**: Data warehousing (Inmon, Kimball)\n- **2000s**: Hadoop ecosystem\n- **2010s**: Spark unifies batch/streaming; Kafka enables real-time\n- **2020s**: Lakehouse, Data Mesh, DataOps",
        ["Google MapReduce paper published (2004)", "Hadoop created by Doug Cutting (2006)", "Apache Kafka at LinkedIn (2010)", "Apache Spark top-level project (2014)", "Apache Flink emerges (2016)", "Delta Lake open-sourced by Databricks (2019)"]))
    write_if_missing(d, "MENTAL_MODELS.md", make_flashcards([
        ("Assembly Line Model", "Each pipeline stage adds value - raw materials in, finished product out"),
        ("Water Flow Model", "Source=reservoir, Pipeline=pipes, Transform=filtration, Sink=storage"),
        ("DAG Model", "Nodes=processing steps, Edges=data dependencies, No cycles"),
        ("Pipes and Filters", "Each filter operates independently, pipes connect them")
    ]))
    write_if_missing(d, "HOW_IT_WORKS.md", """
# How Data Pipelines Work

## Core Mechanism
Data pipelines operate through interconnected processing stages:

## 1. Source Connectors
```java
@Repository
public class OrderRepository {
    private final JdbcTemplate jdbc;
    public List<Order> fetchUnprocessedOrders() {
        return jdbc.query(
            "SELECT * FROM orders WHERE processed_at IS NULL",
            new BeanPropertyRowMapper<>(Order.class));
    }
}
```

## 2. Transformation Layer
```java
Dataset<Row> enriched = orders
    .join(customers, orders.col("customer_id").equalTo(customers.col("id")))
    .withColumn("total_value", functions.expr("quantity * unit_price * (1 - discount)"))
    .withColumn("segment",
        functions.when(functions.col("total_value").$greater(1000), "PREMIUM")
                 .otherwise("STANDARD"));
```

## 3. Sink Connectors
```java
enriched.write().mode("append").jdbc(jdbcUrl, "fact_orders", props);
enriched.write().mode("append").json("s3://data-lake/orders/");
```

## Processing Semantics
- **At-most-once**: Fast but may lose data
- **At-least-once**: Reliable but may duplicate
- **Exactly-once**: Most reliable, highest overhead
""")
    write_if_missing(d, "MATH_FOUNDATION.md", make_math("""
## Throughput
Throughput = Records processed / Time unit
TPS = N / dt

## Latency Components
Total Latency = T_extract + T_transform + T_load + T_queue

## Pipeline Capacity
```java
public class PipelineCapacity {
    public static double calculateRequiredNodes(long dailyDataVolume, double rate, double sla) {
        return Math.ceil((dailyDataVolume / 24.0) / rate * (24.0 / sla));
    }
}
```

## Amdahl's Law
Speedup = 1 / ((1 - P) + P/N) where P = parallelizable portion, N = processors
"""))
    write_if_missing(d, "VISUAL_GUIDE.md", make_visual("""
## Pipeline Architecture
```
+---------+    +----------+    +-----------+    +----------+
| Source  |--->| Extract  |--->| Transform |--->|   Load   |
| Systems |    |  Layer   |    |   Layer   |    |   Layer  |
+---------+    +----------+    +-----------+    +----------+
```

## Streaming Pipeline
```
+----------+    +----------+    +-----------+    +----------+
| Producers|--->|  Kafka   |--->|  Stream   |--->| Consumer |
| (Java)   |    |  Broker  |    | Processor |    |  (Apps)  |
+----------+    +----------+    +-----------+    +----------+
```

## State Machine
PENDING -> RUNNING -> STOPPING -> COMPLETED
                   -> FAILED -> TIMEOUT -> ABORTED
"""))
    write_if_missing(d, "CODE_DEEP_DIVE.md", make_code_dive("""
## Complete Pipeline with Spring Batch

### Domain Model
```java
public class Transaction {
    private String id; private String userId;
    private BigDecimal amount; private LocalDateTime timestamp;
    private String status;
    // Getters and setters
}
```

### Item Processor
```java
public class TransactionProcessor
    implements ItemProcessor<Transaction, EnrichedTransaction> {
    private final CustomerService customerService;
    private final FraudDetectionClient fraudClient;

    @Override
    public EnrichedTransaction process(Transaction item) {
        Customer customer = customerService.findById(item.getUserId());
        BigDecimal riskScore = fraudClient.evaluate(item);
        EnrichedTransaction enriched = new EnrichedTransaction();
        enriched.setId(item.getId());
        enriched.setCustomerName(customer.getFullName());
        enriched.setSegment(customer.getSegment());
        enriched.setRiskScore(riskScore);
        return enriched;
    }
}
```

### Job Configuration
```java
@Bean
public Job transactionPipelineJob(JobRepository repo, Step step) {
    return new JobBuilder("transactionPipeline", repo)
        .start(step).listener(new PipelineMetricsListener()).build();
}
```
"""))
    write_if_missing(d, "STEP_BY_STEP.md", make_step_by_steps([
        ("Requirements Analysis", "Identify sources, define SLAs, map consumers"),
        ("Architecture Design", "Choose batch/streaming, define pipeline properties"),
        ("Source Connection", "Configure JDBC/Kafka/REST connectors"),
        ("Transformation Logic", "Implement cleaning, enrichment, aggregation"),
        ("Sink Configuration", "Write to warehouse, data lake, search index"),
        ("Monitoring", "Health checks, metrics, alerting")
    ]))
    write_if_missing(d, "COMMON_MISTAKES.md", make_common_mistakes("""
1. **Schema Evolution Ignorance**: Hard-coding column names instead of schema-aware access
2. **Not Handling Late Data**: Missing watermarks in streaming pipelines
3. **Missing Idempotency**: Duplicate data on retry instead of using MERGE
4. **No Backpressure**: Unbounded memory growth with infinite queues
5. **Hardcoded Credentials**: Secrets in code instead of environment/vault
"""))
    write_if_missing(d, "DEBUGGING.md", make_debugging("""
## Common Failures
1. **OOM**: Collecting large datasets to driver - use foreachPartition
2. **Serialization**: Task not serializable - use mapPartitions
3. **Data Skew**: Few tasks run much longer - salt keys
4. **Connection Leaks**: Monitor connection pool metrics

## Debugging Commands
```bash
open http://localhost:4040  # Spark UI
kafka-consumer-groups --describe --group pipeline-group
kubectl logs -l app=pipeline -n data-platform --tail=100 -f
```
"""))
    write_if_missing(d, "REFACTORING.md", make_refactoring("""
## Before: Monolithic Pipeline
```java
public class Pipeline { public void run() {
    // 500 lines mixed together
}}
```

## After: Modular
```java
@Component
public class PipelineOrchestrator {
    private final Extractor extractor;
    private final Transformer transformer;
    private final Loader loader;
    public PipelineResult run(PipelineContext ctx) {
        DataFrame raw = extractor.extract(ctx);
        DataFrame transformed = transformer.transform(raw);
        return loader.load(transformed, ctx);
    }
}
```

## Configuration Externalization
Move hardcoded values to application.properties.
"""))
    write_if_missing(d, "PERFORMANCE.md", make_performance("""
## Spark Tuning
```java
spark.conf().set("spark.sql.shuffle.partitions", "200");
spark.conf().set("spark.sql.adaptive.enabled", "true");
spark.conf().set("spark.sql.adaptive.skewJoin.enabled", "true");
```

## Caching
```java
Dataset<Row> lookup = spark.read().parquet("lookup/").cache();
lookup.persist(StorageLevel.MEMORY_AND_DISK_SER());
```

## Partition Pruning
```java
dataset.write().partitionBy("year","month","day").parquet("s3://data/");
spark.read().parquet("s3://data/").filter("year=2024 AND month=6");
```

## Broadcast Joins
```java
largeDF.join(smallDF.hint("broadcast"), "key");
```
"""))
    write_if_missing(d, "SECURITY.md", make_security("""
## Authentication
```java
spark.conf().set("spark.hadoop.fs.s3a.aws.credentials.provider",
    "com.amazonaws.auth.InstanceProfileCredentialsProvider");
String url = "jdbc:postgresql://host:5432/db?ssl=true&sslmode=verify-full";
```

## Data Encryption
```java
public Dataset<Row> encryptPII(Dataset<Row> data, String column, byte[] key) {
    return data.withColumn(column,
        functions.udf((String v) -> Base64.getEncoder().encodeToString(
            Cipher.getInstance("AES/GCM/NoPadding")
                .doFinal(v.getBytes(StandardCharsets.UTF_8))),
        DataTypes.StringType).apply(functions.col(column)));
}
```

## Secret Management
Use HashiCorp Vault or environment variables for secrets.
"""))
    write_if_missing(d, "ARCHITECTURE.md", make_architecture("""
## High-Level Architecture
```
+----------------------------------------------------+
|                Data Pipeline Platform               |
+------------+-----------+-----------+---------------+
| Ingestion  | Processing | Storage   | Serving       |
| Kafka      | Spark      | Data Lake | APIs          |
| JDBC       | Flink      | Warehouse | BI Tools      |
| REST APIs  | Spring Btch| Cache     | Dashboards    |
+------------+-----------+-----------+---------------+
```

## Component Diagram
Controller -> Service Orchestrator -> Extractor/Transformer/Loader
"""))
    write_if_missing(d, "EXERCISES.md", make_exercises([
        ("Build a Simple Batch Pipeline", "Spring Batch job reading CSV, writing to PostgreSQL"),
        ("Streaming Pipeline with Kafka", "Kafka stream processor with windowed aggregation"),
        ("Error Handling", "Implement retry and dead letter queue pattern"),
        ("Pipeline Monitoring", "Add metrics collection and health endpoints")
    ]))
    write_if_missing(d, "QUIZ.md", make_quiz([
        ("What is main diff between batch and stream?", ["Batch is continuous", "Stream is discrete", "Batch=bounded, Stream=unbounded", "No diff"], "C"),
        ("What does DAG stand for?", ["Data Aggregation Graph", "Directed Acyclic Graph", "Distributed App Grid", "Dynamic Access Gateway"], "B"),
        ("Purpose of checkpointing?", ["Speed up", "Fault recovery", "Reduce memory", "Improve quality"], "B")
    ]))
    write_if_missing(d, "FLASHCARDS.md", make_flashcards([
        ("What is a data pipeline?", "Series of steps moving data from sources to destinations"),
        ("What are the three ETL stages?", "Extract, Transform, Load"),
        ("What is exactly-once semantics?", "Each record processed exactly once even on failure"),
        ("What is a dead letter queue?", "Storage for failed messages for later reprocessing"),
        ("What is a watermark?", "Threshold for how late data can arrive in stream processing")
    ]))
    write_if_missing(d, "INTERVIEW.md", make_interview([
        ("Beginner", "Explain a data pipeline in simple terms.", "It moves data from collection to consumption, cleaning and transforming it along the way."),
        ("Intermediate", "Compare ETL vs ELT.", "ETL transforms before loading (good for complex transforms on smaller data). ELT loads raw first and transforms in warehouse (better for big data)."),
        ("Senior", "Design a real-time fraud detection pipeline.", "Kafka for ingestion, Flink/Spark for real-time feature computation, ML model serving, rules engine, feedback loop for retraining. 10K+ events/sec with sub-second latency.")
    ]))
    write_if_missing(d, "REFLECTION.md", make_reflection([
        "Data pipelines are fundamental infrastructure for all data engineering",
        "Understanding batch vs streaming tradeoffs is critical",
        "Pipeline reliability comes from idempotency, checkpointing, and monitoring",
        "Java/Spring ecosystem provides robust tooling for enterprise pipelines"
    ]))
    write_if_missing(d, "REFERENCES.md", make_references([
        '"Designing Data-Intensive Applications" by Martin Kleppmann',
        '"The Data Warehouse Toolkit" by Ralph Kimball',
        '"Streaming Systems" by Tyler Akidau et al.',
        "Apache Spark: https://spark.apache.org/docs/latest/",
        "Spring Batch: https://docs.spring.io/spring-batch/",
        "Apache Kafka: https://kafka.apache.org/documentation/"
    ]))
    print("Lab 01 done")

def make_step_by_steps(steps):
    m = "\n".join(f"### {i+1}. {t}\n{d}\n" for i,(t,d) in enumerate(steps))
    return f"# Step-by-Step Guide\n\n{m}\n"

# =====================================================
# Lab 02: ETL Processes
# =====================================================
def gen_lab02(base):
    d = os.path.join(base, "02-etl-processes")
    write_if_missing(d, "THEORY.md", make_theory("ETL Processes", """
ETL (Extract, Transform, Load) is the core process of data engineering.

## Three Phases
### Extract
- Full extraction: entire source each run
- Incremental: only changed data since last run
- CDC: capture inserts, updates, deletes

### Transform
- Cleaning: remove nulls, deduplicate, standardize
- Validation: check data types, ranges, integrity
- Enrichment: join reference data, derive fields
- Aggregation: summarize, compute metrics

### Load
- Full load: truncate and reload
- Incremental: append new/changed records
- Upsert/Merge: insert or update based on key

## ELT vs ETL
| Aspect | ETL | ELT |
|--------|-----|-----|
| Transform location | Staging area | Target warehouse |
| Data volume | Lower | Higher |
| Typical tools | Spark, Spring Batch | dbt, Snowflake |
"""))
    write_if_missing(d, "WHY_IT_MATTERS.md", make_why_matters("ETL", """
## Business Impact
- **Data Quality**: ETL ensures clean, reliable data for analysis
- **Performance**: Transformed data optimized for query performance
- **Governance**: Audit trails and data lineage
- **Automation**: Scheduled ETL eliminates manual preparation

```java
// Spark's Catalyst optimizer improves ETL performance
Dataset<Row> cleaned = spark.read()
    .option("inferSchema", "true")
    .json("s3://source/")
    .transform(this::cleanData)
    .transform(this::enrichData)
    .transform(this::validateData);
```
"""))
    write_if_missing(d, "HISTORY.md", make_history("ETL",
        "- **1970s**: Custom COBOL programs for data movement\n- **1990s**: Commercial ETL tools (Informatica PowerCenter)\n- **2000s**: Open-source ETL (Pentaho, Talend)\n- **2010s**: Cloud ETL (Fivetran, Stitch, Airbyte)\n- **2020s**: ELT paradigm with dbt",
        ["First data warehouse book by Bill Inmon (1992)", "Kimball's dimensional modeling (1996)", "Apache Spark DataFrame API (2012)", "dbt popularizes transform-in-warehouse (2016)"]))
    write_if_missing(d, "MENTAL_MODELS.md", make_flashcards([
        ("Factory Assembly Line", "Raw materials enter, each station adds value, quality checks at every stage"),
        ("Water Filtration System", "Intake (extract), sedimentation (clean), filtration (validate), chemical treatment (enrich)"),
        ("Kitchen Model", "Shopping=Extract, Prepping=Clean, Cooking=Transform, Plating=Load"),
        ("Data Pipeline as Code", "Declarative ETL using Java streams: source.stream().map().filter().collect()")
    ]))
    write_if_missing(d, "HOW_IT_WORKS.md", """
# How ETL Works

## Extraction Strategies
### JDBC Extraction
```java
@Component
public class JdbcExtractor {
    private final JdbcTemplate jdbc;
    public List<Order> extractIncremental(LocalDateTime lastRun) {
        return jdbc.query(
            "SELECT * FROM orders WHERE updated_at > ?",
            new Object[]{Timestamp.valueOf(lastRun)},
            new OrderRowMapper());
    }
}
```

### API Extraction
```java
@Component
public class ApiExtractor {
    private final RestTemplate rest;
    public List<Customer> extractCustomers(String cursor) {
        ResponseEntity<ApiResponse> resp = rest.exchange(
            "https://api.crm.com/v2/customers?cursor=" + cursor,
            HttpMethod.GET, null, ApiResponse.class);
        return resp.getBody().getRecords();
    }
}
```

## Transform Logic
```java
public Dataset<Row> applyBusinessRules(Dataset<Row> data) {
    return data
        .withColumn("customer_segment",
            functions.when(col("total_spend").$greater(10000), "VIP")
                     .when(col("total_spend").$greater(1000), "GOLD")
                     .otherwise("STANDARD"));
}
```

## Load Patterns
```java
// Upsert pattern
spark.sql("MERGE INTO target t USING staging s ON t.id = s.id " +
    "WHEN MATCHED THEN UPDATE SET * WHEN NOT MATCHED THEN INSERT *");
```
"""))
    write_if_missing(d, "INTERNALS.md", make_internals("""
## ETL Engine Components

### Metadata Management
```java
@Component
public class EtlMetadataManager {
    private final JdbcTemplate metaDb;
    public void recordRun(String job, EtlRunStatus status, long count) {
        metaDb.update("INSERT INTO etl_audit VALUES (?,?,?,?,?)",
            job, status.name(), count,
            Timestamp.valueOf(LocalDateTime.now().minusSeconds(duration)),
            Timestamp.valueOf(LocalDateTime.now()));
    }
}
```

### Staging Area
```java
@Component
public class StagingManager {
    public Path createStage(String jobId) throws IOException {
        Path stage = Paths.get("/tmp/etl/staging/", jobId);
        Files.createDirectories(stage);
        return stage;
    }
}
```

### Error Handling
```java
@Component
public class EtlErrorHandler {
    private final Queue<FailedRecord> dlq = new LinkedBlockingQueue<>(10000);
    public void handleFailedRecord(FailedRecord record) {
        if (record.getRetryCount() < 3) {
            record.incrementRetry(); executor.schedule(() -> retry(record), Duration.ofMinutes(5));
        } else { dlq.offer(record); alertService.sendAlert("ETL Dead Letter"); }
    }
}
```
"""))
    write_if_missing(d, "MATH_FOUNDATION.md", make_math("""
## Data Volume
Raw Data = Sum of source table sizes
Intermediate = Raw + Enrichment joins
Final = After aggregation/deduplication
Compression = 3:1 to 10:1 with Parquet/ORC

## Transformation Complexity
- O(n) for row-wise ops (filter, cast, case)
- O(n log n) for sorts and distinct
- O(n * m) for joins (hash join)

## Load Performance
```java
public static long estimateLoadTime(long records, int batchSize, long latency) {
    return (long)Math.ceil((double)records / batchSize) * latency;
}
```

## Parallelism
Optimal = min(SourcePartitions, TargetPartitions, AvailableCores * 2)
"""))
    write_if_missing(d, "VISUAL_GUIDE.md", make_visual("""
## ETL Pipeline Flow
```
Source Systems              ETL Engine               Target Systems
+----------+              +------------------+       +-----------+
| Oracle   |--->          |    Extract       |--->   | Data      |
|PostgreSQL|--->          |    Transform     |--->   | Warehouse |
|Salesforce|--->          |    Load          |       | Data Lake |
|REST APIs |--->          |  +------------+  |       | BI Tool   |
+----------+              |  | Staging    |  |       +-----------+
                          |  | Area       |  |
                          |  +------------+  |
                          +------------------+
```
"""))
    write_if_missing(d, "CODE_DEEP_DIVE.md", make_code_dive("""
## Complete Spring Batch ETL Job

### Configuration
```java
@Configuration
public class EtlJobConfig {
    @Bean
    public Job etlJob(JobRepository repo, Step extract, Step transform, Step load) {
        return new JobBuilder("customerETL", repo)
            .start(extract).next(transform).next(load)
            .listener(new EtlJobListener()).build();
    }
}
```

### Reader
```java
public class CustomerItemReader implements ItemReader<Customer> {
    private Iterator<Customer> customerIterator;
    @Override
    public Customer read() {
        if (customerIterator != null && customerIterator.hasNext()) return customerIterator.next();
        return null;
    }
    @PostConstruct
    public void init() { this.customerIterator = apiClient.fetchAllCustomers().iterator(); }
}
```

### Processor
```java
public class CustomerProcessor implements ItemProcessor<Customer, EnrichedCustomer> {
    @Override
    public EnrichedCustomer process(Customer c) {
        EnrichedCustomer ec = new EnrichedCustomer();
        ec.setCustomerId(c.getId());
        ec.setFullName(c.getFirstName() + " " + c.getLastName());
        ec.setEmail(c.getEmail().toLowerCase());
        ec.setSegment(c.getTotalOrders() > 50 ? "LOYAL" : c.getTotalOrders() > 10 ? "REGULAR" : "NEW");
        ec.setProcessedAt(LocalDateTime.now());
        return ec;
    }
}
```

### Writer
```java
public class CustomerWriter implements ItemWriter<EnrichedCustomer> {
    @Override
    public void write(Chunk<? extends EnrichedCustomer> chunk) {
        String sql = "MERGE INTO dim_customer t " +
            "USING (VALUES (?,?,?,?,?,?)) AS s(id,name,email,phone,ltv,segment) " +
            "ON t.customer_id = s.id " +
            "WHEN MATCHED THEN UPDATE SET name=s.name, email=s.email " +
            "WHEN NOT MATCHED THEN INSERT VALUES (s.id,s.name,s.email,s.phone,s.ltv,s.segment)";
        jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) {
                EnrichedCustomer c = chunk.getItems().get(i);
                ps.setString(1, c.getCustomerId()); ps.setString(2, c.getFullName());
                ps.setString(3, c.getEmail()); ps.setString(4, c.getNormalizedPhone());
                ps.setBigDecimal(5, c.getLifetimeValue()); ps.setString(6, c.getSegment());
            }
            public int getBatchSize() { return chunk.getItems().size(); }
        });
    }
}
```
"""))
    write_if_missing(d, "STEP_BY_STEP.md", make_step_by_steps([
        ("Design", "Map sources, define target schema, identify transforms, determine loading strategy"),
        ("Setup Project", "Add Spring Batch and Spark dependencies to pom.xml"),
        ("Configure Data Sources", "Set up source and target DataSource beans"),
        ("Implement Transformations", "Create transformation logic with Spark DataFrame API"),
        ("Implement Loading", "Configure JdbcBatchItemWriter for target"),
        ("Schedule", "Use @Scheduled or Airflow for job scheduling")
    ]))
    write_if_missing(d, "COMMON_MISTAKES.md", make_common_mistakes("""
1. **No Incremental Logic**: Always doing full refresh instead of incremental
2. **Ignoring Data Types**: Everything as string instead of proper schema
3. **Not Handling Failures**: No checkpoint or retry mechanism
4. **No Data Quality Checks**: Trusting source data without validation
"""))
    write_if_missing(d, "DEBUGGING.md", make_debugging("""
## Common Issues

### Data Type Mismatches
```java
sourceData.printSchema();
spark.conf().set("spark.sql.ansi.enabled", "false");
```

### Null Pointer Issues
```java
Dataset<Row> nullCounts = sourceData.select(
    sourceData.columns().stream()
        .map(c -> functions.sum(functions.when(col(c).isNull(),1).otherwise(0)).as(c+"_nulls"))
        .toArray(Column[]::new));
nullCounts.show();
```

### Connection Issues
```java
@EventListener(ApplicationReadyEvent.class)
public void testConnections() {
    try { jdbcTemplate.queryForObject("SELECT 1", Integer.class); }
    catch (Exception e) { log.error("DB unavailable: {}", e.getMessage()); }
}
```
"""))
    write_if_missing(d, "REFACTORING.md", make_refactoring("""
## From Procedural to Declarative
### Before
```java
// 20 duplicated ETL jobs
public void runEtl1() { /* 200 lines */ }
public void runEtl2() { /* 190 lines */ }
```

### After
```java
@Component
public abstract class BaseEtlJob {
    protected abstract Dataset<Row> extract();
    protected abstract Dataset<Row> transform(Dataset<Row>);
    protected abstract void load(Dataset<Row>);
    public void execute() { load(transform(extract())); }
}
```

## Configuration Extraction
### Before: Hardcoded SQL
### After: Configurable via properties
"""))
    write_if_missing(d, "PERFORMANCE.md", make_performance("""
## Partitioning
```java
data.repartition(functions.col("country")).sortWithinPartitions("order_date");
```

## Caching Reference Data
```java
spark.sqlContext().cacheTable("dim_country");
```

## Avoiding Shuffles
```java
Dataset<Row> fact = fact.repartition(200, col("dim_key"));
Dataset<Row> dim = dim.repartition(200, col("key"));
Dataset<Row> joined = fact.join(dim, "key");
```

## Memory Tuning
```bash
spark.sql.adaptive.enabled=true
spark.sql.adaptive.coalescePartitions.enabled=true
spark.sql.autoBroadcastJoinThreshold=104857600
```
"""))
    write_if_missing(d, "SECURITY.md", make_security("""
## Data Protection
### Column-Level Encryption
```java
public Dataset<Row> encryptPII(Dataset<Row> data, String col, byte[] key) {
    return data.withColumn(col, encryptColumn(functions.col(col), key));
}
```

### Access Control
```java
@PreAuthorize("hasRole('ETL_ADMIN')")
public void runSensitiveEtl() { }
```

### Audit Logging
```java
@Component
public class EtlAuditLogger {
    @EventListener
    public void onEtlEvent(EtlExecutionEvent event) {
        auditLogRepository.save(new AuditEntry(event.getJobName(), event.getUser(),
            event.getRecordsProcessed(), event.getStatus(), Instant.now()));
    }
}
```
"""))
    write_if_missing(d, "ARCHITECTURE.md", make_architecture("""
## High-Level Architecture
```
+------------------+    +------------------+    +------------------+
|   Source Layer   |    |   Staging Layer  |    |   Target Layer   |
| PostgreSQL       |    | Raw Tables       |    | Dimension Tables |
| Oracle           |--->| Staging Area     |--->| Fact Tables      |
| Salesforce API   |    | Temp Files       |    | Aggregate Views  |
+------------------+    +------------------+    +------------------+
```

## Spring Batch Architecture
```
Job Launcher -> Job -> Step -> ItemReader -> ItemProcessor -> ItemWriter
```
"""))
    write_if_missing(d, "EXERCISES.md", make_exercises([
        ("File-Based ETL", "Read CSV, validate, write to PostgreSQL"),
        ("Incremental Load", "CDC using last_updated timestamp"),
        ("Data Quality", "Reject records with null keys, negative amounts"),
        ("Multi-Source ETL", "Combine database, API, and file data"),
        ("Error Recovery", "Retry logic and dead letter queue")
    ]))
    write_if_missing(d, "QUIZ.md", make_quiz([
        ("What does ETL stand for?", ["Extract, Transfer, Load", "Extract, Transform, Load", "Evaluate, Transform, Log", "Extract, Test, Load"], "B"),
        ("Which load is best for historical tracking?", ["Truncate/reload", "SCD Type 2", "Append only", "Delete/insert"], "B"),
        ("Advantage of ELT over ETL?", ["Less code", "Leverages warehouse compute", "Works with any type", "Less storage"], "B")
    ]))
    write_if_missing(d, "FLASHCARDS.md", make_flashcards([
        ("What is ETL?", "Extract, Transform, Load - moving data from sources, transforming, loading to target"),
        ("ETL vs ELT?", "ETL transforms before loading; ELT loads raw and transforms in warehouse"),
        ("What is SCD?", "Slowly Changing Dimension - strategies for handling historical tracking"),
        ("What is incremental extraction?", "Extracting only changed data since last run"),
        ("What is a staging area?", "Intermediate storage for raw data before transformation")
    ]))
    write_if_missing(d, "INTERVIEW.md", make_interview([
        ("Beginner", "Purpose of staging area in ETL?", "Buffer zone for raw data before transformation - enables error recovery and validation"),
        ("Intermediate", "Handle SCD Type 2?", "New row per change with effective dates, old rows get end_date"),
        ("Senior", "Design ETL for 50TB daily data?", "Partitioned ingestion, Spark for distributed transform, Delta Lake for ACID, incremental CDC, multi-hop architecture")
    ]))
    write_if_missing(d, "REFLECTION.md", make_reflection([
        "ETL is the foundation of data warehousing and analytics",
        "Incremental processing is critical for scaling",
        "Data quality checks should be built into every ETL stage",
        "Monitoring and error handling are as important as ETL logic"
    ]))
    write_if_missing(d, "REFERENCES.md", make_references([
        '"The Data Warehouse ETL Toolkit" by Ralph Kimball',
        '"Building the Data Warehouse" by Bill Inmon',
        "Apache Spark: https://spark.apache.org",
        "Spring Batch: https://spring.io/projects/spring-batch",
        "dbt: https://www.getdbt.com",
        "Airbyte: https://airbyte.com"
    ]))
    print("Lab 02 done")

# =====================================================
# GENERATE ALL 12 LABS
# =====================================================
if __name__ == "__main__":
    gen_lab01(BASE)
    gen_lab02(BASE)
    # Add calls for remaining labs...
    print("\nDone generating files!")

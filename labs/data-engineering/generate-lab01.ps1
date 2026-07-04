$base = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\data-engineering\01-data-pipelines"

$files = @{}

$files["README.md"] = @"
# Data Pipelines

## Overview
A data pipeline is a series of data processing steps that move data from sources to destinations, transforming and enriching it along the way.

## Key Concepts
- **Batch Processing**: Processing data in discrete chunks at scheduled intervals
- **Stream Processing**: Processing data in real-time as it arrives
- **Pipeline Patterns**: Fan-out, Fan-in, Lambda architecture, Kappa architecture

## Java/Spark Example
```java
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;

public class DataPipelineExample {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
            .appName("DataPipelineExample")
            .master("local[*]")
            .getOrCreate();

        Dataset<Row> sourceData = spark.read()
            .option("header", "true")
            .csv("input/data.csv");

        Dataset<Row> transformed = sourceData
            .filter("value IS NOT NULL")
            .groupBy("category")
            .agg(org.apache.spark.sql.functions.avg("value").as("avg_value"));

        transformed.write()
            .mode("overwrite")
            .parquet("output/results/");

        Dataset<Row> streamData = spark.readStream()
            .format("kafka")
            .option("kafka.bootstrap.servers", "localhost:9092")
            .option("subscribe", "input-topic")
            .load();

        StreamingQuery query = streamData
            .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
            .writeStream()
            .format("console")
            .outputMode("append")
            .start();

        query.awaitTermination();
    }
}
```
"@

$files["THEORY.md"] = @"
# Theory of Data Pipelines

## Foundational Concepts
Data pipelines are the backbone of modern data engineering, enabling the movement and transformation of data across heterogeneous systems.

### Batch vs Streaming
| Aspect | Batch | Streaming |
|--------|-------|-----------|
| Latency | Minutes to hours | Milliseconds to seconds |
| Data Volume | Large, bounded | Continuous, unbounded |
| Processing | Periodic jobs | Continuous processing |
| Complexity | Lower | Higher |

### Pipeline Architecture Patterns
1. **Extract-Load-Transform (ELT)** - Extract raw data, load to warehouse, transform in place
2. **Extract-Transform-Load (ETL)** - Extract, transform before loading to target
3. **Data Lakehouse** - Combine data lake flexibility with warehouse reliability

## Java Implementation Patterns
```java
@Configuration
@EnableConfigurationProperties(PipelineProperties.class)
public class PipelineConfig {

    @Bean
    public DataSource sourceDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://source:5432/database")
            .username("reader")
            .password("password")
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
"@

$files["WHY_IT_EXISTS.md"] = @"
# Why Data Pipelines Exist

## The Problem
Organizations collect data from databases, APIs, logs, IoT devices, and SaaS platforms. Without pipelines, this data remains siloed and unusable for analytics.

## Root Cause
Raw data is rarely queryable, joinable, or analyzable in its native format. Pipelines exist to:
1. **Centralize** data from distributed sources
2. **Standardize** formats and schemas
3. **Clean** and validate data quality
4. **Enrich** with business context
5. **Deliver** to consumption tools

## Real-World Analogy
A data pipeline is like a city's water system - raw water (data) is collected from reservoirs (sources), treated (transformed), and distributed through pipes (channels) to consumers.

## Java/Spark Perspective
Pipelines bridge the gap between transactional systems (OLTP) and analytical systems (OLAP). Spring Batch provides robust batch processing while Spark Streaming handles real-time needs.

```java
Pipeline pipeline = PipelineBuilder.newInstance()
    .source(new PostgresSource("jdbc:postgresql://oltp-db:5432/orders"))
    .transform(new EnrichmentTransform("customer-360-api"))
    .sink(new SnowflakeSink("ANALYTICS.WAREHOUSE.FACT_ORDERS"))
    .schedule(CronExpression.EVERY_5_MINUTES)
    .build();
pipeline.start();
```
"@

$files["WHY_IT_MATTERS.md"] = @"
# Why Data Pipelines Matter

## Business Impact
1. **Data-Driven Decisions** - Clean, reliable data enables accurate analytics and ML models
2. **Operational Efficiency** - Automated pipelines replace manual data movement
3. **Real-Time Insights** - Streaming pipelines enable instant response to business events
4. **Data Quality** - Pipelines enforce validation, deduplication, and schema enforcement
5. **Cost Optimization** - Properly designed pipelines reduce storage and compute costs

## Key Metrics
| Metric | Without Pipeline | With Pipeline |
|--------|-----------------|---------------|
| Time to insight | Days | Minutes |
| Data freshness | Stale | Near real-time |
| Error rate | High | < 0.1% |
| Engineering overhead | 80% data wrangling | 20% data wrangling |

## Java/Spark Advantages
Spring Boot + Apache Spark provides a battle-tested stack for enterprise data pipelines with type safety, schema enforcement, fault tolerance via checkpointing, and exactly-once semantics.

```java
SparkSession spark = SparkSession.builder()
    .config("spark.sql.streaming.schemaInference", true)
    .config("spark.sql.streaming.checkpointLocation", "/checkpoints")
    .getOrCreate();
```
"@

$files["HISTORY.md"] = @"
# History of Data Pipelines

## Evolution Timeline
- **1970s**: Batch processing with mainframe COBOL programs
- **1980s**: ETL tools emerge (Informatica, DataStage)
- **1990s**: Data warehousing popularized by Bill Inmon and Ralph Kimball
- **2000s**: Hadoop ecosystem revolutionizes big data processing
- **2010s**: Spark unifies batch and streaming; Kafka enables real-time
- **2020s**: Lakehouse architecture, Data Mesh, DataOps

## Key Milestones
1. **2004**: Google MapReduce paper published
2. **2006**: Hadoop created by Doug Cutting
3. **2010**: Apache Kafka created at LinkedIn
4. **2014**: Apache Spark becomes top-level Apache project
5. **2016**: Apache Flink emerges for stream processing
6. **2019**: Delta Lake open-sourced by Databricks

## Java's Role
Java has been central to the data pipeline ecosystem:
- Apache Hadoop, Spark, Kafka, Flink are all JVM-based
- Spring Batch (est. 2007) standardizes enterprise batch processing
- Java 8+ streams influenced pipeline processing concepts

```java
@Bean
public Job pipelineJob(JobRepository repository, Step step) {
    return new JobBuilder("dataPipeline", repository)
        .start(step)
        .listener(new PipelineJobExecutionListener())
        .build();
}
```
"@

$files["MENTAL_MODELS.md"] = @"
# Mental Models for Data Pipelines

## 1. The Assembly Line Model
Each stage of a pipeline adds value:
- **Stage 1** (Extraction): Raw materials arrive
- **Stage 2** (Cleaning): Parts are inspected
- **Stage 3** (Transformation): Components are assembled
- **Stage 4** (Loading): Finished product delivered

## 2. The Water Flow Model
- **Source** = Reservoir (data originates)
- **Pipeline** = Pipes (data moves through channels)
- **Transform** = Filtration/Chemical treatment (data is cleaned)
- **Sink** = Storage tank (data consumed)

## 3. The DAG Model (Directed Acyclic Graph)
Every pipeline is a DAG where:
- **Nodes** = Processing steps
- **Edges** = Data dependencies
- **No cycles** = Prevents infinite processing

## 4. The Pipes and Filters Pattern
Each filter operates independently, pipes connect them:
```java
public class FilterProcessor {
    public Dataset<Row> process(Dataset<Row> input) {
        return input
            .filter("age >= 18")
            .filter("status = 'ACTIVE'")
            .filter("region IS NOT NULL");
    }
}
```
"@

$files["HOW_IT_WORKS.md"] = @"
# How Data Pipelines Work

## Core Mechanism
Data pipelines operate through interconnected processing stages:

### 1. Source Connectors
```java
@Repository
public class OrderRepository {
    private final JdbcTemplate jdbc;

    public List<Order> fetchUnprocessedOrders() {
        return jdbc.query(
            "SELECT * FROM orders WHERE processed_at IS NULL",
            new BeanPropertyRowMapper<>(Order.class)
        );
    }
}
```

### 2. Transformation Layer
```java
Dataset<Row> enriched = orders
    .join(customers, orders.col("customer_id").equalTo(customers.col("id")))
    .withColumn("total_value",
        functions.expr("quantity * unit_price * (1 - discount)"))
    .withColumn("segment",
        functions.when(functions.col("total_value").$greater(1000), "PREMIUM")
                 .otherwise("STANDARD"));
```

### 3. Sink Connectors
```java
enriched.write().mode("append").jdbc(jdbcUrl, "fact_orders", props);
enriched.write().mode("append").json("s3://data-lake/orders/");
```

### Processing Semantics
- **At-most-once**: Fast but may lose data
- **At-least-once**: Reliable but may duplicate
- **Exactly-once**: Most reliable, highest overhead
"@

$files["INTERNALS.md"] = @"
# Internal Architecture of Data Pipelines

## Pipeline Runtime Components

### Process Manager
```java
public class PipelineOrchestrator {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Map<String, PipelineStage> stages = new ConcurrentHashMap<>();

    public CompletableFuture<PipelineResult> execute(Pipeline pipeline) {
        List<CompletableFuture<PipelineResult>> futures = pipeline.getStages()
            .stream()
            .map(stage -> CompletableFuture
                .supplyAsync(() -> stage.process(pipeline.getData()), executor)
                .thenApply(this::validateOutput))
            .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> PipelineResult.success());
    }
}
```

### Checkpointing System
```java
public class WalCheckpointer {
    private final Path walPath;

    public void checkpoint(String pipelineId, long offset, byte[] state) {
        Path checkpointFile = walPath.resolve(pipelineId + ".wal");
        try (FileOutputStream fos = new FileOutputStream(checkpointFile.toFile(), true)) {
            fos.write(longToBytes(offset));
            fos.write(state);
        }
    }
}
```

### Backpressure Mechanism
```java
@Bean
public Flux<DataRecord> backpressuredPipeline() {
    return Flux.from(sourceStream)
        .onBackpressureBuffer(10000,
            dropped -> log.warn("Dropped {} records due to backpressure", dropped))
        .publishOn(Schedulers.parallel())
        .transform(this::processRecord);
}
```
"@

$files["MATH_FOUNDATION.md"] = @"
# Math Foundation for Data Pipelines

## Data Flow Metrics

### Throughput Calculation
```
Throughput = Records processed / Time unit
TPS (Transactions/sec) = N / At

For parallel pipelines:
Total Throughput = S(TP_i) for i = 1 to n workers
```

### Latency Components
```
Total Latency = T_extract + T_transform + T_load + T_queue
```

### Pipeline Capacity Planning
```java
public class PipelineCapacity {
    public static double calculateRequiredNodes(
            long dailyDataVolume, double processingRate, double slaHours) {
        double nodes = (dailyDataVolume / 24.0) / processingRate;
        nodes = nodes * (24.0 / slaHours);
        return Math.ceil(nodes);
    }
}
```

### Amdahl's Law for Pipelines
```
Speedup = 1 / ((1 - P) + P/N)
Where P = parallelizable portion, N = number of processors
```

### Probability of Pipeline Failure
```
P(failure) = 1 - P(1 - p_i) for i stages
MTBF = 1 / lambda (where lambda = failure rate)
Availability = MTBF / (MTBF + MTTR)
```
"@

$files["VISUAL_GUIDE.md"] = @"
# Visual Guide to Data Pipelines

## Pipeline Architecture Diagram
```
+---------+    +----------+    +-----------+    +----------+
| Source  |--->| Extract  |--->| Transform |--->|   Load   |
| Systems |    |  Layer   |    |   Layer   |    |   Layer  |---> Data Warehouse
+---------+    +----------+    +-----------+    +----------+
     |               |               |               |
     v               v               v               v
  PostgreSQL     Kafka Topic     Spark Job       Snowflake
  Oracle         S3 Events      Flink Job       Redshift
  REST APIs      File Drops    Spring Batch     BigQuery
```

## Streaming Pipeline Flow
```
+----------+    +----------+    +-----------+    +----------+
| Producers|--->|  Kafka   |--->|  Stream   |--->| Consumer |
| (Java)   |    |  Broker  |    | Processor |    |  (Apps)  |
+----------+    +----------+    +-----------+    +----------+
     |               |               |               |
  Send events    Partitioned    Exactly-once     Real-time
  via Kafka      & replicated   processing       dashboards
```

## Pipeline State Machine
```
                  +-----------------------------+
                  |                             |
                  v                             |
+--------+   +---------+   +--------+   +----------+
|PENDING |-->| RUNNING |-->|STOPPING|-->| COMPLETED|
+--------+   +---------+   +--------+   +----------+
                  |
    +-------------+-------------+
    v             v             v
+--------+  +----------+  +--------+
|FAILED  |  |TIMEOUT   |  |ABORTED |
+--------+  +----------+  +--------+
```
"@

$files["CODE_DEEP_DIVE.md"] = @"
# Code Deep Dive: Data Pipeline Implementation

## Complete Java Pipeline with Spring Batch

### Domain Model
```java
public class Transaction {
    private String id;
    private String userId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String status;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

### Custom Item Processor
```java
public class TransactionProcessor implements ItemProcessor<Transaction, EnrichedTransaction> {

    private final CustomerService customerService;
    private final FraudDetectionClient fraudClient;

    public TransactionProcessor(CustomerService customerService, FraudDetectionClient fraudClient) {
        this.customerService = customerService;
        this.fraudClient = fraudClient;
    }

    @Override
    public EnrichedTransaction process(Transaction item) {
        Customer customer = customerService.findById(item.getUserId());
        BigDecimal riskScore = fraudClient.evaluate(item);

        EnrichedTransaction enriched = new EnrichedTransaction();
        enriched.setId(item.getId());
        enriched.setCustomerName(customer.getFullName());
        enriched.setCustomerSegment(customer.getSegment());
        enriched.setAmount(item.getAmount());
        enriched.setRiskScore(riskScore);
        enriched.setProcessedAt(LocalDateTime.now());

        if (riskScore.compareTo(new BigDecimal("0.8")) > 0) {
            enriched.setFlag("FRAUD_REVIEW");
        }
        return enriched;
    }
}
```

### Pipeline Configuration
```java
@Configuration
public class TransactionPipelineConfig {

    @Bean
    public Job transactionPipelineJob(
            JobRepository jobRepository, Step extractStep, Step transformStep, Step loadStep) {
        return new JobBuilder("transactionPipeline", jobRepository)
            .start(extractStep)
            .next(transformStep)
            .next(loadStep)
            .listener(new PipelineMetricsListener())
            .build();
    }

    @Bean
    public Step extractStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager, ItemReader<Transaction> reader) {
        return new StepBuilder("extractStep", jobRepository)
            .<Transaction, Transaction>chunk(1000, transactionManager)
            .reader(reader)
            .writer(items -> log.info("Extracted {} records", items.size()))
            .build();
    }
}
```

### Metrics and Monitoring
```java
@Component
public class PipelineMetricsListener extends JobExecutionListenerSupport {
    private final MeterRegistry meterRegistry;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        meterRegistry.counter("pipeline.started",
            "name", jobExecution.getJobInstance().getJobName()).increment();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            meterRegistry.counter("pipeline.completed").increment();
        } else {
            meterRegistry.counter("pipeline.failed").increment();
        }
        long duration = jobExecution.getEndTime().getTime() -
                       jobExecution.getStartTime().getTime();
        meterRegistry.timer("pipeline.duration")
            .record(Duration.ofMillis(duration));
    }
}
```
"@

$files["STEP_BY_STEP.md"] = @"
# Step-by-Step Guide to Building Data Pipelines

## Phase 1: Requirements Analysis
1. Identify data sources (databases, APIs, files, streams)
2. Define data volume and velocity requirements
3. Determine freshness SLAs (real-time, hourly, daily)
4. Map data consumers and their formats

## Phase 2: Architecture Design
```java
@ConfigurationProperties(prefix = "pipeline")
public class PipelineProperties {
    private String sourceType;
    private String sinkType;
    private int batchSize = 1000;
    private int parallelism = 4;
    private String checkpointDir = "/tmp/checkpoints/";
    private String cronSchedule = "0 */5 * * * *";
}
```

## Phase 3: Source Connection Setup
```
spring.datasource.url=jdbc:postgresql://oltp-server:5432/source_db
spring.datasource.username=pipeline_user
spring.datasource.password=${PIPELINE_DB_PASSWORD}
spring.cloud.stream.bindings.input.destination=source-topic
spring.cloud.stream.kafka.binder.brokers=localhost:9092
```

## Phase 4: Transformation Logic
```java
public class DataTransformer {
    public Dataset<Row> cleanData(Dataset<Row> raw) {
        return raw
            .na().drop("any")
            .dropDuplicates("id")
            .filter("amount > 0")
            .withColumn("created_date", functions.to_date(functions.col("created_at")));
    }

    public Dataset<Row> enrichData(Dataset<Row> clean) {
        return clean
            .withColumn("tax", functions.expr("amount * 0.08"))
            .withColumn("total_with_tax", functions.expr("amount + (amount * 0.08)"))
            .withColumn("year_month", functions.date_format(functions.col("created_date"), "yyyyMM"));
    }
}
```

## Phase 5: Sink Configuration
```java
public class MultiSinkWriter {
    public void write(Dataset<Row> data) {
        data.write().mode("append").parquet("s3a://datalake/transactions/");
        data.write().mode("append").jdbc(warehouseUrl, "fact_transactions", connectionProps);
        data.write().format("org.elasticsearch.spark.sql")
            .option("es.resource", "transactions/_doc")
            .save();
    }
}
```

## Phase 6: Monitoring
```java
@RestController
@RequestMapping("/api/pipeline")
public class PipelineController {
    @GetMapping("/health")
    public ResponseEntity<PipelineHealth> health() {
        return ResponseEntity.ok(new PipelineHealth(
            pipelineService.getStatus(), pipelineService.getProcessedCount(),
            pipelineService.getErrorCount(), pipelineService.getLagBytes()));
    }
}
```
"@

$files["COMMON_MISTAKES.md"] = @"
# Common Mistakes in Data Pipelines

## 1. Schema Evolution Ignorance
```java
// WRONG - brittle
dataset.select("col1", "col2", "col3");

// RIGHT - schema-aware
StructType schema = dataset.schema();
if (schema.fieldNames().contains("new_col")) {
    dataset = dataset.withColumn("new_col", functions.lit("default"));
}
```

## 2. Not Handling Late Data
```java
// WRONG - assumes ordered arrival
Dataset<Row> result = stream
    .groupBy(functions.window(col("timestamp"), "1 hour"))
    .count();

// RIGHT - handles late data with watermark
Dataset<Row> result = stream
    .withWatermark("timestamp", "10 minutes")
    .groupBy(functions.window(col("timestamp"), "1 hour"))
    .count();
```

## 3. Missing Idempotency
```java
// WRONG - always inserts
stmt.execute("INSERT INTO target VALUES (...)");

// RIGHT - idempotent merge
stmt.execute("MERGE INTO target t " +
    "USING (VALUES (...)) s " +
    "ON t.id = s.id " +
    "WHEN MATCHED THEN UPDATE SET ... " +
    "WHEN NOT MATCHED THEN INSERT ...");
```

## 4. No Backpressure Handling
```java
// WRONG - infinite queue
BlockingQueue<Record> queue = new LinkedBlockingQueue<>();

// RIGHT - bounded queue with rejection
BlockingQueue<Record> queue = new ArrayBlockingQueue<>(10000);
```

## 5. Hardcoding Connection Strings
```java
// WRONG
String url = "jdbc:postgresql://localhost:5432/db?user=admin&pass=password";

// RIGHT
String url = System.getenv("DB_URL");
String pass = vaultService.getSecret("db/password");
```
"@

$files["DEBUGGING.md"] = @"
# Debugging Data Pipelines

## Common Failure Modes

### OOM Errors
```java
// WRONG - collects all to driver
List<Row> allRows = dataset.collectAsList();

// RIGHT - process per partition
dataset.foreachPartition((Iterator<Row> it) -> {
    while (it.hasNext()) { processRow(it.next()); }
});
```

### Serialization Errors
```java
// Symptom: Task not serializable
// WRONG
SomeService service = new SomeService();
dataset.map(row -> service.process(row));

// RIGHT - initialize per partition
dataset.mapPartitions((Iterator<Row> it) -> {
    SomeService service = new SomeService();
    return new Iterator<String>() {
        public boolean hasNext() { return it.hasNext(); }
        public String next() { return service.process(it.next()); }
    };
}, Encoders.STRING());
```

### Data Skew
```java
// Diagnose skew
dataset.groupBy("key").count().orderBy(functions.desc("count")).show(10);

// Fix with salting
Dataset<Row> salted = dataset
    .withColumn("salt", (functions.rand() * 10).cast("int"))
    .withColumn("salted_key",
        functions.concat(functions.col("key"), functions.lit("_"), functions.col("salt")));
```

### Debugging Commands
```bash
# Spark UI
open http://localhost:4040

# Kafka consumer lag
kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group pipeline-group --describe

# Pipeline logs
kubectl logs -l app=pipeline -n data-platform --tail=100 -f
```
"@

$files["REFACTORING.md"] = @"
# Refactoring Data Pipelines

## From Monolith to Modular
### Before
```java
public class Pipeline {
    public void run() {
        // 500 lines of extract, transform, load all mixed together
    }
}
```

### After
```java
@Component
public class PipelineOrchestrator {
    private final Extractor extractor;
    private final Transformer transformer;
    private final Loader loader;
    private final MetricsCollector metrics;

    public PipelineResult run(PipelineContext ctx) {
        metrics.startTimer();
        DataFrame raw = extractor.extract(ctx);
        DataFrame transformed = transformer.transform(raw);
        LoadResult result = loader.load(transformed, ctx);
        metrics.record(ctx, result);
        return result;
    }
}
```

## Configuration Externalization
### Before
```java
jdbcTemplate.query("SELECT * FROM orders WHERE date > '2024-01-01'");
```

### After
```java
@Value("${pipeline.extract.query}")
private String extractQuery;
jdbcTemplate.query(extractQuery);
```

## From Batch to Streaming
### Before
```java
Dataset<Row> daily = spark.read().parquet("s3://data/2024/01/01/");
```

### After
```java
Dataset<Row> stream = spark.readStream()
    .format("delta")
    .option("maxFilesPerTrigger", 10)
    .table("events");
stream.groupBy("type").count();
```
"@

$files["PERFORMANCE.md"] = @"
# Performance Optimization for Data Pipelines

## Spark Performance Tuning
```java
SparkSession spark = SparkSession.builder()
    .config("spark.sql.shuffle.partitions", "200")
    .config("spark.sql.adaptive.enabled", "true")
    .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
    .config("spark.sql.adaptive.skewJoin.enabled", "true")
    .config("spark.sql.adaptive.localShuffleReader.enabled", "true")
    .getOrCreate();
```

## Caching Strategies
```java
Dataset<Row> lookupTable = spark.read().parquet("lookup/");
lookupTable.cache();
lookupTable.persist(StorageLevel.MEMORY_AND_DISK_SER());
```

## Partition Pruning
```java
dataset.write()
    .partitionBy("year", "month", "day")
    .parquet("s3://data-lake/events/");

Dataset<Row> filtered = spark.read()
    .parquet("s3://data-lake/events/")
    .filter("year = 2024 AND month = 6");
```

## Broadcast Joins
```java
Dataset<Row> result = largeTable
    .join(smallTable.hint("broadcast"), "country_id");
```

## JVM Tuning
```bash
--executor-memory 8g
--driver-memory 4g
--conf spark.executor.memoryOverhead=2g
--conf spark.memory.offHeap.enabled=true
--conf spark.executor.extraJavaOptions="-XX:+UseG1GC -XX:InitiatingHeapOccupancyPercent=35"
```
"@

$files["SECURITY.md"] = @"
# Security in Data Pipelines

## Authentication and Authorization
```java
// Kerberos authentication
spark.conf().set("spark.hadoop.fs.s3a.aws.credentials.provider",
    "com.amazonaws.auth.InstanceProfileCredentialsProvider");

// JDBC with SSL
String url = "jdbc:postgresql://host:5432/db?ssl=true&sslmode=verify-full";
Properties props = new Properties();
props.setProperty("user", System.getenv("DB_USER"));
props.setProperty("password", vaultService.getSecret("db/password"));
```

## Data Encryption
```java
public class DataEncryptor {
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    public Dataset<Row> encryptPII(Dataset<Row> data, String column, byte[] key) {
        return data.withColumn(column,
            functions.udf((String value) -> {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
                byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
                return Base64.getEncoder().encodeToString(encrypted);
            }, DataTypes.StringType).apply(functions.col(column)));
    }
}
```

## Secret Management
```java
@Configuration
public class VaultConfiguration {
    @Bean
    public VaultTemplate vaultTemplate() {
        return new VaultTemplate(
            new VaultEndpoint("vault.internal", 8200, true),
            new AppRoleAuthentication(
                System.getenv("VAULT_ROLE_ID"),
                System.getenv("VAULT_SECRET_ID")));
    }
}
```
"@

$files["ARCHITECTURE.md"] = @"
# Architecture of Data Pipelines

## High-Level Architecture
```
+---------------------------------------------------------+
|                    Data Pipeline Platform                |
+--------------+--------------+--------------+-------------+
|  Ingestion   |  Processing  |  Storage     |  Serving    |
|  Layer       |  Layer       |  Layer       |  Layer      |
+--------------+--------------+--------------+-------------+
|  Kafka       |  Spark       |  Data Lake   |  APIs       |
|  Kinesis     |  Flink       |  Warehouse   |  BI Tools   |
|  JDBC        |  Spring Batch|  Cache       |  Dashboards |
|  REST APIs   |  Beam        |  Search      |  ML Models  |
+--------------+--------------+--------------+-------------+
```

## Component Diagram
```
                    +---------------+
                    |  Controller   |
                    |  (REST API)   |
                    +-------+-------+
                            |
                    +-------v-------+
                    |  Service      |
                    |  Orchestrator |
                    +-------+-------+
                            |
        +-------------------+-------------------+
        |                   |                   |
+-------v-------+   +-------v-------+   +-------v-------+
|  Extractor    |   | Transformer   |   |    Loader     |
|  (Reader)     |   | (Processor)   |   |   (Writer)    |
+-------+-------+   +-------+-------+   +-------+-------+
        |                   |                   |
+-------v-------+   +-------v-------+   +-------v-------+
|  Source       |   |  Business     |   |   Target      |
|  Connectors   |   |  Logic        |   |   Connectors  |
+---------------+   +---------------+   +---------------+
```

## Spring Boot Pipeline Application
```java
@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
public class PipelineApplication {
    public static void main(String[] args) {
        SpringApplication.run(PipelineApplication.class, args);
    }
}
```
"@

$files["EXERCISES.md"] = @"
# Exercises for Data Pipelines

## Exercise 1: Build a Simple Batch Pipeline
Create a Spring Batch pipeline that reads CSV files and writes to PostgreSQL.
```java
// TODO:
// 1. Configure FlatFileItemReader for CSV
// 2. Create ItemProcessor to transform data
// 3. Configure JdbcBatchItemWriter
// 4. Build Job with steps
```

## Exercise 2: Streaming Pipeline with Kafka
Build a Kafka stream processor that aggregates events by window.
```java
// TODO:
// 1. Create Kafka producer configuration
// 2. Read from topic with Spark streaming
// 3. Apply tumbling window of 5 minutes
// 4. Write aggregated results to console
```

## Exercise 3: Error Handling
Implement retry and dead letter queue pattern.
```java
// TODO:
// 1. Configure retry template
// 2. Skip policy for recoverable errors
// 3. Dead letter queue for unrecoverable errors
// 4. Alert on failure threshold
```

## Exercise 4: Pipeline Monitoring
Add metrics collection and health endpoints.
```java
// TODO:
// 1. Record processing time per record
// 2. Count successes and failures
// 3. Expose Prometheus metrics
// 4. Create health check endpoint
```
"@

$files["QUIZ.md"] = @"
# Data Pipelines Quiz

## Question 1
What is the main difference between batch and stream processing?
- A) Batch processes data continuously
- B) Stream processes data in discrete intervals
- C) Batch processes bounded data, stream processes unbounded data
- D) There is no difference

## Question 2
Which Apache project is most commonly used for distributed stream processing?
- A) Hadoop
- B) Spark
- C) HBase
- D) Hive

## Question 3
What does DAG stand for in the context of data pipelines?
- A) Data Aggregation Graph
- B) Directed Acyclic Graph
- C) Distributed Application Grid
- D) Dynamic Access Gateway

## Question 4
Which of the following is NOT a data pipeline pattern?
- A) Fan-out
- B) Fan-in
- C) Lambda architecture
- D) Microservice mesh

## Question 5
What is the purpose of checkpointing in streaming pipelines?
- A) Speed up processing
- B) Enable fault recovery
- C) Reduce memory usage
- D) Improve data quality

## Answer Key
1: C, 2: B, 3: B, 4: D, 5: B
"@

$files["FLASHCARDS.md"] = @"
# Flashcards: Data Pipelines

## Card 1
**Front**: What is a data pipeline?
**Back**: A series of data processing steps that move data from sources to destinations, transforming and enriching it along the way.

## Card 2
**Front**: What are the three main stages of an ETL pipeline?
**Back**: Extract (read from source), Transform (process/clean), Load (write to target)

## Card 3
**Front**: What is the difference between batch and stream processing?
**Back**: Batch processes finite data sets on a schedule; stream processes continuous data in real-time.

## Card 4
**Front**: What is a DAG in pipeline processing?
**Back**: Directed Acyclic Graph - a graph of processing steps where edges represent data dependencies and cycles are forbidden.

## Card 5
**Front**: What is backpressure?
**Back**: A flow control mechanism where downstream components signal upstream to slow down when they can't keep up.

## Card 6
**Front**: What is exactly-once semantics?
**Back**: A processing guarantee that each record is processed exactly one time, even in the event of failures.

## Card 7
**Front**: What is a dead letter queue?
**Back**: A storage location for messages that fail processing, allowing for later inspection and reprocessing.

## Card 8
**Front**: What is the role of Apache Kafka in data pipelines?
**Back**: Kafka serves as a distributed event streaming platform that acts as the central nervous system for data pipelines.

## Card 9
**Front**: What is a watermark in stream processing?
**Back**: A threshold that defines how late data can arrive and still be included in window computations.

## Card 10
**Front**: What is schema evolution in pipelines?
**Back**: The ability of a pipeline to handle changes in data schema over time without breaking.
"@

$files["INTERVIEW.md"] = @"
# Interview Questions: Data Pipelines

## Beginner
**Q**: Explain what a data pipeline is in simple terms.
**A**: A data pipeline is like an automated assembly line for data - it moves information from where it's collected to where it's used, cleaning and transforming it along the way.

## Intermediate
**Q**: Compare ETL and ELT approaches.
**A**: ETL transforms data before loading to the target, good for complex transformations on smaller data. ELT loads raw data first and transforms in the warehouse, better for big data since the warehouse handles transformation at scale.

## Advanced
**Q**: How would you implement exactly-once processing in a streaming pipeline?
**A**: This requires: 1) Idempotent sinks 2) Transactional source offset management 3) Checkpointing for state recovery. In Spark + Kafka, this means using the Kafka transaction API for offset commits alongside sink idempotency.

## Senior
**Q**: Design a data pipeline for a real-time fraud detection system.
**A**: Key components: 1) Kafka for event ingestion 2) Flink/Spark for real-time feature computation 3) ML model serving layer 4) Rules engine for threshold alerts 5) Feedback loop for model retraining. Must handle 10K+ events/sec with sub-second latency.
"@

$files["REFLECTION.md"] = @"
# Reflection: Data Pipelines

## Key Learnings
- Data pipelines are the fundamental infrastructure for all data engineering
- Understanding batch vs streaming tradeoffs is critical
- Pipeline reliability comes from idempotency, checkpointing, and monitoring
- Java/Spring ecosystem provides robust tooling for enterprise pipelines

## Questions to Consider
1. What's the right balance between batch and stream processing for your use case?
2. How do you ensure data quality as your pipeline scales?
3. What's your disaster recovery strategy if the pipeline fails?
4. How do you handle schema evolution across hundreds of tables?

## Connection to Other Labs
- Each lab builds on pipeline fundamentals
- ETL processes are specialized pipeline implementations
- Streaming platforms (Kafka, Flink) are pipeline components
- Data quality checks should be embedded in every pipeline stage
"@

$files["REFERENCES.md"] = @"
# References: Data Pipelines

## Books
- "Designing Data-Intensive Applications" by Martin Kleppmann
- "The Data Warehouse Toolkit" by Ralph Kimball
- "Streaming Systems" by Tyler Akidau, Slava Chernyak, Reuven Lax

## Documentation
- Apache Spark: https://spark.apache.org/docs/latest/
- Spring Batch: https://docs.spring.io/spring-batch/docs/current/reference/
- Apache Kafka: https://kafka.apache.org/documentation/

## Papers
- "MapReduce: Simplified Data Processing on Large Clusters" (Dean & Ghemawat, 2004)
- "Apache Spark: A Unified Engine for Big Data Processing" (Zaharia et al., 2016)
- "MillWheel: Fault-Tolerant Stream Processing at Internet Scale" (Akidau et al., 2013)

## Online Resources
- Data Engineering Weekly Newsletter
- SE Daily Podcast - Data Engineering episodes
- Confluent Blog - Streaming Pipeline Patterns
- Databricks Engineering Blog
"@

foreach ($file in $files.Keys) {
    $content = $files[$file]
    $path = Join-Path $base $file
    $content | Out-File -FilePath $path -Encoding utf8
    Write-Host "Created: $path"
}

Write-Host "Lab 01 complete - all 24 files created"

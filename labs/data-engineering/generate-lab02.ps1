$base = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\data-engineering\02-etl-processes"

$files = @{}

$files["README.md"] = @"
# ETL Processes

## Overview
ETL (Extract, Transform, Load) is the core process of data engineering - extracting data from sources, transforming it into a usable format, and loading it into a target system.

## Key Concepts
- **Extraction**: Reading data from various source systems
- **Transformation**: Cleaning, enriching, and restructuring data
- **Loading**: Writing processed data to the target destination

## Java/Spark Example
```java
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;

public class EtlJob {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
            .appName("ETLJob")
            .master("local[*]")
            .getOrCreate();

        // Extract
        Dataset<Row> source = spark.read()
            .option("header", "true")
            .csv("s3://source-bucket/raw/");

        // Transform
        Dataset<Row> transformed = source
            .withColumn("processed_date", functions.current_date())
            .withColumn("amount_cleaned",
                functions.regexp_replace(functions.col("amount"), "[$,]", "").cast("decimal(10,2)"))
            .filter("amount_cleaned > 0")
            .dropDuplicates("transaction_id");

        // Load
        transformed.write()
            .mode("append")
            .parquet("s3://target-bucket/etl-output/");

        spark.stop();
    }
}
```
"@

$files["THEORY.md"] = @"
# Theory of ETL Processes

## ETL Architecture
ETL pipelines follow a three-phase architecture:

### 1. Extract Phase
- **Full Extraction**: Read entire source data each run
- **Incremental Extraction**: Read only changed data since last run
- **Change Data Capture**: Capture inserts, updates, deletes

### 2. Transform Phase
- **Cleaning**: Remove nulls, deduplicate, standardize formats
- **Validation**: Check data types, ranges, referential integrity
- **Enrichment**: Join with reference data, calculate derived fields
- **Aggregation**: Summarize, compute metrics

### 3. Load Phase
- **Full Load**: Truncate and reload entire target
- **Incremental Load**: Append only new/changed records
- **Upsert/Merge**: Insert or update based on key match

## ELT vs ETL
| Aspect | ETL | ELT |
|--------|-----|-----|
| Transform location | Staging area | Target warehouse |
| Data volume | Lower | Higher |
| Transformation power | Dedicated engine | Warehouse compute |
| Schema flexibility | Transform before load | Transform after load |
| Typical tools | Spark, Spring Batch | dbt, Snowflake, BigQuery |
"@

$files["WHY_IT_EXISTS.md"] = @"
# Why ETL Processes Exist

## The Problem
Organizations need to consolidate data from disparate systems (ERP, CRM, billing, logs) into a unified analytical view. Each system has its own schema, data format, and quality level.

## Root Cause
Raw operational data is not suitable for analytics due to:
1. **Inconsistent formats** - Dates, currencies, codes differ across systems
2. **Poor data quality** - Missing values, duplicates, invalid entries
3. **Denormalized structures** - OLTP schemas not optimized for queries
4. **Business logic missing** - Raw data lacks computed metrics

## Solutions ETL Provides
```java
// Example: Standardizing date formats from multiple sources
Dataset<Row> standardized = rawData
    .withColumn("order_date",
        functions.when(functions.col("date_format").equalTo("US"),
            functions.to_date(functions.col("order_date"), "MM/dd/yyyy"))
                 .otherwise(functions.to_date(functions.col("order_date"), "yyyy-MM-dd")));
```
"@

$files["WHY_IT_MATTERS.md"] = @"
# Why ETL Processes Matter

## Business Impact
- **Data Quality**: ETL ensures analysts work with clean, reliable data
- **Performance**: Transformed data is optimized for query performance
- **Governance**: ETL provides audit trails and data lineage
- **Automation**: Scheduled ETL eliminates manual data preparation

## Key Metrics
- **Data Freshness**: Time from source update to target availability
- **Transform Accuracy**: Percentage of records correctly transformed
- **ETL Reliability**: Success rate of scheduled ETL jobs

## Java/Spark Benefits
```java
// Spark's Catalyst optimizer improves ETL performance
Dataset<Row> cleaned = spark.read()
    .option("inferSchema", "true")
    .json("s3://source/")
    .transform(this::cleanData)
    .transform(this::enrichData)
    .transform(this::validateData);
```
"@

$files["HISTORY.md"] = @"
# History of ETL Processes

## Evolution
- **1970s**: Custom COBOL programs for data movement
- **1990s**: Commercial ETL tools (Informatica PowerCenter, IBM DataStage)
- **2000s**: Open-source ETL (Pentaho, Talend)
- **2010s**: Cloud ETL (Fivetran, Stitch, Airbyte)
- **2020s**: ELT paradigm with dbt, declarative transformations

## Key Milestones
1. **1992**: First data warehouse book by Bill Inmon
2. **1996**: Kimball's dimensional modeling published
3. **2012**: Apache Spark introduces DataFrame API
4. **2016**: dbt popularizes transform-in-warehouse approach
5. **2020**: DataOps brings CI/CD to ETL pipelines

## Java's Role
```java
// Spring Batch - enterprise ETL framework since 2007
@Bean
public Job etlJob() {
    return new JobBuilder("etlJob", jobRepository)
        .start(extractStep())
        .next(transformStep())
        .next(loadStep())
        .incrementer(new RunIdIncrementer())
        .build();
}
```
"@

$files["MENTAL_MODELS.md"] = @"
# Mental Models for ETL

## 1. The Factory Assembly Line
- Raw materials (source data) enter at one end
- Each station (transform step) adds value
- Quality checks at every stage
- Finished product (analytical data) exits

## 2. The Water Filtration System
- **Intake**: Water from river (extract)
- **Sedimentation**: Let solids settle (clean)
- **Filtration**: Remove impurities (validate)
- **Chemical treatment**: Add minerals (enrich)
- **Distribution**: Send to homes (load)

## 3. The Kitchen Model
- **Shopping** (Extract): Gather ingredients
- **Prepping** (Clean): Wash, chop, measure
- **Cooking** (Transform): Combine, heat, season
- **Plating** (Load): Present final dish

## 4. The Data Pipeline as Code
```java
// Declarative ETL using Java streams
List<Output> etlPipeline = source.stream()
    .map(this::extract)
    .filter(this::validate)
    .map(this::transform)
    .map(this::enrich)
    .collect(Collectors.toList());
```
"@

$files["HOW_IT_WORKS.md"] = @"
# How ETL Processes Work

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
            new OrderRowMapper()
        );
    }
}
```

### API Extraction
```java
@Component
public class ApiExtractor {
    private final RestTemplate rest;

    public List<Customer> extractCustomers(String cursor) {
        String url = "https://api.crm.com/v2/customers?cursor=" + cursor;
        ResponseEntity<ApiResponse> response = rest.exchange(
            url, HttpMethod.GET, null, ApiResponse.class);
        return response.getBody().getRecords();
    }
}
```

## Transformation Logic
```java
public class TransformProcessor {
    public Dataset<Row> applyBusinessRules(Dataset<Row> data) {
        return data
            .withColumn("customer_segment",
                functions.when(functions.col("total_spend").$greater(10000), "VIP")
                         .when(functions.col("total_spend").$greater(1000), "GOLD")
                         .otherwise("STANDARD"))
            .withColumn("is_active",
                functions.when(functions.col("last_purchase")
                    .$greater(functions.date_sub(functions.current_date(), 90)),
                    functions.lit(true)).otherwise(functions.lit(false)));
    }
}
```

## Loading Patterns
```java
// Upsert pattern
public class DeltaLoader {
    public void upsert(Dataset<Row> data, String table) {
        data.createOrReplaceTempView("staging");
        spark.sql(String.format("""
            MERGE INTO %s t
            USING staging s ON t.id = s.id
            WHEN MATCHED THEN UPDATE SET *
            WHEN NOT MATCHED THEN INSERT *
            """, table));
    }
}
```
"@

$files["INTERNALS.md"] = @"
# Internal Architecture of ETL

## ETL Engine Components

### Metadata Management
```java
@Component
public class EtlMetadataManager {
    private final JdbcTemplate metaDb;

    public void recordRun(String jobName, EtlRunStatus status, long recordsProcessed) {
        metaDb.update("INSERT INTO etl_audit (job_name, status, records, started_at, completed_at) " +
            "VALUES (?, ?, ?, ?, ?)",
            jobName, status.name(), recordsProcessed,
            Timestamp.valueOf(LocalDateTime.now().minusSeconds(duration)),
            Timestamp.valueOf(LocalDateTime.now()));
    }

    public LocalDateTime getLastSuccessfulRun(String jobName) {
        return metaDb.queryForObject(
            "SELECT MAX(completed_at) FROM etl_audit WHERE job_name = ? AND status = 'SUCCESS'",
            LocalDateTime.class, jobName);
    }
}
```

### Staging Area Management
```java
@Component
public class StagingManager {
    private final String stagingPath = "/tmp/etl/staging/";

    public Path createStage(String jobId) throws IOException {
        Path stage = Paths.get(stagingPath, jobId);
        Files.createDirectories(stage);
        return stage;
    }

    public void cleanupStage(String jobId) throws IOException {
        Path stage = Paths.get(stagingPath, jobId);
        if (Files.exists(stage)) {
            Files.walk(stage)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> p.toFile().delete());
        }
    }
}
```

### Error Handling Framework
```java
@Component
public class EtlErrorHandler {
    private final Queue<FailedRecord> deadLetterQueue = new LinkedBlockingQueue<>(10000);

    public void handleFailedRecord(FailedRecord record) {
        if (record.getRetryCount() < 3) {
            record.incrementRetry();
            etlExecutor.schedule(() -> retry(record), 
                Duration.ofMinutes(5 * record.getRetryCount()));
        } else {
            deadLetterQueue.offer(record);
            alertService.sendAlert("ETL Dead Letter: " + record.getJobName());
        }
    }
}
```
"@

$files["MATH_FOUNDATION.md"] = @"
# Math Foundation for ETL

## Data Volume Calculations
```
Raw Data Volume = Sum of source table sizes
Intermediate Volume = Raw + Enrichment joins
Final Volume = After aggregation/deduplication
Compression Ratio = 3:1 to 10:1 with Parquet/ORC
```

## Extraction Cost
```
Network Cost = Records * RecordSize * ApiCallOverhead
Full Scan = FullTableScan * Frequency
Incremental = DeltaRecords * Frequency
CDC = ChangeRate * Records * Frequency
```

## Transformation Complexity
```
O(n) for row-wise operations (filter, cast, case)
O(n log n) for sorts and distinct
O(n * m) for joins (hash join)
O(n^2) for cross joins (avoid)
```

## Load Performance
```java
public class LoadPerformance {
    public static long estimateLoadTime(long records, int batchSize, long networkLatencyMs) {
        long batches = (long) Math.ceil((double) records / batchSize);
        return batches * networkLatencyMs;
    }
}
```

## Parallelism Calculation
```
Optimal Parallelism = min(SourcePartitions, TargetPartitions, AvailableCores * 2)
Speedup = 1 / (1 - P + P/N)  // Amdahl's Law
```
"@

$files["VISUAL_GUIDE.md"] = @"
# Visual Guide to ETL

## ETL Pipeline Flow
```
Source Systems              ETL Engine               Target Systems
+----------+                                        
| Oracle   |---\                                    
+----------+    |    +------------------+           
                 |--->|                  |    +-----------+
+----------+    |    |    Extract        |--->|           |
|PostgreSQL|---/     |    Transform      |    | Data      |
+----------+         |    Load           |    | Warehouse |
                     |                  |    |           |
+----------+         |  +------------+  |    +-----------+
| Salesforce|---+    |  | Staging    |  |    +-----------+
+----------+  |    |  | Area       |  |--->| Data Lake |
              +--->|  +------------+  |    +-----------+
+----------+  |    +------------------+    +-----------+
| REST APIs|---/                          |  BI Tool  |
+----------+                              +-----------+
```

## Transform Stage Detail
```
+----------------+     +----------------+     +----------------+
| Raw Stage      |     | Clean Stage    |     | Enriched Stage |
+----------------+     +----------------+     +----------------+
| id: string     |     | id: string     |     | id: string     |
| name: string   |---->| name: string   |---->| name: string   |
| amount: string |     | amount: decimal|     | amount: decimal|
| date: string   |     | date: date     |     | date: date     |
| raw_data: json |     |                |     | category: str  |
+----------------+     +----------------+     | tax: decimal   |
                                               | total: decimal |
                                               +----------------+
      Extract               Clean               Load
```
"@

$files["CODE_DEEP_DIVE.md"] = @"
# Code Deep Dive: ETL Implementation

## Complete Spring Batch ETL Job

### 1. Configuration
```java
@Configuration
public class EtlJobConfig {

    @Bean
    public Job etlJob(JobRepository jobRepository, Step extractStep,
                      Step transformStep, Step loadStep) {
        return new JobBuilder("customerETL", jobRepository)
            .start(extractStep)
            .next(transformStep)
            .next(loadStep)
            .listener(new EtlJobListener())
            .build();
    }

    @Bean
    public Step extractStep(JobRepository jobRepository,
                            PlatformTransactionManager ptm,
                            ItemReader<Customer> reader,
                            ItemWriter<Customer> writer) {
        return new StepBuilder("extractStep", jobRepository)
            .<Customer, Customer>chunk(500, ptm)
            .reader(reader)
            .writer(writer)
            .build();
    }
}
```

### 2. Reader Implementation
```java
public class CustomerItemReader implements ItemReader<Customer> {
    private Iterator<Customer> customerIterator;

    @Override
    public Customer read() {
        if (customerIterator != null && customerIterator.hasNext()) {
            return customerIterator.next();
        }
        return null;
    }

    @PostConstruct
    public void init() {
        // Load data from API or database
        List<Customer> customers = apiClient.fetchAllCustomers();
        this.customerIterator = customers.iterator();
    }
}
```

### 3. Processor
```java
public class CustomerProcessor implements ItemProcessor<Customer, EnrichedCustomer> {
    @Override
    public EnrichedCustomer process(Customer customer) {
        EnrichedCustomer ec = new EnrichedCustomer();
        ec.setCustomerId(customer.getId());
        ec.setFullName(customer.getFirstName() + " " + customer.getLastName());
        ec.setEmail(customer.getEmail().toLowerCase());
        ec.setNormalizedPhone(PhoneNormalizer.normalize(customer.getPhone()));
        ec.setLifetimeValue(calculateLTV(customer));
        ec.setSegment(assignSegment(customer));
        ec.setProcessedAt(LocalDateTime.now());
        return ec;
    }

    private String assignSegment(Customer c) {
        if (c.getTotalOrders() > 50) return "LOYAL";
        if (c.getTotalOrders() > 10) return "REGULAR";
        return "NEW";
    }
}
```

### 4. Writer
```java
public class CustomerWriter implements ItemWriter<EnrichedCustomer> {
    private final JdbcTemplate jdbc;

    @Override
    public void write(Chunk<? extends EnrichedCustomer> chunk) {
        String sql = "MERGE INTO dim_customer t " +
            "USING (VALUES (?, ?, ?, ?, ?, ?)) " +
            "AS s(id, name, email, phone, ltv, segment) " +
            "ON t.customer_id = s.id " +
            "WHEN MATCHED THEN UPDATE SET " +
            "  name = s.name, email = s.email, " +
            "  phone = s.phone, ltv = s.ltv, segment = s.segment " +
            "WHEN NOT MATCHED THEN INSERT " +
            "  (customer_id, name, email, phone, ltv, segment) " +
            "VALUES (s.id, s.name, s.email, s.phone, s.ltv, s.segment)";

        jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) {
                EnrichedCustomer c = chunk.getItems().get(i);
                ps.setString(1, c.getCustomerId());
                ps.setString(2, c.getFullName());
                ps.setString(3, c.getEmail());
                ps.setString(4, c.getNormalizedPhone());
                ps.setBigDecimal(5, c.getLifetimeValue());
                ps.setString(6, c.getSegment());
            }

            @Override
            public int getBatchSize() { return chunk.getItems().size(); }
        });
    }
}
```
"@

$files["STEP_BY_STEP.md"] = @"
# Step-by-Step ETL Implementation

## Step 1: Design
1. Map source systems and schemas
2. Define target schema (star/snowflake)
3. Identify transformation rules
4. Determine loading strategy

## Step 2: Setup Project
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-batch</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-sql_2.12</artifactId>
    <version>3.5.0</version>
</dependency>
```

## Step 3: Configure Data Sources
```java
@Configuration
public class DataSourceConfig {
    @Bean
    @Primary
    public DataSource targetDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://warehouse:5432/analytics")
            .username(System.getenv("DW_USER"))
            .password(System.getenv("DW_PASS"))
            .build();
    }
}
```

## Step 4: Implement Transformations
```java
public class SalesTransformer {
    public Dataset<Row> transform(Dataset<Row> raw) {
        return raw
            .withColumn("sale_date", functions.to_date(functions.col("timestamp")))
            .withColumn("revenue", functions.col("quantity").multiply(functions.col("unit_price")))
            .withColumn("tax", functions.col("revenue").multiply(functions.lit(0.08)))
            .groupBy("sale_date", "product_id")
            .agg(functions.sum("revenue").as("total_revenue"),
                 functions.sum("quantity").as("total_quantity"),
                 functions.count("transaction_id").as("transaction_count"));
    }
}
```

## Step 5: Implement Loading
```java
public class WarehouseLoader {
    public void load(Dataset<Row> data, String table) {
        data.write()
            .mode("append")
            .format("jdbc")
            .option("url", jdbcUrl)
            .option("dbtable", table)
            .option("isolationLevel", "READ_COMMITTED")
            .save();
    }
}
```

## Step 6: Schedule
```java
@Component
public class EtlScheduler {
    @Scheduled(cron = "0 0 2 * * ?")  // Daily at 2 AM
    public void runDailyEtl() {
        jobLauncher.run(job, new JobParametersBuilder()
            .addDate("runDate", new Date())
            .toJobParameters());
    }
}
```
"@

$files["COMMON_MISTAKES.md"] = @"
# Common ETL Mistakes

## 1. No Incremental Logic
```java
// WRONG - always full refresh
spark.read().jdbc(url, "orders", props);

// RIGHT - incremental with watermark
spark.read().jdbc(url, "orders",
    predicates = new String[]{"updated_at >= '" + lastRun + "'"},
    connectionProperties = props);
```

## 2. Ignoring Data Types
```java
// WRONG - everything as string
Dataset<Row> data = spark.read().option("inferSchema", "false").csv(path);

// RIGHT - explicit schema
StructType schema = DataTypes.createStructType(new StructField[]{
    DataTypes.createStructField("id", DataTypes.LongType, false),
    DataTypes.createStructField("amount", DataTypes.createDecimalType(15, 2), false),
    DataTypes.createStructField("date", DataTypes.DateType, true)
});
```

## 3. Not Handling Failures Gracefully
```java
// WRONG
result.write().mode("overwrite").save();

// RIGHT - checkpoint and recover
result.write().mode("overwrite")
    .option("checkpointLocation", "/checkpoints")
    .save();
```

## 4. No Data Quality Checks
```java
// WRONG - trust source data
processed.write().mode("append").save();

// RIGHT - validate before load
long nullCount = processed.filter("key IS NULL").count();
if (nullCount > threshold) {
    throw new DataQualityException("Too many null keys: " + nullCount);
}
```
"@

$files["DEBUGGING.md"] = @"
# Debugging ETL Processes

## Common Issues

### Data Type Mismatches
```java
// Debug: print schema
sourceData.printSchema();
// Check for casting errors
spark.conf().set("spark.sql.ansi.enabled", "false"); // lenient mode
```

### Null Pointer Issues
```java
// Debug: identify null columns
import org.apache.spark.sql.functions;
Dataset<Row> nullCounts = sourceData.select(
    sourceData.columns().stream()
        .map(c -> functions.sum(functions.when(functions.col(c).isNull(), 1).otherwise(0)).as(c + "_nulls"))
        .toArray(Column[]::new));
nullCounts.show();
```

### Performance Bottlenecks
```sql
-- Check Spark SQL physical plan
EXPLAIN COST SELECT * FROM transformed WHERE partition_date = '2024-01-01';
```

### Connection Issues
```java
// Test database connectivity
@EventListener(ApplicationReadyEvent.class)
public void testConnections() {
    try {
        jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        log.info("Source DB connected");
    } catch (Exception e) {
        log.error("Source DB unavailable: {}", e.getMessage());
    }
}
```
"@

$files["REFACTORING.md"] = @"
# Refactoring ETL Processes

## From Procedural to Declarative
### Before: Spaghetti code
```java
// 20 ETL jobs with duplicated logic
public void runEtl1() { /* 200 lines */ }
public void runEtl2() { /* 190 lines, similar */ }
```

### After: Framework-based
```java
@Component
public abstract class BaseEtlJob {
    protected abstract Dataset<Row> extract();
    protected abstract Dataset<Row> transform(Dataset<Row> data);
    protected abstract void load(Dataset<Row> data);

    public void execute() {
        Dataset<Row> raw = extract();
        Dataset<Row> transformed = transform(raw);
        validate(transformed);
        load(transformed);
    }
}
```

## Configuration Extraction
### Before
```java
jdbcTemplate.query("SELECT * FROM orders WHERE source = 'web'");
```

### After
```java
@Component
@ConfigurationProperties(prefix = "etl.queries")
public class QueryConfig {
    private Map<String, String> queries = new HashMap<>();
}
```

## Batch Size Tuning
```java
// Configurable batch size
@Value("${etl.batchSize:1000}")
private int batchSize;

// Adaptive batch sizing
public int calculateOptimalBatchSize() {
    Runtime runtime = Runtime.getRuntime();
    long maxMemory = runtime.maxMemory();
    long recordSize = estimateRecordSize();
    return (int) Math.min(maxMemory / (recordSize * 4), 10000);
}
```
"@

$files["PERFORMANCE.md"] = @"
# ETL Performance Optimization

## Optimization Strategies

### Partitioning
```java
// Repartition for better parallelism
Dataset<Row> optimized = data
    .repartition(functions.col("country"))
    .sortWithinPartitions("order_date");
```

### Caching Reference Data
```java
// Broadcast small dimension tables
Dataset<Row> countries = spark.read().parquet("dim/country/");
countries.createOrReplaceTempView("dim_country");
spark.sqlContext().cacheTable("dim_country");
```

### Parallel Extraction
```java
// Parallel reads from partitioned source
String[] partitions = {"year=2023", "year=2024"};
ExecutorService pool = Executors.newFixedThreadPool(4);
List<Future<Dataset<Row>>> futures = Arrays.stream(partitions)
    .map(p -> pool.submit(() -> spark.read().parquet("s3://data/" + p)))
    .collect(Collectors.toList());
```

### Avoiding Shuffles
```java
// Pre-partition data to avoid shuffle in joins
Dataset<Row> factRepartitioned = fact.repartition(200, functions.col("dim_key"));
Dataset<Row> dimRepartitioned = dim.repartition(200, functions.col("key"));
Dataset<Row> joined = factRepartitioned.join(dimRepartitioned,
    factRepartitioned.col("dim_key").equalTo(dimRepartitioned.col("key")));
```

### Memory Tuning
```java
SparkSession.builder()
    .config("spark.sql.adaptive.enabled", "true")
    .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
    .config("spark.sql.adaptive.skewJoin.enabled", "true")
    .config("spark.sql.autoBroadcastJoinThreshold", "104857600") // 100MB
    .getOrCreate();
```
"@

$files["SECURITY.md"] = @"
# Security in ETL

## Data Protection

### Column-Level Encryption
```java
public class PiiEncryptor {
    public Dataset<Row> encryptSensitiveColumns(Dataset<Row> data) {
        return data
            .withColumn("ssn", encryptColumn(functions.col("ssn"), encryptionKey))
            .withColumn("email", encryptColumn(functions.col("email"), encryptionKey))
            .drop("credit_card");  // Don't store in warehouse
    }
}
```

### Access Control
```java
@PreAuthorize("hasRole('ETL_ADMIN')")
public void runSensitiveEtl() { /* restricted ETL */ }

@PreAuthorize("hasPermission(#jobName, 'ETL_JOB', 'EXECUTE')")
public void runJob(String jobName) { /* fine-grained access */ }
```

### Audit Logging
```java
@Component
public class EtlAuditLogger {
    @EventListener
    public void onEtlEvent(EtlExecutionEvent event) {
        auditLogRepository.save(new AuditEntry(
            event.getJobName(),
            event.getUser(),
            event.getRecordsProcessed(),
            event.getStatus(),
            Instant.now()
        ));
    }
}
```

### Secure Configuration
```properties
# Use environment variables or vault
etl.db.password=${DB_PASSWORD}
etl.api.key=${API_KEY}
etl.encryption.key=${ENCRYPTION_KEY}
```
"@

$files["ARCHITECTURE.md"] = @"
# ETL Architecture

## High-Level Architecture
```
+------------------+    +------------------+    +------------------+
|   Source Layer   |    |   Staging Layer  |    |   Target Layer   |
+------------------+    +------------------+    +------------------+
| PostgreSQL       |    | Raw Tables       |    | Dimension Tables |
| Oracle           |--->| Staging Area     |--->| Fact Tables      |
| Salesforce API   |    | Temp Files       |    | Aggregate Views  |
| Flat Files       |    | Checkpoints      |    | Data Marts       |
+------------------+    +------------------+    +------------------+
                              |
                        +-----+-----+
                        | Transform  |
                        | Engine     |
                        +-----+-----+
                              |
                     +--------+--------+
                     |                  |
              +------+------+   +------+------+
              | Validation  |   | Error       |
              | Checks      |   | Handling    |
              +-------------+   +-------------+
```

## Spring Batch Architecture
```
Job Launcher --> Job --> Step --> ItemReader --> ItemProcessor --> ItemWriter
                              |
                         Chunk-oriented processing
                              |
                    +---------+---------+
                    |                   |
              Read (List)         Process (List)
                    |                   |
              Chunk processor <--- Transform
                    |
               Write (List)
```

## Deployment Architecture
```java
@SpringBootApplication
public class EtlApplication {
    public static void main(String[] args) {
        SpringApplication.run(EtlApplication.class, args);
    }
}
```
"@

$files["EXERCISES.md"] = @"
# ETL Exercises

## Exercise 1: File-Based ETL
Build an ETL job that reads CSV files, validates them, and writes to PostgreSQL.

## Exercise 2: Incremental Load
Implement change data capture using a last_updated timestamp.

## Exercise 3: Data Quality
Add validation rules: reject records with null keys, negative amounts, or future dates.

## Exercise 4: Multi-Source ETL
Combine data from a database, an API, and a file into a unified target table.

## Exercise 5: Error Recovery
Implement retry logic and dead letter queue for failed records.
"@

$files["QUIZ.md"] = @"
# ETL Quiz

## Question 1
What does ETL stand for?
- A) Extract, Transfer, Load
- B) Extract, Transform, Load
- C) Evaluate, Transform, Log
- D) Extract, Test, Load

## Question 2
Which load strategy is best for maintaining historical changes?
- A) Truncate and reload
- B) SCD Type 2
- C) Append only
- D) Delete and insert

## Question 3
What is the main advantage of ELT over ETL?
- A) Less transformation code needed
- B) Leverages warehouse compute power
- C) Works with any data type
- D) Requires less storage

## Question 4
What is a slowly changing dimension?
- A) A dimension that changes frequently
- B) A dimension that changes rarely
- C) A dimension that never changes
- D) A dimension updated every second

## Answer Key
1: B, 2: B, 3: B, 4: B
"@

$files["FLASHCARDS.md"] = @"
# ETL Flashcards

## Card 1
**Front**: What is ETL?
**Back**: Extract, Transform, Load - a process that moves data from source systems, transforms it, and loads it into a target system.

## Card 2
**Front**: What is the difference between ETL and ELT?
**Back**: ETL transforms data before loading; ELT loads raw data first and transforms in the warehouse.

## Card 3
**Front**: What is a slowly changing dimension (SCD)?
**Back**: A dimension that changes infrequently, with strategies to handle historical tracking (Type 1, 2, 3).

## Card 4
**Front**: What is incremental extraction?
**Back**: Extracting only data that has changed since the last ETL run, using watermarks or CDC.

## Card 5
**Front**: What is a staging area in ETL?
**Back**: An intermediate storage area where raw data is placed before transformation.
"@

$files["INTERVIEW.md"] = @"
# ETL Interview Questions

## Beginner
**Q**: What is the purpose of the staging area in ETL?
**A**: The staging area serves as a buffer zone where raw data is landed before transformation. It allows for error recovery, data validation, and auditing without affecting source or target systems.

## Intermediate
**Q**: How would you handle a slowly changing dimension Type 2?
**A**: Create a new row for each change with effective dates. Old rows get an end_date, new rows have current effective date and an indicator flag.

## Advanced
**Q**: Design an ETL system for a global retail company with 50TB of daily data.
**A**: Use partitioned data ingestion, Spark for distributed transform, Delta Lake for ACID transactions on data lake, incremental CDC from sources, and multi-hop data architecture (bronze, silver, gold layers).
"@

$files["REFLECTION.md"] = @"
# ETL Reflection

## Key Takeaways
- ETL is the foundation of data warehousing and analytics
- Incremental processing is critical for scaling
- Data quality checks should be built into every ETL stage
- Monitoring and error handling are as important as the ETL logic itself

## Questions to Explore
1. How does your organization handle schema drift from source systems?
2. What is your recovery strategy when a 6-hour ETL fails at hour 5?
3. How can you reduce ETL runtime while maintaining data quality?
"@

$files["REFERENCES.md"] = @"
# ETL References

## Books
- "The Data Warehouse ETL Toolkit" by Ralph Kimball
- "Building the Data Warehouse" by Bill Inmon
- "Data Pipelines with Apache Spark" by various

## Tools
- Apache Spark: https://spark.apache.org
- Spring Batch: https://spring.io/projects/spring-batch
- dbt: https://www.getdbt.com
- Airbyte: https://airbyte.com

## Papers
- "Data Warehousing and OLAP: A Research-Oriented Bibliography"
- "ETL Evolution: From Custom Code to Declarative Pipelines"
"@

foreach ($file in $files.Keys) {
    $content = $files[$file]
    $path = Join-Path $base $file
    $content | Out-File -FilePath $path -Encoding utf8
    Write-Host "Created: $path"
}
Write-Host "Lab 02 complete"

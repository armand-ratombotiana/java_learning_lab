$base = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\data-engineering\04-apache-spark"

$files = @{}

$files["README.md"] = @"
# Apache Spark

## Overview
Apache Spark is a unified, distributed data processing engine for large-scale data analytics, supporting batch processing, streaming, SQL, machine learning, and graph processing.

## Key Concepts
- **RDD**: Resilient Distributed Dataset - immutable, partitioned collection
- **DataFrame**: Distributed collection of rows with schema
- **Spark SQL**: SQL interface for structured data
- **Catalyst Optimizer**: Query optimization engine
- **Tungsten**: Off-heap memory and code generation

## Java Example
```java
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SparkExample {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
            .appName("SparkExample")
            .config("spark.sql.adaptive.enabled", "true")
            .getOrCreate();

        Dataset<Row> df = spark.read()
            .option("header", "true")
            .csv("data.csv");

        df.createOrReplaceTempView("data");
        Dataset<Row> result = spark.sql(
            "SELECT category, AVG(value) as avg_value " +
            "FROM data GROUP BY category ORDER BY avg_value DESC");
        result.show();
    }
}
```
"@

$files["THEORY.md"] = @"
# Apache Spark Theory

## Architecture
- **Driver**: Main program, creates SparkContext, converts code to tasks
- **Cluster Manager**: Allocates resources (Standalone, YARN, Kubernetes)
- **Executors**: Worker processes running tasks
- **Tasks**: Units of work sent to executors

## Core Abstractions
### RDD (Resilient Distributed Dataset)
- Immutable, partitioned collection of records
- Can be operated in parallel
- Automatically recovers from failures (lineage)
- Created from data sources or transforming other RDDs

### DataFrame
- RDD + Schema
- Optimized via Catalyst
- Supports SQL queries
- Columnar access

### Dataset
- DataFrame with type safety (Java/Scala)
- Compile-time type checking
- Encoder-based serialization

## Execution Model
1. **DAG Scheduler**: Creates stages from RDD lineage
2. **Task Scheduler**: Launches tasks on executors
3. **Shuffle**: Data redistribution across partitions
"@

$files["WHY_IT_EXISTS.md"] = @"
# Why Apache Spark Exists

## The Problem
MapReduce (Hadoop) was too slow for iterative algorithms and interactive queries. Each job writes to disk between stages, making ML training and repeated queries extremely slow.

## Root Cause
- Disk-based shuffle between MapReduce stages
- No in-memory caching
- Limited to batch processing only
- Complex to write (lots of boilerplate)

## Spark's Solution
- **In-memory processing**: Cache data across operations
- **Unified engine**: Batch + Streaming + SQL + ML + Graph
- **Fault tolerance**: RDD lineage for recovery
- **Lazy evaluation**: Optimize execution plan before running

## Java/Spark Benefits
```java
// Spark's DataFrame API is much simpler than raw MapReduce
Dataset<Row> result = spark.read().parquet("data/")
    .filter("age >= 18")
    .groupBy("city")
    .agg(avg("income"), count("*"));
// Spark optimizes this into an efficient execution plan
```
"@

$files["WHY_IT_MATTERS.md"] = @"
# Why Apache Spark Matters

## Impact
- **Speed**: 100x faster than Hadoop MapReduce for iterative algorithms
- **Unified**: One engine for batch, streaming, SQL, ML, graph
- **Ecosystem**: Integration with Hive, HBase, Kafka, Cassandra, S3
- **Language Support**: Java, Scala, Python, SQL, R

## Adoption
- Used by 80%+ of Fortune 500 companies
- De facto standard for big data processing
- Core of Databricks Lakehouse platform
- Active community with 2000+ contributors

## Performance Metrics
- Process 100TB+ datasets
- Stream millions of events/second
- Sub-second latency for streaming
- Optimized via Catalyst + Tungsten
"@

$files["HISTORY.md"] = @"
# History of Apache Spark

## Timeline
- **2009**: Started at UC Berkeley AMPLab by Matei Zaharia
- **2010**: Open-sourced under BSD license
- **2013**: Donated to Apache Software Foundation
- **2014**: Became top-level Apache project
- **2015**: Spark 1.3 introduces DataFrames
- **2016**: Spark 2.0 introduces Dataset API and Tungsten
- **2020**: Spark 3.0 with Adaptive Query Execution
- **2023**: Spark 4.0 preview with Spark Connect

## Key Milestones
1. **2012**: Spark wins Daytona GraySort competition (sort 100TB 3x faster)
2. **2014**: Sets sorting record (100TB in 23 minutes)
3. **2015**: Databricks founded by Spark creators
4. **2021**: Spark becomes essential for lakehouse architecture
"@

$files["MENTAL_MODELS.md"] = @"
# Mental Models for Spark

## 1. The Kitchen Brigade
- **Driver** = Head chef (plans the menu, coordinates)
- **Executors** = Line cooks (execute tasks)
- **Partitions** = Prep stations (organized work areas)
- **Shuffle** = Passing ingredients between stations

## 2. The Assembly Line
- **Source** = Raw materials at start
- **Transformations** = Workstations along the line
- **Actions** = Completed product at end
- **Lazy Evaluation** = Work only happens when product demanded

## 3. The DAG as Blueprint
- Linear stages connected by dependencies
- Shuffle = Reorganizing between stages
- Narrow dependencies = No reorganization needed
- Wide dependencies = Full reorganization

## 4. The LEGO Model
- RDDs = Individual bricks
- Transformations = Connecting bricks
- Actions = The completed model
- Lineage = Instructions to rebuild
- Persistence = Glue to keep assembled
"@

$files["HOW_IT_WORKS.md"] = @"
# How Apache Spark Works

## Execution Pipeline

### 1. DAG Construction
```java
// Spark builds a DAG of stages
Dataset<Row> result = spark.read().parquet("data/")    // Stage 1: Read
    .filter("age > 18")                                  // Narrow dependency
    .groupBy("city")                                     // Stage 2: Shuffle
    .agg(avg("salary").as("avg_salary"))                // Stage 2: Aggregate
    .orderBy(functions.desc("avg_salary"));              // Stage 3: Sort
```

### 2. Stage Division
```java
// Wide dependencies (shuffles) break stages
// narrow: map, filter, union (no shuffle)
// wide: groupBy, join, distinct (shuffle required)

// Stage 0: Read + Filter
// Stage 1: Shuffle + Aggregate
// Stage 2: Sort
```

### 3. Task Execution
```java
SparkSession spark = SparkSession.builder()
    .config("spark.sql.shuffle.partitions", "200")
    .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
    .getOrCreate();
// Each partition becomes a task
// Tasks are scheduled on available executors
```

### 4. Memory Management
```java
// Spark uses off-heap memory via Tungsten
spark.conf().set("spark.memory.offHeap.enabled", "true");
spark.conf().set("spark.memory.offHeap.size", "4g");
// Execution memory for shuffles, joins, aggregations
// Storage memory for caching
// Unified memory can borrow between both
```
"@

$files["INTERNALS.md"] = @"
# Apache Spark Internals

## Catalyst Optimizer Internals

### Query Planning Pipeline
```java
/*
1. Analysis: Resolve column names, table references
2. Logical Optimization: Predicate pushdown, constant folding
3. Physical Planning: Join strategies, partitioning
4. Code Generation: Generate optimized Java bytecode
*/

// Spark SQL plan
Dataset<Row> plan = spark.sql("SELECT * FROM data WHERE value > 100");
plan.explain("cost");  // Shows optimized plan with cost estimates
```

## Tungsten Engine
```java
// Off-heap memory management
// Cache-friendly data layout
// Whole-stage code generation
spark.conf().set("spark.sql.codegen.wholeStage", "true");
spark.conf().set("spark.sql.unsafe.enabled", "true");
```

## Shuffle Implementation
```java
// Sort-based shuffle (default)
spark.conf().set("spark.shuffle.manager", "sort");
// Tungsten-sort for optimized sorting
spark.conf().set("spark.shuffle.sort.bypassMergeThreshold", "200");
```

## Memory Regions
```java
/*
+------------------+------------------+
|   Execution      |    Storage       |
|   Memory         |    Memory        |
| (joins, sorts,   | (cache,         |
|  aggregations)   |  broadcast)     |
+------------------+------------------+
|         Reserved Memory              |
+--------------------------------------+
*/
spark.conf().set("spark.memory.fraction", "0.6");
spark.conf().set("spark.memory.storageFraction", "0.5");
```
"@

$files["MATH_FOUNDATION.md"] = @"
# Math Foundation for Spark

## Memory Calculations
```
Total Available = spark.executor.memory * spark.executor.instances
Execution Memory = Total * spark.memory.fraction * (1 - spark.memory.storageFraction)
Storage Memory = Total * spark.memory.fraction * spark.memory.storageFraction

Safe Cache Size = Storage Memory / 2
Hash Join Threshold = Execution Memory / RowSize / HashTableOverhead
```

## Shuffle Cost
```
Shuffle Cost = Serialization + Network Transfer + Spill + Deserialization
Serialization = RowCount * RowSize / SerializationBandwidth
Network Transfer = ShuffleSize / NetworkBandwidth
Spill to Disk = ShuffleSize(memory) - AvailableMemory
```

## Parallelism
```
Optimal Partition Count = TotalCores * 2 to 4
Max Parallelism = AvailableCores * ThreadsPerCore
Task Time = DataPerPartition / ProcessingRate
Goal: Task Time = 100-200ms for low latency
```

## Join Costs
```
Broadcast Join = O(n) small table to all executors
Hash Join = O(n + m) with hash table build
Sort Merge Join = O(n log n + m log m + n + m)
Skew Join = O(n * k) where k = skew factor
```
"@

$files["VISUAL_GUIDE.md"] = @"
# Visual Guide to Apache Spark

## Architecture Diagram
```
+-------------------+
|     Driver        |
| +---------------+ |
| | SparkContext  | |
| | DAGScheduler  | |
| | TaskScheduler | |
| +---------------+ |
+--------+----------+
         |
+--------+-----------+-----------+
|        |           |           |
v        v           v           v
+------+ +------+ +------+ +------+
| Exec | | Exec | | Exec | | Exec |
| utor | | utor | | utor | | utor |
| 1    | | 2    | | 3    | | 4    |
+------+ +------+ +------+ +------+
| Cache | | Cache | | Cache | | Cache |
+------+ +------+ +------+ +------+
```

## DAG Visualization
```
Stage 0                         Stage 1
+---------+                    +----------+
| TextFile |-----> Filter ---->| Shuffle  |
+---------+   narrow     +--->| (groupBy)|
                           |    +----+-----+
                           |         |
                           |    +----v-----+
                           |    | Aggregate |
                           |    +----------+
                           |
Stage 2                    |
+---------+                |
| Sort    |<---------------+
+---------+   shuffle
```

## Memory Layout
```
Executor Memory (8GB)
+--------------------------------------------------+
| Reserved (300MB) | User Memory (2GB)  |           |
+------------------+--------------------+           |
   |               | Spark Memory (5.7GB)           |
   |               +-------------------------------+|
   |               | Execution   | Storage         ||
   |               | (3.42GB)    | (2.28GB)        ||
   |               +-------------------------------+|
   +--------------------------------------------------+
```
"@

$files["CODE_DEEP_DIVE.md"] = @"
# Code Deep Dive: Apache Spark

## Word Count with RDDs
```java
JavaRDD<String> textFile = spark.sparkContext()
    .textFile("hdfs://...", 4);

JavaPairRDD<String, Integer> counts = textFile
    .flatMap(line -> Arrays.asList(line.split(" ")).iterator())
    .mapToPair(word -> new Tuple2<>(word, 1))
    .reduceByKey(Integer::sum);

counts.saveAsTextFile("hdfs://.../output");
```

## DataFrame API
```java
public class DataFrameOperations {
    public Dataset<Row> analyzeSales(Dataset<Row> sales) {
        return sales
            .select("product_id", "amount", "date")
            .filter(col("amount").$greater(0))
            .withColumn("year", year(col("date")))
            .withColumn("month", month(col("date")))
            .groupBy("product_id", "year", "month")
            .agg(
                sum("amount").as("total_revenue"),
                count("*").as("transaction_count"),
                avg("amount").as("avg_transaction")
            )
            .orderBy(col("year").desc(), col("month").desc());
    }
}
```

## Spark Streaming with Kafka
```java
public class StreamingProcessor {
    public void process(String bootstrapServers) {
        Dataset<Row> stream = spark.readStream()
            .format("kafka")
            .option("kafka.bootstrap.servers", bootstrapServers)
            .option("subscribe", "transactions")
            .option("startingOffsets", "earliest")
            .load()
            .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
            .select(from_json(col("value"), schema).as("data"))
            .select("data.*");

        StreamingQuery query = stream
            .withWatermark("timestamp", "10 minutes")
            .groupBy(
                window(col("timestamp"), "5 minutes"),
                col("category"))
            .agg(sum("amount").as("total"))
            .writeStream()
            .outputMode("complete")
            .format("console")
            .trigger(Trigger.ProcessingTime("10 seconds"))
            .option("checkpointLocation", "/checkpoints")
            .start();

        query.awaitTermination();
    }
}
```

## Custom Aggregation with UDAF
```java
public class MedianUDAF extends UserDefinedAggregateFunction {
    @Override
    public StructType inputSchema() {
        return DataTypes.createStructType(
            new StructField[]{DataTypes.createStructField("value",
                DataTypes.DoubleType, true)});
    }

    @Override
    public StructType bufferSchema() {
        return DataTypes.createStructType(new StructField[]{
            DataTypes.createStructField("values",
                DataTypes.createArrayType(DataTypes.DoubleType), true)});
    }

    @Override
    public DataType dataType() { return DataTypes.DoubleType; }

    @Override
    public boolean deterministic() { return true; }

    @Override
    public void initialize(MutableAggregationBuffer buffer) {
        buffer.update(0, new ArrayList<Double>());
    }

    @Override
    public void update(MutableAggregationBuffer buffer, Row input) {
        if (!input.isNullAt(0)) {
            @SuppressWarnings("unchecked")
            List<Double> values = buffer.getList(0);
            values.add(input.getDouble(0));
            buffer.update(0, values);
        }
    }

    @Override
    public void merge(MutableAggregationBuffer buffer1, Row buffer2) {
        @SuppressWarnings("unchecked")
        List<Double> v1 = buffer1.getList(0);
        @SuppressWarnings("unchecked")
        List<Double> v2 = buffer2.getList(0);
        v1.addAll(v2);
        buffer1.update(0, v1);
    }

    @Override
    public Object evaluate(Row buffer) {
        @SuppressWarnings("unchecked")
        List<Double> values = buffer.getList(0);
        Collections.sort(values);
        int size = values.size();
        if (size == 0) return null;
        if (size % 2 == 0) {
            return (values.get(size/2 - 1) + values.get(size/2)) / 2.0;
        }
        return values.get(size/2);
    }
}
```
"@

$files["STEP_BY_STEP.md"] = @"
# Step-by-Step Spark Development

## Step 1: Setup Project
```xml
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-core_2.12</artifactId>
    <version>3.5.0</version>
</dependency>
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-sql_2.12</artifactId>
    <version>3.5.0</version>
</dependency>
```

## Step 2: Initialize SparkSession
```java
SparkSession spark = SparkSession.builder()
    .appName("MySparkApp")
    .master(System.getProperty("spark.master", "local[*]"))
    .config("spark.sql.adaptive.enabled", "true")
    .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
    .getOrCreate();
```

## Step 3: Load Data
```java
Dataset<Row> jsonData = spark.read().json("s3://bucket/data/");
Dataset<Row> csvData = spark.read().option("header", "true").csv("s3://bucket/data.csv");
Dataset<Row> parquetData = spark.read().parquet("s3://bucket/data.parquet");
Dataset<Row> jdbcData = spark.read().format("jdbc")
    .option("url", "jdbc:postgresql://host/db")
    .option("dbtable", "schema.table").load();
```

## Step 4: Transform
```java
Dataset<Row> transformed = jsonData
    .filter(col("status").equalTo("ACTIVE"))
    .withColumn("year", year(col("created_date")))
    .withColumn("revenue", col("quantity").multiply(col("unit_price")))
    .groupBy("year", "category")
    .agg(sum("revenue").as("total"));
```

## Step 5: Write Output
```java
transformed.write()
    .mode("overwrite")
    .partitionBy("year")
    .parquet("s3://output/");

// Or write to JDBC
transformed.write()
    .mode("append")
    .format("jdbc")
    .option("url", jdbcUrl)
    .option("dbtable", "analytics.sales_summary")
    .save();
```

## Step 6: Submit Job
```bash
spark-submit --class com.example.SparkJob \
  --master yarn \
  --deploy-mode cluster \
  --executor-memory 8g \
  --num-executors 10 \
  --conf spark.sql.shuffle.partitions=200 \
  app.jar
```
"@

$files["COMMON_MISTAKES.md"] = @"
# Common Spark Mistakes

## 1. Collecting Large Datasets
```java
// WRONG - OOM
List<Row> all = dataset.collectAsList();

// RIGHT
dataset.foreachPartition(partition -> {
    while (partition.hasNext()) process(partition.next());
});
```

## 2. Not Broadcasting Small Tables
```java
// WRONG - shuffle join
largeDF.join(smallDF, "key");

// RIGHT - broadcast join
largeDF.join(smallDF.hint("broadcast"), "key");
```

## 3. Creating Too Many Tasks
```java
// WRONG - too many small tasks
spark.conf().set("spark.sql.shuffle.partitions", "10000");

// RIGHT - match cluster size
spark.conf().set("spark.sql.shuffle.partitions",
    spark.sparkContext().defaultParallelism() * 2);
```

## 4. Not Caching Reused Data
```java
// WRONG - recompute every time
for (int i = 0; i < 10; i++) {
    Dataset<Row> filtered = data.filter(...);
}

// RIGHT - cache once
Dataset<Row> filtered = data.filter(...).cache();
```

## 5. Serialization Issues
```java
// WRONG - class not serializable
class MyClass { Object nonSerializable = new NonSerializable(); }
data.map(x -> myClass.method(x));

// RIGHT - use mapPartitions
data.mapPartitions(it -> {
    MyClass mc = new MyClass(); // Created per partition
    ...
});
```
"@

$files["DEBUGGING.md"] = @"
# Debugging Apache Spark

## Common Issues

### Out of Memory
```java
// Check executor memory usage
spark.conf().set("spark.executor.memory", "8g");
spark.conf().set("spark.memory.offHeap.enabled", "true");
spark.conf().set("spark.memory.offHeap.size", "4g");

// Monitor in Spark UI: Executors tab -> Memory
// Look for: Shuffle Spill (Disk), GC Time
```

### Skew
```java
// Check for data skew
dataset.groupBy("key").count()
    .orderBy(functions.desc("count"))
    .show(10);

// Fix with salting
dataset.withColumn("salt",
    (functions.rand()).multiply(10).cast("int"))
    .withColumn("salted_key",
        functions.concat(col("key"), lit("_"), col("salt")))
    .groupBy("salted_key")
    .agg(...);
```

### Slow Tasks
```java
// Tasks that take much longer than others = skew
// Check Spark UI: Stages -> Task Time Distribution
// Fix: Increase partitions, add salting, use AQE
spark.conf().set("spark.sql.adaptive.skewJoin.enabled", "true");
```

### Debugging Commands
```java
// Print execution plan
dataset.explain("formatted");
dataset.explain("cost");

// Show physical plan
spark.sql("EXPLAIN COST SELECT * FROM data").show(false);
```
"@

$files["REFACTORING.md"] = @"
# Refactoring Spark Applications

## RDD to DataFrame
### Before: RDD-based
```java
JavaRDD<String> lines = spark.textFile("data.txt");
JavaPairRDD<String, Integer> counts = lines
    .flatMap(l -> Arrays.asList(l.split(" ")).iterator())
    .mapToPair(w -> new Tuple2<>(w, 1))
    .reduceByKey(Integer::sum);
```

### After: DataFrame-based
```java
Dataset<Row> df = spark.read().text("data.txt");
Dataset<Row> counts = df
    .select(functions.explode(
        functions.split(col("value"), " ")).as("word"))
    .groupBy("word")
    .count();
```

## SQL to Dataset API
### Before: String-based SQL
```java
spark.sql("SELECT * FROM sales JOIN products USING (product_id)");
```

### After: Type-safe Dataset API
```java
salesDF.join(productsDF, "product_id");
```

## Batch to Streaming
### Before: Batch
```java
Dataset<Row> daily = spark.read().parquet("s3://data/2024/01/01/");
```

### After: Streaming
```java
Dataset<Row> stream = spark.readStream()
    .format("kafka")
    .option("subscribe", "topic")
    .load();
```
"@

$files["PERFORMANCE.md"] = @"
# Spark Performance Optimization

## AQE (Adaptive Query Execution)
```java
SparkSession.builder()
    .config("spark.sql.adaptive.enabled", "true")
    .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
    .config("spark.sql.adaptive.coalescePartitions.minPartitionNum", "1")
    .config("spark.sql.adaptive.coalescePartitions.initialPartitionNum", "200")
    .config("spark.sql.adaptive.skewJoin.enabled", "true")
    .config("spark.sql.adaptive.skewJoin.skewedPartitionFactor", "10")
    .config("spark.sql.adaptive.skewJoin.skewedPartitionThresholdInBytes", "256MB")
    .config("spark.sql.adaptive.localShuffleReader.enabled", "true")
    .getOrCreate();
```

## Data Serialization
```java
// Use Kryo serialization
spark.conf().set("spark.serializer",
    "org.apache.spark.serializer.KryoSerializer");
spark.conf().set("spark.kryo.registrationRequired", "true");
spark.conf().set("spark.kryo.classesToRegister",
    "com.example.MyClass,com.example.Result");
```

## File Format Optimization
```java
// Use columnar formats with compression
df.write()
    .option("compression", "snappy")
    .parquet("output/");

// Optimize file sizes
spark.conf().set("spark.sql.files.maxPartitionBytes", "134217728"); // 128MB
```

## Join Optimization
```java
// Broadcast joins for small tables
import org.apache.spark.sql.functions.broadcast;
Dataset<Row> result = largeDF.join(broadcast(smallDF), "key");

// Bucketed joins for repeated joins
largeDF.write().bucketBy(42, "key").saveAsTable("fact_bucketed");
smallDF.write().bucketBy(42, "key").saveAsTable("dim_bucketed");
```
"@

$files["SECURITY.md"] = @"
# Apache Spark Security

## Authentication
```java
// Enable authentication
spark.conf().set("spark.authenticate", "true");
spark.conf().set("spark.authenticate.secret", System.getenv("SPARK_SECRET"));

// Enable encryption
spark.conf().set("spark.network.crypto.enabled", "true");
spark.conf().set("spark.ssl.enabled", "true");
spark.conf().set("spark.ssl.keyStore", "/path/to/keystore");
```

## Access Control
```java
// SQL-level security
spark.conf().set("spark.sql.redaction.string.regex", "(?i)(password|secret|token)");

// View-based access
spark.sql("CREATE VIEW sales_safe AS " +
    "SELECT region, SUM(amount) as total, COUNT(*) as count " +
    "FROM fact_sales GROUP BY region");
```

## Data Protection
```java
// Encrypt sensitive columns
Dataset<Row> safe = df.withColumn("email",
    functions.sha2(col("email"), 256));

// Dynamic data masking
spark.udf().register("mask", (String s) -> {
    if (s == null) return null;
    return s.substring(0, 3) + "***" + s.substring(s.indexOf("@"));
}, DataTypes.StringType);
```
"@

$files["ARCHITECTURE.md"] = @"
# Apache Spark Architecture

## Cluster Mode Overview
```
+------------------+       +------------------+
|    Driver        |       |   Cluster Mgr    |
| +--------------+ |       | (YARN/K8s/Stand) |
| | SparkContext | |       +------------------+
| | DAGScheduler | |               |
| | TaskScheduler| |               |
| +--------------+ |               |
+--------+---------+               |
         |                         |
    +----+----+           +--------+--------+
    |         |           |        |        |
+---v---+ +---v---+  +---v---+ +--v----+ +--v----+
| Exec1 | | Exec2 |  | Exec3 | | Exec4 | | Exec5 |
+-------+ +-------+  +-------+ +-------+ +-------+
  | Cache |  | Cache |  | Cache |  | Cache |  | Cache |
+---------+---------+---------+---------+---------+
```

## Application Components
```java
@SpringBootApplication
public class SparkApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx =
            SpringApplication.run(SparkApplication.class, args);
        SparkSession spark = ctx.getBean(SparkSession.class);
        ctx.getBean(DataPipeline.class).run(spark);
    }

    @Bean
    public SparkSession sparkSession() {
        return SparkSession.builder()
            .appName("SparkApp")
            .config("spark.sql.extensions",
                "io.delta.sql.DeltaSparkSessionExtension")
            .getOrCreate();
    }
}
```
"@

$files["EXERCISES.md"] = @"
# Apache Spark Exercises

## Exercise 1: Word Count
Implement word count using both RDD and DataFrame APIs.

## Exercise 2: Sales Analysis
Load sales CSV, compute daily revenue by product category, find top 10 products.

## Exercise 3: Streaming Pipeline
Build a Spark Streaming application that reads from Kafka, aggregates events in 5-minute windows, and writes to Delta Lake.

## Exercise 4: Custom UDAF
Implement a user-defined aggregate function for percentile calculation.

## Exercise 5: Performance Tuning
Take a slow query, use explain plans to identify bottlenecks, and optimize it with AQE, broadcasting, and partitioning.
"@

$files["QUIZ.md"] = @"
# Apache Spark Quiz

## Question 1
What does RDD stand for?
- A) Resilient Distributed Dataset
- B) Reliable Distributed Data
- C) Redundant Data Distribution
- D) Randomized Data Delivery

## Question 2
What is the main advantage of DataFrames over RDDs?
- A) Type safety
- B) Catalyst optimizer for query optimization
- C) Better for Java
- D) More memory efficient

## Question 3
What triggers the execution of transformations in Spark?
- A) map() operation
- B) filter() operation
- C) Action (like count(), show(), save())
- D) groupBy() operation

## Answer Key
1: A, 2: B, 3: C
"@

$files["FLASHCARDS.md"] = @"
# Apache Spark Flashcards

## Card 1
**Front**: What is Apache Spark?
**Back**: Unified analytics engine for large-scale data processing with built-in modules for SQL, streaming, ML, and graph processing.

## Card 2
**Front**: What is an RDD?
**Back**: Resilient Distributed Dataset - immutable, partitioned collection that can be operated in parallel with automatic fault recovery.

## Card 3
**Front**: What is lazy evaluation in Spark?
**Back**: Transformations are not executed until an action is called, allowing Spark to optimize the execution plan.

## Card 4
**Front**: What is the Catalyst Optimizer?
**Back**: Spark SQL's query optimizer that transforms logical plans into efficient physical plans using rule and cost-based optimization.

## Card 5
**Front**: What is a shuffle in Spark?
**Back**: A redistribution of data across partitions, required for wide dependencies like groupBy or join, and is the most expensive operation.
"@

$files["INTERVIEW.md"] = @"
# Apache Spark Interview Questions

## Beginner
**Q**: Explain the difference between transformations and actions in Spark.
**A**: Transformations (map, filter, groupBy) create new RDDs lazily. Actions (count, collect, save) trigger execution and return results to the driver.

## Intermediate
**Q**: How does Spark handle fault tolerance?
**A**: RDDs track lineage - the sequence of transformations used to build them. If a partition is lost, Spark recomputes it using the lineage, avoiding replication overhead.

## Advanced
**Q**: Explain adaptive query execution in Spark 3.x.
**A**: AQE dynamically optimizes query plans at runtime using statistics collected during execution. It can coalesce shuffle partitions, switch join strategies, and handle skew automatically.
"@

$files["REFLECTION.md"] = @"
# Apache Spark Reflection

## Key Learnings
- Spark's in-memory processing provides massive speedup over disk-based systems
- Understanding the difference between narrow and wide dependencies is critical
- Catalyst optimizer and Tungsten make Spark highly efficient
- Debugging requires understanding the DAG and execution plan

## Questions to Explore
1. How does Spark Connect change the deployment model?
2. What are the tradeoffs between DataFrame API and SQL?
3. How does Spark compare to Flink for streaming workloads?
"@

$files["REFERENCES.md"] = @"
# Apache Spark References

## Books
- "Learning Spark" by Jules Damji, Brooke Wenig, Tathagata Das
- "Spark: The Definitive Guide" by Bill Chambers and Matei Zaharia
- "High Performance Spark" by Holden Karau and Rachel Warren

## Documentation
- Apache Spark: https://spark.apache.org/docs/latest/
- Databricks: https://docs.databricks.com

## Papers
- "Spark: Cluster Computing with Working Sets" (Zaharia et al., 2010)
- "Resilient Distributed Datasets" (Zaharia et al., 2012)
- "Apache Spark: A Unified Engine for Big Data Processing" (Zaharia et al., 2016)
"@

foreach ($file in $files.Keys) {
    $content = $files[$file]
    $path = Join-Path $base $file
    $content | Out-File -FilePath $path -Encoding utf8
    Write-Host "Created: $path"
}
Write-Host "Lab 04 complete"

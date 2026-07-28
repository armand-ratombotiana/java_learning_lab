# Lab 05: Batch Optimization — Implementation Guide

## Step 1: Configuring Spark for Performance

```java
SparkSession spark = SparkSession.builder()
    .appName("OptimizedBatch")
    .config("spark.sql.adaptive.enabled", "true")
    .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
    .config("spark.sql.adaptive.skewJoin.enabled", "true")
    .config("spark.sql.adaptive.maxNumPostShufflePartitions", "200")
    .config("spark.sql.shuffle.partitions", "200")
    .config("spark.sql.autoBroadcastJoinThreshold", "10485760") // 10MB
    .getOrCreate();
```

## Step 2: Broadcast Join

```java
// Small dimension table: forces broadcast hash join
Dataset<Row> dimData = spark.read().parquet("dim_small.parquet");
Dataset<Row> factData = spark.read().parquet("fact_large.parquet");
Dataset<Row> result = factData.join(broadcast(dimData), "key");
```

## Step 3: Salting for Skew

```java
// Add random salt to skewed key for repartitioning
Dataset<Row> salted = factData
    .withColumn("salt", (rand() * 10).cast("int"))
    .withColumn("salted_key", concat(col("key"), lit("_"), col("salt")));
```

## Step 4: Repartitioning Strategy

```java
// By partition key for co-partitioned joins
Dataset<Row> repartitioned = data.repartition(
    data.col("partition_key")
);

// Range partitioning for sort-merge join optimization
Dataset<Row> rangePartitioned = data.repartitionByRange(
    200, data.col("timestamp")
);
```

## Step 5: Memory Tuning Model

```java
// Spark memory fractions
// spark.memory.fraction = 0.6 (default: unified pool)
// spark.memory.storageFraction = 0.5 (default: 50% reserved for storage)
// spark.executor.memory = 4g
// spark.executor.cores = 4
// spark.executor.instances = 20

// Kryo serialization
spark.conf().set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
spark.conf().set("spark.kryo.registrationRequired", "true");
spark.conf().registerKryoClasses(new Class[]{MyRecord.class});
```

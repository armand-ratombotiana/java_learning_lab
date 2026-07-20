# Mini Spark

A simplified Spark-like compute engine in Java implementing RDD abstraction, transformations (map, filter, flatMap, reduceByKey), actions (collect, count, reduce), shuffle with hash partitioning, DAG scheduler, and task execution with configurable parallelism.

## Architecture Overview

```
┌──────────────┐     ┌──────────────────┐     ┌──────────────┐
│  SparkContext│────►│  DAGScheduler    │────►│  TaskExecutor│
│  (parallelize│     │  (stage building │     │  (thread pool│
│    config)   │     │   job scheduling)│     │   execution) │
└──────────────┘     └────────┬─────────┘     └──────────────┘
                              │
                    ┌─────────┴──────────┐
                    │                    │
              ┌─────┴────┐        ┌─────┴────┐
              │   RDD    │        │  PairRDD │
              │  (map/   │        │(reduceBy │
              │ filter/  │        │ Key/join)│
              │  flatMap)│        │          │
              └──────────┘        └────┬─────┘
                                       │
                                ┌──────┴──────┐
                                │  Shuffle     │
                                │  Manager     │
                                │  (hash part  │
                                │   sort-based)│
                                └─────────────┘
```

## Features

- **RDD**: Type-safe distributed dataset with map, filter, flatMap, distinct, union, intersection
- **PairRDD**: Key-value operations including reduceByKey, groupByKey, sortByKey, join, leftOuterJoin
- **SparkContext**: Application config, parallelize, textFile, RDD ID generation
- **DAGScheduler**: Job submission, stage building, parallel execution, status tracking
- **TaskExecutor**: Thread-pool based task execution with success/failure tracking, batch submission
- **ShuffleManager**: Hash-based partitioning, write/read shuffle blocks, configurable partitions

## Usage

```java
var sc = new SparkContext("my-app", "local[*]");
var rdd = sc.parallelize(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

var evens = rdd.filter(x -> x % 2 == 0);
var doubled = evens.map(x -> x * 2);
var sum = doubled.reduce(Integer::sum); // Optional[60]

var pairRdd = rdd.mapToPair(x -> Map.entry(x % 2 == 0 ? "even" : "odd", x));
var counts = pairRdd.reduceByKey((a, b) -> a + b);
```

# Module 59: Apache Spark & Flink - Edge Cases & Pitfalls

---

## Pitfall 1: Calling `collect()` on Massive Datasets in Spark

### ❌ Wrong
Calling the `collect()` action on an RDD or DataFrame that contains 500GB of data.
```java
JavaRDD<String> massiveData = sc.textFile("s3://logs/*");
// ❌ Fetches ALL data from 100 worker nodes into the single Driver node!
List<String> results = massiveData.collect(); 
```

### ✅ Correct
The `collect()` action forces all partitioned data from across the entire cluster to be sent over the network into the RAM of the single "Driver" node executing the main method. This instantly causes an `OutOfMemoryError` and crashes the job. Use actions like `take(10)` for debugging, or write the distributed result directly back to distributed storage (e.g., `.saveAsTextFile()`).

---

## Pitfall 2: OOM due to Missing Shuffles / Data Skew

### ❌ Wrong
Grouping or joining massive datasets where one specific key holds 90% of the data (Data Skew).
For example, grouping users by `country`. If 99% of your users are from "USA", one Spark executor gets handed 99% of the data to process, while the other 99 executors sit idle. The overloaded executor will run out of memory.

### ✅ Correct
Identify data skew early. "Salt" the skewed keys by appending random numbers (e.g., "USA_1", "USA_2") to distribute the data evenly across executors during the shuffle phase, process the partial aggregations, and then do a final aggregation to remove the salt.

---

## Pitfall 3: Not Handling Time Semantics in Flink

### ❌ Wrong
Processing an event stream in Apache Flink and assuming events arrive exactly in the order they were generated. Using standard processing time to group windows.

### ✅ Correct
In distributed systems, networks delay messages. Event 2 might arrive at the broker before Event 1. In Flink, you must use **Event Time** (extracting the timestamp directly from the JSON payload) rather than **Processing Time** (the time the server receives it). Use **Watermarks** to tell Flink how long to wait for late-arriving out-of-order events before closing a window calculation.
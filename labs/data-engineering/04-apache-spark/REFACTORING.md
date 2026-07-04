# Refactoring

## RDD to DataFrame
Before: `rdd.map(...).reduceByKey(...)`
After: `df.groupBy("key").count()`

## SQL to Dataset API
```java
// Before: spark.sql("SELECT * FROM sales")
// After: salesDF.select("*")
```

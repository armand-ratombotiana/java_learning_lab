# How Data Pipelines Work

## Core Mechanism
1. **Source Connectors**: JDBC, Kafka, REST APIs
2. **Transformation Layer**: Cleaning, enrichment, aggregation
3. **Sink Connectors**: Warehouse, data lake, search index

## Processing Semantics
- **At-most-once**: Fast but may lose data
- **At-least-once**: Reliable but may duplicate
- **Exactly-once**: Most reliable, highest overhead

```java
Dataset<Row> result = orders.join(customers, "id")
    .withColumn("total", functions.expr("qty * price"))
    .filter("total > 0");
result.write().mode("append").jdbc(url, "fact_orders", props);
```

# Code Deep Dive: Spark DataFrame API

## DataFrame Operations
```java
public class DataFrameOps {
    public Dataset<Row> analyzeSales(Dataset<Row> sales) {
        return sales
            .select("product_id", "amount", "date")
            .filter(col("amount").$greater(0))
            .withColumn("year", year(col("date")))
            .groupBy("product_id", "year")
            .agg(sum("amount").as("total"), count("*").as("count"))
            .orderBy(col("year").desc());
    }
}
```

## Spark Streaming
```java
Dataset<Row> stream = spark.readStream()
    .format("kafka").option("subscribe", "topic").load()
    .selectExpr("CAST(value AS STRING)");

streamingQuery = stream.writeStream()
    .outputMode("append").format("console").start();
```

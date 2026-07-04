# How ETL Works

## Extraction
```java
@Component
public class JdbcExtractor {
    public List<Order> extractIncremental(LocalDateTime lastRun) {
        return jdbc.query("SELECT * FROM orders WHERE updated_at > ?",
            new Object[]{Timestamp.valueOf(lastRun)}, new OrderRowMapper());
    }
}
```

## Transformation
```java
public Dataset<Row> applyBusinessRules(Dataset<Row> data) {
    return data.withColumn("segment",
        when(col("total").$greater(10000), "VIP").otherwise("STANDARD"));
}
```

## Load (Upsert)
```sql
MERGE INTO target t USING staging s ON t.id = s.id
WHEN MATCHED THEN UPDATE SET *
WHEN NOT MATCHED THEN INSERT *
```

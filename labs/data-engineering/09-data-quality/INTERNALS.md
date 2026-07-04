# Quality Internals

## Check Engine
```java
public abstract class QualityCheck {
    public abstract CheckResult run(Dataset<Row> data, QualityConfig config);
}
```

## Anomaly Detection
```java
public List<Anomaly> detect(Dataset<Row> data, String column) {
    double mean = data.agg(avg(col(column))).first().getDouble(0);
    double stddev = data.agg(stddev(col(column))).first().getDouble(0);
    return data.withColumn("z_score", abs(col(column).minus(mean)).divide(stddev))
        .filter(col("z_score").$greater(3.0)).collectAsList();
}
```

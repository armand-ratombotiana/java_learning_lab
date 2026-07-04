# Feature Store Internals

## Point-in-Time Join
```java
public Dataset<Row> pointInTimeCorrectJoin(Dataset<Row> features, Dataset<Row> labels,
        String entityKey, String featureTs, String labelTs) {
    return features.as("f")
        .join(labels.as("l"),
            f.col(entityKey).equalTo(l.col(entityKey))
                .and(f.col(featureTs).$less(l.col(labelTs))))
        .groupBy("l.*")
        .agg(max(struct(f.col(featureTs), f.col("*"))).as("latest"))
        .select("l.*", "latest.*");
}
```

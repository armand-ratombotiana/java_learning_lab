# Lab 05: Problem Walkthrough — Skew Join Optimizer

## Problem

Implement a `SkewJoinOptimizer` that detects which keys are skewed, applies salting to redistribute data, and performs the join using an optimized plan.

## Walkthrough

### Step 1: Detect Skew

```java
public class SkewDetector {
    private final double skewThreshold; // e.g., 0.1 means top 10% keys hold > threshold fraction

    public SkewDetector(double skewThreshold) { this.skewThreshold = skewThreshold; }

    public List<String> detectSkewedKeys(Dataset<Row> data, String keyColumn) {
        long total = data.count();
        var keyCounts = data.groupBy(keyColumn).count()
            .withColumn("fraction", col("count").divide(total))
            .filter(col("fraction").gt(skewThreshold))
            .select(keyColumn)
            .as(Encoders.STRING())
            .collectAsList();
        return keyCounts;
    }
}
```

### Step 2: Salting

```java
public Dataset<Row> addSalt(Dataset<Row> data, String keyColumn, String[] skewedKeys, int numSalts) {
    Broadcast<List<String>> broadcastSkew = javaSparkContext.broadcast(Arrays.asList(skewedKeys));
    return data.withColumn("_salt",
        when(col(keyColumn).isin(broadcastSkew.value().toArray()), rand().multiply(numSalts).cast("int"))
            .otherwise(0))
        .withColumn("_salted_key", concat(col(keyColumn), lit("_"), col("_salt")));
}
```

### Step 3: Salted Join

```java
public Dataset<Row> saltedJoin(Dataset<Row> fact, Dataset<Row> dim, String keyColumn, List<String> skewedKeys) {
    int numSalts = 10;
    Dataset<Row> saltedFact = addSalt(fact, keyColumn, skewedKeys, numSalts);
    // Create salted dimension by replicating
    Dataset<Row> saltedDim = dim.flatMap((Row row) -> {
        List<Row> result = new ArrayList<>();
        for (int i = 0; i < numSalts; i++) {
            Row salted = RowFactory.create(row.<String>getAs(keyColumn) + "_" + i, /* other fields */);
            result.add(salted);
        }
        return result.iterator();
    }, Encoders.bean(DimRow.class)).toDF();
    return saltedFact.join(saltedDim, "_salted_key");
}
```

## Complexity

- **Time**: O(N) for skew detection scan, O(N * S) for salted join where S = salts
- **Space**: O(S * ||dim||) for salted dimension

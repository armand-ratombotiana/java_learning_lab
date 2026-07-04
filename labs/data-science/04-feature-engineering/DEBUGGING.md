# Debugging Feature Engineering

## Model Performance Worse After Feature Engineering

**Symptom**: Adding new features decreases validation accuracy.

**Diagnosis**: Noise features, collinearity, or overfitting.

```java
// Check correlation of new feature with existing features
for (String existing : data.numericColumnNames()) {
    double r = Smile.correlation(
        data.doubleColumn(existing).asDoubleArray(),
        data.doubleColumn("new_feature").asDoubleArray()
    );
    if (Math.abs(r) > 0.95) {
        System.out.println("Collinear: " + existing + " r=" + r);
    }
}
```

## NaN Values After Transform

**Symptom**: Model fails with NaN loss.

**Diagnosis**: Log of negative or zero value, division by zero.

```java
// Debug transform
public DoubleColumn safeLog(DoubleColumn col) {
    for (int i = 0; i < col.size(); i++) {
        double v = col.getDouble(i);
        if (v <= 0) {
            System.out.println("WARN: log of " + v + " at row " + i);
        }
    }
    return log1p(col);
}
```

## Feature Importance Shows Zero for Engineered Features

**Symptom**: Tree model assigns zero importance to a feature you worked hard on.

**Diagnosis**: Feature is redundant with existing features, or the model can't split on it meaningfully.

```java
// Check: is the feature constant?
System.out.println("Unique values: " + data.doubleColumn("engineered_feature").countUnique());
// If only 2 unique values across 100K rows, it's not informative

// Check: does the feature vary with target?
Table byTarget = data.groupBy("target_bin")
    .mean("engineered_feature");
System.out.println(byTarget);
```

## Hot Encoding Creates Too Many Columns

**Symptom**: OutOfMemoryError after encoding.

**Diagnosis**: High-cardinality column encoded naively.

```java
// Fix: limit to top-k categories + "other"
StringColumn cat = data.stringColumn("city");
Table freq = cat.countByCategory().sortDescending("Count");
Set<String> top10 = new HashSet<>(freq.stringColumn("Category").first(10));
for (int i = 0; i < cat.size(); i++) {
    if (!top10.contains(cat.get(i))) {
        cat.set(i, "other");
    }
}
```

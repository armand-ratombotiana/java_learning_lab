# How Feature Engineering Works

## Common Feature Types and Methods

### 1. Numerical Transformations

```java
// Log transform for right-skewed distributions
public DoubleColumn logTransform(DoubleColumn col) {
    double[] result = new double[col.size()];
    for (int i = 0; i < col.size(); i++) {
        double v = col.getDouble(i);
        result[i] = Math.log1p(v);  // log(1+v) handles zero
    }
    return DoubleColumn.create("log_" + col.name(), result);
}

// Polynomial expansion
public Table polynomialFeatures(Table data, String col, int degree) {
    DoubleColumn c = data.doubleColumn(col);
    for (int d = 2; d <= degree; d++) {
        data.addColumn(power(c, d, col + "^" + d));
    }
    return data;
}
```

### 2. Categorical Encoding

```java
// One-hot encoding (for low-cardinality categories)
public Table oneHot(Table data, String col) {
    for (String val : data.stringColumn(col).unique()) {
        data.addColumn(BooleanColumn.create(col + "_" + val,
            data.stringColumn(col).isEqualTo(val)));
    }
    return data.dropColumns(col);
}

// Target encoding (mean of target per category)
public Table targetEncode(Table data, String catCol, String targetCol) {
    Table means = data.groupBy(catCol).mean(targetCol);
    // Map mean back to each row via join
    return data.joinOn(catCol).inner(means);
}
```

### 3. Date/Time Features

```java
public Table extractDateFeatures(Table data, String dateCol) {
    LocalDateColumn dates = data.dateColumn(dateCol);
    data.addColumn(IntColumn.create("year", dates.year()));
    data.addColumn(IntColumn.create("month", dates.month()));
    data.addColumn(IntColumn.create("day_of_week", dates.dayOfWeek()));
    data.addColumn(BooleanColumn.create("is_weekend", 
        dates.dayOfWeek().isGreaterThan(5)));
    data.addColumn(IntColumn.create("day_of_year", dates.dayOfYear()));
    return data;
}
```

### 4. Aggregation Features

```java
// Per-group aggregations
public Table addGroupAggregates(Table data, String groupCol, String valueCol) {
    Table means = data.groupBy(groupCol).mean(valueCol);
    means = means.withColumn("mean_" + valueCol, means.doubleColumn(1));
    return data.joinOn(groupCol).inner(means);
}
```

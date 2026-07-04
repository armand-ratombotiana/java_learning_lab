# Debugging EDA Workflows

## Summary Statistics Seem Wrong

**Symptom**: `mean()` returns a value outside the expected range.

**Diagnosis**: Missing values, outliers, or type coercion.

```java
// Debug missing values
DoubleColumn col = data.doubleColumn("salary");
System.out.println("Missing count: " + col.countMissing());
System.out.println("Min: " + col.min() + ", Max: " + col.max());
// If max is 9999999, there's a sentinel value that wasn't excluded

// Check for sentinel values
DoubleColumn clean = col.where(col.isLessThan(1_000_000));
System.out.println("Clean mean: " + clean.mean());
```

## Correlation Matrix Is Empty

**Symptom**: All correlations are NaN or 0.

**Diagnosis**: Column types are strings, not doubles.

```java
// Check types
data.columnNames().forEach(c -> 
    System.out.println(c + " -> " + data.column(c).type()));
// If numeric columns show as STRING, need parse:
// data.replaceColumn("age", data.stringColumn("age").asDoubleColumn());
```

## GroupBy Returns Only One Row

**Symptom**: `groupBy("state").count()` returns 1 row.

**Diagnosis**: The column has a single value, or all values are NaN.

```java
// Debug
System.out.println("Unique values: " + data.stringColumn("state").unique());
// If only one unique value appears, check the actual data
```

## Visualizations Are All Flat Lines

**Symptom**: Plotting shows horizontal/vertical lines.

**Diagnosis**: All x values (or y values) are identical, or data is sorted and x is monotonic with no variance.

```java
System.out.println("x unique: " + data.doubleColumn("x").countUnique());
System.out.println("y unique: " + data.doubleColumn("y").countUnique());
```

## Checklist

- [ ] Verify column types match expectations
- [ ] Check for sentinel/placeholder values (-1, 999, "N/A")
- [ ] Confirm missing values are marked as such (not as 0 or empty string)
- [ ] Sample rows before and after each transformation
- [ ] Run EDA on a small subset first, then scale

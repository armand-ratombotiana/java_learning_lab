# Common Mistakes in Data Wrangling

## 1. Filling Missing Values Without Thinking

**Mistake**: Fill all nulls with 0 or mean without considering column semantics.

```
salary: [50000, null, 75000, null, 90000]
After fill with 0: [50000, 0, 75000, 0, 90000]
Mean shifts from 71667 to 43000 — model gets biased.
```

✅ **Fix**: Use per-column strategies — median for income, mode for category, interpolation for time series.

## 2. Destroying Duplicates Without Investigation

**Mistake**: `dropDuplicateRows()` without understanding why duplicates exist.

✅ **Fix**: Group by duplicates and examine before dropping.

```java
// Investigate first
Table dupes = raw.duplicateRows("email");
System.out.println("Dupes per email:");
System.out.println(dupes.groupBy("email").count().sortDescending("Count"));
```

## 3. Silent Type Coercion

**Mistake**: Relying on implicit type conversion when parsing CSVs — strings like "N/A" become NaN, "ninety" becomes a parse failure logged to stderr.

✅ **Fix**: Explicit schema + validation set.

```java
// Validate after parse
StringColumn ageRaw = raw.stringColumn("age");
IntColumn ageValid = ageRaw.asIntColumn();
if (ageValid.countMissing() != ageRaw.countMissing()) {
    // Some strings like "thirty" failed to parse
    System.out.println("Parse failures: " + (ageRaw.countMissing() - ageValid.countMissing()));
}
```

## 4. Dropping Too Many Rows

**Mistake**: Using `dropRowsWithMissingValues()` on a dataset where 80% of rows have at least one missing value → dataset shrinks from 100K to 20K.

✅ **Fix**: Drop columns (not rows) when a column is mostly missing, or impute.

## 5. Forgetting to Reset the Index

After filtering, row indices retain original positions. Slicing, joining, or iterating with old indices causes off-by-one errors in Java.

```java
// After filtering, re-index explicitly
Table filtered = raw.where(...).copy();  // copy resets indices
```

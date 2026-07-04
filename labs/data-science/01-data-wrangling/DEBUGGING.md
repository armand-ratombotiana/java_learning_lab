# Debugging Data Wrangling Pipelines

## Common Failure Modes

### Pipeline Stops Producing Rows

**Symptom**: Final output has 0 rows.
**Diagnosis**: A filter is too aggressive or a join produces an empty result.

```java
// Debug: break the pipeline into stages
Table stage1 = raw.dropRowsWithMissingValues();
System.out.println("After dropNA: " + stage1.rowCount());

Table stage2 = stage1.where(stage1.doubleColumn("age").isGreaterThan(0));
System.out.println("After age filter: " + stage2.rowCount());
// If this is 0, check min() of age column
```

### Memory Errors (OOM)

**Symptom**: `OutOfMemoryError: Java heap space`
**Diagnosis**: Loading a >2GB CSV without chunking.

```java
// Fix: stream in chunks
for (int i = 0; i < totalRows; i += 10000) {
    Table chunk = raw.inRange(i, Math.min(i + 10000, totalRows));
    processChunk(chunk);
}
```

### Wrong Aggregation Results

**Symptom**: Mean salary = $50, but individual salaries are ~$70K.
**Diagnosis**: Unintended group-by — rows with null department were silently excluded or grouped into "null".

```java
// Check: was the group key null?
Table byDept = raw.groupBy("department").mean("salary");
System.out.println(byDept.where(byDept.stringColumn("department").isMissing()));
```

## Debugging Checklist

- [ ] Check row count at each pipeline stage
- [ ] Verify column types after parse
- [ ] Sample 100 rows before and after each transform
- [ ] Assert invariants (non-negative, not-null, unique constraints)
- [ ] Log the number of rows dropped at each cleaning step

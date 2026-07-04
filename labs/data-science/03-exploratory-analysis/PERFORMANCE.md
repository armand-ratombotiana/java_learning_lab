# Performance in EDA

## Bottlenecks

### 1. Full Dataset Load for Exploration

Loading a 50GB CSV just to compute basic statistics wastes time and memory.

**Solution**: Sample before exploring.

```java
// Explore on a sample, then validate on full data
Table sample = data.sampleN(10000);  // 10K rows sufficient for distribution shape
ProfileResult quick = DataProfiler.profile(sample);
```

### 2. Pairwise Correlation on Many Columns

O(d²) correlations where d = number of columns. For 1000 columns, that's ~500K correlations.

**Solution**: Filter to numeric columns of interest first, or use a threshold-based scan.

```java
// Only compute correlations for columns with sufficient variance
List<String> highVarColumns = data.numericColumnNames().stream()
    .filter(c -> data.doubleColumn(c).stdDev() > 0.01)
    .toList();
```

### 3. Unique Value Counts for High-Cardinality String Columns

`countUnique()` on a 10M-row string column requires building a full HashSet.

**Solution**: Use approximate counting (HyperLogLog).

```java
// HyperLogLog sketch for approximate cardinality
HyperLogLog hll = new HyperLogLog(16);  // 2^16 registers, ~1.6% error
for (String val : data.stringColumn("user_id")) {
    hll.add(val);
}
System.out.println("Approximate distinct users: " + hll.cardinality());
```

### 4. Histogram Bin Calculation Over Full Data

Full sort to compute bins is O(n log n).

**Solution**: Use streaming quantiles (T-Digest) to estimate bin boundaries on a first pass, then bin in a second pass.

## Profiling Memory

```java
// Tablesaw provides memory estimates
System.out.println("Data size: " + data.byteSize() / 1024 / 1024 + " MB");
// Each double column: n * 8 bytes + overhead
// Each string column: n * (avg string length + 48 bytes)
```

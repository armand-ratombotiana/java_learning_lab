# Performance in Data Wrangling

## Bottlenecks

### 1. CSV Parsing

CSV parsing is I/O bound and single-threaded by default.

**Improvements:**
- Use `BufferedReader` with large buffer (64KB+)
- Skip parsing columns you don't need
- Use binary formats (Parquet, Arrow) for intermediate storage

```java
// Faster: read only needed columns
// Tablesaw: read().csv() reads all columns
// To limit columns, manually specify schema with only needed columns
CsvReadOptions options = CsvReadOptions.builder("data.csv")
    .columns("id", "name", "age", "salary")
    .build();
Table t = Table.read().csv(options);
```

### 2. String Operations

String operations in Java allocate objects and strain the GC.

**Improvements:**
- Use `StringBuilder` for concatenation
- Reuse compiled `Pattern` objects
- Consider `CharSequence` projections instead of full `String` copies

```java
// Bad: creates intermediate String objects
raw.stringColumn("phone").replaceAll("\\D+", "");

// Better: pre-compile pattern
private static final Pattern NON_DIGIT = Pattern.compile("\\D+");
// column.mapEach(val -> NON_DIGIT.matcher(val).replaceAll(""))
```

### 3. Joins on Large Tables

Hash join complexity is O(n + m), but building the hash table on a column with high cardinality is expensive.

**Improvements:**
- Ensure the smaller table is the build side
- Sort-merge join for already-sorted data
- Use primitive int maps when join key is integer

### 4. Memory Limits

A 10M-row DataFrame with 20 double columns consumes ~1.6 GB (10M × 20 × 8 bytes).

**Improvements:**
- Convert doubles to floats where precision allows
- Use `IntColumn` instead of `LongColumn` where values fit in int range
- Process in partitions and discard after each batch

## Benchmark: Operation Costs (relative)

| Operation | Cost |
|---|---|
| `dropDuplicateRows` | O(n log n) — sorting |
| `groupBy().mean()` | O(n) — single pass |
| `innerJoin` | O(n + m) — hash join |
| `sortOn` | O(n log n) — TimSort |
| `write().csv()` | O(n) — I/O bound |

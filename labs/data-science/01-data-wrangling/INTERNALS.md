# Internals: How a DataFrame Stores Data

## Columnar vs. Row-Oriented Storage

Tablesaw (and most Java DataFrame libraries) stores data in **columnar** format — each column is a primitive array or object list. This contrasts with row-oriented storage (e.g., JDBC `ResultSet`).

```
Columnar:              Row-oriented:
Column "age":          Row 0: [John, 28, 50000]
  [28, 35, 42, ...]   Row 1: [Jane, 35, 75000]
Column "salary":      Row 2: [Bob, 42, 90000]
  [50000, 75000, 90000, ...]
```

### Why Columnar?

- **Cache-friendly aggregation**: computing `mean("salary")` reads one contiguous array
- **Vectorization**: SIMD-friendly for bulk operations
- **Compression**: same-typed values compress better

## Memory Layout in Tablesaw

```java
// Simplified internal structure
public class DoubleColumn extends AbstractColumn {
    private double[] data;          // Raw storage
    private int size;               // Logical size (may differ from array length)
    private BitSet missing;         // 1 bit per row: true = missing
    
    public double mean() {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (!missing.get(i)) {
                sum += data[i];
                count++;
            }
        }
        return sum / count;
    }
}
```

## Selection: The Filter/Mask Pattern

Filtering creates a `Selection` (a `RoaringBitmap`) rather than copying data:

```java
Selection s = column.where(column.isGreaterThan(50));
// s is a bitmap: [0, 0, 1, 1, 0, ...]
// Actual data stays in the original arrays — queries map over the bitmap
```

This lazy evaluation means memory is only consumed when `.copy()` or `.write()` is called.

## Join Implementation

Equi-joins build a hash map on the smaller table's join key, then probe the larger table. Tablesaw uses `IntIntHashMap` (from Koloboke) for integer keys.

```java
// Conceptual join logic
IntIntHashMap index = new IntIntHashMap();
for (int i = 0; i < small.rowCount(); i++) {
    index.put(small.getInt(i, joinCol), i);
}
for (int i = 0; i < large.rowCount(); i++) {
    int v = large.getInt(i, joinCol);
    if (index.containsKey(v)) {
        // emit combined row from large[i] + small[index.get(v)]
    }
}
```

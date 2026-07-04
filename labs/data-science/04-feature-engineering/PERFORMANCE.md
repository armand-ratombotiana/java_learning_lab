# Performance in Feature Engineering

## 1. One-Hot Encoding High-Cardinality Columns

Creating k boolean columns for k categories builds k new column objects.

**Optimization**: Use sparse matrix or frequency encoding.

```java
// Frequency encoding: one feature = category frequency
Table byCount = data.groupBy("city").count();
StringColumn categories = data.stringColumn("city");
DoubleColumn freqEncoded = DoubleColumn.create("city_freq", data.rowCount());
for (int i = 0; i < data.rowCount(); i++) {
    freqEncoded.set(i, byCount.doubleColumn("Count")
        .get(byCount.stringColumn("Category").indexOf(categories.get(i))));
}
// One column instead of #categories columns
```

## 2. Polynomial Feature Explosion

Degree-d polynomial expansion of p features creates $\binom{p+d}{d}$ features.

| p | d=2 | d=3 |
|---|---|---|
| 10 | 65 | 285 |
| 50 | 1,325 | 23,425 |
| 100 | 5,150 | 176,850 |

**Optimization**: Use only degree-2 interactions, or select interactions via mutual information first.

## 3. Date Feature Extraction on Many Rows

Parsing dates is expensive (DateTimeFormatter is thread-safe but slow).

**Optimization**: Parse once and cache components.

```java
// Cache date features after first extraction
public class DateFeatureCache {
    private Map<Integer, DateFeatures> cache = new HashMap<>();
    
    public DateFeatures getFeatures(LocalDate date) {
        return cache.computeIfAbsent(date.toEpochDay(), d -> 
            new DateFeatures(date));
    }
}
```

## 4. Rolling Window Aggregations (Time Series)

For each row, computing a rolling mean over the last 30 days is O(n × window) naive.

**Optimization**: Use cumulative sum.

```java
double sum = 0;
for (int i = 0; i < sorted.size(); i++) {
    sum += sorted.getDouble(i);
    if (i >= 30) sum -= sorted.getDouble(i - 30);
    rollingMean.set(i, sum / Math.min(30, i + 1));
}
```

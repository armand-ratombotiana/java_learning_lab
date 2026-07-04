# Mental Models for Data Wrangling

## 1. The Water Filter

Data flows from source to sink; wrangling is the filter. Each step removes one kind of impurity — missing values, outliers, duplicates, type mismatches — before the water reaches the model. If the filter leaks, contamination spreads to every downstream consumer.

## 2. The Assembly Line

Each wrangling operation is a station in a factory. Raw parts (rows) move down the line. Each station transforms or inspects. The output quality depends on the weakest station. Adding more operations at the end cannot fix early-stage errors.

## 3. Tidy Rectangles

A dataset is a rectangle: rows = observations, columns = variables. Wrangling is the process of getting data into this tidy rectangle form. Every wrangling problem can be framed as "what transformation moves my data closer to this ideal rectangle?"

## 4. Traceability Chain

Every value in the final dataset has a provenance. Good wrangling preserves lineage — if a value was imputed, binned, or derived, the transformation is recorded. In Java:

```java
// Immutable transformation chain preserves lineage
DataFrame original = load();
DataFrame transformed = original
    .copy()                              // snapshot lineage
    .addColumn("age_bin", binAge(col("age")))
    .addColumn("imputed_salary", imputeMean(col("salary")));
```

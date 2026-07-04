# Refactoring Data Wrangling Code

## Smell: God Pipeline

A single 200-line method chaining 30 operations.

```java
// Before: monolithic
Table result = raw
    .where(...).dropColumns(...).addColumn(...).fillNA(...)
    .where(...).sortOn(...).dropDuplicateRows(...).addColumn(...)
    .where(...).addColumn(...).replaceColumn(...).write().csv("out.csv");
```

**Refactor**: Extract named stages.

```java
// After: staged pipeline
Table clean = removeInvalidRows(raw);
Table enriched = addDerivedColumns(clean);
Table validated = validateConstraints(enriched);
validated.write().csv("out.csv");
```

## Smell: Repeated Transformations

The same cleaning logic appears in multiple pipelines.

**Refactor**: Extract reusable utility.

```java
public class DataCleaner {
    public static Table standardizeDates(Table t, String colName) {
        // ...
    }
    public static Table imputeNumericWithMedian(Table t, String colName) {
        // ...
    }
}
```

## Smell: Magic Numbers

Outlier threshold `1.5` appears five times with no explanation.

```java
// Before
col.isGreaterThan(q3 + 1.5 * iqr);

// After
private static final double IQR_MULTIPLIER = 1.5;
```

## Smell: Untested Wrangling

Wrangling has no unit tests → regression goes undetected.

```java
// Test the wrangling function
@Test
public void testDropRowsWithNegativeAge() {
    Table t = Table.create("test", IntColumn.create("age", new int[]{-5, 0, 25, 100}));
    Table result = DataCleaner.removeInvalidAges(t);
    assertEquals(3, result.rowCount());  // -5 should be removed
}
```

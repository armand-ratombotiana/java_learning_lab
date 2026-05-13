# Data Wrangling Lab

## Overview
Data wrangling is the process of cleaning, transforming, and organizing raw data into a format suitable for analysis. In Java, we use libraries like Tablesaw, DataFrame, and custom utilities to accomplish these tasks.

## 1. Core Data Structures

### DataFrame
A DataFrame is a 2-dimensional labeled data structure with columns of potentially different types. Think of it like a spreadsheet or SQL table.

```
Columns: name, age, salary, department
Rows:    [John, 28, 50000, Engineering]
        [Jane, 35, 75000, Marketing]
        [Bob, 42, 90000, Engineering]
```

### Series
A Series is a single column of data with an index. It's similar to a pandas Series.

## 2. Loading Data

### From CSV
```java
DataFrame df = DataFrame.read().csv("data.csv");
```

### From JSON
```java
DataFrame df = DataFrame.read().json("data.json");
```

### From Database
```java
DataFrame df = DataFrame.read()
    .jdbc("jdbc:mysql://localhost:3306/mydb", "SELECT * FROM users");
```

## 3. Data Inspection

### Basic Info
```java
df.shape();           // Returns [rows, columns]
df.columns();        // Returns list of column names
df.first(5);         // First 5 rows
df.last(5);          // Last 5 rows
df.types();          // Data types of columns
```

### Summary Statistics
```java
df.summary();        // Count, mean, std, min, max for numeric columns
df.describe();       // Detailed statistical summary
```

## 4. Selecting Data

### Selecting Columns
```java
df.column("name");              // Single column
df.columns("name", "age");      // Multiple columns
```

### Filtering Rows
```java
df.where(df.col("age").isGreaterThan(25));
df.where(df.col("department").equals("Engineering"));
df.where(df.col("salary").isBetween(50000, 80000));
```

### Combining Conditions
```java
df.where(df.col("age").isGreaterThan(25)
    .and(df.col("salary").isLessThan(100000)));
```

## 5. Data Cleaning

### Handling Missing Values
```java
// Remove rows with missing values
df.removeRowsWithNa();

// Fill missing values
df.fillNA().withMean();           // Numeric columns
df.fillNA().with("Unknown");      // String columns

// Check for missing values
df.isMissing().any();             // Any missing?
df.missingCount();                // Count per column
```

### Removing Duplicates
```java
df.dropDuplicateRows();
df.dropDuplicateRows().onColumns("email", "name");
```

### Data Type Conversion
```java
df.column("age").asInt();
df.column("salary").asDouble();
df.column("date").asDateIn("yyyy-MM-dd");
```

## 6. Transformation Operations

### Adding/Modifying Columns
```java
df.addColumns(df.col("salary").multiply(1.1).setName("new_salary"));
df.addColumn("full_name", df.col("first_name").concat(" ").concat(df.col("last_name")));
```

### Renaming Columns
```java
df.column("old_name").setName("new_name");
```

### Dropping Columns
```java
df.removeColumns("temp_column", "id");
```

### String Operations
```java
df.col("name").toUpperCase();
df.col("email").contains("@");
df.col("phone").replaceAll("\\D+", "");
```

## 7. Aggregation & Grouping

### Group By
```java
df.groupBy("department").mean("salary");
df.groupBy("department").sum("sales");
df.groupBy("department").count();
df.groupBy("department", "year").agg("salary", "mean");
```

### Aggregation Functions
- `sum()` - Sum of values
- `mean()` - Average
- `median()` - Middle value
- `min()` - Minimum
- `max()` - Maximum
- `count()` - Count of non-null values
- `std()` - Standard deviation

## 8. Sorting

```java
df.sortOn("age");                        // Ascending
df.sortOn("age", SortOrder.DESCENDING);  // Descending
df.sortOn("department", "salary");        // Multi-column
```

## 9. Merging & Joining

### Concatenation
```java
DataFrame combined = df1.append(df2);
```

### Join Operations
```java
DataFrame joined = df1.innerJoin(df2, "user_id");
DataFrame leftJoin = df1.leftJoin(df2, "user_id");
DataFrame outerJoin = df1.outerJoin(df2, "user_id");
```

## 10. Pivoting & Reshaping

### Pivot Tables
```java
df.pivot("department", "year", "salary", "mean");
```

### Melt (Wide to Long)
```java
df.melt("id", Arrays.asList("metric1", "metric2"), "variable", "value");
```

### Reshape Operations
```java
// Transpose
df.transpose();

// Long to Wide
df.pivot(indexCols, columnToPivot, valuesColumn);
```

## 11. Date/Time Handling

```java
df.col("date").asDateIn("yyyy-MM-dd HH:mm:ss");
df.col("date").year();
df.col("date").month();
df.col("date").dayOfWeek();
df.col("date").diff(df2.col("date"), DateTimeUnit.DAYS);
```

## 12. Practical Patterns

### Feature Extraction Pipeline
```java
DataFrame cleaned = raw
    .dropColumns("id", "temp")
    .where(raw.col("value").isNotMissing())
    .fillNA().with(0)
    .addColumn("normalized", normalize(raw.col("value")))
    .addColumn("category", binValues(raw.col("value"), 5));
```

### Data Validation
```java
boolean validAge = df.col("age").allMatch(val -> val >= 0 && val <= 150);
boolean validEmail = df.col("email").anyMatch(val -> val.contains("@"));
```

## 13. Performance Optimization

### Lazy Evaluation
Use `lazy()` for large datasets to avoid unnecessary computation.

### Chunk Processing
```java
df.partition(10000).forEach(chunk -> processChunk(chunk));
```

### Memory Management
```java
// Free memory after processing
df.close();

// Use streaming for large files
StreamDataFrame.stream("large_file.csv").forEach(row -> process(row));
```

## 14. Best Practices

1. **Inspect before transforming** - Always check your data first
2. **Handle missing values early** - Decide on strategy upfront
3. **Use method chaining** - Makes code readable and debuggable
4. **Log transformations** - Track data lineage
5. **Validate after each step** - Catch issues early
6. **Use appropriate data types** - Save memory, improve performance
7. **Document assumptions** - Make data decisions explicit

## 15. Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| Memory errors | Use chunking or streaming |
| Slow performance | Use lazy evaluation, indexes |
| Missing values | Remove, fill, or interpolate |
| Wrong types | Use `asXXX()` conversions |
| Duplicates | Use `dropDuplicateRows()` |
| Outliers | Identify with `percentile()` |
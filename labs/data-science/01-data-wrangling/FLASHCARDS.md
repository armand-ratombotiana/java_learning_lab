# Data Wrangling Flashcards

## Core Concepts

### Card 1: DataFrame Definition
**Q:** What is a DataFrame in data science?
**A:** A 2-dimensional labeled data structure with columns of potentially different types, similar to a spreadsheet or SQL table.

### Card 2: Shape Method
**Q:** What does `df.shape()` return?
**A:** An array `[rows, columns]` indicating the number of rows and columns.

### Card 3: Series Definition
**Q:** What is a Series?
**A:** A single column of data with an index, similar to a pandas Series or a column in a spreadsheet.

---

## Data Selection

### Card 4: Selecting Columns
**Q:** How do you select specific columns from a DataFrame?
**A:** `df.select("col1", "col2")` or `df.column("col_name")`

### Card 5: Filtering Rows
**Q:** How do you filter rows where age > 25?
**A:** `df.where(df.col("age").isGreaterThan(25))`

### Card 6: Combined Conditions
**Q:** How do you filter with multiple AND conditions?
**A:** `df.where(condition1.and(condition2))`

### Card 7: OR Conditions
**Q:** How do you filter where city is "NYC" OR "LA"?
**A:** `df.where(df.col("city").isIn("NYC", "LA"))`

### Card 8: Between Filter
**Q:** How do you filter values between 10 and 20?
**A:** `df.where(df.col("value").isBetween(10, 20))`

---

## Data Cleaning

### Card 9: Handle Missing Values
**Q:** How do you remove rows with missing values?
**A:** `df.removeRowsWithNa()`

### Card 10: Fill Missing Values
**Q:** How do you fill null values with the mean for numeric columns?
**A:** `df.fillNA().withMean()` or for single column: `fillNa(col, mean)`

### Card 11: Drop Duplicates
**Q:** How do you remove duplicate rows?
**A:** `df.dropDuplicateRows()` or with specific columns: `df.dropDuplicateRows().onColumns("email")`

### Card 12: Check Missing Values
**Q:** How do you check if any missing values exist?
**A:** `df.isMissing().any()` returns boolean

### Card 13: Rename Column
**Q:** How do you rename "old_name" to "new_name"?
**A:** `df.column("old_name").setName("new_name")`

### Card 14: Drop Columns
**Q:** How do you remove columns "id" and "temp"?
**A:** `df.dropColumns("id", "temp")` or `df.removeColumns("id", "temp")`

### Card 15: Convert Data Type
**Q:** How do you convert a column to integer?
**A:** `df.column("age").asInt()`

---

## String Operations

### Card 16: Uppercase
**Q:** How do you convert a column to uppercase?
**A:** `df.col("name").toUpperCase()`

### Card 17: Extract Digits
**Q:** How do you extract only digits from phone numbers?
**A:** `df.col("phone").replaceAll("\\D+", "")`

### Card 18: String Contains
**Q:** How do you check if emails contain "@"?
**A:** `df.col("email").contains("@")`

### Card 19: String Split
**Q:** How do you split "first_name last_name" into two columns?
**A:** Use string split operations or regex to separate at the space

---

## Aggregation

### Card 20: Group By
**Q:** How do you group by department and calculate mean salary?
**A:** `df.groupBy("department").mean("salary")`

### Card 21: Sum Aggregation
**Q:** How do you calculate total sales by region?
**A:** `df.groupBy("region").sum("sales")`

### Card 22: Count
**Q:** How do you count rows per group?
**A:** `df.groupBy("category").count()`

### Card 23: Multiple Aggregations
**Q:** How do you get sum, mean, min, max in one call?
**A:** `df.groupBy("dept").agg("salary", Arrays.asList("sum", "mean", "min", "max"))`

### Card 24: Multi-Column Group By
**Q:** How do you group by multiple columns (department AND year)?
**A:** `df.groupBy("department", "year")`

---

## Sorting

### Card 25: Sort Ascending
**Q:** How do you sort by age in ascending order?
**A:** `df.sortOn("age")` or `df.sortOn("age", SortOrder.ASCENDING)`

### Card 26: Sort Descending
**Q:** How do you sort by salary in descending order?
**A:** `df.sortOn("salary", SortOrder.DESCENDING)`

### Card 27: Multi-Column Sort
**Q:** How do you sort by department ASC, then salary DESC?
**A:** `df.sortOn("department", SortOrder.ASCENDING).sortOn("salary", SortOrder.DESCENDING)`

---

## Joins

### Card 28: Inner Join
**Q:** How do you perform an inner join on "user_id"?
**A:** `df1.innerJoin(df2, "user_id")` or `df1.join(df2, "user_id", JoinType.INNER)`

### Card 29: Left Join
**Q:** How do you keep all rows from the left DataFrame?
**A:** `df1.leftJoin(df2, "user_id")`

### Card 30: Append/Concatenate
**Q:** How do you stack two DataFrames vertically?
**A:** `df1.append(df2)`

---

## Transformations

### Card 31: Add Computed Column
**Q:** How do you add a column that is 10% salary increase?
**A:** `df.addColumn("new_salary", df.col("salary").multiply(1.1))`

### Card 32: Normalization Formula
**Q:** What is the formula for z-score normalization?
**A:** `z = (x - mean) / std`

### Card 33: Min-Max Scaling
**Q:** What does min-max scaling to [0,1] do?
**A:** Transforms values using: `(x - min) / (max - min)`

### Card 34: One-Hot Encoding
**Q:** What is one-hot encoding?
**A:** Converting categorical values to binary columns (1 if category matches, 0 otherwise)

### Card 35: Label Encoding
**Q:** What is label encoding?
**A:** Converting categorical values to integers (0, 1, 2, ...)

### Card 36: Binning
**Q:** What is binning/discretization?
**A:** Converting continuous values into discrete bins/categories

---

## Pivot Operations

### Card 37: Pivot Table
**Q:** How do you create a pivot table showing avg sales by (region, quarter)?
**A:** `df.pivot("region", "quarter", "sales", "mean")`

### Card 38: Melt
**Q:** What does melt() do?
**A:** Converts wide format data to long format

---

## Window Functions

### Card 39: Cumulative Sum
**Q:** How do you calculate running total of sales?
**A:** Add column using `cumsum()` function or manual loop accumulation

### Card 40: Moving Average
**Q:** How do you add a 3-period moving average?
**A:** Use rolling window operation: average of current and previous 2 values

### Card 41: Lag Function
**Q:** What does lag(col, 1) do?
**A:** Returns the value from 1 period ago (previous row)

### Card 42: Lead Function
**Q:** What does lead(col, 1) do?
**A:** Returns the value from 1 period ahead (next row)

### Card 43: Rank
**Q:** How do you rank values within a group (1 = highest)?
**A:** Use `rank()` with descending order within group

---

## Date Operations

### Card 44: Extract Year
**Q:** How do you extract the year from a date column?
**A:** `df.col("date").year()`

### Card 45: Date Difference
**Q:** How do you calculate days between two dates?
**A:** `diff(date1, date2, DateTimeUnit.DAYS)`

### Card 46: Parse Date
**Q:** How do you parse "2024-01-15" using pattern "yyyy-MM-dd"?
**A:** `LocalDate.parse("2024-01-15")` or `df.col("date").asDateIn("yyyy-MM-dd")`

### Card 47: Day of Week
**Q:** How do you get the day of week from a date?
**A:** `df.col("date").dayOfWeek()`

---

## Performance

### Card 48: Chunking
**Q:** When should you use chunking for large files?
**A:** When the file is too large to fit in memory

### Card 49: Lazy Evaluation
**Q:** What is lazy evaluation?
**A:** Postponing computation until results are actually needed

### Card 50: Memory Optimization
**Q:** How can you reduce memory usage?
**A:** Use appropriate data types, remove unused columns, use categorical types for low-cardinality strings

---

## Best Practices

### Card 51: Inspect Before Transform
**Q:** Why should you inspect data before transforming?
**A:** To understand data types, ranges, and quality issues

### Card 52: Data Lineage
**Q:** What does data lineage track?
**A:** Origin of data, transformations applied, and data flow

### Card 53: Validation
**Q:** When should you validate data?
**A:** After each transformation step to catch issues early

### Card 54: Documentation
**Q:** What should you document in data cleaning?
**A:** All transformations, decisions, and assumptions made

---

## CSV Operations

### Card 55: Read CSV
**Q:** How do you read a CSV file into a DataFrame?
**A:** `DataFrame.read().csv("path/to/file.csv")`

### Card 56: Write CSV
**Q:** How do you save a DataFrame to CSV?
**A:** `CSVParser.writeCSV(df, "output.csv")`

### Card 57: CSV with Headers
**Q:** How do you indicate the first row is a header?
**A:** `CSVOptions.hasHeader(true)`

### Card 58: Custom Delimiter
**Q:** How do you read a tab-separated file?
**A:** `CSVOptions.delimiter("\t")`

---

## Quality Checks

### Card 59: Missing Count
**Q:** How do you get count of missing values per column?
**A:** `df.missingCount()`

### Card 60: Unique Count
**Q:** How do you count unique values in a column?
**A:** `column.data.stream().distinct().count()`

### Card 61: Outlier Detection
**Q:** What is a common threshold for outlier detection using z-scores?
**A:** |z| > 3 (values more than 3 standard deviations from mean)

### Card 62: Data Quality Report
**Q:** What does a data quality report include?
**A:** Missing counts, unique counts, data types, outliers, and warnings

---

## Advanced Operations

### Card 63: Rolling Window
**Q:** What is a rolling window operation?
**A:** Applying a function (like mean) over a sliding window of rows

### Card 64: Exponential Smoothing
**Q:** How is exponential moving average different from simple MA?
**A:** EMA gives more weight to recent values using exponential decay

### Card 65: Percentile Rank
**Q:** How do you calculate percentile rank?
**A:** (Number of values below x / total values) * 100

### Card 66: Difference from Mean
**Q:** How do you calculate how far each value is from group mean?
**A:** `value - groupMean` or standardized: `(value - mean) / std`

---

## Common Patterns

### Card 67: ETL Pipeline
**Q:** What does ETL stand for?
**A:** Extract, Transform, Load

### Card 68: Feature Extraction
**Q:** What is feature extraction?
**A:** Creating new derived features from raw data

### Card 69: Imputation Strategy
**Q:** What are common imputation strategies?
**A:** Mean, median, forward fill, backward fill, interpolation

### Card 70: Cross Validation
**Q:** What is cross validation in data pipelines?
**A:** Splitting data into train/test multiple times to validate models

---

## Java-Specific

### Card 71: DataFrame Library Options
**Q:** What Java libraries provide DataFrame functionality?
**A:** Tablesaw, Joinery, DataFrame (simple), SDV, Deequ

### Card 72: Stream Integration
**Q:** How do Java Streams integrate with DataFrame operations?
**A:** Use `.stream()` and `.collect()` for custom transformations

### Card 73: Null Handling in Java
**Q:** How does Java handle null in numeric operations?
**A:** Null propagates; operations return null when any operand is null

### Card 74: Type Inference
**Q:** How do you infer data type from values in Java?
**A:** Check patterns (digits, dates, decimals) for automatic type conversion

### Card 75: Method Chaining
**Q:** Why is method chaining preferred in DataFrame operations?
**A:** Makes code readable, enables fluent API design, allows composition
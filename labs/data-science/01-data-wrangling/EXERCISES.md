# Data Wrangling Exercises

## Exercise 1-5: Basic DataFrame Operations

### Exercise 1: Create and Inspect DataFrame
Create a DataFrame with student data (id, name, age, gpa) and print its shape, columns, and first 3 rows.

```java
// Sample data
Student 1: 1, "Alice", 20, 3.8
Student 2: 2, "Bob", 22, 3.5
Student 3: 3, "Charlie", 19, 3.9
Student 4: 4, "Diana", 21, 3.7
Student 5: 5, "Eve", 23, 3.6
```

### Exercise 2: Filter by Condition
From the student DataFrame, filter to get:
- Students older than 20
- Students with GPA >= 3.7
- Students with age between 20 and 22 (inclusive)

### Exercise 3: Select and Project
Select only the name and gpa columns from the student DataFrame.

### Exercise 4: Add Computed Column
Add a column `honors` that is true if gpa >= 3.8.

### Exercise 5: Sort DataFrame
Sort students by:
- GPA descending (highest first)
- Age ascending (youngest first)
- Name alphabetically

---

## Exercise 6-10: Data Cleaning

### Exercise 6: Handle Missing Values
Create a DataFrame with some null values and:
- Fill null ages with 20
- Fill null GPAs with 0.0
- Remove rows with null names

### Exercise 7: Remove Duplicates
Create a DataFrame with duplicate rows and remove them.

### Exercise 8: Rename Columns
Rename columns from `student_id` to `id`, `grade_point_avg` to `gpa`.

### Exercise 9: Drop Columns
Remove the `temp` and `unused` columns from a DataFrame.

### Exercise 10: Data Type Conversion
Convert:
- String "25" to integer 25
- String "3.14" to double 3.14
- String "2024-01-15" to LocalDate

---

## Exercise 11-15: Aggregation and Grouping

### Exercise 11: Group By Single Column
Group sales data by department and calculate total sales.

```java
// Sales data
Department | Sales
-----------+------
Electronics| 5000
Clothing   | 3000
Electronics| 4500
Clothing   | 2500
Electronics| 6000
```

### Exercise 12: Group By Multiple Columns
Group by (department, month) and calculate average sales.

### Exercise 13: Multiple Aggregations
For each department, calculate sum, mean, min, max of sales.

### Exercise 14: Count by Category
Count how many products in each category.

### Exercise 15: Conditional Aggregation
Calculate average salary by department, but only for employees with >2 years experience.

---

## Exercise 16-20: String Operations

### Exercise 16: Uppercase/Lowercase
Create a column `email_upper` with uppercase emails, and `name_lower` with lowercase names.

### Exercise 17: Extract Substring
Extract area code from phone numbers formatted as "(123) 456-7890".

### Exercise 18: String Replace
Replace all occurrences of "Inc." with "Incorporated" in company names.

### Exercise 19: String Contains
Create a boolean column `has_gmail` that is true if email contains "gmail.com".

### Exercise 20: String Split
Split a full_name column into `first_name` and `last_name`.

---

## Exercise 21-25: Date/Time Operations

### Exercise 21: Parse Dates
Parse dates in format "15-Jan-2024" to LocalDate objects.

### Exercise 22: Extract Components
From a date column, extract year, month, and day as separate columns.

### Exercise 23: Date Difference
Calculate the number of days between `start_date` and `end_date` columns.

### Exercise 24: Date Filtering
Filter rows where date is in January 2024.

### Exercise 25: Date Range
Filter rows where date is between 2024-01-01 and 2024-06-30.

---

## Exercise 26-30: Joins and Merging

### Exercise 26: Inner Join
Join employees and departments on `dept_id`.

### Exercise 27: Left Join
Left join products and categories, keeping all products.

### Exercise 28: Multiple Join Keys
Join orders and customers on both (customer_id, region).

### Exercise 29: Concatenate Vertically
Stack two DataFrames with the same columns vertically.

### Exercise 30: Append with Different Columns
Append DataFrames with different columns, filling missing with null.

---

## Exercise 31-35: Advanced Transformations

### Exercise 31: Normalize Column
Apply z-score normalization to a numeric column.

### Exercise 32: Min-Max Scaling
Scale values to range [0, 1] using min-max scaling.

### Exercise 33: One-Hot Encode
One-hot encode the `category` column with values A, B, C.

### Exercise 34: Label Encode
Convert categorical `status` column to numeric labels.

### Exercise 35: Bin Numeric Data
Bin ages into categories: 0-18 (young), 19-35 (adult), 36+ (senior).

---

## Exercise 36-40: Pivoting and Reshaping

### Exercise 36: Pivot Table
Create a pivot table showing average sales by (region, quarter).

### Exercise 37: Melt DataFrame
Melt wide format data to long format.

### Exercise 38: Transpose
Transpose a small DataFrame (swap rows and columns).

### Exercise 39: Cross Tabulation
Create a cross-tabulation of (gender, department) showing counts.

### Exercise 40: Unstack
Convert a multi-index result to a wide format.

---

## Exercise 41-45: Window Functions

### Exercise 41: Running Total
Add a column with cumulative sum of sales.

### Exercise 42: Moving Average
Add a 3-period moving average of daily values.

### Exercise 43: Lag Feature
Add columns with values from 1, 2, and 3 periods ago.

### Exercise 44: Lead Feature
Add columns showing next period values.

### Exercise 45: Rank Within Group
Rank students within each department by GPA (1 = highest).

---

## Exercise 46-50: Complex Filtering

### Exercise 46: Multiple Conditions with AND
Filter rows where (age > 30) AND (salary > 50000) AND (status == "active").

### Exercise 47: Multiple Conditions with OR
Filter rows where (city == "NYC") OR (city == "LA") OR (city == "Chicago").

### Exercise 48: Complex NOT Logic
Filter rows where NOT (age < 25 AND department == "Sales").

### Exercise 49: In-List Filter
Filter rows where status is in ["active", "pending", "review"].

### Exercise 50: Between Filter
Filter rows where value is between lower and upper thresholds.

---

## Exercise 51-55: Data Validation

### Exercise 51: Validate Email Format
Check if all emails contain "@" and "." after @.

### Exercise 52: Validate Numeric Range
Check if all ages are between 0 and 150.

### Exercise 53: Validate Uniqueness
Check if all values in a column are unique.

### Exercise 54: Validate Completeness
Check if any row has missing values in critical columns.

### Exercise 55: Generate Quality Report
Generate a complete data quality report for a DataFrame.

---

## Exercise 56-60: Advanced Aggregation

### Exercise 56: Cumulative Distribution
Calculate what percentage of values fall below each value.

### Exercise 57: Percentile Rank
Add a column showing the percentile rank of each value.

### Exercise 58: Weighted Average
Calculate weighted average of scores where weights are credit hours.

### Exercise 59: Conditional Count
Count how many values in each group exceed a threshold.

### Exercise 60: Running Maximum
Add a column showing the maximum value seen so far.

---

## Exercise 61-65: Real-World Scenarios

### Exercise 61: Clean Phone Numbers
Extract digits from messy phone formats like "(123) 456-7890 ext 123".

### Exercise 62: Parse Log Files
Parse semi-structured log data into a DataFrame.

### Exercise 63: Calculate Customer LTV
Calculate lifetime value from transaction history.

### Exercise 64: Detect Changes
Identify when values in a time series change significantly.

### Exercise 65: Impute Time Series
Fill missing values in a time series using interpolation.

---

## Exercise 66-70: Performance Optimization

### Exercise 66: Chunk Large Files
Process a 10GB CSV file in chunks of 100,000 rows.

### Exercise 67: Select Only Needed Columns
Read only specific columns from a CSV to reduce memory.

### Exercise 68: Lazy Filtering
Use lazy evaluation to filter before aggregations.

### Exercise 69: Vectorized Operations
Replace row-by-row loops with vectorized column operations.

### Exercise 70: Memory-Efficient Types
Convert string columns to integers/categories to save memory.

---

## Exercise 71-75: Complex Joins

### Exercise 71: Self Join
Join employees with their managers (same table).

### Exercise 72: Rolling Window Join
Join events that occur within 5 minutes of each other.

### Exercise 73: Fuzzy Join
Join on names with tolerance for minor spelling differences.

### Exercise 74: Exclude Match
Find rows in table A that have no match in table B.

### Exercise 75: Rolling Update
Update table A values with values from table B where keys match.

---

## Exercise 76-80: Statistical Operations

### Exercise 76: Rolling Standard Deviation
Add a 5-period rolling standard deviation column.

### Exercise 77: Exponential Smoothing
Apply simple exponential smoothing to a time series.

### Exercise 78: Difference from Mean
Add column showing how far each value is from the group mean.

### Exercise 79: Coalesce Values
Use values from column B to fill nulls in column A.

### Exercise 80: Conditional Fill
Fill missing values differently based on another column's value.

---

## Exercise 81-85: Data Pipeline

### Exercise 81: Build ETL Pipeline
Create a complete ETL: Extract from CSV, Transform with multiple steps, Load to output.

### Exercise 82: Handle Schema Changes
Adapt to additional columns appearing in new data files.

### Exercise 83: Unit of Work
Implement a transaction: if any step fails, roll back all changes.

### Exercise 84: Data Lineage
Track and log each transformation step applied to data.

### Exercise 85: Pipeline Composition
Build reusable pipeline components that can be chained.

---

## Exercise 86-90: Edge Cases

### Exercise 86: Empty DataFrame
Handle operations gracefully when DataFrame has no rows.

### Exercise 87: Single Row
Handle operations on DataFrame with only one row.

### Exercise 88: All Null Column
Handle column that contains only null values.

### Exercise 89: Mixed Types
Handle column containing both numbers and strings.

### Exercise 90: Unicode Handling
Properly handle UTF-8 characters in string columns.

---

## Exercise 91-95: Data Transformation Patterns

### Exercise 91: Case-When Logic
Implement SQL-style CASE WHEN: assign categories based on value ranges.

### Exercise 92: Normalize Nested JSON
Flatten nested JSON data into a flat DataFrame.

### Exercise 93: Pivot with Aggregation
Pivot while handling duplicate entries with aggregation.

### Exercise 94: Rolling Correlation
Calculate 30-day rolling correlation between two series.

### Exercise 95: Time-Based Grouping
Group data into 5-minute intervals and aggregate.

---

## Exercise 96-100: Advanced Challenges

### Exercise 96: Build a Rolling Window Join
Join two DataFrames where matching rows can be within a time window.

### Exercise 97: Implement a Custom Aggregator
Create a custom median aggregator.

### Exercise 98: Detect Anomalies
Detect anomalies using z-score > 3 as threshold.

### Exercise 99: Calculate Moving Window Stats
Calculate min, max, mean, std for each 7-day window.

### Exercise 100: Create Complete Data Pipeline
Build a production-ready pipeline with logging, error handling, and validation.
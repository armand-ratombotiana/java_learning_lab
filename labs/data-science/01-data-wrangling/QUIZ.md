# Data Wrangling Quiz

## Section 1: DataFrame Basics (1-10)

**Question 1:** What is the primary data structure for tabular data in data science libraries?
- A) Array
- B) DataFrame
- C) Tree
- D) Graph

**Question 2:** How do you get the number of rows and columns from a DataFrame?
- A) `df.size()`
- B) `df.shape()`
- C) `df.dimensions()`
- D) `df.count()`

**Question 3:** Which method selects specific columns from a DataFrame?
- A) `df.filter()`
- B) `df.select()`
- C) `df.where()`
- D) `df.slice()`

**Question 4:** What does `df.first(5)` return?
- A) First column
- B) First 5 columns
- C) First 5 rows
- D) First row

**Question 5:** Which operation filters rows based on a condition?
- A) `select()`
- B) `filter()` / `where()`
- C) `groupBy()`
- D) `sortOn()`

**Question 6:** What is the result of `df.columns()`?
- A) A single column
- B) A list of column names
- C) The number of columns
- D) Column data types

**Question 7:** How do you get a single column from a DataFrame?
- A) `df["column_name"]`
- B) `df.column("column_name")`
- C) `df->column_name`
- D) Both A and B

**Question 8:** What does `df.shape()` return for a DataFrame with 100 rows and 5 columns?
- A) [5, 100]
- B) [100, 5]
- C) {100, 5}
- D) "100 x 5"

**Question 9:** Which method removes duplicate rows?
- A) `removeDuplicates()`
- B) `dropDuplicateRows()`
- C) `unique()`
- D) `dedupe()`

**Question 10:** What is returned by `df.types()`?
- A) Number of data types
- B) List of data types for each column
- C) Type of the DataFrame
- D) Type conversions available

---

## Section 2: Data Cleaning (11-20)

**Question 11:** What does `fillNA().withMean()` do?
- A) Removes all null values
- B) Fills null values with the column mean
- C) Calculates the mean of all columns
- D) Finds rows with null values

**Question 12:** How do you remove rows containing missing values?
- A) `df.removeRowsWithNa()`
- B) `df.dropNa()`
- C) `df.clean()`
- D) Both A and B

**Question 13:** Which method converts a string column to integer?
- A) `df.col("age").toInt()`
- B) `df.column("age").asInt()`
- C) `df.column("age").convert(Integer.class)`
- D) `df.int("age")`

**Question 14:** What is the result of `df.missingCount()`?
- A) Total missing count
- B) Count of missing values for each column
- C) Rows with missing values
- D) Columns with missing values

**Question 15:** When should you consider dropping a column?
- A) When it has >90% missing values
- B) When it has only one unique value
- C) When it's completely redundant with another column
- D) All of the above

**Question 16:** What does `isMissing().any()` return?
- A) Number of missing values
- B) True if any cell is missing, false otherwise
- C) Locations of missing values
- D) Percentage of missing values

**Question 17:** How do you rename a column from "old_name" to "new_name"?
- A) `df.rename("old_name", "new_name")`
- B) `df.column("old_name").setName("new_name")`
- C) `df.replaceColumn("old_name", "new_name")`
- D) Both A and B

**Question 18:** What is proper handling of null values in numeric columns?
- A) Always remove them
- B) Always fill with 0
- C) Analyze impact and choose appropriate strategy
- D) Convert them to "null" strings

**Question 19:** What does `dropDuplicateRows().onColumns("email")` do?
- A) Removes all duplicate rows
- B) Removes rows where email duplicates exist
- C) Finds duplicate emails
- D) Counts duplicate emails

**Question 20:** Which is NOT a good data cleaning practice?
- A) Documenting all transformations
- B) Validating after each step
- C) Making irreversible changes without backup
- D) Logging data lineage

---

## Section 3: Filtering and Selection (21-30)

**Question 21:** How do you filter rows where age > 25?
- A) `df.filter(age > 25)`
- B) `df.where(df.col("age").isGreaterThan(25))`
- C) `df.select(age > 25)`
- D) `df.filter("age", ">", 25)`

**Question 22:** What does `df.where(condition1).and(condition2)` achieve?
- A) Combines conditions with AND
- B) Chains two where clauses
- C) Adds new conditions
- D) Both A and B

**Question 23:** How to filter rows where city is "NYC" or "LA"?
- A) `df.where(city == "NYC" || city == "LA")`
- B) `df.where(df.col("city").isIn("NYC", "LA"))`
- C) `df.filter(city.in(["NYC", "LA"]))`
- D) Both B and C

**Question 24:** What does `df.where(df.col("value").isBetween(10, 20))` return?
- A) Rows where value is 10 or 20
- B) Rows where value is between 10 and 20 (inclusive)
- C) Rows where value is not between 10 and 20
- D) Rows where value is exactly 10 to 20

**Question 25:** How do you select columns "name", "age", "salary"?
- A) `df.select("name", "age", "salary")`
- B) `df.columns("name", "age", "salary")`
- C) `df.where("name", "age", "salary")`
- D) `df.filter("name", "age", "salary")`

**Question 26:** Which method sorts data ascending by "age"?
- A) `df.sort("age")`
- B) `df.sortOn("age")`
- C) `df.orderBy("age", ASC)`
- D) Both B and C

**Question 27:** How do you sort by multiple columns (department ASC, salary DESC)?
- A) `df.sortOn("department", "salary")`
- B) `df.sortOn("department").sortOn("salary", DESC)`
- C) `df.sortOn("department", SortOrder.ASC).sortOn("salary", SortOrder.DESC)`
- D) `df.sortMulti(List.of("department", "salary"))`

**Question 28:** What does `df.last(10)` return?
- A) Last 10 columns
- B) Last 10 rows
- C) The 10th last row
- D) Rows where index > 10

**Question 29:** How to exclude certain columns from selection?
- A) `df.exclude("id", "temp")`
- B) Use select with all except those columns
- C) `df.dropColumns("id", "temp")`
- D) Both B and C work for this

**Question 30:** What is the difference between `filter()` and `where()`?
- A) `filter()` works on rows, `where()` works on columns
- B) They are synonyms for the same operation
- C) `filter()` is for regex, `where()` is for conditions
- D) `where()` is lazy, `filter()` is eager

---

## Section 4: Aggregation (31-40)

**Question 31:** How do you group by "department" and get mean salary?
- A) `df.group("department").mean("salary")`
- B) `df.groupBy("department").mean("salary")`
- C) `df.groupby("department").avg("salary")`
- D) `df.groupby("department", "salary", "mean")`

**Question 32:** Which aggregation function calculates the middle value?
- A) mean
- B) median
- C) mode
- D) middle

**Question 33:** What does `df.groupBy("dept").count()` return?
- A) Count of non-null values for all columns
- B) Number of rows per department
- C) Unique count per department
- D) Total count including nulls

**Question 34:** How to get multiple aggregations (sum, mean, count) in one call?
- A) `df.agg("col", ["sum", "mean", "count"])`
- B) `df.aggregate("sum", "mean", "count")`
- C) `df.groupBy("dept").agg("salary", "sum", "mean", "count")`
- D) `df.aggAll("col", Fns.sum, Fns.mean, Fns.count)`

**Question 35:** What is the result of grouping by multiple columns?
- A) Single-level groups
- B) Hierarchical/Multi-level groups
- C) Cross-tabulation
- D) Union of groups

**Question 36:** Which method returns the sum of a column?
- A) `col.sum()`
- B) `col.total()`
- C) `col.add()`
- D) `col.aggregate(SUM)`

**Question 37:** How do you calculate standard deviation of a column?
- A) `col.std()`
- B) `col.standardDeviation()`
- C) `col.stdev()`
- D) Both A and B

**Question 38:** What does `groupBy("dept").min("salary")` return?
- A) The row with minimum salary
- B) Minimum salary value per department
- C) All rows with minimum salary
- D) Department with minimum salary

**Question 39:** Which is NOT a valid aggregation function?
- A) sum
- B) mean
- C) mode
- D) concat

**Question 40:** How to apply different aggregations to different columns?
- A) Use a map of column -> aggregation
- B) Call groupBy multiple times
- C) Use `agg()` with multiple column specifications
- D) Both A and C

---

## Section 5: Joins and Merging (41-50)

**Question 41:** What is an inner join?
- A) Keeps all rows from both tables
- B) Keeps only rows matching in both tables
- C) Keeps all rows from left table
- D) Keeps all rows from right table

**Question 42:** Which join keeps all rows from the left table?
- A) Inner join
- B) Right join
- C) Left join
- D) Outer join

**Question 43:** How do you perform an inner join on "user_id"?
- A) `df1.innerJoin(df2, "user_id")`
- B) `df1.join(df2, "user_id", "inner")`
- C) `df1.merge(df2, "user_id")`
- D) Both A and B

**Question 44:** What is the result of `df1.append(df2)`?
- A) Vertical concatenation (more rows)
- B) Horizontal concatenation (more columns)
- C) Intersection of rows
- D) Union of columns

**Question 45:** When joining tables with duplicate column names (not join keys), what happens?
- A) Error is thrown
- B) One column is kept
- C) Both columns are kept with disambiguated names
- D) Duplicate columns are dropped

**Question 46:** Which join returns rows when there's a match in either table?
- A) Inner join
- B) Left join
- C) Right join
- D) Outer/Full join

**Question 47:** What happens if join keys have different names in two tables?
- A) Cannot perform join
- B) Must specify both left and right key names
- C) Automatic matching occurs
- D) One table must be renamed

**Question 48:** How do you perform a right join?
- A) `df1.rightJoin(df2, "key")`
- B) `df2.rightJoin(df1, "key")`
- C) `df1.join(df2, "key", RIGHT)`
- D) All of the above

**Question 49:** What is a natural join?
- A) Join on all columns with same name
- B) Join on specified columns
- C) Join without specifying keys
- D) Both A and C

**Question 50:** When might a Cartesian product (cross join) occur accidentally?
- A) Missing join condition
- B) Multiple rows with same key
- C) Both A and B
- D) When using outer join

---

## Section 6: Transformation (51-60)

**Question 51:** How do you add a column that is 10% salary increase?
- A) `df.add("new_salary", salary * 1.1)`
- B) `df.addColumn("new_salary", df.col("salary").multiply(1.1))`
- C) `df.withColumn("new_salary", salary * 1.1)`
- D) Both B and C

**Question 52:** What does `df.col("name").toUpperCase()` return?
- A) Modifies the original column
- B) Returns a new column with uppercase values
- C) Returns the column name in uppercase
- D) Creates a new DataFrame

**Question 53:** How do you drop columns "id" and "temp"?
- A) `df.drop("id", "temp")`
- B) `df.removeColumns("id", "temp")`
- C) `df.dropColumns("id", "temp")`
- D) Both B and C

**Question 54:** What does `df.col("phone").replaceAll("\\D+", "")` do?
- A) Removes all non-digit characters
- B) Replaces phone with empty string
- C) Finds all digit patterns
- D) Validates phone format

**Question 55:** How to create a column combining first_name and last_name?
- A) `df.concat("first_name", "last_name")`
- B) `df.col("first_name").concat(" ").concat(df.col("last_name"))`
- C) `df.merge("first_name", "last_name")`
- D) `df.combine("first_name", "last_name")`

**Question 56:** What is z-score normalization?
- A) Scales values to [0, 1]
- B) Centers data around mean with unit variance
- C) Scales to percentage
- D) Removes outliers

**Question 57:** What does min-max scaling to [0, 1] do to value 50 in range [0, 100]?
- A) 0
- B) 0.5
- C) 50
- D) 1

**Question 58:** What is one-hot encoding?
- A) Binary encoding of categories
- B) Converting to single numeric value
- C) Encoding with powers of 2
- D) Boolean conversion

**Question 59:** Which is NOT a proper encoding for categorical data?
- A) One-hot encoding
- B) Label encoding
- C) String encoding
- D) Target encoding

**Question 60:** What does binning (discretization) do?
- A) Groups continuous values into bins/categories
- B) Removes outliers
- C) Normalizes data
- D) Encodes text

---

## Section 7: Advanced Operations (61-70)

**Question 61:** What is the purpose of pivot tables?
- A) Rotate data
- B) Summarize data with row/column aggregation
- C) Filter data
- D) Join tables

**Question 62:** What does `melt()` do?
- A) Melts ice
- B) Converts wide format to long format
- C) Converts long format to wide format
- D) Aggregates data

**Question 63:** What is a window function?
- A) Function for opening windows
- B) Function operating on a set of rows relative to current row
- C) GUI function
- D) File operation

**Question 64:** What does cumulative sum (cumsum) calculate?
- A) Sum of all values
- B) Running total where each value adds to previous
- C) Sum of differences
- D) Sum of squares

**Question 65:** What is a lag function?
- A) Moves data forward
- B) Accesses previous row values
- C) Creates delays
- D) Both B and C

**Question 66:** What does a 3-period moving average compute?
- A) Average of last 3 values including current
- B) Average of next 3 values
- C) Sum of 3 values
- D) Maximum of 3 values

**Question 67:** What is the difference between rank() and dense_rank()?
- A) Same function
- B) rank() leaves gaps, dense_rank() doesn't
- C) dense_rank() is slower
- D) rank() is for strings

**Question 68:** How do you calculate percentage change between consecutive rows?
- A) `df.col("value").diff()`
- B) `df.col("value").diff() / df.col("value").lag(1)`
- C) `df.col("value").pctChange()`
- D) Both B and C

**Question 69:** What does `fillNaForward()` do?
- A) Fills nulls with the next valid value
- B) Fills nulls with the previous valid value
- C) Fills all nulls with 0
- D) Fills nulls with mean

**Question 70:** What is exponential moving average different from simple moving average?
- A) Gives more weight to recent values
- B) Is faster to compute
- C) Uses all historical data equally
- D) Requires more memory

---

## Section 8: Performance and Best Practices (71-80)

**Question 71:** When should you use chunking for large files?
- A) Always for better performance
- B) When file is too large to fit in memory
- C) Never, Java handles it automatically
- D) Only for text files

**Question 72:** What is lazy evaluation?
- A) Postponing computation until results needed
- B) Running computations slowly
- C) Loading data lazily
- D) Using lazy variables

**Question 73:** How can you reduce memory usage?
- A) Use appropriate data types (int vs long)
- B) Remove unused columns early
- C) Use categorical types for low-cardinality strings
- D) All of the above

**Question 74:** What is the best practice for data transformations?
- A) Make changes to original data
- B) Chain transformations, creating new DataFrames
- C) Delete old DataFrames immediately
- D) Never log transformations

**Question 75:** When should you validate data after transformation?
- A) Never, trust the code
- B) After each transformation step
- C) Only at the end
- D) Only for complex transformations

**Question 76:** What does data lineage track?
- A) Origin of data
- B) Transformations applied
- C) Who touched the data
- D) All of the above

**Question 77:** Why is it important to inspect data before transforming?
- A) To see the data
- B) To understand data types, ranges, and quality issues
- C) To impress stakeholders
- D) It's not important

**Question 78:** What is the correct order of data cleaning?
- A) Transform, then validate, then clean
- B) Inspect, validate, handle missing, remove duplicates, transform
- C) Clean everything at once
- D) Only clean what's visible

**Question 79:** How do you handle conflicting transformations?
- A) Apply all and see what sticks
- B) Document and choose the correct one
- C) Ignore conflicts
- D) Delete conflicting data

**Question 80:** What is a data quality report?
- A) Report on data scientists
- B) Summary of missing values, duplicates, data types, and anomalies
- C) Quality of code
- D) Report on database

---

## Section 9: Date/Time Operations (81-90)

**Question 81:** How do you extract the year from a date column?
- A) `df.col("date").year()`
- B) `df.col("date").getYear()`
- C) `df.extract("date", "year")`
- D) Both A and B

**Question 82:** What does `diff(date1, date2, DateTimeUnit.DAYS)` return?
- A) Sum of dates
- B) Number of days between two dates
- C) Average of dates
- D) Difference in years

**Question 83:** How do you parse "2024-01-15" as a date?
- A) `LocalDate.parse("2024-01-15")`
- B) `Date.parse("yyyy-MM-dd", "2024-01-15")`
- C) `df.col("date").asDateIn("yyyy-MM-dd")`
- D) All of the above

**Question 84:** How do you filter rows from January 2024?
- A) `df.where(df.col("date").month() == 1)`
- B) `df.where(df.col("date").year() == 2024)`
- C) Both A and B
- D) Cannot filter by month and year

**Question 85:** What does `dayOfWeek()` return?
- A) Day number (1-7)
- B) Day name
- C) Day of month
- D) Business day indicator

**Question 86:** How do you calculate age from birth_date?
- A) `birth_date.age()`
- B) `(today - birth_date) / 365`
- C) Both A and B
- D) Cannot calculate in DataFrame

**Question 87:** What is the proper way to handle timezone-aware data?
- A) Store in UTC, convert as needed
- B) Always use local time
- C) Ignore timezones
- D) Store as strings

**Question 88:** How do you generate date ranges in DataFrames?
- A) `dateRange(start, end, interval)`
- B) `generateDates(start, end, "1D")`
- C) `df.dates(start, end, period)`
- D) All of the above

**Question 89:** What does `truncate(date, "MONTH")` do?
- A) Removes time portion
- B) Sets day to 1
- C) Both A and B
- D) Adds months

**Question 90:** How do you handle missing dates in time series?
- A) Leave gaps
- B) Forward fill
- C) Interpolate
- D) All are valid strategies depending on context

---

## Section 10: Real-World Applications (91-100)

**Question 91:** What is ETL?
- A) Extract, Transform, Load
- B) Enter, Type, Leave
- C) Evaluate, Test, Launch
- D) Encode, Transfer, Log

**Question 92:** When cleaning phone numbers, what regex removes all non-digits?
- A) `\\d+`
- B) `\\D+`
- C) `[^0-9]`
- D) Both B and C

**Question 93:** What is a proper approach to customer LTV calculation?
- A) Sum of all transactions
- B) Average transaction * frequency * duration
- C) Single transaction value
- D) Customer age

**Question 94:** What does anomaly detection typically use as threshold?
- A) z-score > 3 or |z| > 2
- B) Always z-score > 1
- C) Any deviation
- D) Fixed number

**Question 95:** When imputing time series missing values, what is interpolation?
- A) Using mean of all values
- B) Estimating between known values
- C) Using previous value
- D) Using next value

**Question 96:** What is the purpose of a rolling window join?
- A) Join within time window
- B) Join with rolling codes
- C) Rolling update of join
- D) Speed up joins

**Question 97:** How do you handle schema changes in data files?
- A) Refuse to load
- B) Adapt code to handle new columns gracefully
- C) Delete new columns
- D) Stop processing

**Question 98:** What is unit of work in data processing?
- A) Single row
- B) Transaction: all or nothing
- C) Single operation
- D) Chunk of data

**Question 99:** When should you use vectorized operations over loops?
- A) Always
- B) Never
- C) When processing large data, for performance
- D) Only for strings

**Question 100:** What makes a data pipeline production-ready?
- A) Works for one test case
- B) Logging, error handling, validation, documentation
- C) Fast execution
- D) Short code

---

## Answer Key

1-B, 2-B, 3-B, 4-C, 5-B, 6-B, 7-D, 8-B, 9-B, 10-B,
11-B, 12-D, 13-B, 14-B, 15-D, 16-B, 17-D, 18-C, 19-B, 20-C,
21-B, 22-D, 23-D, 24-B, 25-A, 26-D, 27-C, 28-B, 29-D, 30-B,
31-B, 32-B, 33-B, 34-A, 35-B, 36-A, 37-D, 38-B, 39-D, 40-D,
41-B, 42-C, 43-D, 44-A, 45-C, 46-D, 47-B, 48-D, 49-D, 50-A,
51-D, 52-B, 53-D, 54-A, 55-B, 56-B, 57-B, 58-A, 59-C, 60-A,
61-B, 62-B, 63-B, 64-B, 65-D, 66-A, 67-B, 68-D, 69-B, 70-A,
71-B, 72-A, 73-D, 74-B, 75-B, 76-D, 77-B, 78-B, 79-B, 80-B,
81-D, 82-B, 83-D, 84-C, 85-A, 86-C, 87-A, 88-D, 89-C, 90-D,
91-A, 92-D, 93-B, 94-A, 95-B, 96-A, 97-B, 98-B, 99-C, 100-B
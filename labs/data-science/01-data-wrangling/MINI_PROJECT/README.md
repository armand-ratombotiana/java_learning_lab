# Mini Project: Employee Data Analysis Pipeline

## Objective
Build a complete data wrangling pipeline to clean, transform, and analyze employee data.

## Scenario
You're given raw employee data from multiple sources with various quality issues. Process this data to prepare it for HR analytics.

## Dataset
Create sample employee data with the following issues:
- Missing values in salary and department
- Duplicate employee records
- Inconsistent date formats
- Messy phone numbers
- Mixed case names
- Outlier salaries

## Files
- `employees_raw.csv`: Raw employee data
- `departments.csv`: Department reference data
- `salary_bands.csv`: Salary band information

## Tasks

### Task 1: Load and Inspect
- Load the raw CSV files
- Print shape, column names, data types
- Generate data quality report
- Identify all data quality issues

### Task 2: Clean Employee Data
- Remove duplicate employee records (by employee_id)
- Parse and standardize dates (multiple formats)
- Clean phone numbers (extract 10-digit numbers)
- Standardize names (proper case)
- Handle missing salaries (impute with median by department)
- Handle missing departments (use last known assignment)

### Task 3: Join and Enrich
- Join employees with department reference
- Add department statistics (count, avg salary)
- Map employees to salary bands
- Calculate tenure (years since hire)

### Task 4: Transform and Engineer Features
- Normalize salary to z-scores
- Create salary_percentile column
- Create is_high_performer flag (salary > 90th percentile)
- Create tenure_category (new: <2yr, mid: 2-5yr, senior: 5-10yr, veteran: >10yr)
- Extract year, month, quarter from hire date

### Task 5: Aggregate and Summarize
- Average salary by department
- Headcount by department and tenure category
- Salary trends by hire year
- Gender distribution by department
- Department size vs average salary correlation

### Task 6: Export Results
- Save cleaned employee data
- Save department summary
- Save high performers list
- Generate final data quality report

## Expected Output
```
=== EMPLOYEE DATA ANALYSIS REPORT ===

Data Quality Summary:
- Original records: 150
- After deduplication: 145
- Missing values handled: 23
- Outliers identified: 3

Department Summary:
| Department    | Count | Avg Salary | Avg Tenure |
|---------------|-------|------------|------------|
| Engineering   | 45    | $95,000    | 4.2 years  |
| Sales         | 38    | $72,000    | 3.8 years  |
| ...           | ...   | ...        | ...        |

High Performers: 14 employees (10% of workforce)
```

## Bonus Challenges
1. Handle cross-references (manager who is also an employee)
2. Validate all business rules (salary within band, valid dates, etc.)
3. Create an anomaly report for statistical outliers
4. Build a reusable transformation pipeline class
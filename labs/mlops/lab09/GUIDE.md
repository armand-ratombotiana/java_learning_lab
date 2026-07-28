# Lab 09: Data Validation & Quality — Guide

## Step 1: Understand Data Validation Concepts

Great Expectations concepts mapped to Java:
- **Expectation**: A verifiable statement about data (e.g., "column X has no nulls")
- **Suite**: A collection of expectations
- **Validation Result**: Pass/fail for each expectation with details
- **Data Docs**: Human-readable validation reports

## Step 2: Implement DataValidator

The `DataValidator` supports:
- Column existence checks
- Null ratio thresholds
- Value range bounds (min/max)
- Distribution comparisons (KS test approximation)
- Uniqueness constraints

## Step 3: Compile and Run

```bash
cd lab09/src
javac com/mlops/lab09/*.java
java com.mlops.lab09.DataValidationLab
```

## Key Expectations

| Expectation | Description | Threshold |
|------------|-------------|-----------|
| expect_column_to_exist | Column present in dataset | — |
| expect_column_values_to_not_be_null | Null ratio < limit | < 5% |
| expect_column_values_to_be_between | Values in [min, max] range | per column |
| expect_column_values_to_be_unique | No duplicate values | — |
| expect_column_distinct_values_to_be_in_set | Valid categories | per column |

## Best Practices
- Run validation before every training run
- Fail the pipeline if critical expectations fail
- Alert on warning-level expectation failures
- Track validation results over time (trend analysis)
- Version expectation suites alongside code

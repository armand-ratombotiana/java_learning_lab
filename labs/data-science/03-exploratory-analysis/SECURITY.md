# Security in Exploratory Data Analysis

## 1. Sensitive Data Exposure in Profiles

EDA reports often include sample rows, min/max values, and unique value lists — these can leak PII.

```java
// Unsafe: profile outputs include raw samples
System.out.println(data.first(10));  // contains emails, names, SSNs

// Safe: filter out PII columns
Table safeData = data.dropColumns("email", "ssn", "phone");
System.out.println(safeData.first(10));
```

## 2. Aggregation Attacks

Min/max values on small groups can reveal individual data points. If a department has 2 employees and the EDA report shows salary min=$120K, max=$150K, each person's salary is known.

**Fix**: Suppress statistics for groups with < 5 members.

```java
if (groupSize < 5) {
    System.out.println("Group suppressed (n < 5)");
}
```

## 3. Adversarial Data Poisoning

If the dataset is public or user-contributed, EDA profiles can be manipulated by poisoning inputs to create misleading patterns.

**Fix**: Validate data provenance. Compare current EDA profile against historical baselines.

## 4. Report Distribution

EDA reports saved as HTML or PDF may be distributed to users without access control.

**Fix**: Store reports in access-controlled directories. Include a data sensitivity header in the report:

```java
report.addMetadata("classification", "confidential");
report.addMetadata("allowed_roles", "data_science, legal");
```

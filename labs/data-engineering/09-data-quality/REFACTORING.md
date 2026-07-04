# Refactoring Data Quality

## Before: Ad-hoc Checks
```java
if (data.filter(col("id").isNull()).count() > 100) { alert(); }
if (!data.schema().fieldNames().contains("email")) { alert(); }
```

## After: Unified Framework
```java
QualityReport report = framework.validate(data, "customers");
if (!report.isPassed()) { alert(report); }
```

## Before: Hardcoded Rules
## After: Declarative Configuration
```yaml
customers:
  rules:
    - type: NOT_NULL
      columns: [customer_id, email]
    - type: UNIQUE
      columns: [customer_id]
```

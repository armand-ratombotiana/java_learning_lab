# Refactoring Governance

## Before: No Governance
```java
// Anyone can access any data
spark.sql("SELECT * FROM customers").show();
```

## After: Policy Enforced
```java
spark.sql("SELECT * FROM governed.customers").show();
// Automatically filtered by policies
```

## Before: Manual Documentation
README.txt out of date within days.

## After: Automated Catalog
```java
catalog.getAsset("fact_orders");
// Returns: schema, owner, domain, lineage, quality score
```

## Before: No Lineage (2-hour investigation)
## After: Automated Lineage
```java
lineageService.getLineage("fact_orders");
// Returns: CRM -> ETL -> fact_orders -> BI Dashboard
```

# Debugging

## High Null Rate
Check for schema drift in source.

## Duplicates After Restart
Check batch_id distribution.

## Referential Failures
```java
data.join(dimTable, "key", "left_anti").groupBy("source").count().show();
```

## Freshness Issues
```java
data.agg(max("updated_at")).show();
```

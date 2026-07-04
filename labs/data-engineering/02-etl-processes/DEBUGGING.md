# Debugging

## Type Mismatches
```java
sourceData.printSchema();
spark.conf().set("spark.sql.ansi.enabled", "false");
```

## Null Analysis
```java
long nullCount = sourceData.filter(col("key").isNull()).count();
```

## Connection Testing
```java
try { jdbcTemplate.queryForObject("SELECT 1", Integer.class); }
catch (Exception e) { log.error("DB unavailable"); }

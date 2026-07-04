# Security

## Authentication
```java
spark.conf().set("spark.authenticate", "true");
spark.conf().set("spark.network.crypto.enabled", "true");
```

## SQL Redaction
```java
spark.conf().set("spark.sql.redaction.string.regex", "(?i)(password|secret)");
```

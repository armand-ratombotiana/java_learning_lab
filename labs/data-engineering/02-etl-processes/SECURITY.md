# Security

## Column Encryption
```java
public Dataset<Row> encryptPII(Dataset<Row> data) {
    return data.withColumn("ssn", encryptColumn(col("ssn"), key));
}
```

## Access Control
```java
@PreAuthorize("hasRole('ETL_ADMIN')")
public void runSensitiveEtl() {}
```

## Audit Logging
Log all ETL executions with user, job, records, status, timestamp.

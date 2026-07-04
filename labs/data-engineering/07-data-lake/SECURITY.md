# Security

## Bucket Policies
IAM policies for S3 bucket access control per layer.

## Column Masking
```java
spark.sql("CREATE VIEW safe_employees AS " +
    "SELECT id, name, CONCAT(LEFT(ssn,4),'***-****') as ssn " +
    "FROM employees");
```

## Encryption
Server-side encryption (SSE-S3/KMS) for data at rest.

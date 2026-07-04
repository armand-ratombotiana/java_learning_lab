# Security in Data Wrangling

## 1. PII Leakage in Logs

Wrangling pipelines often log rows for debugging. Logging full rows containing PII (email, SSN, name) is a data breach waiting to happen.

```java
// Unsafe: logs entire row
logger.info("Row {}: {}", i, row);

// Safe: log only non-sensitive columns or hashed identifiers
logger.info("Row {}: age={}, region={}", i, row.getDouble("age"), row.getString("region"));
```

## 2. CSV Injection

When writing CSV files that may be opened in Excel, values starting with `=`, `+`, `-`, or `@` execute formulas.

```java
// Sanitize cells to prevent formula execution
public String sanitizeCell(String value) {
    if (value != null && (value.startsWith("=") || value.startsWith("+")
            || value.startsWith("-") || value.startsWith("@"))) {
        return "'" + value;  // Excel treats ' as literal prefix
    }
    return value;
}
```

## 3. SQL Injection in JDBC Wrangles

Building SQL for JDBC-backed DataFrames by concatenating user input opens SQL injection vectors.

```java
// Unsafe
Table t = DataFrame.read().jdbc(url, "SELECT * FROM users WHERE id = " + userId);

// Safe: use parameterized queries
Table t = DataFrame.read().jdbc(url, 
    "SELECT * FROM users WHERE id = ?", 
    userId);
```

## 4. File Path Traversal

Accepting filenames from user input without validation allows reading arbitrary files.

```java
// Validate paths
Path base = Paths.get("/data/input/");
Path requested = base.resolve(inputFileName).normalize();
if (!requested.startsWith(base)) {
    throw new SecurityException("Path traversal detected");
}
```

## 5. Data Integrity Checksums

For compliance, verify that wrangled data matches source row counts and hash values.

```java
// After wrangling, assert counts
assert clean.rowCount() == raw.rowCount() - expectedRemovals : "Unexpected row loss";
```

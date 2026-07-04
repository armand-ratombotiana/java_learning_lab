# Security

## Credentials
Use environment variables or Vault for DB passwords.

## SSL
```properties
database.ssl.mode=verify_full
```

## Data Masking
```java
.with("column.mask.with.2.chars.hash.salt", "table:sensitive_col")
```

## Network
CDC user should have minimal permissions (SELECT, REPLICATION SLAVE).

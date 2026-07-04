# Security

## Avoid PII in Alerts
```java
long invalidCount = data.filter(...).count();
// Report count only, not actual values
```

## Access Control
@PreAuthorize for role-based access to quality reports.

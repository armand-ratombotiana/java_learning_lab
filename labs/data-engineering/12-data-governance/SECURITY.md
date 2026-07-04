# Security

## RBAC
```java
@PreAuthorize("hasRole('GOVERNANCE_ADMIN')")
public void createPolicy(Policy p) { }
```

## Data Masking
Mask PII fields in query results based on user role.

## Audit Logging
```java
@Audited
public DataAsset registerAsset(DataAsset asset) { ... }
```

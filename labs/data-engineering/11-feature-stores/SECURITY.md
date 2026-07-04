# Security

## Access Control
```java
@PreAuthorize("hasPermission('feature', 'read')")
public Map<String, Object> getFeatures(...) { ... }
```

## PII
Flag sensitive features with @FeatureMetadata(sensitive = true).

## Audit
Log all feature access for compliance.

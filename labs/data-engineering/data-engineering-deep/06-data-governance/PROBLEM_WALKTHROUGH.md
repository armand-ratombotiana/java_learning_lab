# Lab 06: Problem Walkthrough — Policy Enforcement Proxy

## Problem

Build a `PolicyEnforcementProxy` that intercepts data access requests, evaluates governance policies, logs the decision, and applies data masking for restricted columns.

## Walkthrough

### Step 1: Request Model

```java
public record AccessRequest(User user, String datasetId, String queryType, List<String> columns,
                            String purpose, String ipAddress) {}
```

### Step 2: Policy Evaluation

```java
public class PolicyEvaluator {
    public boolean evaluate(AccessRequest request) {
        // Level 1: Is user active?
        if (!request.user().active()) return false;
        // Level 2: Does user have dataset access?
        if (!hasDatasetAccess(request.user(), request.datasetId())) return false;
        // Level 3: Column-level check (PII, restricted)
        for (String col : request.columns()) {
            Classification c = classifier.classify(col);
            if (c == Classification.RESTRICTED && !request.user().roles().contains("admin")) return false;
        }
        // Level 4: Purpose check
        if (!isPurposeAllowed(request.purpose())) return false;
        return true;
    }
}
```

### Step 3: Data Masking

```java
public class DataMasker {
    public String mask(String value, Classification classification) {
        return switch (classification) {
            case PII -> value.length() <= 4 ? "****" : value.substring(0, 2) + "****" + value.substring(value.length() - 2);
            case CONFIDENTIAL -> "***REDACTED***";
            default -> value;
        };
    }
}
```

### Step 4: Proxy

```java
public class PolicyEnforcementProxy {
    private final PolicyEvaluator evaluator;
    private final DataMasker masker;
    private final AuditLogger logger;

    public QueryResult execute(AccessRequest request, QueryResult rawResult) {
        boolean granted = evaluator.evaluate(request);
        logger.log(new AuditEvent(request.user().id(), request.datasetId(),
            Action.READ, Instant.now(), request.ipAddress(), granted));
        if (!granted) throw new AccessDeniedException("Access denied for " + request.user().id());
        // Apply masking based on classification
        var maskedColumns = rawResult.columns().stream()
            .map(col -> new Column(col.name(), masker.mask(col.value(),
                classifier.classify(col.name()))))
            .toList();
        return new QueryResult(maskedColumns);
    }
}
```

## Complexity

- **Time**: O(C + P) where C = columns, P = policies
- **Space**: O(1) for single-request evaluation

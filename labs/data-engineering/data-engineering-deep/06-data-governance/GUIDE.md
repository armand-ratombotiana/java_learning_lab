# Lab 06: Data Governance — Implementation Guide

## Step 1: User and Role Model

```java
public record User(String id, String name, List<String> roles) {}
public record Role(String name, List<Permission> permissions) {}
public record Permission(ResourceType resource, Action action, Effect effect) {
    public enum ResourceType { DATASET, COLUMN, TAG, CATALOG }
    public enum Action { READ, WRITE, DELETE, GRANT }
    public enum Effect { ALLOW, DENY }
}
```

## Step 2: Policy Engine

```java
public class PolicyEngine {
    private final List<Permission> globalPolicies = new ArrayList<>();

    public void addPolicy(Permission permission) { globalPolicies.add(permission); }

    public boolean evaluate(User user, ResourceType resource, Action action) {
        for (Permission p : globalPolicies) {
            if (p.resource() == resource && p.action() == action) {
                if (p.effect() == Effect.DENY) return false;
            }
        }
        return user.roles().stream().anyMatch(role -> hasPermission(role, resource, action));
    }
}
```

## Step 3: Audit Logger

```java
public record AuditEvent(String userId, String resourceId, Action action, Instant timestamp,
                         String ipAddress, boolean granted) {}

public class AuditLogger {
    private final List<AuditEvent> events = new CopyOnWriteArrayList<>();

    public void log(AuditEvent event) { events.add(event); }

    public List<AuditEvent> queryByUser(String userId, Instant from, Instant to) {
        return events.stream()
            .filter(e -> e.userId().equals(userId)
                && e.timestamp().isAfter(from)
                && e.timestamp().isBefore(to))
            .toList();
    }
}
```

## Step 4: Data Classifier

```java
public enum Classification { PUBLIC, INTERNAL, CONFIDENTIAL, PII, RESTRICTED }

public class DataClassifier {
    private final Map<Pattern, Classification> rules = new LinkedHashMap<>();

    // e.g., match column names like "email", "ssn", "phone"
    public void addRule(Pattern pattern, Classification classification) {
        rules.put(pattern, classification);
    }

    public Classification classify(String columnName) {
        return rules.entrySet().stream()
            .filter(e -> e.getKey().matcher(columnName).matches())
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(Classification.PUBLIC);
    }
}
```

## Step 5: Access Manager

```java
public class AccessManager {
    private final PolicyEngine policyEngine;
    private final AuditLogger auditLogger;
    private final DataClassifier classifier;

    public boolean checkAccess(User user, String datasetId, String column, Action action, String ip) {
        Classification classification = classifier.classify(column);
        boolean granted = policyEngine.evaluate(user, ResourceType.COLUMN, action)
            && classification.ordinal() <= maxAllowedRole(user);
        auditLogger.log(new AuditEvent(user.id(), datasetId + "." + column, action, Instant.now(), ip, granted));
        return granted;
    }
}
```

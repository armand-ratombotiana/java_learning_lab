# Refactoring CDC

## Before: Timestamp-Based Polling
```java
SELECT * FROM orders WHERE updated_at > ?;
// Misses deletes, high latency
```

## After: Log-Based CDC
```java
DebeziumEngine.create(Json.class)
    .using(config)
    .notifying(this::process)
    .build();
// Captures all changes, zero source impact
```

## Before: Raw Table CDC
Noisy, captures all internal changes.

## After: Outbox Pattern
```java
// Application writes to outbox table with explicit events
// Clean, intentional event publication
```

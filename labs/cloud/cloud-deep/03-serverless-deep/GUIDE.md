# GUIDE — Serverless Deep

## Step 1: Function Lifecycle
```java
public enum FunctionPhase { INIT, INVOKE, SHUTDOWN }
public record InvocationContext(String requestId, long deadlineMs, FunctionPhase phase) {}
```

## Step 2: Cold Start Simulator
- Track init duration vs invoke duration
- Model snapstart (pre-init) vs standard cold start
- Calculate P50/P95/P99 cold start latencies

## Step 3: Provisioned Concurrency Manager
```java
ProvisionedConcurrencyManager pcm = new ProvisionedConcurrencyManager(minCapacity, maxCapacity);
pcm.scaleTo(50); // pre-warm 50 environments
```

## Step 4: Event Filtering Engine
- Filter SQS messages by message attributes
- Filter S3 events by object key patterns
- Filter Kinesis records by partition key ranges

## Step 5: Lambda Extension Architecture
- Register extension via Extensions API
- Subscribe to Telemetry API for logs and metrics
- Implement shutdown hook for graceful cleanup

## Step 6: Exercises
1. Implement a snapshot/restore lifecycle for faster cold starts
2. Build a custom extension that collects GC metrics
3. Create an event filtering pipeline for a multi-tenant SaaS

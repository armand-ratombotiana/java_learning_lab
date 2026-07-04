# Pipeline Architecture Exercises

## Beginner Exercises

### Exercise 1: Simple String Pipeline
Create a pipeline with stages: Trim -> Uppercase -> Replace spaces with underscores.

### Exercise 2: Filter Pipeline
Create a number processing pipeline: Filter even numbers -> Double the value -> Convert to string.

## Intermediate Exercises

### Exercise 3: ETL Pipeline
Build an ETL pipeline:
- Read data from CSV
- Validate fields
- Transform dates and currencies
- Enrich with external data
- Persist to database

### Exercise 4: Error Handling Pipeline
Add error handling to the ETL pipeline:
- Per-stage error handlers
- Retry logic for transient failures
- Dead letter queue for failed records
- Continue processing on non-fatal errors

### Exercise 5: Branching Pipeline
Create a pipeline that branches based on record type:
```java
if (record.type() == "A") { processA(); }
else if (record.type() == "B") { processB(); }
else { processDefault(); }
```

## Advanced Exercises

### Exercise 6: Parallel Pipeline
Create a pipeline that processes records in parallel:
- Split input into chunks
- Process each chunk in parallel
- Aggregate results
- Handle partial failures

### Exercise 7: Reactive Pipeline
Build a reactive pipeline using Project Reactor:
- Non-blocking stages
- Backpressure handling
- Error recovery with fallback
- Metrics per stage

### Exercise 8: Configurable Pipeline
Create a pipeline that can be configured at runtime:
- YAML/JSON pipeline definition
- Stage ordering via configuration
- Conditional stage inclusion
- Dynamic stage parameters

# Performance: EBS Setup and Configuration

## 1. Bottleneck Analysis

### 1.1 Database Queries
- Proper indexing on FND tables
- Bind variables to avoid hard parsing
- Monitor V

### 1.2 Concurrent Processing

- Tune concurrent manager sleep seconds
- Use multiple workers for volume
- Set appropriate request priority

### 1.3 Forms/OAF Response Time

- Enable APPLCSF JAR caching
- Tune BC4J connection pooling
- Enable network compression

## 2. SQL Profiling

SELECT request_id, actual_start_date, actual_completion_date FROM fnd_concurrent_requests;

## 3. Caching Strategies

- Profile cache (FND_PROFILE_OPTIONS_VALUES)
- Flexfield cache (FND_FLEX_VALUES)
- Data dictionary cache

## 4. Memory Tuning

- Forms Server sessions per process
- OA Framework heap settings
- JVM for OACore

## 5. Summary

Regular AWR reports, ADDM analysis, and SQL Tuning Advisor are essential for EBS performance.

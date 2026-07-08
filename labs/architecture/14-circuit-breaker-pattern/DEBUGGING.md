# Debugging: Circuit Breaker Pattern

## 1. Common Debugging Scenarios

### 1.1 Connection Issues
- Symptom: Timeouts connecting to downstream services
- Check: Network policies, DNS resolution, firewall rules
- Fix: Verify connectivity, check service discovery, validate endpoints

### 1.2 Configuration Problems
- Symptom: Unexpected behavior after configuration change
- Check: Config file syntax, environment variable override
- Fix: Validate configuration schema, enable config logging

### 1.3 State Inconsistency
- Symptom: Data mismatches between services
- Check: Event ordering, idempotency handling
- Fix: Audit log review, replay events, reconcile state

## 2. Debugging Tools

### 2.1 Logging
- Structured logging with correlation IDs
- Log levels: ERROR for failures, WARN for issues, INFO for state changes
- Log aggregation with ELK stack or Grafana Loki

### 2.2 Distributed Tracing
- Trace propagation across service boundaries
- Span annotations for key operations
- Trace sampling for production performance

### 2.3 Metrics
- Rate, errors, duration (RED) metrics
- USE (utilization, saturation, errors) for resources
- Custom business metrics for domain-level visibility

## 3. Step-by-Step Debugging

### 3.1 Reproduce the Issue
- Identify the exact conditions that trigger the problem
- Create a minimal reproducible example
- Isolate the failing component

### 3.2 Gather Information
- Review logs around the failure time
- Check metrics for anomalies
- Trace the request through distributed systems

### 3.3 Identify Root Cause
- Formulate hypotheses based on gathered data
- Test each hypothesis with targeted experiments
- Confirm root cause with a fix in development

## 4. Production Debugging

### 4.1 Safe Debugging Practices
- Use read-only access when possible
- Enable debug logging for specific services only
- Never modify production data during debugging

### 4.2 Debug Endpoints
- Health check endpoints for basic connectivity
- Metrics endpoints for performance data
- Thread dump endpoints for concurrency analysis

## 5. Common Pitfalls

### 5.1 Timeouts
- Configure appropriate timeouts per service
- Use circuit breakers for failing dependencies
- Implement retry with exponential backoff

### 5.2 Resource Leaks
- Monitor connection pool utilization
- Track thread pool growth
- Profile memory usage for leaks

# Debugging - Azure Fundamentals

## Common Scenarios

### 1. Configuration Issues
**Symptoms**: Service fails to start, wrong behavior
**Root Cause**: Missing environment variables, config format errors

**Debugging**:
`ash
# Print all config at startup
java -Ddebug.config=true -jar service.jar
# Validate config file
python -c "import yaml; yaml.safe_load(open('config.yml'))"
`

### 2. Connection Failures
**Symptoms**: Timeouts, connection refused
**Root Cause**: Network policies, wrong endpoints, expired certs

**Debugging**:
`java
public void diagnoseConnection(String endpoint) {
    try {
        InetAddress addr = InetAddress.getByName(new URI(endpoint).getHost());
        System.out.println("Resolved: " + addr.getHostAddress());
        System.out.println("Reachable: " + addr.isReachable(5000));
    } catch (Exception e) {
        System.err.println("DNS failed: " + e.getMessage());
    }
}
`

### 3. Memory Leaks
**Symptoms**: OOM, increasing heap, GC pressure
**Root Cause**: Unbounded collections, uncleared references

**Debugging**:
`ash
# Capture heap dump
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/dumps
# Analyze with Eclipse MAT
`

### 4. Thread Deadlocks
**Symptoms**: Application hang, BLOCKED threads
**Root Cause**: Improper lock ordering

**Debugging**:
`ash
jstack <pid> > threaddump.txt
# Look for BLOCKED threads with common monitors
`

## Logging Best Practices
`java
private static final Logger log = LoggerFactory.getLogger(Service.class);
log.info("Processing: id={}, user={}", id, user);
log.error("Failed: id={}", id, exception);
`
"@
}

function GetCommonMistakes {
    param(Azure Fundamentals, Azure cloud services including VMs, Blob Storage, Azure SQL, AKS, and managed identities)
    return @"
## 1. Over-Engineering
Building complex abstractions before needed. Start simple, refactor when patterns emerge.

## 2. Ignoring Error Handling
Catching generic Exception instead of specific types. Always log context.

## 3. Tight Coupling
Depending on concrete classes instead of interfaces. Use dependency injection.

## 4. Missing Config Externalization
Hardcoding endpoints and credentials. Use environment variables and config files.

## 5. Inadequate Testing
Only testing the happy path. Test both success and failure scenarios.

## 6. Resource Leaks
Not closing connections or streams. Use try-with-resources.

## 7. Premature Optimization
Optimizing before measuring. Profile first, optimize hot spots.

## 8. Lack of Observability
No metrics, logging, or tracing. Add structured logging and metrics endpoints.

## 9. Ignoring Security
Skipping input validation, using weak auth. Security-first mindset.

## 10. Poor Configuration Management
Mixing config with code. Externalize all configuration.

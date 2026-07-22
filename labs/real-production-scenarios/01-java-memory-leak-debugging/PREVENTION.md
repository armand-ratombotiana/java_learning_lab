# Prevention: Avoiding ClassLoader and Metaspace Leaks

**Incident**: INC-2024-0415-ZUUL-OOM
**Category**: JVM Memory Management
**Applies To**: All Java services using thread pools and ThreadLocal

## Prevention Strategies

### 1. Code Review: ThreadLocal Cleanup Check

Every static ThreadLocal field must be accompanied by a `remove()` call in a `finally` block. The following pattern is mandatory for any request-scoped or task-scoped ThreadLocal usage in thread-pooled environments:

```java
// REQUIRED PATTERN for all static ThreadLocal fields
private static final ThreadLocal<MyContext> contextHolder = new ThreadLocal<>();

public void process() {
    try {
        contextHolder.set(new MyContext());
        // ... business logic ...
    } finally {
        // MANDATORY: Always remove to prevent leaks
        MyContext ctx = contextHolder.get();
        if (ctx != null) {
            ctx.close();  // Clean up internal references
        }
        contextHolder.remove();
    }
}
```

### 2. Automated Enforcement: Checkstyle / ErrorProne Rule

Add a custom Checkstyle or ErrorProne check that flags any `ThreadLocal.set()` call that is not paired with a `ThreadLocal.remove()` in a `finally` block.

#### ErrorProne Check (Conceptual)

```java
// ErrorProne BugPattern to detect missing ThreadLocal.remove()
@BugPattern(
    name = "ThreadLocalMustCleanup",
    summary = "ThreadLocal values must be removed in finally block",
    severity = WARNING,
    category = JDK
)
public class ThreadLocalCleanupChecker extends BugChecker
        implements MethodInvocationTreeMatcher {

    @Override
    public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        if (isThreadLocalSetCall(tree)) {
            if (!isInsideFinallyBlockWithRemoveCall(tree)) {
                return describeMatch(tree,
                    "ThreadLocal.set() must be paired with remove() in a finally block");
            }
        }
        return Description.NO_MATCH;
    }
}
```

### 3. Architecture: Prefer Scoped Values (Java 20+)

For Java 20+, use `ScopedValue` instead of `ThreadLocal` for request-scoped context propagation. ScopedValues are automatically cleaned up when the scope exits, eliminating the need for manual removal:

```java
// Java 20+ ScopedValue — no manual cleanup needed
public class RequestContext {
    private static final ScopedValue<SecurityContext> SECURITY_CONTEXT =
            ScopedValue.newInstance();

    public static SecurityContext get() {
        return SECURITY_CONTEXT.get();
    }

    public static void runWithContext(SecurityContext ctx, Runnable action) {
        ScopedValue.where(SECURITY_CONTEXT, ctx, action);
        // SECURITY_CONTEXT is automatically cleaned up when the callWhere scope exits
    }
}
```

### 4. Framework: WeakThreadLocal Adoption

Mandate the use of `WeakThreadLocal` (or similar wrapper) for all request-scoped ThreadLocal usage. This provides a defensive layer against missed cleanup:

```java
// Standard: Use WeakThreadLocal instead of raw ThreadLocal
// for any data that is scoped to a request or task
private static final WeakThreadLocal<SecurityContext> ctx =
        new WeakThreadLocal<>();
```

### 5. Monitoring: Metaspace Alerts

Implement proactive alerting on Metaspace usage, not just heap usage:

| Metric | Warning Threshold | Critical Threshold | Action |
|--------|-------------------|--------------------|--------|
| Metaspace Used % | > 60% of MaxMetaspaceSize | > 80% | Alert, investigate class loading |
| Metaspace Growth Rate | > 1 MB/min for 10 min | > 5 MB/min | Pager, potential ClassLoader leak |
| ClassLoader Count | > 100 active loaders | > 500 | Dump ClassLoader stats, identify leak |
| Class Count Growth | > 1000 classes/min | > 5000 classes/min | Likely dynamic class generation leak |
| GC Time (Metaspace) | > 5% Metaspace GC time | > 15% | Investigate class unloading failure |

### 6. JVM Configuration: Safe Defaults

| Flag | Value | Rationale |
|------|-------|-----------|
| `-XX:MaxMetaspaceSize` | 256m | Cap Metaspace to prevent OOM from filling container memory |
| `-XX:MetaspaceSize` | 64m | Initial Metaspace size to avoid early resizing |
| `-XX:MaxMetaspaceFreeRatio` | 70 | Trigger GC when Metaspace is 70% free after class unloading |
| `-XX:MinMetaspaceFreeRatio` | 40 | Avoid shrinking Metaspace too aggressively |
| `-XX:+PrintGCDetails` | true | Log GC events including Metaspace |
| `-XX:+PrintGCDateStamps` | true | Timestamp GC events for correlation |
| `-XX:+HeapDumpOnOutOfMemoryError` | true | Capture heap dump for post-mortem |
| `-XX:HeapDumpPath` | /data/dumps | Ensure directory exists and has space |

### 7. Load Testing Requirements

All services using ThreadLocal with thread pools must run load tests for a minimum duration:

| ThreadLocal Usage Pattern | Minimum Test Duration | Verification |
|---------------------------|-----------------------|--------------|
| Static ThreadLocal with set/get | 12+ hours at peak TPS | Metaspace growth < 1 MB/hour |
| InheritableThreadLocal | 24+ hours | Verify child thread cleanup |
| Dynamic ClassLoader creation | 24+ hours | ClassLoader count stable |
| Custom ClassLoader per request | 48+ hours | No ClassLoader count increase |

### 8. Runtime Detection: ClassLoader Leak Detection Tool

Use JDK's built-in class unloading detection:

```bash
# Watch class loader count over time
watch -n 60 'jcmd <pid> VM.classloader_stats | grep -E "ClassLoader instances|Active loaders"'

# Enable class unloading logging
-XX:+TraceClassUnloading
-XX:+TraceClassLoading

# Or use jcmd for on-demand class loading tracing
jcmd <pid> VM.class_load_stats
```

### 9. ThreadLocal Pattern Decision Matrix

| Use Case | ThreadLocal | ScopedValue | InheritableThreadLocal | WeakThreadLocal | Recommendation |
|----------|-------------|-------------|------------------------|-----------------|----------------|
| Request context (web) | ❌ | ✅ | ❌ | ⚠️ Fallback | ScopedValue |
| Transaction context | ❌ | ✅ | ❌ | ⚠️ Fallback | ScopedValue |
| Security principal | ❌ | ✅ | ❌ | ⚠️ Fallback | ScopedValue |
| JDBC connection binding | ✅ | ❌ | ❌ | ✅ | WeakThreadLocal |
| Thread-local cache | ✅ | ❌ | ❌ | ⚠️ | ThreadLocal (static, short-lived) |
| Inherited logging MDC | ❌ | ❌ | ✅ | ❌ | InheritableThreadLocal |
| Per-thread random | ✅ | ❌ | ❌ | ❌ | ThreadLocal (no cleanup needed) |

### 10. Education and Training

All engineers working on JVM-based services must complete:

| Topic | Training | Frequency |
|-------|----------|-----------|
| ThreadLocal internals (ThreadLocalMap, Entry, WeakReference) | CodeLab | Onboarding + annually |
| Metaspace and ClassLoader architecture | Workshop | Onboarding |
| Eclipse MAT heap dump analysis | Hands-on | Quarterly |
| JVM memory leak patterns | Incident review | Per event |
| ScopedValue migration (Java 20+) | Migration guide | When upgrading JDK |

### 11. Dependency Management: Library Audits

Several popular libraries are known to use ThreadLocal internally. Audit these for potential leak paths:

| Library | Known Issue | Mitigation |
|---------|-------------|------------|
| Logback MDC | MDC uses InheritableThreadLocal | Always call MDC.clear() in finally |
| Spring SecurityContextHolder | ThreadLocal-based security context | Use MODE_INHERITABLETHREADLOCAL with cleanup |
| Hibernate Session | ThreadLocal session binding | Ensure sessionFactory.close() in finally |
| Apache CXF | ThreadLocal message context | Call MessageContextUtils.clear() |
| SLF4J MDC | Similar to Logback MDC | MDC.remove() in finally blocks |
| Brave (Zipkin) | Tracing context in ThreadLocal | Ensure span.finish() in finally |
| Micrometer | Observation context | Ensure Scope.close() in finally |

### 12. Deployment Verification Checklist

Before any deployment to production:

- [ ] Run ThreadLocal leak detector: scan for static ThreadLocal fields
- [ ] Verify all ThreadLocal.set() calls have corresponding remove() in finally
- [ ] Check for TCCL (Thread Context ClassLoader) modifications without reset
- [ ] Run 12-hour load test with Metaspace monitoring
- [ ] Verify Metaspace growth rate is < 1MB/hour
- [ ] Verify ClassLoader count is stable (no increase over time)
- [ ] Check new dependencies for ThreadLocal usage patterns

### 13. JDK Version Migration Strategy

| JDK Version | ThreadLocal Behavior | Recommendation |
|-------------|---------------------|----------------|
| JDK 8 | Standard ThreadLocalMap with Entry[] | Use WeakThreadLocal wrapper |
| JDK 11 | Same as JDK 8 | Use WeakThreadLocal wrapper |
| JDK 17 | Same as JDK 8 | Use WeakThreadLocal wrapper |
| JDK 20+ | ScopedValue (JEP 429) | Migrate to ScopedValue for request-scoped data |
| JDK 21+ | ScopedValue preview final | Prefer ScopedValue over ThreadLocal |

### 14. Code Review Integration with GitHub Checks

Create a GitHub Actions workflow that automatically checks for ThreadLocal misuse:

```yaml
# .github/workflows/threadlocal-check.yml
name: ThreadLocal Safety Check
on: [pull_request]
jobs:
  check:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Check ThreadLocal cleanup
        run: |
          # Find Java files with ThreadLocal
          files=$(grep -rl "ThreadLocal<" --include="*.java" .)
          violations=0
          for f in $files; do
            if grep -q "ThreadLocal<" "$f"; then
              # Check for remove() in finally
              if ! grep -qE "(finally|try-with-resources)" "$f" 2>/dev/null; then
                echo "❌ $f: ThreadLocal without cleanup block"
                violations=$((violations+1))
              fi
            fi
          done
          echo "Found $violations potential ThreadLocal violations"
          exit $violations
```

### 15. Regular Metaspace Health Audits

Schedule weekly health audits for all production JVMs:

```bash
#!/bin/bash
# Weekly Metaspace health audit script
for host in $(cat production_hosts.txt); do
    echo "=== $host ==="
    ssh $host "jcmd \$(pgrep -f zuul-gateway) VM.metaspace" 2>/dev/null
    ssh $host "jcmd \$(pgrep -f zuul-gateway) VM.classloader_stats" 2>/dev/null
done > metaspace_audit_$(date +%Y%m%d).txt
```

### 16. Enforcement via Git Hooks

Add a pre-commit hook that checks for ThreadLocal misuse:

```bash
#!/bin/bash
# Pre-commit hook: check ThreadLocal without remove()
FILES=$(git diff --cached --name-only --diff-filter=ACM | grep "\.java$")
if [ -n "$FILES" ]; then
    grep -l "ThreadLocal" $FILES | while read file; do
        if grep -q "ThreadLocal<" "$file"; then
            # Check if there's a remove() call in a finally block
            if ! grep -q "finally.*remove\|remove.*finally" "$file"; then
                echo "WARNING: $file uses ThreadLocal without remove() in finally"
                echo "Consider adding cleanup or suppressing with @SuppressWarnings"
            fi
        fi
    done
fi
```

## References

- Google SRE Book: "Managing Critical State with ThreadLocal" — Google SRE Workbook
- Oracle: "Troubleshooting ClassLoader Leaks" — https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/classloaddleaks.html
- Netflix Tech Blog: "Preventing Memory Leaks in Zuul" — Netflix Engineering
- Spring Framework: "RequestContextHolder — ThreadLocal Cleanup" — Spring Documentation
- Tomcat: "ClassLoader Leak Prevention" — Apache Tomcat 9 Documentation
- IntelliJ IDEA: "ThreadLocal Cleanup Inspection" — IntelliJ Code Inspection Documentation
- ErrorProne: "Custom Bug Patterns for ThreadLocal" — Google ErrorProne
- JEP 429: "Scoped Values" — OpenJDK
- Oracle: "ThreadLocal Best Practices" — Oracle Java Documentation
- IBM: "Diagnosing ClassLoader Leaks in WebSphere" — IBM Support Documentation


# Security Considerations for GC

## Information Leakage from GC Timing
GC pause times can leak information about object allocation patterns and live data sizes. For security-critical applications, this timing side channel could reveal sensitive information about the application's internal state. Use constant-time GC (ZGC) to minimize timing variability.

## GC-Based DoS
An attacker who can trigger excessive allocation can cause GC thrashing. The application spends >90% of time in GC, effectively a denial of service. Mitigations:
- Use bounded queues for incoming requests
- Limit per-request allocation
- Monitor GC overhead and alert
- Consider `-XX:SoftRefLRUPolicyMSPerMB=0` to clear soft references aggressively

## Heap Dump Exposure
`HeapDumpOnOutOfMemoryError` creates a heap dump containing all objects and their data. If the application processes sensitive data (passwords, PII, cryptographic keys), the heap dump can leak this data. Mitigations:
- Encrypt heap dumps
- Store heap dumps in secure locations
- Use `-XX:+HeapDumpAfterFullGC` in controlled environments
- Zero-sensitive data before releasing references

## Finalizer Exploitation
Objects with `finalize()` methods are collected in two passes:
1. GC identifies the object as unreachable
2. Object is enqueued to the finalization queue
3. Finalizer thread runs `finalize()`
4. Object is collected in the next GC cycle

An attacker could exploit finalization to resurrect objects, extending their lifetime indefinitely. Avoid `finalize()` — use `Cleaner` (Java 9+) or `AutoCloseable`.

## ClassLoader Leak via GC
If a ClassLoader retains references to application objects, the ClassLoader and all its classes can't be collected. This causes Metaspace leaks in application servers. Use `-XX:+TraceClassUnloading` and `-XX:+TraceClassLoading` to diagnose.

## GC Stopped-Time Side Channel
The `-XX:+PrintGCApplicationStoppedTime` flag reveals the exact stop time of GC pauses. In security-critical applications, this could be used as a timing oracle. Disable GC logging in production security-sensitive environments.

# Incident Response Runbook: Metaspace / ClassLoader Leak

**Incident Type**: JVM OutOfMemoryError (Metaspace)
**Severity**: P0 (SEV-1)
**Response Time**: < 5 minutes for initial triage

## 1. DETECTION AND TRIAGE

### 1.1 Verify the Alert
- [ ] Confirm alert: "MetaspaceUsageCritical" or "InstanceUnhealthy" in PagerDuty
- [ ] Check CloudWatch / Atlas dashboard: Metaspace utilization %
- [ ] Verify cluster health: how many instances are affected?
- [ ] Check if OOM crash has occurred: `dmesg | grep -i "oom-killer"` on host
- [ ] Determine crash window: correlate with recent deployments (last 6 hours)
- [ ] Is this a repeat incident? Check incident history for similar events

### 1.2 Assess Impact
- [ ] Which regions/clusters are affected?
- [ ] What percentage of traffic is impacted?
- [ ] Are circuit breakers isolating the issue?
- [ ] Has any user-facing impact been detected?
- [ ] Notify on-call incident manager
- [ ] Declare incident severity level (P0/P1)

### 1.3 Initial Mitigation (if actively crashing)
- [ ] Failover traffic to healthy cluster/region
- [ ] If all instances crashing: scale up cluster to reduce per-instance load
- [ ] If single instance: restart instance (ASG will do this automatically)
- [ ] Consider reducing traffic to affected cluster (if circuit breakers not working)
- [ ] Set -XX:MaxMetaspaceSize=256m to cap growth (temporary, will cause faster OOM but limits blast radius)

## 2. DATA COLLECTION

### 2.1 Gather JVM Artifacts (do this immediately, instance may crash soon)
- [ ] Check if heap dump was generated: `ls -la /data/dumps/`
- [ ] Check if JFR recording is available: `ls -la /data/jfr/`
- [ ] Collect GC logs: `cat /var/log/gc-*.log`
- [ ] Collect application logs: `journalctl -u zuul-gateway --since "6 hours ago"`
- [ ] Collect OS-level logs: `dmesg | grep -i "oom\|out of memory"`
- [ ] Collect container metrics: CPU, memory, network from CloudWatch

### 2.2 If JVM is Still Alive
- [ ] Run `jcmd <pid> VM.metaspace` — Metaspace usage breakdown
- [ ] Run `jcmd <pid> VM.classloader_stats` — ClassLoader instance count
- [ ] Run `jcmd <pid> GC.class_histogram` — Top classes by instance count
- [ ] Run `jcmd <pid> Thread.print` — Thread dump (3 samples, 10s apart)
- [ ] Run `jcmd <pid> JFR.start name=diagnostic settings=profile duration=60s filename=outage.jfr`
- [ ] Run `jstat -gcmetacapacity <pid> 5s 10` — Live Metaspace metrics
- [ ] Run `jmap -clstats <pid>` — Detailed ClassLoader statistics
- [ ] Trigger manual heap dump: `jcmd <pid> GC.heap_dump /data/dumps/manual.hprof`

### 2.3 Capture Baseline (from healthy instances for comparison)
- [ ] Collect same metrics from a healthy instance (same deployment, same region)
- [ ] Note: Metaspace baseline should be stable in healthy instances
- [ ] Compare ClassLoader counts between healthy and unhealthy

## 3. ROOT CAUSE ANALYSIS

### 3.1 Quick Checks
- [ ] Is Metaspace usage growing over time? Check 24-hour trend
- [ ] Is ClassLoader count increasing? Run `jcmd` classloader_stats
- [ ] Is class unloading happening? Check `jcmd GC.class_stats` unload count
- [ ] Are there stale ThreadLocal entries? Analyze with custom JFR or MAT
- [ ] Is the TCCL (Thread Context ClassLoader) being set and not reset?

### 3.2 Heap Dump Analysis (Eclipse MAT)
- [ ] Load heap dump in Eclipse MAT
- [ ] Run "Leak Suspects" report
- [ ] Run "ClassLoader" analysis: search for URLClassLoader instances
- [ ] For each suspicious ClassLoader:
  - [ ] Right-click → "Path to GC Roots" → "exclude weak/soft references"
  - [ ] Identify the reference chain holding the ClassLoader
  - [ ] Look for ThreadLocal, Thread, or servlet container references
- [ ] Check ThreadLocalMap entries:
  - [ ] In MAT: OQL `SELECT * FROM java.lang.ThreadLocal$ThreadLocalMap`
  - [ ] Look for entries with null keys (stale entries)
- [ ] Check for SecurityContext or similar context objects holding ClassLoader refs

### 3.3 JFR Analysis (JDK Mission Control)
- [ ] Load JFR recording in JMC
- [ ] Open "Memory" → "Metaspace" view
- [ ] Check "Class Loader Statistics" table — look for loaders with most classes
- [ ] Check "Class Loading" events — look for repeated loading of same classes
- [ ] Check "GC Configuration" and "GC Events" for Metaspace GC frequency
- [ ] Look for "ThreadLocal" related events

### 3.4 Code Inspection
- [ ] Search for `ThreadLocal` static fields in the codebase
- [ ] For each static ThreadLocal:
  - [ ] Is remove() called in a finally block?
  - [ ] Is the ThreadLocal value a context object that holds ClassLoader?
  - [ ] Is the ThreadLocal used in a thread pool environment?
- [ ] Search for `Thread.currentThread().setContextClassLoader()` calls
  - [ ] Is the original TCCL restored in a finally block?
- [ ] Search for dynamic class loading: `URLClassLoader`, `defineClass`
- [ ] Check for servlet API usage where request attributes might retain references

## 4. FIX AND VERIFICATION

### 4.1 Apply Fix
- [ ] Add `try { ... } finally { threadLocal.remove(); }` to all ThreadLocal usage
- [ ] Replace strong ThreadLocal with WeakThreadLocal wrapper
- [ ] Fix TCCL: restore original ClassLoader in finally block
- [ ] Add `URLClassLoader.close()` in cleanup code
- [ ] Build and deploy to canary instance

### 4.2 Verify Fix
- [ ] Check canary: Metaspace should stabilize after 6+ hours
- [ ] Run `jcmd <pid> VM.classloader_stats` — compare with pre-fix data
- [ ] Verify ThreadLocal cleanup: inject test requests, check ThreadLocalMap entries
- [ ] Run load test for 12+ hours (exceeding previous crash window)
- [ ] Verify Metaspace growth rate < 1MB/hour
- [ ] Verify no regression in p99 latency or throughput

### 4.3 Deploy to Production
- [ ] Canary (10% traffic, 12 hours)
- [ ] Regional (50% traffic, 24 hours)
- [ ] Full rollout (100% all regions, 48 hours monitoring)

## 5. PREVENTIVE MEASURES

### 5.1 Monitoring
- [ ] Add Metaspace usage alerts (60% warning, 80% critical)
- [ ] Add ClassLoader count monitoring
- [ ] Add growth rate alerting for Metaspace
- [ ] Deploy JVM monitoring configuration (jmx_exporter / Atlas)

### 5.2 Code Quality
- [ ] Add Checkstyle/ErrorProne rule for ThreadLocal cleanup
- [ ] Migrate to WeakThreadLocal pattern
- [ ] Plan migration to ScopedValue (Java 20+)

### 5.3 Testing
- [ ] Add long-duration load tests (12+ hours) to test suite
- [ ] Add Metaspace stability tests to CI/CD pipeline
- [ ] Add ThreadLocal cleanup tests

### 5.4 Runbook
- [ ] Update runbook with this incident's findings
- [ ] Train on-call engineers on Metaspace leak diagnosis
- [ ] Schedule quarterly tabletop exercise for OOM incidents

## 6. POSTMORTEM

### 6.1 Documentation
- [ ] Complete INCIDENT_REPORT.md with timeline
- [ ] Complete ROOT_CAUSE.md with 5 Whys
- [ ] Complete PREVENTION.md for future incidents
- [ ] Update MONITORING.md with any new metrics

### 6.2 Review
- [ ] Schedule postmortem meeting with all stakeholders
- [ ] Share findings with broader engineering organization
- [ ] Update incident response handbook
- [ ] File follow-up tickets for all action items

## Key Metrics Reference

| Metric | Healthy Value | Warning | Critical | Leak Indicator |
|--------|--------------|---------|----------|----------------|
| Metaspace Used | < 60% of MaxMetaspaceSize | 60-80% | > 80% | Increasing over time |
| Metaspace Growth Rate | < 1MB/hour | 1-5MB/hour | > 5MB/hour | Linear growth |
| ClassLoader Count | < 50 | 50-500 | > 500 | Increasing over time |
| Loaded Classes | < 10000 | 10000-50000 | > 50000 | Increasing over time |
| Unloaded Classes | > 0/hour | = 0 for 6 hours | = 0 for 24 hours | No unloading happening |
| GC Time (Metaspace) | < 1% GC time | 1-5% | > 5% | Increasing |

## Tools Reference

| Tool | Command | Purpose |
|------|---------|---------|
| jcmd | `jcmd <pid> VM.metaspace` | Metaspace usage breakdown |
| jcmd | `jcmd <pid> VM.classloader_stats` | ClassLoader instance count |
| jcmd | `jcmd <pid> GC.heap_dump <file>` | Generate heap dump |
| jstat | `jstat -gcmetacapacity <pid> <interval>` | Live Metaspace metrics |
| jmap | `jmap -clstats <pid>` | Detailed ClassLoader stats |
| jhsdb | `jhsdb jmap --heap --pid <pid>` | Heap info |
| Java Mission Control | JMC GUI | JFR analysis |
| Eclipse MAT | MAT GUI | Heap dump analysis |
| YourKit | Profiler | Alternative to JMC |


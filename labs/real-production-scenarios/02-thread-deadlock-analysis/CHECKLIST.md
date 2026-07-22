# Incident Response Runbook: Thread Deadlock / Application Hang

**Incident Type**: Thread Deadlock (Application Unresponsive)
**Severity**: P1 (SEV-2)
**Response Time**: < 2 minutes for initial triage

## 1. DETECTION AND TRIAGE

### 1.1 Verify the Alert
- [ ] Confirm alert: "DeadlockDetected", "BlockedThreadSpike", or latency SLO breach
- [ ] Check if application is responding to health checks
- [ ] Check if application is responding to requests (test endpoint)
- [ ] Verify the alert is not a monitoring false positive
- [ ] Check incident history: has this happened before?

### 1.2 Assess Impact
- [ ] Which nodes/services are affected?
- [ ] What is the latency impact (p50, p99, p999)?
- [ ] Are there cascading failures to downstream services?
- [ ] Are requests queuing or being dropped?
- [ ] Notify on-call incident manager
- [ ] Declare incident severity level (P0/P1)

### 1.3 Initial Mitigation (if actively hanging)
- [ ] Isolate affected node(s) from load balancer
- [ ] If all nodes affected: failover to standby cluster/region
- [ ] Restart affected JVM(s) as last resort (only after thread dump collection)
- [ ] Reduce traffic to degraded cluster
- [ ] Consider taking thread dumps before restart!

## 2. DATA COLLECTION

### 2.1 Collect Thread Dumps (3-Sample Method)
- [ ] Sample 1: `jstack -l <pid> > /data/dumps/thread_dump_1.txt`
- [ ] Wait 5 seconds
- [ ] Sample 2: `jstack -l <pid> > /data/dumps/thread_dump_2.txt`
- [ ] Wait 5 seconds
- [ ] Sample 3: `jstack -l <pid> > /data/dumps/thread_dump_3.txt`
- [ ] OR use: `jcmd <pid> Thread.print -l > /data/dumps/thread_dump_1.txt`
- [ ] Verify all 3 dumps show the same threads in BLOCKED state (confirms deadlock)

### 2.2 Check for JVM-Reported Deadlock
```bash
grep "Found one Java-level deadlock" /data/dumps/thread_dump_1.txt
grep -A 30 "Found one Java-level deadlock" /data/dumps/thread_dump_1.txt
```

### 2.3 Collect Additional JVM Data
- [ ] JFR recording: `jcmd <pid> JFR.start name=diagnostic settings=profile duration=60s`
- [ ] Thread state summary: `jstack <pid> | grep "java.lang.Thread.State" | sort | uniq -c`
- [ ] Lock contention stats: `jcmd <pid> VM.command_line` (check if lock profiling enabled)
- [ ] GC logs: check if GC is causing stop-the-world pauses
- [ ] CPU profile: `top -H -p <pid>` (async-profiler if available)

### 2.4 Capture System State
- [ ] CPU: `top` / `htop` (check if CPU is elevated or idle)
- [ ] Memory: `free -m`, `cat /proc/meminfo`
- [ ] Disk I/O: `iostat -x 1 5`
- [ ] Network: `netstat -an | wc -l`, `ss -s`
- [ ] Logs: application logs, system logs (`journalctl`)

## 3. THREAD DUMP ANALYSIS

### 3.1 Identify Deadlocked Threads
- [ ] Look for "Found one Java-level deadlock" in thread dump
- [ ] Identify the deadlock cycle: Thread A → Lock 1 (held by Thread B) ↔ Thread B → Lock 2 (held by Thread A)
- [ ] Note the thread names, stack traces, and monitor addresses

### 3.2 Identify Suspect Code Paths
- [ ] Trace the stack of each deadlocked thread to find the code entry point
- [ ] Identify which methods hold which locks
- [ ] Map each code path to a business operation (acquireLease, revokeLock, etc.)
- [ ] Check if locks are acquired in consistent order across all code paths

### 3.3 Analyze Lock Contention (Not Deadlock)
If no explicit deadlock is found, check for severe lock contention:
- [ ] Threads in BLOCKED state waiting for a single hot lock
- [ ] Lock hold times: are critical sections too large?
- [ ] Thread pool exhaustion: threads waiting for pool threads
- [ ] Cascading: one slow operation holding a lock that blocks many threads

### 3.4 Check for Other Hang Causes
- [ ] GC pause: check GC logs for long stop-the-world pauses
- [ ] Network I/O: threads in WAITING state on socket reads
- [ ] Disk I/O: threads in WAITING state on file operations
- [ ] External service call: threads waiting for downstream service response
- [ ] Infinite loop: threads in RUNNABLE state but making no progress (async-profiler)

## 4. ROOT CAUSE IDENTIFICATION

### 4.1 Lock Ordering Audit
- [ ] Identify all locks involved in the deadlock
- [ ] Find ALL code paths that acquire these locks
- [ ] Verify lock ordering is consistent across all paths
- [ ] Check if any code path acquires locks in a different order

### 4.2 Code Patterns to Check
- [ ] Nested synchronized blocks
- [ ] Nested ReentrantLock.lock() calls
- [ ] Mixed synchronization (synchronized + ReentrantLock together)
- [ ] Calling external code while holding a lock (callback pattern)
- [ ] Lock promotion: acquiring a second lock while holding a first
- [ ] Wait/notify patterns that might miss notifications

### 4.3 Testing for Reproduction
- [ ] Write a multi-threaded test that triggers the deadlock
- [ ] Use CountDownLatch to control thread execution order
- [ ] Vary thread counts and operation sequences
- [ ] Confirm the fix by running the same test after changes

## 5. FIX AND VERIFICATION

### 5.1 Apply Fix
#### Option A: Consistent Lock Ordering
- [ ] Determine the correct lock ordering (establish convention)
- [ ] Audit all code paths for the involved locks
- [ ] Reorder lock acquisition in inconsistent paths
- [ ] Add comments documenting lock order convention

#### Option B: ReentrantLock tryLock()
- [ ] Replace synchronized blocks with ReentrantLock
- [ ] Implement tryLock() with timeout (e.g., 100ms)
- [ ] Add retry logic with backoff
- [ ] Add recovery path when timeout is exceeded

#### Option C: Lock Breaking
- [ ] Reduce lock granularity (split into smaller locks)
- [ ] Eliminate nested locks entirely (restructure code)
- [ ] Use higher-level concurrency utilities

### 5.2 Verify Fix
- [ ] Run deadlock reproduction test — should pass without hanging
- [ ] Run high-concurrency stress test (10x production traffic)
- [ ] Verify no regression in latency or throughput
- [ ] Verify lock contention metrics improve (not just deadlock)
- [ ] Run thread safety analyzer (if available)

### 5.3 Deploy to Production
- [ ] Canary (5-10% traffic, minimum 6 hours)
- [ ] Regional (50% traffic, minimum 12 hours)
- [ ] Full rollout (100%, minimum 24 hours monitoring)

## 6. PREVENTIVE MEASURES

### 6.1 Monitoring
- [ ] Enable JFR lock contention events in production
- [ ] Add ThreadMXBean deadlock detection monitor
- [ ] Add blocked thread ratio alerts
- [ ] Add automated thread dump on hang detection

### 6.2 Code Quality
- [ ] Add consistent lock ordering convention
- [ ] Add Checkstyle rule for synchronized ordering
- [ ] Add deadlock detection tests to CI/CD pipeline
- [ ] Review all nested lock usage across codebase

### 6.3 Training
- [ ] Train team on deadlock theory (Coffman conditions)
- [ ] Train team on thread dump analysis
- [ ] Train team on JFR lock profiling

### 6.4 Runbook
- [ ] Update runbook with this incident's findings
- [ ] Add "3 thread dump" technique to on-call training
- [ ] Schedule quarterly deadlock tabletop exercise

## 7. POSTMORTEM

- [ ] Complete INCIDENT_REPORT.md with timeline
- [ ] Complete ROOT_CAUSE.md with 5 Whys
- [ ] Complete PREVENTION.md for future incidents
- [ ] Share findings with engineering organization
- [ ] File follow-up tickets for all action items

## Key Metrics Reference

| Metric | Healthy | Warning | Critical | Action |
|--------|---------|---------|----------|--------|
| Blocked Thread % | < 5% | 15-30% | > 30% | Collect thread dumps |
| Lock Contention Rate | < 10 events/s | 10-100/s | > 100/s | JFR lock profiling |
| Deadlocked Threads | 0 | 0 | > 0 | Immediately investigate |
| Lock Acquire Time | < 1ms | 1-100ms | > 100ms | Review critical sections |
| Thread Pool Queue | 0 | < 100 | > 1000 | Scale or investigate |
| GC Pause Time | < 50ms | 50-500ms | > 500ms | May masquerade as deadlock |

## Tools Reference

| Tool | Command | Purpose |
|------|---------|---------|
| jstack | `jstack -l <pid>` | Thread dump with lock info |
| jcmd | `jcmd <pid> Thread.print -l` | Thread dump (preferred) |
| jmc | `jmc` (GUI) | JFR analysis, lock contention |
| VisualVM | `visualvm` (GUI) | Thread monitoring, deadlock detection |
| jconsole | `jconsole <pid>` | Thread tab, detect deadlock button |
| async-profiler | `profiler.sh -d 60 -e lock <pid>` | Lock profiling |
| top | `top -H -p <pid>` | Per-thread CPU usage |
| Java Mission Control | JMC GUI | Comprehensive JVM monitoring |


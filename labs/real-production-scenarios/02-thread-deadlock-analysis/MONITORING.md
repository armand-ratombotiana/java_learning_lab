# Monitoring and Alerting: Thread Deadlock and Lock Contention

**Incident**: INC-2024-0520-DEADLOCK
**Category**: JVM Thread Observability
**Author**: JVM Platform Team + Google SRE

## Overview

This document specifies the monitoring, metrics, and alerting rules required to detect thread deadlocks, lock contention, and thread pool health issues before they cause service degradation. These monitors would have caught the lock coordinator deadlock incident within seconds of the first hang, rather than relying on downstream latency alerts.

## Metrics Collection

### 1. Thread State Metrics (Critical)

```
ThreadMXBean
  ├── ThreadCount           — Total live threads (daemon + non-daemon)
  ├── PeakThreadCount       — Peak live threads since JVM start
  ├── DaemonThreadCount     — Daemon thread count
  ├── TotalStartedThreadCount — Total threads created since JVM start
  
Per-state breakdown:
  ├── RUNNABLE              — Active threads executing code
  ├── BLOCKED               — Threads blocked waiting for monitor lock
  ├── WAITING               — Threads waiting (Object.wait, LockSupport.park)
  ├── TIMED_WAITING         — Threads waiting with timeout
  └── TERMINATED            — Dead threads
```

### 2. Lock Contention Metrics (Critical)

```
JFR Events (jdk.JavaMonitorEnter, jdk.JavaMonitorWait):
  ├── Lock.acquisitionTime        — Time to acquire a lock
  ├── Lock.contentedTime         — Time spent contending for lock
  ├── Lock.contentedEntries     — Number of times contention occurred
  ├── Lock.monitorAddress        — Lock identity hash

ReentrantLock metrics (via JFR jdk.Lock event, JDK 14+):
  ├── Lock.holdCount              — Number of holds on this lock
  ├── Lock.queueLength            — Threads waiting in queue
  ├── Lock.acquireTime            — Time to acquire the lock
  └── Lock.isFair                 — Fairness setting
```

### 3. Deadlock Detection (Critical)

```
ThreadMXBean:
  ├── findMonitorDeadlockedThreads()   — Deadlocked threads (intrinsic locks)
  └── findDeadlockedThreads()          — Deadlocked threads (all locks, JDK 6+)
```

### 4. Thread Pool Metrics (High)

```
For each thread pool (ThreadPoolExecutor):
  ├── pool.corePoolSize           — Core pool size
  ├── pool.maximumPoolSize        — Maximum pool size
  ├── pool.poolSize              — Current pool size
  ├── pool.activeCount           — Active threads
  ├── pool.queueSize             — Task queue size
  ├── pool.completedTaskCount    — Completed tasks
  ├── pool.largestPoolSize       — Largest pool size ever
  └── pool.rejectedExecutionCount — Rejected task count
```

## Alerting Rules

### P0/Critical Alarms (Page On-Call)

| Alert Name | Condition | Duration | Runbook |
|------------|-----------|----------|---------|
| DeadlockDetected | `findDeadlockedThreads() != null` | 0 min | Immediate thread dump collection |
| BlockedThreadSpike | `ratio(blocked_threads / total_threads) > 0.3` | 1 min | Collect thread dumps, investigate |
| ZeroProgressTime | `running_threads > 0 && completed_tasks == 0` for 30s | 0 min | Likely deadlock, force dump |
| ThreadPoolExhausted | `pool.activeCount >= pool.maximumPoolSize` AND `pool.queueSize > 1000` | 2 min | Thread pool saturation |

### P1/Warning Alarms (Alert during business hours)

| Alert Name | Condition | Duration | Action |
|------------|-----------|----------|--------|
| HighBlockedRatio | `ratio(blocked_threads / total_threads) > 0.15` | 5 min | Check thread dumps |
| LockContentionHigh | `rate(jdk.JavaMonitorEnter.contentedEntries[1m]) > 100` | 5 min | JFR lock profiling |
| ThreadPoolGrowth | `pool.poolSize > pool.corePoolSize * 1.5` for 10 min | 10 min | Review request pattern |
| LongLockAcquisition | `avg(Lock.acquisitionTime) > 500ms` | 5 min | Check lock hold times |

### P2/Info Alarms (Dashboard / Report)

| Alert Name | Condition | Action |
|------------|-----------|--------|
| ThreadCountIncrease | `ThreadCount > baseline * 1.5` for 1 hour | Investigate thread creation |
| ParkedThreadsHigh | `WAITING + TIMED_WAITING > 80%` of threads | Check for excessive blocking |
| LockQueueBacklog | `Lock.queueLength > 50` consistently | Review lock granularity |

## Dashboard: Thread Health Overview

### Panel 1: Thread State Distribution (Stacked Area Chart)
```sql
SELECT
  runnable_threads{instance="$instance"} AS "RUNNABLE",
  blocked_threads{instance="$instance"} AS "BLOCKED",
  waiting_threads{instance="$instance"} AS "WAITING",
  timed_waiting_threads{instance="$instance"} AS "TIMED_WAITING"
FROM jvm_thread_metrics
WHERE $__timeFilter
```

### Panel 2: Blocked Thread Ratio (Single Stat + Trend)
```sql
SELECT
  blocked_threads{instance="$instance"}
  / total_threads{instance="$instance"} * 100
AS "Blocked %"
FROM jvm_thread_metrics
```

### Panel 3: Lock Contention Heatmap (Heatmap)
```sql
SELECT
  lock_monitor_address,
  rate(jdk.JavaMonitorEnter.contentedEntries[1m])
AS "Contention Rate"
FROM jfr_events
WHERE $__timeFilter
```

### Panel 4: Thread Pool Utilization (Time Series)
```sql
SELECT
  pool_active_count{instance="$instance", pool="$pool"} AS "Active",
  pool_pool_size{instance="$instance", pool="$pool"} AS "Pool Size",
  pool_queue_size{instance="$instance", pool="$pool"} AS "Queue"
FROM jvm_thread_pool_metrics
```

## JFR Configuration for Lock Monitoring

```bash
# Enable lock profiling in production (low overhead, ~2% CPU)
jcmd <pid> JFR.start name=lock_monitoring \
  settings=profile \
  dumponexit=true \
  filename=/data/jfr/lock_monitoring.jfr

# Critical JFR events for lock analysis:
#   jdk.JavaMonitorEnter (monitor enter events)
#   jdk.JavaMonitorWait  (monitor wait events)
#   jdk.ThreadPark       (LockSupport.park events)
#   jdk.ThreadSleep      (Thread.sleep events)
#   jdk.ThreadStart      (thread creation)
#   jdk.ThreadEnd        (thread termination)
#   jdk.Lock             (ReentrantLock events, JDK 14+)

# Custom JFR event threshold:
-XX:FlightRecorderOptions:jdk.JavaMonitorEnter#threshold=10ms
```

## JMX Exporter Configuration

```yaml
# /etc/jmx_exporter/config_threads.yaml
rules:
  # Thread counts
  - pattern: 'java.lang<type=Threading><>(DaemonThreadCount|PeakThreadCount|ThreadCount|TotalStartedThreadCount)'
    name: jvm_threads_$1
    type: GAUGE

  # Deadlock detection
  - pattern: 'java.lang<type=Threading><>findDeadlockedThreads'
    name: jvm_deadlocked_threads
    type: UNTYPED

  # Thread pool metrics (Tomcat, Jetty, custom executors)
  - pattern: 'java.util.concurrent<type=ThreadPoolExecutor, name=(.+)><>(.+): (.+)'
    name: jvm_thread_pool_$2
    labels:
      pool: "$1"
    type: GAUGE
```

## Automated Deadlock Response

When a deadlock is detected, the system should automatically:

1. **Collect thread dumps** (3 samples, 5 seconds apart)
2. **Trigger JFR dump** for lock events
3. **Log the deadlock details** to a dedicated deadlock log file
4. **Alert on-call** with the thread dump attached
5. **Attempt recovery**: incrementally interrupt deadlocked threads (starting with the lowest-priority thread)
6. **If recovery fails**: restart the JVM (last resort)

```java
// Automated deadlock recovery
public class DeadlockRecovery {

    private final ThreadMXBean threadMxBean;

    public boolean attemptRecovery(long[] deadlockedIds) {
        LOG.severe("Attempting deadlock recovery for threads: " +
                Arrays.toString(deadlockedIds));

        // Try interrupting threads one by one
        for (long threadId : deadlockedIds) {
            ThreadInfo info = threadMxBean.getThreadInfo(threadId);
            if (info != null && info.getThreadState() == Thread.State.BLOCKED) {
                // Find the actual Thread object (requires thread registry)
                Thread thread = findThreadById(threadId);
                if (thread != null) {
                    LOG.info("Interrupting thread " + thread.getName() +
                            " (id=" + threadId + ")");
                    thread.interrupt();

                    // Wait for the thread to react to interruption
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // Check if deadlock is resolved
                    long[] remaining = threadMxBean.findDeadlockedThreads();
                    if (remaining == null || remaining.length == 0) {
                        LOG.info("Deadlock resolved by interrupting thread " +
                                thread.getName());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Thread findThreadById(long threadId) {
        // Simple implementation — in production use ThreadRegistry
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getId() == threadId) {
                return t;
            }
        }
        return null;
    }
}
```

## References

- Oracle: "Monitoring Thread Contention with JFR" — JDK Mission Control Documentation
- Google SRE: "Alerting and Thread Monitoring" — Google SRE Workbook
- Prometheus: "JMX Exporter for Thread Metrics" — https://github.com/prometheus/jmx_exporter
- Grafana: "JVM Thread Dashboard" — https://grafana.com/grafana/dashboards/14800
- JDK Documentation: "ThreadMXBean API" — https://docs.oracle.com/javase/8/docs/api/java/lang/management/ThreadMXBean.html


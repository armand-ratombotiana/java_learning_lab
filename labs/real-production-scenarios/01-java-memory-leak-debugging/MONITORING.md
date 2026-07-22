# Monitoring and Alerting: Metaspace and ClassLoader Leak Detection

**Incident**: INC-2024-0415-ZUUL-OOM
**Category**: JVM Observability
**Author**: JVM Platform Team

## Overview

This document specifies the monitoring, metrics, and alerting rules required to detect Metaspace leaks, ClassLoader leaks, and JVM memory pressure before they cause OutOfMemoryErrors. These monitors would have caught the Zuul gateway OOM incident approximately 4 hours before crash, giving engineers time to investigate and remediate.

## Metrics Collection

### 1. JVM Metaspace Metrics (Critical)

Collect the following metrics from each JVM instance via JMX MBeans:

```
MemoryPoolMXBean (Metaspace)
  ├── Usage.used           (bytes) — Current Metaspace usage
  ├── Usage.committed      (bytes) — Committed Metaspace size
  ├── Usage.max            (bytes) — MaxMetaspaceSize (-XX:MaxMetaspaceSize)
  └── CollectionUsage.used (bytes) — Usage after last GC (for growth rate calc)

MemoryPoolMXBean (Compressed Class Space)
  ├── Usage.used           (bytes) — Compressed class pointer space usage
  └── Usage.max            (bytes) — Max compressed class space
```

### 2. ClassLoader Metrics (Critical)

```
ClassLoadingMXBean
  ├── TotalLoadedClassCount     — Cumulative classes loaded since JVM start
  ├── LoadedClassCount          — Currently loaded classes (active)
  └── UnloadedClassCount        — Classes unloaded since JVM start

jcmd VM.classloader_stats
  ├── ClassLoader instances     — Current live ClassLoader count
  ├── Active loaders            — ClassLoaders with defined classes
  └── Decommissioned loaders    — ClassLoaders pending GC
```

### 3. ThreadLocal Retention Metrics (Advisory)

```
Per-thread ThreadLocalMap statistics (via JFR event or custom JMX)
  ├── ThreadLocalMap entries          — Number of entries in map (per thread)
  ├── Stale entries (null key)        — Leaked entries with GC'd ThreadLocal
  └── ThreadLocalMap.rehashCount     — Number of rehashes (indicator of stale entry accumulation)
```

## Monitoring Configuration

### JMX Export Example (Prometheus / Atlas)

```yaml
# Prometheus JMX Exporter Configuration for Metaspace monitoring
# Save as: /etc/jmx_exporter/config.yaml

startDelaySeconds: 0
ssl: false
lowercaseOutputName: true
lowercaseOutputLabelNames: true

rules:
  # Metaspace Memory Pool
  - pattern: 'java.lang<type=MemoryPool, name=(.*)><>Usage\.(used|committed|max)'
    name: jvm_memory_pool_$2_bytes
    labels:
      pool: "$1"
    type: GAUGE
    help: "JVM Memory Pool usage in bytes"

  # Metaspace Collection Usage (after GC)
  - pattern: 'java.lang<type=MemoryPool, name=(.*)><>CollectionUsage\.used'
    name: jvm_memory_pool_collection_used_bytes
    labels:
      pool: "$1"
    type: GAUGE

  # Class Loading
  - pattern: 'java.lang<type=ClassLoading><>(LoadedClassCount|TotalLoadedClassCount|UnloadedClassCount)'
    name: jvm_classes_$1
    type: GAUGE

  # Garbage Collection pause time and frequency
  - pattern: 'java.lang<type=GarbageCollector, name=(.*)><>(CollectionCount|CollectionTime)'
    name: jvm_gc_$2
    labels:
      gc: "$1"
    type: GAUGE
```

## Alerting Rules

### P0/Critical Alarms (Page On-Call)

| Alert Name | Condition | Duration | Runbook |
|------------|-----------|----------|---------|
| MetaspaceUsageCritical | `jvm_memory_pool_used_bytes{pool="Metaspace"} / jvm_memory_pool_max_bytes{pool="Metaspace"} >= 0.85` | 2 min | Check for ClassLoader leak, dump heap |
| MetaspaceGrowthRateCritical | `rate(jvm_memory_pool_used_bytes{pool="Metaspace"}[15m]) >= 52428800` (50MB/15min) | 5 min | Investigate dynamic class loading |
| ClassLoaderCountCritical | `jvm_classes_LoadedClassCount >= 50000` | 5 min | jcmd VM.classloader_stats |
| NoClassUnloading | `jvm_classes_UnloadedClassCount == 0` over 1 hour AND `rate(jvm_classes_TotalLoadedClassCount[1h]) > 1000` | 1 hour | Likely ClassLoader leak |

### P1/Warning Alarms (Alert during business hours)

| Alert Name | Condition | Duration | Action |
|------------|-----------|----------|--------|
| MetaspaceUsageWarning | `jvm_memory_pool_used_bytes{pool="Metaspace"} / jvm_memory_pool_max_bytes{pool="Metaspace"} >= 0.60` | 5 min | Investigate, trigger heap dump |
| MetaspaceGrowthRateWarning | `rate(jvm_memory_pool_used_bytes{pool="Metaspace"}[15m]) >= 10485760` (10MB/15min) | 10 min | Review recent deployments |
| ClassLoaderCountWarning | `jvm_classes_LoadedClassCount >= 20000` | 10 min | jcmd <pid> VM.classloader_stats |
| GCThrashing | `rate(jvm_gc_CollectionTime[5m]) / 5000 >= 0.25` (25% GC time) | 5 min | Check Metaspace GC activity |

### P2/Info Alarms (Dashboard / Report)

| Alert Name | Condition | Action |
|------------|-----------|--------|
| MetaspaceGrowthDetected | `derivative(jvm_memory_pool_used_bytes{pool="Metaspace"}[1h]) > 0` for 6 hours | Log ticket, trend analysis |
| ClassLoaderTrendUp | `derivative(jvm_classes_LoadedClassCount[24h]) > 1000` | Schedule investigation |
| ThreadLocalMapGrowth | Custom JFR event for ThreadLocalMap size increasing | Review code for missing remove() |

## Dashboard: Metaspace Health Overview

Create a Grafana/Atlas dashboard with the following panels:

### Panel 1: Metaspace Utilization (Time Series)
```sql
SELECT
  jvm_memory_pool_used_bytes{pool="Metaspace", instance="$instance"}
  / jvm_memory_pool_max_bytes{pool="Metaspace", instance="$instance"} * 100
AS "Metaspace % Used"
FROM metrics
WHERE $__timeFilter
```

Visual: Area chart with warning line at 60%, critical line at 80%

### Panel 2: Metaspace Growth Rate (Time Series)
```sql
SELECT
  rate(jvm_memory_pool_used_bytes{pool="Metaspace", instance="$instance"}[15m]) / 1048576
AS "Metaspace Growth Rate (MB/15min)"
FROM metrics
```

Visual: Bar chart with warning line at 10MB/15min

### Panel 3: Class Loading Activity (Time Series)
```sql
SELECT
  jvm_classes_LoadedClassCount{instance="$instance"}
AS "Loaded Classes (active)",
  rate(jvm_classes_TotalLoadedClassCount{instance="$instance"}[5m])
AS "Class Load Rate (classes/s)"
FROM metrics
```

### Panel 4: Class Unloading Health (Gauge)
```sql
SELECT
  jvm_classes_UnloadedClassCount{instance="$instance"}
AS "Total Unloaded Classes"
FROM metrics
```

If unloaded count is 0 and load rate is positive → LEAK INDICATOR

### Panel 5: GC Activity (Time Series)
```sql
SELECT
  rate(jvm_gc_CollectionTime{gc="Metaspace", instance="$instance"}[5m])
AS "Metaspace GC Time (ms/s)"
FROM metrics
```

## JFR Events for Metaspace Monitoring

Enable the following JFR events in production:

```bash
# Continuous JFR recording (low overhead, 24/7)
jcmd <pid> JFR.start name=metaspace_monitor \
  settings=profile \
  dumponexit=true \
  filename=/data/jfr/metaspace.jfr

# Or add to JVM startup:
-XX:StartFlightRecording=name=metaspace_monitor,settings=profile,\
  dumponexit=true,filename=/data/jfr/metaspace.jfr
```

Key JFR events to monitor:

| Event Name | Description | Threshold |
|------------|-------------|-----------|
| jdk.ClassLoaderStatistics | ClassLoader defineClass count and size | Monitor trend |
| jdk.ClassLoad | Class loading events | > 100/s indicates leak |
| jdk.ClassUnload | Class unloading events | Should be > 0 |
| jdk.MetaspaceAllocationFailure | When Metaspace allocation fails | Trigger P0 |
| jdk.MetaspaceGCThreshold | When Metaspace GC threshold is hit | > 10/min unusual |
| jdk.MetaspaceChunkFreeListSummary | Chunk utilization in Metaspace | Internal diagnostic |

## Incident Response Integration

When Metaspace alerts fire, the runbook should direct engineers to:

1. **Check ClassLoader count**: `jcmd <pid> VM.classloader_stats`
2. **Enable class unloading trace**: `jcmd <pid> VM.command_line -XX:+TraceClassUnloading`
3. **Capture JFR with high Metaspace eventing**: `jcmd <pid> JFR.start name=metaspace_dump settings=profile`
4. **Trigger heap dump**: `jcmd <pid> GC.heap_dump /data/dumps/heap.hprof`
5. **Check ThreadLocalMap usage**: Use JFR event `jdk.ThreadAllocationStatistics` or custom agent
6. **Review recent deployments** for new libraries that might use ThreadLocal with ClassLoaders

## Periodic Health Checks

Run these checks on production instances weekly:

```bash
# 1. ClassLoader inventory
jcmd <pid> VM.classloader_stats > classloader_report.txt

# 2. Metaspace summary
jcmd <pid> VM.metaspace > metaspace_report.txt

# 3. Class histogram (top 50 classes by instance count)
jcmd <pid> GC.class_histogram | head -50

# 4. JFR recording sample (5 minutes)
jcmd <pid> JFR.start name=weekly_check duration=5m \
  settings=profile filename=weekly_check.jfr

# 5. Thread dump for thread count and state analysis
jcmd <pid> Thread.print > thread_dump.txt
```

## Implementation Guide for SRE Teams

### Setting Up Metaspace Monitoring in Production

#### Step 1: Enable JFR for Continuous Monitoring

```bash
# Add to JVM startup parameters
-XX:StartFlightRecording=name=metaspace_monitor,\
  settings=profile,\
  dumponexit=true,\
  maxage=24h,\
  maxsize=500M,\
  filename=/data/jfr/metaspace.jfr
```

#### Step 2: Configure Prometheus JMX Exporter

Download the JMX exporter agent and add to JVM:

```bash
-javaagent:/opt/jmx_prometheus_javaagent.jar=9100:/etc/jmx_exporter/config.yaml
```

#### Step 3: Create Grafana Alerts

```yaml
# Grafana alert rule for Metaspace growth
groups:
  - name: jvm_metaspace_alerts
    rules:
      - alert: MetaspaceGrowthRateHigh
        expr: |
          rate(jvm_memory_pool_used_bytes{pool="Metaspace"}[15m]) > 52428800
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "Metaspace growing rapidly at {{ $value | humanize }} per 15min"

      - alert: MetaspaceUsageHigh
        expr: |
          jvm_memory_pool_used_bytes{pool="Metaspace"}
          / jvm_memory_pool_max_bytes{pool="Metaspace"} > 0.8
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "Metaspace usage at {{ $value | humanize }}%"
```

#### Step 4: Runbook Integration

When the Metaspace alert fires, the SRE runbook should include:

1. Connect to the affected node
2. Run `jcmd <pid> VM.metaspace` to check Metaspace breakdown
3. Run `jcmd <pid> VM.classloader_stats` to count ClassLoaders
4. If ClassLoader count is abnormal, capture thread dump and heap dump
5. Check recent deployments for new dynamic class loading
6. Compare Metaspace metrics with baseline from 24 hours ago
7. Escalate to JVM platform team if ClassLoader leak is confirmed

## References

- Prometheus JMX Exporter: https://github.com/prometheus/jmx_exporter
- Netflix Atlas: https://netflixtechblog.com/introducing-atlas-netflixs-primary-telemetry-platform
- Oracle: "Monitoring JVM Metaspace" — https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/memleaks002.html
- JDK Flight Recorder: JFR Event Reference — Oracle JDK Documentation
- Grafana: "JVM Dashboard Template" — https://grafana.com/grafana/dashboards/
- Google SRE: "Monitoring Distributed Systems" — Google SRE Book Chapter 6
- Datadog: "JVM Metaspace Monitoring" — Datadog Documentation
- New Relic: "JVM Memory Monitoring Best Practices" — New Relic Blog


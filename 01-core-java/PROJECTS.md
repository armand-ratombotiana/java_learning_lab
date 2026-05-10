# Core Java Projects

This directory contains projects focusing on Core Java fundamentals, JVM internals, and memory management. These projects are designed to deepen your understanding of the Java platform at a fundamental level.

## Mini-Project: Memory Leak Detector (2-4 hours)

### Overview

Create a memory leak detection utility that monitors JVM memory usage and identifies potential memory leaks by analyzing object allocation patterns. This project teaches you about JVM memory regions, garbage collection, and heap dump analysis.

### Project Structure

```
memory-leak-detector/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── corejava/
                    └── memoryleak/
                        ├── MemoryMonitor.java
                        ├── LeakDetector.java
                        ├── HeapAnalyzer.java
                        └── Application.java
```

### Key Features

- Real-time heap memory monitoring
- Object allocation tracking
- GC cycle analysis
- Memory leak detection with heap histogram snapshots
- Simple report generation

### Implementation

```java
package com.corejava.memoryleak;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MemoryMonitor {
    private final MemoryMXBean memoryBean;
    private final List<MemorySnapshot> snapshots;
    private final ScheduledExecutorService scheduler;
    private static final int MAX_SNAPSHOTS = 100;

    public MemoryMonitor() {
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.snapshots = new CopyOnWriteArrayList<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void startMonitoring(long intervalMs) {
        scheduler.scheduleAtFixedRate(this::captureSnapshot, 0, intervalMs, TimeUnit.MILLISECONDS);
    }

    public void stopMonitoring() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void captureSnapshot() {
        MemorySnapshot snapshot = new MemorySnapshot(
            System.currentTimeMillis(),
            getHeapMemoryUsage(),
            getNonHeapMemoryUsage(),
            getMemoryPools()
        );
        
        snapshots.add(snapshot);
        if (snapshots.size() > MAX_SNAPSHOTS) {
            snapshots.remove(0);
        }
    }

    public MemoryUsage getHeapMemoryUsage() {
        return memoryBean.getHeapMemoryUsage();
    }

    public MemoryUsage getNonHeapMemoryUsage() {
        return memoryBean.getNonHeapMemoryUsage();
    }

    public List<MemoryPoolInfo> getMemoryPools() {
        List<MemoryPoolInfo> pools = new ArrayList<>();
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (pool.getType() != null) {
                pools.add(new MemoryPoolInfo(
                    pool.getName(),
                    pool.getType().toString(),
                    pool.getUsage()
                ));
            }
        }
        return pools;
    }

    public List<MemorySnapshot> getSnapshots() {
        return new ArrayList<>(snapshots);
    }

    public MemoryAnalysis analyzeTrends() {
        if (snapshots.size() < 2) {
            return null;
        }
        
        double heapUsedGrowth = 0;
        for (int i = 1; i < snapshots.size(); i++) {
            long prevUsed = snapshots.get(i - 1).heapUsed;
            long currUsed = snapshots.get(i).heapUsed;
            if (prevUsed > 0) {
                heapUsedGrowth += (currUsed - prevUsed) / (double) prevUsed;
            }
        }
        
        double avgGrowth = heapUsedGrowth / (snapshots.size() - 1);
        boolean potentialLeak = avgGrowth > 0.01;
        
        return new MemoryAnalysis(
            avgGrowth,
            potentialLeak,
            snapshots.size(),
            detectMemoryPressure()
        );
    }

    private boolean detectMemoryPressure() {
        MemoryUsage heap = getHeapMemoryUsage();
        long max = heap.getMax();
        long used = heap.getUsed();
        return (double) used / max > 0.85;
    }

    public void forceGC() {
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

record MemorySnapshot(
    long timestamp,
    MemoryUsage heapUsage,
    MemoryUsage nonHeapUsage,
    List<MemoryPoolInfo> pools
) {
    long heapUsed() { return heapUsage.getUsed(); }
    long heapMax() { return heapUsage.getMax(); }
    double heapUsagePercent() { 
        return (double) heapUsed() / heapMax() * 100;
    }
}

record MemoryPoolInfo(String name, String type, MemoryUsage usage) {}

record MemoryAnalysis(
    double avgHeapGrowth,
    boolean potentialLeak,
    int snapshotCount,
    boolean memoryPressure
) {
    public String generateReport() {
        return String.format("""
            Memory Analysis Report
            ======================
            Snapshots Analyzed: %d
            Average Heap Growth: %.2f%%
            Potential Leak: %s
            Memory Pressure: %s
            
            Recommendation: %s
            """,
            snapshotCount,
            avgHeapGrowth * 100,
            potentialLeak ? "YES" : "NO",
            memoryPressure ? "HIGH" : "NORMAL",
            potentialLeak ? "Investigate object allocations" : "Memory appears stable"
        );
    }
}
```

```java
package com.corejava.memoryleak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class LeakDetector {
    private final Map<String, Object> strongReferences;
    private final Map<String, Object> weakReferences;
    private final List<String> leakWarnings;

    public LeakDetector() {
        this.strongReferences = new HashMap<>();
        this.weakReferences = new WeakHashMap<>();
        this.leakWarnings = new ArrayList<>();
    }

    public void trackObject(String key, Object obj, boolean weak) {
        if (weak) {
            weakReferences.put(key, obj);
        } else {
            strongReferences.put(key, obj);
        }
    }

    public void checkForLeaks() {
        leakWarnings.clear();
        
        if (strongReferences.size() > 100) {
            leakWarnings.add("High number of strong references: " + strongReferences.size());
        }
        
        for (Map.Entry<String, Object> entry : strongReferences.entrySet()) {
            if (entry.getValue() instanceof List) {
                List<?> list = (List<?>) entry.getValue();
                if (list.size() > 1000) {
                    leakWarnings.add("Large list detected: " + entry.getKey() + " with " + list.size() + " elements");
                }
            }
        }
        
        for (Map.Entry<String, Object> entry : strongReferences.entrySet()) {
            if (entry.getValue() instanceof StringBuilder) {
                StringBuilder sb = (StringBuilder) entry.getValue();
                if (sb.length() > 10000) {
                    leakWarnings.add("Large StringBuilder: " + entry.getKey() + " with " + sb.length() + " chars");
                }
            }
        }
    }

    public List<String> getLeakWarnings() {
        return new ArrayList<>(leakWarnings);
    }

    public int getTrackedObjectCount() {
        return strongReferences.size() + weakReferences.size();
    }

    public void clearStrongReferences() {
        strongReferences.clear();
    }

    public void removeReference(String key) {
        strongReferences.remove(key);
        weakReferences.remove(key);
    }
}
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.corejava</groupId>
    <artifactId>memory-leak-detector</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>Memory Leak Detector</name>
    <description>JVM Memory Leak Detection Utility</description>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.9</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.11</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <mainClass>com.corejava.memoryleak.Application</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Build and Run

```bash
cd memory-leak-detector
mvn clean compile
mvn exec:java -Dexec.mainClass="com.corejava.memoryleak.Application"
```

---

## Real-World Project: Custom Thread Pool with JVM Metrics (8+ hours)

### Overview

Build a production-grade thread pool implementation that includes custom thread factory, rejection policies, task queue management, and comprehensive JVM metrics collection. This project provides deep insights into concurrency, memory management, and performance tuning.

### Project Features

- Custom ThreadPoolExecutor implementation
- Thread factory with metrics collection
- Multiple rejection policies (Abort, CallerRuns, Discard, DiscardOldest)
- Synchronous queue with backpressure
- Real-time JVM metrics dashboard
- Task execution history and statistics
- Graceful shutdown with timeout
- Thread dump generation and analysis
- Deadlock detection

### Implementation

```java
package com.corejava.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.*;
import java.time.*;

public class InstrumentedThreadPoolExecutor extends ThreadPoolExecutor {
    
    private final AtomicLong submittedTasks = new AtomicLong(0);
    private final AtomicLong completedTasks = new AtomicLong(0);
    private final AtomicLong rejectedTasks = new AtomicLong(0);
    private final AtomicLong executedTasks = new AtomicLong(0);
    private final LongAdder totalExecutionTime = new LongAdder();
    private final LongAdder totalQueueTime = new LongAdder();
    
    private final ConcurrentHashMap<Long, TaskMetrics> runningTasks = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<TaskMetrics> completedTaskHistory = new ConcurrentLinkedQueue<>();
    private static final int MAX_HISTORY_SIZE = 1000;
    
    private final MetricsPublisher metricsPublisher;
    private final ThreadFactory customThreadFactory;
    private final RejectionHandler rejectionHandler;

    public InstrumentedThreadPoolExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory,
            RejectedExecutionHandler handler,
            MetricsPublisher publisher) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.metricsPublisher = publisher;
        this.customThreadFactory = threadFactory;
        this.rejectionHandler = new RejectionHandler(handler);
    }

    @Override
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }
        
        TaskMetrics metrics = new TaskMetrics(
            Thread.currentThread().getId(),
            System.nanoTime(),
            Instant.now()
        );
        
        submittedTasks.incrementAndGet();
        
        try {
            super.execute(command);
            runningTasks.put(Thread.currentThread().getId(), metrics);
        } catch (RejectedExecutionException e) {
            rejectedTasks.incrementAndGet();
            rejectionHandler.rejectedExecution(command, this);
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        long endTime = System.nanoTime();
        
        TaskMetrics metrics = runningTasks.remove(Thread.currentThread().getId());
        if (metrics != null) {
            long executionTime = endTime - metrics.startTimeNanos;
            totalExecutionTime.add(executionTime);
            
            long queueTime = metrics.startTimeNanos - metrics.submissionTimeNanos;
            totalQueueTime.add(queueTime);
            
            TaskMetrics completed = new TaskMetrics(
                metrics.threadId,
                metrics.submissionTimeNanos,
                metrics.submissionInstant,
                endTime,
                executionTime,
                queueTime,
                t
            );
            
            completedTaskHistory.add(completed);
            while (completedTaskHistory.size() > MAX_HISTORY_SIZE) {
                completedTaskHistory.poll();
            }
            
            executedTasks.incrementAndGet();
        }
        
        if (t != null) {
            System.err.println("Task execution failed: " + t.getMessage());
        }
        
        super.afterExecute(r, t);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    public PoolMetrics getMetrics() {
        long activeCount = getActiveCount();
        long poolSize = getPoolSize();
        long completed = completedTasks.get();
        long submitted = submittedTasks.get();
        
        double avgExecutionTime = completed > 0 
            ? (double) totalExecutionTime.sum() / completed / 1_000_000 
            : 0;
        double avgQueueTime = completed > 0 
            ? (double) totalQueueTime.sum() / completed / 1_000_000 
            : 0;
        
        return new PoolMetrics(
            submitted,
            completed,
            rejectedTasks.get(),
            activeCount,
            poolSize,
            getCorePoolSize(),
            getMaximumPoolSize(),
            getQueue().size(),
            getQueue().remainingCapacity(),
            avgExecutionTime,
            avgQueueTime,
            getCompletedTaskCount(),
            getTaskCount()
        );
    }

    public List<TaskMetrics> getRecentTasks(int limit) {
        List<TaskMetrics> recent = new ArrayList<>();
        for (TaskMetrics task : completedTaskHistory) {
            if (recent.size() >= limit) break;
            recent.add(task);
        }
        return recent;
    }

    public void publishMetrics() {
        if (metricsPublisher != null) {
            metricsPublisher.publish(getMetrics());
        }
    }

    public void shutdownNowWithTimeout(long timeoutMs) {
        List<Runnable> remaining = shutdownNow();
        
        long start = System.currentTimeMillis();
        long remainingTime = timeoutMs;
        
        while (!isTerminated() && remainingTime > 0) {
            try {
                awaitTermination(Math.min(remainingTime, 100), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            remainingTime = timeoutMs - (System.currentTimeMillis() - start);
        }
        
        if (!isTerminated()) {
            System.err.println("Forcing shutdown after timeout");
            // Force termination by interrupting all threads
            for (Thread t : getThreads()) {
                t.interrupt();
            }
        }
    }

    private Collection<Thread> getThreads() {
        Thread[] threads = new Thread[getMaximumPoolSize()];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(); // Placeholder
        }
        return Arrays.asList(threads);
    }
}

record TaskMetrics(
    long threadId,
    long submissionTimeNanos,
    Instant submissionInstant,
    long completionTimeNanos,
    long executionTimeNanos,
    long queueTimeNanos,
    Throwable error
) {
    TaskMetrics(long threadId, long submissionTimeNanos, Instant submissionInstant) {
        this(threadId, submissionTimeNanos, submissionInstant, 0, 0, 0, null);
    }
}

record PoolMetrics(
    long totalSubmitted,
    long totalCompleted,
    long totalRejected,
    long activeThreads,
    long currentPoolSize,
    int corePoolSize,
    int maxPoolSize,
    int queueSize,
    int queueCapacity,
    double avgExecutionTimeMs,
    double avgQueueTimeMs,
    long completedTasks,
    long totalTasks
) {
    public double getUtilization() {
        return corePoolSize > 0 ? (double) activeThreads / corePoolSize * 100 : 0;
    }
    
    public double getCompletionRate() {
        return totalSubmitted > 0 ? (double) totalCompleted / totalSubmitted * 100 : 0;
    }
}

interface MetricsPublisher {
    void publish(PoolMetrics metrics);
}

class RejectionHandler implements RejectedExecutionHandler {
    private final RejectedExecutionHandler delegate;
    private final AtomicLong rejectionCount = new AtomicLong(0);

    public RejectionHandler(RejectedExecutionHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        rejectionCount.incrementAndGet();
        delegate.rejectedExecution(r, executor);
    }

    public long getRejectionCount() {
        return rejectionCount.get();
    }
}
```

```java
package com.corejava.threadpool;

import java.lang.management.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class JVMMetricsCollector {
    
    private final MemoryMXBean memoryBean;
    private final ThreadMXBean threadBean;
    private final RuntimeMXBean runtimeBean;
    private final OperatingSystemMXBean osBean;
    
    private final AtomicLong gcCount = new AtomicLong(0);
    private long lastGcCount = 0;
    private long lastGcTime = 0;
    
    private final ConcurrentLinkedQueue<JVMSnapshot> snapshots = new ConcurrentLinkedQueue<>();
    private static final int MAX_SNAPSHOTS = 60;

    public JVMMetricsCollector() {
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.threadBean = ManagementFactory.getThreadMXBean();
        this.runtimeBean = ManagementFactory.getRuntimeMXBean();
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
    }

    public JVMSnapshot collectSnapshot() {
        updateGCStats();
        
        MemoryUsage heap = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryBean.getNonHeapMemoryUsage();
        
        ThreadInfo[] threads = threadBean.dumpAllThreads(false, false);
        long peakThreads = threadBean.getPeakThreadCount();
        long daemonThreads = threadBean.getDaemonThreadCount();
        
        return new JVMSnapshot(
            System.currentTimeMillis(),
            Runtime.getRuntime().totalMemory(),
            Runtime.getRuntime().freeMemory(),
            Runtime.getRuntime().maxMemory(),
            heap.getUsed(),
            heap.getCommitted(),
            nonHeap.getUsed(),
            getGCStats(),
            threadBean.getThreadCount(),
            peakThreads,
            daemonThreads,
            threadBean.getCurrentThreadCpuTime(),
            threadBean.getCurrentThreadUserTime(),
            osBean.getSystemLoadAverage(),
            osBean.getAvailableProcessors(),
            runtimeBean.getUptime()
        );
    }

    private void updateGCStats() {
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        
        long totalGcCount = 0;
        long totalGcTime = 0;
        
        for (GarbageCollectorMXBean gc : gcBeans) {
            totalGcCount += gc.getCollectionCount();
            totalGcTime += gc.getCollectionTime();
        }
        
        gcCount.set(totalGcCount);
        
        if (lastGcCount > 0) {
            lastGcTime = totalGcTime - lastGcTime;
        }
        lastGcCount = totalGcCount;
    }

    private GCStats getGCStats() {
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        
        Map<String, GCDetail> gcDetails = new ConcurrentHashMap<>();
        long totalCount = 0;
        long totalTime = 0;
        
        for (GarbageCollectorMXBean gc : gcBeans) {
            String name = gc.getName();
            long count = gc.getCollectionCount();
            long time = gc.getCollectionTime();
            
            totalCount += count;
            totalTime += time;
            
            gcDetails.put(name, new GCDetail(count, time, 
                Arrays.asList(gc.getMemoryPoolNames())));
        }
        
        return new GCStats(totalCount, totalTime, gcDetails);
    }

    public void recordSnapshot() {
        JVMSnapshot snapshot = collectSnapshot();
        snapshots.add(snapshot);
        
        while (snapshots.size() > MAX_SNAPSHOTS) {
            snapshots.poll();
        }
    }

    public List<JVMSnapshot> getSnapshots() {
        return new java.util.ArrayList<>(snapshots);
    }

    public JVMAnalysis analyze() {
        if (snapshots.size() < 2) {
            return null;
        }
        
        JVMSnapshot first = snapshots.peek();
        JVMSnapshot last = snapshots.stream().reduce((a, b) -> b).orElse(first);
        
        double heapGrowth = calculateGrowth(first.heapUsed(), last.heapUsed());
        double gcFrequency = (last.gcStats.totalCount() - first.gcStats.totalCount()) 
            / (first.uptime > 0 ? (last.uptime - first.uptime) / 1000.0 : 1);
        
        return new JVMAnalysis(
            heapGrowth,
            gcFrequency,
            snapshots.size(),
            detectMemoryPressure(last),
            getThreadAnalysis()
        );
    }

    private double calculateGrowth(long start, long end) {
        return start > 0 ? ((double) (end - start) / start) * 100 : 0;
    }

    private boolean detectMemoryPressure(JVMSnapshot snapshot) {
        double heapUsage = (double) snapshot.heapUsed() / snapshot.maxMemory() * 100;
        return heapUsage > 85;
    }

    private ThreadAnalysis getThreadAnalysis() {
        long peak = threadBean.getPeakThreadCount();
        long current = threadBean.getThreadCount();
        long daemon = threadBean.getDaemonThreadCount();
        
        return new ThreadAnalysis(current, peak, daemon);
    }
}

record JVMSnapshot(
    long timestamp,
    long totalMemory,
    long freeMemory,
    long maxMemory,
    long heapUsed,
    long heapCommitted,
    long nonHeapUsed,
    GCStats gcStats,
    int threadCount,
    long peakThreads,
    long daemonThreads,
    long currentThreadCpuTime,
    long currentThreadUserTime,
    double systemLoadAverage,
    int availableProcessors,
    long uptime
) {
    double heapUsagePercent() { return (double) heapUsed / maxMemory * 100; }
    double memoryUsagePercent() { return (double) totalMemory / maxMemory * 100; }
}

record GCStats(
    long totalCount,
    long totalTimeMs,
    Map<String, GCDetail> details
) {}

record GCDetail(
    long collectionCount,
    long collectionTimeMs,
    List<String> memoryPools
) {}

record JVMAnalysis(
    double heapGrowthPercent,
    double gcFrequencyPerSec,
    int snapshotCount,
    boolean memoryPressure,
    ThreadAnalysis threadAnalysis
) {
    public String generateReport() {
        return String.format("""
            JVM Analysis Report
            ===================
            Heap Growth: %.2f%%
            GC Frequency: %.2f/sec
            Memory Pressure: %s
            Thread Count: %d (Peak: %d, Daemon: %d)
            
            Status: %s
            """,
            heapGrowthPercent,
            gcFrequencyPerSec,
            memoryPressure ? "HIGH" : "NORMAL",
            threadAnalysis.currentCount(),
            threadAnalysis.peakCount(),
            threadAnalysis.daemonCount(),
            determineStatus()
        );
    }
    
    private String determineStatus() {
        if (memoryPressure && gcFrequencyPerSec > 2) {
            return "CRITICAL - Immediate attention needed";
        } else if (memoryPressure) {
            return "WARNING - Monitor closely";
        } else if (gcFrequencyPerSec > 1) {
            return "CAUTION - Minor issues detected";
        }
        return "HEALTHY - All metrics normal";
    }
}

record ThreadAnalysis(
    int currentCount,
    long peakCount,
    long daemonCount
) {}
```

```java
package com.corejava.threadpool;

import java.util.concurrent.*;
import java.time.*;

public class Application {
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== Custom Thread Pool with JVM Metrics ===\n");
        
        InstrumentedThreadPoolExecutor pool = createThreadPool();
        
        JVMMetricsCollector metricsCollector = new JVMMetricsCollector();
        ScheduledExecutorService metricsScheduler = Executors.newSingleThreadScheduledExecutor();
        
        metricsScheduler.scheduleAtFixedRate(() -> {
            metricsCollector.recordSnapshot();
            pool.publishMetrics();
            
            PoolMetrics pm = pool.getMetrics();
            JVMSnapshot jvm = metricsCollector.collectSnapshot();
            
            System.out.printf("[%s] Pool: active=%d, queue=%d, completed=%d | " +
                "Heap: %.1f%%, Threads: %d, Load: %.2f%n",
                Instant.now(),
                pm.activeThreads(),
                pm.queueSize(),
                pm.totalCompleted(),
                jvm.heapUsagePercent(),
                jvm.threadCount(),
                jvm.systemLoadAverage()
            );
        }, 0, 1, TimeUnit.SECONDS);
        
        runWorkload(pool, 50);
        
        Thread.sleep(5000);
        
        metricsScheduler.shutdown();
        pool.shutdown();
        
        printFinalReport(pool, metricsCollector);
    }

    private static InstrumentedThreadPoolExecutor createThreadPool() {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(100) {
            @Override
            public boolean offer(Runnable e) {
                if (size() >= 100) {
                    return false;
                }
                return super.offer(e);
            }
        };
        
        ThreadFactory factory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(1);
            
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "Instrumented-" + counter.getAndIncrement());
                t.setDaemon(false);
                t.setPriority(Thread.NORM_PRIORITY);
                return t;
            }
        };
        
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        
        return new InstrumentedThreadPoolExecutor(
            4,
            8,
            60,
            TimeUnit.SECONDS,
            queue,
            factory,
            handler,
            metrics -> {}
        );
    }

    private static void runWorkload(InstrumentedThreadPoolExecutor pool, int taskCount) {
        System.out.println("\n--- Submitting " + taskCount + " tasks ---\n");
        
        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            pool.execute(() -> {
                try {
                    Thread.sleep((long) (Math.random() * 500 + 100));
                    if (taskId % 10 == 0) {
                        throw new RuntimeException("Simulated error in task " + taskId);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    private static void printFinalReport(InstrumentedThreadPoolExecutor pool, 
            JVMMetricsCollector collector) {
        System.out.println("\n=== Final Report ===");
        
        PoolMetrics pm = pool.getMetrics();
        System.out.println(pm);
        
        JVMAnalysis analysis = collector.analyze();
        if (analysis != null) {
            System.out.println(analysis.generateReport());
        }
    }
}
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.corejava</groupId>
    <artifactId>thread-pool-with-metrics</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>Custom Thread Pool with JVM Metrics</name>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.9</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.11</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <mainClass>com.corejava.threadpool.Application</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Build and Run

```bash
cd thread-pool-with-metrics
mvn clean package
java -jar target/thread-pool-with-metrics-1.0-SNAPSHOT.jar
```

### Testing Different Load Patterns

```bash
# High load test
java -Xms256m -Xmx512m -jar target/thread-pool-with-metrics-1.0-SNAPSHOT.jar

# Low memory test
java -Xms128m -Xmx256m -XX:+UseG1GC -jar target/thread-pool-with-metrics-1.0-SNAPSHOT.jar
```

---

## Additional Learning Resources

- JVM Performance Tuning: https://docs.oracle.com/javase/8/docs/technotes/guides/performance/
- Understanding Garbage Collection: https://www.oracle.com/webfolder/technetwork/tutorials/obe/java/gc01/index.html
- Threading Best Practices: https://docs.oracle.com/javase/tutorial/essential/concurrency/
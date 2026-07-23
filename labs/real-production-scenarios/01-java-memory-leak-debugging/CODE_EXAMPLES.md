# Lab 01 — Java Memory Leak: Code Examples

## Reproducing the Bug

### LeakingFilterRunner.java — Demonstrates the Metaspace Leak

```java
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LeakingFilterRunner {

    // This static ThreadLocal causes the leak — value is never removed
    private static final ThreadLocal<SecurityContext> contextHolder = new ThreadLocal<>();

    // Track ClassLoader instances to demonstrate leak
    private static final List<WeakReference<ClassLoader>> classLoaderRefs = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Metaspace Leak Demo...");
        System.out.println("Run with -XX:MaxMetaspaceSize=128m -XX:+PrintGCDetails");
        System.out.println("Monitor with: jcmd <pid> VM.classloader_stats");

        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Simulate request processing — each request "loads" a new filter
        // (represented by a ClassLoader) and stores it in ThreadLocal
        for (int i = 0; i < 100000; i++) {
            executor.submit(() -> processRequest());
            Thread.sleep(10);

            if (i % 100 == 0) {
                printStats(i);
            }

            // Check for OOM
            Runtime runtime = Runtime.getRuntime();
            if (runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory() < 1024 * 1024 * 10) {
                System.out.println("Low memory — would crash soon!");
                break;
            }
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        printFinalStats();
    }

    static void processRequest() {
        // BUG: Security context stores reference to ClassLoader
        // ThreadLocal is set but NEVER REMOVED
        SecurityContext ctx = new SecurityContext(createDynamicFilterClassLoader());
        contextHolder.set(ctx);

        // Simulate request processing
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // BUG: Missing contextHolder.remove() in finally block!
        // The ThreadLocal value remains for the lifetime of the thread
    }

    static ClassLoader createDynamicFilterClassLoader() {
        URLClassLoader cl = new URLClassLoader(new URL[0], ClassLoader.getSystemClassLoader());
        classLoaderRefs.add(new WeakReference<>(cl));
        return cl;
    }

    static void printStats(int requestCount) {
        long totalClassLoaders = classLoaderRefs.stream()
                .filter(ref -> ref.get() != null)
                .count();

        System.out.printf("Request #%d — Active ClassLoaders: %d (%.2f%% leaked)%n",
                requestCount, totalClassLoaders,
                (double) totalClassLoaders / Math.max(1, requestCount) * 100);
    }

    static void printFinalStats() {
        long totalClassLoaders = classLoaderRefs.stream()
                .filter(ref -> ref.get() != null)
                .count();

        long gcCollected = classLoaderRefs.size() - totalClassLoaders;

        System.out.printf("%n%n--- FINAL STATS ---%n");
        System.out.printf("Total ClassLoaders created: %d%n", classLoaderRefs.size());
        System.out.printf("Still alive (leaked): %d%n", totalClassLoaders);
        System.out.printf("GC collected: %d%n", gcCollected);
        System.out.printf("Leak rate: %.2f%%%n",
                (double) totalClassLoaders / classLoaderRefs.size() * 100);

        if (totalClassLoaders > 0) {
            System.out.println("LEAK CONFIRMED: " + totalClassLoaders +
                    " ClassLoaders are still referenced via ThreadLocal!");
        }
    }
}

class SecurityContext {
    private final ClassLoader classLoader;
    private final long createdAt = System.currentTimeMillis();
    private final String requestId = java.util.UUID.randomUUID().toString().substring(0, 8);

    SecurityContext(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() { return classLoader; }

    @Override
    protected void finalize() {
        System.out.println("[GC] SecurityContext " + requestId + " garbage collected");
    }
}
```

### Unit Test — Confirms the Leak

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class LeakingFilterRunnerTest {

    @Test
    @Timeout(30)
    void testThreadLocalCausesClassLoaderLeak() throws Exception {
        // Track a specific ClassLoader
        URLClassLoader testLoader = new URLClassLoader(new URL[0],
                ClassLoader.getSystemClassLoader());
        WeakReference<ClassLoader> weakRef = new WeakReference<>(testLoader);

        // Set ThreadLocal in a pool thread — this would leak in production
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        executor.submit(() -> {
            // Simulate the bug: set ThreadLocal without remove
            ThreadLocal<ClassLoader> leakingTl = new ThreadLocal<>();
            leakingTl.set(testLoader);
            latch.countDown();
            // ThreadLocal is never removed!
        });

        latch.await();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Clear our reference to the ClassLoader
        testLoader = null;

        // Force GC multiple times
        System.gc();
        System.gc();
        System.gc();

        // The ClassLoader should still be alive because the thread pool
        // thread still holds it in ThreadLocalMap
        assertNotNull(weakRef.get(),
                "ClassLoader should NOT be GC'd — ThreadLocal holds strong reference!");
    }
}
```

## Fixing the Bug

### FixedFilterRunner.java — ThreadLocal Cleanup with try-finally

```java
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FixedFilterRunner {

    // Using try-finally pattern for safe ThreadLocal usage
    private static final ThreadLocal<SecurityContext> contextHolder = new ThreadLocal<>();

    private static final List<WeakReference<ClassLoader>> classLoaderRefs = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        System.out.println("Starting FIXED Metaspace Demo...");
        System.out.println("ThreadLocal values are properly removed in finally blocks.");
        System.out.println("No Metaspace leak expected.");

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> processRequestFixed());
            Thread.sleep(50);
            if (i % 100 == 0) printStats(i);
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        printFinalStats();
    }

    static void processRequestFixed() {
        // Set ThreadLocal at the START of request processing
        SecurityContext ctx = new SecurityContext(createDynamicFilterClassLoader());
        contextHolder.set(ctx);

        try {
            // Simulate request processing — may throw exceptions
            Thread.sleep(50);
            doBusinessLogic();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // Log exception but ensure cleanup still happens
            System.err.println("Error processing request: " + e.getMessage());
        } finally {
            // FIX: ALWAYS remove ThreadLocal value — even on exception paths!
            contextHolder.remove();
        }
    }

    static void doBusinessLogic() {
        // Access ThreadLocal safely — it's valid here
        SecurityContext ctx = contextHolder.get();
        if (ctx != null) {
            // Use context for business logic
        }
    }

    static ClassLoader createDynamicFilterClassLoader() {
        URLClassLoader cl = new URLClassLoader(new URL[0],
                ClassLoader.getSystemClassLoader());
        classLoaderRefs.add(new WeakReference<>(cl));
        return cl;
    }

    static void printStats(int requestCount) {
        long alive = classLoaderRefs.stream()
                .filter(ref -> ref.get() != null)
                .count();
        System.out.printf("Request #%d — Alive ClassLoaders: %d%n", requestCount, alive);
    }

    static void printFinalStats() {
        long alive = classLoaderRefs.stream()
                .filter(ref -> ref.get() != null)
                .count();
        long collected = classLoaderRefs.size() - alive;
        System.out.printf("%nCreated: %d, Collected by GC: %d, Still alive: %d%n",
                classLoaderRefs.size(), collected, alive);
        System.out.println("If collected == created (minus recently created), leak is fixed!");
    }
}
```

### Unit Test — Confirms the Fix

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class FixedFilterRunnerTest {

    @Test
    @Timeout(15)
    void testThreadLocalDoesNotLeakWithTryFinally() throws Exception {
        ThreadLocal<ClassLoader> safeHolder = new ThreadLocal<>();
        URLClassLoader testLoader = new URLClassLoader(new URL[0],
                ClassLoader.getSystemClassLoader());
        WeakReference<ClassLoader> weakRef = new WeakReference<>(testLoader);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        executor.submit(() -> {
            safeHolder.set(testLoader);
            try {
                // Process request
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // FIX: Always remove
                safeHolder.remove();
            }
            latch.countDown();
        });

        latch.await();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Clear our strong reference
        testLoader = null;

        // Force GC
        System.gc();
        System.gc();
        System.gc();

        // The ClassLoader should be GC'd because ThreadLocal was removed
        assertNull(weakRef.get(),
                "ClassLoader should be GC'd — ThreadLocal was properly removed!");
    }

    @Test
    @Timeout(15)
    void testThreadLocalCleanedUpOnException() throws Exception {
        ThreadLocal<ClassLoader> safeHolder = new ThreadLocal<>();
        URLClassLoader testLoader = new URLClassLoader(new URL[0],
                ClassLoader.getSystemClassLoader());
        WeakReference<ClassLoader> weakRef = new WeakReference<>(testLoader);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        executor.submit(() -> {
            safeHolder.set(testLoader);
            try {
                // Simulate exception during request processing
                throw new RuntimeException("Simulated failure");
            } finally {
                // FIX: Cleanup happens even on exception
                safeHolder.remove();
                latch.countDown();
            }
        });

        latch.await();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        testLoader = null;
        System.gc();
        System.gc();
        System.gc();

        assertNull(weakRef.get(),
                "ClassLoader should be GC'd even when exception occurred!");
    }
}
```

### WeakThreadLocal.java — Alternative Pattern

```java
import java.lang.ref.WeakReference;

/**
 * A WeakThreadLocal wrapper that prevents ClassLoader leaks by using
 * WeakReference to wrap values. When the thread is done, the GC can
 * reclaim the value even if remove() is not explicitly called.
 *
 * Note: This is a workaround, not a replacement for proper cleanup.
 * Always prefer try-finally with remove().
 */
public class WeakThreadLocal<T> {

    private final ThreadLocal<WeakReference<T>> delegate = new ThreadLocal<>();

    public void set(T value) {
        delegate.set(new WeakReference<>(value));
    }

    public T get() {
        WeakReference<T> ref = delegate.get();
        return ref != null ? ref.get() : null;
    }

    public void remove() {
        delegate.remove();
    }

    // For InheritableThreadLocal equivalent
    public static class Inheritable<T> {
        private final InheritableThreadLocal<WeakReference<T>> delegate =
                new InheritableThreadLocal<>();

        public void set(T value) {
            delegate.set(new WeakReference<>(value));
        }

        public T get() {
            WeakReference<T> ref = delegate.get();
            return ref != null ? ref.get() : null;
        }

        public void remove() {
            delegate.remove();
        }
    }
}
```

### ThreadLocalCleanupFilter.java — Framework-Level Cleanup

```java
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Servlet Filter that cleans up all ThreadLocal values after each request.
 * This is a safety net for codebases with many ThreadLocal usages.
 */
public class ThreadLocalCleanupFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } finally {
            // Clean up all known ThreadLocals in this codebase
            SecurityContextHolder.clear();
            RequestContextHolder.clear();
            TracingContextHolder.clear();

            // Optional: clear ALL ThreadLocals via reflection (use with caution)
            // clearAllThreadLocals();
        }
    }

    @Override
    public void destroy() {}

    @SuppressWarnings("unchecked")
    private void clearAllThreadLocals() {
        try {
            Thread currentThread = Thread.currentThread();
            Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);
            Object threadLocalMap = threadLocalsField.get(currentThread);
            if (threadLocalMap != null) {
                Field tableField = threadLocalMap.getClass().getDeclaredField("table");
                tableField.setAccessible(true);
                Object[] table = (Object[]) tableField.get(threadLocalMap);
                for (Object entry : table) {
                    if (entry != null) {
                        Field valueField = entry.getClass().getDeclaredField("value");
                        valueField.setAccessible(true);
                        valueField.set(entry, null);
                    }
                }
            }
        } catch (Exception e) {
            // Log but don't fail the request
            System.err.println("Failed to clear ThreadLocals: " + e.getMessage());
        }
    }
}
```

### MetaspaceMonitor.java — Production Monitoring

```java
import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Production monitor for Metaspace utilization that alerts on abnormal growth.
 */
public class MetaspaceMonitor {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private long previousMetaspaceUsed = 0;
    private long previousTimestamp = System.currentTimeMillis();

    public void startMonitoring(long intervalMs) {
        scheduler.scheduleAtFixedRate(this::checkMetaspace, 0, intervalMs, TimeUnit.MILLISECONDS);
    }

    public void stopMonitoring() {
        scheduler.shutdown();
    }

    void checkMetaspace() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage metaspaceUsage = memoryMXBean.getNonHeapMemoryUsage();

            long used = metaspaceUsage.getUsed();
            long max = metaspaceUsage.getMax();
            long now = System.currentTimeMillis();

            double utilizationPercent = (double) used / max * 100;
            long deltaTime = now - previousTimestamp;
            double growthRateMBPerHour = 0;

            if (previousMetaspaceUsed > 0 && deltaTime > 0) {
                long deltaBytes = used - previousMetaspaceUsed;
                growthRateMBPerHour = (double) deltaBytes / (1024 * 1024) / (deltaTime / 3600000.0);

                System.out.printf("[Metaspace Monitor] Used: %d MB (%.1f%%), Max: %d MB, " +
                                "Growth rate: %.2f MB/hour%n",
                        used / (1024 * 1024), utilizationPercent,
                        max / (1024 * 1024), growthRateMBPerHour);

                if (utilizationPercent > 80) {
                    System.err.println("WARNING: Metaspace utilization > 80%! " + utilizationPercent + "%");
                }

                if (growthRateMBPerHour > 50) {
                    System.err.println("ALERT: Metaspace growth rate > 50 MB/hour! " +
                            String.format("%.2f", growthRateMBPerHour) + " MB/hour");
                }
            }

            previousMetaspaceUsed = used;
            previousTimestamp = now;

        } catch (Exception e) {
            System.err.println("Error checking Metaspace: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MetaspaceMonitor monitor = new MetaspaceMonitor();
        monitor.startMonitoring(5000);
        System.out.println("Monitoring Metaspace every 5 seconds...");
        Thread.sleep(60000);
        monitor.stopMonitoring();
    }
}
```

### LeakDetectorService.java — Heap Dump Analysis Automation

```java
import javax.management.*;
import java.lang.management.ManagementFactory;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Automated leak detector that triggers heap dump analysis when
 * Metaspace growth exceeds thresholds.
 */
public class LeakDetectorService {

    private static final String HEAP_DUMP_PATH = "/data/dumps/";

    public void onMetaspaceAlert(double growthRateMBPerHour) {
        System.out.println("Metaspace alert: " + growthRateMBPerHour + " MB/hour growth");

        // Capture diagnostic data
        captureHeapDump();
        captureJFRRecording();
        captureThreadDump();
    }

    void captureHeapDump() {
        try {
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String dumpFile = HEAP_DUMP_PATH + "heap_" + pid + "_" + timestamp + ".hprof";

            // Trigger heap dump via HotSpotDiagnostic MXBean
            ObjectName name = new ObjectName("com.sun.management:type=HotSpotDiagnostic");
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            server.invoke(name, "dumpHeap",
                    new Object[]{dumpFile, true},
                    new String[]{String.class.getName(), boolean.class.getName()});

            System.out.println("Heap dump saved to: " + dumpFile);
        } catch (Exception e) {
            System.err.println("Failed to capture heap dump: " + e.getMessage());
        }
    }

    void captureJFRRecording() {
        try {
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String jfrFile = HEAP_DUMP_PATH + "recording_" + pid + "_" + timestamp + ".jfr";

            ProcessBuilder pb = new ProcessBuilder(
                    "jcmd", pid,
                    "JFR.start",
                    "name=metaspace_diag",
                    "settings=profile",
                    "duration=300s",
                    "filename=" + jfrFile
            );
            Process p = pb.start();
            p.waitFor();

            System.out.println("JFR recording saved to: " + jfrFile);
        } catch (Exception e) {
            System.err.println("Failed to capture JFR: " + e.getMessage());
        }
    }

    void captureThreadDump() {
        try {
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String dumpFile = HEAP_DUMP_PATH + "threads_" + pid + "_" + timestamp + ".txt";

            ProcessBuilder pb = new ProcessBuilder(
                    "jcmd", pid, "Thread.print", "-l"
            );
            Process p = pb.start();
            String output = new String(p.getInputStream().readAllBytes());
            Files.writeString(Paths.get(dumpFile), output);
            p.waitFor();

            System.out.println("Thread dump saved to: " + dumpFile);
        } catch (Exception e) {
            System.err.println("Failed to capture thread dump: " + e.getMessage());
        }
    }
}
```

### MetricsExportService.java — Prometheus JMX Export for Metaspace

```java
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;
import javax.management.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * Exposes Metaspace metrics via Prometheus for production monitoring.
 * Integrates with Grafana dashboards and alerting.
 */
public class MetaspaceMetricsExporter {

    static final Gauge metaspaceUsed = Gauge.build()
            .name("jvm_metaspace_used_bytes")
            .help("Current Metaspace used in bytes")
            .register();

    static final Gauge metaspaceMax = Gauge.build()
            .name("jvm_metaspace_max_bytes")
            .help("Maximum Metaspace size in bytes")
            .register();

    static final Gauge classLoaderCount = Gauge.build()
            .name("jvm_classloader_count")
            .help("Number of active ClassLoaders")
            .register();

    static final Gauge classLoadingRate = Gauge.build()
            .name("jvm_classes_loaded_per_second")
            .help("Rate of class loading per second")
            .register();

    private long previousClassCount = 0;
    private long previousTimestamp = System.currentTimeMillis();

    public void updateMetrics() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage metaspaceUsage = memoryMXBean.getNonHeapMemoryUsage();

        metaspaceUsed.set(metaspaceUsage.getUsed());
        metaspaceMax.set(metaspaceUsage.getMax());

        // ClassLoader count via JMX
        try {
            ObjectName classLoadingName = new ObjectName("java.lang:type=ClassLoading");
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            long loadedClassCount = (Long) server.getAttribute(classLoadingName, "LoadedClassCount");
            classLoaderCount.set(loadedClassCount);

            long now = System.currentTimeMillis();
            if (previousClassCount > 0) {
                double rate = (double) (loadedClassCount - previousClassCount) /
                        ((now - previousTimestamp) / 1000.0);
                classLoadingRate.set(rate);
            }
            previousClassCount = loadedClassCount;
            previousTimestamp = now;

        } catch (Exception e) {
            System.err.println("Failed to get ClassLoading metrics: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        MetaspaceMetricsExporter exporter = new MetaspaceMetricsExporter();

        // Start Prometheus HTTP server
        HTTPServer server = new HTTPServer(8080);
        System.out.println("Prometheus metrics endpoint: http://localhost:8080/metrics");

        // Update metrics every 10 seconds
        while (true) {
            exporter.updateMetrics();
            Thread.sleep(10000);
        }
    }
}
```

## Compilation and Execution

```bash
# 1. Compile the leaking version
javac LeakingFilterRunner.java SecurityContext.java

# Run with limited Metaspace to demonstrate failure
java -XX:MaxMetaspaceSize=128m -XX:+PrintGCDetails LeakingFilterRunner

# 2. Compile and run the fixed version
javac FixedFilterRunner.java SecurityContext.java
java -XX:MaxMetaspaceSize=128m -XX:+PrintGCDetails FixedFilterRunner

# 3. Run tests
javac -cp .:junit-jupiter-api-5.9.0.jar LeakingFilterRunnerTest.java
java -cp .:junit-jupiter-api-5.9.0.jar:junit-jupiter-engine-5.9.0.jar \
     org.junit.platform.console.ConsoleLauncher --select-class LeakingFilterRunnerTest

# 4. Start Metaspace monitoring
javac MetaspaceMonitor.java MetaspaceMetricsExporter.java
java MetaspaceMonitor

# 5. Monitor with jcmd
jcmd <pid> VM.classloader_stats    # Check ClassLoader count
jcmd <pid> VM.metaspace             # Check Metaspace usage
jcmd <pid> GC.class_histogram       # Top classes by instance count
```

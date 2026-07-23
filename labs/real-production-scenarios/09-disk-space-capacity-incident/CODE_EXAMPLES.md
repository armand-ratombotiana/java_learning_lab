# Lab 09 — Disk Space / Capacity: Code Examples

## Disk Monitor

```java
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DiskMonitor {
    private final Path path;
    private final double warningThreshold;
    private final double criticalThreshold;

    public DiskMonitor(Path path, double warning, double critical) {
        this.path = path;
        this.warningThreshold = warning;
        this.criticalThreshold = critical;
    }

    public DiskStatus check() throws IOException {
        FileStore store = Files.getFileStore(path);
        long total = store.getTotalSpace();
        long used = total - store.getUnallocatedSpace();
        long usable = store.getUsableSpace();
        double utilization = (double) used / total * 100;
        long inodesTotal = store.getTotalInodes();
        long inodesUsed = store.getTotalInodes() - store.getUnallocatedInodes();
        double inodeUtilization = inodesTotal > 0 ? (double) inodesUsed / inodesTotal * 100 : 0;

        String status = "OK";
        if (utilization > criticalThreshold || inodeUtilization > criticalThreshold) {
            status = "CRITICAL";
        } else if (utilization > warningThreshold || inodeUtilization > warningThreshold) {
            status = "WARNING";
        }

        return new DiskStatus(path.toString(), total, used, usable, utilization,
                inodesTotal, inodesUsed, inodeUtilization, status);
    }

    public record DiskStatus(String mount, long total, long used, long usable,
                             double utilization, long inodesTotal, long inodesUsed,
                             double inodeUtilization, String status) {
        public String toAlertString() {
            return String.format("[%s] %s: %.1f%% used (%.1f GB / %.1f GB), " +
                            "inodes: %.1f%% (%d / %d)",
                    status, mount, utilization,
                    used / 1e9, total / 1e9,
                    inodeUtilization, inodesUsed, inodesTotal);
        }
    }

    public static void main(String[] args) throws Exception {
        DiskMonitor monitor = new DiskMonitor(Path.of("/"), 80, 90);
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    DiskStatus status = monitor.check();
                    System.out.println(status.toAlertString());
                    if ("CRITICAL".equals(status.status())) {
                        System.err.println("DISK CRITICAL — initiating emergency cleanup!");
                        emergencyCleanup();
                    }
                } catch (IOException e) {
                    System.err.println("Disk check failed: " + e.getMessage());
                }
            }
        }, 0, 60000); // Every 60 seconds
        Thread.sleep(300000); // Run for 5 min
        timer.cancel();
    }

    static void emergencyCleanup() {
        // Find top-10 largest files/directories
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "du", "-sh", "/var/log/*", "|", "sort", "-rh", "|", "head", "-10");
            pb.inheritIO();
            pb.start();
        } catch (Exception e) {
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }
}
```

## Log Rate Limiter

```java
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class LogRateLimiter {
    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();
    private final int maxLinesPerSecond;
    private volatile long lastReset = System.currentTimeMillis();

    public LogRateLimiter(int maxLinesPerSecond) {
        this.maxLinesPerSecond = maxLinesPerSecond;
    }

    public boolean allow(String source) {
        long now = System.currentTimeMillis();
        if (now - lastReset > 1000) {
            counters.clear();
            lastReset = now;
        }
        AtomicInteger counter = counters.computeIfAbsent(source, k -> new AtomicInteger(0));
        return counter.incrementAndGet() <= maxLinesPerSecond;
    }
}
```

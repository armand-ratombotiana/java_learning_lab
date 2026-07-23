package com.prod.solutions.diskspace;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simulates a disk space monitoring system that tracks usage over time
 * and alerts when growth exceeds thresholds.
 *
 * In production: Prometheus + node_exporter for disk metrics,
 * Alertmanager for alerting, Grafana for dashboards.
 */
public class DiskSpaceMonitor {

    static class DiskMetrics {
        final String mountPoint;
        final long totalBytes;
        final AtomicLong usedBytes = new AtomicLong();
        final AtomicLong growthRateBytesPerHour = new AtomicLong();
        private long lastCheckTime = System.currentTimeMillis();
        private long lastUsedBytes = 0;

        DiskMetrics(String mountPoint, long totalBytes) {
            this.mountPoint = mountPoint;
            this.totalBytes = totalBytes;
        }

        void updateUsage(long usedBytes) {
            long now = System.currentTimeMillis();
            long elapsedHours = (now - lastCheckTime) / 3600000;
            if (elapsedHours > 0) {
                long deltaBytes = usedBytes - lastUsedBytes;
                growthRateBytesPerHour.set(deltaBytes / Math.max(elapsedHours, 1));
            }
            this.usedBytes.set(usedBytes);
            this.lastUsedBytes = usedBytes;
            this.lastCheckTime = now;
        }

        double getUsedPercent() {
            return (double) usedBytes.get() / totalBytes * 100;
        }

        long getEstimatedTimeToFull() {
            long rate = growthRateBytesPerHour.get();
            if (rate <= 0) return Long.MAX_VALUE;
            long remainingBytes = totalBytes - usedBytes.get();
            return remainingBytes / rate; // hours
        }

        String getAlertLevel() {
            double pct = getUsedPercent();
            if (pct >= 95) return "CRITICAL";
            if (pct >= 85) return "WARNING";
            if (pct >= 75) return "INFO";
            if (growthRateBytesPerHour.get() > totalBytes / 720) return "GROWTH_ALERT"; // >10% per 30 days
            return "OK";
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Disk Space Monitoring System ===\n");

        DiskMetrics root = new DiskMetrics("/", 500L * 1024 * 1024 * 1024); // 500GB
        DiskMetrics varLog = new DiskMetrics("/var/log", 100L * 1024 * 1024 * 1024); // 100GB
        DiskMetrics data = new DiskMetrics("/data", 2L * 1024 * 1024 * 1024 * 1024); // 2TB

        // Simulate disk usage over time
        root.updateUsage(250L * 1024 * 1024 * 1024); // 50%
        varLog.updateUsage(60L * 1024 * 1024 * 1024); // 60%
        data.updateUsage(500L * 1024 * 1024 * 1024);  // 25%

        printMetrics(root);
        printMetrics(varLog);
        printMetrics(data);

        // Simulate log growth (100MB/hour)
        System.out.println("\n>>> Simulating log file growth (100MB/hour) <<<\n");

        for (int hour = 1; hour <= 6; hour++) {
            Thread.sleep(100);
            long newUsage = (60L + hour * 100L) * 1024 * 1024 * 1024 / 1024;
            varLog.updateUsage(newUsage * 1024);
            System.out.printf("Hour %d: /var/log at %.1f%% (rate: %d MB/h)%n",
                    hour, varLog.getUsedPercent(),
                    varLog.growthRateBytesPerHour.get() / (1024 * 1024));

            String alert = varLog.getAlertLevel();
            if (!alert.equals("OK")) {
                System.out.printf("  >>> ALERT: %s on /var/log <<<%n", alert);
                if (alert.equals("CRITICAL") || alert.equals("GROWTH_ALERT")) {
                    System.out.println("  >>> RECOMMENDED ACTION: Rotate logs or increase disk <<<");
                }
            }
        }
    }

    static void printMetrics(DiskMetrics m) {
        System.out.printf("""
                --- %s ---
                Total:     %d GB
                Used:      %d GB (%.1f%%)
                Growth:    %d MB/h
                Time to full: %s
                Alert:     %s
                %n""",
                m.mountPoint,
                m.totalBytes / (1024 * 1024 * 1024),
                m.usedBytes.get() / (1024 * 1024 * 1024),
                m.getUsedPercent(),
                m.growthRateBytesPerHour.get() / (1024 * 1024),
                m.getEstimatedTimeToFull() == Long.MAX_VALUE ? "N/A" : m.getEstimatedTimeToFull() + " hours",
                m.getAlertLevel());
    }
}

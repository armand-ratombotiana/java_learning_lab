package com.javaacademy.lab47.profiling;

import javax.management.*;
import java.lang.management.ManagementFactory;

/**
 * Exposes a custom MBean and registers it with the platform MBeanServer.
 * Demonstrates JMX monitoring — the bean can be inspected via JConsole,
 * VisualVM, or programmatic JMX clients.
 */
public class JmxMonitorExample {

    public interface LabMonitorMBean {
        int getActiveThreads();
        long getProcessedCount();
        void setProcessedCount(long count);
        String getStatus();
        void reset();
    }

    static class LabMonitor implements LabMonitorMBean {
        private volatile long processedCount = 0;
        private volatile boolean running = true;

        @Override
        public int getActiveThreads() {
            return Thread.activeCount();
        }

        @Override
        public long getProcessedCount() {
            return processedCount;
        }

        @Override
        public void setProcessedCount(long count) {
            this.processedCount = count;
        }

        @Override
        public String getStatus() {
            return running ? "RUNNING" : "STOPPED";
        }

        @Override
        public void reset() {
            processedCount = 0;
            running = true;
        }
    }

    public static void main(String[] args) throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("com.javaacademy:type=LabMonitor");
        LabMonitor monitor = new LabMonitor();
        mbs.registerMBean(monitor, name);

        System.out.println("MBean registered. Open JConsole to inspect.");
        for (int i = 0; i < 20; i++) {
            monitor.processedCount++;
            Thread.sleep(500);
            System.out.println("Processed: " + monitor.processedCount);
        }
    }
}
